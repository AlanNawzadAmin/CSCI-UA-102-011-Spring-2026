# Section 10: Binary Search Trees

Textbook: *Data Structures and Algorithms in Java*, 6th Edition, Chapter 11 (Exercises)

---

## Quick Refresh: Binary Search Trees

### Definition

A **binary search tree** (BST) is a proper binary tree where each internal position $p$ stores a key-value pair $(k, v)$ such that:
- All keys in the **left** subtree of $p$ are **less than** $k$.
- All keys in the **right** subtree of $p$ are **greater than** $k$.

Leaves (external nodes) serve as sentinels and do not store entries.

### Searching

To find key $k$, start at the root and at each internal node $p$:
1. If $k = \text{key}(p)$, found.
2. If $k < \text{key}(p)$, recurse on the left child.
3. If $k > \text{key}(p)$, recurse on the right child.
4. If we reach a leaf, the key is not in the tree.

Running time: $O(h)$ where $h$ is the height of the tree.

### Insertion

To insert $(k, v)$:
1. Search for $k$. If found, update the value.
2. If not found, the search ends at a leaf. Replace that leaf with a new internal node storing $(k, v)$, giving it two leaf children.

### Performance

| Operation | Time |
|---|---|
| `get(k)` | $O(h)$ |
| `put(k, v)` | $O(h)$ |
| `remove(k)` | $O(h)$ |

In the **worst case**, $h = n - 1$ (a degenerate tree that looks like a linked list). In the **best case**, $h = O(\log n)$ (a balanced tree).

### In-order traversal

An in-order traversal of a BST visits keys in **sorted order**. This is why BSTs are used to implement **sorted maps**.

---

### R-11.1

If we insert the entries $(1, A)$, $(2, B)$, $(3, C)$, $(4, D)$, and $(5, E)$, in this order, into an initially empty binary search tree, what will it look like?

<details><summary>Solution</summary>

Each key is larger than all previous keys, so every insertion goes to the right child. The result is a degenerate tree (a chain going right):

```
1
 \
  2
   \
    3
     \
      4
       \
        5
```

This is the worst case for a BST: height $h = n - 1 = 4$, and every operation takes $O(n)$ time.

</details>

---

### R-11.2

Insert, into an empty binary search tree, entries with keys 30, 40, 24, 58, 48, 26, 11, 13 (in this order). Draw the tree after each insertion.

<details><summary>Solution</summary>

**Insert 30:** Root is 30.
```
30
```

**Insert 40:** 40 > 30, goes right.
```
30
  \
   40
```

**Insert 24:** 24 < 30, goes left.
```
    30
   /  \
  24   40
```

**Insert 58:** 58 > 30, 58 > 40, goes right of 40.
```
    30
   /  \
  24   40
         \
          58
```

**Insert 48:** 48 > 30, 48 > 40, 48 < 58, goes left of 58.
```
    30
   /  \
  24   40
         \
          58
         /
        48
```

**Insert 26:** 26 < 30, 26 > 24, goes right of 24.
```
    30
   /  \
  24   40
    \    \
    26    58
         /
        48
```

**Insert 11:** 11 < 30, 11 < 24, goes left of 24.
```
      30
     /  \
   24    40
  /  \     \
 11  26     58
           /
          48
```

**Insert 13:** 13 < 30, 13 < 24, 13 > 11, goes right of 11.
```
      30
     /  \
   24    40
  /  \     \
 11  26     58
  \        /
  13      48
```

</details>

---

### R-11.3

How many different binary search trees can store the keys $\{1, 2, 3\}$?

<details><summary>Solution</summary>

**5 different BSTs.** We enumerate by choosing each possible root:

**Root = 1** (2 trees):
```
1           1
 \           \
  2           3
   \         /
    3       2
```

**Root = 2** (1 tree):
```
  2
 / \
1   3
```

**Root = 3** (2 trees):
```
    3       3
   /       /
  2       1
 /         \
1           2
```

Total: $2 + 1 + 2 = 5$.

This is the Catalan number $C_3 = \frac{1}{4}\binom{6}{3} = 5$. In general, the number of structurally distinct BSTs on $n$ keys is $C_n = \frac{1}{n+1}\binom{2n}{n}$.

</details>

---

### R-11.4

True or False: the order in which a fixed set of entries is inserted into a binary search tree does not matter: the same tree results every time. 

<details><summary>Solution</summary>

Consider the keys $\{1, 2, 3\}$.

**Insertion order 2, 1, 3:**
```
  2
 / \
1   3
```

**Insertion order 1, 2, 3:**
```
1
 \
  2
   \
    3
```

Same set of keys, different trees. The insertion order determines the tree structure. (In fact, R-11.3 shows there are 5 possible BSTs for just 3 keys.)

</details>

---

### Rank of the root

Write a method `int rootRank()` that returns the rank of the root in a BST (i.e., its 1-based position in sorted order). For example, in the tree below the root is 5 and its rank is 3 (the keys in sorted order are 1, 3, **5**, 7, 9).

```
      5
     / \
    3   7
   /     \
  1       9
```


<details><summary>Solution</summary>

**Approach 1: subtreeSize.** The rank of the root is `1 + (number of nodes in the left subtree)`.

```java
private int rootRank() {
    return 1 + subtreeSize(left(root()));
}

private int subtreeSize(Position<Entry<Integer,V>> p) {
    if (isExternal(p)) return 0;
    return 1 + subtreeSize(left(p)) + subtreeSize(right(p));
}
```

This visits every node in the left subtree, so it is $O(m)$ where $m$ is the left subtree size.

**Approach 2: in-order counter.** Do an in-order traversal with a mutable counter, and stop as soon as we visit the root.

```java
private int rootRank() {
    int[] count = new int[]{0};
    rankHelper(root(), count);
    return count[0];
}

private boolean rankHelper(Position<Entry<Integer,V>> p, int[] count) {
    if (isExternal(p)) return false;

    if (rankHelper(left(p), count)) return true;   // already found? stop

    count[0]++;                                     // visit current node
    if (p == root()) return true;                   // this is the root, stop

    return rankHelper(right(p), count);             // continue right
}
```

The helper returns `true` to signal "stop early." The in-order traversal visits the entire left subtree, then the root, then immediately stops. Same asymptotic cost as Approach 1, but this pattern generalizes: to find the rank of *any* node, just change the stopping condition from `p == root()` to `p == target`.

</details>
