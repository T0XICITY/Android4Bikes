package de.thu.tpro.android4bikes.util.compression;

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

    public static void compressPositions(List<Position> list_positions) {
        try {
            //Serialization to byte array:
            byte[] bytes_positionlist = serialize(list_positions);


            byte[] compressed_positionlist = compress(bytes_positionlist);


            byte[] decompressed_positionlist = decompress(compressed_positionlist);

            //Deserialization from byte array
            Object object_list_position = deserialize(decompressed_positionlist);
            list_positions = (List<Position>) object_list_position;
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(list_positions.get(0));
    }


    public static byte[] serialize(Object obj) throws IOException {
        //compare: https://stackoverflow.com/questions/3736058/java-object-to-byte-and-byte-to-object-converter-for-tokyo-cabinet/3736091
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        //compare: https://stackoverflow.com/questions/3736058/java-object-to-byte-and-byte-to-object-converter-for-tokyo-cabinet/3736091
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }

    public static byte[] compress(byte[] data) throws IOException { //(ZLIB-Library zur Kompression genutzt)
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
        System.out.println("Original: " + data.length / 1024 + "KiB");
        System.out.println("Compressed: " + output.length / 1024 + "KiB");

        System.out.println("Elemente Original: " + data.length);
        System.out.println("Elemente Compressed: " + output.length);
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



