package ninja.hudy.infosmog.exception;

public class HttpCommunicationException extends Exception {

    public HttpCommunicationException() {
        super();
    }

    public HttpCommunicationException(String message) {
        super(message);
    }

    public HttpCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpCommunicationException(Throwable cause) {
        super(cause);
    }
}
