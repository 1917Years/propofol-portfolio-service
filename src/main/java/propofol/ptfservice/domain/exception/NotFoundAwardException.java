package propofol.ptfservice.domain.exception;

import java.util.NoSuchElementException;

public class NotFoundAwardException extends NoSuchElementException {
    public NotFoundAwardException() {
    }

    public NotFoundAwardException(String s) {
        super(s);
    }
}
