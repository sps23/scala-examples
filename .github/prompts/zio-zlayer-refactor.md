Use `zio-knowledge` first.

Task:
{{TASK}}

ZLayer refactor constraints:
- Keep dependency graph explicit and minimal.
- Avoid unnecessary global layers.
- Preserve behavior while improving readability/testability.
- Call out any lifecycle/scope changes.

Execution plan:
1. Map current service dependencies and layer graph.
2. Identify redundant wiring and potential simplifications.
3. Refactor with minimal public API changes.
4. Add/update tests for wiring and behavior.
5. Compile/tests for touched modules.

Output must include:
- Old vs new dependency graph (short text form)
- Why the new layering is safer/clearer
- Files changed
- Verification results
- Remaining caveats

