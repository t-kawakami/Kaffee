package jp.co.paper.game.framework;

/**
 * Created by kawakami_note on 2015/08/20.
 */
public class ApplicationException extends RuntimeException {
    public ApplicationException(Throwable th) {
        super(th);
    }

    public ApplicationException(String message, Throwable th) {
        super(message, th);
    }

    public ApplicationException(String message) {
        super(message);
    }
}
