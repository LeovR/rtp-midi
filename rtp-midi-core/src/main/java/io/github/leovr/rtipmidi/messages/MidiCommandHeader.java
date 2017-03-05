package io.github.leovr.rtipmidi.messages;

import lombok.Value;

@Value
public class MidiCommandHeader {
    private boolean b;
    private boolean j;
    private boolean z;
    private boolean p;
    private short length;
    private RtpHeader rtpHeader;
}
