# Section 5: Recursion and Recursion in Trees

Textbook: *Data Structures and Algorithms in Java*, 6th Edition — Chapters 5 and 8

---

## Quick Refresher (~5 min)

### Anatomy of a Recursive Method

Every recursive method needs:
1. **Base case(s)** — when to stop recursing
2. **Recursive case(s)** — call itself on a smaller subproblem
3. **Progress toward the base case** — each call must reduce the problem


```java
public static long fib(int n) {
    if (n <= 1) return n;   // base cases
    return fib(n - 2) + fib(n - 1);    // recursive case
}
```

Common patterns:
- **Linear recursion**: one recursive call per invocation (e.g. factorial, linear sum)
- **Binary recursion**: two recursive calls (e.g. Fibonacci, binary sum)
- **Multiple recursion**: variable number of recursive calls (e.g. tree traversals)

## Fibonacci

The naive recursive Fibonacci makes **two** recursive calls that overlap:

`fib(n)` calls `fib(n-1)`, which itself calls `fib(n-2)` — so `fib(n-2)` is computed **twice**. This cascades: $O(2^{n/2})$ total calls.

The fix: when computing `fib(n-1)`, you already computed `fib(n-2)` along the way. Instead of throwing it away, **return both values together**:

```java
// returns {F(n), F(n-1)}
public static long[] fibFast(int n) {
    if (n <= 1) return new long[]{n, 0};
    long[] prev = fibFast(n - 1);               // {F(n-1), F(n-2)}
    return new long[]{prev[0] + prev[1], prev[0]};  // {F(n), F(n-1)}
}
```

Now each level makes **one** call and gets back the **two** values it needs. Linear recursion: $O(n)$.

**Trace for `fibFast(4)`:**

| Call | Returns | Meaning |
|---|---|---|
| `fibFast(1)` | `{1, 0}` | $F_1 = 1, F_0 = 0$ |
| `fibFast(2)` | `{1, 1}` | $F_2 = 1, F_1 = 1$ |
| `fibFast(3)` | `{2, 1}` | $F_3 = 2, F_2 = 1$ |
| `fibFast(4)` | `{3, 2}` | $F_4 = 3, F_3 = 2$ |


---

## Recursion (~20 min)

### R-5.1

Describe a recursive algorithm for finding the maximum element in an array, $A$, of $n$ elements. What is your running time and space usage?

<details><summary>Solution</summary>

```java
public static int recursiveMax(int[] A, int n) {
    if (n == 1)
        return A[0];
    else
        return Math.max(A[n - 1], recursiveMax(A, n - 1));
}
```

**Idea:** The max of $n$ elements is either the last element or the max of the first $n - 1$ elements.

**Running time:** $O(n)$ — one recursive call per element, $O(1)$ work per call.

**Space usage:** $O(n)$ — the recursion depth is $n$ (one frame per call on the call stack).

</details>

---

### R-5.7

Describe a recursive algorithm for computing the $n$th **Harmonic number**, defined as $H_n = \sum_{k=1}^{n} 1/k$.

<details><summary>Solution</summary>

$$H_1 = 1, \quad H_n = H_{n-1} + \frac{1}{n} \text{ for } n > 1$$

```java
public static double harmonic(int n) {
    if (n == 1)
        return 1.0;
    else
        return harmonic(n - 1) + 1.0 / n;
}
```

This is linear recursion: one call per step, $O(n)$ total calls, $O(n)$ space.

Note: this is **not** tail recursion because we add $1/n$ *after* the recursive call returns.

</details>

---

### C-5.18

Write a short recursive Java method that determines if a string $s$ is a palindrome, that is, it is equal to its reverse. Examples of palindromes include `'racecar'` and `'gohangasalamiimalasagnahog'`.

<details><summary>Solution</summary>

```java
public static boolean isPalindrome(String s, int low, int high) {
    if (low >= high)
        return true;                              // 0 or 1 characters left
    if (s.charAt(low) != s.charAt(high))
        return false;                             // mismatch found
    return isPalindrome(s, low + 1, high - 1);    // check inner substring
}
```

Call with `isPalindrome(s, 0, s.length() - 1)`.

**Why it works:** A palindrome reads the same forwards and backwards. If the first and last characters match, the string is a palindrome if and only if the inner substring is also a palindrome.

**Running time:** $O(n)$ — each call shrinks the range by 2, so at most $n/2$ calls.

**Space:** $O(n)$ due to recursion depth. Note this *is* tail recursion (the recursive call is the last operation), so it could be converted to a loop.

</details>

---

## Recursion in Trees (~25 min)

### Background: Tree Terminology

Refer to the tree of Figure 8.3 (a file system):

```
                                /user/rt/courses/
                              /                    \
                        cs016/                      cs252/
                    /     |      \                 /      \
               grades    hw/    prog/         projects/  grades
                        / | \   / | \          /     \
                      h1 h2 h3 p1 p2 p3   papers/  demos/
                                           / |  \
                                     buylow sh  market
```
*(hw/ = homeworks/, prog/ = programs/, h1 = hw1, etc., sh = sellhigh)*

