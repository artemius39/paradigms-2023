package expression.exceptions;

public class ExpressionParsingException extends Exception {

    public ExpressionParsingException(String message) {
        super(message);
    }

    public ExpressionParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpressionParsingException(Throwable cause) {
        super(cause);
    }
}
