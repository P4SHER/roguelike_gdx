# Sprites Directory

## Placeholder Sprites
Currently using programmatically generated placeholder sprites (colored rectangles).

## Recommended Free Tilesets

To replace placeholders with real pixel art, download these free tilesets:

### Kenney Micro Roguelike
- URL: https://kenney.nl/assets/micro-roguelike
- License: CC0 (Public Domain)
- Contains: dungeon tiles, characters, items
- Tile size: 8x8 pixels

### OpenGameArt Roguelike Tiles
- URL: https://opengameart.org/content/roguelike-tiles
- Various licenses (check individual packs)

## Installation

1. Download tileset pack
2. Extract PNG files to appropriate subfolders:
   - `tiles/` - floor, wall, door sprites
   - `characters/` - player, enemy sprites  
   - `items/` - weapon, potion, food sprites
3. Update `GameAssetManager.java` with actual file paths

## Current Structure

```
sprites/
├── tiles/       - dungeon environment
├── characters/  - player and enemies
├── items/       - pickable items
└── README.md    - this file
```
