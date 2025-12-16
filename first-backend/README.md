# FIRST Spring Boot Backend

This module provides a modern Spring Boot foundation for migrating the legacy FIRST Java EE components. It aligns with FINA Software Development General Standards and establishes the layers and tooling required to complete the migration.

## Features
- Spring Boot 3 (Java 17)
- Spring Data JPA repositories
- JWT-based Spring Security
- MapStruct mappers and DTO-based APIs
- Centralized exception handling
- OpenAPI documentation via springdoc

## Running locally
1. Ensure PostgreSQL is available and update the datasource configuration in `src/main/resources/application.yml`.
2. On first run the embedded `data.sql` seeds a demo admin user (`admin` / `password`) and required roles for the MVP UI.
2. From the `first-backend` directory run:
   ```bash
   mvn spring-boot:run
   ```

## Demo UI (MVP)
Plain HTML pages under `src/main/resources/static` exercise the FI registration workflow end-to-end using the REST APIs:

- `index.html`: authenticate and create a Phase 1 draft. Stores the current FI id for later steps.
- `phase1.html`: edit general info, managers, beneficiaries, and branches; submit Phase 1.
- `entities.html`: lightweight manager/beneficiary/branch maintenance that reuses general info from the current FI.
- `phase2.html`: open Phase 2 and add PSP/VASP licenses.
- `questionnaire.html`: capture obligatory/non-obligatory questionnaire answers.
- `review.html`: submit to controller, run Phase 1 review, and approve/decline final registration.

All pages call the live backend with JWT authentication. Launch the app and open `http://localhost:8080/index.html`, authenticate with the seeded admin user, and follow the links to progress through Phase 1, Phase 2, questionnaire submission, and controller decisions.

## Migration notes
- Entities, services, and controllers have been organized to reflect clean architecture boundaries so legacy EJB and REST logic can be ported feature-by-feature.
- DTOs and mappers ensure controllers remain free of persistence concerns.
- Security is JWT-based; existing user and role data can be migrated into the provided schema, updating `User` and `Role` definitions as legacy rules are mapped.
- Additional modules from the legacy system should be incrementally rewritten into the package structure under `net.fina.first`, preserving business rules while adopting Spring idioms.
