package de.morihofi.dooropener.libgnetplus;

import java.util.Arrays;

public class ByteTools {

    public static byte[] hexStringToBytes(String hexString) {
        // Remove any leading "0x" prefix from the hex string
        hexString = hexString.replaceAll("^0x", "");

        // Check if the length of the hex string is odd
        if (hexString.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex string length must be even");
        }

        // Convert each pair of hexadecimal characters to a byte
        byte[] byteArray = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            // Parse each pair of characters as a hexadecimal number and store it in the byte array
            byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) +
                    Character.digit(hexString.charAt(i + 1), 16));
        }
        return byteArray;
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            result.append(String.format("%02x", aByte));
            // upper case
            // result.append(String.format("%02X", aByte));
        }
        return result.toString();
    }

    public static byte[] toPrimitives(Byte[] oBytes) {

        byte[] bytes = new byte[oBytes.length];
        for (int i = 0; i < oBytes.length; i++) {
            bytes[i] = oBytes[i];
        }
        return bytes;

    }

    public static Byte[] toObjects(byte[] bytesPrim) {
        Byte[] bytes = new Byte[bytesPrim.length];
        int i = 0;
        for (byte b : bytesPrim) bytes[i++] = b; //Autoboxing
        return bytes;
    }

    public static byte toPrimitive(Byte oByte) {
        return oByte;
    }

    public static Byte toObject(byte b) {
        return b;
    }

    public static byte[] trimTrailingZeros(byte[] bytes) {
        int lastIndex = bytes.length - 1;
        while (lastIndex >= 0 && bytes[lastIndex] == 0) {
            lastIndex--;
        }
        return Arrays.copyOf(bytes, lastIndex + 1);
    }

    public static void reverse(byte[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            byte temp = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = temp;
        }
    }

}
