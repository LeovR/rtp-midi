package io.github.leovr.rtipmidi.session;

import io.github.leovr.rtipmidi.AppleMidiCommandListener;
import io.github.leovr.rtipmidi.AppleMidiMessageListener;
import io.github.leovr.rtipmidi.error.AppleMidiSessionServerRuntimeException;
import io.github.leovr.rtipmidi.handler.AppleMidiCommandHandler;
import io.github.leovr.rtipmidi.handler.AppleMidiMessageHandler;
import io.github.leovr.rtipmidi.messages.AppleMidiClockSynchronization;
import io.github.leovr.rtipmidi.messages.AppleMidiCommand;
import io.github.leovr.rtipmidi.messages.AppleMidiEndSession;
import io.github.leovr.rtipmidi.messages.AppleMidiInvitation;
import io.github.leovr.rtipmidi.messages.AppleMidiInvitationAccepted;
import io.github.leovr.rtipmidi.messages.AppleMidiInvitationDeclined;
import io.github.leovr.rtipmidi.messages.AppleMidiInvitationRequest;
import io.github.leovr.rtipmidi.messages.MidiCommandHeader;
import io.github.leovr.rtipmidi.model.AppleMidiServer;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import javax.sound.midi.MidiMessage;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.github.leovr.rtipmidi.messages.AppleMidiCommand.MIDI_COMMAND_HEADER1;

@Slf4j
public class AppleMidiSessionServer extends Thread implements AppleMidiCommandListener, AppleMidiMessageListener {

    private static final int SOCKET_TIMEOUT = 1000;

    private enum State {
        ACCEPT_INVITATIONS, FULL
    }

    @Value
    private static class AppleMidiSessionAppleMidiServer {

        @Nonnull
        private AppleMidiSession appleMidiSession;
        @Nonnull
        private AppleMidiServer appleMidiServer;
    }

    private static final int RECEIVE_BUFFER_LENGTH = 1024;

    private ExecutorService threadPool;
    private final int ssrc;
    private final String name;
    private final AppleMidiCommandHandler midiCommandHandler = new AppleMidiCommandHandler();
    private final AppleMidiMessageHandler midiMessageHandler = new AppleMidiMessageHandler();
    private final int port;
    private boolean running = true;
    private DatagramSocket socket;
    private final Deque<AppleMidiSession> sessions = new ArrayDeque<>();
    private final Map<Integer, AppleMidiSessionAppleMidiServer> currentSessions = new HashMap<>();
    private final List<SessionChangeListener> sessionChangeListeners = new ArrayList<>();

    public AppleMidiSessionServer(@Nonnull final String name, final int port) {
        super(name + "SessionThread");
        this.port = port;
        this.ssrc = new Random().nextInt();
        this.name = name;

        midiCommandHandler.registerListener(this);
        midiMessageHandler.registerListener(this);
    }

    @Override
    public synchronized void start() {
        threadPool = Executors.newCachedThreadPool();
        try {
            socket = new DatagramSocket(port);
            socket.setSoTimeout(SOCKET_TIMEOUT);
        } catch (final SocketException e) {
            throw new AppleMidiSessionServerRuntimeException("DatagramSocket cannot be opened", e);
        }
        super.start();
        log.debug("MIDI session server started");
    }

    @Override
    public void run() {
        while (running) {

            try {
                final byte[] receiveData = new byte[RECEIVE_BUFFER_LENGTH];
                final DatagramPacket incomingPacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(incomingPacket);
                threadPool.execute(() -> {
                    if (receiveData[0] == MIDI_COMMAND_HEADER1) {
                        midiCommandHandler.handle(receiveData,
                                new AppleMidiServer(incomingPacket.getAddress(), incomingPacket.getPort()));
                    } else {
                        midiMessageHandler.handle(receiveData,
                                new AppleMidiServer(incomingPacket.getAddress(), incomingPacket.getPort()));
                    }
                });
            } catch (final SocketTimeoutException ignored) {
            } catch (final IOException e) {
                log.error("IOException while receiving", e);
            }
        }
        socket.close();
    }

    public void stopServer() {
        running = false;
        currentSessions.clear();
        sessions.clear();
        threadPool.shutdown();
        log.debug("MIDI session server stopped");
    }

    private void send(final AppleMidiCommand midiCommand, final AppleMidiServer appleMidiServer) throws IOException {
        final byte[] invitationAcceptedBytes = midiCommand.toByteArray();

        socket.send(new DatagramPacket(invitationAcceptedBytes, invitationAcceptedBytes.length,
                appleMidiServer.getInetAddress(), appleMidiServer.getPort()));
    }

