package expression;

public class Set extends BinaryOperation implements SuperExpression {

    public Set(TripleExpression leftOperand, TripleExpression rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected int eval(int a, int b) {
        return a | (1 << b);
    }

    @Override
    protected int getPriority() {
        return Priority.SET.ordinal();
    }

    @Override
    protected String getSign() {
        return "set";
    }
}
