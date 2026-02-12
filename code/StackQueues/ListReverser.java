package StackQueues_live;

// LIFO application: Reverse an array using a stack.
// array: [0, 1, 2, 3, 4]
// stack: [0, 1, 2, 3, 4]
// array -> [4, 3, 2, 1, 0]
public class ListReverser{
	public static <E> void reverse(E[] array) {
		Stack<E> stack = new LinkedStack<E>();
		// TODO
		// push everything on to the stack
		for (int i=0; i<array.length; i++) {
			stack.push(array[i]);
		}
		// pop everything off and store in array
		for (int i=0; i<array.length; i++) {
			array[i] = stack.pop();
		}
		// array: [0, 1, 2, 3, 4], stack: [0, 1, 2, 3, 4]
		// array: [4, 1, 2, 3, 4], stack: [0, 1, 2, 3, _]
		// array: [4, 3, 2, 3, 4], stack: [0, 1, 2, _, _]
		// ...
		// array: [4, 3, 2, 1, 0], stack: [_, _, _, _, _]
	}
	public static void main(String[] args) {
		Integer[] array = new Integer[10];
		for (int i=0; i<array.length; i++) {
			array[i] = i;
		}
		ListReverser.<Integer>reverse(array);
		for (int i=0; i<array.length; i++) {
			System.out.println("output: " + array[i] + ", expected: " + (array.length - 1 - i));
		}
	}
}
