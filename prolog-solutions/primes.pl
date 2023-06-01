prime(N) :- \+ composite(N), N > 1.

sieve(N, MAX_N) :- N * N > MAX_N, !.
sieve(N, MAX_N) :-
	update(N, MAX_N),
	M is N + 1,
	sieve(M, MAX_N).

update(N, _) :- composite(N), !.
update(N, MAX_N) :- 
	SQ is N * N,
	mark(SQ, N, MAX_N).

mark(X, _, MAX_N) :- X > MAX_N, !.
mark(X, N, MAX_N) :-
	mark_as_composite(X, N),
	Y is X + N,
	mark(Y, N, MAX_N).

mark_as_composite(N, _) :- composite(N), !.
mark_as_composite(N, D) :- assertz(composite(N)), assertz(min_divisor(N, D)).

min_divisor(N, N) :- prime(N).

init(MAX_N) :- sieve(2, MAX_N).

prime_divisors(1, []).
prime_divisors(N, [N]) :- prime(N), !.

% find prime divisors by number
prime_divisors(N, [H | T]) :- 
	number(N),
	min_divisor(N, H),
	M is div(N, H),
	prime_divisors(M, T).

% find number by its prime divisors
prime_divisors(N, [H, H1 | T]) :-
	number(H), number(H1),
	prime(H),
	H =< H1,
	prime_divisors(M, [H1 | T]),
	N is M * H.

% find greatest P such that D^P divides N and M = N / D^P
max_power(N, D, 0, N) :- mod(N, D) =\= 0.
max_power(N, D, P, M) :- 
	number(N), number(D),
	mod(N, D) =:= 0,
	K is div(N, D), 
	max_power(K, D, Q, M), 
	P is Q + 1.

% find N such that N = D^P * M
mul_power(N, _, 0, N).
mul_power(N, D, P, M) :-
	number(D), number(P), number(M),
	P > 0,
	Q is P - 1,
	mul_power(K, D, Q, M),
	N is K * D.

compact_prime_divisors(1, []).

compact_prime_divisors(N, [(D, K) | T]) :-
	number(N),
	min_divisor(N, D),
	max_power(N, D, K, M),
	compact_prime_divisors(M, T).

compact_prime_divisors(N, [(D, K) | T]) :-
	number(D), number(K),
	compact_prime_divisors(M, T),
	(M = 1; min_divisor(M, D1),	D < D1),
	mul_power(N, D, K, M).
	
