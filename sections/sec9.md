# Section 9: Collision Resolution

Textbook: *Data Structures and Algorithms in Java*, 6th Edition, Chapter 10 (Exercises)

---

## Quick Refresh: Collision Resolution

### The Problem

A hash table maps keys to bucket indices via $h(k) \bmod N$. When two distinct keys $k_1 \neq k_2$ satisfy $h(k_1) = h(k_2)$, we have a **collision**. Every hash table needs a strategy for handling collisions.

### Separate Chaining

Each bucket $A[j]$ stores a secondary container (e.g., an unsorted list/map) holding all entries whose key hashes to $j$.

- **Insert:** compute $h(k)$, add $(k,v)$ to the list at $A[h(k)]$.
- **Search/Remove:** scan the list at $A[h(k)]$.
- **Load factor** $\lambda = n/N$ can exceed 1 (lists grow as needed).
- Expected bucket size is $\lambda$, so operations are $O(1)$ expected when $\lambda = O(1)$.

### Open Addressing

Instead of secondary containers, each slot holds at most one entry. On collision, we **probe** other slots according to a deterministic sequence.

**Linear probing:** On collision at index $j = h(k)$, try $j+1, j+2, \ldots$ (mod $N$).
- Simple, good cache performance.
- Suffers from **primary clustering:** long contiguous runs of occupied cells form and merge, causing long probe sequences.

**Quadratic probing:** On collision at $j = h(k)$, try $j + 1^2, j + 2^2, j + 3^2, \ldots$ (mod $N$).
- Avoids primary clustering.
- Suffers from **secondary clustering:** keys with the same hash follow the same probe sequence.
- Guaranteed to find an empty slot only if $N$ is prime and $\lambda < 0.5$.

**Double hashing:** On collision at $j = h(k)$, use a secondary hash $h'(k)$ and try $j + h', j + 2h', j + 3h', \ldots$ (mod $N$).
- Common choice: $h'(k) = q - (k \bmod q)$ for some prime $q < N$.
- $h'(k)$ must never be 0 (otherwise the probe sequence stays at $j$ forever).
- Avoids both primary and secondary clustering.

### Deletion in Open Addressing

Cannot simply empty a slot: it would break probe chains for keys inserted after it. Two approaches:

1. **Sentinel (AVAILABLE) marker:** Replace the deleted entry with a special marker. Search skips over it; insert can reuse it. Simple but markers accumulate and degrade performance.

2. **Shift-back:** After removing an entry, shift subsequent entries backward to fill the gap so that no probe chain is broken. Harder to implement but avoids markers entirely (see C-10.41).

### Load Factor Thresholds

| Strategy | Max $\lambda$ | Typical threshold |
|---|---|---|
| Separate chaining | $> 1$ ok | $\lambda < 0.75$ (Java default) |
| Open addressing | must be $< 1$ | $\lambda \leq 0.5$ |

When $\lambda$ exceeds the threshold, **rehash**: allocate a table of roughly $2N$ capacity and re-insert all entries.

---

### R-10.3

The use of null values in a map is problematic, as there is then no way to differentiate whether a null value returned by `get(k)` represents a legitimate value of an entry $(k, \text{null})$, or designates that key $k$ was not found. The `java.util.Map` interface includes a boolean method `containsKey(k)` that resolves any such ambiguity. Implement such a method for the `UnsortedTableMap` class and the `ChainHashMap` class.

For reference, `UnsortedTableMap` stores entries in an `ArrayList<MapEntry<K,V>>` called `table`, and its `get` method is:

```java
public V get(K key) {
    for (int j = 0; j < table.size(); j++)
        if (table.get(j).getKey().equals(key))
            return table.get(j).getValue();   // could be null if value is null
    return null;                               // also null if key not found
}
```

`ChainHashMap` stores an array of `UnsortedTableMap` buckets. Its `get` method hashes the key to find the right bucket, then delegates:

```java
public V get(K key) {
    int h = hashValue(key);
    UnsortedTableMap<K,V> bucket = table[h];
    if (bucket == null) return null;
    return bucket.get(key);              // delegates to UnsortedTableMap.get
}
```

<details><summary>Solution</summary>

**UnsortedTableMap** ($O(n)$): scan the whole list, check keys only.

```java
public boolean containsKey(K key) {
    for (int j = 0; j < table.size(); j++) {
        if (table.get(j).getKey().equals(key))
            return true;
    }
    return false;
}
```

**ChainHashMap** ($O(1)$ expected): hash once, check one bucket.

```java
public boolean containsKey(K key) {
    int h = hashValue(key);
    UnsortedTableMap<K,V> bucket = table[h];
    if (bucket == null) return false;
    return bucket.containsKey(key);     // uses the method above
}
```

The key insight: `get(k)` returning null is ambiguous (key absent vs. value is null), but `containsKey(k)` returns a boolean that is never ambiguous.

</details>

---

### C-10.33

Consider the goal of adding entry $(k, v)$ to a map only if there does not yet exist some other entry with key $k$. For a map $M$ (without null values), this might be accomplished as follows:

```java
if (M.get(k) == null)
    M.put(k, v);
```

While this accomplishes the goal, its efficiency is less than ideal, as time will be spent on the failed search during the get call, and again during the put call (which always begins by trying to locate an existing entry with the given key). To avoid this inefficiency, some map implementations support a custom method `putIfAbsent(k, v)` that accomplishes this goal. Implement `putIfAbsent` for both the `UnsortedTableMap` class and the `ChainHashMap` class.

For reference, `UnsortedTableMap.put` is:

