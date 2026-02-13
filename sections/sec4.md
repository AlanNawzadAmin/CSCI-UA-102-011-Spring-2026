# Section 4: Stacks, Queues, and Algorithmic Complexity

Textbook: *Data Structures and Algorithms in Java*, 6th Edition — Chapters 4 and 6

---

## Quick Refresher (~5 min)

### Stack — LIFO (Last-In, First-Out)

| Method | Description |
|---|---|
| `push(e)` | Add element to the top |
| `pop()` | Remove and return the top element |
| `size()` | Number of elements |

`push(1), push(2), push(3)` → `pop()` returns **3**, `pop()` returns **2**, `pop()` returns **1**

### Queue — FIFO (First-In, First-Out)

| Method | Description |
|---|---|
| `enqueue(e)` | Add element to the back |
| `dequeue()` | Remove and return the front element |
| `size()` | Number of elements |

`enqueue(1), enqueue(2), enqueue(3)` → `dequeue()` returns **1**, `dequeue()` returns **2**, `dequeue()` returns **3**

---

## Stacks and Queues Applied (~20 min)

### R-6.4
Implement a method with signature `transfer(S, T)` that transfers all elements from stack S onto stack T, so that the element that starts at the top of S is the first to be inserted onto T, and the element at the bottom of S ends up at the top of T.

<details><summary>Solution</summary>

```java
public static <E> void transfer(Stack<E> S, Stack<E> T) {
    while (S.size() > 0) {
        T.push(S.pop());
    }
}
```

Each pop from S returns the current top, which gets pushed onto T. Since S is processed top-to-bottom, T ends up with S's bottom element on top. This **reverses** the order.

Example: S = (1, 2, 3) with 3 on top → T = (3, 2, 1) with 1 on top.

</details>

---

### C-6.17
Show how to use the `transfer` method from R-6.4, and two temporary stacks, to replace the contents of a given stack S with those same elements, but in reversed order.

<details><summary>Solution</summary>

Use two temporary stacks T1 and T2:

```java
Stack<E> T1 = new LinkedStack<>();
Stack<E> T2 = new LinkedStack<>();
transfer(S, T1);    // S → T1 (reversed)
transfer(T1, T2);   // T1 → T2 (reversed again = original order)
transfer(T2, S);    // T2 → S (reversed again = reversed original)
```

Each `transfer` reverses the order. Three reversals = one net reversal.

Trace with S = (1, 2, 3), 3 on top:

| Step | S | T1 | T2 |
|---|---|---|---|
| Start | (1, 2, 3) | () | () |
| transfer(S, T1) | () | (3, 2, 1) | () |
| transfer(T1, T2) | () | () | (1, 2, 3) |
| transfer(T2, S) | (3, 2, 1) | () | () |

S is now reversed: 1 on top.

</details>

---

### C-6.24
Suppose you have a stack S containing $n$ elements and a queue Q that is initially empty. Describe how you can use Q to scan S to see if it contains a certain element $x$, with the additional constraint that your algorithm must return the elements back to S in their original order. You may only use S, Q, and a constant number of other primitive variables.

<details><summary>Solution</summary>

```java
public static <E> boolean scanStack(Stack<E> S, Queue<E> Q, E x) {
    boolean found = false;
    int n = S.size();

    // Phase 1: pop everything from S into Q, checking for x
    for (int i = 0; i < n; i++) {
        E elem = S.pop();
        if (elem.equals(x)) found = true;
        Q.enqueue(elem);
    }

    // Phase 2: move everything from Q back to S
    // This puts them in reversed order (bottom of S is now on top)
    for (int i = 0; i < n; i++) {
        S.push(Q.dequeue());
    }

    // Phase 3: pop from S into Q (reversing again)
    for (int i = 0; i < n; i++) {
        Q.enqueue(S.pop());
    }

    // Phase 4: move from Q back to S (original order restored)
    for (int i = 0; i < n; i++) {
        S.push(Q.dequeue());
    }

    return found;
}
```

Key insight: popping a stack into a queue reverses the order. We need to do this an even number of times to restore the original order. Phases 2-4 accomplish this (reverse, reverse again via queue round-trip).

Total work: $O(n)$.

</details>

---

## Big-Oh (~25 min)

### Background: Big-Oh Notation

We say $f(n)$ is $O(g(n))$ if there exist constants $c > 0$ and $n_0 \geq 0$ such that:

$$f(n) \leq c \cdot g(n) \quad \text{for all } n \geq n_0$$

In other words, $f(n)$ grows **no faster than** $g(n)$ up to a constant factor, once $n$ is large enough. We use big-Oh to describe **worst-case** running time as a function of input size, ignoring constant factors and lower-order terms.

Common growth rates, slowest to fastest:

$$O(1) \subset O(\log n) \subset O(n) \subset O(n \log n) \subset O(n^2) \subset O(n^3) \subset O(2^n)$$

---

### R-4.8
Order the following functions by asymptotic growth rate:

$4n \log n + 2n$, $\;2^{10}$, $\;2^{\log n}$, $\;3n + 100 \log n$, $\;4n$, $\;2^n$, $\;n^2 + 10n$, $\;n^3$, $\;n \log n$

<details><summary>Solution</summary>

