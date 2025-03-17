package nl.inholland.endassignment.endproject.utils;

import java.io.*;

public class Serializer {

    private static final String FILE_PATH = "database.bin";  // File to save serialized data

    // Serialize an object to a file
    public static void serialize(Object object) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(object);
            System.out.println("Data serialized successfully to " + FILE_PATH);
        } catch (IOException e) {
            System.err.println("Failed to serialize data: " + e.getMessage());
        }
    }

    // Deserialize an object from a file
    public static <T> T deserialize(Class<T> clazz) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            Object object = ois.readObject();
            System.out.println("Data deserialized successfully from " + FILE_PATH);
            return clazz.cast(object);
        } catch (FileNotFoundException e) {
            System.out.println("No existing file found at " + FILE_PATH + ". Starting with a new database.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to deserialize data: " + e.getMessage());
        }
        return null;
    }
}
