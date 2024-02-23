package de.morihofi.dooropener.libgnetplus;

public enum ControlLedBuzzerOption {
    TURN_LED_OFF(0),
    GREEN_ON(1),
    GREEN_OFF(2),
    RED_ON(3),
    RED_OFF(4),
    ONE_BEEP(5),
    THREE_BEEPS(6),
    GREEN_ON_WITH_BEEP(7),
    RED_ON_WITH_THREE_BEEPS(8),
    RED_GREEN_ON(9),
    BLUE_ON(10), //On ER750 not available
    BLUE_OFF(11), //On ER750 not available
    BUZZER_ALWAYS_BEEP(12),
    BUZZER_OFF(13);

    private final byte commandCode;

    ControlLedBuzzerOption(int commandCode) {
        this.commandCode = (byte) commandCode;
    }

    public byte getCommandCode() {
        return commandCode;
    }
}
