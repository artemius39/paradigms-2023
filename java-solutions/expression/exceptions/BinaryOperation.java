package expression.exceptions;

import expression.*;

public enum BinaryOperation {
    SET(Priority.SET), CLEAR(Priority.SET),
    ADD(Priority.ADD), SUBTRACT(Priority.ADD),
    MULTIPLY(Priority.MULTIPLY), DIVIDE(Priority.MULTIPLY),
    NO_OPERATION(Integer.MIN_VALUE);

    public final int priority;

    BinaryOperation(Priority priority) {
        this.priority = priority.ordinal();
    }

    BinaryOperation(int priority) {
        this.priority = priority;
    }

    public TripleExpression applyOperation(TripleExpression leftOperand, TripleExpression rightOperand) {
        return switch (this) {
            case ADD -> new CheckedAdd(leftOperand, rightOperand);
            case SUBTRACT -> new CheckedSubtract(leftOperand, rightOperand);
            case MULTIPLY -> new CheckedMultiply(leftOperand, rightOperand);
            case DIVIDE -> new CheckedDivide(leftOperand, rightOperand);
            case SET -> new Set(leftOperand, rightOperand);
            case CLEAR -> new Clear(leftOperand, rightOperand);
            case NO_OPERATION -> throw new UnsupportedOperationException(
                    "NO_OPERATION is not an actual operation and therefore cannot be applied"
            );
        };
    }
}
