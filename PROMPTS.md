# Prompt Shortcuts

This file provides quick, copy-ready prompts for GitHub Copilot in this repository.

For full templates, see `.github/prompts/`.

## How to use

1. Pick a shortcut below.
2. Paste it into Copilot Chat.
3. Replace the task text after `TASK:`.

## Default ZIO

```text
Use `.github/prompts/zio-default.md`.
TASK: <your request>
```

## ZIO HTTP Endpoint

```text
Use `.github/prompts/zio-http-endpoint.md`.
TASK: <your request>
```

## ZIO Stream Review

```text
Use `.github/prompts/zio-stream-review.md`.
TASK: <your request>
```

## ZLayer Refactor

```text
Use `.github/prompts/zio-zlayer-refactor.md`.
TASK: <your request>
```

## Test Generation

```text
Use `.github/prompts/zio-test-generation.md`.
TASK: <your request>
```

## One-line strict quality gate

```text
After implementation, run a strict self-review for regressions, verify ZIO/ZIO HTTP types/imports, apply minimal fixes, and re-run compile/tests for touched modules.
```

## Example

```text
Use `.github/prompts/zio-http-endpoint.md`.
TASK: Add GET /version endpoint returning appName, scalaVersion, zioVersion as JSON. Add route tests and run zioExamples compile/test.
```

