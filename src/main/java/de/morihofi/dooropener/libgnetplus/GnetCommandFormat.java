package de.morihofi.dooropener.libgnetplus;

import java.util.ArrayList;
import java.util.List;

public class GnetCommandFormat {

    // Constants
    public static final byte GNet_SOH = 1;
    public static final byte GNet_STX = 2;
    public static final byte GNet_CR = 0x0D;
    public static final byte GNet_ACK = 6;
    public static final byte GNet_NAK = 0x15;
    public static final byte GNet_EVN = 0x12;
    public static final int GNETPACKAGE_ADDR = 0;
    public static final int GNETPACKAGE_FUNCTION = 1;
    public static final int GNETPACKAGE_LENGTH = 2;
    public static final int GNETPACKAGE_DATA = 3;
    public static final int GNETPACKAGE_HEADER_SIZE = 5; // Addr(1 Byte)+Function(1 Byte)+Length(1 Byte)+CRC(2 Bytes) = 5


    private byte replyCode;
    private byte replyDataSize;
    private byte[] replyDataByteArray;

    // CRC16 calculation
    public static int GNetCRC16(byte[] bBuffer, int iLength, int iStart) {
        final int CRC_PRESET = 0xFFFF;
        final int CRC_POLYNOM = 0xA001;
        int iCRC = CRC_PRESET;
        int iLen = bBuffer.length - iStart;
        if (iLength == -1 || iLength > iLen) {
            iLength = iLen;
        }
        for (int I = iStart; I < iStart + iLength; I++) {
            iCRC = iCRC ^ (bBuffer[I] & 0xFF); // Ensure bitwise operation applies correctly
            for (int J = 0; J < 8; J++) {
                if ((iCRC & 1) != 0) {
                    iCRC = (iCRC >>> 1) ^ CRC_POLYNOM;
                } else {
                    iCRC = iCRC >>> 1;
                }
            }
        }
        return iCRC;
    }

    // Overloaded method for convenience
    public static int GNetCRC16(byte[] bBuffer) {
        return GNetCRC16(bBuffer, bBuffer.length, 0);
    }

    // Getters
    public byte getReplyCode() {
        return replyCode;
    }

    public byte getReplyDataSize() {
        return replyDataSize;
    }

    public byte[] getReplyDataByteArray() {
        return replyDataByteArray;
    }

    public static EGnetErrorCodes getErrorCode(byte replyCode) {
        return getErrorCode(replyCode, null);
    }

    public static EGnetErrorCodes getErrorCode(byte replyCode, byte[] data) {
        EGnetErrorCodes errorCode;
        switch (replyCode) {
            case GNet_ACK:
                errorCode = EGnetErrorCodes.SUCCESS;
                break;
            case GNet_NAK:
                if (data == null || data.length == 0) {
                    errorCode = EGnetErrorCodes.FAILED_NO_REASON_SPECIFIED;
                } else {
                    errorCode = EGnetErrorCodes.getByByte(data[0]);
                }
                break;
            default:
                errorCode = EGnetErrorCodes.UNKNOWN_RESPONSE_CODE;
                break;
        }
        return errorCode;
    }


    public void parseGnetCommand(byte[] replyParameterByteArray) {
        try {
            replyCode = replyParameterByteArray[2];
            replyDataSize = replyParameterByteArray[3];
            if (replyDataSize > 0) {
                replyDataByteArray = new byte[replyDataSize];
                System.arraycopy(replyParameterByteArray, 4, replyDataByteArray, 0, replyDataSize);
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid parameter", ex);
        }
    }

    public static byte[] getPackageCommand(byte address, byte commandCode, byte[] parameterByteArray) {
        List<Byte> cmd = new ArrayList<>();
        int crc;
        byte[] crcByteArray = new byte[2];

        // SOH
        cmd.add(GNet_SOH);
        // Address
        cmd.add(address);
        // Command Code
        cmd.add(commandCode);
        // Parameter Length
        if (parameterByteArray == null) {
            cmd.add((byte) 0);
            // No need to add parameter field
        } else {
            cmd.add((byte) parameterByteArray.length);
            // Parameter
            for (byte parameter : parameterByteArray) {
                cmd.add(parameter);
            }
        }

        crc = GNetCRC16(ByteTools.toPrimitives(cmd.toArray(new Byte[0])), cmd.size() - 1, 1);
        crcByteArray[0] = (byte) (crc >> 8);
        crcByteArray[1] = (byte) (crc & 0xFF);

        for (byte crcByte : crcByteArray) {
            cmd.add(crcByte);
        }

        byte[] cmdArray = new byte[cmd.size()];
        for (int i = 0; i < cmd.size(); i++) {
            cmdArray[i] = cmd.get(i);
        }

        return cmdArray;
    }
        public enum EGnetErrorCodes {
        /**
         * Success, there is no error
         */
        SUCCESS(GNet_ACK),
        /**
         * Failed: Scanner sent unknown error
         */
        FAILED_UNKNOWN_REASON((byte) 0xEF),

        /**
         * Failed: Access Denied
         */
        FAILED_ACCESS_DENIED((byte) 0xE0),

        /**
         * Failed: Illegal Query Code
         */
        FAILED_ILLEGAL_QUERY_CODE((byte) 0xE4),

        /**
         * Failed: Overrun, Out of record count
         */
        FAILED_OVERRUN((byte) 0xE6),

        /**
         * Failed: CRC Error
         */
        FAILED_CRC_ERROR((byte) 0xE7),

        /**
         * Failed: Out of memory range
         */
        FAILED_OUT_OF_MEMORY_RANGE((byte) 0xED),

        /**
         * Failed: Address Number out of range
         */
        FAILED_ADDRESS_OUT_RANGE((byte) 0xEE),

        /**
         * Failed: Reason not specified by scanner
         */
        FAILED_NO_REASON_SPECIFIED(null),

        /**
         * Response code is unknown
         */
        UNKNOWN_RESPONSE_CODE(null);
        private Byte value;

        EGnetErrorCodes(Byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }

        public static EGnetErrorCodes getByByte(byte data) {
            for (EGnetErrorCodes code : EGnetErrorCodes.values()){
                if(code.value == null){
                    continue; //Continue to prevent null pointer
                }
                if(data == ByteTools.toPrimitive(code.value)){
                    return code;
                }
            }
            return UNKNOWN_RESPONSE_CODE;
        }
    }
}
