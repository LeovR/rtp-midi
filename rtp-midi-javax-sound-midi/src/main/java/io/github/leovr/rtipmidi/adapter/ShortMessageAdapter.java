package io.github.leovr.rtipmidi.adapter;

import io.github.leovr.rtipmidi.model.ShortMessage;

/**
 * Adapter class to make the {@link ShortMessage} compatible with {@link javax.sound.midi.ShortMessage}
 */
public class ShortMessageAdapter extends javax.sound.midi.ShortMessage {

    public ShortMessageAdapter(final ShortMessage message) {
        super(message.getData());
    }
}
