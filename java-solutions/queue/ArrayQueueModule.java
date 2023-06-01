package queue;

import java.util.Objects;

public class ArrayQueueModule {

    /*
     * Model: a[1], a[2], a[3], ... a[n]
     * Invariant: n >= 0 && forall i=1..n: a[i] != null
     * Let: immutable(k): forall i=1..k: a[i] = a'[i]
     */

    private static Object[] elements = new Object[2];
    private static int head = 0;
    private static int tail = 0;
    private static int size = 0;

    /*
     * Precondition:
     * - element != null
     *
     * Postconditions:
     * - n' = n + 1
     * - a'[n'] = element
     * - immutable(n)
     */
    public static void enqueue(Object element) {
        Objects.requireNonNull(element);

        ensureCapacity(size + 1);
        elements[tail] = element;
        tail = (tail + 1) % elements.length;
        size++;
    }

/*
     Precondition: capacity >= 0

     Postconditions:
     - immutable(n)
     - n' = n
     - elements.length >= capacity
     - forall i=0..n-1 elements[i] = a[i + 1]
*/
    private static void ensureCapacity(int capacity) {
        if (elements.length < capacity) {
            Object[] copy = new Object[Math.max(elements.length * 2, capacity)];

            for (int i = 0; i < elements.length; i++) {
                copy[i] = elements[(head + i) % elements.length];
            }

            elements = copy;
            head = 0;
            tail = size;
        }
    }

    /*
     * Precondition: n > 0
     *
     * Postconditions:
     * - R = a[n]
     * - n' = n
     * - immutable(n)
     */
    public static Object element() {
        assert size > 0;

        return elements[head];
    }

    /*
     * Precondition:
     * - n > 0
     *
     * Postconditions:
     * - n' = n - 1
     * - forall i=1..n' a'[i] = a[i + 1]
     * - R = a[n]
     */
    public static Object dequeue() {
        assert size > 0;

        final Object result = elements[head];

        elements[head] = null;
        head = (head + 1) % elements.length;
        size--;

        return result;
    }

    /*
     * Precondition:
     * - true
     *
     * Postconditions:
     * - R = n
     * - immutable(n)
     * - n' = n
     */
    public static int size() {
        return size;
    }

    /*
     * Precondition:
     * - true
     *
     * Postcondition:
     * - R = true if n = 0, R = false otherwise
     * - immutable(n)
     * - n' = n
     */
    public static boolean isEmpty() {
        return size == 0;
    }

    /*
     * Precondition: true
     *
     * Postcondition: n = 0
     */
    public static void clear() {
        elements = new Object[2];
        size = 0;
        head = 0;
        tail = 0;
    }

    /*
     * Precondition: true
     *
     * Postconditions:
     * - n' = n
     * - immutable(n)
     * - R = a
     */
    public static Object[] toArray() {
        final Object[] array = new Object[size];

        for (int i = 0; i < size; i++) {
            array[i] = elements[(head + i) % elements.length];
        }

        return array;
    }

    /*
     * - n > 0
     * Precondition:
     *
     * Postcondition:
     * - n' = n - 1
     * - immutable(n')
     * - R = a[n]
     */
    public static Object remove() {
        assert size > 0;

        tail = (tail - 1 + elements.length) % elements.length;
        final Object result = elements[tail];
        elements[tail] = null;
        size--;

        return result;
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
    public static void push(Object element) {
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
    public static Object peek() {
        assert size > 0;

        return elements[(tail - 1 + elements.length) % elements.length];
    }
}
