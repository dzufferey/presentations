# Hierarchical Verification (Assume-guarantee Reasoning)

Verifying systems composed of multiple compenents is computationally hard.
The state space grows exponentially in the number of compenents.
Therefore, the product state space is to large to be explored.

Here, we will see some techniques to do verification while avoiding building the product state space.


## Preliminaries

#### DFA

A _deterministic finite automaton_ (DFA) `M` is a 5-tuple `(Q, Σ, δ, q₀, F)` where

* `Q` is a finite set of states
* `Σ` is a finite alphabet (set of input symbols)
* `δ` is the transition function (`Q × Σ → Q`)
* `q₀` is the initial state (`q₀ ∈ Q`)
* `F` is the set of accepting states (`F ⊆ Q`)

Let `w = a₁ a₂ … a_n` be a word over the alphabet `Σ`.
The automaton `M` accepts `w` if there is a sequence of states, `r₀ r₁ … r_n`, such that:
* `r₀ = q₀`
* `r_{i+1} = δ(r_i, a_{i+1})` for i = 0, …, n−1
* `r_n ∈ F`


#### NFA

A _non-deterministic finite automaton_ (NFA) `M` is a 5-tuple `(Q, Σ, δ, q₀, F)` where

* `Q` is a finite set of states
* `Σ` is a finite alphabet (set of input symbols)
* `δ` is the transition function (`Q × Σ → 2^Q`)
* `q₀` is the initial state (`q₀ ∈ Q`)
* `F` is the set of accepting states (`F ⊆ Q`)

Let `w = a₁ a₂ … a_n` an be a word over the alphabet `Σ`.
The automaton `M` accepts `w` if there is a sequence of states, `r₀ r₁ … r_n`, such that:
* `r₀ = q₀`
* `r_{i+1} ∈ δ(r_i, a_{i+1})` for i = 0, …, n−1
* `r_n ∈ F`


#### Language

The _language_ of an automaton `M`, denoted `L(M)`, is the set of words accepted by `M`.

A language is _prefix-closed_ iff `w ∈ L` then `∀ w′ w″. w = w′w″ ⇒ w′ ∈ L`.
For a minimal DFA, this means that every state is accepting except some non-accepting sink state(s).
For a minimal NFA, all the state are accepting.


#### Parallel Composition

Given automaton `M₁` and `M₂`, the synchronized product `M₁ ∥ M₂` is the automaton `M` where:
* `Q = Q₁ × Q₂`
* `Σ = Σ₁ ∪ Σ₂`
* `δ` is the transition function
  - `δ((q₁,q₂), a) = (δ₁(q₁, a), δ₂(q₂, a))` if `a ∈ Σ₁` and `a ∈ Σ₂`
  - `δ((q₁,q₂), a) = (q₁, δ₂(q₂, a))` if `a ∉ Σ₁` and `a ∈ Σ₂`
  - `δ((q₁,q₂), a) = (δ₁(q₁, a), q₂))` if `a ∈ Σ₁` and `a ∉ Σ₂`
* `q₀ = (q₀₁,q₀₂)`
* `F = F₁ × F₂`

_Example._
```
                                              (a,d)
      0                   0                2 ↙  0  ↖ 1
→ (a) ⇄ (b)    ∥    → (c) ⇄ (d)     =   → (a,c) → (b,d)
      1                   2                1 ↖     ↙ 2
                                              (b,c)
```


#### Stuttering Closure and Projection

This assumes that the alphabets of both machines are the same.
If this is not the case, we can "equalize" the alphabets with the following operations

_stuttering closure_:
let `Σ₁` and `Σ₂` be two alphabets such that `Σ₁ ⊂ Σ₂`
* on word: given `w₁ ∈ Σ₁*` with `w₂ = a₁ a₂ ... a_n` we get `w₂ ∈ Σ₂*` with `w = (Σ₂∖Σ₁)* a₁ (Σ₂∖Σ₁)* a₂ (Σ₂∖Σ₁)* ... (Σ₂∖Σ₁)* a_n (Σ₂∖Σ₁)*`.
* on a language: apply to every word
* on automaton: add a self edge to every state for every element in `Σ₂∖Σ₁`

_projection_ ("dual" of stuttering):
let `Σ₁` and `Σ₂` be two alphabets such that `Σ₁ ⊂ Σ₂`
* on word: let `w₂ ∈ Σ₂*` remove every character from `Σ₂∖Σ₁`
* on language: apply transformation to every word
* on automaton: replace every edge a character in `Σ₂∖Σ₁` by an `ε` transition (you may get an NFA and need to determinize if DFA are required).

We write the stuttering closure for `Σ` `stutter(M, Σ)` and the projection as `proj(M, Σ)`.

