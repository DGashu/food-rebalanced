# Food Rebalanced Mod

This is a configurable food rebalancing mod for Minecraft that allows
custom hunger values, effects, and effect removal through a JSON config.
It's inspired on a mod called Food Effects that is no longer being updated
so I decided to do it on my own and then I'll add a few more mechanics.

## Features
- Configure hunger & saturation values
- Add potion effects to foods
- Remove effects when eating specific foods
- Reload config with /foodreload command

## Installation
1. Install Minecraft Forge
2. Put the mod in the mods folder
3. Launch Minecraft

(If or when available I'll try to get it on a mod page)

## How the configuration works
Food values are controlled via a JSON config file.

Example:
{
  "minecraft:apple": {
    "hunger": 6,
    "saturation": 0.4,
    "effects": [
      {
        "effect": "minecraft:regeneration",
        "duration": 100,
        "amplifier": 1
      }
    ]
  }
}

## Development
Built using Java and Minecraft Forge.