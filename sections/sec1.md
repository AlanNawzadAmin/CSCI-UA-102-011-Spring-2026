# Java OOP Exercises

## R-2.10: Shallow Inheritance Trees

**Question:** What are some potential efficiency disadvantages of having very shallow inheritance trees, that is, a large set of classes, A, B, C, and so on, such that all of these classes extend a single class, Z?

<details>
<summary>Answer</summary>

A shallow tree structure looks like:

```
        Z
      / | \
     A  B  C  D  E  F  G  ...
```

**Disadvantages:**

1. **Code Duplication** — If A, B, and C share common behavior that D, E, F don't need, you can't put it in Z (because then everyone inherits it). So you end up copying code across multiple classes.

2. **Maintenance Nightmare** — When shared behavior needs to change, you have to find and update it in every class that duplicated it.

3. **Z Becomes Bloated** — Alternatively, you might stuff everything into Z to avoid duplication, making Z massive. Every subclass then inherits methods it doesn't need.

**Better Approach — Intermediate Classes:**

```
           Z
         /   \
       ABC   DEF
      / | \   | \
     A  B  C  D  E
```

Now shared code lives at the right level of abstraction.

</details>

---

## R-2.11: Method Output with Inheritance

**Question:** What is the output from calling the main() method of the Maryland class?

```java
public class Maryland extends State {
    Maryland() { /* null constructor */ }
    public void printMe() { System.out.println("Read it."); }
    
    public static void main(String[] args) {
        Region east = new State();
        State md = new Maryland();
        Object obj = new Place();
        Place usa = new Region();
        md.printMe();
        east.printMe();
        ((Place) obj).printMe();
        obj = md;
        ((Maryland) obj).printMe();
        obj = usa;
        ((Place) obj).printMe();
        usa = md;
        ((Place) usa).printMe();
    }
}

class State extends Region {
    State() { /* null constructor */ }
    public void printMe() { System.out.println("Ship it."); }
}

class Region extends Place {
    Region() { /* null constructor */ }
    public void printMe() { System.out.println("Box it."); }
}

class Place extends Object {
    Place() { /* null constructor */ }
    public void printMe() { System.out.println("Buy it."); }
}
```

<details>
<summary>Answer</summary>

**The Hierarchy:**

```
Object
  └── Place       → "Buy it."
        └── Region    → "Box it."
              └── State     → "Ship it."
                    └── Maryland  → "Read it."
```

**Key Concept:** Dynamic dispatch means the **actual object type** determines which method runs, not the declared variable type. Casting doesn't change the object.

**Line by Line:**

| Code | Actual Object | Output |
|------|---------------|--------|
| `md.printMe()` | Maryland | Read it. |
| `east.printMe()` | State | Ship it. |
| `((Place) obj).printMe()` | Place | Buy it. |
| `obj = md; ((Maryland) obj).printMe()` | Maryland | Read it. |
| `obj = usa; ((Place) obj).printMe()` | Region | Box it. |
| `usa = md; ((Place) usa).printMe()` | Maryland | Read it. |

**Output:**
```
Read it.
Ship it.
Buy it.
Read it.
Box it.
Read it.
```

</details>

---

## R-2.12: Class Inheritance Diagram

**Question:** Draw a class inheritance diagram for the following set of classes:
- Class Goat extends Object and adds an instance variable tail and methods milk() and jump().
- Class Pig extends Object and adds an instance variable nose and methods eat(food) and wallow().
- Class Horse extends Object and adds instance variables height and color, and methods run() and jump().
- Class Racer extends Horse and adds a method race().
- Class Equestrian extends Horse and adds instance variable weight and isTrained, and methods trot() and isTrained().

<details>
<summary>Answer</summary>

```
                        Object
                      /   |   \
                     /    |    \
                  Goat   Pig   Horse
                               /    \
                              /      \
                           Racer  Equestrian
```

**Details:**

```
Object
├── Goat
│   ├── Fields: tail
│   └── Methods: milk(), jump()
│
├── Pig
│   ├── Fields: nose
│   └── Methods: eat(food), wallow()
│
└── Horse
    ├── Fields: height, color
    ├── Methods: run(), jump()
    │
    ├── Racer
    │   └── Methods: race()
    │
    └── Equestrian
        ├── Fields: weight, isTrained
        └── Methods: trot(), isTrained()
```

**What Each Class Has Access To:**

| Class | Fields | Methods |
|-------|--------|---------|
| Goat | tail | milk(), jump() |
| Pig | nose | eat(food), wallow() |
| Horse | height, color | run(), jump() |
| Racer | height, color (inherited) | run(), jump() (inherited), race() |
| Equestrian | height, color (inherited), weight, isTrained | run(), jump() (inherited), trot(), isTrained() |

</details>

---

## R-2.13: Casting Between Sibling Classes

**Question:** Consider the inheritance of classes from Exercise R-2.12, and let d be an object variable of type Horse. If d refers to an actual object of type Equestrian, can it be cast to the class Racer? Why or why not?

<details>
<summary>Answer</summary>

**No, it cannot.**

```java
Horse d = new Equestrian();
Racer r = (Racer) d;  // ClassCastException at runtime!
```

**Why It Fails:**

Look at the hierarchy:

