package de.morihofi.dooropener.libgnetplus;

public class ER750Scanner {

    /**
     * Get the command bytes to open the door for a specific amount of time
     * @param durationInSecs Duration the door should be open in seconds
     * @return command in bytes
     */
    public static byte[] getOpenDoorCommand(int durationInSecs) {
        if(durationInSecs > 255 || durationInSecs < 0){
            throw new IllegalStateException("Duration too long to fit into a byte");
        }
        byte[] parameterByteArray = new byte[2];
        byte[] commandByteArray;

        parameterByteArray[0] = 0;
        parameterByteArray[1] = (byte) durationInSecs;
        commandByteArray = GnetCommandFormat.getPackageCommand((byte)0, (byte)0x11, parameterByteArray);

        return commandByteArray;
    }

    /**
     * Get the command to control the buzzer and the LEDs
     * @param option action you want to do
     * @return command in bytes
     */
    public static byte[] getControlLedBuzzerCommand(ControlLedBuzzerOption option) {
        byte[] commandByteArray = new byte[4];

        commandByteArray[0] = 2; // Start of Header
        commandByteArray[1] = 0x4A; // Command identifier
        commandByteArray[2] = (byte) (0x30 + option.getCommandCode()); // Adding the selection to the base value
        commandByteArray[3] = 0x0D; // End of Text/Command

        return commandByteArray;
    }

}
