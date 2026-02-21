# Contributing to KompGeom

Thank you for your interest in contributing to KompGeom! This guide will help you get started with contributing to our computational geometry library.

## Getting Started

## Project Structure

The project follows Kotlin Multiplatform conventions:

- `src/commonMain/kotlin/` - Shared source code for all platforms
- `src/commonTest/kotlin/` - Shared test code
- `src/commonMain/kotlin/io/github/cponfick/kompgeom/`
  - `algorithms/` - Computational geometry algorithms
  - `core/` - Core abstractions and utilities
  - `euclidean/` - Euclidean space implementations (1D, 2D, 3D)

## How to Contribute

### Reporting Issues

- Use the GitHub issue tracker to report bugs or request features
- Provide clear reproduction steps for bugs
- Include relevant platform information when applicable

### Coding Standards

- **Kotlin style**: Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- **Code formatting**: We use Spotless for automatic formatting. Run `./gradlew spotlessApply` before committing
- **Documentation**: Document public APIs with KDoc comments

### Testing Guidelines

- Write unit tests for all new functionality
- Place tests in `src/commonTest/kotlin/` with the same package structure
- Use descriptive test names that explain what is being tested
- Test edge cases and error conditions

### Adding New Algorithms

When contributing new geometric algorithms:

1. **Interface first**: Define appropriate interfaces in the `algorithms/` package
2. **Implementation**: Provide concrete implementations
3. **Tests**: Comprehensive test coverage including edge cases

### Platform Considerations

Since this is a multiplatform project:

- Use `expect`/`actual` declarations sparingly and only when necessary
- Prefer common Kotlin standard library functions
- Test on multiple platforms when possible

## Development Workflow

### Before Submitting

1. **Format your code:**
   ```bash
   ./gradlew spotlessApply
   ```
2. **Verify tests pass:**
   ```bash
   ./gradlew allTests
   ```
   This runs all tests available to your platform. The github actions will test a set of different platforms.

### Pull Request Guidelines

- Keep PRs focused on a single feature or fix
- Write clear, descriptive commit messages
- Update documentation if you're changing public APIs
- Add or update tests as needed
- Ensure CI checks pass

## API Stability

> **Note**: Until version 1.0.0, the API may change frequently. After 1.0.0, we follow semantic versioning.

When making API changes:
- Mark deprecated APIs with `@Deprecated` annotation
- Provide migration paths in deprecation messages
- Consider backward compatibility impact


## AI-Assisted Development

AI tools (Copilot, ChatGPT, Claude, etc.) are welcome as development aids — for code generation, refactoring suggestions, exploring ideas, and more. However, contributors are fully responsible for every line of code they submit. You must understand, review, and be able to explain all changes in your PR.

AI-generated code is held to the same quality standards as any other contribution: tests, formatting (`spotlessApply`), KDoc on public APIs, and adherence to the project's design patterns. Treat AI output as a starting point, not a finished product.

PRs that appear to be unreviewed AI output — such as hallucinated APIs, irrelevant or out-of-scope changes, or generic boilerplate that doesn't fit the codebase — will be closed.

## Getting Help

If you need help contributing:
- Check existing issues and documentation
- Open a discussion for design questions
- Reach out through GitHub issues for specific problems

Thank you for helping make KompGeom better! 🚀
