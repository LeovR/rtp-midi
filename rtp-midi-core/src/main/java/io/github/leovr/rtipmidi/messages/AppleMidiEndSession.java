package io.github.leovr.rtipmidi.messages;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AppleMidiEndSession extends AppleMidiCommand {
    private final int protocolVersion;
    private final int initiatorToken;

    public AppleMidiEndSession(final int protocolVersion, final int initiatorToken, final int ssrc) {
        super(CommandWord.BY, ssrc);
        this.protocolVersion = protocolVersion;
        this.initiatorToken = initiatorToken;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.write(MIDI_COMMAND_HEADER1);
        dataOutputStream.write(MIDI_COMMAND_HEADER2);
        dataOutputStream.write(getCommandWord().name().getBytes(UTF_8));
        dataOutputStream.writeInt(protocolVersion);
        dataOutputStream.writeInt(initiatorToken);
        dataOutputStream.writeInt(getSsrc());
        dataOutputStream.flush();
        return outputStream.toByteArray();
    }
}