First, simplify where possible:
- $2^{10} = 1024$ — constant
- $2^{\log n} = n$ (assuming log base 2)

Assuming log base 2 throughout:

| Rank | Function | Growth class |
|---|---|---|
| 1 | $2^{10}$ | $O(1)$ |
| 2 | $2^{\log n}$ | $O(n)$ |
| 3 | $3n + 100 \log n$ | $O(n)$ |
| 4 | $4n$ | $O(n)$ |
| 5 | $n \log n$ | $O(n \log n)$ |
| 6 | $4n \log n + 2n$ | $O(n \log n)$ |
| 7 | $n^2 + 10n$ | $O(n^2)$ |
| 8 | $n^3$ | $O(n^3)$ |
| 9 | $2^n$ | $O(2^n)$ |

Note: functions with the same growth class (e.g., ranks 2-4 are all $O(n)$) can be further distinguished by their constant factors, but asymptotically they are equivalent.

</details>

---

### R-4.9 through R-4.12
Give a big-Oh characterization of the running time of the following methods:

**example1** — Sum of array elements:
```java
public static int example1(int[] arr) {
    int n = arr.length, total = 0;
    for (int j = 0; j < n; j++)       // loop from 0 to n-1
        total += arr[j];
    return total;
}
```

**example2** — Sum of even-indexed elements:
```java
public static int example2(int[] arr) {
    int n = arr.length, total = 0;
    for (int j = 0; j < n; j += 2)    // note the increment of 2
        total += arr[j];
    return total;
}
```

**example3** — Sum of prefix sums (nested loops):
```java
public static int example3(int[] arr) {
    int n = arr.length, total = 0;
    for (int j = 0; j < n; j++)       // loop from 0 to n-1
        for (int k = 0; k <= j; k++)   // loop from 0 to j
            total += arr[j];
    return total;
}
```

**example4** — Sum of prefix sums (single loop):
```java
public static int example4(int[] arr) {
    int n = arr.length, prefix = 0, total = 0;
    for (int j = 0; j < n; j++) {     // loop from 0 to n-1
        prefix += arr[j];
        total += prefix;
    }
    return total;
}
```

<details><summary>Solution</summary>

| Method | Running time | Reasoning |
|---|---|---|
| example1 | $O(n)$ | Single loop, $n$ iterations, $O(1)$ work each. |
| example2 | $O(n)$ | Single loop, $n/2$ iterations, $O(1)$ work each. $n/2$ is still $O(n)$. |
| example3 | $O(n^2)$ | Inner loop runs $1 + 2 + \cdots + n = \frac{n(n+1)}{2}$ times total. |
| example4 | $O(n)$ | Single loop, $n$ iterations. Computes the same result as example3 but avoids the inner loop by maintaining a running prefix sum. |

The key takeaway: example3 and example4 compute the same thing, but example4 is asymptotically faster because it avoids redundant work.

</details>

---

### R-4.16
Show that if $d(n)$ is $O(f(n))$ and $e(n)$ is $O(g(n))$, then $d(n) + e(n)$ is $O(f(n) + g(n))$.

<details><summary>Solution</summary>

**Proof:**

Since $d(n)$ is $O(f(n))$, there exist constants $c_1 > 0$ and $n_1$ such that:

$$d(n) \leq c_1 \cdot f(n) \quad \text{for all } n \geq n_1$$

Since $e(n)$ is $O(g(n))$, there exist constants $c_2 > 0$ and $n_2$ such that:

$$e(n) \leq c_2 \cdot g(n) \quad \text{for all } n \geq n_2$$

Let $c = \max(c_1, c_2)$ and $n_0 = \max(n_1, n_2)$. Then for all $n \geq n_0$:

$$d(n) + e(n) \leq c_1 \cdot f(n) + c_2 \cdot g(n) \leq c \cdot f(n) + c \cdot g(n) = c \cdot (f(n) + g(n))$$

Therefore $d(n) + e(n)$ is $O(f(n) + g(n))$. $\blacksquare$

**Why this is useful:** It tells us that when we compose two sequential algorithms, the total running time is bounded by the sum of their individual bounds. For example, an $O(n)$ pass followed by an $O(n \log n)$ sort is $O(n + n \log n) = O(n \log n)$.

</details>

---

### R-4.17
Show that if $d(n)$ is $O(f(n))$ and $e(n)$ is $O(g(n))$, then $d(n) - e(n)$ is **not** necessarily $O(f(n) - g(n))$.

<details><summary>Solution</summary>

Counterexample:

$$d(n) = n^2 \text{ which is } O(n^2), \quad \text{so } f(n) = n^2$$
$$e(n) = n \text{ which is } O(n^2), \quad \text{so } g(n) = n^2$$

Note: $n$ is $O(n^2)$ — a loose but valid bound.

$$d(n) - e(n) = n^2 - n \quad \text{which grows like } n^2$$
$$f(n) - g(n) = n^2 - n^2 = 0$$

We'd need $n^2 - n$ to be $O(0)$, i.e., bounded by a constant. It isn't.

**Intuition:** Big-Oh is an upper bound, not tight. With addition, overestimating both terms only overestimates the sum — still valid. With subtraction, overestimating the term being subtracted can shrink the bound to zero while the actual difference is large. $\blacksquare$

</details>

---
