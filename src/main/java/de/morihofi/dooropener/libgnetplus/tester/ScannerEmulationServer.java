package de.morihofi.dooropener.libgnetplus.tester;

import de.morihofi.dooropener.libgnetplus.DoorOpener;
import de.morihofi.dooropener.libgnetplus.GnetCommandFormat;
import de.morihofi.dooropener.libgnetplus.ByteTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScannerEmulationServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int PORT = 2167;

    public static void main(String[] args) {
        new Thread(() -> {
            // Create a thread pool for the client connections
            ExecutorService executor = Executors.newCachedThreadPool();

            try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
                // Bind the ServerSocketChannel to an address and a port
                serverSocketChannel.bind(new InetSocketAddress("0.0.0.0", PORT));
                LOGGER.info("Server listening on port " + PORT);

                while (true) { // Infinite loop to wait for connections
                    SocketChannel socketChannel = serverSocketChannel.accept(); // Waiting for a connection
                    LOGGER.info("Connection accepted: " + socketChannel.getRemoteAddress());

                    // Handle the connection in a separate thread
                    executor.submit(() -> handleConnection(socketChannel));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            final int PORT = 23;
            byte[] buffer = new byte[1024]; // Adjust buffer size as needed

            try (DatagramSocket socket = new DatagramSocket(PORT)) {
                socket.setBroadcast(true);

                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet); // Blocked until a message is received

                    // Process the received message
                    byte[] data = packet.getData();
                    LOGGER.info("Broadcast-Message received {}", ByteTools.bytesToHexString(trimTrailingZeros(data)));

                    // Prepare answer
                    byte[] replyData = createReply();

                    // Send reply
                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();
                    DatagramPacket replyPacket = new DatagramPacket(replyData, replyData.length, address, port);
                    socket.send(replyPacket);
                }
            } catch (Exception e) {
                LOGGER.error("Error in UDP Broadcast part", e);
            }
        }).start();
    }

    public static byte[] createReply() throws SocketException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        // Reserved
        buffer.put(new byte[]{(byte) 0xFE, 0x5F}); //Second byte can also be 0x5F (not sure what is the difference) 0x54

        // Command Code
        buffer.put((byte) 0x01);

        // Board type
        buffer.put((byte) 0x04);

        // Board ID
        buffer.put((byte) 0x01);


        // IP Address and MAC Address
        byte[] ipAddress = null;
        byte[] macAddress = null;
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            // Filter loopback addresses and non-active interfaces
            if (!networkInterface.isLoopback() && networkInterface.isUp()) {
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    // Search for the first IPv4 address (which is not a loopback address)
                    if (addr.getAddress().length == 4) {
                        ipAddress = addr.getAddress();
                        macAddress = networkInterface.getHardwareAddress();
                        ByteTools.reverse(macAddress); // Reverse MAC Address, needed for the Manufacturer SDK
                        break;
                    }
                }
            }
            if (ipAddress != null && macAddress != null) {
                break;
            }
        }

        if (ipAddress != null) {
            buffer.put(ipAddress);
        } else {
            buffer.put(new byte[]{0, 0, 0, 0}); // Default if not IP address was found
        }
        if (macAddress == null) {
            macAddress = new byte[]{0, 0, 0, 0, 0, 0}; // Default value if no MAC is found
        }
        buffer.put(macAddress);

        // Firmware Version (here is 1.0.0.0, written backwards)
        buffer.put(new byte[]{0x00, 0x00, 0x00, 0x01});

        // Device Name
        byte[] deviceName = new byte[64];
        Arrays.fill(deviceName, (byte) 0); // Init with zeros
        byte[] nameBytes = "ExampleDevice".getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(nameBytes, 0, deviceName, 0, nameBytes.length);
        buffer.put(deviceName);

        // Reserved
        buffer.put((byte) 0x00);

        // Subnet Mask
        byte[] subnetMask = {(byte) 255, (byte) 255, (byte) 255, 0};
        buffer.put(subnetMask);

        // Gateway IP
        byte[] gatewayIp = {(byte) 192, (byte) 168, 1, 1};
        buffer.put(gatewayIp);

        // Reserved
        buffer.put(new byte[]{0x00, 0x00, 0x00});

        buffer.flip(); // Preparation for reading
        byte[] reply = new byte[buffer.limit()];
        buffer.get(reply);

        return reply;
    }

    private static void handleConnection(SocketChannel socketChannel) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(64); // Adjust the size of the buffer
            while (socketChannel.read(buffer) > 0) {
                buffer.flip(); // Preparing the buffer for reading


                // Trimming the array for the view
                byte[] trimmedArray = ByteTools.trimTrailingZeros(buffer.array());
                LOGGER.info("Received data: {}", ByteTools.bytesToHexString(trimmedArray));

                DoorOpener.Response response = null;

                LOGGER.info("Processing command");
                switch (ER750CommandInterpreter.interpretCommand(buffer.array())) {
                    case COMMAND_OPEN_DOOR -> {
                        LOGGER.info("Command is open door command with duration of {} seconds", ER750CommandInterpreter.extractDurationFromOpenDoorCommand(buffer.array()));
                        response = DoorOpener.Response.generateResponse(GnetCommandFormat.EGnetErrorCodes.SUCCESS, null);
                    }
                    case COMMAND_CONTROL_LED_OR_BUZZER -> {
                        LOGGER.info("Command is LED/Buzzer command: {}", ER750CommandInterpreter.extractControlLedBuzzerOption(buffer.array()));
                        response = DoorOpener.Response.generateResponse(GnetCommandFormat.EGnetErrorCodes.SUCCESS, null);
                    }
                    case COMMAND_GET_VERSION -> {
                        LOGGER.info("Command is Get Version, answering with a read-out version of a physical device");
                        response = DoorOpener.Response.generateResponse(GnetCommandFormat.EGnetErrorCodes.SUCCESS, "PGM-T1379 V1.1R1 (141125)".getBytes(StandardCharsets.US_ASCII));
                    }
                }

                // Create an error message if it was hot handled
                if(response == null){
                    LOGGER.error("Throwing error of unknown reason, cause command is not supported");
                    response = DoorOpener.Response.generateResponse(GnetCommandFormat.EGnetErrorCodes.FAILED_UNKNOWN_REASON, null);
                }

                ByteBuffer responseBuffer = ByteBuffer.wrap(response.getResponseBytes());
                LOGGER.info("Writing response ({} bytes)", responseBuffer.remaining());
                socketChannel.write(responseBuffer);


                buffer.clear(); // Clean up the buffer for the next read
            }
            LOGGER.info("Closing connection");
            socketChannel.close(); // Close client connection
        } catch (IOException e) {
           LOGGER.error("Error on client connection", e);
        }
    }




}