Use `zio-http-knowledge` first, and use `zio-knowledge` for core effect/stream/layer decisions.

Task:
{{TASK}}

Endpoint-specific requirements:
- Use ZIO HTTP endpoint/route patterns already used in this repo.
- Preserve existing middleware/logging/auth conventions.
- Keep endpoint contracts explicit (path, method, inputs, outputs, status codes).
- Update OpenAPI/Swagger-related wiring if needed.

Validation checklist:
1. Confirm route composition does not break existing endpoints.
2. Verify status codes and error handling behavior.
3. Add/update route tests (happy path + failure path).
4. Compile and run relevant tests.
5. Cite docs URLs for APIs used.

Output format:
- Plan checklist
- Endpoint contract summary
- Exact code edits
- Test changes
- Compile/test result summary

