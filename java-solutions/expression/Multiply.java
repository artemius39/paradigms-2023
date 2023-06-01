package expression;

public class Multiply extends BinaryOperation implements SuperExpression {

    public Multiply(SuperExpression leftOperand, SuperExpression rightOperand) {
        super(leftOperand, rightOperand);
    }

    public Multiply(TripleExpression leftOperand, TripleExpression rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String parseEqualPriority() {
        if (rightOperand instanceof Multiply) {
            // a * (b * c) == a * b * c, parentheses are redundant
            return rightOperand.toMiniString();
        }
        return super.parseEqualPriority();
    }

    @Override
    protected int eval(int a, int b) {
        return a * b;
    }

    @Override
    protected double eval(double a, double b) {
        return a * b;
    }

    @Override
    protected String getSign() {
        return "*";
    }

    @Override
    protected int getPriority() {
        return Priority.MULTIPLY.ordinal();
    }
}
