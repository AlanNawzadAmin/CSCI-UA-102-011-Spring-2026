# Section 13: Graphs

Textbook: *Data Structures and Algorithms in Java*, 6th Edition, Chapter 14 (Exercises)

---

## Quick Refresh

### Definitions

A **graph** $G = (V, E)$ has a set of **vertices** $V$ and a set of **edges** $E$. An edge connects two vertices (its **endpoints**).

- **Directed** edge: ordered pair $(u, v)$ — from $u$ to $v$.
- **Undirected** edge: unordered pair $\{u, v\}$.
- **Simple** graph: no self-loops, no parallel edges.
- Let $n = |V|$, $m = |E|$.

**Degree:** number of incident edges. For directed graphs: **in-degree** and **out-degree**.

**Path:** sequence of vertices connected by edges. **Cycle:** path that starts and ends at the same vertex.

**Connected component:** maximal set of mutually reachable vertices.

### Basic facts

- $\sum_v \text{deg}(v) = 2m$ (undirected); $\sum_v \text{in-deg}(v) = \sum_v \text{out-deg}(v) = m$ (directed).
- Simple undirected graph: $m \leq \binom{n}{2} = O(n^2)$.
- Simple directed graph: $m \leq n(n-1) = O(n^2)$.

### Representations

We'll use this toy directed graph as a running example (4 vertices, 4 edges):

```
v1 ──→ v2
│      │
↓      ↓
v3 ──→ v4
```

Edges: $e_{12}=(v_1 \to v_2),\ e_{13}=(v_1 \to v_3),\ e_{24}=(v_2 \to v_4),\ e_{34}=(v_3 \to v_4)$.

| Structure | Space | `getEdge(u,v)` | `outgoingEdges(v)` | `insertEdge` |
|---|---|---|---|---|
| Edge list | $O(n + m)$ | $O(m)$ | $O(m)$ | $O(1)$ |
| Adjacency list | $O(n + m)$ | $O(\min(\deg u, \deg v))$ | $O(\deg v)$ | $O(1)$ |
| Adjacency map | $O(n + m)$ | $O(1)$ expected | $O(\deg v)$ | $O(1)$ expected |
| Adjacency matrix | $O(n^2)$ | $O(1)$ | $O(n)$ | $O(1)$ |

#### Edge list

Two flat lists. Each edge stores its endpoints. Vertices don't know which edges touch them.

```
vertices: [v1, v2, v3, v4]
edges:    [e12, e13, e24, e34]
```

- `getEdge(u,v)`: **$O(m)$** — must scan the whole edge list.
- `outgoingEdges(v)`: **$O(m)$** — scan all edges, filter by endpoint.
- `insertEdge`: **$O(1)$** — append to the list.

Great for space, terrible for queries. Useful when the algorithm iterates all edges (e.g., Kruskal's sorts all edges by weight).

#### Adjacency list

Each vertex stores a list of its incident edges.

```
v1 → [e12, e13]
v2 → [e24]
v3 → [e34]
v4 → []
```

- `getEdge(u,v)`: **$O(\min(\deg u, \deg v))$** — scan the shorter neighbor list.
- `outgoingEdges(v)`: **$O(\deg v)$** — return the list for $v$.
- `insertEdge`: **$O(1)$** — append to the endpoint's list.

Good for graph algorithms that walk the structure (DFS/BFS): finding neighbors is proportional to how many there are, not the whole graph.

#### Adjacency map

Same as adjacency list, but each vertex's collection is a **map** keyed by neighbor, not a list.

```
v1 → {v2: e12, v3: e13}
v2 → {v4: e24}
v3 → {v4: e34}
v4 → {}
```

- `getEdge(u,v)`: **$O(1)$ expected** — hash lookup in $u$'s map for key $v$.
- `outgoingEdges(v)`: **$O(\deg v)$** — iterate $v$'s map. (Still linear in the number of neighbors — you can't list $k$ things in less than $k$ time.)
- `insertEdge`: **$O(1)$ expected** — hash insert.

Upgrades `getEdge` from $O(\deg)$ to $O(1)$ expected. The default for most real-world graph libraries.

#### Adjacency matrix

An $n \times n$ grid. Cell $A[i][j]$ is the edge from $v_i$ to $v_j$ (or null).

|       | v1 | v2  | v3  | v4  |
|-------|----|-----|-----|-----|
| **v1** | −  | e12 | e13 | −   |
| **v2** | −  | −   | −   | e24 |
| **v3** | −  | −   | −   | e34 |
| **v4** | −  | −   | −   | −   |

- `getEdge(u,v)`: **$O(1)$** — direct array index.
- `outgoingEdges(v)`: **$O(n)$** — scan row $v$, most cells are probably null.
- `insertEdge`: **$O(1)$** — just set the cell.

Best for edge-existence queries, worst for space and for iterating a vertex's edges (can't skip empty cells).

#### Which to pick

| Question | Answer |
|---|---|
| Sparse graph ($m \ll n^2$)? | List or map, not matrix. |
| Need `getEdge(u,v)` a lot? | Map (expected $O(1)$) or matrix (worst-case $O(1)$). |
| DFS/BFS dominated? | List: tighter memory, better cache locality, no hash overhead. |
| Multigraph (parallel edges)? | List: map assumes one edge per neighbor pair. |
| Very dense + memory-tight + edges have no payload? | Matrix with 1-bit cells. |

### Graph API

