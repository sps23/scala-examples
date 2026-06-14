Use `zio-knowledge` before writing code. If any zio-http integration is touched, also use `zio-http-knowledge`.

Task:
{{TASK}}

Stream-focused goals:
- Keep stream behavior explicit for throughput, backpressure, retry, and failure semantics.
- Avoid hidden side effects; use typed effects and clear logging.
- Preserve interruption safety and resource cleanup.

Review and implementation workflow:
1. Summarize current stream behavior.
2. Propose minimal change and expected runtime behavior.
3. Implement with clear failure handling.
4. Add/update tests for normal and failing scenarios.
5. Compile/tests for touched modules and report.

Include in response:
- Before/after behavior notes
- Risks (ordering, concurrency, memory, cancellation)
- Docs URLs used

