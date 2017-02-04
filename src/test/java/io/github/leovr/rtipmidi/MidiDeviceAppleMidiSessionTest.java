package io.github.leovr.rtipmidi;

import io.github.leovr.rtipmidi.error.AppleMidiSessionInstantiationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MidiDeviceAppleMidiSessionTest {

    @Mock
    private MidiDevice midiDevice;
    @Mock
    private Receiver receiver;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        given(midiDevice.isOpen()).willReturn(false);
        given(midiDevice.getMaxReceivers()).willReturn(-1);
        given(midiDevice.getReceiver()).willReturn(receiver);
    }

    @Test
    public void testInstatniationAlreadyOpen() throws Exception {
        exception.expect(AppleMidiSessionInstantiationException.class);
        given(midiDevice.isOpen()).willReturn(true);

        new MidiDeviceAppleMidiSession(midiDevice);

        verify(midiDevice).isOpen();
    }

    @Test
    public void testNoMaxReceivers() throws Exception {
        exception.expect(AppleMidiSessionInstantiationException.class);
        given(midiDevice.getMaxReceivers()).willReturn(0);

        new MidiDeviceAppleMidiSession(midiDevice);

        verify(midiDevice).isOpen();
        verify(midiDevice).getMaxReceivers();
    }

    @Test
    public void testOpenError() throws Exception {
        exception.expect(AppleMidiSessionInstantiationException.class);
        willThrow(new MidiUnavailableException()).given(midiDevice).open();

        new MidiDeviceAppleMidiSession(midiDevice);

        verify(midiDevice).isOpen();
        verify(midiDevice).getMaxReceivers();
        verify(midiDevice).open();
    }

    @Test
    public void testGetReceiverError() throws Exception {
        exception.expect(AppleMidiSessionInstantiationException.class);
        willThrow(new MidiUnavailableException()).given(midiDevice).getReceiver();

        new MidiDeviceAppleMidiSession(midiDevice);

        verify(midiDevice).isOpen();
        verify(midiDevice).getMaxReceivers();
        verify(midiDevice).open();
        verify(midiDevice).getReceiver();
    }

    @Test
    public void testNormalInstantiation() throws Exception {
        new MidiDeviceAppleMidiSession(midiDevice);

        verify(midiDevice).isOpen();
        verify(midiDevice).getMaxReceivers();
        verify(midiDevice).open();
        verify(midiDevice).getReceiver();
    }

    @Test
    public void testGetMicroseconds() throws Exception {
        given(midiDevice.getMicrosecondPosition()).willReturn(133L);

        final MidiDeviceAppleMidiSession session = new MidiDeviceAppleMidiSession(midiDevice);

        assertThat(session.getCurrentTimestamp()).isEqualTo(13300L);
    }

    @Test
    public void testCloseMidiDevice() throws Exception {
        final MidiDeviceAppleMidiSession session = new MidiDeviceAppleMidiSession(midiDevice);

        session.onEndSession();

        verify(midiDevice).close();
    }

    @Test
    public void sendMidiMessage() throws Exception {
        final ShortMessage message = new ShortMessage();
        final long timestamp = 5L;

        final MidiDeviceAppleMidiSession session = new MidiDeviceAppleMidiSession(midiDevice);
        session.onMidiMessage(message, timestamp);

        verify(receiver).send(message, timestamp);
    }
}
