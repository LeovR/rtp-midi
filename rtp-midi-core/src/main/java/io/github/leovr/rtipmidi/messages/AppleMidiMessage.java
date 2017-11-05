package io.github.leovr.rtipmidi.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class AppleMidiMessage {

    private final MidiCommandHeader midiCommandHeader;
    private final List<MidiTimestampPair> messages;

    public byte[] toByteArray() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final DataOutputStream outputStream = new DataOutputStream(byteArrayOutputStream);

        outputStream.write(midiCommandHeader.toByteArray());

        boolean first = true;
        for (final MidiTimestampPair message : messages) {
            if (first && !midiCommandHeader.isZ()) {
                first = false;
            } else {
                final int timestamp = message.getTimestamp();
                if (timestamp > 0x0FFFFFFF) {
                    throw new IllegalArgumentException("Timestamp too big: " + timestamp);
                }
                if (timestamp > 0) {
                    int numberOfSeptets =
                            (int) Math.ceil(Integer.bitCount(Integer.highestOneBit(timestamp) * 2 - 1) / 7.0);
                    while (numberOfSeptets > 0) {
                        outputStream.writeByte(
                                (numberOfSeptets > 1 ? 0x80 : 0) | ((timestamp >> ((numberOfSeptets - 1) * 7)) & 0x7F));
                        numberOfSeptets--;
                    }
                } else {
                    outputStream.writeByte(0);
                }
            }
            outputStream.write(message.getMidiMessage().getData());
        }

        outputStream.flush();
        return byteArrayOutputStream.toByteArray();
    }

}
