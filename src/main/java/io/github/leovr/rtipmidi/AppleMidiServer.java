package io.github.leovr.rtipmidi;

import io.github.leovr.rtipmidi.control.AppleMidiControlServer;
import io.github.leovr.rtipmidi.session.AppleMidiSession;
import io.github.leovr.rtipmidi.session.AppleMidiSessionServer;
import io.github.leovr.rtipmidi.session.SessionChangeListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;

@Slf4j
public class AppleMidiServer implements SessionChangeListener {

    private static final int DEFAULT_PORT = 50004;
    private static final String DEFAULT_NAME = "rtpMIDIJava";
    @Getter
    private final int port;
    private final AppleMidiControlServer controlServer;
    private final AppleMidiSessionServer sessionServer;

    public AppleMidiServer() {
        this(DEFAULT_NAME, DEFAULT_PORT);
    }

    public AppleMidiServer(@Nonnull final String name, final int port) {
        this.port = port;
        controlServer = new AppleMidiControlServer(name, port);
        sessionServer = new AppleMidiSessionServer(name, port + 1);
        sessionServer.registerSessionChangeListener(this);
    }

    public void addAppleMidiSession(@Nonnull final AppleMidiSession session) {
        sessionServer.addAppleMidiSession(session);
    }

    public void removeAppleMidiSession(@Nonnull final AppleMidiSession session) {
        sessionServer.removeAppleMidiSession(session);
    }

    @Override
    public void onMaxNumberOfSessionsChange(final int maxNumberOfSessions) {
        controlServer.setMaxNumberOfSessions(maxNumberOfSessions);
    }

    public void start() {
        sessionServer.start();
        controlServer.start();
        log.info("AppleMidiServer started");
    }

    public void stop() {
        sessionServer.stopServer();
        controlServer.stopServer();
        log.info("AppleMidiServer stopped");
    }

}
