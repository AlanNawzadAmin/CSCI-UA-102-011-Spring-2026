package StackQueues_live;
import LinkedList_final.DoublyLinkedList;

// FIFO (First-In, First-Out)
// Queue backed by a DoublyLinkedList. enqueue at tail, dequeue from head — both O(1).
// enqueue(1): head -> [1] <- tail
// enqueue(2): head -> [1] <-> [2] <- tail
// enqueue(3): head -> [1] <-> [2] <-> [3] <- tail
// dequeue(): returns 1 (head), dequeue(): returns 2, dequeue(): returns 3
public class LinkedQueue<E> extends DoublyLinkedList<E> implements Queue<E>{

	public void enqueue(E element) {
		// TODO
		addLast(element);
	}
	public E dequeue() {
		// TODO
		return removeFirst();
	}

	public static void main(String [] args) {
		LinkedQueue<Integer> queue = new LinkedQueue();
		queue.enqueue(1);
		queue.enqueue(2);
		queue.enqueue(3);
		System.out.println("output: " + queue.dequeue() + ", expected: 1");
		System.out.println("output: " + queue.dequeue() + ", expected: 2");
		System.out.println("output: " + queue.dequeue() + ", expected: 3");
	}
}
