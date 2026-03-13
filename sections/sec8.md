# Section 8: Maps and Hash Maps

Textbook: *Data Structures and Algorithms in Java*, 6th Edition, Chapter 10 (Exercises)

---

## Quick Refresh: Maps and Hash Tables

### The Map ADT

A **map** (or dictionary) stores key-value pairs $(k, v)$ and supports the following core operations:

| Method | Description |
|---|---|
| `get(k)` | Return the value associated with key $k$, or null if no such entry |
| `put(k, v)` | Insert entry $(k, v)$; if key $k$ already exists, replace its value and return the old value |
| `remove(k)` | Remove the entry with key $k$ and return its value, or null if not found |
| `size()` | Return the number of entries |
| `isEmpty()` | Return whether the map is empty |
| `keySet()` | Return an iterable collection of all keys |
| `values()` | Return an iterable collection of all values |
| `entrySet()` | Return an iterable collection of all key-value entries |

### Hash Tables

A **hash table** implements a map by using a **hash function** $h(k)$ to map each key $k$ to an index in a bucket array of capacity $N$. The goal is $O(1)$ expected time for get, put, and remove.

**Two-step hashing:**
1. **Hash code:** map the key to an integer.
2. **Compression function:** map the integer to the range $[0, N-1]$ (e.g., $i \bmod N$).

### Hash Codes

A **hash code** maps a key to an integer. The key contract:
- **If two objects are equal (via `equals`), they must have the same hash code.**
- The converse need not hold: unequal objects *may* share a hash code (collision).

For **integers**, the value itself can serve as the hash code. For **strings** or **composite objects**, we need something smarter.

**Bad hash code, summing components:**
If we hash a string by summing character values, then `"abc"` and `"cba"` produce the same hash. Any permutation collides. This is terrible.

**Good hash code, polynomial hash code:**
For a sequence of components $(c_0, c_1, \ldots, c_{n-1})$, define:

$$h = c_0 \, a^{n-1} + c_1 \, a^{n-2} + \cdots + c_{n-2} \, a + c_{n-1}$$

for some constant $a \neq 0, 1$. This is evaluated efficiently with **Horner's method**:

$$h = c_{n-1} + a\,(c_{n-2} + a\,(c_{n-3} + \cdots + a\,(c_1 + a \cdot c_0)\cdots))$$

Equivalently, define $h_0 = c_0$ and $h_i = a \cdot h_{i-1} + c_i$ for $i = 1, \ldots, n-1$. Then $h = h_{n-1}$. This is $O(n)$ multiplications instead of computing each power separately.

**Why polynomial hashing works well:**

1. **Position-sensitive:** Each component $c_i$ is multiplied by a different power of $a$, so `"abc"` and `"cba"` hash differently. A sum-based hash has no way to distinguish ordering.

2. **Spreading effect:** Multiplying by $a$ at each step "shifts" the contribution of earlier components, spreading them across the full range of integers. This is similar to how a number in base $a$ uses each digit position to represent a different magnitude.

3. **Few collisions in practice:** Two distinct sequences $(c_0, \ldots, c_{n-1})$ and $(d_0, \ldots, d_{n-1})$ collide only when $\sum (c_i - d_i)\,a^{n-1-i} = 0$. This is a polynomial of degree $n-1$ in $a$, which has at most $n-1$ roots. So for any fixed pair of strings, at most $n-1$ values of $a$ cause a collision — out of all possible choices of $a$, the chance of collision is tiny.

4. **Choice of $a$:** Ideally exceed alphabet size. If the components $c_i$ take values in $\{0, 1, \ldots, d-1\}$ (alphabet size $d$), then choosing $a \geq d$ ensures that every distinct sequence of length $\leq n$ maps to a *unique* polynomial value (just like digits in base $a$ give unique numbers). If $a < d$, short sequences can collide trivially — e.g., with $a = 2$ and characters up to 127, the strings `"A"` (65) and `"!"` (33) with a trailing `"\0"` would interfere. In practice, primes slightly above $d$ work best. For ASCII ($d = 128$), $a = 31$ is borderline but works well empirically; $a = 37$ or $a = 41$ are safer choices.

