"use strict";

function BaseOperation(...operands) {
    this.operands = operands;
    this.diffs = {};
}

BaseOperation.prototype.evaluate = function (...variables) {
    return this.eval(...this.operands.map(operand => operand.evaluate(...variables)));
}

BaseOperation.prototype.toString = function () {
    return this.operands.map(operand => operand.toString()).join(' ') + ' ' + this.getSign();
}

BaseOperation.prototype.prefix = function () {
    return '(' + this.getSign() + ' ' + this.operands.map(operand => operand.prefix()).join(' ') + ')';
}

BaseOperation.prototype.postfix = function () {
    return '(' + this.operands.map(operand => operand.postfix()).join(' ') + ' ' + this.getSign() + ')';
}

BaseOperation.prototype.diffByOperands = function (...operands) {
    if (this.diffByOps === undefined) {
        this.diffByOps = this.diffByOperandsImpl(...operands);
    }
    return this.diffByOps;
}

BaseOperation.prototype.diff = function (variable) {
    if (this.diffs[variable] === undefined) {
        // applying multivariable chain rule:
        // df/dt = df/dx0 * dx0/dt + df/dx1 * dx1/dt + ..., where x0, x1, ... are f's operands
        this.diffs[variable] = this.diffByOperands(...this.operands) // an array where i'th element is df/dxi
            .map((diffByOperand, i) => new Multiply(diffByOperand, this.operands[i].diff(variable)))
            .reduce((accumulator, currentValue) => new Add(accumulator, currentValue));
    }
    return this.diffs[variable];
}

function operation(sign, evaluate, diffByOperands) {
    function Operation(...operands) {
        BaseOperation.call(this, ...operands);
    }

    Operation.prototype = Object.create(BaseOperation.prototype);
    Operation.prototype.getSign = () => sign;
    Operation.prototype.eval = evaluate;
    const arity = evaluate.length === 0 ? Infinity : evaluate.length;
    Operation.getArity = () => arity;
    Operation.prototype.diffByOperandsImpl = diffByOperands;

    return Operation;
}

const Add = operation("+", (a, b) => a + b, () => [Const.ONE, Const.ONE]);
const Subtract = operation("-", (a, b) => a - b, () => [Const.ONE, Const.NEGATIVE_ONE]);
const Multiply = operation("*", (a, b) => a * b, (x, y) => [y, x]);
const Divide = operation(
    "/", (a, b) => a / b,
    (x, y) => [new Divide(Const.ONE, y), new Divide(new Negate(x), new Multiply(y, y))]
);
const Negate = operation("negate", a => -a, () => [Const.NEGATIVE_ONE]);
const SumsqN = operation(
    "sumsq", (...operands) => operands.reduce((accumulator, currentValue) => accumulator + currentValue * currentValue, 0),
    (...operands) => operands.map(operand => new Multiply(Const.TWO, operand))
);
const DistanceN = operation("distance", (...operands) => Math.sqrt(SumsqN.prototype.eval(...operands)));

// Override
DistanceN.prototype.diff = function (variable) {
    // d(sqrt(x1^2 + x2^2 + ...)/dxi = 1/2sqrt(x1^2 + x2^2 + ...) * d(x1^2 + x2^2 + ...)/dxi
    return new Divide(new SumsqN(...this.operands).diff(variable), new Multiply(Const.TWO, this));
}

const operationN = operation => function (n) {
    function Operation(...operands) {
        operation.call(this, ...operands);
    }

    Operation.prototype = Object.create(operation.prototype);
    const sign = operation.prototype.getSign() + n;
    Operation.prototype.getSign = () => sign;
    Operation.getArity = () => n;

    return Operation;
};

const Sumsq = operationN(SumsqN);
const Sumsq2 = Sumsq(2);
const Sumsq3 = Sumsq(3);
const Sumsq4 = Sumsq(4);
const Sumsq5 = Sumsq(5);

const Distance = operationN(DistanceN);
const Distance2 = Distance(2);
const Distance3 = Distance(3);
const Distance4 = Distance(4);
const Distance5 = Distance(5);

const Sumexp = operation(
    "sumexp", (...operands) => operands.reduce((accumulator, currentValue) => accumulator + Math.exp(currentValue), 0),
    (...operands) => operands.map(operand => new Sumexp(operand))
);

const LSE = operation("lse", (...operands) => Math.log(Sumexp.prototype.eval(...operands)));

// Override
LSE.prototype.diff = function (variable) {
    // (log(e^x1 + e^x2 + ...))' = (e^x1 + e^x2 + ...)'/(e^x1 + e^x2 + ...)
    const sumexp = new Sumexp(...this.operands);
    return new Divide(sumexp.diff(variable), sumexp);
}

function Const(value) {
    this.value = value;
}

Const.prototype.evaluate = function () {
    return this.value;
}

Const.prototype.diff = () => Const.ZERO;

Const.prototype.prefix = Const.prototype.postfix = Const.prototype.toString = function () {
    return this.value.toString();
}

Const.NEGATIVE_ONE = new Const(-1);
Const.ZERO = new Const(0);
Const.ONE = new Const(1);
Const.TWO = new Const(2);

const VARIABLES = ["x", "y", "z"];

function Variable(name) {
    this.index = VARIABLES.indexOf(name);
}

Variable.prototype.evaluate = function (...variables) {
    return variables[this.index];
}

Variable.prototype.prefix = Variable.prototype.postfix = Variable.prototype.toString = function () {
    return VARIABLES[this.index];
}

Variable.prototype.diff = function (variable) {
    return VARIABLES[this.index] === variable ? Const.ONE : Const.ZERO;
}

