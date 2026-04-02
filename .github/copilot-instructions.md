# Copilot Instructions for LibGDX Roguelike

## Build & Run Commands

**Build and run the game:**
```bash
gradlew lwjgl3:run
```

**Build distribution JAR:**
```bash
gradlew lwjgl3:jar
```

**Full build (all modules):**
```bash
gradlew build
```

**Clean rebuild:**
```bash
gradlew clean build
```

**Compile without running:**
```bash
gradlew compileJava
```

**Generate asset list (run automatically with build):**
```bash
gradlew generateAssetList
```

## Architecture Overview

This is a **Java 17 LibGDX roguelike game** with a layered, modular architecture:

### Layers

- **Domain** (`domain/`): Pure game logic (entities, services, level generation). **No rendering or persistence imports**. This is the business logic core.
- **Data Layer** (`datalayer/`): JSON persistence using Jackson. Handles save/load operations via `SaveService`.
- **Presentation** (`presentation/`, `presentation_lanterna/`): UI rendering. Lanterna for terminal-based UI (testing/alternative view).

### Module Structure

- **core**: Shared domain, presentation, and data layer logic
- **lwjgl3**: Desktop launcher and platform-specific entry point

### Key Domain Components

- **Entities**: `Player`, `Enemy`, `Item`, `Character`, `Backpack`
- **Services** (stateless, dependency-injected):
  - `GameService`: Main session and turn management
  - `CombatService`: Battle mechanics and damage calculations
  - `MovementService`: Character/enemy movement and collision detection
  - `EnemyAiService`: Enemy AI and pathfinding
  - `InventoryService`: Inventory management
  - `GameSessionGeneration`: Session setup
- **Level**: `LevelGeneration`, `Level`, `Room`, `Passage`, `Tile`, `Coordinates`
- **Enums**: `EnemyType`, `ItemType`, `ItemSubType`, `Direction`, `SpaceType`

### Data Layer Components

- `SaveService`: Load/save game state to JSON files
- `JsonMapper`: Jackson configuration for serialization
- Data classes: `PlayerSaveData`, `EnemySaveData`, `ItemSaveData`, `LevelSaveData`, `GameRecordSaveData`
- `Leaderboard`: High scores and game records

## Coding Conventions

### Architecture Rules (Strict)

1. **Domain is UI-agnostic**: No LibGDX or Lanterna imports in `domain/` package
2. **Data flows one direction**: `domain` → `datalayer`/`presentation`, never reverse
3. **Serialization isolated**: All save/load logic lives in `datalayer/`, not domain
4. **Services are stateless**: Prefer constructor dependency injection; avoid singletons

### Naming Conventions

- **Services**: `*Service` suffix (e.g., `CombatService`, `MovementService`)
- **Entities**: Domain objects without suffix (e.g., `Player`, `Enemy`, `Item`)
- **Save data**: `*SaveData` suffix (e.g., `PlayerSaveData`)
- **Enums**: Uppercase values (e.g., `EnemyType.ZOMBIE`, `ItemType.WEAPON`)
- **Result/output objects**: `*Result` suffix (e.g., `CombatResult`, `MoveResult`)

### Code Style

- **Java 17+**: Use modern Java features where appropriate
- **Comments**: Only clarify non-obvious logic; avoid restating code
- **Testing**: Tests belong in `domain/unittest/`. Use simple assertions; avoid mocking

## Dependencies

**Key libraries:**
- **LibGDX** (`gdx`, `gdx-freetype`): Graphics and game framework
- **Jackson** (`jackson-databind`, `jackson-datatype-jsr310`): JSON serialization
- **Lanterna**: Terminal-based UI library

## Issue Tracking

This project uses **Beads** (`bd`) for issue tracking. Issues stored in `.beads/` and tracked in git.

**Quick commands:**
```bash
bd ready              # Find unblocked, open work
bd show <id>          # View issue details
bd update <id> --claim  # Claim work
bd close <id>         # Complete and close
bd dolt push          # Sync issues to remote
```

## Session Protocol

When ending a session, you **MUST** push all changes to remote:

```bash
git pull --rebase
bd dolt push
git commit -m "..."
git push
git status  # Must show "up to date with origin"
```

Work is **NOT complete** until `git push` succeeds.

## Multi-Module Gradle Tips

- `core` module contains domain logic and shared services
- `lwjgl3` module is the desktop entry point and launcher
- Changes to `core` affect both `lwjgl3` and any other platform modules
- Incremental compilation is enabled: `compileJava { options.incremental = true }`
