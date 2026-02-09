# Data Structures and Algorithms in Java (6th Edition) — Selected Problems

### Background: Position and the PositionalList ADT

In an array-based list, elements are accessed by integer index. But indices are fragile — inserting or removing an element shifts the indices of everything after it. The **positional list** ADT solves this by replacing indices with **positions**: opaque tokens that remain valid even as the list is modified around them.

**The `Position<E>` interface** has a single method:

| Method | Description |
|---|---|
| `E getElement()` | Returns the element stored at this position |

A position is tied to a specific node in the list, not an index. It stays valid until the node is explicitly removed.

**The `PositionalList<E>` interface** (implemented by `LinkedPositionalList`):

```
  PositionalList with elements A, B, C:

    position p0      position p1      position p2
        │                 │                 │
        ▼                 ▼                 ▼
  ┌──────────┐     ┌──────────┐     ┌──────────┐
  │  node A  │ ←─▶ │  node B  │ ←─▶ │  node C  │
  └──────────┘     └──────────┘     └──────────┘

  first() → p0          after(p0) → p1         last() → p2
  before(p2) → p1       before(p1) → p0
```

| Method | Description | Time |
|---|---|---|
| `int size()` | Number of elements | O(1) |
| `boolean isEmpty()` | Whether the list is empty | O(1) |
| `Position<E> first()` | Position of the first element (or null) | O(1) |
| `Position<E> last()` | Position of the last element (or null) | O(1) |
| `Position<E> before(Position p)` | Position immediately before `p` (or null) | O(1) |
| `Position<E> after(Position p)` | Position immediately after `p` (or null) | O(1) |
| `Position<E> addFirst(E e)` | Insert `e` at the front; return its position | O(1) |
| `Position<E> addLast(E e)` | Insert `e` at the back; return its position | O(1) |
| `Position<E> addBefore(Position p, E e)` | Insert `e` just before `p`; return its position | O(1) |
| `Position<E> addAfter(Position p, E e)` | Insert `e` just after `p`; return its position | O(1) |
| `E set(Position p, E e)` | Replace the element at `p`; return the old element | O(1) |
| `E remove(Position p)` | Remove and return the element at `p` | O(1) |

All times assume the `LinkedPositionalList` implementation, which is a doubly linked list with header/trailer sentinels — the same structure from Chapter 3, but wrapped in the positional abstraction. The key insight is that a `Position` directly wraps a node, so once you have a position, you never need to traverse to find it.

---

### R-7.11
Describe an implementation of the positional list methods `addLast` and `addBefore` realized by using only methods in the set {`isEmpty`, `first`, `last`, `before`, `after`, `addAfter`, `addFirst`}.

<details><summary>Solution</summary>

**`addLast(e)`:**
```
if isEmpty() then
    return addFirst(e)
else
    return addAfter(last(), e)
```
If the list is empty, `addFirst` handles it. Otherwise, adding after the current last position places the new element at the end.

**`addBefore(p, e)`:**
```
if p == first() then
    return addFirst(e)
else
    return addAfter(before(p), e)
```
If `p` is the first position, we use `addFirst`. Otherwise, we find the position just before `p` (using `before(p)`) and insert after it, which places the new element immediately before `p`.

</details>

---


### C-7.46
Modify the `LinkedPositionalList` class to support a method `swap(p, q)` that causes the underlying nodes referenced by positions `p` and `q` to be exchanged for each other. Relink the existing nodes; do not create any new nodes.

<details><summary>Solution</summary>

There are two approaches. The simplest correct one handles the special case where `p` and `q` are adjacent:

```java
public void swap(Position<E> posP, Position<E> posQ) {
    Node<E> p = validate(posP);
    Node<E> q = validate(posQ);
    if (p == q) return;

    Node<E> pPrev = p.getPrev();
    Node<E> pNext = p.getNext();
    Node<E> qPrev = q.getPrev();
    Node<E> qNext = q.getNext();

    if (pNext == q) {
        // p and q are adjacent: p → q
        pPrev.setNext(q);
        q.setPrev(pPrev);
        q.setNext(p);
        p.setPrev(q);
        p.setNext(qNext);
        qNext.setPrev(p);
    } else if (qNext == p) {
        // q and p are adjacent: q → p  (symmetric case)
        qPrev.setNext(p);
        p.setPrev(qPrev);
        p.setNext(q);
        q.setPrev(p);
        q.setNext(pNext);
        pNext.setPrev(q);
    } else {
        // p and q are not adjacent
        pPrev.setNext(q);
        q.setPrev(pPrev);
        q.setNext(pNext);
        pNext.setPrev(q);

        qPrev.setNext(p);
        p.setPrev(qPrev);
        p.setNext(qNext);
        qNext.setPrev(p);
    }
    // size is unchanged; no nodes created or destroyed
}
```

The adjacent case must be handled separately because, for example, if `p.next == q`, then `q.prev == p`, and naively relinking all four neighbors would create a cycle. Runs in **O(1)** time.

</details>
