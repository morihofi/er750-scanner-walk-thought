package de.morihofi.dooropener.libgnetplus.tester;

import de.morihofi.dooropener.libgnetplus.ControlLedBuzzerOption;
import de.morihofi.dooropener.libgnetplus.GnetQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.invoke.MethodHandles;
import static de.morihofi.dooropener.libgnetplus.GnetCommandFormat.*;

public class ER750CommandInterpreter {
    private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public enum CMD_TYPE {
        COMMAND_OPEN_DOOR, COMMAND_CONTROL_LED_OR_BUZZER, COMMAND_GET_VERSION, UNKNOWN
    }

    /**
     * Extracts the duration in seconds from the command bytes for opening the door.
     * @param commandBytes the command bytes.
     * @return the duration in seconds.
     */
    public static int extractDurationFromOpenDoorCommand(byte[] commandBytes) {
        // Check whether the array has the minimum expected length
        if (commandBytes == null || commandBytes.length < 7) { // SOH + Adresse + Befehlscode + Längenangabe + Min. 2 Parameter + Min. 1 Byte CRC
            throw new IllegalArgumentException("Invalid command bytes array");
        }

        // Checking the SOH, the address and the command code
        if (commandBytes[0] != GNet_SOH || commandBytes[2] != 0x11) {
            throw new IllegalArgumentException("Invalid command format");
        }

        // Extract and check the length of the parameters
        byte parameterLength = commandBytes[3];
        if (parameterLength != 2 || commandBytes.length < 7) { // Expect exactly 2 parameters based on the given array
            throw new IllegalArgumentException("Unexpected parameters length or total command length");
        }

        // Extraction of the duration located in the fifth element of the array (index 4)
        int duration = commandBytes[5] & 0xFF; // Correct position based on the structure and conversion to a positive integer

        return duration;
    }

    /**
     * Extracts the ControlLedBuzzerOption from the command bytes for controlling the buzzer and LEDs.
     * @param commandByteArray the command bytes.
     * @return the ControlLedBuzzerOption enum value.
     */
    public static ControlLedBuzzerOption extractControlLedBuzzerOption(byte[] commandByteArray) {
        // Extract the relevant part of the command and convert it into the enum
        int commandCode = commandByteArray[2] - 0x30; // Rückumwandlung
        for (ControlLedBuzzerOption option : ControlLedBuzzerOption.values()) {
            if (option.getCommandCode() == commandCode) {
                return option;
            }
        }
        throw new IllegalArgumentException("Invalid command code in byte array");
    }


    /**
     * Interprets the command byte array and forwards it to the appropriate method based on its type.
     * @param commandByteArray the command byte array to be interpreted.
     */
    public static CMD_TYPE interpretCommand(byte[] commandByteArray) {
        if (commandByteArray == null || commandByteArray.length == 0) {
            throw new IllegalArgumentException("Empty or null command byte array");
        }

        if(commandByteArray[1] == 0x4A){
            // Identifier for buzzer/LED command
            return CMD_TYPE.COMMAND_CONTROL_LED_OR_BUZZER;
        }

        if(commandByteArray[1] == (byte) 0x00 && commandByteArray[2] == (byte) 0x11){
            // Identifier for door opening command
            return CMD_TYPE.COMMAND_OPEN_DOOR;
        }

        if(commandByteArray[1] == (byte) 0x00 && commandByteArray[2] == GnetQuery.GET_VERSION){
            return CMD_TYPE.COMMAND_GET_VERSION;
        }

        return CMD_TYPE.UNKNOWN;
    }

}
