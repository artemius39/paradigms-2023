package expression.exceptions;

import expression.*;
import expression.parser.*;
import expression.parser.BinaryOperation;

import java.util.Map;
import java.util.Objects;

public class ExpressionParser implements TripleParser {
    @Override
    public TripleExpression parse(final String expression) throws ExpressionParsingException {
        return new Parser(new StringSource(Objects.requireNonNull(expression))).parse();
    }

    private static class Parser extends BaseParser {
        private static final Map<BinaryOperation, String> OPERATORS = Map.of(
                BinaryOperation.ADD, "+",
                BinaryOperation.SUBTRACT, "-",
                BinaryOperation.MULTIPLY, "*",
                BinaryOperation.DIVIDE, "/",
                BinaryOperation.CLEAR, "clear",
                BinaryOperation.SET, "set"
        );
        private static final Map<String, BinaryOperation> OPERATIONS = Map.of(
                "+", BinaryOperation.ADD,
                "-", BinaryOperation.SUBTRACT,
                "*", BinaryOperation.MULTIPLY,
                "/", BinaryOperation.DIVIDE,
                "clear", BinaryOperation.CLEAR,
                "set", BinaryOperation.SET
        );

        private static TripleExpression applyOperation(BinaryOperation operation,
                                                       TripleExpression leftOperand,
                                                       TripleExpression rightOperand) {
            return switch (operation) {
                case ADD -> new CheckedAdd(leftOperand, rightOperand);
                case SUBTRACT -> new CheckedSubtract(leftOperand, rightOperand);
                case MULTIPLY -> new CheckedMultiply(leftOperand, rightOperand);
                case DIVIDE -> new CheckedDivide(leftOperand, rightOperand);
                case SET -> new Set(leftOperand, rightOperand);
                case CLEAR -> new Clear(leftOperand, rightOperand);
                default -> throw new IllegalArgumentException("This operation is not supported");
            };
        }

        private BinaryOperation previouslyParsedBinaryOperation;

        private Parser(CharSource source) {
            super(source);
            previouslyParsedBinaryOperation = null;
        }

        protected ExpressionParsingException error1(String message) {
            return error(message, getPosition() - (eof() ? 0 : 1));
        }

        private ExpressionParsingException error(String message, int pos) {
            return new ExpressionParsingException(pos + 1 + ": " + message);
        }

        private TripleExpression parse() throws ExpressionParsingException {
            TripleExpression result = parseExpression();

            if (eof()) {
                return result;
            }
            throw error1("end of expression or binary operator expected");
        }

        private TripleExpression parseExpression() throws ExpressionParsingException {
            TripleExpression result = parseExpression(Integer.MIN_VALUE);
            if (previouslyParsedBinaryOperation != null) {
                throw error1("missing right operand for '" + OPERATORS.get(previouslyParsedBinaryOperation) + "'");
            }

            if (result == null) {
                BinaryOperation operation = parseBinaryOperation();
                if (operation != null) {
                    throw error1("missing left operand for '" + OPERATORS.get(operation) + "'");
                } else if (eof()){
                    throw error1("expression expected");
                } else {
                    throw error1("unexpected character: '" + ch + "'");
                }
            }

            return result;
        }

        // finds right operand for an operation
        private TripleExpression parseExpression(final int priority) throws ExpressionParsingException {
            TripleExpression operand = parsePrimary();
            if (operand == null) {
                // no operand was found
                return null;
            }
            skipWhitespace();
            while (!eof()) {
                final BinaryOperation binaryOperation = parseBinaryOperation();
                if (binaryOperation == null) {
                    // no operation was found
                    break;
                } else if (binaryOperation.priority <= priority) {
                    previouslyParsedBinaryOperation = binaryOperation;
                    break;
                } else {
                    final TripleExpression rightOperand = parseExpression(binaryOperation.priority);
                    if (rightOperand == null) {
                        previouslyParsedBinaryOperation = binaryOperation;
                        break;
                    }
                    operand = applyOperation(binaryOperation, operand, rightOperand);
                    skipWhitespace();
                }
            }
            return operand;
        }

        private BinaryOperation parseBinaryOperation() throws ExpressionParsingException {
            if (previouslyParsedBinaryOperation != null) {
                final BinaryOperation result = previouslyParsedBinaryOperation;
                previouslyParsedBinaryOperation = null;
                return result;
            }

            // :NOTE: Map
            BinaryOperation binaryOperation = OPERATIONS.get(String.valueOf(ch));
            if (binaryOperation != null) {
                take();
                return binaryOperation;
            }

            // :NOTE: Map
            final String identifier = parseIdentifier();
            if (identifier.isEmpty()){
                return null;
            }
            binaryOperation = OPERATIONS.get(identifier);
            if (binaryOperation != null) {
                return binaryOperation;
            } else {
                throw error("unexpected identifier: " + identifier, getPosition() - identifier.length());
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

        private TripleExpression parsePrimary() throws ExpressionParsingException {
            skipWhitespace();
            if (take('(')) {
                final TripleExpression result = parseExpression();
                if (result == null) {
                    throw error1("expression expected after '('");
                }
                skipWhitespace();
                if (!take(')')) {
                    throw error1("')' or binary operator expected");
                }
                return result;
            }

            StringBuilder sb = new StringBuilder();
            if (take('-')) {
                sb.append('-');
            }

            if (Character.isDigit(ch)) {
                sb.append(take());
                while (Character.isDigit(ch)) {
                    sb.append(take());
                }
                final String number = sb.toString();
                try {
                    return new Const(Integer.parseInt(number));
                } catch (NumberFormatException e) {
                    throw error1("constant overflow: " + number);
                }
            }

            final String primary = sb.isEmpty() ? parseIdentifier() : "-";
            return switch (primary) {
                case "" -> null;
                case "x", "y", "z" -> new Variable(primary);
                case "-" -> new CheckedNegate(getOperandForUnaryOperation(primary));
                case "count" -> new Count(getOperandForUnaryOperation(primary));
                case "pow10" -> new Pow10(getOperandForUnaryOperation(primary));
                case "log10" -> new Log10(getOperandForUnaryOperation(primary));
                default -> throw error("invalid identifier at expression start: '" + primary + "'", getPosition() - primary.length());
            };
        }

        private TripleExpression getOperandForUnaryOperation(String operator) throws ExpressionParsingException {
            final TripleExpression operand = parsePrimary();
            if (operand == null) {
                throw error1("missing operand for '" + operator + "'");
            } else {
                return operand;
            }
        }
    }
}
