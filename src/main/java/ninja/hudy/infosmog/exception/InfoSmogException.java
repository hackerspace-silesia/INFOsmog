package ninja.hudy.infosmog.exception;

public class InfoSmogException extends Exception {
    public InfoSmogException() {
        super();
    }

    public InfoSmogException(String message) {
        super(message);
    }

    public InfoSmogException(String message, Throwable cause) {
        super(message, cause);
    }

    public InfoSmogException(Throwable cause) {
        super(cause);
    }
}
