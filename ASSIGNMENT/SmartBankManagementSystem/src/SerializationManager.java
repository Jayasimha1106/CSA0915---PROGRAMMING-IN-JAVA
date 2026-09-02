import java.io.*;

/**
 * Handles Object Serialization and Deserialization.
 * Demonstrates:
 * 1. Java Object Serialization: ObjectOutputStream & ObjectInputStream
 * 2. Deep Object Graph Persistence
 * 3. File Restoration
 */
public class SerializationManager {

    /**
     * Serializes any Serializable object into a binary file.
     * @param object Object to serialize
     * @param filepath Destination file path (e.g., data/bank_data.ser)
     * @throws IOException on write errors
     */
    public void serializeObject(Object object, String filepath) throws IOException {
        File file = new File(filepath);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(object);
            oos.flush();
        }
    }

    /**
     * Deserializes an object from a binary file.
     * @param filepath Source file path
     * @return Deserialized object
     * @throws IOException on read errors
     * @throws ClassNotFoundException if class definition is missing
     */
    public Object deserializeObject(String filepath) throws IOException, ClassNotFoundException {
        File file = new File(filepath);
        if (!file.exists()) {
            throw new FileNotFoundException("Serialized bank file not found at: " + filepath);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return ois.readObject();
        }
    }

    /**
     * Saves entire Bank domain model into serialized file.
     */
    public void saveBankState(Bank bank, String filepath) throws IOException {
        serializeObject(bank, filepath);
    }

    /**
     * Restores entire Bank domain model from serialized file.
     */
    public Bank loadBankState(String filepath) throws IOException, ClassNotFoundException {
        Object obj = deserializeObject(filepath);
        if (obj instanceof Bank) {
            return (Bank) obj;
        }
        throw new IOException("Deserialized object is not a valid Bank instance.");
    }
}
