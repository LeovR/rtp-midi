package io.github.leovr.rtipmidi;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

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

    public AppleMidiServer(final String name, final int port) {
        this.port = port;
        controlServer = new AppleMidiControlServer(name, port);
        sessionServer = new AppleMidiSessionServer(name, port + 1);
        sessionServer.addSessionChangeListener(this);
    }

    public void addAppleMidiSession(final AppleMidiSession session) {
        sessionServer.addAppleMidiSession(session);
    }

    public void removeAppleMidiSession(final AppleMidiSession session) {
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
