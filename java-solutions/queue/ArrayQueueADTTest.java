package queue;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

public class ArrayQueueADTTest {
    private static void testInitialization() {
        ArrayQueueADT queue = ArrayQueueADT.create();
        if (!ArrayQueueADT.isEmpty(queue)) {
            System.out.println("new queue must be empty");
        }
    }

    private static void testSize() {
        ArrayQueueADT queue = ArrayQueueADT.create();

        int expectedSize = 10;
        for (int i = 1; i <= expectedSize; i++) {
            ArrayQueueADT.enqueue(queue, i);
        }
        if (ArrayQueueADT.size(queue) != expectedSize) {
            System.out.println("Added " + expectedSize + " elements, but size() = " + ArrayQueueADT.size(queue));
        }
    }

    private static void testAddition() {
        ArrayQueueADT queue = ArrayQueueADT.create();
        Queue<Object> javaQueue = new ArrayDeque<>();

        final int elementCount = 10;
        for (int i = 0; i < elementCount; i++) {
            ArrayQueueADT.enqueue(queue, i);
            javaQueue.add(i);
        }

        while (!javaQueue.isEmpty() && !ArrayQueueADT.isEmpty(queue)) {
            final Object element1 = ArrayQueueADT.dequeue(queue);
            final Object element2 = javaQueue.remove();

            if (!element1.equals(element2)) {
                System.out.println("Added " + element2 + ", but retrieved " + element1);
                return;
            }
        }

        if (javaQueue.isEmpty() && !ArrayQueueADT.isEmpty(queue)) {
            System.out.println("Added and retrieved " + elementCount + " elements, but the queue is not empty");
        } else if (!javaQueue.isEmpty() && ArrayQueueADT.isEmpty(queue)) {
            System.out.println("Added " + elementCount + "elements, but retrieved only" + (elementCount - ArrayQueueADT.size(queue)));
        }
    }

    private static void testDeque() {
        ArrayQueueADT deque = ArrayQueueADT.create();
        Deque<Object> javaDeque = new ArrayDeque<>();

        final int elementCount = 10;
        for (int i = 1; i <= elementCount; i += 2) {
            ArrayQueueADT.enqueue(deque, i);
            javaDeque.addLast(i);
            ArrayQueueADT.push(deque, i + 1);
            javaDeque.addFirst(i + 1);
        }

        while (!javaDeque.isEmpty() && !ArrayQueueADT.isEmpty(deque)) {
            final Object element1 = javaDeque.removeLast();
            final Object element2 = ArrayQueueADT.remove(deque);

            if (!element1.equals(element2)) {
                System.out.println("Added " + element1 + " to deque, but retrieved " + element2);
            }
        }

        if (!javaDeque.isEmpty() && ArrayQueueADT.isEmpty(deque)) {
            System.out.println("Added " + elementCount + " elements, but retrieved only" + (elementCount - ArrayQueueADT.size(deque)));
        } else if (javaDeque.isEmpty() && !ArrayQueueADT.isEmpty(deque)) {
            System.out.println("Added and retrieved " + elementCount + " elements, but the deque is not empty");
        }
    }

    private static void testClear() {
        ArrayQueueADT queue = ArrayQueueADT.create();
        for (int i = 1; i <= 10; i++) {
            ArrayQueueADT.enqueue(queue, i);
        }
        ArrayQueueADT.clear(queue);
        if (!ArrayQueueADT.isEmpty(queue)) {
            System.out.println("queue must be empty after clear()");
        }
    }

    public static void main(String[] args) {
        testInitialization();
        testSize();
        testAddition();
        testClear();
        testDeque();
    }
}