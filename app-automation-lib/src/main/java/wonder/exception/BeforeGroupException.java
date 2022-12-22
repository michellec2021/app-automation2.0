package wonder.exception;

/**
 * @author michelle
 */
public class BeforeGroupException extends BaseException {

    private static final long serialVersionUID = -8511117820026505015L;

    public BeforeGroupException(String message) {
        super(message);
    }

    public BeforeGroupException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeforeGroupException(Throwable cause) {
        super(cause);
    }

    protected BeforeGroupException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
