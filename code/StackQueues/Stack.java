package StackQueues_live;

// Last-In, First-Out (LIFO)
//
// push(3)   push(2)   push(1)   pop()→1   pop()→2   pop()→3
//
//                     |  1  |
//           |  2  |   |  2  |   |  2  |
// |  3  |   |  3  |   |  3  |   |  3  |   |  3  |
// +-----+   +-----+   +-----+   +-----+   +-----+   (empty)
public interface Stack<E> {
	public abstract void push(E element);
	public abstract E pop();
	public abstract int size();
}
