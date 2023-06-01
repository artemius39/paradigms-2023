package search;

public class BinarySearch {
    /*
     * Let: n = a.length
     *
     * Let: A:
     *  - A[-1] = +inf
     *  - A[0..n-1] = a
     *  - A[n] = -inf
     *
     * Let: sorted(a): forall i < j: a[i] >= a[j]
     *
     * Let: M = { i | A[i] <= x }
     */

    /*
     * Preconditions:
     * - a != null
     * - sorted(a)
     *
     * Postcondition:
     * - R = min M
     */
    public static int iterativeSearch(int x, int[] a) {
        // Invariant: A[l] > x && A[r] <= x
        int l = -1;
        int r = a.length;
        // A[l] = A[-1] = +inf > x
        // A[r] = A[n] = -inf <= x

        while (l + 1 != r) {
            int m = (l + r) / 2;

            if (a[m] > x) {
                l = m;
                // A[m] > x -> A[l'] > x
            } else {
                r = m;
                // A[m] <= x -> A[r'] <= x
            }
        }

        // A[l] > x, sorted(a) -> forall i <= l: A[i] >= A[l] > x -> forall i <= l: i not in M ->
        // forall i in M: i > l -> forall i in M: i >= l + 1 = r
        // r in M, forall i in M: r <= i -> r = min M
        return r;
    }

    /*
     * Preconditions:
     * - a != null
     * - sorted(a)
     *
     * Postconditions:
     * - R = min M
     */
    public static int recursiveSearch(int x, int[] a) {
        // R' = min M -> R = min M
        return recursiveSearch(x, a, -1, a.length);
    }

    /*
     * Preconditions:
     * - a != null
     * - sorted(a)
     * - -1 <= l < n
     * - 0 <= r <= n
     * - A[l] > x
     * - A[r] <= x
     *
     * Postconditions:
     * - R = min M
     */
    private static int recursiveSearch(int x, int[] a, int l, int r) {
        if (l + 1 == r) {
            // A[l] > x, sorted(a) -> forall i < l: A[i] >= A[l] > x -> forall i <= l: i not in M -> forall i in M: i > l ->
            // forall i in M: i >= l + 1 == r
            // r in M, forall i in M: r <= i -> r = min M
            return r;
        }

        int m = (l + r) / 2;

        if (a[m] > x) {
            // a' = a -> a' != null, sorted(a')
            // m = (l + r) / 2 -> l <= m <= r -> -1 <= l <= m -> l' >= -1
            // r' = r -> 0 <= r' <= n
            // A[m] > x -> A[l'] > x
            // A[r'] = A[r] <= x
            // -> R' = min M -> R = min M
            return recursiveSearch(x, a, m, r);
        } else {
            // a' = a -> a' != null, sorted(a')
            // m = (l + r) / 2 -> l <= m <= r -> m <= r <= n -> r' >= -1
            // l' = l -> -1 <= l' < n
            // A[m] <= x -> A[r'] <= x
            // A[l'] = A[l] > x
            // -> R' = min M -> R = min M
            return recursiveSearch(x, a, l, m);
        }
    }

    /*
     * Let: x = Integer.parseInt(args[0])
     * Let: a = Integer.parseInt(args[1..args.length-1]
     *
     * Preconditions:
     * - args != null
     * - args.length > 0
     * - forall arg in args: arg != null && Integer.parseInt(arg) does not throw an exception
     * - sorted(a)
     *
     * Postconditions:
     * - prints min M to stdout
     */
    public static void main(String[] args) {
        final int x = Integer.parseInt(args[0]);
        final int[] a = new int[args.length - 1];
        int remainder = 0;

        // Invariant: remainder = (a[0] + a[1] + ... + a[i - 1]) % 2
        for (int i = 0; i < a.length; i++) {
            a[i] = Integer.parseInt(args[i + 1]);
            remainder = (remainder + a[i] % 2) % 2;
            // ((a[0] + ... + a[i - 1]) % 2 + a[i]) % 2 = (a[0] + ... + a[i]) % 2
        }

        final int result;
        if (remainder == 0) {
            result = recursiveSearch(x, a);
            // a != null && sorted(a) -> result = min M
        } else {
            result = iterativeSearch(x, a);
            // a != null && sorted(a) -> result = min M
        }
        System.out.println(result);
        // result = min M
    }
}