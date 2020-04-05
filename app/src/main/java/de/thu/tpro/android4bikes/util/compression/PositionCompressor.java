package de.thu.tpro.android4bikes.util.compression;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import de.thu.tpro.android4bikes.data.model.Position;

public class PositionCompressor {
    Gson gson;

    public PositionCompressor() {
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }

    public void compressPositions(List<Position> list_positions) {
        try {
            JSONObject jsonObject_list = new JSONObject();
            JSONObject jsonObject_position = null;
            JSONArray jsonArray_positions = new JSONArray();
            for (Position pos : list_positions) {
                jsonObject_position = new JSONObject(gson.toJson(pos));
                jsonArray_positions.put(jsonObject_position);
            }
            jsonObject_list.put("positions", jsonArray_positions); //{positions: [{position1},{position2},...]}

            //String to serialize, compress and send:
            String jsonObjectString_positions = jsonObject_list.toString(); // Print it with specified indentation

            //Serialization to byte array:
            byte[] bytes_jsonObjectString_positions = serialize(jsonObjectString_positions);
            byte[] compressed_bytes_jsonObjectString_positions = compress(bytes_jsonObjectString_positions);


            byte[] decompressed_positionlist = decompress(compressed_bytes_jsonObjectString_positions);
            Object object_list_position = deserialize(decompressed_positionlist);

            jsonObjectString_positions = String.valueOf(object_list_position);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public byte[] serialize(Object obj) throws IOException {
        //compare: https://stackoverflow.com/questions/3736058/java-object-to-byte-and-byte-to-object-converter-for-tokyo-cabinet/3736091
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    public Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        //compare: https://stackoverflow.com/questions/3736058/java-object-to-byte-and-byte-to-object-converter-for-tokyo-cabinet/3736091
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }

    public byte[] compress(byte[] data) throws IOException { //(ZLIB-Library zur Kompression genutzt)
        //compare: https://dzone.com/articles/how-compress-and-uncompress
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        deflater.finish();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer); // returns the generated code... index
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();
        Log.d("Hallo Welt", "Original: " + data.length / 1024 + "KiB");
        Log.d("Hallo Welt", "Compressed: " + output.length / 1024 + "KiB");
        return output;
    }


    public static byte[] decompress(byte[] data) throws IOException, DataFormatException {
        //compare: https://dzone.com/articles/how-compress-and-uncompress
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();
        return output;
    }

}



