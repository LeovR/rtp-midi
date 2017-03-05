package io.github.leovr.rtipmidi.model;

public class SysexMessage extends MidiMessage {

    public static final int SYSTEM_EXCLUSIVE = 0xF0; // 240
    public static final int SPECIAL_SYSTEM_EXCLUSIVE = 0xF7; // 247

    public SysexMessage(final byte[] data, final int length) {
        super(data, length);
    }
}