    @Override
    public void onMidiInvitation(@Nonnull final AppleMidiInvitationRequest invitation, @Nonnull final AppleMidiServer appleMidiServer) {
        log.info("MIDI invitation from: {}", appleMidiServer);
        if (getSessionServerState() == State.ACCEPT_INVITATIONS) {
            sendMidiInvitationAnswer(appleMidiServer, "accept",
                    new AppleMidiInvitationAccepted(invitation.getProtocolVersion(), invitation.getInitiatorToken(),
                            ssrc, name));
            currentSessions
                    .put(invitation.getSsrc(), new AppleMidiSessionAppleMidiServer(sessions.pop(), appleMidiServer));
            notifyMaxNumberOfSessions();
        } else {
            sendMidiInvitationAnswer(appleMidiServer, "decline",
                    new AppleMidiInvitationDeclined(invitation.getProtocolVersion(), invitation.getInitiatorToken(),
                            ssrc, name));
        }
    }

    private State getSessionServerState() {
        return sessions.isEmpty() ? State.FULL : State.ACCEPT_INVITATIONS;
    }

    private void sendMidiInvitationAnswer(final AppleMidiServer appleMidiServer, final String type,
                                          final AppleMidiInvitation midiInvitation) {
        try {
            log.info("Sending invitation {} to: {}", type, appleMidiServer);
            send(midiInvitation, appleMidiServer);
        } catch (final IOException e) {
            log.error("IOException while sending invitation {}", type, e);
        }
    }

    @Override
    public void onClockSynchronization(@Nonnull final AppleMidiClockSynchronization clockSynchronization,
                                       @Nonnull final AppleMidiServer appleMidiServer) {
        if (clockSynchronization.getCount() == (byte) 0) {
            final AppleMidiSessionAppleMidiServer sessionTuple = currentSessions.get(clockSynchronization.getSsrc());
            final long currentTimestamp;
            if (sessionTuple != null &&
                    sessionTuple.getAppleMidiSession().getCurrentTimestamp() != -1) {
                currentTimestamp = sessionTuple.getAppleMidiSession().getCurrentTimestamp();
            } else {
                currentTimestamp = ManagementFactory.getRuntimeMXBean().getUptime() * 10;
            }
            final AppleMidiClockSynchronization clockSynchronizationAnswer =
                    new AppleMidiClockSynchronization(ssrc, (byte) 1, clockSynchronization.getTimestamp1(),
                            currentTimestamp, 0L);
            try {
                send(clockSynchronizationAnswer, appleMidiServer);
            } catch (final IOException e) {
                log.error("IOException while sending clock synchronization", e);
            }
        } else if (clockSynchronization.getCount() == (byte) 2) {
            final long offsetEstimate =
                    (clockSynchronization.getTimestamp3() + clockSynchronization.getTimestamp1()) / 2 -
                            clockSynchronization.getTimestamp2();
            Optional.ofNullable(currentSessions.get(clockSynchronization.getSsrc()))
                    .ifPresent(sessionTuple -> sessionTuple.getAppleMidiSession().setOffsetEstimate(offsetEstimate));
        }
    }

    @Override
    public void onEndSession(@Nonnull final AppleMidiEndSession appleMidiEndSession, @Nonnull final AppleMidiServer appleMidiServer) {
        log.info("Session end from: {}", appleMidiServer);
        Optional.ofNullable(currentSessions.get(appleMidiEndSession.getSsrc())).ifPresent(
                sessionTuple -> sessionTuple.getAppleMidiSession().onEndSession(appleMidiEndSession, appleMidiServer));
        final AppleMidiSessionAppleMidiServer sessionTuple = currentSessions.remove(appleMidiEndSession.getSsrc());
        if (sessionTuple != null) {
            sessions.add(sessionTuple.getAppleMidiSession());
            notifyMaxNumberOfSessions();
        }
    }

    @Override
    public void onMidiMessage(final MidiCommandHeader midiCommandHeader, final MidiMessage message,
                              final int timestamp) {
        final AppleMidiSessionAppleMidiServer sessionTuple =
                currentSessions.get(midiCommandHeader.getRtpHeader().getSsrc());
        if (sessionTuple != null) {
            sessionTuple.getAppleMidiSession().onMidiMessage(midiCommandHeader, message, timestamp);
        } else {
            log.debug("Could not find session for ssrc: {}", ssrc);
        }
    }

    public void addAppleMidiSession(@Nonnull final AppleMidiSession session) {
        sessions.add(session);
        notifyMaxNumberOfSessions();
    }

    private void notifyMaxNumberOfSessions() {
        sessionChangeListeners.forEach(listener -> listener.onMaxNumberOfSessionsChange(sessions.size()));
    }

    public int getNumberOfAvailableSessions() {
        return sessions.size();
    }

    public void removeAppleMidiSession(@Nonnull final AppleMidiSession session) {
        sessions.remove(session);
        final Optional<Integer> ssrcOptional = currentSessions.entrySet().stream()
                .filter(entry -> entry.getValue().getAppleMidiSession().equals(session)).findFirst()
                .map(Map.Entry::getKey);
        if (ssrcOptional.isPresent()) {
            currentSessions.remove(ssrcOptional.get());
            notifyMaxNumberOfSessions();
        }
    }

    public void registerSessionChangeListener(@Nonnull final SessionChangeListener listener) {
        sessionChangeListeners.add(listener);
    }

    public void unregisterSessionChangeListener(@Nonnull final SessionChangeListener listener) {
        sessionChangeListeners.remove(listener);
    }

}
