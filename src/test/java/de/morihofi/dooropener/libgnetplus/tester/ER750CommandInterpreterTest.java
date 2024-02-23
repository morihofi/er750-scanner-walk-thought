package de.morihofi.dooropener.libgnetplus.tester;

import de.morihofi.dooropener.libgnetplus.ControlLedBuzzerOption;
import de.morihofi.dooropener.libgnetplus.ER750Scanner;
import org.junit.jupiter.api.Assertions;

class ER750CommandInterpreterTest {

    @org.junit.jupiter.api.Test
    void extractDurationFromOpenDoorCommand() {
        for (int i = 0; i < 256; i++) {
            Assertions.assertEquals(i, ER750CommandInterpreter.extractDurationFromOpenDoorCommand(ER750Scanner.getOpenDoorCommand(i)));
        }
    }

    @org.junit.jupiter.api.Test
    void extractControlLedBuzzerOption() {
        for (ControlLedBuzzerOption option : ControlLedBuzzerOption.values()){
            Assertions.assertEquals(option, ER750CommandInterpreter.extractControlLedBuzzerOption(ER750Scanner.getControlLedBuzzerCommand(option)));
        }
    }

    @org.junit.jupiter.api.Test
    void interpretCommand() {
        Assertions.assertEquals(ER750CommandInterpreter.CMD_TYPE.COMMAND_OPEN_DOOR, ER750CommandInterpreter.interpretCommand(ER750Scanner.getOpenDoorCommand(7)));
        Assertions.assertEquals(ER750CommandInterpreter.CMD_TYPE.COMMAND_CONTROL_LED_OR_BUZZER, ER750CommandInterpreter.interpretCommand(ER750Scanner.getControlLedBuzzerCommand(ControlLedBuzzerOption.TURN_LED_OFF)));
    }
}