# Agent Instructions

This project is a **LibGDX Roguelike Game** with a layered architecture separating domain logic, presentation, and data persistence.

**Key Technologies**: Java 17, LibGDX, Gradle multi-module build (core + lwjgl3), Jackson JSON, Beads issue tracking

**Architecture Layers**:
- `domain/`: Game logic (entities, services) independent of rendering
- `presentation/` & `presentation_lanterna/`: UI rendering layers
- `datalayer/`: JSON persistence via Jackson

This project uses **bd** (beads) for issue tracking. Run `bd onboard` to get started.

## Build & Run

```bash
# Build and run the game (LWJGL3 desktop platform)
gradlew lwjgl3:run

# Build distribution jar
gradlew lwjgl3:jar

# Build all projects
gradlew build

# Clean and rebuild
gradlew clean build

# Compile only (without running)
gradlew compileJava
```

## Quick Reference (Beads)

```bash
bd ready              # Find available work
bd show <id>          # View issue details
bd update <id> --claim  # Claim work atomically
bd close <id>         # Complete work
bd dolt push          # Push beads data to remote
```

## Non-Interactive Shell Commands

**ALWAYS use non-interactive flags** with file operations to avoid hanging on confirmation prompts.

Shell commands like `cp`, `mv`, and `rm` may be aliased to include `-i` (interactive) mode on some systems, causing the agent to hang indefinitely waiting for y/n input.

**Use these forms instead:**
```bash
# Force overwrite without prompting
cp -f source dest           # NOT: cp source dest
mv -f source dest           # NOT: mv source dest
rm -f file                  # NOT: rm file

# For recursive operations
rm -rf directory            # NOT: rm -r directory
cp -rf source dest          # NOT: cp -r source dest
```

**Other commands that may prompt:**
- `scp` - use `-o BatchMode=yes` for non-interactive
- `ssh` - use `-o BatchMode=yes` to fail instead of prompting
- `apt-get` - use `-y` flag
- `brew` - use `HOMEBREW_NO_AUTO_UPDATE=1` env var

## Project Architecture

### Domain Layer (`domain/`)

Game logic independent of rendering or persistence. Contains:

- **Entities**: `Player`, `Enemy`, `Item`, `Character`, `Backpack` - define game objects with their properties
- **Services**: Game orchestration and business logic
  - `GameService`: Main game session and turn management
  - `CombatService`: Battle calculations and damage mechanics
  - `MovementService`: Character/enemy movement and collision
  - `EnemyAiService`: Enemy AI and pathfinding
  - `InventoryService`: Inventory management
- **Level**: Dungeon generation and structure (rooms, corridors)
- **enums/types**: `EnemyType`, `ItemType`, `ItemSubType`, `Direction`, etc.

### Data Layer (`datalayer/`)

JSON-based persistence using Jackson:
- `SaveService`: Handles game save/load operations
- Data classes: `PlayerSaveData`, `EnemySaveData`, `ItemSaveData`, `LevelSaveData`, `GameRecordSaveData`
- `JsonMapper`: Custom Jackson configuration for serialization/deserialization
- `Leaderboard`: High scores and completion records

### Presentation Layer (`presentation/`, `presentation_lanterna/`)

Rendering implementations:
- **LibGDX (`presentation/`)**: Primary rendering layer (when implemented)
- **Lanterna (`presentation_lanterna/`)**: Terminal-based UI for testing/alternative view
  - `MainRenderer`, `TurnRenderer`, `ActorsRenderer`, `FogRenderer`
  - `ColorScheme`: Terminal color configurations

### Module Structure

- **core**: Shared application logic (domain, presentation, datalayer)
- **lwjgl3**: Desktop launcher and platform-specific configuration

## Coding Conventions

### Architecture Rules

1. **Domain is UI-agnostic**: Domain classes use no LibGDX or Lanterna imports
2. **Services are stateless where possible**: Prefer dependency injection over singleton patterns
3. **Data flows one direction**: domain → presentation/datalayer, not reverse
4. **Serialization in datalayer**: All save/load logic is in `datalayer/`, not domain

### Naming & Structure

