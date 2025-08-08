# Giant TNT Mod - Resource Pack Support

## 🎨 **Custom Textures for Resource Pack Creators**

The Giant TNT Mod includes built-in support for resource pack texture overrides, allowing creators to customize the visual appearance without modifying the mod JAR.

### 📁 **Texture Override Locations**

Replace these textures in your resource pack to customize the Giant TNT appearance:

#### **Entity Textures**
```
assets/gianttntmod/textures/entity/giant_tnt.png       # Default Giant TNT entity texture
assets/gianttntmod/textures/entity/giant_tnt_alt.png   # Alternative texture variant
```

#### **Block Textures**
```
assets/gianttntmod/textures/block/giant_tnt_side.png   # Block side texture
assets/gianttntmod/textures/block/giant_tnt_top.png    # Block top texture
assets/gianttntmod/textures/block/giant_tnt_bottom.png # Block bottom texture
```

#### **Item Textures**
```
assets/gianttntmod/textures/item/giant_tnt.png         # Item icon texture
```

### 🖼️ **Texture Specifications**

#### **Entity Texture Requirements**
- **Size**: 32×32 pixels (recommended) or 64×64 for high-res packs
- **Format**: PNG with transparency support
- **UV Mapping**: Standard Minecraft entity UV layout
- **Animation**: Static texture (animated textures supported via OptiFine/mcmeta)

#### **Block Texture Requirements**
- **Size**: 16×16 pixels (standard) or higher resolution for HD packs
- **Format**: PNG 
- **Recommended**: TNT-style design with unique elements to distinguish from vanilla TNT

#### **Alternative Texture Support**
The mod automatically detects `giant_tnt_alt.png` and can be configured to use it via:
- Server admin configuration
- Resource pack mcmeta directives
- Player preference settings (if implemented)

### 🎨 **Recommended Themes**

#### **Mystical Variant** (`giant_tnt_alt.png`)
- **Colors**: Purple, dark blue, mystical glow effects
- **Style**: Magical runes, sparkling effects, ethereal appearance
- **Use Case**: Fantasy/magic-themed resource packs

#### **Industrial Variant**
- **Colors**: Metallic grays, warning stripes, industrial markings
- **Style**: High-tech, futuristic, military-grade appearance
- **Use Case**: Modern/sci-fi themed resource packs

#### **Medieval Variant**
- **Colors**: Earthy tones, wooden textures, iron bands
- **Style**: Barrel-like, rustic, medieval bomb appearance
- **Use Case**: Medieval/fantasy themed resource packs

### 📦 **Resource Pack Structure Example**

```
MyCustomPack/
├── pack.mcmeta
├── pack.png
└── assets/
    └── gianttntmod/
        ├── textures/
        │   ├── entity/
        │   │   ├── giant_tnt.png     # Your custom entity texture
        │   │   └── giant_tnt_alt.png # Alternative variant
        │   ├── block/
        │   │   ├── giant_tnt_side.png
        │   │   ├── giant_tnt_top.png
        │   │   └── giant_tnt_bottom.png
        │   └── item/
        │       └── giant_tnt.png
        └── models/              # Optional: custom model overrides
            ├── block/
            └── item/
```

### 🔧 **Advanced Customization**

#### **Model Overrides**
For advanced users, you can also override the model files:
```
assets/gianttntmod/models/block/giant_tnt.json
assets/gianttntmod/models/item/giant_tnt.json
```

#### **Sound Overrides**
Customize explosion sounds (requires sound resource pack):
```
assets/gianttntmod/sounds/giant_explosion.ogg
assets/gianttntmod/sounds/giant_fuse.ogg
```

### 🚀 **Testing Your Resource Pack**

1. **Install** your resource pack in `.minecraft/resourcepacks/`
2. **Enable** it in Minecraft settings
3. **Spawn** a Giant TNT with `/gianttnt spawn`
4. **Verify** your textures appear correctly
5. **Test** block placement and entity rendering

### 💡 **Tips for Resource Pack Creators**

- **Contrast**: Ensure your Giant TNT is visually distinct from vanilla TNT
- **Performance**: Keep texture sizes reasonable for better performance
- **Compatibility**: Test with popular shader packs and OptiFine
- **Documentation**: Include usage instructions in your pack description
- **Preview**: Provide screenshots showing the Giant TNT in action

### 🎬 **Recommended Screenshots for Pack Promotion**

1. Giant TNT block placed in world
2. Flying Giant TNT entity with trail effects
3. Massive explosion crater showcasing destruction
4. Comparison with vanilla TNT (before/after)
5. Different lighting conditions (day/night/nether)

---

**Happy creating!** 🎨 Your custom Giant TNT textures will make explosive experiences even more spectacular!
