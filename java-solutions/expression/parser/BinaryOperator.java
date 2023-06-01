package expression.parser;

import expression.*;

public enum BinaryOperator {
    SET(Priority.SET), CLEAR(Priority.SET),
    ADD(Priority.ADD), SUBTRACT(Priority.ADD),
    MULTIPLY(Priority.MULTIPLY), DIVIDE(Priority.MULTIPLY);

    public final int priority;

    BinaryOperator(Priority priority) {
        this.priority = priority.ordinal();
    }

    public TripleExpression applyOperation(TripleExpression left, TripleExpression right) {
        return switch (this) {
            case SET -> new Set(left, right);
            case CLEAR -> new Clear(left, right);
            case ADD -> new Add(left, right);
            case SUBTRACT -> new Subtract(left, right);
            case MULTIPLY -> new Multiply(left, right);
            case DIVIDE -> new Divide(left, right);
        };
    }
}
