package expression.exceptions;

public class DivisionByZeroException extends ExpressionEvaluationException{
    public DivisionByZeroException() {
        super("division by zero");
    }
}
