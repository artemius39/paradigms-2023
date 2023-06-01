package expression.exceptions;

public class IllegalOperandException extends ExpressionEvaluationException {
    public IllegalOperandException(String message) {
        super(message);
    }
}
