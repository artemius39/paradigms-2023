package queue;

import java.util.Objects;

/*
 * Model: a[1], a[2], a[3], ... a[n]
 * Invariant: n >= 0 && forall i=1..n: a[i] != null
 * Let: immutable(k): forall i=1..k: a[i] = a'[i]
 */

public class ArrayQueueADT {
    private Object[] elements;
    private int head;
    private int tail;
    private int size;

    public ArrayQueueADT() {
        this.elements = new Object[2];
        this.head = 0;
        this.tail = 0;
        this.size = 0;
    }

    /*
     * Precondition: true
     *
     * Postconditions:
     * - n = 0
     */
    public static ArrayQueueADT create() {
        ArrayQueueADT queue = new ArrayQueueADT();
        queue.elements = new Object[2];
        queue.head = 0;
        queue.tail = 0;
        queue.size = 0;

        return queue;
    }

    /*
     * Precondition:
     * - element != null
     * - queue != null
     *
     * Postconditions:
     * - n' = n + 1
     * - a'[n'] = element
     * - immutable(n)
     */
    public static void enqueue(ArrayQueueADT queue, Object element) {
        Objects.requireNonNull(element);
        Objects.requireNonNull(queue);

        ensureCapacity(queue, queue.size + 1);
        queue.elements[queue.tail] = element;
        queue.tail = (queue.tail + 1) % queue.elements.length;
        queue.size++;
    }

/*
     Preconditions:
     - capacity >= 0
     - queue != null

     Postconditions:
     - immutable(n)
     - n' = n
     - elements.length >= capacity
     - forall i=0..n-1 elements[i] = a[i + 1]
*/
    private static void ensureCapacity(ArrayQueueADT queue, int capacity) {
        Objects.requireNonNull(queue);

        if (queue.elements.length < capacity) {
            Object[] copy = new Object[Math.max(queue.elements.length * 2, capacity)];

            for (int i = 0; i < queue.elements.length; i++) {
                copy[i] = queue.elements[(queue.head + i) % queue.elements.length];
            }

            queue.elements = copy;
            queue.head = 0;
            queue.tail = queue.size;
        }
    }

    /*
     * Preconditions:
     * - n > 0
     * - queue != null
     *
     * Postcondition:
     * - R = a[n]
     * - n' = n
     * - immutable(n)
     */
    public static Object element(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);

        assert queue.size > 0;

        return queue.elements[queue.head];
    }

    /*
     * Preconditions:
     * - n > 0
     * - queue != null
     *
     * Postconditions:
     * - n' = n - 1
     * - forall i=1..n' a'[i] = a[i + 1]
     * - R = a[n]
     */
    public static Object dequeue(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);

        assert queue.size > 0;

        final Object result = queue.elements[queue.head];

        queue.elements[queue.head] = null;
        queue.head = (queue.head + 1) % queue.elements.length;
        queue.size--;

        return result;
    }

    /*
     * Precondition:
     * - true
     * - queue != null
     *
     * Postconditions:
     * - R = n
     * - immutable(n)
     * - n' = n
     */
    public static int size(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);

        return queue.size;
    }

    /*
     * Preconditions:
     * - true
     * - queue != null
     *
     * Postcondition:
     * - R = true if n = 0, R = false otherwise
     * - immutable(n)
     * - n' = n
     */
    public static boolean isEmpty(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);

        return queue.size == 0;
    }

    /*
     * Preconditions:
     * - true
     * - queue != null
     *
     * Postcondition: n = 0
     */
    public static void clear(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);

        queue.elements = new Object[2];
        queue.size = 0;
        queue.head = 0;
        queue.tail = 0;
    }

    /*
     * Precondition:
     * - queue != null
     *
     * Postconditions:
     * - n' = n
     * - immutable(n)
     * - R = a
     */
    public static Object[] toArray(ArrayQueueADT queue) {
        final Object[] array = new Object[queue.size];

        for (int i = 0; i < queue.size; i++) {
            array[i] = queue.elements[(queue.head + i) % queue.elements.length];
        }

        return array;
    }

    /*
     * Precondition:
     * - deque != null
     * - n > 0
     *
     * Postcondition:
     * - n' = n - 1
     * - immutable(n')
     * - R = a[n]
     */
    public static Object remove(ArrayQueueADT deque) {
        assert deque.size > 0;

        deque.tail = (deque.tail - 1 + deque.elements.length) % deque.elements.length;
        final Object result = deque.elements[deque.tail];
        deque.elements[deque.tail] = null;
        deque.size--;

        return result;
    }

    /*
     * Preconditions:
     * - deque != null
     * - element != null
     *
     * Postconditions:
     * - n' = n + 1
     * - a[1] = element
     * - forall i=2..n a'[i] = a[i - 1]
     */
    public static void push(ArrayQueueADT deque, Object element) {
        Objects.requireNonNull(element);

        ensureCapacity(deque, deque.size + 1);
        deque.head = (deque.head - 1 + deque.elements.length) % deque.elements.length;
        deque.elements[deque.head] = element;
        deque.size++;
    }

    /*
     * Preconditions:
     * - deque != null
     * - n > 0
     *
     * Postconditions:
     * - n' = n
     * - immutable(n)
     * - R = a[n]
     */
    public static Object peek(ArrayQueueADT deque) {
        assert deque.size > 0;

        return deque.elements[(deque.tail - 1 + deque.elements.length) % deque.elements.length];
    }
}
