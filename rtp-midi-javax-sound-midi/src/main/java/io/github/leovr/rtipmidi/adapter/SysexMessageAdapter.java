package io.github.leovr.rtipmidi.adapter;

import io.github.leovr.rtipmidi.model.SysexMessage;

/**
 * Adapter class to make the {@link SysexMessage} compatible with {@link javax.sound.midi.SysexMessage}
 */
public class SysexMessageAdapter extends javax.sound.midi.SysexMessage {

    public SysexMessageAdapter(final SysexMessage message) {
        super(message.getData());
    }
}
