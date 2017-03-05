package io.github.leovr.rtipmidi.error;

public class AppleMidiControlServerRuntimeException extends RuntimeException {

    public AppleMidiControlServerRuntimeException() {
        super();
    }

    public AppleMidiControlServerRuntimeException(final String message) {
        super(message);
    }

    public AppleMidiControlServerRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AppleMidiControlServerRuntimeException(final Throwable cause) {
        super(cause);
    }
}
