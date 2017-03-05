package io.github.leovr.rtipmidi.error;

public class AppleMidiSessionInstantiationException extends RuntimeException {

    public AppleMidiSessionInstantiationException() {
        super();
    }

    public AppleMidiSessionInstantiationException(final String message) {
        super(message);
    }

    public AppleMidiSessionInstantiationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AppleMidiSessionInstantiationException(final Throwable cause) {
        super(cause);
    }

    public AppleMidiSessionInstantiationException(final String message, final Throwable cause,
                                                  final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
