package search;

public class BinarySearchUni {
    /*
     * Let: n = a.length
     *
     * Let: A:
     * - A[-1] = -inf
     * - A[0..n-1] = a
     * - A[n] = -inf
     *
     * Let: M = { I | forall i=0..I-1: A[i] > A[i - 1] && forall i=I..n-1: A[i] > A[i + 1] }
     * Let: I = min M
     */

    /*
     * Preconditions:
     * - a != null
     * - n > 0
     * - |M| > 0
     *
     * Postcondition:
     * - R = I
     */
    public static int iterativeSearch(int[] a) {
        // Invariant: A[l] <= A[l + 1], A[r] > A[r + 1]
        int l = -1;
        int r = a.length - 1;
        // A[-1] = -inf < A[0]
        // A[n - 1] > A[n] = -inf

        while (l + 1 != r) {
            int m = (l + r) / 2;

            if (a[m] <= a[m + 1]) {
                l = m;
                // A[l'] <= A[l' + 1]
            } else {
                r = m;
                // A[r'] > A[r' + 1]
            }
        }

        // A[l] <= A[l + 1] -> l < I
        // A[r] > A[r + 1] -> r >= I
        // r = l + 1 -> l < I <= l + 1 -> I = l + 1 = r
        return r;
    }

    /*
     * Preconditions:
     * - a != null
     * - |M| > 0
     * - n > 0
     *
     * Postcondition:
     * - R = I
     */
    public static int recursiveSearch(int[] a) {
        // a != null
        // |M| > 0
        // n > 0, l = -1 -> -1 <= l < n - 1
        // n > 0 -> 0 <= r < n
        // A[l] = A[-1] = -inf <= A[l + 1]
        // A[r + 1] = A[n] = -inf -> A[r] > A[r + 1]
        // R' = I -> R = I
        return recursiveSearch(a, -1, a.length - 1);
    }

    /*
     * Preconditions:
     * - a != null
     * - |M| > 0
     * - -1 <= l < n - 1
     * - 0 <= r < n
     * - l < r
     * - A[l] <= A[l + 1]
     * - A[r] > A[r + 1]
     *
     * Postcondition:
     * - R = I
     */
    private static int recursiveSearch(int[] a, int l, int r) {
        if (l + 1 == r) {
            // A[l] <= A[l + 1] -> l < I
            // A[r] > A[r + 1] -> r >= I
            // r = l + 1 -> l < I <= l + 1 -> I = l + 1 = r
            return r;
        }

        final int m = (l + r) / 2;
        // l < r && l + 1 != r -> l < m < r

        if (a[m] <= a[m + 1]) {
            // a' = a != null
            // M' = M -> |M'| > 0
            // l < m < r -> -1 <= l < l'
            // l' < r -> l' <= r - 1 < n - 1
            // l' < r
            // A[l'] <= A[l' + 1]
            // r' = r -> A[r'] > A[r' + 1]
            // R' = I -> R = I
            return recursiveSearch(a, m, r);
        } else {
            // a' = a != null
            // M' = M -> |M'| > 0
            // l < m < r -> r' < r < n - 1
            // r' > l -> r' >= l + 1 >= 0
            // l < r'
            // l' = l -> A[l'] <= A[l' + 1]
            // r' = m -> A[r'] > A[r' + 1]
            return recursiveSearch(a, l, m);
            // R' = I -> R = I
        }
    }

    /*
     * Preconditions:
     * - args != null
     * - args.length > 0
     * - forall arg in args Integer.parseInt(arg) does not throw an exception
     * - |M| > 0
     *
     * Postcondition:
     * - prints I to stdin
     */
    public static void main(String[] args) {
        final int[] a = new int[args.length];
        int remainder = 0;

        // Invariant: remainder = (a[0] + a[1] + ... + a[i - 1]) % 2
        for (int i = 0; i < a.length; i++) {
            a[i] = Integer.parseInt(args[i]);
            remainder = (remainder + a[i]) % 2;
            // ((a[0] + ... + a[i - 1]) % 2 + a[i]) % 2 = (a[0] + ... + a[i]) % 2
        }

        final int result;
        if (remainder == 0) {
            result = recursiveSearch(a);
        } else {
            result = iterativeSearch(a);
        }
        System.out.println(result);
    }
}