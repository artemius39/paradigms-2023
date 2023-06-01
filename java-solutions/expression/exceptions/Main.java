package expression.exceptions;

import expression.TripleExpression;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Enter expression");
            return;
        }
        TripleParser parser = new ExpressionParser();

        try {
            TripleExpression parsed = parser.parse(args[0]);
            System.out.println("Parsing result:");
            System.out.println("    toString:     " + parsed);
            System.out.println("    toMiniString: " + parsed.toMiniString());
            if (args.length < 4) {
                return;
            }
            try {
                System.out.println(
                        "Evaluation result: " +
                        parsed.evaluate(
                                Integer.parseInt(args[1]),
                                Integer.parseInt(args[2]),
                                Integer.parseInt(args[3])
                        )
                );
            } catch (ExpressionEvaluationException e) {
                System.err.println("Could not evaluate: " + e.getMessage());
            }
        } catch (ExpressionParsingException e) {
            System.out.println("Parsing failed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
