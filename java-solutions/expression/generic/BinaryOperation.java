package expression.generic;

import java.util.function.BinaryOperator;

public class BinaryOperation<T extends Number> implements TripleExpression<T>  {
    private final TripleExpression<T> leftOperand;
    private final TripleExpression<T> rightOperand;
    private final BinaryOperator<T> operation;

    public BinaryOperation(TripleExpression<T> leftOperand, TripleExpression<T> rightOperand, BinaryOperator<T> operation) {
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.operation = operation;
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return operation.apply(leftOperand.evaluate(x, y, z), rightOperand.evaluate(x, y, z));
    }
}
