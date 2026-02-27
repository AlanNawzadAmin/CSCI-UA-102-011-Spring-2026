# Section 6: Recursion and Trees

Textbook: *Data Structures and Algorithms in Java*, 6th Edition — Chapter 8 (Exercises)

---

## Quick Refresh: Tree Traversals

Given a tree, there are several systematic ways to visit every node:

| Traversal | Order |
|---|---|
| **Preorder** | Visit node, then children (left to right) |
| **Postorder** | Visit children (left to right), then node |
| **Inorder** (binary trees only) | Left subtree, visit node, right subtree |
| **Breadth-first** | Level by level, left to right |

All four traversals run in $O(n)$ time.

**Preorder pseudocode:**
```
Algorithm preorder(p):
    visit p
    for each child c of p do
        preorder(c)
```

**Postorder pseudocode:**
```
Algorithm postorder(p):
    for each child c of p do
        postorder(c)
    visit p
```

**Inorder pseudocode (binary tree):**
```
Algorithm inorder(p):
    if p has a left child lc then
        inorder(lc)
    visit p
    if p has a right child rc then
        inorder(rc)
```

**Breadth-first pseudocode:**
```
Algorithm breadthfirst():
    Initialize queue Q with root()
    while Q is not empty do
        p = Q.dequeue()
        visit p
        for each child c of p do
            Q.enqueue(c)
```

---


### R-8.20

Let $T$ be an ordered tree with more than one node. Is it possible that the preorder traversal of $T$ visits the nodes in the **same order** as the postorder traversal of $T$? Likewise, is it possible that preorder visits nodes in the **reverse order** of the postorder traversal?

<details><summary>Solution</summary>

**Same order: No.**

In preorder, the root is visited **first**. In postorder, the root is visited **last**. Since $T$ has more than one node, the root cannot be both first and last. So the orders cannot be identical.

**Reverse order: Yes.**

If every internal node has exactly **one** child (i.e., $T$ is a single path), then:
- Preorder visits nodes top-to-bottom: $v_0, v_1, v_2, \ldots, v_k$
- Postorder visits nodes bottom-to-top: $v_k, v_{k-1}, \ldots, v_0$

These are reverses of each other. For example, a path $A \to B \to C$:
- Preorder: A, B, C
- Postorder: C, B, A

If any internal node has **two or more** children, the reversal property breaks. In preorder, the root comes before all descendants. In the reverse of postorder, the root also comes first — but the children's subtrees appear in reversed order. So this only works for single-child chains.

</details>


---

## Tree Properties and Algorithms

### R-8.5

Describe an algorithm, relying only on the BinaryTree operations, that counts the number of leaves in a binary tree that are the **left child** of their respective parent.

<details><summary>Solution</summary>

```java
public static <E> int countLeftLeaves(BinaryTree<E> T, Position<E> p) {
    int count = 0;
    Position<E> lc = T.left(p);
    if (lc != null) {
        if (T.isExternal(lc))
            count = 1;              // left child is a leaf — count it
        else
            count = countLeftLeaves(T, lc);
    }
    Position<E> rc = T.right(p);
    if (rc != null)
        count += countLeftLeaves(T, rc);
    return count;
}
```

Call with `countLeftLeaves(T, T.root())`.

**Idea:** At each internal node, check if its left child exists and is a leaf. If so, count 1. Then recurse on both children.

**Running time:** $O(n)$ — visits each node once.

</details>

---


### R-8.13

Justify Table 8.2, summarizing the running time of the methods of a general tree represented with a linked structure, by providing for each method a description of its implementation and an analysis of its running time.

**Table 8.2:**

| Method | Running Time |
|---|---|
| size, isEmpty | $O(1)$ |
| root, parent, isRoot, isInternal, isExternal | $O(1)$ |
| numChildren $(p)$ | $O(1)$ |
| children $(p)$ | $O(c_p)$ |
| depth $(p)$ | $O(d_p)$ |
| height | $O(n)$ |

<details><summary>Solution</summary>

- **size, isEmpty**: The tree stores a `size` instance variable that is updated on insertions/deletions. Both methods just return or compare this variable. $O(1)$.

- **root**: Returns the stored root reference. $O(1)$.

- **parent**: Each node stores a reference to its parent. Simply return it. $O(1)$.

- **isRoot**: Compare `p == root()`. $O(1)$.

- **isInternal, isExternal**: Check whether `numChildren(p) > 0` (or `== 0`). Since numChildren is $O(1)$, these are $O(1)$.

- **numChildren$(p)$**: Each node stores its children in a collection (e.g., a list). The collection tracks its size, so returning `children.size()` is $O(1)$.

- **children$(p)$**: Returns an iterable over $p$'s children. Creating the iterable takes $O(1)$, but iterating through all children takes $O(c_p)$ where $c_p$ is the number of children. Total: $O(c_p)$ (the $+1$ accounts for the case $c_p = 0$).

- **depth$(p)$**: Recursively walks up from $p$ to the root via parent references: `depth(p) = 1 + depth(parent(p))`. Takes $O(d_p)$ where $d_p$ is the depth.

- **height**: Uses the recursive algorithm from Code Fragment 8.5, visiting every node in the subtree. Each node is visited once. So height runs in $O(n)$.

</details>


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

**Why this works:** Without the auxiliary `depth` parameter, we would need to call `T.depth(p)` at each node, which itself takes $O(d_p)$ time — leading to $O(n^2)$ total. By passing the depth *down* through the recursion, each node's depth is available in $O(1)$.

**Running time:** $O(n)$ — each of the $n$ nodes is visited exactly once, and $O(1)$ work is done per node (plus iterating over children, which sums to $O(n-1)$ total by Proposition 8.4).

</details>