```java
interface Graph<V, E> {                        // V = vertex element, E = edge element
    PositionList<Vertex<V>>   vertices();
    PositionList<Edge<E, V>>  edges();

    PositionList<Edge<E, V>>  incomingEdges(Vertex<V> v);
    PositionList<Edge<E, V>>  outgoingEdges(Vertex<V> v);

    Edge<E, V>   getEdge(Vertex<V> from, Vertex<V> to);
    Vertex<V>[]  endVertices(Edge<E, V> e);
    Vertex<V>    opposite(Vertex<V> v, Edge<E, V> e);

    void  insertVertex(V x);                   // returns void! see note below
    void  insertEdge(Vertex<V> from, Vertex<V> to, E x);
    void  removeVertex(Vertex<V> v);
    void  removeEdge(Edge<E, V> e);

    int  numVertices();  int  numEdges();
    int  outDegree(Vertex<V> v);  int  inDegree(Vertex<V> v);
}

interface Vertex<V> {
    V getElement();                            // returns the payload stored at this vertex
}

interface Edge<E, V> {                         // Edge<edgeElement, vertexElement>
    E           getElement();                  // payload stored on this edge
    Vertex<V>[] getEndpoints();                // [0] = from, [1] = to
}
```

**Concrete type:** `EdgeListGraph<V, E>` implements `Graph<V, E>`.

**`insertVertex` quirk.** It returns `void`, so you don't get the new `Vertex<V>` back. Workaround when you need a reference to the just-inserted vertex:

```java
graph.insertVertex(x);
Vertex<V> v = graph.vertices().last().getElement();
```

This works because `insertVertex` appends, and `vertices()` is a `PositionList` whose `.last()` returns the `Position` of the most recently added vertex.

### `EdgeListGraph` internals

`EdgeListGraph<V, E>` is the only concrete `Graph` implementation covered so far. Its state:

```java
DoublyLinkedList<Vertex<V>>   vertices;   // one flat list of all vertices
DoublyLinkedList<Edge<E, V>>  edges;      // one flat list of all edges
int n_vertices, n_edges;
```

Each edge is an `InnerEdge` that stores `element` + `endpoints[2]` (`[from, to]`). Vertices are `InnerVertex` objects that store only their element — **they do not remember which edges touch them.**

| Method | Cost | What it does |
|---|---|---|
| `insertVertex(x)` | $O(1)$ | append a new `InnerVertex(x)` to `vertices` |
| `insertEdge(u, v, x)` | $O(1)$ | append a new `InnerEdge(u, v, x)` to `edges` |
| `endVertices(e)`, `opposite(v, e)` | $O(1)$ | read the edge's stored `endpoints` |
| `numVertices()`, `numEdges()` | $O(1)$ | counter read |
| `outgoingEdges(v)` | $O(m)$ | scan `edges`, keep those with `endpoints[0] == v` |
| `incomingEdges(v)` | $O(m)$ | scan `edges`, keep those with `endpoints[1] == v` |
| `outDegree(v)`, `inDegree(v)` | $O(m)$ | call `outgoingEdges` / `incomingEdges` and return `.size()` |
| `getEdge(u, v)` | $O(m)$ | scan `edges` for a match on both endpoints |
| `removeEdge(e)` | $O(m)$ | walk `edges` to find `e`'s position |
| `removeVertex(v)` | $O(m + n)$ | scan `edges` (drop those touching `v`), then scan `vertices` |

Inserts are $O(1)$, anything else is $O(m)$, because there's no per-vertex bookkeeping for "edges touching $v$."


---

### R-14.1

Draw a simple undirected graph $G$ that has 12 vertices, 18 edges, and 3 connected components.

<details><summary>Solution</summary>

We need three disjoint simple subgraphs with vertex/edge counts summing to $(12, 18)$.

**Clean choice: three copies of $K_4$.** Each $K_4$ has 4 vertices and $\binom{4}{2} = 6$ edges, so $3 \times 4 = 12$ and $3 \times 6 = 18$. ✓

```
  Component 1       Component 2       Component 3
    1 —— 2            5 —— 6            9 —— 10
    |\   /|           |\   /|           |\   /|
    | \ / |           | \ / |           | \ / |
    |  X  |           |  X  |           |  X  |
    | / \ |           | / \ |           | / \ |
    |/   \|           |/   \|           |/   \|
    3 —— 4            7 —— 8           11 —— 12
```

Each $K_4$ has edges: $\{1{-}2, 1{-}3, 1{-}4, 2{-}3, 2{-}4, 3{-}4\}$ (and analogously for the other two).

**Sanity checks:**
- Simple: no self-loops, no duplicate edges. ✓
- 3 components: each $K_4$ is connected, and there are no edges between components. ✓

</details>

**Follow-up.** Write Java code to construct this graph using the `Graph<V, E>` interface.

<details><summary>Solution</summary>

Vertices carry `Integer` labels; edges carry nothing meaningful (`Object`, value `null`). Note: `Graph` is directed, so we add one directed edge per undirected edge — this is the standard trick when modeling an undirected graph with a directed API.

```java
static Graph<Integer, Object> buildK4Triple() {
    Graph<Integer, Object> g = new EdgeListGraph<>();
    Vertex<Integer>[] vs = new Vertex[12];

    // 12 vertices labeled 1..12
    for (int i = 0; i < 12; i++) {
        g.insertVertex(i + 1);
        vs[i] = g.vertices().last().getElement();   // grab the just-added vertex
    }

    // Three K_4 components: {1..4}, {5..8}, {9..12}
    for (int base = 0; base < 12; base += 4) {
        for (int i = 0; i < 4; i++) {
            for (int j = i + 1; j < 4; j++) {
                g.insertEdge(vs[base + i], vs[base + j], null);
            }
        }
    }
    return g;
}
```

Each $K_4$ contributes $\binom{4}{2} = 6$ edges via the nested `i < j` loop, giving $18$ total.

</details>

---
