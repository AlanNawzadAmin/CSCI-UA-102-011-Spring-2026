# Section 12: Efficient Sorting

Textbook: *Data Structures and Algorithms in Java*, 6th Edition, Chapter 12 (Exercises)

---

## Quick Refresh

### Comparison-Based Sorts

| Algorithm | Worst | Average | Best | In-place |
|---|---|---|---|---|
| Insertion-sort | $O(n^2)$ | $O(n^2)$ | $O(n)$ | yes |
| Selection-sort | $O(n^2)$ | $O(n^2)$ | $O(n^2)$ | yes |
| Merge-sort | $O(n \log n)$ | $O(n \log n)$ | $O(n \log n)$ | no |
| Heap-sort | $O(n \log n)$ | $O(n \log n)$ | $O(n \log n)$ | yes |
| Quick-sort | $O(n^2)$ | $O(n \log n)$ | $O(n \log n)$ | yes |

**Lower bound:** any comparison-based sort requires $\Omega(n \log n)$ comparisons in the worst case. (Proof: a decision tree with $n!$ leaves has depth $\geq \log_2(n!) = \Omega(n \log n)$.)

### R-12.7

Suppose we are given two $n$-element sorted sequences $A$ and $B$ each with distinct elements, but potentially some elements that are in both sequences. Describe an $O(n)$-time method for computing a sequence representing the union $A \cup B$ (with no duplicates) as a sorted sequence.

<details><summary>Solution</summary>

Use the **merge step** of merge-sort, with a tweak to skip duplicates:

```
union(A, B):
    i = 0, j = 0, result = []
    while i < |A| and j < |B|:
        if A[i] < B[j]:
            result.append(A[i]); i++
        else if A[i] > B[j]:
            result.append(B[j]); j++
        else:                          # A[i] == B[j]
            result.append(A[i])        # take one copy
            i++; j++                   # advance both
    append remaining of A or B
    return result
```

Each iteration advances $i$, $j$, or both by 1, and there are at most $2n$ iterations. So this runs in $O(n)$.

</details>

---


### R-12.23

Give an example input that requires merge-sort take $O(n \log n)$ time to sort, but insertion-sort runs in $O(n)$ time. What if you reverse this list?

<details><summary>Solution</summary>

**Already-sorted input:** $S = [1, 2, 3, \ldots, n]$.

- **Insertion-sort:** $O(n)$. Each insertion finds its element already in place after one comparison.
- **Merge-sort:** $O(n \log n)$. Always splits and merges regardless of input.

**Reverse-sorted input:** $S = [n, n-1, \ldots, 2, 1]$.

- **Insertion-sort:** $O(n^2)$. Each new element must be shifted past every prior element — worst case for insertion-sort.
- **Merge-sort:** $O(n \log n)$ (unchanged).

</details>

---

### C-12.39

Suppose we are given two sequences $A$ and $B$ of $n$ elements, possibly containing duplicates, on which a total order relation is defined. Describe an efficient algorithm for determining if $A$ and $B$ contain the same set of elements. What is the running time of this method?

<details><summary>Solution</summary>

**Sort both, then compare element-by-element.**

```
1. Sort A and B (each O(n log n)).
2. Walk through both in parallel; if any A[i] != B[i], return false.
3. Otherwise return true.
```

Total: $O(n \log n)$.

We could also use a hash map: count occurrences in $A$, then verify $B$ has the same counts. Expected $O(n)$, but $O(n \log n)$ worst-case via balanced BST.

</details>

---

### C-12.42

Given a sequence $S$ of $n$ elements, on which a total order relation is defined, describe an efficient method for determining whether there are two equal elements in $S$. What is the running time of your method?

<details><summary>Solution</summary>

**Sort, then check adjacent pairs.**

```
1. Sort S in O(n log n).
2. For i = 1 to n-1: if S[i] == S[i-1], return true.
3. Return false.
```

Total: $O(n \log n)$.

Or with a hash set: $O(n)$ expected.

</details>

---

### C-12.46

Let $A$ and $B$ be two sequences of $n$ integers each. Given an integer $m$, describe an $O(n \log n)$-time algorithm for determining if there is an integer $a \in A$ and an integer $b \in B$ such that $m = a + b$.

<details><summary>Solution</summary>

**Sort $B$, then for each $a \in A$ binary-search for $m - a$ in $B$.**

```
1. Sort B in O(n log n).
2. For each a in A:
       if binarySearch(B, m - a) succeeds, return (a, m - a).
3. Return false.
```

Step 1: $O(n \log n)$. Step 2: $n$ iterations of $O(\log n)$ binary search = $O(n \log n)$. Total: $O(n \log n)$.

**Alternative ($O(n)$ with hashing):** put all of $B$ into a hash set, then for each $a$, check if $m - a$ is in the set. Expected $O(n)$.

</details>

---

