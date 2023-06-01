package expression;

public class Divide extends BinaryOperation implements SuperExpression {
    public Divide(SuperExpression leftOperand, SuperExpression rightOperand) {
        super(leftOperand, rightOperand);
    }

    public Divide(TripleExpression leftOperand, TripleExpression rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected int eval(int a, int b) {
        return a / b;
    }

    @Override
    protected double eval(double a, double b) {
        return a / b;
    }

    @Override
    protected String getSign() {
        return "/";
    }

    @Override
    protected int getPriority() {
        return Priority.MULTIPLY.ordinal();
    }
}