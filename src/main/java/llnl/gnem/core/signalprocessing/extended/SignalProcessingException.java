package llnl.gnem.core.signalprocessing.extended;

/**
 * @author paik
 */
public class SignalProcessingException extends Exception {
    private static final long serialVersionUID = -8041494927599322508L;

    public SignalProcessingException() {
        super();
    }

    public SignalProcessingException(String msg) {
        super(msg);
    }

    public SignalProcessingException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SignalProcessingException(Throwable cause) {
        super(cause);
    }
}
