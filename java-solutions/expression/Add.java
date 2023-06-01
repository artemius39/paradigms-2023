package expression;

public class Add extends BinaryOperation implements SuperExpression {

    public Add(SuperExpression leftOperand, SuperExpression rightOperand) {
        super(leftOperand, rightOperand);
    }

    public Add(TripleExpression leftOperand, TripleExpression rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getSign() {
        return "+";
    }

    @Override
    protected String parseEqualPriority() {
        if (rightOperand instanceof Add || rightOperand instanceof Subtract) {
            // a + (b + c) == a + b + c, parentheses are redundant
            // a + (b - c) == a + b - c, parentheses are redundant
            return rightOperand.toMiniString();
        }
        return super.parseEqualPriority();
    }

    @Override
    protected int eval(int a, int b) {
        return a + b;
    }

    @Override
    protected double eval(double a, double b) {
        return a + b;
    }

    @Override
    protected int getPriority() {
        return Priority.ADD.ordinal();
    }
}