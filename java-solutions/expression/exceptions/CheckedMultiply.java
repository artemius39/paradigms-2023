package expression.exceptions;

import expression.Multiply;
import expression.TripleExpression;

public class CheckedMultiply extends Multiply {
    public CheckedMultiply(TripleExpression leftOperand, TripleExpression rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected int eval(int a, int b) {
        if (a == Integer.MIN_VALUE && b == -1) {
            throw new OverflowException();
        }

        int result = a * b;
        if (b != 0 && result / b != a) {
            throw new OverflowException();
        } else {
            return result;
        }
    }
}