Given `Σ₁ ⊂ Σ₂`, it is easy to see that `proj(stutter(L, Σ₂), Σ₁) = L` but `stutter(proj(L, Σ₁), Σ₂) ≠ L`.

_Example._
Given
```
      1
→ (a) ⇄ (b)
      0
```
The stuttering closure with `{0,1,2}` is
```
      1
→ (a) ⇄ (b)
   ↺  0  ↺
   2     2
```
The projection on `{0}` is
```
→ (a)
   ↺
   0
```


#### Refinement

Given two automata `P` and `I`,  `P` _refines_ `I`, written `P ≼ I`, iff `L(P) ⊆ L(I)` (language inclusion).

__Lemma (language of ∥).__
Given `M₁` and `M₂` with their respective alphabets `Σ₁` and `Σ₂`.
We have that `L(P ∥ Q) = stutter(L(P), Σ₁∪Σ₂) ∩ stutter(L(Q), Σ₁∪Σ₂)`.

__Lemma (monotonicity of ∥).__
If `P ⋞ Q` then `P ∥ R ≼ Q ∥ R`.


## Assume-Guarantee Rules

The fist and most common idea is to use the structure of the system and associate an interface/specification to each component.
Ideally, the interfaces are much smaller than the implementations.
Then we take the product of the interfaces which manageable. 

Assume that we have two components `P₁` and `P₂` and their interfaces are `I₁` and `I₂`.

Usually we use an AG rule in conjunction with the consequence rule:
```
A ⋞ B    B ≼ C
━━━━━━━━━━━━━━
    A ≼ C
```

To show a property `φ`: we show that `P₁ ∥ P₂ ≼ I₁ ∥ I₂` and `I₁ ∥ I₂ ≼ φ`.


### AG reasoning rule 0 (AG0)

```
P₁ ∥ I₂ ⋞ I₁    I₁ ∥ P₂ ≼ I₂
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      P₁ ∥ P₂ ≼ I₁ ∥ I₂
```

How is that sound??
There are circular dependencies.
We assume `I₂` to show the refinement of `I₁` and we assume `I₁` to show the refinement of `I₂`.

Indeed, this is not correct.
For instance, take `L(I₁) = L(I₂) = ∅` and `L(P₁ ∥ P₂) ≠ ∅`.

To fix that we need to be a bit more subtle about how the interfaces are defined.
In particular, we need to split the alphabet into _inputs_ and _outputs_, i.e., splitting the assumptions from the guarantees.


### Interface/Signatures

For each component/interface, we have:
* `inputs(P) ⊆ Σ`
* `outputs(P) ⊆ Σ`
* `inputs(P) ∩ outputs(P) = ∅` (input and outputs are disjoint)
* `stutter(proj(L(P), inputs(P) ∪ outputs(P)), Σ) = L(P)` (the variables which are neither input nor outputs are "don't care")

Given two automaton `P` and `Q`, we say that `P` implements the signature of `Q` (`implements(P, Q)`) iff `inputs(P) ⊆ inputs(Q) ∧ outputs(P) ⊆ outputs(Q)`.

Given two automaton `P` and `Q`, we sat that they are compatible (`compatible(P, Q)`) iff `outputs(P) ∩ outputs(Q) = ∅`.

Furthermore, we have that
* `inputs(P ∥ Q) = (inputs(P) ∪ inputs(Q)) ∖ (outputs(P) ∪ outputs(Q))`
* `outputs(P ∥ Q) = outputs(P) ∪ outputs(Q)`

#### Weakening

To use an interface as assumption in a sound way, we need to _weaken_ it.
Weakening by a symbol `a` means that it is always possible to do `a`.

In terms of languages, weakening by `a` means `∀ w ∈ L. w⋅a ∈ weaken(L, a)`

Weakening of an automaton for `a ∈ Σ`:
* Add a new "one step from dump" state: an accepting state with a `Σ` transition to the dump state.
* To every state without `a` transition, add a transition `a` to the "one step from dump" state.


_Example._
Words starting with `0` and alternating between `0` and `1`.
(Dump state and transition to the dump state are omitted.)
```
      0
→ (a) ⇄ (b)
      1
```
Weakening by `0` give the automaton:
```
      0     0
→ (a) ⇄ (b) → (c)
      1
```


### AG reasoning rule 1 (AG1)

```
compatible(I₁, I₂)
implements(P₁, I₁)  implements(P₂, I₂)
P₁ ∥ weaken(I₂, outputs(I₁)) ⋞ I₁
P₂ ∥ weaken(I₁, outputs(I₂)) ⋞ I₂
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        P₁ ∥ P₂ ≼ I₁ ∥ I₂
```


__Thm.__ The rule AG1 is sound.

