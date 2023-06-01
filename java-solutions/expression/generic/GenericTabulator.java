package expression.generic;

import expression.exceptions.ExpressionEvaluationException;

public class GenericTabulator implements Tabulator {
    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
        final Evaluator<? extends Number> evaluator = switch (mode) {
            case "i" -> new CheckedIntegerEvaluator();
            case "d" -> new DoubleEvaluator();
            case "bi" -> new BigIntegerEvaluator();
            case "u" -> new UncheckedIntegerEvaluator();
            case "l" -> new LongEvaluator();
            case "s" -> new ShortEvaluator();
            default -> throw new IllegalArgumentException("unsupported mode: " + mode);
        };
        return tabulate(evaluator, expression, x1, x2, y1, y2, z1, z2);
    }

    private static <T extends Number> Object[][][] tabulate(Evaluator<T> evaluator,
                                                            String expression,
                                                            int x1, int x2,
                                                            int y1, int y2,
                                                            int z1, int z2
    ) throws Exception {
        final TripleExpression<T> parsedExpression = new ExpressionParser<>(evaluator).parse(expression);

        final Object[][][] table = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    try {
                        table[x - x1][y - y1][z - z1] = parsedExpression.evaluate(
                                evaluator.valueOf(x),
                                evaluator.valueOf(y),
                                evaluator.valueOf(z)
                        );
                    } catch (Exception e) {
                        table[x - x1][y - y1][z - z1] = null;
                    }
                }
            }
        }

        return table;
    }
}