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
public abstract class AppleMidiInvitation extends AppleMidiCommand {

    private final int protocolVersion;
    private final int initiatorToken;
    private final String name;

    AppleMidiInvitation(final CommandWord commandWord, final int protocolVersion, final int initiatorToken,
                        final int ssrc, final String name) {
        super(commandWord, ssrc);
        this.protocolVersion = protocolVersion;
        this.initiatorToken = initiatorToken;
        this.name = name;
    }

    public byte[] toByteArray() throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.write(MIDI_COMMAND_HEADER1);
        dataOutputStream.write(MIDI_COMMAND_HEADER2);
        dataOutputStream.write(getCommandWord().name().getBytes(UTF_8));
        dataOutputStream.writeInt(protocolVersion);
        dataOutputStream.writeInt(initiatorToken);
        dataOutputStream.writeInt(getSsrc());
        dataOutputStream.write(name.getBytes(UTF_8));
        dataOutputStream.writeByte(0);
        dataOutputStream.flush();
        return outputStream.toByteArray();
    }
}
