package expression.exceptions;

import expression.Add;
import expression.TripleExpression;

public class CheckedAdd extends Add {
    public CheckedAdd(TripleExpression leftOperand, TripleExpression rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected int eval(int a, int b) {
        int r = a + b;
        if (r < a && b > 0 || r > a && b < 0) {
            throw new OverflowException();
        }
        return r;
    }
}
