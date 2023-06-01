package queue;

/*
 * Model: a[1], a[2], a[3], ... a[n]
 *
 * Invariant: n >= 0 && forall i=1..n: a[i] != null
 *
 * Let: immutable(k): forall i=1..k: a[i] = a'[i]
 * Let: totally_immutable: n' = n && immutable(n)
 * Let: occurrences(element) = { i | a[i] = element }
 * Let: remove(i): n' = n - 1 && immutable(i - 1) && forall j=i..n' a'[j] = a[j + 1]
 */

public class LinkedQueue extends AbstractQueue {

    private static class Node {
        private Node next;
        private Object value;
    }

    private Node head;
    private Node tail;

    /*
     * Precondition: true
     *
     * Postcondition: n = 0
     */
    public LinkedQueue() {
        head = new Node();
        head.next = tail = new Node();
    }

    /*
     * Precondition:
     * - element != null
     *
     * Postcondition:
     * - a[n + 1] = element
     * - immutable(n)
     * - n' = n
     */
    @Override
    protected void enqueueImpl(Object element) {
        tail.value = element;
        tail = tail.next = new Node();
    }

    /*
     * Precondition:
     * - n > 0
     *
     * Postcondition:
     * - R = a[n]
     * - totally_immutable
     */
    @Override
    protected Object elementImpl() {
        return head.next.value;
    }

    /*
     * Preconditions:
     * - n > 0
     *
     * Postconditions:
     * - R = a[n]
     * - remove(1)
     */
    @Override
    protected Object dequeueImpl() {
        final Object result = head.next.value;
        head = head.next;

        return result;
    }

    /*
     * Precondition: true
     *
     * Postcondition: n = 0
     */
    @Override
    protected void clearImpl() {
        head.next = tail;
    }

    /*
     * Precondition: n > 0
     *
     * Postconditions:
     * - R.cursor = 1
     * - totally_immutable
     */
    @Override
    protected AbstractQueueIterator begin() {
        return new LinkedQueueIterator(head);
    }

    private class LinkedQueueIterator extends AbstractQueueIterator {
        // Model: cursor
        // Invariant: cursor == null || 1 <= cursor <= n + 1

        private Node cursor;

        public LinkedQueueIterator(Node cursor) {
            this.cursor = cursor;
        }

        /*
         * Precondition:
         * - cursor != null
         * - cursor != n + 1
         *
         * Postcondition:
         * - R = a[cursor]
         * - cursor' = cursor
         * - totally_immutable
         */
        @Override
        public Object get() {
            return cursor.next.value;
        }

        /*
         * Precondition:
         * - cursor != null
         *
         * Postcondition:
         * - R = true, if cursor = n + 1, false otherwise
         * - cursor' = cursor
         * - totally_immutable
         */
        @Override
        public boolean isEnd() {
            return cursor.next == tail;
        }

        /*
         * Preconditions:
         * - cursor != null
         * - cursor != n + 1
         *
         * Postconditions:
         * - cursor' = cursor
         * - remove(cursor)
         */
        @Override
        public void removeImpl() {
            cursor.next = cursor.next.next;
        }

        /*
         * Precondition:
         * - cursor != null
         * - cursor != n + 1
         *
         * Postcondition:
         * - cursor' = cursor + 1
         */
        @Override
        public void next() {
            cursor = cursor.next;
        }
    }
}
