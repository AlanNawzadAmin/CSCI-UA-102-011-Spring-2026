package StackQueues_live;

// FIFO (First-In, First-Out)
// How do we implement a queue with an array?
//
// Idea 1: enqueue at end, dequeue by shifting everything left.
//   enqueue(1), enqueue(2), enqueue(3): [1, 2, 3, _, _]
//   dequeue(): shift 2,3 left -> [2, 3, _, _, _]
//   Problem: dequeue is O(n) — we have to move every element.
//
// Idea 2: track a 'start' index instead of shifting.
//   enqueue(1), enqueue(2), enqueue(3): [1, 2, 3, _, _], start=0, end=3
//   dequeue(): just move start forward  [_, 2, 3, _, _], start=1, end=3

// [1, 2, 3, 4], start=0, end=4
// [5, _, _, 4], start=3, end=5

//   Now dequeue is O(1)!
//   Problem: after enough enqueues, 'end' reaches the end of the array.
//   But there's wasted space at the front from old dequeues.
//
// Idea 3: wrap around with % MAX_SIZE — reuse the empty front slots.
//   [_, _, _, Y, Z], start=3, end=5
//   enqueue(X): array[5 % 5] = array[0] = X -> [X, _, _, Y, Z]
//   This is a "circular array" — O(1) enqueue and dequeue, no wasted space.
//   Why is this safe? We can never wrap into a live element as long as
//   size (end - start) never exceeds MAX_SIZE.
public class ArrayQueue<E> {
	public static int MAX_SIZE = 5;
	E array[];
	int end;
	int start;

	public ArrayQueue(){
		array = (E[ ]) new Object[MAX_SIZE];
	}

	public void enqueue(E element) {
		// TODO
		if (size() == MAX_SIZE) throw new RuntimeException("Queue is full");
		array[end%MAX_SIZE] = element;
		end++;
	}
	public E dequeue() {
		// TODO
		E start_elem = array[start%MAX_SIZE];
		start++;
		return start_elem;
	}
	public int size() {
		// TODO
		return end - start;
	}

	public static void main(String [] args) {
		ArrayQueue<Integer> queue = new ArrayQueue();

		// basic FIFO
		queue.enqueue(1);
		queue.enqueue(2);
		queue.enqueue(3);
		System.out.println("output: " + queue.size() + ", expected: 3");
		System.out.println("output: " + queue.dequeue() + ", expected: 1");
		System.out.println("output: " + queue.dequeue() + ", expected: 2");
		System.out.println("output: " + queue.dequeue() + ", expected: 3");

		// circular wrap-around: we already used indices 0-2, now fill 3-4 and wrap to 0
		queue.enqueue(4);
		queue.enqueue(5);
		queue.enqueue(6); // wraps to index 0
		queue.enqueue(7); // wraps to index 1
		System.out.println("output: " + queue.dequeue() + ", expected: 4");
		System.out.println("output: " + queue.dequeue() + ", expected: 5");
		System.out.println("output: " + queue.dequeue() + ", expected: 6");
		System.out.println("output: " + queue.dequeue() + ", expected: 7");
	}
}
