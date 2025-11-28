# Module Kotlin Computational Geometry

## Usage Examples

Here are practical examples showing how to use KompGeom for common geometric tasks:

### Basic Vector Operations

```kotlin
// Create 2D points/vectors
val pointA = Vec2(3.0, 4.0)
val pointB = Vec2(6.0, 8.0)

// Distance calculations
val distance = pointA distance pointB

// Vector arithmetic
val sum = pointA + pointB
val difference = pointB - pointA
val scaled = pointA * 2.5
val negated = -pointA

// Vector properties
val magnitude = pointA.norm()
val unitVector = pointA.normalize()
val dotProduct = pointA dot pointB

// Linear interpolation between points
val midpoint = pointA.lerp(pointB, 0.5)

// Vector projection and rejection
val projection = pointA.project(pointB)
val rejection = pointA.reject(pointB)
```

### Affine Transformations

```kotlin
// Rotation matrix (45 degrees counterclockwise)
val cos45 = kotlin.math.cos(PI / 4)
val sin45 = kotlin.math.sin(PI / 4)
val rotation = AffineTransformationMatrix2(
    cos45, -sin45, 0.0,
    sin45,  cos45, 0.0
)

// Apply transformations
val point = Vec2(1.0, 1.0)
val rotated = rotation.apply(point)

// Get matrix inverse for reverse transformations
val inverse = translation.inverse()
val originalPoint = inverse.apply(translated)
```

### Calling Algorithms

```kotlin
// Create a dataset of points
val points = listOf(
    Vec2(0.0, 0.0),
    Vec2(1.0, 1.0),
    Vec2(2.0, 2.0),
    Vec2(5.0, 1.0),
    Vec2(10.0, 10.0),
    Vec2(3.0, 4.0),
    Vec2(7.0, 2.0)
)

// Create an algorithm instance and run it
val closestPairAlgo = ClosestPairDivideAndConquer(points)
val result = closestPairAlgo.run()
```
