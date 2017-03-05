package io.github.leovr.rtipmidi.error;

public class AppleMidiSessionServerRuntimeException extends RuntimeException {

    public AppleMidiSessionServerRuntimeException() {
        super();
    }

    public AppleMidiSessionServerRuntimeException(final String message) {
        super(message);
    }

    public AppleMidiSessionServerRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AppleMidiSessionServerRuntimeException(final Throwable cause) {
        super(cause);
    }

    public AppleMidiSessionServerRuntimeException(final String message, final Throwable cause,
                                                  final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
