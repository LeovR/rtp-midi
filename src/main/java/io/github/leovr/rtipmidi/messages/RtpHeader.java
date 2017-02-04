package io.github.leovr.rtipmidi.messages;

import lombok.Value;

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
}
