map_get(root(Tree), K, V) :- map_get(Tree, K, V).

map_get(leaf(K, V), K, V).

map_get(node(MaxKL, L, R), K, V) :- 
    K =< MaxKL, 
    map_get(L, K, V).
map_get(node(MaxKL, L, R), K, V) :- 
    K > MaxKL,
    map_get(R, K, V).

map_get(node(MaxKL, MaxKM, L, M, R), K, V) :- 
    K =< MaxKL, 
    map_get(L, K, V).
map_get(node(MaxKL, MaxKM, L, M, R), K, V) :- 
    MaxKL < K, K =< MaxKM, 
    map_get(M, K, V).
map_get(node(MaxKL, MaxKM, L, M, R), K, V) :- 
    K > MaxKM, 
    map_get(R, K, V).

map_build([], root(empty)).
map_build([(K, V) | T], TreeMap) :- map_build(T, TreeMap1), map_put(TreeMap1, K, V, TreeMap).

map_op(root(Tree), K, Op, Args, Res) :- 
    map_op(Tree, K, Op, Args, NewTree),
    fix(root(NewTree), Res).

map_op(node(MaxKL, L, R), K, Op, Args, Res) :-
    K =< MaxKL,
    map_op(L, K, Op, Args, NewL),
    fix(node(MaxKL, NewL, R), Res).
map_op(node(MaxKL, L, R), K, Op, Args, Res) :-
    K > MaxKL,
    map_op(R, K, Op, Args, NewR),
    fix(node(MaxKL, L, NewR), Res).

map_op(node(MaxKL, MaxKM, L, M, R), K, Op, Args, Res) :-
    K =< MaxKL,
    map_op(L, K, Op, Args, NewL),
    fix(node(MaxKL, MaxKM, NewL, M, R), Res).
map_op(node(MaxKL, MaxKM, L, M, R), K, Op, Args, Res) :-
    MaxKL < K, K =< MaxKM,
    map_op(M, K, Op, Args, NewM),
    fix(node(MaxKL, MaxKM, L, NewM, R), Res).
map_op(node(MaxKL, MaxKM, L, M, R), K, Op, Args, Res) :-
    K > MaxKM,
    map_op(R, K, Op, Args, NewR),
    fix(node(MaxKL, MaxKM, L, M, NewR), Res).

map_op(Tree, K, Op, Args, Res) :-
    (Tree = leaf(_, _); Tree = empty),
    F =..[Op, Tree, Res, K | Args],
    call(F).

map_put(Tree, K, V, Res) :- map_op(Tree, K, map_put_impl, [V], Res).

map_put_impl(empty, leaf(K, V), K, V).
map_put_impl(leaf(K, _), leaf(K, V), K, V).
map_put_impl(leaf(K1, V1), nodes(K1, leaf(K1, V1), leaf(K, V)), K, V) :- K > K1.
map_put_impl(leaf(K1, V1), nodes(K, leaf(K, V), leaf(K1, V1)), K, V) :- K < K1.

map_putIfAbsent(Tree, K, V, Res) :- map_op(Tree, K, map_putIfAbsentImpl, [V], Res).

map_putIfAbsentImpl(empty, leaf(K, V), K, V).
map_putIfAbsentImpl(leaf(K, V), leaf(K, V), K, V1).
map_putIfAbsentImpl(leaf(K1, V1), nodes(K1, leaf(K1, V1), leaf(K, V)), K, V) :- K > K1.
map_putIfAbsentImpl(leaf(K1, V1), nodes(K, leaf(K, V), leaf(K1, V1)), K, V) :- K < K1.

fix(root(nodes(MaxKL, L, R)), 
    root(node(MaxKL, L, R))) :- !.

fix(node(MaxKL, nodes(MaxKL1, L1, R1), R), 
    node(MaxKL1, MaxKL, L1, R1, R)) :- !.
fix(node(MaxKL, L, nodes(MaxKL1, L1, R1)), 
    node(MaxKL, MaxKL1, L, L1, R1)) :- !.

fix(node(MaxKL, MaxKM, nodes(MaxKL1, L1, R1), M, R), 
    nodes(MaxKL, node(MaxKL1, L1, R1), node(MaxKM, M, R))) :- !.
fix(node(MaxKL, MaxKM, L, nodes(MaxKL1, L1, R1), R), 
    nodes(MaxKL1, node(MaxKL, L, L1), node(MaxKM, R1, R))) :- !.
fix(node(MaxKL, MaxKM, L, M, nodes(MaxKL1, L1, R1)), 
    nodes(MaxKM, node(MaxKL, L, M), node(MaxKL1, L1, R1))) :- !.

map_remove(Tree, K, Res) :- map_op(Tree, K, map_remove_impl, [], Res).

map_remove_impl(empty, empty, _).
map_remove_impl(leaf(K, _), empty, K).
map_remove_impl(leaf(K1, V1), leaf(K1, V1), K) :- K \= K1.

fix(root(single(Tree)), root(Tree)) :- !.
fix(root(single(_, Tree)), root(Tree)) :- !.
fix(root(update_max(_, Tree)), root(Tree)) :- !.

fix(node(_, empty, R), 
    single(R)) :- !.
fix(node(MaxKL, L, empty), 
    single(MaxKL, L)) :- !.
fix(node(_, MaxKM, empty, M, R), 
    node(MaxKM, M, R)) :- !.
fix(node(MaxKL, _, L, empty, R), 
    node(MaxKL, L, R)) :- !.