_Proof._
By induction on the length of the trace:
* _base case_: `ε ∈ L(P₁ ∥ P₂)` and `ε ∈ L(I₁ ∥ I₂)` because we are dealing with prefix-closed languages.
* _induction step_: `w⋅a ∈ L(P₁ ∥ P₂)`.
  We have the following induction hypothesis:
  - `w ∈ L(P₁ ∥ P₂)`
  - `w ∈ L(I₁ ∥ I₂)`
  We need to show `w⋅a ∈ L(I₁ ∥ I₂)`.
  Let us case-split on
  - `a ∈ outputs(I₁)`:
    * Therefore, `w⋅a ∈ weaken(I₂, outputs(I₁), Σ)`
    * `w⋅a ∈ L(P₁ ∥ weaken(I₂, outputs(I₁))  ⇔  w⋅a ∈ L(P₁)`
    * from `P₁ ∥ weaken(I₂, outputs(I₁)) ⋞ I₁` we deduce `w⋅a ∈ L(I₁)`
    * therefore, `w⋅a ∈ L(weaken(I₁, outputs(I₂)))`
    * from `P₂ ∥ weaken(I₁, outputs(I₂)) ⋞ I₂` and the assumption `w⋅a ∈ L(P₁ ∥ P₂)` we deduce `w⋅a ∈ L(I₂)`
    * putting `w⋅a ∈ L(I₁)` and `w⋅a ∈ L(I₂)` together gives `w⋅a ∈ L(I₁ ∥ I₂)`
  - `a ∈ outputs(I₂)`:
    * same as previous case with 1 and 2 swapped
  - `a ∉ outputs(I₁) ∧ a ∈ outputs(I₂)`:
    * `a` is don't care therefore: `stutter(proj(L(I₁ ∥ I₂), Σ∖{a}), Σ) = L(I₁ ∥ I₂)`
    * because `w⋅a ∈ stutter(proj(L(I₁ ∥ I₂), Σ∖{a}), Σ)` we have `w⋅a ∈ L(I₁ ∥ I₂)`.


_Example._
Let us look at:
* `P₁` with `Σ = {0, 1}`, `inputs(P₁) = {0}`, `outputs(P₁) = {1}`
* `I₁` with `Σ = {0, 1}`, `inputs(I₁) = {0}`, `outputs(I₁) = {1}`
* `I₂` with `Σ = {0, 1}`, `inputs(I₂) = {1}`, `outputs(I₂) = {0}`
```
P₁                      I₁                      I₂
      0                       0                       0
→ (a) ⇄ (b)             → (a) ⇄ (b)             → (c) ⇄ (d)
      1  ↺                    1                    ↺  1
         0                                         1
```
If we build `P₁ ∥ weaken(I₂, outputs(I₁))` we get:
```
      0                 0
→ (a) ⇄ (b)   ∥   → (c) ⇄ (d)
      1  ↺           ↺  1
         0           1
```
The product gives:
```
        0
→ (a,a) ⇄ (b,b)
        1
```
which trivially refines `I₁`


The rule can be generalize to `n` components as follow:
```
compatible(I₁, I₂, …, I_n)
∀ i. implements(P_i, I_i)
∀ i. P_i ∥ ∏_{j≠i} weaken(I_j, outputs(I_i)) ⋞ I_i 
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
              ∏_i P_i ≼ ∏_i I_i
```


### AG Reasoning from the Specification (no Interfaces)

Sometime we would like to skip the interfaces altogether and directly use `φ`.

#### AG reasoning rule 2 (AG2)

__AG reasoning rule 2 (AG2)__
```
∀ a ∈ Σ. ∃ i. weaken(φ, a) ∥ P_i ≼ φ
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            ∏_i P_i ≼ φ
```


__Thm.__ The rule AG2 is sound.

_Proof._
By induction on the length of the trace:
* _base case_: `ε ∈ L(∏_i P_i)` and `ε ∈ φ` because we are dealing with prefix-closed languages.
* _induction step_: `w⋅a ∈ L(∏_i P_i)`.
  We have the following hypothesis:
  - `w ∈ L(∏_i P_i)`
  - `w ∈ φ`
  We need to show `w⋅a ∈ φ`:
  - By the definition of the weakening `w ∈ φ  ⇒  w⋅a ∈ weaken(φ, a)`.
  - Because `w⋅a ∈ L(∏_i P_i)`, we have that `∀ i. w⋅a ∈ L(P_i)`.
  - Therefore `∀ i. w⋅a ∈ (weaken(φ, a) ∩ L(P_i))` which is `∀ i. w⋅a ∈ L(weaken(φ, a) ∥ P_i)`
  - By the antecedent of AG2, we have that `w⋅a ∈ φ`.


_Remark._
AG2 works on any set of automaton.
It does not make a distinction between inputs and outputs.


_Example._
See the examples in [these slides](https://github.com/dzufferey/presentations/blob/master/2012_06_13_MSR_Cambridge/compositional_verification.pdf)
