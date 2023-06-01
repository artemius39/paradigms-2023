package expression;

public class Count extends UnaryOperation implements TripleExpression {
    public Count(TripleExpression operand) {
        super(operand);
    }

    @Override
    protected String getSign() {
        return "count";
    }

    @Override
    public int eval(int a) {
        int ans = 0;
        for (int i = 0; i < 32; i++) {
            if ((a & (1 << i)) != 0) {
                ans++;
            }
        }
        return ans;
    }
}
