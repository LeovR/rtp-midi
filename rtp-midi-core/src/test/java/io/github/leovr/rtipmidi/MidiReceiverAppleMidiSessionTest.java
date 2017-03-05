package io.github.leovr.rtipmidi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.util.Arrays;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MidiReceiverAppleMidiSessionTest {

    @Mock
    private Receiver receiver;
    @Mock
    private Receiver receiver2;

    @Test
    public void testSendSingleReceiver() throws Exception {
        final MidiReceiverAppleMidiSession session = new MidiReceiverAppleMidiSession(receiver);

        final ShortMessage message = new ShortMessage();
        final long timestamp = 5L;
        session.onMidiMessage(message, timestamp);

        verify(receiver).send(message, timestamp);
    }

    @Test
    public void testMultipleReceivers() throws Exception {
        final MidiReceiverAppleMidiSession session =
                new MidiReceiverAppleMidiSession(Arrays.asList(receiver, receiver2));

        final ShortMessage message = new ShortMessage();
        final long timestamp = 5L;
        session.onMidiMessage(message, timestamp);

        verify(receiver).send(message, timestamp);
        verify(receiver2).send(message, timestamp);
    }
}
