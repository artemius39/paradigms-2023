package queue;

import java.util.Objects;

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

public abstract class AbstractQueue implements Queue {
    protected int size;

    /*
     * Precondition: true
     *
     * Postcondition: n = 0
     */
    protected AbstractQueue() {
        size = 0;
    }

    /*
     * Precondition:
     * - element != null
     *
     * Postconditions:
     * - n' = n + 1
     * - a'[n'] = element
     * - immutable(n)
     */
    @Override
    public void enqueue(Object element) {
        Objects.requireNonNull(element);

        enqueueImpl(element);
        size++;
    }

    /*
     * Precondition:
     * - element != null
     *
     * Postcondition:
     * - a'[n + 1] = element
     * - immutable(n)
     * - n' = n + 1
     */
    protected abstract void enqueueImpl(Object element);

    /*
     * Precondition:
     * - n > 0
     *
     * Postcondition:
     * - R = a[n]
     * - totally_immutable
     */
    @Override
    public Object element() {
        assert size > 0;

        return elementImpl();
    }

    /*
     * Precondition:
     * - n > 0
     *
     * Postcondition:
     * - R = a[n]
     * - totally_immutable
     */
    protected abstract Object elementImpl();

    /*
     * Precondition:
     * - n > 0
     *
     * Postconditions:
     * - R = a[n]
     * - remove(1)
     */
    @Override
    public Object dequeue() {
        assert size > 0;

        final Object result = dequeueImpl();
        size--;

        return result;
    }

    /*
     * Preconditions:
     * - n > 0
     *
     * Postconditions:
     * - R = a[n]
     * - remove(1)
     */
    protected abstract Object dequeueImpl();

    /*
     * Precondition:
     * - true
     *
     * Postconditions:
     * - R = n
     * - totally_immutable
     */
    @Override
    public int size() {
        return size;
    }

    /*
     * Precondition:
     * - true
     *
     * Postcondition:
     * - R = true if n = 0, R = false otherwise
     * - totally_immutable
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /*
     * Precondition: true
     *
     * Postcondition: n = 0
     */
    @Override
    public void clear() {
        clearImpl();

        size = 0;
    }

    /*
     * Precondition: true
     *
     * Postcondition: n = 0
     */
    protected abstract void clearImpl();

    protected abstract class AbstractQueueIterator {
        // Model: cursor
        // Invariant: cursor == null || 1 <= cursor <= n + 1

        /*
         * Precondition:
         * - cursor != null
         *
         * Postcondition:
         * - R = true, if cursor = n + 1, false otherwise
         * - cursor' = cursor
         * - totally_immutable
         */
        protected abstract boolean isEnd();

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
        protected abstract Object get();

        /*
         * Precondition:
         * - cursor != null
         * - cursor != n + 1
         *
         * Postcondition:
         * - cursor' = cursor + 1
         */
        protected abstract void next();

        /*
         * Preconditions:
         * - cursor != null
         * - cursor != n + 1
         *
         * Postconditions:
         * - cursor' = cursor
         * - remove(cursor)
         */
        public void remove() {
            removeImpl();
            size--;
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
        protected abstract void removeImpl();
    }

    /*
     * Precondition: n > 0
     *
     * Postconditions:
     * - R.cursor = 1
     * - totally_immutable
     */
    protected abstract AbstractQueueIterator begin();

    /*
     * Preconditions:
     * - true
     *
     * Postconditions:
     * - if M = occurrences(element) is not empty, R.cursor = min M, else R.cursor = n + 1
     * - totally_immutable
     */
    private AbstractQueueIterator find(Object element) {
        AbstractQueueIterator iterator = begin();

        while (!iterator.isEnd() && !iterator.get().equals(element)) {
            iterator.next();
        }

        return iterator;
    }

    /*
     * Preconditions:
     * - true
     *
     * Postconditions:
     * - R = true if occurrences(element) is not empty, false otherwise
     * - totally_immutable
     */
    public boolean contains(Object element) {
        return !find(element).isEnd();
    }

    /*
     * Preconditions:
     * - true
     *
     * Postconditions:
     *
     * if M = occurrences(element) is not empty:
     * - R = true
     * - remove(min M)
     *
     * else:
     * - R = false
     * - totally_immutable
     */
    public boolean removeFirstOccurrence(Object element) {
        AbstractQueueIterator iterator = find(element);

        if (!iterator.isEnd()) {
            iterator.remove();
            return true;
        } else {
            return false;
        }
    }
}
