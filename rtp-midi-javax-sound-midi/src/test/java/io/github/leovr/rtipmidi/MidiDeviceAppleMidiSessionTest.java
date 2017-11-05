package io.github.leovr.rtipmidi;

import io.github.leovr.rtipmidi.error.AppleMidiSessionInstantiationException;
import io.github.leovr.rtipmidi.model.MidiMessage;
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
import javax.sound.midi.Transmitter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MidiDeviceAppleMidiSessionTest {

    @Mock
    private MidiDevice midiDevice;
    @Mock
    private Receiver receiver;
    @Mock
    private Transmitter transmitter;

    @Rule
    public ExpectedException exception = ExpectedException.none();
    private boolean methodCalled;

    @Before
    public void setUp() throws Exception {
        given(midiDevice.isOpen()).willReturn(false);
        given(midiDevice.getMaxReceivers()).willReturn(-1);
        given(midiDevice.getReceiver()).willReturn(receiver);
        given(midiDevice.getMaxTransmitters()).willReturn(-1);
        given(midiDevice.getTransmitter()).willReturn(transmitter);

        methodCalled = false;
    }

    @Test
    public void testInstatniationAlreadyOpen() throws Exception {
        exception.expect(AppleMidiSessionInstantiationException.class);
        given(midiDevice.isOpen()).willReturn(true);

        new MidiDeviceAppleMidiSession(new MidiDeviceModePair(midiDevice, MidiDeviceMode.BIDIRECTIONAL));
    }

    @Test
    public void testNoMaxReceivers() throws Exception {
        exception.expect(AppleMidiSessionInstantiationException.class);
        given(midiDevice.getMaxReceivers()).willReturn(0);

        new MidiDeviceAppleMidiSession(new MidiDeviceModePair(midiDevice, MidiDeviceMode.BIDIRECTIONAL));
    }

    @Test
    public void testNoMaxTransmitters() throws Exception {
        exception.expect(AppleMidiSessionInstantiationException.class);
        given(midiDevice.getMaxTransmitters()).willReturn(0);

        new MidiDeviceAppleMidiSession(new MidiDeviceModePair(midiDevice, MidiDeviceMode.BIDIRECTIONAL));
    }

    @Test
    public void testOpenError() throws Exception {
        exception.expect(AppleMidiSessionInstantiationException.class);
        willThrow(new MidiUnavailableException()).given(midiDevice).open();

        new MidiDeviceAppleMidiSession(new MidiDeviceModePair(midiDevice, MidiDeviceMode.BIDIRECTIONAL));
    }

    @Test
    public void testGetReceiverError() throws Exception {
        exception.expect(AppleMidiSessionInstantiationException.class);
        willThrow(new MidiUnavailableException()).given(midiDevice).getReceiver();

        new MidiDeviceAppleMidiSession(new MidiDeviceModePair(midiDevice, MidiDeviceMode.BIDIRECTIONAL));
    }

    @Test
    public void testGetTransmitterError() throws Exception {
        exception.expect(AppleMidiSessionInstantiationException.class);
        willThrow(new MidiUnavailableException()).given(midiDevice).getTransmitter();

        new MidiDeviceAppleMidiSession(new MidiDeviceModePair(midiDevice, MidiDeviceMode.BIDIRECTIONAL));
    }

    @Test
    public void testNormalInstantiation() throws Exception {
        new MidiDeviceAppleMidiSession(new MidiDeviceModePair(midiDevice, MidiDeviceMode.BIDIRECTIONAL));

        verify(midiDevice).isOpen();
        verify(midiDevice).open();
        verify(midiDevice).getMaxReceivers();
        verify(midiDevice).getReceiver();
        verify(midiDevice).getMaxTransmitters();
        verify(midiDevice).getTransmitter();
        verify(transmitter).setReceiver(any(Receiver.class));
    }

    @Test
    public void testReadOnly() throws Exception {
        new MidiDeviceAppleMidiSession(new MidiDeviceModePair(midiDevice, MidiDeviceMode.READ_ONLY));

        verify(midiDevice).isOpen();
        verify(midiDevice).open();
        verify(midiDevice).getMaxReceivers();
        verify(midiDevice).getReceiver();
        verify(midiDevice, never()).getMaxTransmitters();
        verify(midiDevice, never()).getTransmitter();
    }


    @Test
    public void testWriteOnly() throws Exception {
        new MidiDeviceAppleMidiSession(new MidiDeviceModePair(midiDevice, MidiDeviceMode.WRITE_ONLY));

        verify(midiDevice).isOpen();
        verify(midiDevice).open();
        verify(midiDevice, never()).getMaxReceivers();
        verify(midiDevice, never()).getReceiver();
        verify(midiDevice).getMaxTransmitters();
        verify(midiDevice).getTransmitter();
    }

    @Test
    public void testGetMicroseconds() throws Exception {
        given(midiDevice.getMicrosecondPosition()).willReturn(13300L);

        final MidiDeviceAppleMidiSession session =
                new MidiDeviceAppleMidiSession(new MidiDeviceModePair(midiDevice, MidiDeviceMode.BIDIRECTIONAL));
        session.setTimestampOffset(0);

        assertThat(session.getCurrentTimestamp()).isEqualTo(133L);
    }

    @Test
    public void testCloseMidiDevice() throws Exception {
        final MidiDeviceAppleMidiSession session =
                new MidiDeviceAppleMidiSession(new MidiDeviceModePair(midiDevice, MidiDeviceMode.BIDIRECTIONAL));

        session.onEndSession();

        verify(midiDevice).close();
    }

    @Test
    public void testOnMidiMessage() throws Exception {
        final ShortMessage message = new ShortMessage();
        final long timestamp = 5L;

        final MidiDeviceAppleMidiSession session =
                new MidiDeviceAppleMidiSession(new MidiDeviceModePair(midiDevice, MidiDeviceMode.BIDIRECTIONAL));
        session.setTimestampOffset(0);
        session.onMidiMessage(message, timestamp);

        verify(receiver).send(message, timestamp);
    }

    @Test
    public void testSendMidiMessage() throws Exception {
        final MidiDeviceAppleMidiSession session =
                new MidiDeviceAppleMidiSession(new MidiDeviceModePair(midiDevice, MidiDeviceMode.BIDIRECTIONAL)) {
                    @Override
                    public void sendMidiMessage(final MidiMessage message, final long timestamp) {
                        methodCalled = true;
                    }
                };

        session.sendMidiMessage(new ShortMessage(), 1L);

        assertThat(methodCalled).isTrue();
    }

}
