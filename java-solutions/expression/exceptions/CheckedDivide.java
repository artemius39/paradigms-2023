package expression.exceptions;

import expression.Divide;
import expression.TripleExpression;

public class CheckedDivide extends Divide {
    public CheckedDivide(TripleExpression leftOperand, TripleExpression rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected int eval(int a, int b) {
        if (b == 0) {
            throw new DivisionByZeroException();
        }
        if (a == Integer.MIN_VALUE && b == -1) {
            throw new OverflowException();
        }
        return super.eval(a, b);
    }
}
