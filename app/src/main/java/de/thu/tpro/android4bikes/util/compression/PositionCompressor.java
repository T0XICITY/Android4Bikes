package de.thu.tpro.android4bikes.util.compression;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.util.JSONHelper;

/**
 *
 */
public class PositionCompressor {
    private JSONHelper<Position> jsonHelper;

    public PositionCompressor() {
        jsonHelper = new JSONHelper<Position>(Position.class);
    }

    /**
     * decompresses a given array of bytes (byte[]' -> byte[])
     *
     * @param data data that should be decompressed (byte[]')
     * @return decompressed array of bytes (byte[])
     * @throws IOException
     */
    private static byte[] decompress(byte[] data) throws IOException, DataFormatException {
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

    /**
     * list_positions -> {@link JSONObject} -> JSON-{@link String} -> byte[] -> compression of byte[]
     * Format of the JSONObject:
     * {
     * positions: [
     * {"longitude":23.34,"latidtude":4.344},
     * {"longitude":23.01,"latidtude":4.345},
     * ...
     * ]
     * }
     *
     * @param list_positions positions to compress
     * @return compressed byte[] regarding all positions
     */
    public byte[] compressPositions(List<Position> list_positions) {
        byte[] byteArray_list_json_compressed = null;
        byte[] bytes_jsonObjectString_positions = null;
        JSONObject jsonObject_list_positions = null;
        JSONObject jsonObject_position = null;
        JSONArray jsonArray_positions = null;

        try {
            jsonObject_list_positions = new JSONObject();
            jsonObject_position = null;
            jsonArray_positions = new JSONArray();

            //put every position as a json object into a JSONArray
            for (Position pos : list_positions) {
                jsonObject_position = jsonHelper.convertObjectToJSONObject(pos);
                jsonArray_positions.put(jsonObject_position);
            }
            jsonObject_list_positions.put("positions", jsonArray_positions); //{positions: [{position1},{position2},...]}

            //String to serialize, compress and send:
            String jsonObjectString_positions = jsonObject_list_positions.toString(); // Print it with specified indentation

            //Serialization to byte array:
            bytes_jsonObjectString_positions = serialize(jsonObjectString_positions);

            byteArray_list_json_compressed = compress(bytes_jsonObjectString_positions);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return byteArray_list_json_compressed;
    }

    /**
     * compressed byte[] -> decompressed byte[] -> JSON-{@link String} -> List<{@link Position}>
     *
     * @param compressedPositions
     * @return
     */
    public List<Position> decompressPositions(byte[] compressedPositions) {
        List<Position> list_positions = null;
        String jsonObjectString_positions = null;
        JSONObject jsonObject_list_positions = null;
        JSONArray jsonArray_positions = null;
        JSONObject jsonObject_position = null;
        Position position = null;

        try {
            list_positions = new ArrayList<>();

            byte[] decompressed_positionlist = decompress(compressedPositions);
            Object object_list_position = deserialize(decompressed_positionlist);
            jsonObjectString_positions = String.valueOf(object_list_position);

            //Generate JSONObject out of jsonObjectString_positions
            jsonObject_list_positions = new JSONObject(jsonObjectString_positions);
            jsonArray_positions = jsonObject_list_positions.getJSONArray("positions");

            //convert each position to a position object and insert them into the list
            for (int i = 0; i < jsonArray_positions.length(); ++i) {
                jsonObject_position = jsonArray_positions.getJSONObject(i);
                position = jsonHelper.convertJSONObjectToObject(jsonObject_position);
                list_positions.add(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list_positions;
    }

    /**
     * calculate the allocated memory space regarding a certain object.
     *
     * @param object Object that must implement the interface {@link java.io.Serializable}
     * @return allocated memory space in KiB (1 KiB = 1024 Byte). Returns -1 if something went wrong.
     */
    public long getObjectSizeInKiB(Object object) {
        long objectSizeInKiB = -1;
        try {
            //get all the Bytes regarding a certain object
            byte[] object_bytes = serialize(object);

            //1 KiB = 1024 Bytes
            objectSizeInKiB = object_bytes.length / 1024;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objectSizeInKiB;
    }

    /**
     * Serializes an object (Object -> byte[])
     * @param obj Object that must implement the interface {@link java.io.Serializable}
     * @return bytes regarding the specified object
     * @throws IOException
     */
    private byte[] serialize(Object obj) throws IOException {
        //compare: https://stackoverflow.com/questions/3736058/java-object-to-byte-and-byte-to-object-converter-for-tokyo-cabinet/3736091
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    /**
     * Deserializes a specified array of bytes (byte[] -> Object)
     * @param data valid bytes regarding the object (as a result of the method 'serialize')
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        //compare: https://stackoverflow.com/questions/3736058/java-object-to-byte-and-byte-to-object-converter-for-tokyo-cabinet/3736091
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }

    /**
     * compresses a given array of bytes (byte[] -> byte[]')
     * @param data data that should be compressed (byte[])
     * @return compressed array of bytes (byte[])
     * @throws IOException
     */
    private byte[] compress(byte[] data) throws IOException { //(ZLIB-Library zur Kompression genutzt)
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
        return output;
    }

}



