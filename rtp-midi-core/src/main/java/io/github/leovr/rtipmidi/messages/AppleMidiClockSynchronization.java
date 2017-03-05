package io.github.leovr.rtipmidi.messages;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AppleMidiClockSynchronization extends AppleMidiCommand {

    private final byte count;
    private final long timestamp1;
    private final long timestamp2;
    private final long timestamp3;

    public AppleMidiClockSynchronization(final int ssrc, final byte count, final long timestamp1, final long timestamp2, final long timestamp3) {
        super(CommandWord.CK, ssrc);
        this.count = count;
        this.timestamp1 = timestamp1;
        this.timestamp2 = timestamp2;
        this.timestamp3 = timestamp3;
    }

    public byte[] toByteArray() throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.write(MIDI_COMMAND_HEADER1);
        dataOutputStream.write(MIDI_COMMAND_HEADER2);
        dataOutputStream.write(getCommandWord().name().getBytes(StandardCharsets.UTF_8));
        dataOutputStream.writeInt(getSsrc());
        dataOutputStream.writeByte(count);
        dataOutputStream.write(new byte[3]);
        dataOutputStream.writeLong(timestamp1);
        dataOutputStream.writeLong(timestamp2);
        dataOutputStream.writeLong(timestamp3);
        dataOutputStream.flush();
        return outputStream.toByteArray();
    }
}