**Java's `String.hashCode()`, a walkthrough:**

Java uses exactly the polynomial hash code with $a = 31$:

```java
// Equivalent to what Java does internally:
public int hashCode() {
    int h = 0;
    for (int i = 0; i < length(); i++) {
        h = 31 * h + charAt(i);    // Horner's method
    }
    return h;
}
```

For the string `"Cat"`:
- Start: $h = 0$
- `'C'` (= 67): $h = 31 \times 0 + 67 = 67$
- `'a'` (= 97): $h = 31 \times 67 + 97 = 2174$
- `'t'` (= 116): $h = 31 \times 2174 + 116 = 67510$

So `"Cat".hashCode()` returns 67510. Notice this equals $67 \times 31^2 + 97 \times 31 + 116$ — exactly the polynomial formula.

**Why 31?** It's an odd prime (avoids losing the low bit like multiplying by 32 would), and `31 * h` can be optimized by the JVM to `(h << 5) - h` (a shift and subtract, faster than multiplication). The tradeoff: 31 < 128 (ASCII size), so very short strings with certain characters *can* collide, but for typical string lengths this is not a problem in practice.

### Collision Handling

When two keys hash to the same index, we have a **collision**. Common strategies:

| Strategy | Description |
|---|---|
| **Separate chaining** | Each bucket stores a list (or small map) of all entries that hash there |
| **Linear probing** | On collision at index $h$, try $h+1, h+2, \ldots$ (mod $N$) |
| **Quadratic probing** | On collision at index $h$, try $h+1^2, h+2^2, h+3^2, \ldots$ (mod $N$) |
| **Double hashing** | On collision, use a secondary hash $h'(k)$ to determine the probe step: try $h+h', h+2h', \ldots$ (mod $N$) |

### Load Factor and Rehashing

The **load factor** is $\lambda = n / N$, where $n$ is the number of entries and $N$ is the table capacity.

- For open addressing (probing), we require $\lambda < 1$; typically we keep $\lambda \leq 0.5$.
- For separate chaining, $\lambda$ can exceed 1, but performance degrades as $\lambda$ grows.
- When $\lambda$ exceeds the threshold, **rehash**: allocate a larger table (typically $2N$) and re-insert all entries using the new capacity.

---


### R-10.4

Which of the hash table collision-handling schemes could tolerate a load factor above 1 and which could not?

<details><summary>Solution</summary>

**Separate chaining** can tolerate load factor $\lambda > 1$ because each bucket is a list that can hold multiple entries. As long as we can keep appending to the list, the number of entries can exceed the number of buckets.

**Linear probing, quadratic probing, and double hashing** (all open addressing schemes) **cannot** tolerate $\lambda > 1$ because each slot holds at most one entry. With $n$ entries and $N$ slots, we need $n \leq N$, so $\lambda \leq 1$. In practice, open addressing requires $\lambda < 1$ to function at all.

</details>



### R-10.6

Draw the 11-entry hash table that results from using the hash function $h(i) = (3i + 5) \bmod 11$, to hash the keys 12, 44, 13, 88, 23, 94, 11, 39, 20, 16, and 5, assuming collisions are handled by chaining.

- $h(12) = (36+5) \bmod 11 = 41 \bmod 11 = 8$
- $h(44) = (132+5) \bmod 11 = 137 \bmod 11 = 5$
- $h(13) = (39+5) \bmod 11 = 44 \bmod 11 = 0$
- $h(88) = (264+5) \bmod 11 = 269 \bmod 11 = 5$
- $h(23) = (69+5) \bmod 11 = 74 \bmod 11 = 8$
- $h(94) = (282+5) \bmod 11 = 287 \bmod 11 = 1$
- $h(11) = (33+5) \bmod 11 = 38 \bmod 11 = 5$
- $h(39) = (117+5) \bmod 11 = 122 \bmod 11 = 1$
- $h(20) = (60+5) \bmod 11 = 65 \bmod 11 = 10$
- $h(16) = (48+5) \bmod 11 = 53 \bmod 11 = 9$
- $h(5) = (15+5) \bmod 11 = 20 \bmod 11 = 9$

<details><summary>Solution</summary>

