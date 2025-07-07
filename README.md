# Minecraft Mods Collection

A comprehensive collection of three advanced Minecraft Forge mods that add powerful weapons, massive explosions, and intelligent AI to your Minecraft experience!

## 🧨 Giant TNT Mod

**The most explosive mod you'll ever use!**

### Features
- **Massive Explosions**: Creates 650-block diameter craters (325-block radius)
- **Flying Mechanics**: TNT launches high into the air before exploding
- **Dramatic Effects**: Enhanced particles, sounds, and visual feedback
- **Smart Physics**: Realistic trajectory with air time and ground impact
- **Balanced Crafting**: Requires 8 TNT blocks and a Nether Star

### Usage
1. Craft using 8 TNT + 1 Nether Star
2. Place and ignite with flint & steel, redstone, or fire charges
3. Watch it soar into the sky and create devastating destruction!

## 🔫 Firearms Mod

**Realistic modern weapons for Minecraft combat!**

### Features
- **4 Weapon Types**: Assault Rifle, Sniper Rifle, Pistol, Shotgun
- **Realistic Combat**: Each weapon has unique damage, accuracy, range, and recoil
- **Ammunition System**: Weapon-specific ammo requirements
- **Advanced Mechanics**: 
  - Muzzle flash particles
  - Bullet trail effects  
  - Realistic ballistics with gravity
  - Different firing patterns (shotgun spreads, sniper precision)
- **Crafting Components**: Gun barrels, stocks, triggers, enhanced gunpowder

### Weapon Stats
- **Pistol**: Fast, low damage, good for close combat
- **Assault Rifle**: Balanced damage and rate of fire
- **Sniper Rifle**: High damage, perfect accuracy, slow reload
- **Shotgun**: Devastating close-range spread damage

## 🧠 Villager AI Mod

**Revolutionary AI that makes villagers truly intelligent!**

### Features
- **English Communication**: 200+ unique phrases and responses
- **Advanced Emotions**: 10 different emotional states (Happy, Angry, Love, Trust, Fear, etc.)
- **Relationship System**: Villagers remember your actions and develop lasting relationships
- **Dynamic Behaviors**:
  - **Follow**: High-trust villagers will follow you around
  - **Obey**: Medium-trust villagers will respond to commands  
  - **Attack**: Low-trust villagers become hostile
  - **Ignore**: Neutral villagers maintain distance
- **Persistent Memory**: Relationships saved between game sessions
- **Conversation Depth**: Deeper conversations unlock as relationships develop
- **Context-Aware Responses**: Messages change based on situation and history

### How It Works
- **Normal Interaction**: Right-click villagers for basic greetings
- **Deep Communication**: Sneak + right-click for extended conversations
- **Relationship Building**: 
  - Positive actions increase trust (trading, helping, gifts)
  - Negative actions decrease trust (attacking, ignoring, stealing)
  - Time slowly decays relationships toward neutral
- **Emotional Responses**: Villagers react with appropriate emotions and behaviors

## 🌌 Dimensions Mod

**Explore incredible new worlds and mysterious realms!**

### Features
- **The Aether Dimension**: Heavenly realm inspired by the classic Aether mod
  - Floating islands and cloud-level exploration
  - Bright, peaceful environment with unique opportunities
  - Access via Glowing Stone portals (4x5 minimum frame)
  
- **The Middle Dimension**: Mysterious transit realm
  - Dark, liminal space that serves as a teleportation hub
  - Automatically accessed during long-distance overworld teleports
  - Built with Bedrock portals for direct access
  
- **Advanced Teleportation System**:
  - Long-distance teleports (10+ blocks) automatically route through Middle Dimension
  - 5-second transit time creates suspenseful journey
  - Seamless integration with vanilla teleportation mechanics

### Portal Building
- **Aether Portal**: Build 4x5+ frame with Glowing Stone, ignite with Flint & Steel
- **Bedrock Portal**: Build 4x5+ frame with Bedrock blocks, ignite with Flint & Steel
- **Glowing Stone Crafting**: 8 Stone + 8 Glowstone = 8 Glowing Stone

### Dimension Features
- **Aether**: Bright, elevated world perfect for sky bases and exploration
- **Middle**: Dark transit realm with unique aesthetics and temporary stays

## 🛠️ Multi-Project Structure

This repository uses Gradle multi-project setup:

```
minecraft-mods-collection/
├── giant-tnt-mod/          # Explosive TNT mod
├── firearms-mod/           # Modern weapons mod  
├── villager-ai-mod/        # Intelligent villager AI
├── dimensions-mod/         # Custom dimensions and portals
├── build.gradle            # Root build configuration
└── settings.gradle         # Multi-project setup
```

## 🚀 Building & Installation

### Prerequisites
- Java 17+
- Minecraft Forge 1.19.2-43.3.0+

### Build All Mods
```bash
./gradlew build
```

### Build Individual Mods
```bash
./gradlew :giant-tnt-mod:build
./gradlew :firearms-mod:build  
./gradlew :villager-ai-mod:build
./gradlew :dimensions-mod:build
```

### Installation
1. Install Minecraft Forge 1.19.2
2. Built JAR files will be in each mod's `build/libs/` folder
3. Place desired JAR files in your `mods` folder
4. Launch Minecraft with Forge profile

## ⚠️ Important Warnings

- **Giant TNT**: Creates MASSIVE explosions that can destroy entire landscapes!
- **Firearms**: Adds lethal weapons - use responsibly in multiplayer
- **Villager AI**: Villagers will remember everything - treat them well!
- **Dimensions**: Long-distance teleports automatically route through Middle dimension!

## 🎮 Gameplay Tips

### Giant TNT
- Use in creative mode or backup your world first
- Creates spectacular crater lakes and mining operations
- Perfect for large-scale terraforming projects

### Firearms  
- Different ammo types for different weapons
- Weapons have durability and need maintenance
- Realistic recoil affects accuracy

### Villager AI
- Start conversations by sneaking + right-clicking
- Give gifts to build relationships faster
- Hurt villagers and they'll hold grudges forever
- High-trust villagers unlock special dialogue and behaviors

### Dimensions
- Craft Glowing Stone with 8 Stone + 8 Glowstone for Aether portals
- Build 4x5 minimum portal frames (can be larger)
- Use Bedrock for Middle dimension portals (creative mode recommended)
- Long ender pearl throws automatically use Middle dimension transit
- Aether is perfect for building sky cities and cloud bases

## 🔧 Technical Details

- **Minecraft Version**: 1.19.2
- **Mod Loader**: Minecraft Forge 43.3.0+
- **Java Version**: 17+
- **License**: MIT

All four mods are fully independent and can be used separately or together for the ultimate enhanced Minecraft experience!