- **Services**: `*Service` suffix (e.g., `CombatService`, `MovementService`)
- **Entities**: Domain objects (e.g., `Player`, `Enemy`, `Item`)
- **Save data**: `*SaveData` suffix (e.g., `PlayerSaveData`)
- **Enums**: Uppercase enum values (e.g., `EnemyType.ZOMBIE`, `ItemType.WEAPON`)

### Testing

Tests are in `domain/unittest/` directory. Use simple assertions and avoid mocking.



<!-- BEGIN BEADS INTEGRATION v:1 profile:minimal hash:ca08a54f -->
## Beads Issue Tracker

This project uses **bd (beads)** for issue tracking. Run `bd prime` to see full workflow context and commands.

### Quick Reference

```bash
bd ready              # Find available work
bd show <id>          # View issue details
bd update <id> --claim  # Claim work
bd close <id>         # Complete work
```

### Rules

- Use `bd` for ALL task tracking — do NOT use TodoWrite, TaskCreate, or markdown TODO lists
- Run `bd prime` for detailed command reference and session close protocol
- Use `bd remember` for persistent knowledge — do NOT use MEMORY.md files

## Session Completion

**When ending a work session**, you MUST complete ALL steps below. Work is NOT complete until `git push` succeeds.

**MANDATORY WORKFLOW:**

1. **File issues for remaining work** - Create issues for anything that needs follow-up
2. **Run quality gates** (if code changed) - Tests, linters, builds
3. **Update issue status** - Close finished work, update in-progress items
4. **PUSH TO REMOTE** - This is MANDATORY:
   ```bash
   git pull --rebase
   bd dolt push
   git push
   git status  # MUST show "up to date with origin"
   ```
5. **Clean up** - Clear stashes, prune remote branches
6. **Verify** - All changes committed AND pushed
7. **Hand off** - Provide context for next session

**CRITICAL RULES:**
- Work is NOT complete until `git push` succeeds
- NEVER stop before pushing - that leaves work stranded locally
- NEVER say "ready to push when you are" - YOU must push
- If push fails, resolve and retry until it succeeds
<!-- END BEADS INTEGRATION -->
Use 'bd' for task tracking

<!-- br-agent-instructions-v1 -->

---

## Beads Workflow Integration

This project uses [beads_rust](https://github.com/Dicklesworthstone/beads_rust) (`br`/`bd`) for issue tracking. Issues are stored in `.beads/` and tracked in git.

### Essential Commands

```bash
# View ready issues (open, unblocked, not deferred)
br ready              # or: bd ready

# List and search
br list --status=open # All open issues
br show <id>          # Full issue details with dependencies
br search "keyword"   # Full-text search

# Create and update
br create --title="..." --description="..." --type=task --priority=2
br update <id> --status=in_progress
br close <id> --reason="Completed"
br close <id1> <id2>  # Close multiple issues at once

# Sync with git
br sync --flush-only  # Export DB to JSONL
br sync --status      # Check sync status
```

### Workflow Pattern

1. **Start**: Run `br ready` to find actionable work
2. **Claim**: Use `br update <id> --status=in_progress`
3. **Work**: Implement the task
4. **Complete**: Use `br close <id>`
5. **Sync**: Always run `br sync --flush-only` at session end

### Key Concepts

- **Dependencies**: Issues can block other issues. `br ready` shows only open, unblocked work.
- **Priority**: P0=critical, P1=high, P2=medium, P3=low, P4=backlog (use numbers 0-4, not words)
- **Types**: task, bug, feature, epic, chore, docs, question
- **Blocking**: `br dep add <issue> <depends-on>` to add dependencies

### Session Protocol

**Before ending any session, run this checklist:**

```bash
git status              # Check what changed
git add <files>         # Stage code changes
br sync --flush-only    # Export beads changes to JSONL
git commit -m "..."     # Commit everything
git push                # Push to remote
```

### Best Practices

- Check `br ready` at session start to find available work
- Update status as you work (in_progress → closed)
- Create new issues with `br create` when you discover tasks
- Use descriptive titles and set appropriate priority/type
- Always sync before ending session

<!-- end-br-agent-instructions -->
