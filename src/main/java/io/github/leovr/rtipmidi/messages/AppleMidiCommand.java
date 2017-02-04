package io.github.leovr.rtipmidi.messages;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;

@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public abstract class AppleMidiCommand {

    public static final byte MIDI_COMMAND_HEADER1 = (byte) 0xFF;
    public static final byte MIDI_COMMAND_HEADER2 = (byte) 0xFF;
    private final CommandWord commandWord;
    private final int ssrc;

    public abstract byte[] toByteArray() throws IOException;
}
