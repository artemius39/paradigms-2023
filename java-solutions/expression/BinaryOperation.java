package expression;

import java.util.Objects;

public abstract class BinaryOperation extends Operation {
    protected final AbstractExpression leftOperand;
    protected final AbstractExpression rightOperand;

    public BinaryOperation(ToMiniString leftOperand, ToMiniString rightOperand) {
        if (!(leftOperand instanceof AbstractExpression)) {
            throw new IllegalArgumentException("This operand is not supported: " + leftOperand);
        }
        if (!(rightOperand instanceof AbstractExpression)) {
            throw new IllegalArgumentException("This operand is not supported: " + rightOperand);
        }
        this.leftOperand = (AbstractExpression) leftOperand;
        this.rightOperand = (AbstractExpression) rightOperand;
    }

    @Override
    public String toString() {
        return "(" + leftOperand + " " + getSign() + " " + rightOperand + ")";
    }

    @Override
    public String toMiniString() {
        StringBuilder sb = new StringBuilder();

        if (leftOperand.getPriority() < this.getPriority()) {
            sb.append("(").append(leftOperand.toMiniString()).append(")");
        } else {
            sb.append(leftOperand.toMiniString());
        }

        sb.append(" ").append(getSign()).append(" ");

        if (rightOperand.getPriority() < this.getPriority()) {
            sb.append("(").append(rightOperand.toMiniString()).append(")");
        } else if (rightOperand.getPriority() > this.getPriority()) {
            sb.append(rightOperand.toMiniString());
        } else {
            sb.append(parseEqualPriority());
        }

        return sb.toString();
    }

    protected String parseEqualPriority() {
        return "(" + rightOperand.toMiniString() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && this.getClass() == obj.getClass()) {
            return  leftOperand.equals(((BinaryOperation) obj).leftOperand) &&
                    rightOperand.equals(((BinaryOperation) obj).rightOperand);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftOperand, rightOperand, getSign());
    }

    protected int eval(int a, int b) {
        throw new UnsupportedOperationException("This operator does not support integer operands");
    }

    protected double eval(double a, double b) {
        throw new UnsupportedOperationException("This operator does not support double operands");
    }

    public int evaluate(int x) {
        if (!(leftOperand instanceof Expression)) {
            throw new UnsupportedOperationException("Cannot evaluate left operand");
        }
        if (!(rightOperand instanceof Expression)) {
            throw new UnsupportedOperationException("Cannot evaluate right operand");
        }
        return eval(
                ((Expression) leftOperand).evaluate(x),
                ((Expression) rightOperand).evaluate(x)
        );
    }

    public int evaluate(int x, int y, int z) {
        if (!(leftOperand instanceof TripleExpression)) {
            throw new UnsupportedOperationException("Cannot evaluate left operand");
        }
        if (!(rightOperand instanceof TripleExpression)) {
            throw new UnsupportedOperationException("Cannot evaluate right operand");
        }
        return eval(
                ((TripleExpression) leftOperand).evaluate(x, y, z),
                ((TripleExpression) rightOperand).evaluate(x, y, z)
        );
    }

    public double evaluate(double x) {
        if (!(leftOperand instanceof DoubleExpression)) {
            throw new UnsupportedOperationException("Cannot evaluate left operand");
        }
        if (!(rightOperand instanceof DoubleExpression)) {
            throw new UnsupportedOperationException("Cannot evaluate right operand");
        }
        return eval(
                ((DoubleExpression) leftOperand).evaluate(x),
                ((DoubleExpression) rightOperand).evaluate(x)
        );
    }
}
