Use `zio-knowledge` first. If endpoint behavior is involved, also use `zio-http-knowledge`.

Task:
{{TASK}}

Testing objectives:
- Prefer deterministic tests with clear setup and assertions.
- Cover happy path, error path, and interruption/cancellation where relevant.
- Keep tests readable and close to behavior specs.

Required steps:
1. Identify behavior to validate.
2. Propose test cases and edge cases before coding.
3. Implement tests in existing test style.
4. Run targeted tests (and compile if needed).
5. Report coverage gaps and next candidates.

Response format:
- Test plan checklist
- New/updated test files
- Why each test exists
- Test execution summary
- Remaining risks

