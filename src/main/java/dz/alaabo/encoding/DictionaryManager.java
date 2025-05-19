package dz.alaabo.encoding;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DictionaryManager implements Serializable{
    private final Map<Character , Byte> charToCode = new HashMap<>();
    private final Map<Byte, Character> codeToChar = new HashMap<>();
    private byte nextCode = 0;

    public synchronized byte encodeChar(char c) {
        return charToCode.computeIfAbsent(c, k -> {
            codeToChar.put(nextCode, k);
            return nextCode++;
        });
    }

    public synchronized byte[] encodeString(String input) {
        byte[] result = new byte[input.length()];
        for (int i = 0; i < input.length(); i++) {
            result[i] = encodeChar(input.charAt(i));
        }
        return result;
    }

    public synchronized String decodeString(byte[] encoded) {
        StringBuilder sb = new StringBuilder(encoded.length);
        for (byte b : encoded) {
            sb.append(codeToChar.get(b));
        }
        return sb.toString();
    }

    public void saveToFile(String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static DictionaryManager loadFromFile(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (DictionaryManager) ois.readObject();
        }
    }



}
