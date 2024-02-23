package de.morihofi.dooropener.libgnetplus;

/**
 * This class contains query message to be sent from host machine to card reader device. Magical
 * constants taken from protocol documentation.
 */
public class GnetQuery {
    public static final byte POLLING = 0x00;
    public static final byte GET_VERSION = 0x01;
    public static final byte SET_SLAVE_ADDR = 0x02;
    public static final byte LOGON = 0x03;
    public static final byte LOGOFF = 0x04;
    public static final byte SET_PASSWORD = 0x05;
    public static final byte CLASSNAME = 0x06;
    public static final byte SET_DATETIME = 0x07;
    public static final byte GET_DATETIME = 0x08;
    public static final byte GET_REGISTER = 0x09;
    public static final byte SET_REGISTER = 0x0A;
    public static final byte RECORD_COUNT = 0x0B;
    public static final byte GET_FIRST_RECORD = 0x0C;
    public static final byte GET_NEXT_RECORD = 0x0D;
    public static final byte ERASE_ALL_RECORDS = 0x0E;
    public static final byte ADD_RECORD = 0x0F;
    public static final byte RECOVER_ALL_RECORDS = 0x10;
    public static final byte DO = 0x11;
    public static final byte DI = 0x12;
    public static final byte ANALOG_INPUT = 0x13;
    public static final byte THERMOMETER = 0x14;
    public static final byte GET_NODE = 0x15;
    public static final byte GET_SN = 0x16;
    public static final byte SILENT_MODE = 0x17;
    public static final byte RESERVE = 0x18;
    public static final byte ENABLE_AUTO_MODE = 0x19;
    public static final byte GET_TIME_ADJUST = 0x1A;
    public static final byte ECHO = 0x18;
    public static final byte SET_TIME_ADJUST = 0x1C;
    public static final byte DEBUG = 0x1D;
    public static final byte RESET = 0x1E;
    public static final byte GO_TO_ISP = 0x1F;
    public static final byte REQUEST = 0x20;
    public static final byte ANTI_COLLISION = 0x21;
    public static final byte SELECT_CARD = 0x22;
    public static final byte AUTHENTICATE = 0x23;
    public static final byte READ_BLOCK = 0x24;
    public static final byte WRITE_BLOCk = 0x25;
    public static final byte SET_VALUE = 0x26;
    public static final byte READ_VALUE = 0x27;
    public static final byte CREATE_VALUE_BLOCK = 0x28;
    public static final byte ACCESS_CONDITION = 0x29;
    public static final byte HALT = 0x2A;
    public static final byte SAVE_KEY = 0x2B;
    public static final byte GET_SECOND_SN = 0x2C;
    public static final byte GET_ACCESS_CONDITION = 0x2D;
    public static final byte AUTHENTICATE_KEY = 0x2E;
    public static final byte REQUEST_ALL = 0x2F;
    public static final byte SET_VALUEEX = 0x32;
    public static final byte TRANSFER = 0x33;
    public static final byte RESTORE = 0x34;
    public static final byte GET_SECTOR = 0x3D;
    public static final byte RF_POWER_ONOFF = 0x3E;
    public static final byte AUTO_MODE = 0x3F;

    /**
     * Gets the Command to querying the Firmware version
     *
     * @return actual command to be sent
     */
    public static byte[] getVersionQuery() {
        byte address = 0x00; // Device address
        byte[] parameterByteArray = {}; // Keine Parameter, leeres Array

        return GnetCommandFormat.getPackageCommand(address, GET_VERSION, parameterByteArray);
    }

    public static byte[] getRegisterQuery(byte address, int regAddress, byte regLen) {
        // Prepare parameters for "Get Register"
        byte[] parameterByteArray = new byte[4];

        // Split register address into 2 bytes (high byte first)
        parameterByteArray[0] = (byte) (regAddress >> 8); // High byte of the register address
        parameterByteArray[1] = (byte) (regAddress & 0xFF); // Low byte of the register address
        // Register length
        parameterByteArray[2] = regLen; // Number of registers to be queried

        // Create the command package with the device address, function code for "Get Register" and the parameters
        return GnetCommandFormat.getPackageCommand(address, GET_REGISTER, parameterByteArray);
    }


}
