package StackQueues_live;

// LIFO application: Uses a stack to check balanced parentheses.
//
// Balanced:    "()"    "()()"    "(())"
// Unbalanced:  "("     "())("    "(()"
//
// Idea: On '(' push, on ')' pop. Fail if pop from empty or stack non-empty at end.
// "(())": push, push, pop, pop -> stack empty -> true
// "())(": push, pop, pop on empty -> false
public class ParenthesesChecker {
	public static boolean check(String string) {
		Stack<Character> stack = new LinkedStack<Character>();
		for (int i=0; i<string.length(); i++) {
			char character = string.charAt(i);
			// TODO
			if (character == '(') {
				stack.push('(');
			} else {
				if (stack.size() == 0) {
					return false;
				} else {
					stack.pop();
				}
			}
		}
		// "()("
		return stack.size() == 0;
	}

	public static void main(String[] args) {
		System.out.println("output: " + check("()")     + ", expected: true");
		System.out.println("output: " + check("()()")   + ", expected: true");
		System.out.println("output: " + check("(())")   + ", expected: true");
		System.out.println("output: " + check("(")      + ", expected: false");
		System.out.println("output: " + check("())(")    + ", expected: false");
		System.out.println("output: " + check("((())")    + ", expected: false");
	}
}
