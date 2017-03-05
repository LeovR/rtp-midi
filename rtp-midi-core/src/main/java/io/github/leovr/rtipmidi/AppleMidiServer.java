package io.github.leovr.rtipmidi;

import io.github.leovr.rtipmidi.session.AppleMidiSession;
import io.github.leovr.rtipmidi.session.SessionChangeListener;
import io.github.leovr.rtipmidi.control.AppleMidiControlServer;
import io.github.leovr.rtipmidi.session.AppleMidiSessionServer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;

/**
 * Main class for the RTP MIDI communication. This class instantiates the {@link AppleMidiControlServer} and the {@link
 * AppleMidiSessionServer}. In order to receive midi messages a {@link AppleMidiSession} should be registerd via {@link
 * #addAppleMidiSession(AppleMidiSession)}.
 */
@Slf4j
public class AppleMidiServer implements SessionChangeListener {

    private static final int DEFAULT_PORT = 50004;
    private static final String DEFAULT_NAME = "rtpMIDIJava";
    @Getter
    private final int port;
    private final AppleMidiControlServer controlServer;
    private final AppleMidiSessionServer sessionServer;

    /**
     * Creates a {@link AppleMidiServer} with {@link #DEFAULT_NAME} and {@link #DEFAULT_PORT}
     */
    public AppleMidiServer() {
        this(DEFAULT_NAME, DEFAULT_PORT);
    }

    /**
     * Creates a new {@link AppleMidiServer} with the given name and port
     *
     * @param name The name under which the other peers should see this server
     * @param port The control port. A session server will be created on the {@code port + 1}
     */
    public AppleMidiServer(@Nonnull final String name, final int port) {
        this.port = port;
        controlServer = new AppleMidiControlServer(name, port);
        sessionServer = new AppleMidiSessionServer(name, port + 1);
        sessionServer.registerSessionChangeListener(this);
        controlServer.registerEndSessionListener(sessionServer);
    }

    /**
     * Add a new {@link AppleMidiSession} to this server
     *
     * @param session The session to be added
     */
    public void addAppleMidiSession(@Nonnull final AppleMidiSession session) {
        sessionServer.addAppleMidiSession(session);
    }

    /**
     * Remove the {@link AppleMidiSession} from this server
     *
     * @param session The session to be removed
     */
    public void removeAppleMidiSession(@Nonnull final AppleMidiSession session) {
        sessionServer.removeAppleMidiSession(session);
    }

    @Override
    public void onMaxNumberOfSessionsChange(final int maxNumberOfSessions) {
        controlServer.setMaxNumberOfSessions(maxNumberOfSessions);
    }

    /**
     * Starts the control server and the session server
     */
    public void start() {
        sessionServer.start();
        controlServer.start();
        log.info("AppleMidiServer started");
    }

    /**
     * Stops the session server and the control server
     */
    public void stop() {
        sessionServer.stopServer();
        controlServer.stopServer();
        log.info("AppleMidiServer stopped");
    }

}
