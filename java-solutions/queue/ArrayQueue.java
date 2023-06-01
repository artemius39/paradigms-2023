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

import java.util.Objects;

public class ArrayQueue extends AbstractQueue {
    private Object[] elements;
    private int head;

    private int arrayIndex(int i) {
        return (head + i) % elements.length;
    }

    /*
     * Precondition: true
     *
     * Postcondition: n = 0
     */
    public ArrayQueue() {
        elements = new Object[2];
        head = 0;
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
        ensureCapacity(size + 1);
        elements[arrayIndex(size)] = element;
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
        return elements[head];
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
        final Object result = elements[head];
        elements[head] = null;
        head = (head + 1) % elements.length;

        return result;
    }

    /*
     * Precondition: true
     *
     * Postcondition: n = 0
     */
    @Override
    protected void clearImpl() {
        elements = new Object[2];
        head = 0;
    }

    /*
     * Precondition: capacity >= 0
     *
     * Postconditions:
     * - immutable(n)
     * - n' = n
     * - elements.length >= capacity
     * - forall i=0..n-1 elements[i] = a[i + 1]
     */
    private void ensureCapacity(int capacity) {
        if (elements.length < capacity) {
            Object[] copy = new Object[Math.max(elements.length * 2, capacity)];
            circularArraycopy(elements, head, copy, 0, elements.length);
            elements = copy;
            head = 0;
        }
    }

    /*
     * Preconditions:
     * - element != null
     *
     * Postconditions:
     * - n' = n + 1
     * - a[1] = element
     * - forall i=2..n a'[i] = a[i - 1]
     */
    public void push(Object element) {
        Objects.requireNonNull(element);

        ensureCapacity(size + 1);
        head = (head - 1 + elements.length) % elements.length;
        elements[head] = element;
        size++;
    }

    /*
     * Preconditions:
     * - n > 0
     *
     * Postconditions:
     * - n' = n
     * - immutable(n)
     * - R = a[n]
     */
    public Object peek() {
        assert size > 0;

        return elements[arrayIndex(size - 1)];
    }

    /*
     * Precondition:
     * - n > 0
     *
     * Postcondition:
     * - n' = n - 1
     * - immutable(n')
     * - R = a[n]
     */
    public Object remove() {
        assert size > 0;

        final int last = arrayIndex(size - 1);
        final Object result = elements[last];
        elements[last] = null;
        size--;

        return result;
    }

    /*
     * Precondition: true
     *
     * Postconditions:
     * - n' = n
     * - immutable(n)
     * - R = a
     */
    public Object[] toArray() {
        final Object[] array = new Object[size];
        circularArraycopy(elements, head, array, 0, size);

        return array;
    }

    /*
     * Preconditions:
     * - length >= 0
     * - src != null
     * - dest != null
     * - srcPos >= 0
     * - srcPos < src.length
     * - 0 <= destPos < dest.length
     *
     * Postconditions:
     * - dest'.length = dest.length
     * - forall i=0..length-1 dest'[(destPos + i) % dest.length] = src[(srcPos + i) % src.length]
     * - forall i=length..dest.length-1 dest'[(destPos + i) % dest.length] = dest[(destPos + i) % dest.length]
     * - src'.length = src.length
     * - forall i=0..src.length-1 src'[i] = src[i]
     */
    private static void circularArraycopy(Object[] src, int srcPos, Object[] dest, int destPos, int length) {
        if (srcPos + length < src.length) {
            simpleToCircularArraycopy(src, srcPos, dest, destPos, length);
        } else {
            final int length1 = src.length - srcPos;
            simpleToCircularArraycopy(src, srcPos, dest, destPos, length1);
            simpleToCircularArraycopy(src, 0, dest, (destPos + length1) % dest.length, length - length1);
        }
    }

    /*
     * Preconditions:
     * - length >= 0
     * - src != null
     * - dest != null
     * - srcPos >= 0
     * - srcPos < src.length
     * - srcPos + length <= src.length
     * - 0 <= destPos < dest.length
     *
     * Postconditions:
     * - dest'.length = dest.length
     * - forall i=0..length-1 dest'[(destPos + i) % dest.length] = src[srcPos + i]
     * - forall i=length..dest.length-1 dest'[(destPos + i) % dest.length] = dest[(destPos + i) % dest.length]
     * - src'.length = src.length
     * - forall i=0..src.length-1 src'[i] = src[i]
     */
    private static void simpleToCircularArraycopy(Object[] src, int srcPos, Object[] dest, int destPos, int length) {
        if (destPos + length < dest.length) {
            System.arraycopy(src, srcPos, dest, destPos, length);
        } else {
            final int length1 = dest.length - destPos;
            System.arraycopy(src, srcPos, dest, destPos, length1);
            System.arraycopy(src, srcPos + length1, dest, 0, length - length1);
        }
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
        return new ArrayQueueIterator(0);
    }

    private class ArrayQueueIterator extends AbstractQueueIterator {
        private int cursor;

        public ArrayQueueIterator(int cursor) {
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
            return elements[arrayIndex(cursor)];
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
            return cursor == size;
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
            circularArraycopy(elements, arrayIndex(cursor + 1), elements, arrayIndex(cursor), size - cursor);
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
            cursor++;
        }
    }
}
