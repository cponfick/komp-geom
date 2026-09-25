# CI/CD development cycle

This document describes the validation, release, and documentation workflows used by the project.

```mermaid
flowchart TD
    A[Developer opens or updates PR] --> B[PR validation]
    B --> C[Code style]
    B --> D[JVM test matrix\nJDK 17 / 21 / 25]
    B --> E[Platform test matrix\nJS, Wasm, Native]
    B --> F{Trusted PR?}
    F -->|Yes| G[Sonar coverage and quality analysis]
    F -->|Fork| H[Skip token-dependent Sonar upload]
    C --> I{Required PR checks pass?}
    D --> I
    E --> I
    G --> I
    H --> I
    I -->|No| A
    I -->|Yes| J[Merge to protected main]
    J --> K[Create protected stable or RC tag]
    K --> L[Validate exact tag and Gradle version]
    L --> M[Verify tag commit is reachable from main]
    M --> N[Release environment approval]
    N --> O[Build and publish signed Maven artifacts]
    O --> P[Deploy tag documentation to GitHub Pages]
    O --> Q[Trigger retryable documentation workflow]
    Q --> R[Build versioned docs from immutable tag]
    R --> S[Push <tag>-docs branch]
    S --> T{Documentation PR exists?}
    T -->|No| U[Create documentation PR against main]
    T -->|Yes| V[Reuse existing documentation PR]
    U --> W[Review and merge docs PR]
    V --> W
```

The Pages deployment and documentation PR workflow are independent after Maven publication, so documentation can be retried without republishing artifacts.
