# Screen Transitions Implementation - Phase 5.5

## Overview
This document describes the screen transition system implemented in the presentation layer. The system provides smooth fade animations between screens with state preservation capabilities.

## Architecture

### Core Classes

#### 1. **ScreenTransition** (Base Class)
- **Location**: `presentation/screens/ScreenTransition.java`
- **Purpose**: Abstract base class for all transition animations
- **Key Methods**:
  - `update(float delta)` - Updates transition progress
  - `getProgress()` - Returns 0.0 to 1.0 progress value
  - `isCompleted()` - Checks if transition has finished
  - `render(SpriteBatch batch, int width, int height)` - Renders the transition effect
  - `drawOverlay()` - Helper to render colored overlays with alpha blending

**Key Features**:
- Configurable duration (default 0.5 seconds)
- Automatic progress calculation
- Built-in ShapeRenderer for rendering overlays
- State tracking (completed, in-progress)

#### 2. **FadeOutTransition**
- **Duration**: 0.5 seconds
- **Effect**: Screen gradually fades to black (0.0 → 1.0 alpha)
- **Use Case**: When closing or hiding screens

#### 3. **FadeInTransition**
- **Duration**: 0.5 seconds
- **Effect**: Screen fades in from black (1.0 → 0.0 alpha)
- **Use Case**: When opening or showing screens

#### 4. **CrossfadeTransition**
- **Duration**: 1.0 seconds default
- **Effect**: 
  - First 0.5s: Fade out to black
  - Second 0.5s: Fade in from black
- **Use Case**: Smooth transition between completely different screens

#### 5. **GameStateSnapshot**
- **Location**: `presentation/screens/GameStateSnapshot.java`
- **Purpose**: Captures game state for preservation during transitions
- **Data Stored**:
  - GameSession reference
  - Screen name
  - Timestamp for expiration checking

### Enhanced ScreenManager

**Location**: `presentation/screens/ScreenManager.java`

**New Fields**:
```java
private ScreenTransition activeTransition;
private Screen nextScreen;
private boolean isTransitioning;
private Map<String, GameStateSnapshot> stateSnapshots;
```

**New Methods**:

1. `transitionToScreen(Screen screen)` - Default 0.5s crossfade
2. `transitionToScreen(Screen screen, float duration)` - Custom duration crossfade
3. `transitionToScreen(Screen screen, ScreenTransition transition)` - Custom transition
4. `isTransitioning()` - Check if transition is active
5. `getActiveTransition()` - Get current transition object
6. `saveGameState(String screenName, GameStateSnapshot snapshot)` - Store game state
7. `getGameState(String screenName)` - Retrieve saved game state
8. `renderTransition(SpriteBatch batch, int width, int height)` - Render transition overlay

**Transition Flow**:
1. When `transitionToScreen()` is called:
   - Current game state is captured
   - Transition animation starts
   - At 50% progress: Screen switch occurs
   - At 100% progress: Transition completes

2. `render(float delta, SpriteBatch batch)` now:
   - Updates the active transition
   - Switches screens at midpoint
   - Clears transition when complete

### Enhanced PresentationLayer

**Location**: `presentation/PresentationLayer.java`

**New Methods**:
- `transitionToScreen(Screen screen)` - Delegates to ScreenManager
- `transitionToScreen(Screen screen, float duration)` - Custom duration delegation
- Updated `render()` to call `renderTransition()` after screen rendering

**Rendering Integration**:
```java
public void render(float delta) {
    mainRenderer.updateCamera();
    screenManager.render(delta, batch);
    
    // Render transition overlay
    batch.begin();
    screenManager.renderTransition(batch, (int) levelWidth, (int) levelHeight);
    batch.end();
}
```

## Usage Examples

### Example 1: Basic Transition to Menu Screen
```java
MenuScreen menuScreen = new MenuScreen(callback);
presentationLayer.transitionToScreen(menuScreen);
```

### Example 2: Custom Duration Transition
```java
GameScreen gameScreen = new GameScreen(gameService, renderer, inputHandler, assetManager);
presentationLayer.transitionToScreen(gameScreen, 0.75f); // 0.75 second transition
```

### Example 3: Custom Transition Type
```java
FadeOutTransition fadeOut = new FadeOutTransition(0.3f); // 0.3 second fade out
presentationLayer.getScreenManager().transitionToScreen(newScreen, fadeOut);
```

### Example 4: Save and Restore Game State
```java
// Save state before transition
GameStateSnapshot snapshot = new GameStateSnapshot(gameSession, "GameScreen");
screenManager.saveGameState("GameScreen", snapshot);

// Later, restore state
GameStateSnapshot restored = screenManager.getGameState("GameScreen");
```

