package queue;

import java.util.ArrayDeque;
import java.util.Deque;

public class ArrayQueueModuleTest {

    public static void main(String[] args) {
        if (!ArrayQueueModule.isEmpty()) {
            System.out.println("new queue must be empty");
            return;
        }

        int expectedSize = 10;
        for (int i = 1; i <= expectedSize; i++) {
            ArrayQueueModule.enqueue(i);
        }
        if (ArrayQueueModule.size() != expectedSize) {
            System.out.println("Added " + expectedSize + " elements, but size() = " + ArrayQueueModule.size());
            return;
        }

        ArrayQueueModule.clear();
        if (!ArrayQueueModule.isEmpty()) {
            System.out.println("queue must be empty after clear()");
            return;
        }

        Deque<Object> javaDeque = new ArrayDeque<>();

        final int elementCount = 10;
        for (int i = 1; i <= elementCount; i += 2) {
            ArrayQueueModule.enqueue(i);
            javaDeque.addLast(i);
            ArrayQueueModule.push(i + 1);
            javaDeque.addFirst(i + 1);
        }

        while (!javaDeque.isEmpty() && !ArrayQueueModule.isEmpty()) {
            final Object element1 = javaDeque.removeLast();
            final Object element2 = ArrayQueueModule.remove();

            if (!element1.equals(element2)) {
                System.out.println("Added " + element1 + " to deque, but retrieved " + element2);
            }
        }

        if (!javaDeque.isEmpty()) {
            System.out.println("Added " + elementCount + " elements, but retrieved only" + (elementCount - ArrayQueueModule.size()));
        } else if (!ArrayQueueModule.isEmpty()) {
            System.out.println("Added and retrieved " + elementCount + " elements, but the deque is not empty");
        }
    }
}