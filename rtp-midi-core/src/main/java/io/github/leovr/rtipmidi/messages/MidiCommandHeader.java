package io.github.leovr.rtipmidi.messages;

import lombok.Value;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Value
public class MidiCommandHeader {

    private boolean b;
    private boolean j;
    private boolean z;
    private boolean p;
    private short length;
    private RtpHeader rtpHeader;

    public byte[] toByteArray() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final DataOutputStream outputStream = new DataOutputStream(byteArrayOutputStream);
        outputStream.write(rtpHeader.toByteArray());

        byte midiCommandHeader1 = 0;

        midiCommandHeader1 |= (b ? 1 : 0) << 7;
        midiCommandHeader1 |= (j ? 1 : 0) << 6;
        midiCommandHeader1 |= (z ? 1 : 0) << 5;
        midiCommandHeader1 |= (p ? 1 : 0) << 4;

        if (b) {
            midiCommandHeader1 |= (length & 0x0F00) >> 8;
            outputStream.writeByte(midiCommandHeader1);
            outputStream.writeByte(length & 0x00FF);
        } else {
            midiCommandHeader1 |= length;
            outputStream.writeByte(midiCommandHeader1);
        }

        outputStream.flush();
        return byteArrayOutputStream.toByteArray();
    }
}
