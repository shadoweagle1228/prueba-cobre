---
name: aidlc-navigate
description: >-
  Show where you are and what to do next. Use when the user seems lost, asks what's next, or wants orientation within a ritual.
---

Read the project state file (look for `*-state.md` in `aidlc-docs/`) and provide ritual-level orientation.

1. State your current position: which ritual, which phase/stage, what was last completed.
2. Match the current state to this table and suggest the next action:

| Current State | Next Action | Reason |
|---------------|-------------|--------|
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

3. List 2–3 other commands relevant to the current state.

Keep the response concise: 3–5 lines. This is orientation, not a status report.