```java
public V put(K key, V value) {
    for (int j = 0; j < table.size(); j++)
        if (table.get(j).getKey().equals(key)) {
            V old = table.get(j).getValue();
            table.get(j).setValue(value);
            return old;                        // key existed, return old value
        }
    table.add(new MapEntry<>(key, value));     // key not found, append
    return null;
}
```

And `ChainHashMap.put` delegates to the bucket:

```java
public V put(K key, V value) {
    int h = hashValue(key);
    UnsortedTableMap<K,V> bucket = table[h];
    if (bucket == null)
        bucket = table[h] = new UnsortedTableMap<>();
    int oldSize = bucket.size();
    V answer = bucket.put(key, value);         // delegates to UnsortedTableMap.put
    n += (bucket.size() - oldSize);            // update size if new entry was added
    return answer;
}
```

<details><summary>Solution</summary>

**UnsortedTableMap:** Single scan instead of two.

```java
public V putIfAbsent(K key, V value) {
    for (int j = 0; j < table.size(); j++) {
        if (table.get(j).getKey().equals(key))
            return table.get(j).getValue();   // key exists, return current value
    }
    table.add(new MapEntry<>(key, value));    // key not found, insert
    return null;
}
```

The naive `get` + `put` approach scans the list twice ($2 \times O(n)$). `putIfAbsent` does it in a single $O(n)$ pass.

**ChainHashMap:** Hash once, touch one bucket.

```java
public V putIfAbsent(K key, V value) {
    int h = hashValue(key);
    UnsortedTableMap<K,V> bucket = table[h];
    if (bucket == null)
        bucket = table[h] = new UnsortedTableMap<>();
    int oldSize = bucket.size();
    V answer = bucket.putIfAbsent(key, value);  // delegate to bucket
    n += (bucket.size() - oldSize);
    return answer;
}
```

For `ChainHashMap`, the naive approach hashes the key twice and searches the same bucket twice. `putIfAbsent` hashes once and searches the bucket once. Both are $O(1)$ expected, but `putIfAbsent` saves a constant factor.

</details>

---

### Linear vs. Quadratic Probing

Draw the hash table of size 11 that results from inserting keys 20, 31, 42, 53, 64, 75 (in this order) using $h(k) = k \bmod 11$, with:
1. Linear probing
2. Quadratic probing

Note that all six keys hash to the same slot: $h(k) = 9$ for each.

<details><summary>Solution</summary>

All keys hash to slot 9: $20 \bmod 11 = 9$, $31 \bmod 11 = 9$, $42 \bmod 11 = 9$, etc.

**Linear probing:** On collision at slot $j$, try $j+1, j+2, \ldots$

- 20 → slot 9 (empty) → **9**
- 31 → 9 occupied → 10 → **10**
- 42 → 9, 10 occupied → 0 → **0** (wraps around)
- 53 → 9, 10, 0 occupied → 1 → **1**
- 64 → 9, 10, 0, 1 occupied → 2 → **2**
- 75 → 9, 10, 0, 1, 2 occupied → 3 → **3**

| 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 |
|---|---|---|---|---|---|---|---|---|---|---|
| 42 | 53 | 64 | 75 | | | | | | 20 | 31 |

All six keys form one contiguous cluster wrapping from 9 through 3. This is **primary clustering** in action: every new key with the same hash must probe through the entire existing cluster.

**Quadratic probing:** On collision at slot $j$, try $j + 1, j + 4, j + 9, j + 16, \ldots$ (i.e., $j + i^2$ for $i = 1, 2, 3, \ldots$)

- 20 → slot 9 (empty) → **9**
- 31 → 9 occupied → $9+1=10$ → **10**
- 42 → 9, 10 occupied → $9+4=13 \bmod 11 = 2$ → **2**
- 53 → 9 occupied → $9+1=10$ occupied → $9+4=2$ occupied → $9+9=18 \bmod 11 = 7$ → **7**
- 64 → 9 occupied → 10 occupied → 2 occupied → 7 occupied → $9+16=25 \bmod 11 = 3$ → **3**
- 75 → 9 occupied → 10 occupied → 2 occupied → 7 occupied → 3 occupied → $9+25=34 \bmod 11 = 1$ → **1**

| 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 |
|---|---|---|---|---|---|---|---|---|---|---|
| | 75 | 42 | 64 | | | | 53 | | 20 | 31 |

The entries are **scattered** across the table instead of clustering together. However, keys with the same hash still follow the same probe sequence (secondary clustering): every key that hashes to 9 tries 9, 10, 2, 7, 3, 1 in that order.

**Key comparison:**
- Linear probing: cluster of 6 consecutive slots. The 6th insertion required 6 probes.
- Quadratic probing: entries spread out. The 6th insertion also required 6 probes (same hash = same sequence), but future keys with *different* hashes won't collide with this cluster as easily.

**Deletion and AVAILABLE markers:**

Using the linear probing table above, suppose we remove 31 (slot 10) by simply emptying it:

| 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 |
|---|---|---|---|---|---|---|---|---|---|---|
| 42 | 53 | 64 | 75 | | | | | | 20 | |

Now `get(42)` starts at $h(42) = 9$ (finds 20), probes 10 (empty!), and stops. It concludes 42 is not in the map, even though 42 is sitting at slot 0. The empty slot broke the probe chain.

The fix: instead of emptying slot 10, mark it **AVAILABLE**. This is a special sentinel that means "a key used to be here." The probing rules become:
- **Search** skips over AVAILABLE (keeps probing).
- **Insert** can reuse an AVAILABLE slot (but must keep probing first to check the key doesn't already exist further along).

| 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 |
|---|---|---|---|---|---|---|---|---|---|---|
| 42 | 53 | 64 | 75 | | | | | | 20 | A |

Now `get(42)` probes 9 (20), 10 (A, skip), 0 (42, found!). The probe chain is intact.

</details>

---

