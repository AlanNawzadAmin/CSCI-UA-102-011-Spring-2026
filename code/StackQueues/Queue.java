package StackQueues_live;

// First-In, First-Out (FIFO)
// enqueue(1), enqueue(2), enqueue(3): -> [1] -> [1, 2] -> [1, 2, 3]
// dequeue(), dequeue(), dequeue() -> [2, 3] -> [3] -> [] (returns 1, 2, 3)
// Compare with Stack: same input, but Stack returns 3, 2, 1
public interface Queue<E> {
	public abstract void enqueue(E element);
	public abstract E dequeue();
	public abstract int size();
}
