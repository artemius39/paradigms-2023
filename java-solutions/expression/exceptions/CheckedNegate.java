package expression.exceptions;

import expression.Negate;
import expression.TripleExpression;

public class CheckedNegate extends Negate {
    public CheckedNegate(TripleExpression operand) {
        super(operand);
    }

    @Override
    public int eval(int a) {
        if (a == Integer.MIN_VALUE) {
            throw new OverflowException();
        }
        return super.eval(a);
    }
}
