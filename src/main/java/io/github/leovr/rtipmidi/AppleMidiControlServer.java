package io.github.leovr.rtipmidi;

import io.github.leovr.rtipmidi.error.AppleMidiControlServerRuntimeException;
import io.github.leovr.rtipmidi.messages.AppleMidiClockSynchronization;
import io.github.leovr.rtipmidi.messages.AppleMidiCommand;
import io.github.leovr.rtipmidi.messages.AppleMidiEndSession;
import io.github.leovr.rtipmidi.messages.AppleMidiInvitation;
import io.github.leovr.rtipmidi.messages.AppleMidiInvitationAccepted;
import io.github.leovr.rtipmidi.messages.AppleMidiInvitationDeclined;
import io.github.leovr.rtipmidi.messages.AppleMidiInvitationRequest;
import io.github.leovr.rtipmidi.model.AppleMidiServer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
public class AppleMidiControlServer extends Thread implements AppleMidiCommandListener {

    private static final int SOCKET_TIMEOUT = 1000;

    private enum State {
        ACCEPT_INVITATIONS, FULL
    }

    private static final int RECEIVE_BUFFER_LENGTH = 1024;
    private static final String THREAD_SUFFIX = "ControlThread";
    private final int port;
    @Getter
    @Setter
    private int maxNumberOfSessions;
    private boolean running = true;
    private final int ssrc;
    private final String name;
    private final AppleMidiCommandHandler handler = new AppleMidiCommandHandler();
    private DatagramSocket socket;
    private final List<AppleMidiServer> acceptedServers = new ArrayList<>();

    public AppleMidiControlServer(final String name, final int port) {
        super(name + THREAD_SUFFIX);
        this.port = port;
        this.ssrc = new Random().nextInt();
        this.name = name;
        handler.registerListener(this);
    }

    @Override
    public synchronized void start() {
        try {
            socket = new DatagramSocket(port);
            socket.setSoTimeout(SOCKET_TIMEOUT);
        } catch (final SocketException e) {
            throw new AppleMidiControlServerRuntimeException("DatagramSocket cannot be opened", e);
        }
        super.start();
        log.debug("MIDI control server started");
    }

    public void stopServer() {
        acceptedServers.forEach(server -> {
            try {
                log.info("Sending end session to {}", server);
                send(new AppleMidiEndSession(2, new Random().nextInt(), ssrc), server);
            } catch (final IOException e) {
                log.info("Error closing session with server: {}", server, e);
            }
        });
        running = false;
        acceptedServers.clear();
        log.debug("MIDI control server stopped");
    }

    @Override
    public void run() {
        while (running) {
            try {
                final byte[] receiveData = new byte[RECEIVE_BUFFER_LENGTH];

                final DatagramPacket incomingPacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(incomingPacket);
                handler.handle(receiveData, new AppleMidiServer(incomingPacket.getAddress(), incomingPacket.getPort()));
            } catch (final SocketTimeoutException ignored) {
            } catch (final IOException e) {
                log.error("IOException while receiving", e);
            }
        }
        socket.close();
    }

    @Override
    public void onMidiInvitation(final AppleMidiInvitationRequest invitation, final AppleMidiServer appleMidiServer) {
        log.info("MIDI invitation from: {}", appleMidiServer);
        if (getServerState() == State.ACCEPT_INVITATIONS) {
            sendMidiInvitationAnswer(appleMidiServer, "accept",
                    new AppleMidiInvitationAccepted(invitation.getProtocolVersion(), invitation.getInitiatorToken(),
                            ssrc, name));
            acceptedServers.add(appleMidiServer);
        } else {
            sendMidiInvitationAnswer(appleMidiServer, "decline",
                    new AppleMidiInvitationDeclined(invitation.getProtocolVersion(), invitation.getInitiatorToken(),
                            ssrc, name));
        }

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

    private void send(final AppleMidiCommand midiCommand, final AppleMidiServer appleMidiServer) throws IOException {
        final byte[] invitationAcceptedBytes = midiCommand.toByteArray();

        socket.send(new DatagramPacket(invitationAcceptedBytes, invitationAcceptedBytes.length,
                appleMidiServer.getInetAddress(), appleMidiServer.getPort()));
    }

    @Override
    public void onClockSynchronization(final AppleMidiClockSynchronization clockSynchronization,
                                       final AppleMidiServer appleMidiServer) {
    }

    private State getServerState() {
        return acceptedServers.size() < maxNumberOfSessions ? State.ACCEPT_INVITATIONS : State.FULL;
    }

    @Override
    public void onEndSession(final AppleMidiEndSession appleMidiEndSession, final AppleMidiServer appleMidiServer) {
        log.info("Session ended with: {}", appleMidiServer);
        acceptedServers.remove(appleMidiServer);
    }
}
