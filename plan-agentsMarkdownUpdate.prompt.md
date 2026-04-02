## AGENTS.md Update Summary

### What Changed

The `AGENTS.md` file has been significantly enhanced with project-specific guidance for AI coding agents working on the LibGDX Roguelike codebase. The previous version was generic and incomplete. The updated version now provides comprehensive, actionable guidance tailored to this specific project.

### Sections Added/Modified

#### 1. **Project Context Header** (NEW)
- Added upfront description identifying this as a LibGDX Roguelike game
- Listed key technologies: Java 17, LibGDX, Gradle multi-module, Jackson, Beads
- Listed the three main architecture layers with directory mapping

**Before**: Generic "Agent Instructions" title  
**After**: Clear project identity with technology stack and architecture overview

---

#### 2. **Build & Run Section** (NEW - Moved earlier for visibility)
Added dedicated section with Gradle commands specific to this project:
```bash
gradlew lwjgl3:run           # Most common: build & run the game
gradlew lwjgl3:jar           # Distribution jar
gradlew build                # Multi-module build
gradlew clean build          # Full rebuild
gradlew compileJava          # Compile without running
```

**Rationale**: Agents need immediate access to build commands; this prevents trial-and-error

---

#### 3. **Project Architecture Section** (NEW - Major Addition)
Detailed breakdown of three-layer architecture with specific examples:

**Domain Layer (`domain/`)**
- Lists actual entity classes: Player, Enemy, Item, Character, Backpack
- Enumerates services: GameService, CombatService, MovementService, EnemyAiService, InventoryService
- Mentions Level subsystem and enum types

**Data Layer (`datalayer/`)**
- SaveService for persistence
- Save data classes: PlayerSaveData, EnemySaveData, ItemSaveData, etc.
- JsonMapper for Jackson configuration
- Leaderboard component

**Presentation Layer (`presentation/`, `presentation_lanterna/`)**
- LibGDX (primary, when implemented)
- Lanterna (terminal-based, for testing/alternative view)
- Specific renderer classes: MainRenderer, TurnRenderer, ActorsRenderer, FogRenderer
- ColorScheme configuration

**Module Structure**
- `core`: Shared logic (domain, presentation, datalayer)
- `lwjgl3`: Desktop launcher and platform config

---

#### 4. **Coding Conventions Section** (NEW)
Established four key architecture rules:

1. **Domain is UI-agnostic** - No LibGDX or Lanterna imports in domain/
2. **Services are stateless** - Prefer dependency injection
3. **Unidirectional data flow** - domain → presentation/datalayer (not reverse)
4. **Serialization in datalayer** - All save/load logic stays in datalayer/, not domain

Naming conventions:
- Services: `*Service` suffix
- Entities: Plain names (Player, Enemy, Item)
- Save data: `*SaveData` suffix
- Enums: Uppercase values (EnemyType.ZOMBIE)

Testing guidance:
- Tests in `domain/unittest/`
- Simple assertions, avoid mocking

---

#### 5. **Preserved Sections**
All existing content retained verbatim:
- Non-Interactive Shell Commands (with full details)
- Beads Issue Tracker documentation
- Session Completion workflow
- Beads Workflow Integration (br/bd commands)
- Session Protocol
- Best Practices

---

### Key Improvements

| Aspect | Before | After |
|--------|--------|-------|
| **Onboarding** | 5 lines + generic content | Comprehensive project overview + architecture guide |
| **Build Commands** | None | Platform-specific Gradle commands |
| **Architecture Knowledge** | Zero | Detailed 3-layer guide with file/class mappings |
| **Naming Patterns** | None | Specific conventions (Service, SaveData, etc.) |
| **Code Organization** | Unknown | Clear module structure and layer responsibilities |
| **Testing Location** | Unknown | `domain/unittest/` specified with approach |
| **Total Length** | 86 lines | 240 lines |

---

### Source Analysis Performed

Examined:
- `.beads/` issue tracker integration
- `gradle.properties` and Gradle multi-module structure
- `core/build.gradle` (Java 17 target, Jackson dependencies)
- `core/src/main/java/io/github/example/domain/` entity and service files
- `core/src/main/java/io/github/example/datalayer/` persistence layer
- `core/src/main/java/io/github/example/presentation_lanterna/` rendering implementation
- `lwjgl3/build.gradle` (LWJGL3 desktop launcher)
- `.copilot/copilot-instructions.md` (existing AI guidance)

---

### Design Decisions

1. **Placed architecture section after build commands** - Agents need to both build AND understand the codebase structure
2. **Included specific class names** - Generic architecture descriptions aren't actionable; specific examples (GameService, CombatService) enable better code navigation
3. **Preserved all existing Beads workflow** - No contradictions with established issue tracking practices
4. **Emphasized domain layer independence** - Critical constraint for this architecture pattern
5. **Listed actual project structure** - Rather than theoretical best practices, focused on THIS project's patterns

---

### Not Changed (Intentionally)

- Session completion workflow remains mandatory
- Beads integration and workflow unchanged
- Non-interactive command guidance preserved
- Issue tracking process unchanged

These were already well-documented and correct.

---

## Implementation Checklist

- [x] Read existing AGENTS.md (86 lines, generic)
- [x] Analyzed codebase structure and patterns
- [x] Examined domain layer entities and services
- [x] Reviewed data persistence layer
- [x] Checked presentation and presentation_lanterna layers
- [x] Reviewed Gradle multi-module configuration
- [x] Added project context header
- [x] Added Build & Run section with specific commands
- [x] Added comprehensive Project Architecture section
- [x] Added Coding Conventions with rules and examples
- [x] Preserved all existing content
- [x] Verified final AGENTS.md (240 lines)
- [x] Created summary document

## Next Steps for Refinement

Potential enhancements (if needed):
1. Add debugging/troubleshooting section with common LibGDX issues
2. Add performance considerations for roguelike turn-based logic
3. Add dependency graph showing service relationships
4. Add examples of common code patterns (e.g., GameService usage)
5. Add notes on Jackson custom serialization patterns
6. Document Lanterna rendering pipeline
7. Add game loop/turn cycle documentation

