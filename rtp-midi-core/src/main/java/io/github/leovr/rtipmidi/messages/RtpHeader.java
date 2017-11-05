package io.github.leovr.rtipmidi.messages;

import lombok.Value;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Value
public class RtpHeader {

    private byte version;
    private boolean paddingFlag;
    private boolean extensionFlag;
    private byte contributingSourceIdentifiersCount;
    private boolean markerFlag;
    private byte payloadType;
    private short sequenceNumber;
    private int timestamp;
    private int ssrc;

    public byte[] toByteArray() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(12);
        final DataOutputStream outputStream = new DataOutputStream(byteArrayOutputStream);

        byte header1 = 0;
        header1 |= version << 6;
        header1 |= (paddingFlag ? 1 : 0) << 5;
        header1 |= (extensionFlag ? 1 : 0) << 4;
        header1 |= contributingSourceIdentifiersCount;

        outputStream.writeByte(header1);

        byte header2 = 0;
        header2 |= (markerFlag ? 1 : 0) << 7;
        header2 |= payloadType;

        outputStream.writeByte(header2);

        outputStream.writeShort(sequenceNumber);
        outputStream.writeInt(timestamp);
        outputStream.writeInt(ssrc);
        outputStream.flush();
        return byteArrayOutputStream.toByteArray();
    }
}
