package expression;

public class Clear extends BinaryOperation implements TripleExpression {

    public Clear(TripleExpression leftOperand, TripleExpression rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public int eval(int a, int b) {
        return a & ~(1 << b);
    }

    @Override
    protected String getSign() {
        return "clear";
    }

    @Override
    protected int getPriority() {
        return Priority.SET.ordinal();
    }
}
