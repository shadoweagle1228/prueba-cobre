# AI-DLC Project Router

This is an AI-DLC **greenfield** project. All methodology artifacts are in `aidlc-docs/`.

## Session Start

When the user's first message is a greeting or a general question about the project, respond with the welcome below and list the available commands. If the first message is a command or specific request (e.g., `/aidlc-elaborate` or "start Mob Elaboration"), skip the welcome and proceed directly. Only display this welcome ONCE per conversation. If the user asks about project status or where they left off, check `aidlc-docs/aidlc-state.md`.

This project follows the **AI Development Lifecycle (AI-DLC)** methodology, a structured approach to building software with AI assistance.

AI-DLC is described in *Reimagine, Don't Retrofit* by Ricardo González Vargas. This tool (aidlc-kit) provides the project scaffolding, prompt templates, and consistency checks to put the methodology into practice.

### How it works
- **Rituals** are the core workflows (Mob Elaboration, Mob Construction, and Code Elevation for brownfield). Each ritual has a prompt file that the AI reads and follows step by step.
- **Phases/Stages** break each ritual into checkpoints. The AI asks for your approval before moving to the next one.
- **Bolts** are the smallest units of deliverable work. Each bolt goes through domain modeling, design, code generation, and testing.
- **Completion actions** (consistency check, intent consolidation, bolt criteria, retrospective) are available between or after rituals for quality assurance.

### Your role
You decide what to build, approve plans, and validate outputs. The AI drives the workflow, asks clarifying questions, and generates artifacts. You stay in control at every checkpoint.

### Getting started
Ask for a ritual by name (e.g., "start Mob Elaboration") or type `/aidlc-help` to see available commands.

---
aidlc-kit is licensed under the Business Source License 1.1. Free to use for your projects. See LICENSE for details.

## Commands

When the user types a command (with or without the `/` prefix) or requests the action in natural language (e.g., "start Mob Elaboration"), read the corresponding file and follow its instructions.

| Command | Action | File |
|---------|--------|------|
| `/aidlc-elaborate` | Start Mob Elaboration | `aidlc-docs/prompts/mob-elaboration.md` |
| `/aidlc-construct` | Start Mob Construction | `aidlc-docs/prompts/mob-construction.md` |
| `/aidlc-check` | Run consistency check | `aidlc-docs/completion/consistency-check-prompt.md` |
| `/aidlc-consolidate` | Consolidate intent | `aidlc-docs/completion/intent-consolidation-prompt.md` |
| `/aidlc-criteria` | Check bolt completion | `aidlc-docs/completion/bolt-completion-criteria.md` |
| `/aidlc-retro` | Session retrospective | `aidlc-docs/retrospectives/session-retrospective.md` |
| `/aidlc-validate` | Re-validate elaboration plan | Run the Plan Validation Gate checklist from the elaboration prompt against current artifacts |
| `/aidlc-status` | Show project progress | Read `aidlc-docs/aidlc-state.md` and summarize |
| `/aidlc-navigate` | What should I do next? | Read state and suggest the next action |
| `/aidlc-help` | Show available commands | Display this table |

## Key Project Files

- **State**: `aidlc-docs/aidlc-state.md` — current phase/stage progress
- **Intent**: `aidlc-docs/intents/intent-primary.md` — what we're building
- **Decisions**: `aidlc-docs/decisions/decision-log.md` — architectural decisions
- **Intent Summaries**: `aidlc-docs/intent-summaries/` — context from previous intents
- **Audit Log**: `aidlc-docs/audit/audit-log.md` — session activity log
- **Standards**: `aidlc-docs/standards/` — content validation, error handling, and question format rules

## Rules

- Do NOT start any ritual without the user explicitly requesting it (via command or natural language).
- Always read the full prompt file before beginning a ritual.
- Follow the pre-flight checks defined in each prompt.
- Ask for user confirmation at phase/stage boundaries.
- For completion actions, read the prompt file and follow its instructions. Do not modify project artifacts unless the prompt explicitly says to.
- When the user asks what to do next, seems lost, or sends a message that is not a command or ritual request, check `aidlc-docs/aidlc-state.md` and suggest the next action using this mapping:

| Current State | Suggested Command | Reason |
|---------------|-------------------|--------|
| No state file or all fields blank | `/aidlc-elaborate` | Project just scaffolded, start with elaboration |
| Mode = brownfield, no Code Elevation output | `/aidlc-elevate` | Brownfield projects must elevate before elaborating |
| Code Elevation complete, no elaboration started | `/aidlc-elaborate` | Codebase understood, ready to plan |
| Mob Elaboration in progress (Phase 1–5) | `Resume current phase` | Continue where you left off |
| Mob Elaboration complete, no construction started | `/aidlc-validate then /aidlc-construct` | Validate plan, then build first bolt |
| Mob Construction in progress | `Resume current bolt` | Continue where you left off |
| Bolt complete | `/aidlc-criteria` | Verify bolt meets quality gates |
| All bolts complete | `/aidlc-consolidate` | Wrap up the intent |
| Intent consolidated | `/aidlc-retro then aidlc-kit archive` | Retrospective, then archive via CLI |
| Workspace idle (after archive) | `/aidlc-elaborate` | Start a new intent |

Always mention `/aidlc-navigate` for quick orientation and `/aidlc-help` to see all available commands. If the state doesn't match any row above, show the current state and ask the user what they'd like to do.
