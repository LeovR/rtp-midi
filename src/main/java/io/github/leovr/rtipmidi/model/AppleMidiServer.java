package io.github.leovr.rtipmidi.model;

import lombok.Value;

import javax.annotation.Nonnull;
import java.net.InetAddress;

@Value
public class AppleMidiServer {

    @Nonnull
    private InetAddress inetAddress;
    private int port;

}
