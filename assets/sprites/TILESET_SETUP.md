# Tileset Setup Guide

## Current Status

The sprite directories have been created with the following structure:

```
assets/sprites/
├── tiles/           # Dungeon tiles (walls, floors, doors, etc.)
├── characters/      # Player and enemy sprites
├── items/          # Item sprites
└── TILESET_SETUP.md (this file)
```

## Kenney Micro Roguelike Integration

**Target**: Download from https://kenney.nl/assets/micro-roguelike

### Expected Files

Once downloaded, extract and organize as follows:

```
assets/sprites/
├── tiles/
│   ├── Dungeon_Tileset.png        # Or individual tile PNGs
│   └── tilemap.txt                # Tile mapping reference
│
├── characters/
│   ├── player.png                 # Player sprite(s)
│   ├── enemies/
│   │   ├── zombie.png
│   │   ├── vampire.png
│   │   ├── ghost.png
│   │   ├── ogre.png
│   │   └── snake_mage.png
│   └── character_index.txt        # Sprite mapping
│
└── items/
    ├── weapons.png
    ├── armor.png
    └── potions.png
```

### Tile Specifications

- **Tile Size**: 32×32 pixels (as per requirements)
- **Format**: PNG with alpha channel (RGBA)
- **Color Space**: sRGB
- **Total Tiles**: ~64-128 tile types for complete roguelike

### Manual Setup (Temporary)

If automatic download fails, manually:

1. Visit: https://kenney.nl/assets/micro-roguelike
2. Download the ZIP file
3. Extract to `assets/sprites/`
4. Run `bd` to mark task as complete

### Sprite Mapping Reference

For AssetLoader implementation, maintain a reference file mapping sprite names to coordinates:

```
# format: NAME=ATLAS_X,ATLAS_Y,WIDTH,HEIGHT
player_idle=0,0,32,32
player_attack=32,0,32,32
enemy_zombie=64,0,32,32
enemy_vampire=96,0,32,32
tile_floor=0,32,32,32
tile_wall=32,32,32,32
```

## Next Steps

1. **LibGDX_Roguelike-6fd** (AssetLoader) - Will load these tilesets
2. **LibGDX_Roguelike-q00** (LevelRenderer) - Will render tiles
3. **LibGDX_Roguelike-4ui** (ActorsRenderer) - Will render sprites

## References

- Kenney Assets: https://kenney.nl/
- LibGDX Texture Atlases: https://github.com/libgdx/libgdx/wiki/Texture-atlases
- OpenGameArt Roguelike: https://opengameart.org/content/roguelike-rpg-tileset