Key terms:
- **Root**: `/user/rt/courses/`
- **Internal node**: has at least one child (e.g. `cs016/`, `homeworks/`)
- **External node (leaf)**: no children (e.g. `grades`, `hw1`, `market`)
- **Depth** of node: number of ancestors excluding itself (root has depth 0)
- **Height** of tree: maximum depth among all nodes

---

### R-8.1

The following questions refer to the tree of Figure 8.3 above.

a. Which node is the root?
b. What are the internal nodes?
c. How many descendants does node `cs016/` have?
d. How many ancestors does node `cs016/` have?
e. What are the siblings of node `homeworks/`?
f. Which nodes are in the subtree rooted at node `projects/`?
g. What is the depth of node `papers/`?
h. What is the height of the tree?

<details><summary>Solution</summary>

a. `/user/rt/courses/`

b. `/user/rt/courses/`, `cs016/`, `cs252/`, `homeworks/`, `programs/`, `projects/`, `papers/`, `demos/`

c. **9** descendants: `grades`, `homeworks/`, `programs/`, `hw1`, `hw2`, `hw3`, `pr1`, `pr2`, `pr3`

d. **1** ancestor: `/user/rt/courses/`

e. `grades` and `programs/`

f. `projects/`, `papers/`, `demos/`, `buylow`, `sellhigh`, `market`

g. **3** — path: `/user/rt/courses/` → `cs252/` → `projects/` → `papers/`

h. **4** — the deepest nodes are `buylow`, `sellhigh`, `market` at depth 4

</details>

---

### R-8.4

What is the running time of a call to `T.height(p)` (Code Fragment 8.5) when called on a position $p$ distinct from the root of tree $T$?

Code Fragment 8.5:
```java
public int height(Position<E> p) {
    int h = 0;                               // base case if p is external
    for (Position<E> c : children(p))
        h = Math.max(h, 1 + height(c));
    return h;
}
```

<details><summary>Solution</summary>

The method visits every node in the subtree rooted at $p$, doing $O(1)$ work per node. So the running time is $O(n_p)$, where $n_p$ is the number of nodes in that subtree.

When $p$ is the root, this is $O(n)$. When $p$ is a leaf, this is $O(1)$.

</details>

---

### C-8.28

The **path length** of a tree $T$ is the sum of the depths of all positions in $T$. Describe a linear-time method for computing the path length of a tree $T$.

*Hint: use an auxiliary parameter in a recursive method.*

<details><summary>Solution</summary>

Pass the current depth as a parameter:

```java
public static <E> int pathLength(Tree<E> T, Position<E> p, int depth) {
    int total = depth;                          // count this node's depth
    for (Position<E> c : T.children(p))
        total += pathLength(T, c, depth + 1);   // children are one level deeper
    return total;
}
```

Call with `pathLength(T, T.root(), 0)`.

**Why this works:** Without the auxiliary `depth` parameter, we would need to call `T.depth(p)` at each node, which itself takes $O(d_p)$ time — leading to $O(n^2)$ total (same issue as `heightBad`). By passing the depth *down* through the recursion, each node's depth is available in $O(1)$.

**Running time:** $O(n)$ — each of the $n$ nodes is visited exactly once, and $O(1)$ work is done per node (plus iterating over children, which sums to $O(n-1)$ total by Proposition 8.4).

</details>

---

### C-8.32

For a tree $T$, let $n_I$ denote the number of its internal nodes, and let $n_E$ denote the number of its external nodes. Show that if every internal node in $T$ has exactly 3 children, then $n_E = 2n_I + 1$.

<details><summary>Solution</summary>

**Intuition:** Start with a single leaf ($n_I = 0$, $n_E = 1$). Each time you expand a leaf into an internal node with 3 children, you lose 1 leaf and gain 3 — net **+1 internal, +2 leaves**. So the relationship $n_E = 2n_I + 1$ is maintained at every step.

**Proof by induction on $n_I$:**

*Base case:* $n_I = 0$. The tree is a single leaf. $n_E = 1 = 2(0) + 1$. $\checkmark$

*Inductive step:* Assume the claim holds for all ternary trees with fewer than $n_I$ internal nodes. Consider a tree $T$ with $n_I \geq 1$ internal nodes. Pick any internal node $p$ whose children are all leaves. Removing $p$'s 3 children turns $p$ into a leaf, giving a smaller tree $T'$ with $n_I' = n_I - 1$ and $n_E' = n_E - 3 + 1 = n_E - 2$.

By the inductive hypothesis: $n_E' = 2n_I' + 1$, i.e. $n_E - 2 = 2(n_I - 1) + 1$.

Solving: $n_E = 2n_I + 1$. $\blacksquare$

**Generalization:** For $k$ children per internal node, each expansion nets $+(k-1)$ leaves, so $n_E = (k-1)n_I + 1$. The binary case ($k = 2$) gives $n_E = n_I + 1$.

</details>

---
