package expression.generic;

import java.util.function.UnaryOperator;

public class UnaryOperation<T extends Number> implements TripleExpression<T> {
    private final TripleExpression<T> operand;
    private final UnaryOperator<T> operation;

    public UnaryOperation(TripleExpression<T> operand, UnaryOperator<T> operation) {
        this.operand = operand;
        this.operation = operation;
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return operation.apply(operand.evaluate(x, y, z));
    }
}