| Index | Chain |
|---|---|
| 0 | 13 |
| 1 | 94 → 39 |
| 2 | |
| 3 | |
| 4 | |
| 5 | 44 → 88 → 11 |
| 6 | |
| 7 | |
| 8 | 12 → 23 |
| 9 | 16 → 5 |
| 10 | 20 |

</details>


---

### R-10.7

What is the result of the previous exercise, assuming collisions are handled by linear probing?

<details><summary>Solution</summary>

Insert in order: 12, 44, 13, 88, 23, 94, 11, 39, 20, 16, 5

- 12 → $h=8$, slot 8 empty → place at 8
- 44 → $h=5$, slot 5 empty → place at 5
- 13 → $h=0$, slot 0 empty → place at 0
- 88 → $h=5$, occupied → try 6 → place at 6
- 23 → $h=8$, occupied → try 9 → place at 9
- 94 → $h=1$, slot 1 empty → place at 1
- 11 → $h=5$, occupied → 6 occupied → 7 → place at 7
- 39 → $h=1$, occupied → 2 → place at 2
- 20 → $h=10$, slot 10 empty → place at 10
- 16 → $h=9$, occupied → 10 occupied → 0 occupied → 1 occupied → 2 occupied → 3 → place at 3
- 5 → $h=9$, occupied → 10 occupied → 0 occupied → 1 occupied → 2 occupied → 3 occupied → 4 → place at 4

| Index | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 |
|---|---|---|---|---|---|---|---|---|---|---|---|
| Key | 13 | 94 | 39 | 16 | 5 | 44 | 88 | 11 | 12 | 23 | 20 |

</details>


---

### R-10.10

What is the worst-case time for putting $n$ entries in an initially empty hash table, with collisions resolved by chaining? What is the best case?

<details><summary>Solution</summary>

- **Worst case:** All $n$ keys hash to the same bucket. The $i$-th insertion must traverse a chain of length $i-1$. Total time: $O(1 + 2 + \cdots + n) = O(n^2)$.
- **Best case:** All $n$ keys hash to distinct buckets. Each insertion is $O(1)$. Total time: $O(n)$.

</details>

---

### R-10.12

Modify the Pair class from Code Fragment 2.17 on page 92 so that it provides a natural definition for both the `equals()` and `hashCode()` methods.

<details><summary>Solution</summary>

```java
public class Pair<A, B> {
    A first;
    B second;

    public boolean equals(Object other) {
        if (!(other instanceof Pair)) return false;
        Pair<?, ?> that = (Pair<?, ?>) other;
        return Objects.equals(this.first, that.first)
            && Objects.equals(this.second, that.second);
    }

    public int hashCode() {
        return 31 * first.hashCode() + second.hashCode();
    }
}
```

Key point: if two Pairs are equal, they must produce the same hash code. The `hashCode` uses polynomial hashing with $a = 31$, the same idea as `String.hashCode()`. Multiplying `first.hashCode()` by 31 makes it position-sensitive: `Pair("a", "b")` and `Pair("b", "a")` produce different hashes. For more fields, keep chaining: `31 * (31 * f1.hashCode() + f2.hashCode()) + f3.hashCode()`, this is Horner's method applied across fields.

</details>

---

### Follow-up: Triplet

Now do the same for a `Triplet<A, B, C>` class with fields `first`, `second`, and `third`.

<details><summary>Solution</summary>

```java
public class Triplet<A, B, C> {
    A first;
    B second;
    C third;

    public boolean equals(Object other) {
        if (!(other instanceof Triplet)) return false;
        Triplet<?, ?, ?> that = (Triplet<?, ?, ?>) other;
        return Objects.equals(this.first, that.first)
            && Objects.equals(this.second, that.second)
            && Objects.equals(this.third, that.third);
    }

    public int hashCode() {
        return 31 * (31 * first.hashCode() + second.hashCode())
            + third.hashCode();
    }
}
```

The `hashCode` is exactly Horner's method for three components: $31^2 \cdot h_1 + 31 \cdot h_2 + h_3$. This extends the Pair pattern naturally: nest one more level of `31 * (...) + next`.

</details>