const OPERATIONS = new Map([
    ["+", Add], ["-", Subtract], ["*", Multiply], ["/", Divide], ["negate", Negate],
    ["distance2", Distance2], ["distance3", Distance3], ["distance4", Distance4], ["distance5", Distance5],
    ["sumsq2", Sumsq2], ["sumsq3", Sumsq3], ["sumsq4", Sumsq4], ["sumsq5", Sumsq5],
    ["lse", LSE], ["sumexp", Sumexp],
]);

function parse(expression) {
    function parseToken(token) {
        if (VARIABLES.includes(token)) {
            return new Variable(token);
        }

        const operation = OPERATIONS.get(token);
        if (operation !== undefined) {
            return new operation(...stack.splice(-operation.getArity()));
        }

        return new Const(parseFloat(token));
    }

    const tokens = expression.trim().split(/\s+/);
    const stack = [];

    tokens.forEach(token => stack.push(parseToken(token)));

    return stack.pop();
}

function ParsingError(message) {
    this.message = message;
}

ParsingError.prototype = Object.create(Error.prototype);
ParsingError.prototype.name = "ParsingError";
ParsingError.prototype.constructor = ParsingError;

class BaseParser {
    token;
    tokenPosition;
    tokenIndex;
    tokens;
    expression;

    constructor(expression) {
        if (typeof (expression) !== "string" && !(expression instanceof String)) {
            throw new ParsingError("the expression must be a string");
        }

        this.tokenIndex = 0;
        this.expression = expression;
        this.tokens = tokenize(expression);
        this.tokens.push({value: "", position: expression.length + 1});

        if (this.tokens.length === 0) {
            throw new ParsingError("the expression is empty");
        }
    }

    error(message) {
        const errorContext1 = this.expression.slice(Math.max(0, this.tokenPosition - 20), this.tokenPosition - 1);
        const errorContext2 = this.expression.slice(this.tokenPosition - 1, Math.min(this.expression.length, this.tokenPosition + 20));

        return new ParsingError(this.tokenPosition + ": " + message + " " + errorContext1 + " HERE -->" + errorContext2);
    }

    eof() {
        return this.tokenPosition > this.expression.length;
    }

    take(expected) {
        if (this.token === expected) {
            this.read();
            return true;
        } else {
            return false;
        }
    }

    parse() {
        if (this.take('(')) {
            return this.parseOperation();
        }

        if (VARIABLES.includes(this.token)) {
            return new Variable(this.read());
        }

        const numericValue = parseFloat(this.token);
        if (Number.isNaN(numericValue) || Number.isNaN(Number(this.token))) {
            throw this.error("Unexpected token: '" + this.token + "'");
        }

        this.read();
        return new Const(numericValue);
    }

    parseExpression() {
        const result = this.parse();

        if (!this.eof()) {
            throw this.error("end of expression expected");
        }

        return result;
    }
}

class PrefixParser extends BaseParser {
    constructor(expression) {
        super(expression);
        this.read();
    }

    read() {
        const result = this.token;

        this.token = this.tokens[this.tokenIndex].value;
        this.tokenPosition = this.tokens[this.tokenIndex].position;
        this.tokenIndex++;

        return result;
    }

    parseOperation() {
        const operation = OPERATIONS.get(this.token);

        if (operation === undefined) {
            throw this.error("operation expected");
        }

        const operationName = this.token;
        const operands = [];

        this.read();
        while (!this.eof() && this.token !== ')' && operands.length < operation.getArity()) {
            operands.push(this.parse());
        }

        if (operands.length < operation.getArity() && operation.getArity() !== Infinity) {
            throw this.error("Another operand for '" + operationName + "' expected (expected " + operation.getArity() + " operands, got " + operands.length + ")");
        }
        if (!this.take(')')) {
            throw this.error("')' expected");
        }

        return new operation(...operands);
    }
}

class PostfixParser extends BaseParser {
    nextToken;
    nextTokenPosition;

    constructor(expression) {
        super(expression);
        this.tokens.push({});
        this.read();
        this.read();
    }

    read() {
        const result = this.token;

        this.token = this.nextToken;
        this.tokenPosition = this.nextTokenPosition;

        this.nextToken = this.tokens[this.tokenIndex].value;
        this.nextTokenPosition = this.tokens[this.tokenIndex].position;
        this.tokenIndex++;

        return result;
    }

    parseOperation() {
        const operands = [];

        while (!this.eof() && this.nextToken !== ')') {
            operands.push(this.parse());
        }

        const operation = OPERATIONS.get(this.token);
        if (operation === undefined) {
            throw this.error("operation expected");
        }
        if (operands.length !== operation.getArity() && operation.getArity() !== Infinity) {
            throw this.error("Invalid number of operands for '" + this.token + "': expected " + operation.getArity() + ", got " + operands.length);
        }
        this.read();
        if (!this.take(')')) {
            throw this.error("')' expected");
        }

        return new operation(...operands);
    }
}

function parsePrefix(expression) {
    return new PrefixParser(expression).parseExpression();
}

function parsePostfix(expression) {
    return new PostfixParser(expression).parseExpression();
}

function tokenize(expression) {
    let ch;
    let index = 0;
    const tokens = [];

    function eof() {
        return index > expression.length;
    }

    function read() {
        const result = ch;
        if (index < expression.length) {
            ch = expression.charAt(index);
        }
        index++;
        return result;
    }

    function skipWhitespace() {
        while (!eof() && /\s/.test(ch)) {
            read();
        }
    }

    function getToken() {
        if (ch === ')' || ch === '(') {
            return read();
        }
        const token = [];
        while (!/\s/.test(ch) && !eof() && ch !== ')' && ch !== '(') {
            token.push(read());
        }
        return token.join("");
    }

    read();
    skipWhitespace();
    while (!eof()) {
        tokens.push({position: index, value: getToken()});
        skipWhitespace();
    }

    return tokens;
}
