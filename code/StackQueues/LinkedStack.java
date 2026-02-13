package StackQueues_live;
import LinkedList_final.LinkedList;

// LIFO (Last-In, First-Out)
// Stack backed by a LinkedList. push/pop operate at the head — both O(1).
//
// push(3)   push(2)   push(1)   pop()→1   pop()→2   pop()→3
//
//                     |  1  |
//           |  2  |   |  2  |   |  2  |
// |  3  |   |  3  |   |  3  |   |  3  |   |  3  |
// +-----+   +-----+   +-----+   +-----+   +-----+   (empty)
public class LinkedStack<E> implements Stack<E>{
	private LinkedList<E> list;

	public LinkedStack(){
		list = new LinkedList<E>();
	}

	public E pop() {
		// TODO
		return list.removeLast();
	}
	public void push(E element) {
		// TODO
		list.addLast(element);
	}

	public int size() {
		// TODO
		return list.size();
	}

	public static void main(String [] args) {
		LinkedStack<Integer> stack = new LinkedStack();
		stack.push(3);
		stack.push(2);
		stack.push(1);
		System.out.println("output: " + stack.pop() + ", expected: 1");
		System.out.println("output: " + stack.pop() + ", expected: 2");
		System.out.println("output: " + stack.pop() + ", expected: 3");
		System.out.println("output: " + stack.size() + ", expected: 0");
	}
}
