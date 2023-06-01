package queue;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;


public class ArrayQueueTest1 {
    private static void testInitialization() {
        ArrayQueue queue = new ArrayQueue();
        if (!queue.isEmpty()) {
            System.out.println("new queue must be empty");
        }
    }

    private static void testSize() {
        ArrayQueue queue = new ArrayQueue();

        int expectedSize = 10;
        for (int i = 1; i <= expectedSize; i++) {
            queue.enqueue(i);
        }

        if (queue.size() != expectedSize) {
            System.out.println("Added " + expectedSize + " elements, but size() = " + queue.size());
        }
    }

    private static void testAddition() {
        ArrayQueue queue = new ArrayQueue();
        Queue<Object> javaQueue = new ArrayDeque<>();

        final int elementCount = 10;
        for (int i = 0; i < elementCount; i++) {
            queue.enqueue(i);
            javaQueue.add(i);
        }

        while (!javaQueue.isEmpty() && !queue.isEmpty()) {
            final Object element1 = queue.dequeue();
            final Object element2 = javaQueue.remove();

            if (!element1.equals(element2)) {
                System.out.println("Added " + element2 + ", but retrieved " + element1);
                return;
            }
        }

        if (javaQueue.isEmpty() && !queue.isEmpty()) {
            System.out.println("Added and retrieved " + elementCount + " elements, but the queue is not empty");
        } else if (!javaQueue.isEmpty() && queue.isEmpty()) {
            System.out.println("Added " + elementCount + "elements, but retrieved only" + (elementCount - queue.size()));
        }
    }

    private static void testClear() {
        ArrayQueue queue = new ArrayQueue();
        for (int i = 1; i <= 10; i++) {
            queue.enqueue(i);
        }
        queue.clear();
        if (!queue.isEmpty()) {
            System.out.println("queue must be empty after clear()");
        }
    }

    private static void testDeque() {
        ArrayQueue deque = new ArrayQueue();
        Deque<Object> javaDeque = new ArrayDeque<>();

        final int elementCount = 10;
        for (int i = 1; i <= elementCount; i += 2) {
            deque.enqueue(i);
            javaDeque.addLast(i);
            deque.push(i + 1);
            javaDeque.addFirst(i + 1);
        }

        while (!javaDeque.isEmpty() && !deque.isEmpty()) {
            final Object element1 = javaDeque.removeLast();
            final Object element2 = deque.remove();

            if (!element1.equals(element2)) {
                System.out.println("Added " + element1 + " to deque, but retrieved " + element2);
            }
        }

        if (!javaDeque.isEmpty() && deque.isEmpty()) {
            System.out.println("Added " + elementCount + " elements, but retrieved only" + (elementCount - deque.size()));
        } else if (javaDeque.isEmpty() && !deque.isEmpty()) {
            System.out.println("Added and retrieved " + elementCount + " elements, but the deque is not empty");
        }
    }

    public static void main(String[] args) {
        testInitialization();
        testSize();
        testAddition();
        testDeque();
        testClear();
    }
}
