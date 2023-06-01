package expression.parser;

import expression.*;

public enum BinaryOperation {
    SET(Priority.SET), CLEAR(Priority.SET),
    ADD(Priority.ADD), SUBTRACT(Priority.ADD),
    MULTIPLY(Priority.MULTIPLY), DIVIDE(Priority.MULTIPLY), MOD(Priority.MULTIPLY);

    public final int priority;

    BinaryOperation(Priority priority) {
        this.priority = priority.ordinal();
    }
}
