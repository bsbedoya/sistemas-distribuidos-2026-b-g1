# HU-03 — MVP Cut 1
- [Overall status](./mvp.md)
- [Setup and development](./development.md)
- [QA](./qa.md)
- [Delivery](./release.md)
- Previous: [HU-02](../HU-02/README.md)
Conclusion: The deliverable for Cut 1 is the Angular + Spring Boot + PostgreSQL monolith located at `MVP/di-lucca-mvp`. Conversion to microservices is not part of this HU; will begin later using Strangler Fig.
## Purpose
HU-03 takes the scope defined in HU-02 and turns it into demonstrable deliverable. It's not enough for files to exist: each included flow must run, persist data, respect permissions, and leave evidence of proof.
## Phases
1. **Setup:** prepare variables, PostgreSQL and containers.
2. **Development:** only complete defects or gaps in prioritized capabilities.
3. **QA:** check frontend, backend, security, data and comprehensive journey.
4. **Delivery:** version only when the start is reproducible and the pending ones are known.
## Current condition
Static inspection confirms extensive implementation, but is not a substitute for functional execution. That is why this document differentiates “present in code” from “verified by QA”. The microservices phase is not used as Cut 1 acceptance criteria.
