package de.morihofi.dooropener.libgnetplus;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ER750ScannerTest {

    @Test
    void getOpenDoorCommand() {
        Assertions.assertEquals("010011020000fc80", ByteTools.bytesToHexString(ER750Scanner.getOpenDoorCommand(0)));
        Assertions.assertEquals("0100110200013c41", ByteTools.bytesToHexString(ER750Scanner.getOpenDoorCommand(1)));
        Assertions.assertEquals("0100110200023d01", ByteTools.bytesToHexString(ER750Scanner.getOpenDoorCommand(2)));
        Assertions.assertEquals("010011020003fdc0", ByteTools.bytesToHexString(ER750Scanner.getOpenDoorCommand(3)));
        Assertions.assertEquals("0100110200043f81", ByteTools.bytesToHexString(ER750Scanner.getOpenDoorCommand(4)));
        Assertions.assertEquals("010011020005ff40", ByteTools.bytesToHexString(ER750Scanner.getOpenDoorCommand(5)));
        Assertions.assertEquals("010011020006fe00", ByteTools.bytesToHexString(ER750Scanner.getOpenDoorCommand(6)));
        Assertions.assertEquals("0100110200073ec1", ByteTools.bytesToHexString(ER750Scanner.getOpenDoorCommand(7)));
        Assertions.assertEquals("0100110200083a81", ByteTools.bytesToHexString(ER750Scanner.getOpenDoorCommand(8)));
        Assertions.assertEquals("010011020009fa40", ByteTools.bytesToHexString(ER750Scanner.getOpenDoorCommand(9)));
    }

    @Test
    void getControlLedBuzzerCommand() {
        Assertions.assertEquals("024a300d", ByteTools.bytesToHexString(ER750Scanner.getControlLedBuzzerCommand(ControlLedBuzzerOption.TURN_LED_OFF)));
        Assertions.assertEquals("024a310d", ByteTools.bytesToHexString(ER750Scanner.getControlLedBuzzerCommand(ControlLedBuzzerOption.GREEN_ON)));
        Assertions.assertEquals("024a320d", ByteTools.bytesToHexString(ER750Scanner.getControlLedBuzzerCommand(ControlLedBuzzerOption.GREEN_OFF)));
        Assertions.assertEquals("024a330d", ByteTools.bytesToHexString(ER750Scanner.getControlLedBuzzerCommand(ControlLedBuzzerOption.RED_ON)));
        Assertions.assertEquals("024a340d", ByteTools.bytesToHexString(ER750Scanner.getControlLedBuzzerCommand(ControlLedBuzzerOption.RED_OFF)));
        Assertions.assertEquals("024a350d", ByteTools.bytesToHexString(ER750Scanner.getControlLedBuzzerCommand(ControlLedBuzzerOption.ONE_BEEP)));
        Assertions.assertEquals("024a360d", ByteTools.bytesToHexString(ER750Scanner.getControlLedBuzzerCommand(ControlLedBuzzerOption.THREE_BEEPS)));
        Assertions.assertEquals("024a370d", ByteTools.bytesToHexString(ER750Scanner.getControlLedBuzzerCommand(ControlLedBuzzerOption.GREEN_ON_WITH_BEEP)));
        Assertions.assertEquals("024a380d", ByteTools.bytesToHexString(ER750Scanner.getControlLedBuzzerCommand(ControlLedBuzzerOption.RED_ON_WITH_THREE_BEEPS)));
        Assertions.assertEquals("024a390d", ByteTools.bytesToHexString(ER750Scanner.getControlLedBuzzerCommand(ControlLedBuzzerOption.RED_GREEN_ON)));
        Assertions.assertEquals("024a3c0d", ByteTools.bytesToHexString(ER750Scanner.getControlLedBuzzerCommand(ControlLedBuzzerOption.BUZZER_ALWAYS_BEEP)));
        Assertions.assertEquals("024a3d0d", ByteTools.bytesToHexString(ER750Scanner.getControlLedBuzzerCommand(ControlLedBuzzerOption.BUZZER_OFF)));
    }
}