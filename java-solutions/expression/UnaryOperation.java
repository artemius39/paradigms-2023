package expression;

import java.util.Objects;

public abstract class UnaryOperation extends Operation {
    protected final AbstractExpression operand;

    public UnaryOperation(ToMiniString operand) {
        if (!(operand instanceof AbstractExpression)) {
            throw new UnsupportedOperationException("This operand is not supported: " + operand);
        }
        this.operand = (AbstractExpression) operand;
    }

    @Override
    public String toString() {
        return getSign() + "(" + operand + ")";
    }

    @Override
    public String toMiniString() {
        if (this.getPriority() > operand.getPriority()) {
            return getSign() + "(" + operand.toMiniString() + ")";
        } else {
            return getSign() + " " + operand.toMiniString();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(operand, getSign());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && this.getClass().equals(obj.getClass())) {
            return this.operand.equals(((UnaryOperation) obj).operand);
        }
        return false;
    }

    protected int eval(int a) {
        throw new UnsupportedOperationException("This operator does not support integer operands");
    }

    public int evaluate(int x) {
        if (!(operand instanceof Expression)) {
            throw new UnsupportedOperationException("Cannot evaluate operand");
        }
        return eval(((Expression) operand).evaluate(x));
    }

    public int evaluate(int x, int y, int z) {
        if (!(operand instanceof TripleExpression)) {
            throw new UnsupportedOperationException("Cannot evaluate operand");
        }
        return eval(((TripleExpression) operand).evaluate(x, y, z));
    }
}
