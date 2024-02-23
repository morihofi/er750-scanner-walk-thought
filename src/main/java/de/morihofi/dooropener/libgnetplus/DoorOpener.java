package de.morihofi.dooropener.libgnetplus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static de.morihofi.dooropener.libgnetplus.GnetCommandFormat.GNetCRC16;

public class DoorOpener {
    private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final String ipAddress;
    private final int portNumber;

    public DoorOpener(String ipAddress, int portNumber) {
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
    }

    public Response sendCommandToScanner(byte[] commandBytes) throws IOException {
        return sendCommandToScanner(ByteBuffer.wrap(commandBytes));
    }

    public Response sendCommandToScanner(ByteBuffer commandBuffer) throws IOException {
        // Open a SocketChannel
        SocketChannel socketChannel = SocketChannel.open();
        LOGGER.info("Connecting to {}:{} ...", ipAddress, portNumber);
        socketChannel.connect(new InetSocketAddress(ipAddress, portNumber));
        LOGGER.info("Connected");
        // Write data to the server
        LOGGER.info("Attempting to write {}₁₆ as bytes", ByteTools.bytesToHexString(commandBuffer.array()));

        // Write the command to the SocketChannel
        while (commandBuffer.hasRemaining()) {
            int write = socketChannel.write(commandBuffer);
            // Check if there are any bytes remaining after the write operation
            if (commandBuffer.hasRemaining()) {
                LOGGER.info("Wrote {} bytes, Remaining: {}, Still has remaining bytes to write", write, commandBuffer.remaining());
            } else {
                // When no bytes remain to be written
                LOGGER.info("Wrote {} bytes, Remaining: {}, No remaining bytes to write", write, commandBuffer.remaining());
            }
        }


        // Prepare to read the response
        ByteBuffer responseBuffer = ByteBuffer.allocate(1024); // Adjust size as needed
        int bytesRead = socketChannel.read(responseBuffer);
        LOGGER.info("Bytes read: {}", bytesRead);

        // Close the SocketChannel
        socketChannel.close();

        // Prepare the buffer to be read
        responseBuffer.flip();
        byte[] responseBytes = new byte[responseBuffer.limit()];
        responseBuffer.get(responseBytes);

        return new Response(responseBytes);
    }

    public int getPortNumber() {
        return portNumber;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public static class Response {
        /**
         * Response bytes
         * byte 0 - Error Code
         */
        private final byte[] responseBytes;
        private final GnetCommandFormat.EGnetErrorCodes errorCode;
        private byte[] dataBytes = new byte[0];

        public static Response generateResponse(GnetCommandFormat.EGnetErrorCodes errorCode, byte[] data) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024); // should be enough

            if (errorCode == GnetCommandFormat.EGnetErrorCodes.SUCCESS && data == null) {
                // Success without data, send only GNet_ACK
                byteBuffer.put(GnetCommandFormat.GNet_ACK);
            } else {
                // Otherwise, start composing a complete message
                byteBuffer.put(GnetCommandFormat.GNet_SOH); // SOH must be first

                if (errorCode == GnetCommandFormat.EGnetErrorCodes.SUCCESS) {
                    // Successful response with data
                    byteBuffer.put((byte) 0x00); // Assumption: 0x00 as successful response address
                    byteBuffer.put(GnetCommandFormat.GNet_ACK); // ACK for success
                    byteBuffer.put((byte) data.length); // Length byte of the data
                    if (data.length > 0) {
                        byteBuffer.put(data); // The actual data
                    }
                } else {
                    // Response with error code
                    byteBuffer.put((byte) 0x00); // Assumption: 0x00 as response address for error cases
                    byteBuffer.put(GnetCommandFormat.GNet_NAK); // NAK for errors
                    byteBuffer.put((byte) 1); // Length is 1, as only the error code is sent
                    byteBuffer.put(errorCode.getValue()); // Length is 1, as only the error code is sent
                }

                // Determine the length of the data
                byte dataLength = (data != null) ? (byte) data.length : 0;
                byteBuffer.put(dataLength);

                if (data != null && data.length > 0) {
                    byteBuffer.put(data); // Actual data
                }

                // Calculation of the CRC before "flipping"
                byte[] dataForCrcCalculation = new byte[byteBuffer.position()];
                System.arraycopy(byteBuffer.array(), 0, dataForCrcCalculation, 0, byteBuffer.position());
                // Calculation of the CRC checksum for the verification
                int calculatedCrc = GNetCRC16(dataForCrcCalculation, dataForCrcCalculation.length - 1, 1); // Start at 1 to skip SOH

                // Add the CRC to the ByteBuffer
                byteBuffer.put((byte) (calculatedCrc >> 8));
                byteBuffer.put((byte) (calculatedCrc & 0xFF));


            }

            byteBuffer.flip(); // Reset the marker for the readout
            byte[] responsePacket = new byte[byteBuffer.limit()];
            byteBuffer.get(responsePacket);
            return new Response(responsePacket);
        }

        /**
         * Constructor to parse a GNetPlus Scanner Response
         *
         * @param responseBytes response to parse
         */
        private Response(byte[] responseBytes) {
            this.responseBytes = responseBytes;

            if (responseBytes.length == 0) {
                throw new IllegalArgumentException("Response Byte lenght cannot be 0");
            }

            if (responseBytes.length == 1) {
                errorCode = GnetCommandFormat.getErrorCode(responseBytes[0]);
                //No data extracting -> Doesn't contain data. Just a simple response
            } else {
                // Check SOH
                if (responseBytes[0] != GnetCommandFormat.GNet_SOH) {
                    throw new IllegalArgumentException("Invalid SOH");
                }

                // Extract data first, cause if we have encountered an error we already know the reason before we check it
                int dataLength = responseBytes[3];
                dataBytes = new byte[dataLength];
                System.arraycopy(responseBytes, 4, dataBytes, 0, dataLength);

                // Now extract the error code status
                errorCode = GnetCommandFormat.getErrorCode(responseBytes[2], dataBytes);

                // Remove the CRC bytes from the calculation
                byte[] dataForCrcCalculation = new byte[responseBytes.length - 2];
                System.arraycopy(responseBytes, 0, dataForCrcCalculation, 0, responseBytes.length - 2);

                // Extraction of the CRC checksum from the response
                int receivedCrc = ((responseBytes[responseBytes.length - 2] & 0xFF) << 8) | (responseBytes[responseBytes.length - 1] & 0xFF);

                // Calculation of the CRC checksum for the verification
                int calculatedCrc = GNetCRC16(dataForCrcCalculation, dataForCrcCalculation.length - 1, 1); // Start at 1, to skip SOH


                // Verification of the CRC checksum
                if (calculatedCrc == receivedCrc) {
                    LOGGER.info("CRC check passed.");
                } else {
                    LOGGER.warn("CRC check failed. Calculated CRC: {}, Received CRC: {}", Integer.toHexString(calculatedCrc), Integer.toHexString(receivedCrc));
                }
            }


        }

        public GnetCommandFormat.EGnetErrorCodes getErrorCode() {
            return errorCode;
        }

        public byte[] getDataBytes() {
            return dataBytes;
        }

        public byte[] getResponseBytes() {
            return responseBytes;
        }
    }


}