```
        Horse
       /     \
    Racer   Equestrian
```

`Racer` and `Equestrian` are **siblings**—they share a parent but have no inheritance relationship with each other. An `Equestrian` is not a `Racer`, and a `Racer` is not an `Equestrian`.

**What Happens:**

The compiler allows the cast (because it only checks if there's *any possibility* it could work—both are subtypes of `Horse`). But at runtime, Java checks the actual object type and throws a `ClassCastException`.

**The Rule:**

You can only cast to:
1. A superclass (always safe)
2. The actual type of the object
3. A superclass of the actual type

You cannot cast to a sibling or unrelated class.

</details>

---

## C-2.17: Unreachable Code Undetected by Compiler

**Question:** Write a short Java method that contains code for which it is provably impossible for that code to ever be executed, yet the Java compiler does not detect this fact.

<details>
<summary>Answer</summary>

**Example 1: Runtime Type Check**

```java
public void unreachable() {
    Object obj = new String("hello");
    
    if (obj instanceof Integer) {
        System.out.println("this never runs");
    }
}
```

The compiler sees `obj` as type `Object`, which *could* be an `Integer`. But we know it's always a `String`.

**Example 2: Always-False Method**

```java
public void unreachable() {
    if (alwaysFalse()) {
        System.out.println("this never runs");
    }
}

private boolean alwaysFalse() {
    return false;
}
```

The compiler doesn't trace through method calls to determine return values.

**Example 3: Math That's Always False**

```java
public void unreachable() {
    int x = 5;
    int y = 5;
    
    if (x + y > 100) {
        System.out.println("this never runs");
    }
}
```

**Why the Compiler Misses These:**

The compiler does basic reachability analysis:
- Code after `return`
- Code after `while(true)` with no break
- `if (false)` with literal constants

But it doesn't do **data flow analysis**:
- Doesn't track values through variables
- Doesn't evaluate arithmetic
- Doesn't trace through method calls
- Doesn't analyze runtime types

</details>

---

## C-2.21: Accessing Shadowed Variables Across Inheritance

**Question:** Write a program that consists of three classes, A, B, and C, such that B extends A and that C extends B. Each class should define an instance variable named "x". Describe a way for a method in C to access and set A's version of x to a given value, without changing B or C's version.

<details>
<summary>Answer</summary>

**Approach 1: Casting**

```java
class A {
    int x = 1;
}

class B extends A {
    int x = 2;
}

class C extends B {
    int x = 3;
    
    void setAx(int value) {
        ((A) this).x = value;  // cast to A, then access x
    }
    
    void printAll() {
        System.out.println("C's x: " + this.x);
        System.out.println("B's x: " + ((B) this).x);
        System.out.println("A's x: " + ((A) this).x);
    }
}
```

**Approach 2: Using Methods (Cleaner)**

```java
class A {
    int x = 1;
    
    void setX(int value) {
        this.x = value;
    }
    
    int getX() {
        return this.x;
    }
}

class B extends A {
    int x = 2;
}

class C extends B {
    int x = 3;
    
    void setAx(int value) {
        setX(value);  // calls inherited method from A
    }
    
    int getAx() {
        return getX();
    }
}
```

**Why This Works:**

When subclasses declare a variable with the same name, they **shadow** (hide) the parent's variable. All three `x` variables exist separately in memory.

Unlike methods (which use dynamic dispatch), **field access is resolved at compile time** based on the declared/cast type.

</details>

---

## C-2.24: Difference Progression

**Question:** Write a Java class that extends the Progression class so that each value in the progression is the absolute value of the difference between the previous two values. Include a default constructor that starts with 2 and 200 as the first two values and a parametric constructor that starts with a specified pair of numbers.

<details>
<summary>Answer</summary>

```java
/**
 * A progression where each value is the absolute difference
 * of the previous two values.
 * 
 * Example with default (2, 200):
 * 2, 200, 198, 2, 196, 194, 2, 192, ...
 */
public class DifferenceProgression extends Progression {
    
    protected long prev;  // store the previous value
    
    /**
     * Default constructor: starts with 2 and 200
     */
    public DifferenceProgression() {
        this(2, 200);
    }
    
    /**
     * Parametric constructor with specified first two values
     * @param first the first value
     * @param second the second value
     */
    public DifferenceProgression(long first, long second) {
        super(first);
        prev = second;  // store second value as "previous"
                        // (will be swapped on first advance)
    }
    
    @Override
    protected void advance() {
        long temp = prev;
        prev = current;
        current = Math.abs(temp - current);
    }
}
```

**How It Works:**

1. `current` holds the current value in the progression
2. `prev` holds the value before that
3. On each `advance()`:
   - Save `prev` temporarily
   - Move `current` into `prev`
   - Calculate new `current` as `|old_prev - old_current|`

**Example Sequence (2, 200):**

| Step | prev | current | Output |
|------|------|---------|--------|
| Start | 200 | 2 | 2 |
| advance() | 2 | 198 | 200 |
| advance() | 198 | 196 | 198 |
| advance() | 196 | 2 | 196 |
| ... | ... | ... | ... |

**Note:** This assumes a `Progression` base class similar to the one in the textbook with `current` field and `advance()` method.

</details>