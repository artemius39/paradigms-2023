to_lowercase('X', x).
to_lowercase('x', x).
to_lowercase('Y', y).
to_lowercase('y', y).
to_lowercase('Z', z).
to_lowercase('z', z).

evaluate(const(Value), _, Value).
evaluate(variable(FullName), [(Name, Value) | _], Value) :- 
	atom_chars(FullName, [FirstCh | _]), 
	to_lowercase(FirstCh, Name), !.
evaluate(variable(Name), [H | T], Res) :- evaluate(variable(Name), T, Res).

evaluate(operation(Op, A), Vars, Res) :- 
	evaluate(A, Vars, AR), 
	F =.. [Op, AR, Res],
	call(F).
	
evaluate(operation(Op, A, B), Vars, Res) :- 
	evaluate(A, Vars, ARes),
	evaluate(B, Vars, BRes),
	F =.. [Op, ARes, BRes, Res], 
	call(F).

op_negate(A, Res) :- Res is -A.
op_add(A, B, Res) :- Res is A + B.
op_subtract(A, B, Res) :- Res is A - B.
op_multiply(A, B, Res) :- Res is A * B.
op_divide(A, B, Res) :- Res is A / B.

op_not(A, 1.0) :- A =< 0.
op_not(A, 0.0) :- A > 0.
op_and(A, B, 1.0) :- A > 0, B > 0, !.
op_and(A, B, 0.0) :- \+ op_and(A, B, 1.0).
op_or(A, B, 0.0) :- A =< 0, B =< 0.
op_or(A, B, 1.0) :- \+ op_or(A, B, 0.0).
op_xor(A, B, 1.0) :- A > 0, B =< 0; A =< 0, B > 0.
op_xor(A, B, 0.0) :- \+ op_xor(A, B, 1.0).

:- load_library('alice.tuprolog.lib.DCGLibrary').

nonvar(V, _) :- var(V).
nonvar(V, T) :- nonvar(V), call(T).

digit(H) :- member(H, ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9']).

expr_p(variable(Name)) -->
	{ nonvar(Name, atom_chars(Name, Chars)) },
  var_p(Chars),
  { Chars = [_ | _], atom_chars(Name, Chars) }.

var_p([]) --> [].
var_p([H | T]) --> [H], var_p(T), { member(H, ['x', 'y', 'z', 'X', 'Y', 'Z']) }.

expr_p(const(Value)) -->
  { nonvar(Value, number_chars(Value, Chars)) },
  number_p(Chars),
  { number_chars(Value, Chars) }.

number_p([H | T]) --> 
	{ member(H, ['+', '-']) },
	[H], magnitude_p(T),
  { T = [_ | _] }.
number_p(T) --> 
	magnitude_p(T),
  { T = [_ | _] }.

magnitude_p([]) --> [].
magnitude_p([H | T]) -->
	{ digit(H) },
	[H], magnitude_p(T).
magnitude_p(['.' | T]) -->
	['.'], digits_p(T),
  { T = [_ | _] }.

digits_p([]) --> [].
digits_p([H | T]) -->
  { digit(H) },
  [H],
  digits_p(T).	

ws --> [].
ws --> [' '], ws.

expr_ws_p(Expr) --> ws, expr_p(Expr), ws.

expr_p(operation(Op, A)) --> op_p(Op), [' '], expr_ws_p(A).
expr_p(operation(Op, A, B)) --> ['('], expr_ws_p(A), [' '], op_p(Op), [' '], expr_ws_p(B), [')'].

op_p(op_negate) --> 
  { atom_chars(negate, Chars) }, 
  Chars.
op_p(op_add) --> ['+'].
op_p(op_subtract) --> ['-'].
op_p(op_multiply) --> ['*'].
op_p(op_divide) --> ['/'].
op_p(op_not) --> ['!'].
op_p(op_and) --> ['&', '&'].
op_p(op_or) --> ['|', '|'].
op_p(op_xor) --> ['^', '^'].

infix_str(Expr, String) :- 
	atom(String),
	atom_chars(String, Chars),
	phrase(expr_ws_p(Expr), Chars), !.

infix_str(Expr, String) :- 
	ground(Expr),
	phrase(expr_p(Expr), Chars),
	atom_chars(String, Chars), !.

