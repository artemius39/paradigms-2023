package expression.exceptions;

public class OverflowException extends ExpressionEvaluationException {
    public OverflowException() {
        super("overflow");
    }
}
