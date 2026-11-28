# Software Testing Semester Project Report — Milestone 1

## Cover Information

- **Course:** Software Testing (2010031410)
- **Milestone:** Milestone 1 
- **Student names and IDs:** 
  - [Omar Jadallah / 2332725 / Its0mar]
  - [Sanad Silmi / 2332711 / silmisanad-jpg]
  - [Loiy Malaeb / 2434236 / loaymalaeb]
- **Repository URL:** https://github.com/Its0mar/BookingManagerTesting
- **Final commit hash:** `a426580`

---

## 1. System and Test Scope

The system under test is an in-memory **Ticket Booking System** focused purely on domain logic and test quality.

### Implemented Scope
- **`BookingRequest`**: Validates input boundaries (non-blank IDs, 1–5 tickets, positive price) and computes total cost (`ticketQuantity * pricePerTicketInCents`).
- **`BookingManager`**: Coordinates four core workflows:
  - **M1-US1 (Success)**: Verifies availability, processes payment, persists booking, sends confirmation, and returns the transaction ID.
  - **M1-US2 (Invalid Request)**: Rejects invalid inputs early by throwing `IllegalArgumentException` with zero external calls.
  - **M1-US3 (Sold Out)**: Halts immediately if an event is sold out without charging or saving.
  - **M1-US4 (Payment Failure)**: Handles payment declines safely without saving or notifying.
- **Test Doubles**: All collaborators (`IEventRepository`, `IPaymentGateway`, `INotificationService`) are mocked using Mockito.

### Intentionally Excluded Scope
- No GUI, web frontend, databases, REST APIs, or real third-party integrations (Stripe, SMTP).

---

## 2. Milestone 1: Design and Testability

### Design Overview
The system follows the **Single Responsibility** and **Dependency Inversion** principles. `BookingManager` acts strictly as a policy orchestrator and depends solely on three abstract interfaces:
- `IEventRepository`: Availability checks and persistence.
- `IPaymentGateway`: Financial transaction processing.
- `INotificationService`: Confirmation messaging.

### Dependency Injection and Testability
1. **Controllability**: Constructor Dependency Injection (DI) allows tests to substitute real systems with stubs and mocks, enabling effortless simulation of hard-to-reach edge cases (e.g., payment exceptions, sold-out events).
2. **Observability**: Key collaborator methods (`saveBooking`, `sendConfirmation`) return `void`. Constructor DI introduces an architectural seam, allowing Mockito to verify method invocations, execution order (`InOrder`), and inspect internal arguments using `ArgumentCaptor`.
3. **Trade-off**: Constructor DI requires callers to supply dependencies (slightly relaxing encapsulation), but achieves full component decoupling, modularity, and isolated unit testability.


## 3. Milestone 1: TDD Evidence

The development strictly followed the **Red–Green–Refactor** cycle for two core behaviors:

### Behavior 1: Invalid Request Validation (M1-US2)
* **Red (`612cd72`):** Added a `@ParameterizedTest` covering 12 invalid input cases (null/blank IDs, quantity outside 1–5, price $\le 0$). It failed because the stubbed `processBooking()` did not throw `IllegalArgumentException`.
* **Green (`cabe416`):** Added minimal validation logic (`if (!request.isValid()) throw ...`), satisfying all test cases immediately.
* **Refactor (`ba29af3`):** Extracted validation into a private `validateRequest()` helper method with clear, rule-specific exception messages while keeping tests green.

---

### Behavior 2: Successful Booking Workflow (M1-US1)
* **Red (`452cdfd`):** Added a workflow test asserting sequential execution (`isSoldOut` $\to$ `processPayment` $\to$ `saveBooking` $\to$ `sendConfirmation`). It failed because the method returned `null`.
* **Green (`aa1278d`):** Implemented minimal sequential orchestration across collaborators and returned `BookingResult.success(transactionId)`.
* **Refactor (`ceb6db6`):** Extracted `createBookingRecord()` to separate entity creation from workflow logic, and enhanced the test using Mockito's `ArgumentCaptor<Booking>` to verify persisted entity fields.

## 4. Limitation of Mocking

Mocks only test our assumptions about external dependencies rather than real third-party behavior. If the actual collaborator changes its return format, timing, or throws unhandled runtime exceptions, mocked unit tests will still pass ("false confidence") while the production system fails.

## 9. Team Contribution Matrix

| Student name and ID | Main responsibilities | Key files or report sections | Representative commit hashes | Contribution confirmed by team |
|---|---|---|---|---|
| [Omar Jadallah / 2332725] | Architecture design, Interfaces, M1-US2 TDD Cycle | Most of files, and the report | `8a13cbf`, `612cd72`, `cabe416`, `ba29af3` | Yes |
| [Loiy Malaeb / 2434236] | M1-US1 TDD Cycle, In-order Mockito Verification | `BookingManagerTest.java`, `BookingFactory.java` | `452cdfd`, `aa1278d`, `ceb6db6` | Yes |
| [Sanad Silmi / 2332711] | M1-US3 & M1-US4 | `BookingManagerTest.java` | `9928f7c`, `27e9664` | Yes |


## Appendix A: AI Use Log

| Date | Tool | Purpose and prompt summary | Files or sections affected | How the output was checked | Decision |
|---|---|---|---|---|---|
| 2026-11-22 | Antigravity AI | Reviewed project specification to clarify Milestone 1 requirements, scope boundaries, and grading rubric | `PROJECT_SPECIFICATION.md`, Report Sec 1 | Checked the specification on Moodle again | Accepted |
| 2026-11-22 | Antigravity AI | Assisted in designing the system architecture, domain models, and constructor dependency injection | `BookingManager.java`, service interfaces | Understanding the suggested design and comparing it of what is required | Accepted |
| 2026-11-28 | ChatGPT | Checked correctness of test and production code | `BookingManagerTest.java`, `BookingManager.java` | Tested | Modified & Accepted |
| 2026-11-28 | ChatGPT | Assisted in resolving environment configuration and Git remote/repository setup issues | Git configuration, `pom.xml`, build setup | Tested and worked | Accepted |

### Student Confirmation
We confirm that all AI-assisted suggestions were reviewed, independently verified against project requirements, and that our team understands and takes full responsibility for all submitted code, tests, and documentation.