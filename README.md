# FYP Hub — Final Year Project Management Platform

A desktop application for managing the complete lifecycle of Final Year Projects (FYPs) in a university setting. Built with **Java 21**, **JavaFX**, and **SQLite**.

> **AI-Assisted Development** — This project was developed with the assistance of **Claude AI**.

---

## Features

### Role-Based Dashboards
| Role | Capabilities |
|------|-------------|
| **Admin** | Register users, view all users, reset database |
| **Student** | Submit proposals, track milestones, view feedback |
| **Coordinator** | Assign supervisors, create projects, send to evaluation |
| **Supervisor** | Review proposals, approve/request revision with feedback |
| **Evaluator** | Grade projects, submit evaluation reports |

### Core Functionality
- **Plagiarism Detection** — Jaccard similarity algorithm checks proposal abstracts against existing submissions (20% threshold)
- **Proposal Lifecycle** — DRAFT → SUBMITTED → UNDER_REVIEW → APPROVED / REVISION_REQUIRED
- **Project Tracking** — Milestone management with real-time progress calculation
- **Evaluation System** — Weighted scoring across multiple evaluators
- **Persistent Storage** — SQLite database with auto-schema creation and seed data

### UI
- Dark theme with gradient accents
- Glassmorphism-inspired cards with hover effects
- Staggered entrance animations on all dashboards
- Slide-up login card animation

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 21 |
| UI Framework | JavaFX 21 |
| Database | SQLite (via JDBC) |
| Build Tool | Maven |
| Styling | JavaFX CSS |

---

## Project Structure

```
src/main/java/
├── app/
│   ├── Launcher.java          # Entry point (JavaFX module workaround)
│   └── Main.java              # Application init + DB setup
├── model/
│   ├── User.java              # Abstract base class
│   ├── Student.java
│   ├── Supervisor.java
│   ├── Coordinator.java
│   ├── Evaluator.java
│   ├── Admin.java
│   ├── Proposal.java
│   ├── Project.java
│   ├── Milestone.java
│   ├── Feedback.java
│   ├── EvalReport.java
│   ├── Role.java              # Enum
│   ├── ProposalStatus.java    # Enum
│   └── ProjectStatus.java     # Enum
├── repository/
│   ├── DatabaseManager.java   # SQLite connection, schema, seeding
│   └── DataStore.java         # All CRUD operations (Repository Pattern)
├── service/
│   ├── AuthService.java       # Login logic
│   ├── PlagiarismService.java # Jaccard similarity
│   ├── ProposalService.java   # Proposal workflow
│   ├── ProjectService.java    # Milestones + progress
│   └── EvaluationService.java # Grading + weighted scores
└── ui/
    ├── LoginView.java
    ├── ViewAnimations.java    # Fade, slide, stagger animations
    ├── admin/
    │   └── AdminDashboard.java
    ├── student/
    │   ├── StudentDashboard.java
    │   ├── ProposalForm.java
    │   ├── MilestoneView.java
    │   └── FeedbackView.java
    ├── coordinator/
    │   └── CoordinatorDashboard.java
    ├── supervisor/
    │   └── SupervisorDashboard.java
    └── evaluator/
        └── EvaluatorDashboard.java

src/main/resources/
└── style.css                  # Premium dark theme
```

---

## Database Schema

```
users          ← All roles (single-table inheritance)
proposals      ← Student submissions with plagiarism scores
projects       ← Created from approved proposals
feedbacks      ← Supervisor review comments
milestones     ← Project progress tracking
eval_reports   ← Evaluator grades
```

---

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.6+

### Run
```bash
# Clone the repo
git clone https://github.com/your-username/fyphub.git
cd fyphub

# Compile and run
mvn compile javafx:run
```

The SQLite database (`fyphub.db`) is auto-created on first run with seed data.

### Default Accounts

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@nu.edu.pk | admin123 |
| Student | student1@nu.edu.pk | 1234 |
| Student | student2@nu.edu.pk | 1234 |
| Coordinator | coord1@nu.edu.pk | 1234 |
| Supervisor | sup1@nu.edu.pk | 1234 |
| Supervisor | sup2@nu.edu.pk | 1234 |
| Evaluator | eval1@nu.edu.pk | 1234 |

---

## Workflow

```
Student ──submit──▶ Coordinator ──assign──▶ Supervisor
                                                │
                          ┌──── approve ────────┘
                          │         └── revision ──▶ Student (resubmit)
                          ▼
                    Coordinator ──create project──▶ Student (milestones)
                          │
                          ▼
                    Coordinator ──send to eval──▶ Evaluator ──grade──▶ COMPLETED
```

---

## Design Patterns Used

| Pattern | Where | Purpose |
|---------|-------|---------|
| **Repository** | `DataStore.java` | Abstracts all SQL behind clean methods |
| **Singleton** | `DatabaseManager.java` | Single shared DB connection |
| **Template Method** | `User.java` (abstract) | Common base with role-specific subclasses |
| **Strategy** (potential) | `PlagiarismService.java` | Algorithm could be swapped via interface |

---

## AI Disclosure

This project was developed with the assistance of **Claude AI** through the Antigravity IDE extension. The AI assisted with:
- Code generation for models, services, and UI components
- Database schema design and SQLite integration
- CSS theme design and JavaFX animations
- Refactoring (InMemoryStore → DataStore migration)
- Bug identification and architectural decisions

All AI-generated code was reviewed, tested, and modified by the developer.

---

## License

This project is for academic purposes (SDA — Software Design & Architecture, Semester 4).
