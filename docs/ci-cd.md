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
    O --> Q[On successful Publish: trigger Release Documentation]
    Q --> P[Build and deploy tag docs to GitHub Pages]
    Q --> R[Build versioned docs from release commit]
    R --> S[Push <tag>-docs branch]
    S --> T{Documentation PR exists?}
    T -->|No| U[Create documentation PR against main]
    T -->|Yes| V[Reuse existing documentation PR]
    U --> W[Review and merge docs PR]
    V --> W
```

After Maven publication succeeds, `Release Documentation` runs automatically. Its Pages deployment and versioned documentation PR are independent jobs: a failure in either does not require republishing Maven artifacts. Review and merge the `<tag>-docs` PR. If documentation fails, rerun failed jobs in Actions or manually run **Release Documentation** with the published release tag; do not rerun **Publish** to recover documentation. A manual run redeploys that tag as the site's current version, so use the latest intended release tag when retrying Pages. The repository needs GitHub Pages configured to publish via Actions, the `documentation` label, and permission for Actions to create pull requests.
