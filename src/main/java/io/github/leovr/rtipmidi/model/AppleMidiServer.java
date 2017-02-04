package io.github.leovr.rtipmidi.model;

import lombok.Value;

import java.net.InetAddress;

@Value
public class AppleMidiServer {

    private InetAddress inetAddress;
    private int port;

}
