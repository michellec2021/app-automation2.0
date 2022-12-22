package wonder.exception;

/**
 * @author michelle
 */
public class AfterTestException extends BaseException {

    private static final long serialVersionUID = -3414211960625894658L;

    public AfterTestException(String message) {
        super(message);
    }

    public AfterTestException(String message, Throwable cause) {
        super(message, cause);
    }

    public AfterTestException(Throwable cause) {
        super(cause);
    }

    protected AfterTestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
