package expression;

public class Subtract extends BinaryOperation implements SuperExpression {
    public Subtract(SuperExpression leftOperand, SuperExpression rightOperand) {
        super(leftOperand, rightOperand);
    }

    public Subtract(TripleExpression leftOperand, TripleExpression rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected int eval(int a, int b) {
        return a - b;
    }

    @Override
    protected double eval(double a, double b) {
        return a - b;
    }

    @Override
    protected String getSign() {
        return "-";
    }

    @Override
    protected int getPriority() {
        return Priority.ADD.ordinal();
    }
}