fix(node(MaxKL, MaxKM, L, M, empty),
    update_max(MaxKM, node(MaxKL, L, M))) :- !.

fix(node(MaxKL, single(L), node(MaxKL1, L1, R1)), 
    single(node(MaxKL, MaxKL1, L, L1, R1))) :- !.
fix(node(_, single(NewMaxKL, L), node(MaxKL1, L1, R1)), 
    single(node(NewMaxKL, MaxKL1, L, L1, R1))) :- !.
fix(node(MaxKL, single(L), node(MaxKL1, MaxKM1, L1, M1, R1)), 
    node(MaxKL1, node(MaxKL, L, L1), node(MaxKM1, M1, R1))) :- !.
fix(node(_, single(NewMaxKL, L), node(MaxKL1, MaxKM1, L1, M1, R1)), 
    node(MaxKL1, node(NewMaxKL, L, L1), node(MaxKM1, M1, R1))) :- !.

fix(node(MaxKL, node(MaxKL1, L1, R1), single(R)), 
    single(node(MaxKL1, MaxKL, L1, R1, R))) :- !.
fix(node(MaxKL, node(MaxKL1, L1, R1), single(NewMaxK, R)), 
    single(NewMaxK, node(MaxKL1, MaxKL, L1, R1, R))) :- !.
fix(node(MaxKL, node(MaxKL1, MaxKM1, L1, M1, R1), single(R)), 
    node(MaxKM1, node(MaxKL1, L1, M1), node(MaxKL, R1, R))) :- !.
fix(node(MaxKL, node(MaxKL1, MaxKM1, L1, M1, R1), single(NewMaxK, R)), 
    update_max(NewMaxK, node(MaxKM1, node(MaxKL1, L1, M1), node(MaxKL, R1, R)))) :- !.

fix(node(MaxKL, MaxKM, single(L), node(MaxKL1, L1, R1), R), 
    node(MaxKM, node(MaxKL, MaxKL1, L, L1, R1), R)) :- !.
fix(node(_, MaxKM, single(NewMaxKL, L), node(MaxKL1, L1, R1), R), 
    node(MaxKM, node(NewMaxKL, MaxKL1, L, L1, R1), R)) :- !.
fix(node(MaxKL, MaxKM, single(L), node(MaxKL1, MaxKM1, L1, M1, R1), R), 
    node(MaxKL1, MaxKM, node(MaxKL, L, L1), node(MaxKM1, M1, R1), R)) :- !.
fix(node(_, MaxKM, single(NewMaxKL, L), node(MaxKL1, MaxKM1, L1, M1, R1), R), 
    node(MaxKL1, MaxKM, node(NewMaxKL, L, L1), node(MaxKM1, M1, R1), R)) :- !.

fix(node(MaxKL, MaxKM, node(MaxKL1, L1, R1), single(M), R), 
    node(MaxKM, node(MaxKL1, MaxKL, L1, R1, M), R)) :- !.
fix(node(MaxKL, _, node(MaxKL1, L1, R1), single(NewMaxKM, M), R), 
    node(NewMaxKM, node(MaxKL1, MaxKL, L1, R1, M), R)) :- !.
fix(node(MaxKL, MaxKM, node(MaxKL1, MaxKM1, L1, M1, R1), single(M), R), 
    node(MaxKM1, MaxKM, node(MaxKL1, L1, M1), node(MaxKL, R1, M), R)) :- !.
fix(node(MaxKL, _, node(MaxKL1, MaxKM1, L1, M1, R1), single(NewMaxKM, M), R), 
    node(MaxKM1, NewMaxKM, node(MaxKM1, L1, M1), node(MaxKM, R1, M), R)) :- !.

fix(node(MaxKL, MaxKM, L, node(MaxKL1, L1, R1), single(R)), 
    node(MaxKL, L, node(MaxKL1, MaxKM, L1, R1, R))) :- !.
fix(node(MaxKL, MaxKM, L, node(MaxKL1, L1, R1), single(NewMaxK, R)), 
    update_max(NewMaxK, node(MaxKL, L, node(MaxKL1, L1, R1, R)))) :- !.
fix(node(MaxKL, MaxKM, L, node(MaxKL1, MaxKM1, L1, M1, R1), single(R)), 
    node(MaxKL, MaxKM1, L, node(MaxKL1, L1, M1), node(MaxKM, R1, R))) :- !.
fix(node(MaxKL, MaxKM, L, node(MaxKL1, MaxKM1, L1, M1, R1), single(NewMaxK, R)), 
    update_max(NewMaxK, node(MaxKL, MaxKM1, L, node(MaxKL1, L1, M1), node(MaxKM, R1, R)))) :- !.

fix(node(_, update_max(NewMaxKL, L), R), 
    node(NewMaxKL, L, R)) :- !.
fix(node(MaxKL, L, update_max(NewMaxK, R)), 
    update_max(NewMaxK, node(MaxKL, L, R))) :- !.
fix(node(_, MaxKM, update_max(NewMaxKL, L), M, R), 
    node(NewMaxKL, MaxKM, L, M, R)) :- !.
fix(node(MaxKL, _, L, update_max(NewMaxKM, M), R), 
    node(MaxKL, NewMaxKM, L, M, R)) :- !.
fix(node(MaxKL, MaxKM, L, M, update_max(NewMaxK, R)), 
    update_max(NewMaxK, node(MaxKL, MaxKM, L, M, R))) :- !.

fix(Tree, Tree).
