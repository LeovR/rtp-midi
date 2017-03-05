package io.github.leovr.rtipmidi;

import io.github.leovr.rtipmidi.model.ShortMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class JavaxAppleMidiSessionTest {

    @Spy
    private JavaxAppleMidiSession session;
    @Mock
    private JavaxMidiMessageConverter midiMessageConverter;

    @Before
    public void setUp() throws Exception {
        session.setMidiMessageConverter(midiMessageConverter);
    }

    @Test
    public void testOnMidiMessage() throws Exception {
        final ShortMessage message = new ShortMessage((byte) ShortMessage.START);

        final javax.sound.midi.ShortMessage javaxMessage = new javax.sound.midi.ShortMessage(ShortMessage.START);
        given(midiMessageConverter.convert(message)).willReturn(javaxMessage);

        session.onMidiMessage(message, 0L);

        verify(midiMessageConverter).convert(message);
        verify(session).onMidiMessage(javaxMessage, 0L);

    }
}
