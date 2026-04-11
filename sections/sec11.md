# Section 11: AVL Trees and Sorting

Textbook: *Data Structures and Algorithms in Java*, 6th Edition, Chapters 11 and 12 (Exercises)

---

## Quick Refresh

### AVL Trees

An **AVL tree** is a BST with the **height-balance property**: for every internal node $p$, the heights of $p$'s two children differ by at most 1.

The **balance factor** of $p$ is $\text{height(left)} - \text{height(right)}$. AVL requires this to be in $\{-1, 0, +1\}$ for every node.

**Balanced (OK):**
```
        4 (bf=0)
       / \
      2   6 (bf=0)
     /\   /\
    1 3  5 7
```

**Imbalanced (violates AVL):**
```
       4 (bf=-3)  ← imbalance!
      / \
     2   6 (bf=-2)
          \
           7 (bf=-1)
            \
             8
```
Heights: left of 4 is 1, right of 4 is 3. Difference = 2.

**Consequence:** an AVL tree storing $n$ entries has height $O(\log n)$, so all map operations run in $O(\log n)$.

**Maintaining the property:** after an insertion or deletion, walk up from the modified node and check the balance factor. If a node $z$ becomes unbalanced ($|\text{bf}| = 2$), perform a **trinode restructuring** (rotation) to restore balance.

Let $z$ be the imbalanced node, $y$ be the taller child of $z$, and $x$ be the taller child of $y$. The four cases:

**Right rotation (LL case):** $z$-left, $y$-left.
```
        z                       y
       / \                     / \
      y   T4                  x   z
     / \           →         /\   /\
    x   T3                 T1 T2 T3 T4
   /\
  T1 T2
```

**Left rotation (RR case):** $z$-right, $y$-right.
```
    z                           y
   / \                         / \
  T1  y                       z   x
     / \           →         /\   /\
    T2  x                  T1 T2 T3 T4
       /\
      T3 T4
```

**Left-right rotation (LR case):** $z$-left, $y$-right.
```
      z                z                  x
     / \              / \                / \
    y   T4           x   T4             y   z
   / \         →    / \         →      /\   /\
  T1  x            y   T3            T1 T2 T3 T4
     /\           / \
    T2 T3        T1 T2
```
(Left rotation at $y$, then right rotation at $z$.)

**Right-left rotation (RL case):** $z$-right, $y$-left. Symmetric to LR.

| Case | Rotation needed |
|---|---|
| left-left (LL) | single right rotation at $z$ |
| right-right (RR) | single left rotation at $z$ |
| left-right (LR) | left rotation at $y$, then right rotation at $z$ |
| right-left (RL) | right rotation at $y$, then left rotation at $z$ |

**Key fact:** rotations preserve the BST in-order. The keys still appear in the same left-to-right order — only the tree's vertical structure changes.

After insertion, **one** restructuring suffices to restore the entire tree. After deletion, restructurings may need to **propagate** up to the root, but at most $O(\log n)$ of them.

### R-11.5

Claim: the order in which a fixed set of entries is inserted into an AVL tree does not matter: the same AVL tree results every time. True or False? 

<details><summary>Solution</summary>
Try $\{1, 2, 3, 4\}$:

**Insertion order 1, 2, 3, 4:**
- Insert 1, 2, 3 → rotate to make 2 the root (as above).
- Insert 4: goes right of 3. Tree is balanced.
```
  2
 / \
1   3
     \
      4
```

**Insertion order 4, 3, 2, 1:**
- Symmetrically:
```
    3
   / \
  2   4
 /
1
```

Different trees! Same set of keys, different AVL structures because the rotation choices depend on the order in which imbalances arise.

</details>

---

### R-11.8

Draw the AVL tree resulting from the insertion of an entry with key 52 into the AVL tree of Figure 11.13b.

The AVL tree in Figure 11.13b:
```
              62
            /    \
          44      78
         /  \    /  \
        17   50      88
            /  \
           48   54
```

<details><summary>Solution</summary>

**Step 1: Standard BST insert.** $52 > 44$ → right. $52 > 50$ → right. $52 < 54$ → left. Insert 52 as left child of 54.

```
              62
            /    \
          44      78
         /  \    /  \
        17   50      88
            /  \
           48   54
                /
               52
```

**Step 2: Check balance walking up.**
- 54: left height 1, right height 0. balance = 1. OK.
- 50: left height 1 (48), right height 2 (54-52). balance = $-1$. OK.
- 44: left height 1 (17), right height 3 (50-54-52). balance = $-2$. **Imbalanced.**

**Step 3: Trinode restructuring.** $z = 44$, $y = 50$ (taller child of 44), $x = 54$ (taller child of 50). Pattern: $z$-right, $y$-right → **right-right (RR)** case → **single left rotation at 44**.

After left rotation: 50 takes 44's place; 44 becomes 50's left child; 50's old left child 48 becomes 44's new right child.

```
              62
            /    \
          50      78
         /  \    /  \
        44   54     88
       /  \  /
      17  48 52
```

All nodes now have balance factor in $\{-1, 0, 1\}$. AVL property restored.

</details>

---

