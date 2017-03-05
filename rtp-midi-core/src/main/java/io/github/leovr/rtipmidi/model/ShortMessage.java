package io.github.leovr.rtipmidi.model;

public class ShortMessage extends MidiMessage {

    public static final int MIDI_TIME_CODE = 0xF1; // 241
    public static final int SONG_POSITION_POINTER = 0xF2; // 242
    public static final int SONG_SELECT = 0xF3; // 243
    public static final int TUNE_REQUEST = 0xF6; // 246
    public static final int TIMING_CLOCK = 0xF8; // 248
    public static final int START = 0xFA; // 250
    public static final int STOP = 0xFC; //252
    public static final int ACTIVE_SENSING = 0xFE; // 254
    public static final int SYSTEM_RESET = 0xFF; // 255
    public static final int NOTE_OFF = 0x80;  // 128
    public static final int NOTE_ON = 0x90;  // 144
    public static final int POLY_PRESSURE = 0xA0;  // 160
    public static final int CONTROL_CHANGE = 0xB0;  // 176
    public static final int PROGRAM_CHANGE = 0xC0;  // 192
    public static final int CHANNEL_PRESSURE = 0xD0;  // 208
    public static final int PITCH_BEND = 0xE0;  // 224

    public ShortMessage(final byte command) {
        super(new byte[]{command}, 1);
    }

    public ShortMessage(final byte command, final byte data1) {
        super(new byte[]{command, data1}, 2);
    }

    public ShortMessage(final byte command, final byte data1, final byte data2) {
        super(new byte[]{command, data1, data2}, 3);
    }


}
