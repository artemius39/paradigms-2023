package expression.parser;

import expression.*;

public class ExpressionParser implements TripleParser {
    @Override
    public TripleExpression parse(final String expression) {
        return new Parser(new StringSource(expression)).parseExpression();
    }

    private static class Parser extends BaseParser {
        private static TripleExpression applyOperation(BinaryOperation operation, TripleExpression left, TripleExpression right) {
            return switch (operation) {
                case SET -> new Set(left, right);
                case CLEAR -> new Clear(left, right);
                case ADD -> new Add(left, right);
                case SUBTRACT -> new Subtract(left, right);
                case MULTIPLY -> new Multiply(left, right);
                case DIVIDE -> new Divide(left, right);
                default -> throw new IllegalArgumentException("This operation is not supported: " + operation);
            };
        }

        private BinaryOperation previouslyReadBinaryOperation;

        private Parser(final CharSource source) {
            super(source);
            previouslyReadBinaryOperation = null;
        }

        private TripleExpression parseExpression() {
            return parseExpression(Integer.MIN_VALUE);
        }

        // parses expression as an operand for an operation with given priority
        private TripleExpression parseExpression(final int priority) {
            TripleExpression operand = parsePrimary();
            skipWhitespace();

            while (!eof()) {
                final BinaryOperation binaryOperation = parseBinaryOperator();
                if (binaryOperation == null) {
                    break;
                } else if (binaryOperation.priority <= priority) {
                    previouslyReadBinaryOperation = binaryOperation;
                    break;
                } else {
                    operand = applyOperation(binaryOperation, operand, parseExpression(binaryOperation.priority));
                    skipWhitespace();
                }
            }
            return operand;
        }

        private BinaryOperation parseBinaryOperator() {
            if (previouslyReadBinaryOperation != null) {
                final BinaryOperation result = previouslyReadBinaryOperation;
                previouslyReadBinaryOperation = null;
                return result;
            }

            if (take('+')) {
                return BinaryOperation.ADD;
            } else if (take('-')) {
                return BinaryOperation.SUBTRACT;
            } else if (take('*')) {
                return BinaryOperation.MULTIPLY;
            } else if (take('/')) {
                return BinaryOperation.DIVIDE;
            }

            final String operator = parseIdentifier();
            if (operator.equals("set")) {
                return BinaryOperation.SET;
            } else if (operator.equals("clear")) {
                return BinaryOperation.CLEAR;
            } else {
                return null;
            }
        }

        private String parseIdentifier() {
            final StringBuilder sb = new StringBuilder();
            if (Character.isJavaIdentifierStart(ch)) {
                sb.append(take());
                while (!eof() && Character.isJavaIdentifierPart(ch)) {
                    sb.append(take());
                }
            }
            return sb.toString();
        }

        private String getPrimary() {
            if (take('(')) {
                return "(";
            }

            StringBuilder sb = new StringBuilder();
            if (take('-')) {
                sb.append('-');
            }
            while (Character.isDigit(ch)) {
                sb.append(take());
            }

            if (!sb.isEmpty()) {
                return sb.toString();
            } else {
                return parseIdentifier();
            }
        }

        private TripleExpression parsePrimary() {
            skipWhitespace();

            String primary = getPrimary();
            if (primary.isEmpty()) {
                throw error("expression expected");
            }

            switch (primary) {
                case "(" -> {
                    final TripleExpression result = parseExpression();
                    skipWhitespace();
                    expect(')');
                    return result;
                }
                case "-" -> {
                    return new Negate(parsePrimary());
                }
                case "count" -> {
                    return new Count(parsePrimary());
                }
                case "x", "y", "z" -> {
                    return new Variable(primary);
                }
                default -> {
                    try {
                        return new Const(Integer.parseInt(primary));
                    } catch (final NumberFormatException e) {
                        throw error("invalid expression start: " + primary);
                    }
                }
            }
        }
    }
}
