package wonder.exception;

/**
 * @author michelle
 */
public class BeforeTestException extends BaseException {
    private static final long serialVersionUID = 464116507448615926L;

    public BeforeTestException(String message) {
        super(message);
    }

    public BeforeTestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeforeTestException(Throwable cause) {
        super(cause);
    }

    protected BeforeTestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