## Implementation Details

### Transition Timeline
For a 1.0 second crossfade:
```
Time: 0.0s   → 0.25s  → 0.50s  → 0.75s  → 1.0s
      Start    Fading  Midpoint Switch   Unfading
      Out      Out     Screens In        In
      ↓        ↓       ↓       ↓        ↓
Alpha: 0 → 0.5 → 1.0 → 0.5 → 0
```

### Screen Switch Point
- Screens are switched when transition progress reaches **50%**
- This ensures visual continuity (screen hidden by black overlay during switch)
- Next screen has time to initialize while invisible

### Resource Management
- Each transition owns a ShapeRenderer instance
- ShapeRenderer is disposed when transition completes
- Prevents resource leaks with proper lifecycle management

### Alpha Blending
- Alpha values are clamped to [0.0, 1.0] range
- Uses LibGDX's built-in color blending
- Overlay color is set to black (0, 0, 0) for all transitions

## State Preservation

### How It Works
1. Before transition starts, current game state is captured
2. State includes GameSession and screen metadata
3. After transition completes, state can be retrieved and restored

### Storage
- States are stored in a HashMap: `Map<String, GameStateSnapshot>`
- Keyed by screen name for quick lookup
- Can check expiration with `isExpired(long maxAgeMs)`

### Integration Points
```java
// In GameScreen or other screens that need state preservation:
GameStateSnapshot snapshot = new GameStateSnapshot(gameService.getSession(), "GameScreen");
screenManager.saveGameState("GameScreen", snapshot);

// Later, when returning:
GameStateSnapshot restored = screenManager.getGameState("GameScreen");
if (restored != null) {
    // Restore inventory, position, enemies, etc.
}
```

## Compilation & Testing

### Build Command
```bash
cd C:\Users\User\Desktop\LibGDX_Roguelike
.\gradlew compileJava
```

### Files Created
- `ScreenTransition.java` - Base class
- `FadeOutTransition.java` - Fade out effect
- `FadeInTransition.java` - Fade in effect
- `CrossfadeTransition.java` - Crossfade effect
- `GameStateSnapshot.java` - State preservation

### Files Modified
- `ScreenManager.java` - Enhanced with transition support
- `PresentationLayer.java` - Added transition rendering integration

## Future Enhancements

Possible extensions to the transition system:
1. **Slide Transitions** - Screens slide in from sides
2. **Dissolve Transitions** - Pixel-by-pixel dissolve effect
3. **Wipe Transitions** - Screen wipes reveal next screen
4. **Particle Transitions** - Custom particle effect transitions
5. **Easing Functions** - Non-linear transition timing (ease-in, ease-out, etc.)
6. **Transition Callbacks** - Pre/post-transition hooks
7. **Queued Transitions** - Chain multiple transitions
8. **Pause/Resume** - Pause and resume active transitions

## Performance Considerations

- ShapeRenderer is created once per transition (efficient)
- Transition updates are O(1) complexity
- Overlay rendering is single draw call
- Minimal memory overhead (only storing active transition)
- State snapshots can be manually cleared to free memory

## Thread Safety

The current implementation is not thread-safe. Transitions should only be initiated from the main render thread:
- LibGDX render() method
- Event handlers triggered from render thread
- Never from background threads or async callbacks

## Known Limitations

1. ShapeRenderer requires batch to be ended/restarted for rendering
2. Transitions are blocking (game continues during transition)
3. Only one active transition at a time (enforced by checks)
4. State snapshots are stored indefinitely (manual cleanup needed for long-running games)

## Integration with GameScreen

When GameScreen transitions to menu, state should be saved:

```java
// In GameScreen's callback handler
public void onPause() {
    GameStateSnapshot snapshot = new GameStateSnapshot(
        gameService.getSession(), 
        "GameScreen"
    );
    screenManager.saveGameState("GameScreen", snapshot);
    
    // Transition to PauseScreen or MenuScreen
    screenManager.transitionToScreen(pauseScreen);
}
```

## Color Scheme

All fade transitions use **black (0, 0, 0)** for the overlay:
- Provides visual continuity
- Professional appearance
- Matches industry standard transitions
- Future versions could make this customizable

## Resolution Handling

Transitions automatically adapt to screen resolution:
- `renderTransition()` called with actual screen dimensions
- Overlay scales to full screen size
- Works with any resolution (1920x1080, 1280x720, etc.)
