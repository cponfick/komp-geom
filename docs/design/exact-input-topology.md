# Design: Exact-Input Topology for Komp-Geom

**Status:** proposed; this document does not change existing behavior.
**Scope:** library-wide architecture, with 2D segment intersection as the first implementation.
**Baseline:** Komp-Geom 0.4.0.
**Motivation:** [precision-boundaries article](https://constantinsblog.eu/posts/precision-boundaries-komp-geom/) and [issue #184](https://github.com/cponfick/komp-geom/issues/184).
**Implementation tracking:** [umbrella issue #187](https://github.com/cponfick/komp-geom/issues/187), with implementation issues #188–#196.

## 1. Summary and proposed decision

Recommend **exact-input topology for finite `Double` coordinates** as the common contract for topology-sensitive operations. Interpret each input coordinate as the exact binary rational represented by that `Double`, not as an uncertain measurement. Retain exact constructions whenever their results participate in subsequent decisions. Establish a correct exact reference path first, then add certified floating-point filters with exact fallback.

Separate the semantic direction from the implementation and release decisions. Validate the complete contract with a small, explicitly opt-in segment-sweep prototype before committing to a production numeric representation, stabilizing the public handle API, or changing default behavior. The exact-only prototype is a correctness milestone, not a performance-ready implementation.

The essential rule is:

> Every decision that determines existence, membership, ordering, or connectivity must use one coherent geometric model. A rounded output coordinate is a view of a geometric object, not its identity or a certificate of incidence.

Implement this through a shared geometry kernel, not independent epsilon fixes in individual algorithms. The first proving ground is pairwise segment intersection and Bentley–Ottmann, including exact event/status comparisons and degeneracies. Validate Shamos–Hoey and polygon simplicity against the same kernel before releasing the intersection family as a coherent whole. Extend capabilities to other algorithm families only after the prototype passes correctness and performance gates; do not build a universal kernel or migrate the whole library upfront.

Keep `DoubleEquivalence` for explicit local approximate comparisons and metric/application policies. It is not a topology kernel. Fixed-grid geometry remains a separate, explicitly selected future model; it must not be simulated by increasing epsilon or rounding individual output points.

This is a semantic API change: some contacts currently accepted under tolerance become exact near-misses. It cannot preserve all current epsilon-based results and also promise exact-input topology.

## 2. Problem and current implementation

The article distinguishes three things that must not be conflated:

1. Arithmetic rounding error: an implementation issue that can invalidate a decision.
2. Application tolerance: a policy that deliberately treats nearby quantities as close.
3. Geometric identity: the transitive relation that defines which vertices and events are the same.

`EpsilonDoubleEquivalence` uses `|a - b| <= epsilon * max(1, |a|, |b|)`. This is useful locally, but closeness is non-transitive. Neither approximate equality nor a comparator derived from it can define vertex identity, event batches, or sorted-collection keys. Setting epsilon to zero also does not make arithmetic expressions exact.

The repository currently has several separate decision paths:

| Component | Current behavior relevant to topology |
| --- | --- |
| `core/shapes/Segment.kt` | `Segment2.intersection` uses tolerance-aware orientation and denominator tests, then constructs a `Vec2` with rounded arithmetic. `Segment3.intersection` uses tolerant degeneracy, parameter, and point comparisons. |
| `algorithms/intersection/BentleyOttmann.kt` | `SegmentSweep` shears rounded endpoints, orders `Double` event coordinates, computes rounded slopes/heights, and tests incidence after inverse shearing. Its position-based AVL status avoids a moving comparator, but geometric decisions remain mixed. |
| `algorithms/intersection/ShamosHoey.kt` | Shares `SegmentSweep`; polygon simplicity permits adjacent contacts by approximately comparing the reported point with a shared corner. |
| `euclidean/twod/Polygon2.kt` | Convexity and boundary checks use tolerance; ray crossings use rounded division and strict comparisons; orientation uses the sign of a rounded area. |
| `algorithms/convexhull/Quickhull2.kt` | Uses coordinate-based duplicate removal but normalized `Line2` constructions and approximate side/offset calculations to select hull vertices. |
| `euclidean/threed/Polygon3.kt` | Projects through a rounded orthonormal basis into `Polygon2`; exactness in a future 2D kernel alone would not establish exactness for the original 3D polygon. |

Issue #184 retains the ignored test `perturbed event heights agree with pairwise intersections` in `BentleyOttmannTest.kt`, using `Random(804)` and epsilon `1e-8`. It records a missing pair `(10, 14)` at iteration 8. This is evidence of disagreement, not a proof of one specific numerical failure mechanism.

The intended solution must make the entire classification–construction–ordering–incidence chain coherent. Replacing only orientation, only the tree comparator, or only the reported point is insufficient.

## 3. Goals and non-goals

### Goals

- Give pairwise and collection algorithms the same answer under the same model.
- Guarantee transitive geometric identity and consistent total ordering of event keys.
- Preserve exact incidence and connectivity through constructed objects and algorithm composition.
- Handle duplicates, zero-length segments, collinearity, overlaps, shared endpoints, and concurrency without generic-position assumptions.
- Work in `commonMain` on JVM, JS, Wasm, and Native, with deterministic decisions across targets.
- Preserve output-sensitive sweep behavior; do not repair missing results with a production all-pairs scan.
- Make the extension contract reusable: future algorithms add certified primitives rather than inventing their own numerical policies.
- Distinguish approximate reporting from exact classification in public APIs and documentation.

### Non-goals

- Recover unknown real-world coordinates from measurements or rounded inputs.
- Make every metric calculation, normal, centroid, or rendered coordinate exact.
- Guarantee preservation of topology after users export, round, and reimport coordinates.
- Implement grid reduction, snap rounding, all 3D predicates, or curved geometry in the first milestone.
- Claim that a shared kernel automatically proves an algorithm correct. Its combinatorial invariants and degeneracy handling still require review and tests.

## 4. Semantic contract

### 4.1 Input and model boundary

- Accept finite coordinates only in exact-topology entry points. Reject NaN and infinities consistently, even for trivial/empty algorithm paths containing invalid objects.
- Interpret normal and subnormal `Double` values exactly. Canonicalize `-0.0` and `+0.0` to the same geometric coordinate inside the kernel without changing general-purpose vector equality.
- Snapshot coordinates from mutable vectors/segments at the operation boundary. A queued event must not change because an input object is subsequently mutated.
- Exact topology refers to the coordinates at this boundary. A user-supplied rounded transformation produces new exact input doubles, not an exact transformation of the previous geometry.
- Use one model for an operation and for exact objects composed between operations. Reject incompatible model contexts rather than silently converting them.

### 4.2 Predicate and construction closure

A predicate returns a discrete result: sign, order, classification, or membership. It must equal the mathematical result for the represented geometry. A small nonzero determinant is not zero merely because it is small.

A construction creates a geometric object. If later predicates consume it, its exact value must remain available, either directly or through an immutable expression with exact evaluation. Provenance is useful for caching and endpoint reuse, but different construction histories may produce the same geometric point. Provenance alone is not identity.

For closed segments, the classification is exactly one of:

- `NONE`: the intersection is empty.
- `POINT`: the intersection is one exact point, including endpoint contacts and point-segment contacts.
- `OVERLAP`: the intersection is a positive-length closed segment; its endpoints are exact and canonically ordered.

Zero-length segments are points, not invalid inputs. Collinear overlap is classified by exact coordinate intervals without normalized directions or tolerance thresholds.

The exact point returned for a contact must belong to both operands. A public `Vec2` approximation need not satisfy an exact incidence test after reimport; this is unavoidable when the point is not representable by two doubles.

### 4.3 Identity and order

Separate three relations:

- **Geometric equality:** exact coordinates are equal; used for shared vertices and event grouping.
- **Occurrence identity:** stable input index or edge/vertex ID; distinguishes duplicate objects and supports pair reporting.
- **Approximate closeness:** explicit `eq` or tolerance query; never used for topology keys.

Use exact lexicographic point order. It is transitive, and comparison returns zero exactly when geometric points coincide. Stable occurrence IDs break ties between distinct coincident edges only after equality/overlap has been established. They must not split one geometric event into separate events.

Any exact hash-based representation must have equality-consistent hashing. The initial implementation may use exact ordered keys rather than introducing a coordinate interning scheme before canonical hashing is ready.

### 4.4 Composition and boundary ownership

Within one model, require these laws:

- Pairwise intersection is symmetric; reversing either segment preserves the geometric result.
- The sweep reports exactly the nonempty pairwise intersections, once per unordered pair of input occurrences.
- Shamos–Hoey returns true exactly when at least one such pair exists.
- Polygon simplicity rejects zero-length edges and nonconsecutive contacts. Adjacent edges may meet only at their exact shared corner, with no positive-length overlap or additional contact.
- Polygon boundary classification uses the same point-on-segment predicate as segment intersection. `contains` includes the boundary as it does today; a proposed `locate` result can distinguish inside, boundary, and outside.
- Internal kernel transformations preserve incidence exactly; approximate projection/export does not inherit this promise.

Algorithms may have different domain policies, such as which polygon contacts are allowed, but must apply those policies to the same exact geometric facts.

## 5. Architecture

### 5.1 Layers and responsibilities

Proposed package structure (names are provisional):

| Layer | Responsibility |
| --- | --- |
| `core/numeric/` | Internal arbitrary-precision integers, exact rationals, certified intervals/filters, and conversions. No geometry policy. |
| `core/topology/` | Model/context contracts, immutable exact point handles, identity rules, and explicit approximation boundaries. |
| `euclidean/twod/internal/kernel/` | 2D predicates and constructions, including operations on constructed points. |
| `euclidean/threed/internal/kernel/` | Later 3D capabilities following the same contract. |
| Algorithms and shapes | Combinatorial logic and documented domain policies, consuming kernel operations. |
| `core/equivalence/` | Existing approximate comparisons; deliberately independent of topology. |

Do not create a universal kernel interface containing every future operation. Use focused internal capabilities: orientation, segment classification, constructed-point comparison, sweep ordering, squared-distance comparison, and later incircle/3D predicates. An algorithm declares which capabilities it needs. An unsupported construction or predicate is a missing capability, not permission to fall back to unverified doubles.

Prefer one concrete exact-input implementation first. For the prototype, implement only orientation, point-on-segment, segment classification/construction, and the exact comparisons needed by the sweep. The package structure above is a growth direction, not a requirement to build all layers before testing issue #184. Generalize backend injection when a second coherent model exists; arbitrary caller-defined numeric equivalence must not masquerade as a certified kernel.

### 5.2 Exact numeric foundation

Every finite double is a dyadic rational. Decode its sign, significand, and binary exponent exactly using common Kotlin bit operations. Use an arbitrary-precision signed integer foundation to represent numerators and positive denominators. Normalize zero and ensure equality/order/hash conventions agree.

For straight-line geometry, rational arithmetic is closed under the required constructions: intersecting two rational lines gives rational coordinates. A homogeneous point `(X, Y, W)` with `W > 0` is a useful internal representation. Compare coordinates by exact cross multiplication, without first dividing into doubles. Equality must work across different homogeneous scalings and construction histories.

An internal rational Cartesian representation is also acceptable. Choose between it and homogeneous storage through implementation experiments, not by changing semantics. Canonical reduced rationals are the reference implementation and oracle; lazy/homogeneous forms may optimize it.

The first implementation must provide a correct exact path before adding filters. Evaluate a suitably licensed, maintained multiplatform integer dependency against a small internal implementation. No JVM-only `BigInteger` in production `commonMain`; a JVM implementation may help independently validate test fixtures. Fixed-width `Long` arithmetic alone cannot cover all finite double inputs or arbitrary constructed rationals.

Rationals do not cover every future construction. Circle intersections can require algebraic numbers; normalized directions introduce square roots. Such algorithms must add an exact algebraic capability, use an equivalent predicate that avoids the construction, or explicitly document a different guarantee. A rational kernel is a foundation, not a claim of universal numeric closure.

### 5.3 Certified fast paths

A filter computes a floating-point approximation and a proven enclosure/error bound for the actual expression. It may accept a sign or order only when the enclosure excludes zero. Otherwise it evaluates the exact expression. The bound is unrelated to application epsilon.

- Port and review established orientation/incircle techniques where applicable; preserve their arithmetic assumptions and license notices.
- Certify event-coordinate comparisons, segment-vs-event comparisons, and right-of-event order too. An `orient2d` filter on input doubles does not cover all higher-degree predicates involving intersections.
- For constructed points, filters must enclose the exact construction, not treat its rounded `Vec2` as exact input.
- Overflow, underflow, unsupported floating behavior, or an inconclusive interval triggers exact evaluation. Validate any rescaling; it must not discard significant bits.
- Do not assume availability of FMA, identical compiler contraction, or a platform rounding-mode API. Start with portable conservative filters; disable a fast path on targets where its proof assumptions cannot be established.
- Cache exact values/refinements immutably. Optimization must never change a predicate result.

Filters improve performance only. Disabling every filter must preserve all topology results.

### 5.4 Public exact objects and approximate views

Issue #184 could be repaired with internal exact events, but that alone would not solve future arrangement/DCEL composition. Design opaque immutable exact point/segment handles that can be consumed by kernel-aware operations without exporting doubles. Exercise these handles internally or through an explicitly experimental opt-in API during the prototype. Stabilize their public API only after correctness, composition, and performance evaluation; keep numeric storage internal.

Illustrative API shape, not a final declaration:

```kotlin
val context = ExactInputTopology2D()
val a = context.segment(firstSegment) // validates and snapshots coordinates
val b = context.segment(secondSegment)
val contact = context.intersection(a, b)

// A point contact retains an exact point handle.
// Its approximate Vec2 is an explicit reporting operation only.
val displayPoint = contact.pointOrNull()?.approximate()
```

The exact result should use mutually exclusive none/point/overlap variants, carrying handles and optional contact provenance. Existing `IntersectionData<Vec2>` remains a compatibility/reporting projection, not the internal representation. Its `type` can be exact even when its coordinates are approximate. Two distinct exact vertices may have the same approximate `Vec2`; exporting a plain coordinate list must not implicitly merge them.

Export finite rational coordinates with a specified deterministic rounding rule, preferably nearest binary64 with ties to even. Implement conversion without overflowing intermediate numerators/denominators. Segment intersection coordinates stay within finite endpoint bounds, but general line constructions may exceed binary64 range; their export must fail explicitly or return a documented conversion outcome, never silently substitute a topological point at infinity.

## 6. First proving ground: segment algorithms

### 6.1 Pairwise classification

Centralize orientation, exact coordinate bounds, point-on-segment, and segment classification in the 2D kernel. Handle point operands first. Use exact orientation signs for proper crossings and endpoint contacts, and exact interval intersection for collinear cases. Reuse an exact endpoint handle when the result equals that endpoint. Construct a rational intersection only for a unique nonparallel line intersection that lies on both closed segments.

Make denominator classification part of the same exact computation; a tolerance test must not contradict orientation. `computeIntersection` must have explicit preconditions or validation in its replacement API: unique segment intersection is different from supporting-line intersection.

### 6.2 Event coordinates and sweep status

Keep the position-based AVL order-maintenance tree. Replace every topology-relevant geometric calculation around it:

| Current operation | Required replacement |
| --- | --- |
| Rounded shear and inverse shear | Exact shear of exact point handles; retain original exact geometry. |
| Rounded event point comparison/batching | Exact lexicographic comparison/equality of endpoints and constructed intersections. |
| `height(x)` and `rankAt` | Certified comparison of an active edge with an exact event, without rounded division. |
| Epsilon height scan and `touches` | Exact incidence; locate and traverse the incident status block under proven ordering invariants. |
| Rounded slope sort | Exact direction comparison defining order immediately to the right of an event. |
| Rescheduled point from `IntersectionData<Vec2>` | Exact construction retained from kernel classification. |
| Reclassification for reporting | Same kernel and exact operands; approximate coordinates generated only when exporting results. |

An exact shear `x' = x + alpha * y` is viable. Select a deterministic integer `alpha` for which no nonpoint edge has zero transformed x extent, testing forbidden directions exactly. Generate/select candidates without an unbounded floating-point counter or a hidden quadratic preprocessing scan. Prove and document its cost. The shear may exceed double range internally; that is not a reason to reject finite inputs when exact storage can represent it.

At an event, collect all starts, ends, point segments, and active incident edges at the same exact point. Remove the affected active block, then insert continuing/starting edges in exact right-of-event order. Coincident directions require explicit overlap handling, not an invented slope tie that assumes separated curves. Discard or validate stale queued pair events without letting them introduce nonincident edges into an event batch.

### 6.3 Degeneracies and overlap policy

Exact arithmetic does not solve overlap enumeration by itself. Specify a sweep invariant for coincident active edges, such as a bundle of coincident supporting geometry with separate occurrence IDs and membership intervals. Enumerate every overlapping occurrence pair once; expose crossings with a bundle to all members that actually contain the crossing point. Bundle creation/splitting and neighbor scheduling require their own correctness and output-sensitive cost argument.

At an event with `m` incident occurrences, emitting `m * (m - 1) / 2` intersecting pairs is legitimate output work. Do not enumerate unrelated pairs outside such certified contact/overlap groups. Point segments participate in event contacts but never become persistent status edges.

Shamos–Hoey stops on the first disallowed exact contact. Its endpoint-only argument must be checked for ties, point segments, overlaps, and the polygon policy that suppresses allowed adjacent corner touches. If suppressed events can reorder the status, use a correct event-processing variant rather than retaining endpoint-only detection by assumption.

## 7. Extension to other algorithms

| Algorithm family | Required kernel capabilities and obligations |
| --- | --- |
| Convex hull | Exact duplicate identity, orientation, and determinant/distance ordering used for vertex selection. Quickhull should avoid normalized `Line2` for decisions and use deterministic tie handling for equal candidates. |
| Polygon containment/convexity/orientation | Shared exact boundary predicate, orientation-based ray/winding tests with half-open vertex ownership, and certified sign of the complete signed-area sum. Rounded area may still be reported as a metric. |
| Triangulation | Exact orientation and incircle; explicit cocircular/collinear policy. Symbolic tie-breaking may choose a deterministic triangulation, but must not erase true boundary incidence. |
| Overlay, arrangements, DCEL, Minkowski operations | Exact construction closure, canonical shared vertex identity, exact angular/edge order, and incidence-preserving graph assembly. Reuse handles across stages rather than round–reimport. |
| Closest pair and spatial pruning | Exact/certified squared-distance comparisons when claiming the true minimizing pair, plus conservative pruning bounds. An approximate displayed distance does not justify unsafe candidate rejection. |
| 3D segments and planar polygons | Exact collinearity/coplanarity, parameter bounds, and constructions. For planar polygons, validate the original plane exactly and use an injective coordinate-axis projection selected from an exact nonzero normal component, rather than a rounded orthonormal basis. |

Roll these out explicitly. Existing methods do not acquire an exact-topology guarantee merely because some dependencies have migrated. Model metadata must propagate through shape factories, `copy`, transforms, and algorithm results. Rounded user transformations establish a new input boundary; internal exact transforms retain closure.

For every future algorithm, require a design/review checklist:

1. Define its geometric model, domain, boundary rules, degeneracies, and deterministic tie policy.
2. List every branch, sort, identity lookup, selection, and pruning bound that depends on geometry.
3. Route those decisions through certified capabilities, including decisions on constructed objects.
4. Identify output approximation boundaries and composition requirements.
5. Test shared laws against independent exact fixtures/oracles and adversarial inputs.
6. State combinatorial and arithmetic costs separately; never hide recovery scans in complexity claims.

## 8. Compatibility and migration

Current constructors/methods accept `DoubleEquivalence`, and polygons store it as part of their API. Do not silently ignore an explicitly supplied tolerance or reinterpret it as an exact kernel.

Proposed staged migration before 1.0:

1. Add explicitly named, experimental opt-in exact-topology entry points for the segment prototype and composable results. Keep existing behavior and document its precision limitation; prototype availability alone is not a reason to deprecate current APIs.
2. Add paired exact adapters for segment methods and sweep constructors. Avoid overloads ambiguous with existing default arguments. Compare the current, exact-only, and filtered implementations before selecting production storage or stabilizing names.
3. After the correctness and performance gates in section 9 pass, stabilize the full intersection family together, including Shamos–Hoey, polygon simplicity, and validation policies. Deprecate precision-taking topology signatures with actionable replacements. Retain explicit local `eq`/near-contact APIs if needed, without sweep correctness guarantees.
4. Only after an explicit release decision, make migrated topology APIs exact by default in a documented breaking release and remove their `DoubleEquivalence` parameters. Shape-level tolerance properties may remain only for explicitly approximate/metric operations or be replaced in the same release with clearly separated settings. No default transition is implied by completing the exact-only prototype.
5. Migrate other families as their required capabilities and measured performance are ready. Publish a guarantee matrix so partially migrated 2D/3D APIs are not mistaken for one complete exact kernel.

Near-parallel but intersecting segments can now intersect even when the old denominator threshold rejected them. Tiny nonzero segments no longer collapse to points, and near-but-disjoint edges no longer connect. These changes need release notes and before/after examples, including polygon simplicity and boundary classification.

A future `FixedGrid` model must define origin, spacing, rounding/tie rule, coordinate range, collapse policy, and topology-preserving noding/snap rounding. Snapping endpoints independently is not sufficient. A grid is preprocessing plus its own coherent geometry semantics; grids can merge features or close gaps. It must not be implicitly selected by an epsilon parameter, and objects from different models must not be silently combined.

Do not implement grid reduction in the first milestone without a concrete application requiring a chosen resolution. Preserve the architectural path `original coordinates -> topology-preserving grid reduction -> robust algorithms`: the grid model deliberately changes geometry, and subsequent decisions must be correct for the reduced geometry. Bounded integer predicates may be efficient where range analysis proves no overflow, but snapping/noding and off-grid constructions still require a coherent policy. Grid geometry is a valid alternative, not a promise of correctness without computational cost or a silent fallback when exact-input performance is poor.

## 9. Validation and acceptance criteria

Put behavioral tests in `commonTest` and run them across supported targets. Shared-kernel differential tests are necessary for consistency but insufficient for correctness: all algorithms can agree on a buggy predicate.

### Numeric and kernel tests

- Exact decoding of zero, signed zero, subnormals, extreme finite exponents, and nearby binary64 values.
- Integer/rational normalization, arithmetic, equality, hashing, and comparator laws; compare distinct representations of the same value.
- Certified filters versus the exact reference path, including overflow/underflow cases and constructed rational operands. Force exact-only execution in tests.
- Independent exact fixtures with intersections such as `1/3`, cancellation-sensitive signs, and different segment pairs constructing one concurrent point.
- Approximation round-trip limitations, ties-to-even export, and distinct exact points sharing a `Vec2` view.
- Rejection of nonfinite inputs and stable snapshots of mutable inputs.

### Algorithm tests

- Retain the seed-804 coordinates and all 80 iterations, but change the differential oracle and sweep to the **same exact-input model** before removing `@Ignore`. Preserve a separate historical legacy test/fixture; do not assert that exact semantics must reproduce the old epsilon pair set.
- Extract small deterministic regressions from failing inputs, including the historical pair `(10, 14)`, with independently established exact classifications.
- Cover near-endpoint misses and contacts, vertical/near-vertical edges, almost parallel crossings, exact and near concurrency, duplicate/reversed segments, point segments, overlap chains/bundles, and equal event coordinates.
- Check pairwise symmetry, reversal, unique reporting, detector/reporter equivalence, allowed polygon contacts, and shared boundary classification.
- Permute input order and map occurrence IDs back before comparing. Check exact shears, axis swaps/reflections, and power-of-two scaling where the test transformation preserves input values exactly. Do not assume arbitrary rounded translation/scaling preserves the original topology.
- Check composition: build a later segment/graph using an exact intersection handle and verify incidence; separately show why exporting to `Vec2` loses that guarantee.
- Use an independent rational oracle for bounded randomized datasets and persisted hard fixtures. JVM-only tooling may generate fixtures, but their common tests must run without JVM dependencies.

### Performance and release gates

Compare three implementations on identical datasets: the current implementation as a performance baseline, the exact-only reference, and the filtered exact kernel. The baseline is not the correctness oracle, and differences caused by changed intersection semantics must be recorded alongside timings.

| Dataset | Evaluation purpose |
| --- | --- |
| Large sparse, well-separated segments | Fast-path overhead and preservation of sparse scaling. |
| Dense crossings | Construction, output conversion, and per-result storage costs. |
| Near-endpoint and almost parallel inputs | Exact fallback frequency and cost. |
| Exact concurrency and overlap chains | Degeneracy handling, bundle maintenance, and duplicate work. |
| Repeated construction/composition | Operand growth, expression retention, and cache lifetime. |

Track runtime, peak memory, allocations, certified fast-path acceptance, exact fallback frequency, operand bit sizes, event/status operations, and retained expression memory across JVM, JS, Wasm, and Native. Use operation-count regressions to detect accidental quadratic scans on sparse inputs; avoid fragile wall-clock unit tests.

Before promoting the prototype, define representative workloads and explicit runtime/memory budgets for the intended platforms. Record hardware/runtime versions, dataset sizes, output counts, benchmark methodology, and observed trade-offs. Do not choose an unsupported universal slowdown factor in advance.

Promotion to stable/default APIs requires:

1. Independent correctness expectations, exact-only/filtered parity, composition tests, and documented degeneracy invariants pass.
2. Sparse operation counts support the intended complexity and no production all-pairs repair is present.
3. Measured time and memory satisfy the agreed workload/platform budgets, or the maintainer explicitly accepts and documents revised budgets.
4. Numeric storage, cache lifetime, public handle design, and release migration are selected based on that evidence.

If performance is inadequate, keep the API opt-in and optimize filters, representations, caching, and combinatorial handling. Do not weaken exact decisions, increase epsilon, or silently switch to a grid to meet a benchmark. Broader algorithm migration waits for this evaluation.

Issue #184 is resolved only when the migrated APIs agree with independent exact expectations, the seed test is active under the new semantics, targeted degeneracy tests pass, and the event/status/overlap invariants and complexity assumptions are documented. No production all-pairs fallback, larger-epsilon workaround, or ignored adverse cases count as acceptance.

Run `./gradlew spotlessApply`, `./gradlew spotlessCheck`, and `./gradlew allTests`. Browser tests require Chrome/`CHROME_BIN`; CI must cover additional Native hosts. Cross-target topology decisions should match exactly, and approximation conversions should match the specified rounding contract.

## 10. Performance implications and complexity contract

### 10.1 Expected costs and optimization strategy

A general topology guarantee requires resolving uncertainty or restricting/changing the geometric model. This has a cost compared with unchecked arithmetic, but it does not imply that every operation or dataset must become slower. For example, a determinant predicate may avoid constructing and normalizing a line.

- **Ordinary inputs:** certified filters add error-bound arithmetic and branching, but can avoid exact evaluation for well-separated decisions. Acceptance rates for simple input orientation do not predict rates for higher-degree sweep comparisons on constructed points. No slowdown factor is assumed before measurement.
- **Difficult inputs:** inconclusive filters trigger arbitrary-precision operations with input-dependent cost. Nearly parallel or cancellation-sensitive cases can be substantially more expensive. Exact degeneracy itself is not necessarily costly; collinearity of small integer coordinates can be cheap to establish.
- **Constructions:** retaining an exact construction need not mean eagerly evaluating and reducing every rational. Lazy expressions/homogeneous representations may postpone work, provided all later decisions remain certified and exact-only parity holds.
- **Memory:** handles, construction expressions, exact operands, and cached approximations increase per-object storage and may create allocation/GC pressure. Share immutable constructions where useful, avoid unnecessary normalization, and bound cache/expression lifetimes so discarded sweep state does not retain an entire construction history.
- **Composition:** one sweep constructs intersections from input lines; repeated overlays or constructions on previous results can grow rational operands and retained expression graphs. Benchmark these separately rather than extrapolating from isolated segment tests.
- **Platforms:** integer arithmetic, allocation, and compiler behavior differ across Kotlin targets. Backend selection and portable filters require cross-target evidence, not JVM-only timings.

The optimization objective is to localize exact work while preserving the contract, not make every metric or reporting operation exact. Approximate output conversion remains outside the topology decision path. Fixed-grid geometry may offer different performance trade-offs for applications with a meaningful resolution, but is not an implementation substitute for exact-input semantics.

### 10.2 Complexity contract

State complexity in terms of input occurrence count `n` and number of reported unordered intersecting pairs `k`, including overlap and concurrent pairs, not merely distinct intersection points.

Target combinatorial bounds under unit-cost kernel operations:

- Reporting: `O((n + k) log(n + k))` time and `O(n + k)` retained storage, including output/events.
- Detection and polygon simplicity: `O(n log n)` time and `O(n)` storage, provided the degeneracy/contact policy is covered by the algorithm proof.

These are targets, not yet established for the revised overlap implementation. Preprocessing, bundle maintenance, duplicate work, and pending/stale event storage must fit the bound. Reconcile `getTimeComplexity()` metadata, KDoc, and benchmark descriptions; the existing reporting strings are not identical.

Exact arithmetic is not constant-cost. Separately document operand bit-size dependence, integer multiplication/comparison cost, rational reduction, filter/refinement cost, and construction-depth growth for composed algorithms. Do not present a combinatorial bound as an unconditional runtime guarantee for adversarial exact operands. Inputs are binary64, but intermediate rational sizes and later construction chains still matter.

## 11. Implementation sequence and open decisions

1. **Approve the contract and evaluation plan:** exact finite input, explicit approximation boundary, invalid input policy, opt-in rollout, representative workloads, and performance budgets. Do not approve a default switch or universal kernel at this stage.
2. **Build the minimal reference foundation:** multiplatform exact integers/rationals, conversion, experimental/internal handles, and law/fixture tests. The initial integer backend and its measured limitations are recorded in [exact-integer-backend.md](../benchmarks/exact-integer-backend.md); retain it as the reference backend while keeping optimized production representation open to measured alternatives.
3. **Implement exact pairwise geometry:** point/segment incidence, orientation, classification, and composable constructions, initially exact-only.
4. **Prove the complete chain with the sweep:** exact shear/events/status predicates and full degeneracy/overlap handling. Activate exact-semantics regressions and validate Shamos–Hoey/polygon contact policies using the same kernel.
5. **Add filters and evaluate:** compare current, exact-only, and filtered implementations across targets; check parity, costs, operand growth, and conversion behavior. Optimize while keeping the API opt-in until section 9's gates pass.
6. **Make an explicit promotion/release decision:** select production storage and stable public names based on evidence, then publish deprecations, guarantee matrix, release examples, and complexity wording. Change defaults only in the approved breaking release.
7. **Extend capabilities incrementally:** hull and polygon queries first; triangulation, graph assembly, and 3D operations as separately verified and benchmarked consumers. Add a grid model only when a concrete resolution-driven use case justifies it.

Open implementation choices must not reopen the core distinction between topology and tolerance:

- Which representative workloads and per-platform time/memory budgets should gate promotion from the opt-in prototype?
- Whether the internal [exact integer backend decision](../benchmarks/exact-integer-backend.md) continues to satisfy licensing, maintenance, JS/Wasm performance, and package-size requirements once those measurements are available?
- Are normalized rationals or lazy homogeneous constructions the better initial production representation?
- What public handle/result names fit existing shapes while keeping model mixing and accidental approximation difficult?
- Which overlap bundle formulation admits a clear output-sensitive proof for all supported degeneracies?
- Which portable filters provide meaningful benefit without relying on unsupported floating-point assumptions?
- What is the breaking release boundary, and how long should explicit legacy approximate methods remain?

Until these steps are complete, retain the documented precision limitation. This design is not a claim that the current sweep is robust.

## 12. Alternatives and references

### Alternatives considered

- **Increase epsilon:** can accept additional near contacts, but does not establish transitive identity or compatible event construction/order. Rejected as a general solution.
- **Strict comparison of rounded doubles:** gives a valid container order, not necessarily the correct geometric order. Useful for occurrence keys, insufficient for constructed topology.
- **Robust orientation only:** necessary for many algorithms, insufficient for sweeps/arrangements with constructed points and higher-degree comparisons.
- **Exact predicates, approximate constructions everywhere:** viable when constructions are reporting-only; rejected when rounded constructions feed later decisions.
- **All-pairs repair:** useful as a bounded test oracle, rejected in production because it abandons sparse sweep complexity.
- **Snap rounding/fixed grid:** legitimate alternative when users choose a resolution, not rejected on correctness grounds. Deferred until a concrete resolution-driven use case justifies its preprocessing and collapse policies. It can enable efficient bounded arithmetic, but changes the geometry and is not an epsilon-compatible implementation of exact input.
- **Global symbolic perturbation:** can regularize ordering, but changes treatment of true degeneracies unless carefully scoped. Use symbolic right-of-event reasoning and explicit tie policies without deleting actual contacts or overlaps.

### Sources

- [When Geometry Algorithms Disagree: Precision Boundaries in Komp-Geom](https://constantinsblog.eu/posts/precision-boundaries-komp-geom/): motivation and the proposed exact-input direction.
- [Komp-Geom #184](https://github.com/cponfick/komp-geom/issues/184): reproducer, cross-API scope, and complexity constraint.
- [Shewchuk, Adaptive Precision Floating-Point Arithmetic and Fast Robust Predicates](https://www.cs.cmu.edu/~quake/robust.html): certified/adaptive orientation and incircle; not a complete construction or sweep solution.
- [CGAL, Robustness Issues](https://doc.cgal.org/latest/Manual/devman_robustness.html) and [Geometry Kernels](https://doc.cgal.org/latest/Manual/devman_kernels.html): separation of predicates/constructions and operation-based kernel contracts.
- [Boissonnat and Preparata, Robust Plane Sweep for Intersecting Segments](https://doi.org/10.1137/S0097539797329373) ([preprint](https://inria.hal.science/inria-00073419/document)): sweep robustness involves more than input orientation predicates; consult when deriving event/status comparisons.
- [Apache Commons Geometry user guide](https://commons.apache.org/proper/commons-geometry/userguide/index.html): approximate comparison versus strict object equality; no claim of a robust sweep follows from that distinction.
- [JTS GeometryPrecisionReducer](https://locationtech.github.io/jts/javadoc/org/locationtech/jts/precision/GeometryPrecisionReducer.html): topological precision reduction and its deliberate changes to feature connectivity.
