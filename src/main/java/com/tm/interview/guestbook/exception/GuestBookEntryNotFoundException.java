package com.tm.interview.guestbook.exception;

public class GuestBookEntryNotFoundException extends RuntimeException {

    public GuestBookEntryNotFoundException() {
        super();
    }

    public GuestBookEntryNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public GuestBookEntryNotFoundException(final String message) {
        super(message);
    }

    public GuestBookEntryNotFoundException(final Throwable cause) {
        super(cause);
    }
}