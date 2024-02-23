package de.morihofi.dooropener.libgnetplus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;

public class Main {

    private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) throws IOException {
        DoorOpener doorOpener = new DoorOpener("127.0.0.1", 2167); //Use the Emulation Server Class for Quick testing

        DoorOpener.Response response = doorOpener.sendCommandToScanner(ER750Scanner.getControlLedBuzzerCommand(ControlLedBuzzerOption.TURN_LED_OFF));

        //DoorOpener.Response response = doorOpener.sendCommandToScanner(GnetQuery.getVersionQuery());
        LOGGER.info("RESPONSE HEX : {}", ByteTools.bytesToHexString(response.getResponseBytes()));
        LOGGER.info("DATA STRING  : {}", new String(response.getDataBytes(), StandardCharsets.US_ASCII));
        LOGGER.info("DATA HEX     : {}", ByteTools.bytesToHexString(response.getDataBytes()));
        LOGGER.info("ERROR CODE   : {}", response.getErrorCode());

    }


}
