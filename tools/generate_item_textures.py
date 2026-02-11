#!/usr/bin/env python3
"""
Generate item textures for Promaton mod in vanilla Minecraft style.
Run this script to regenerate all item texture PNGs.

Item textures are 16x16 pixels.

Color references from vanilla Minecraft:
- Smooth Stone: #7D7D7D to #A8A8A8 (gray tones)
- Redstone: #AA0000 to #FF0000 (red)
- Amethyst: #9D78C4 to #CFA7FF (purple)
- Iron: #D8D8D8 (light gray metallic)
- Gold: #FCDB4C (yellow-gold)
"""

from PIL import Image, ImageDraw

# Color palette for items
COLORS = {
    # Stone/tablet base
    'stone_dark': (90, 90, 90),
    'stone_mid': (125, 125, 125),
    'stone_light': (160, 160, 160),
    'stone_highlight': (180, 180, 180),

    # Redstone/circuitry
    'redstone_dark': (139, 0, 0),
    'redstone_mid': (180, 20, 20),
    'redstone_bright': (220, 50, 50),

    # Amethyst/crystal
    'amethyst_dark': (100, 60, 130),
    'amethyst_mid': (157, 120, 196),
    'amethyst_light': (200, 160, 230),
    'amethyst_bright': (220, 190, 255),

    # Outlines
    'outline_dark': (40, 40, 40),
    'outline_black': (20, 20, 20),

    # Transparent
    'transparent': (0, 0, 0, 0),
}


def create_item_image():
    """Create a new 16x16 RGBA image with transparent background."""
    return Image.new('RGBA', (16, 16), COLORS['transparent'])


def generate_program():
    """
    Generate the Program item texture.

    Design: A flat tablet/chip with circuit traces and a central amethyst crystal.
    - Smooth stone base (rectangular, slightly rounded corners)
    - Redstone circuit traces etched into surface
    - Small amethyst crystal in center (the "memory")
    """
    img = create_item_image()
    draw = ImageDraw.Draw(img)

    # === BASE TABLET ===
    # Main body (rows 2-13, cols 3-12) - leaves room for depth effect
    # Fill with mid stone color
    for y in range(3, 14):
        for x in range(3, 13):
            draw.point((x, y), fill=COLORS['stone_mid'])

    # Top edge highlight (lighter)
    for x in range(4, 12):
        draw.point((x, 3), fill=COLORS['stone_light'])

    # Left edge highlight
    for y in range(4, 13):
        draw.point((3, y), fill=COLORS['stone_light'])

    # Bottom edge shadow (darker)
    for x in range(4, 13):
        draw.point((x, 13), fill=COLORS['stone_dark'])

    # Right edge shadow
    for y in range(4, 14):
        draw.point((12, y), fill=COLORS['stone_dark'])

    # Corner pixels for slight rounding
    draw.point((3, 3), fill=COLORS['transparent'])
    draw.point((12, 3), fill=COLORS['transparent'])
    draw.point((3, 13), fill=COLORS['transparent'])
    draw.point((12, 13), fill=COLORS['transparent'])

    # Dark outline
    # Top edge
    for x in range(4, 12):
        draw.point((x, 2), fill=COLORS['outline_dark'])
    # Bottom edge
    for x in range(4, 12):
        draw.point((x, 14), fill=COLORS['outline_dark'])
    # Left edge
    for y in range(4, 13):
        draw.point((2, y), fill=COLORS['outline_dark'])
    # Right edge
    for y in range(4, 14):
        draw.point((13, y), fill=COLORS['outline_dark'])

    # Corner outline pixels
    draw.point((3, 2), fill=COLORS['outline_dark'])
    draw.point((12, 2), fill=COLORS['outline_dark'])
    draw.point((2, 3), fill=COLORS['outline_dark'])
    draw.point((13, 3), fill=COLORS['outline_dark'])
    draw.point((2, 13), fill=COLORS['outline_dark'])
    draw.point((13, 13), fill=COLORS['outline_dark'])
    draw.point((3, 14), fill=COLORS['outline_dark'])
    draw.point((12, 14), fill=COLORS['outline_dark'])

    # === CIRCUIT TRACES ===
    # Horizontal traces
    for x in range(4, 7):
        draw.point((x, 5), fill=COLORS['redstone_mid'])
    for x in range(9, 12):
        draw.point((x, 5), fill=COLORS['redstone_mid'])

    for x in range(4, 6):
        draw.point((x, 11), fill=COLORS['redstone_mid'])
    for x in range(10, 12):
        draw.point((x, 11), fill=COLORS['redstone_mid'])

    # Vertical traces
    for y in range(5, 8):
        draw.point((4, y), fill=COLORS['redstone_mid'])
    for y in range(5, 8):
        draw.point((11, y), fill=COLORS['redstone_mid'])

    for y in range(9, 12):
        draw.point((4, y), fill=COLORS['redstone_mid'])
    for y in range(9, 12):
        draw.point((11, y), fill=COLORS['redstone_mid'])

    # Trace highlights (brighter pixels for depth)
    draw.point((5, 5), fill=COLORS['redstone_bright'])
    draw.point((10, 5), fill=COLORS['redstone_bright'])
    draw.point((4, 6), fill=COLORS['redstone_bright'])
    draw.point((11, 6), fill=COLORS['redstone_bright'])

    # === CENTRAL AMETHYST CRYSTAL ===
    # 4x4 crystal in center (cols 6-9, rows 6-9)
    # Dark edges
    draw.point((6, 7), fill=COLORS['amethyst_dark'])
    draw.point((6, 8), fill=COLORS['amethyst_dark'])
    draw.point((7, 9), fill=COLORS['amethyst_dark'])
    draw.point((8, 9), fill=COLORS['amethyst_dark'])
    draw.point((9, 8), fill=COLORS['amethyst_dark'])

    # Mid tones
    draw.point((7, 7), fill=COLORS['amethyst_mid'])
    draw.point((8, 7), fill=COLORS['amethyst_mid'])
    draw.point((7, 8), fill=COLORS['amethyst_mid'])
    draw.point((8, 8), fill=COLORS['amethyst_mid'])
    draw.point((9, 7), fill=COLORS['amethyst_mid'])

    # Highlight (top-left of crystal)
    draw.point((6, 6), fill=COLORS['amethyst_light'])
    draw.point((7, 6), fill=COLORS['amethyst_light'])
    draw.point((8, 6), fill=COLORS['amethyst_bright'])
    draw.point((9, 6), fill=COLORS['amethyst_light'])

    return img


def generate_automaton_casing():
    """
    Generate the Automaton Casing item texture.

    Design: Front-view of a Minecraft player character.
    - Blocky, thick proportions
    - Simple rectangular shapes
    - Iron body with gold accents
    """
    img = create_item_image()
    draw = ImageDraw.Draw(img)

    # Color palette for casing
    iron_dark = (120, 120, 130)
    iron_mid = (160, 160, 170)
    iron_light = (200, 200, 210)
    gold_accent = (220, 180, 60)
    gold_dark = (180, 140, 40)
    outline = (40, 40, 50)

    # === HEAD (4x4 block, rows 1-4) ===
    # Outline top
    for x in range(6, 10):
        draw.point((x, 1), fill=outline)
    # Sides and fill
    for y in range(2, 5):
        draw.point((5, y), fill=outline)
        draw.point((6, y), fill=iron_light)
        draw.point((7, y), fill=iron_light)
        draw.point((8, y), fill=iron_mid)
        draw.point((9, y), fill=iron_mid)
        draw.point((10, y), fill=outline)
    # Outline bottom
    for x in range(6, 10):
        draw.point((x, 5), fill=outline)

    # Eyes (gold accents, row 3)
    draw.point((6, 3), fill=gold_accent)
    draw.point((9, 3), fill=gold_accent)

    # === BODY (4 wide x 5 tall, rows 6-10) ===
    for y in range(6, 11):
        draw.point((5, y), fill=outline)
        draw.point((6, y), fill=iron_light if y < 8 else iron_mid)
        draw.point((7, y), fill=iron_light if y < 8 else iron_mid)
        draw.point((8, y), fill=iron_mid if y < 8 else iron_dark)
        draw.point((9, y), fill=iron_mid if y < 8 else iron_dark)
        draw.point((10, y), fill=outline)

    # Gold chest accent (rows 6-7)
    draw.point((7, 6), fill=gold_accent)
    draw.point((8, 6), fill=gold_dark)
    draw.point((7, 7), fill=gold_dark)

    # === ARMS (2 wide each, rows 6-10) ===
    # Left arm
    for y in range(6, 11):
        draw.point((3, y), fill=outline)
        draw.point((4, y), fill=iron_light if y < 8 else iron_mid)
        # x=5 is body outline

    # Right arm
    for y in range(6, 11):
        # x=10 is body outline
        draw.point((11, y), fill=iron_mid if y < 8 else iron_dark)
        draw.point((12, y), fill=outline)

    # Arm bottoms
    draw.point((4, 11), fill=outline)
    draw.point((11, 11), fill=outline)

    # === LEGS (2 wide each, rows 11-14) ===
    # Left leg
    for y in range(11, 15):
        draw.point((5, y), fill=outline)
        draw.point((6, y), fill=iron_mid if y < 13 else iron_dark)
        draw.point((7, y), fill=outline)

    # Right leg
    for y in range(11, 15):
        draw.point((8, y), fill=outline)
        draw.point((9, y), fill=iron_mid if y < 13 else iron_dark)
        draw.point((10, y), fill=outline)

    # Feet
    draw.point((6, 15), fill=outline)
    draw.point((9, 15), fill=outline)

    return img


def generate_waypoint_wand():
    """
    Generate the Waypoint Wand item texture.

    Design: A magical wand for marking locations (diagonal orientation).
    - Ender pearl tip (dark teal) - top
    - Blaze rod shaft (orange/yellow) - middle
    - Copper handle (orange-brown) - bottom
    """
    img = create_item_image()
    draw = ImageDraw.Draw(img)

    # Color palette
    # Ender pearl colors (teal/cyan)
    ender_dark = (15, 60, 60)
    ender_mid = (25, 100, 100)
    ender_light = (40, 140, 130)
    ender_highlight = (80, 180, 170)

    # Blaze rod colors (orange/yellow)
    blaze_dark = (180, 100, 20)
    blaze_mid = (220, 150, 40)
    blaze_light = (250, 190, 70)
    blaze_highlight = (255, 220, 120)

    # Copper colors (orange-brown)
    copper_dark = (140, 70, 40)
    copper_mid = (180, 100, 60)
    copper_light = (200, 130, 80)

    outline = (30, 30, 30)

    # Wand goes from bottom-left to top-right (diagonal)
    # Total length ~12 pixels diagonal

    # === ENDER PEARL TIP (top-right, 3x3 area) ===
    # Main pearl body
    draw.point((12, 1), fill=ender_light)
    draw.point((13, 1), fill=ender_mid)
    draw.point((14, 1), fill=outline)

    draw.point((11, 2), fill=ender_highlight)
    draw.point((12, 2), fill=ender_light)
    draw.point((13, 2), fill=ender_mid)
    draw.point((14, 2), fill=ender_dark)

    draw.point((11, 3), fill=ender_light)
    draw.point((12, 3), fill=ender_mid)
    draw.point((13, 3), fill=ender_dark)

    draw.point((10, 3), fill=outline)
    draw.point((11, 4), fill=ender_dark)
    draw.point((12, 4), fill=outline)

    # === BLAZE ROD SHAFT (middle diagonal) ===
    # Main shaft pixels going diagonal
    draw.point((10, 4), fill=blaze_highlight)
    draw.point((9, 5), fill=blaze_light)
    draw.point((10, 5), fill=blaze_mid)

    draw.point((8, 6), fill=blaze_light)
    draw.point((9, 6), fill=blaze_mid)

    draw.point((7, 7), fill=blaze_light)
    draw.point((8, 7), fill=blaze_mid)

    draw.point((6, 8), fill=blaze_light)
    draw.point((7, 8), fill=blaze_mid)

    draw.point((5, 9), fill=blaze_light)
    draw.point((6, 9), fill=blaze_dark)

    draw.point((4, 10), fill=blaze_mid)
    draw.point((5, 10), fill=blaze_dark)

    # === COPPER HANDLE (bottom-left) ===
    draw.point((3, 11), fill=copper_light)
    draw.point((4, 11), fill=copper_mid)

    draw.point((2, 12), fill=copper_light)
    draw.point((3, 12), fill=copper_mid)

    draw.point((1, 13), fill=copper_mid)
    draw.point((2, 13), fill=copper_dark)

    draw.point((1, 14), fill=copper_dark)

    # Outline/shadow pixels for definition
    draw.point((0, 14), fill=outline)
    draw.point((0, 13), fill=outline)
    draw.point((1, 12), fill=outline)

    return img


def generate_slot_program():
    """
    Generate a ghost icon for the Program slot.
    Vanilla style: single color outline matching the program item shape.
    Traces the inner gray fill edge, not the dark outline.
    """
    img = create_item_image()
    draw = ImageDraw.Draw(img)

    # Vanilla slot sprite color (matches vanilla's 85,85,85 used in smithing table etc.)
    c = (85, 85, 85, 255)

    # Trace the inner edge where the gray tablet fill starts
    # The fill is from (3,3) to (12,13) with rounded corners

    # Top edge (row 3)
    for x in range(4, 12):
        draw.point((x, 3), fill=c)

    # Left edge
    for y in range(4, 13):
        draw.point((3, y), fill=c)

    # Right edge
    for y in range(4, 13):
        draw.point((12, y), fill=c)

    # Bottom edge (row 13)
    for x in range(4, 12):
        draw.point((x, 13), fill=c)

    # Rounded corners (the fill doesn't have sharp corners)
    # These connect the edges smoothly

    # Inner amethyst crystal outline (4x4 area, cols 6-9, rows 6-9)
    # Top edge of crystal
    for x in range(6, 10):
        draw.point((x, 6), fill=c)
    # Bottom edge of crystal
    for x in range(7, 9):
        draw.point((x, 9), fill=c)
    # Left edge of crystal
    for y in range(7, 9):
        draw.point((6, y), fill=c)
    # Right edge of crystal
    for y in range(7, 9):
        draw.point((9, y), fill=c)

    return img


def generate_slot_casing():
    """
    Generate a ghost icon for the Automaton Casing slot.
    Vanilla style: single color outline matching the casing shape.
    Traces the inner fill edge, not the dark outline.
    """
    img = create_item_image()
    draw = ImageDraw.Draw(img)

    # Vanilla slot sprite color (matches vanilla's 85,85,85 used in smithing table etc.)
    c = (85, 85, 85, 255)

    # Trace the inner fill of the automaton casing (front-view player)

    # === HEAD (4x4 block, fill is x=6-9, y=2-4) ===
    # Top edge
    for x in range(6, 10):
        draw.point((x, 2), fill=c)
    # Bottom edge
    for x in range(6, 10):
        draw.point((x, 4), fill=c)
    # Left edge
    for y in range(2, 5):
        draw.point((6, y), fill=c)
    # Right edge
    for y in range(2, 5):
        draw.point((9, y), fill=c)

    # === BODY (fill is x=6-9, y=6-10) ===
    # Top edge
    for x in range(6, 10):
        draw.point((x, 6), fill=c)
    # Bottom edge
    for x in range(6, 10):
        draw.point((x, 10), fill=c)
    # Left edge
    for y in range(6, 11):
        draw.point((6, y), fill=c)
    # Right edge
    for y in range(6, 11):
        draw.point((9, y), fill=c)

    # === LEFT ARM (fill is x=4, y=6-10) ===
    draw.point((4, 6), fill=c)
    draw.point((4, 10), fill=c)
    for y in range(6, 11):
        draw.point((4, y), fill=c)

    # === RIGHT ARM (fill is x=11, y=6-10) ===
    draw.point((11, 6), fill=c)
    draw.point((11, 10), fill=c)
    for y in range(6, 11):
        draw.point((11, y), fill=c)

    # === LEGS (left: x=6, y=11-14; right: x=9, y=11-14) ===
    # Left leg
    for y in range(11, 15):
        draw.point((6, y), fill=c)
    # Right leg
    for y in range(11, 15):
        draw.point((9, y), fill=c)

    # Leg tops (connect to body)
    draw.point((6, 11), fill=c)
    draw.point((9, 11), fill=c)

    # Leg bottoms
    draw.point((6, 14), fill=c)
    draw.point((9, 14), fill=c)

    return img


def generate_anchor_crystal():
    """
    Generate the Anchor Crystal item texture.

    Design: End-game crystal for chunk loading.
    - End crystal inspired (pink/magenta glow)
    - Nether star sparkle (white/yellow center)
    - Diamond blue accents
    - Dark netherite/obsidian frame
    """
    img = create_item_image()
    draw = ImageDraw.Draw(img)

    # Color palette
    # End crystal pink/magenta
    crystal_dark = (120, 40, 100)
    crystal_mid = (180, 80, 150)
    crystal_light = (220, 130, 190)
    crystal_glow = (250, 180, 230)

    # Nether star white/yellow
    star_white = (255, 255, 255)
    star_yellow = (255, 250, 200)
    star_core = (255, 255, 220)

    # Diamond blue
    diamond_dark = (60, 140, 190)
    diamond_mid = (100, 190, 230)
    diamond_light = (150, 220, 250)

    # Netherite/obsidian dark
    dark_frame = (40, 35, 45)
    obsidian = (20, 15, 30)

    # === CRYSTAL SHAPE (diamond/octagon) ===
    # The crystal is centered, roughly 10x12 pixels

    # Top point (row 1-2)
    draw.point((8, 1), fill=crystal_glow)
    draw.point((7, 2), fill=crystal_light)
    draw.point((8, 2), fill=crystal_glow)
    draw.point((9, 2), fill=crystal_light)

    # Upper body (row 3-4) - widening
    draw.point((6, 3), fill=crystal_mid)
    draw.point((7, 3), fill=crystal_light)
    draw.point((8, 3), fill=star_white)  # bright center
    draw.point((9, 3), fill=crystal_light)
    draw.point((10, 3), fill=crystal_mid)

    draw.point((5, 4), fill=crystal_dark)
    draw.point((6, 4), fill=crystal_mid)
    draw.point((7, 4), fill=crystal_light)
    draw.point((8, 4), fill=star_core)  # nether star glow
    draw.point((9, 4), fill=crystal_light)
    draw.point((10, 4), fill=crystal_mid)
    draw.point((11, 4), fill=crystal_dark)

    # Middle body (row 5-7) - widest part with diamond accents
    for y in range(5, 8):
        draw.point((4, y), fill=dark_frame)
        draw.point((5, y), fill=crystal_dark)
        draw.point((6, y), fill=crystal_mid)
        draw.point((10, y), fill=crystal_mid)
        draw.point((11, y), fill=crystal_dark)
        draw.point((12, y), fill=dark_frame)

    # Center area with star glow and diamond accents
    draw.point((7, 5), fill=diamond_light)
    draw.point((8, 5), fill=star_white)
    draw.point((9, 5), fill=diamond_light)

    draw.point((7, 6), fill=diamond_mid)
    draw.point((8, 6), fill=star_yellow)  # core
    draw.point((9, 6), fill=diamond_mid)

    draw.point((7, 7), fill=diamond_dark)
    draw.point((8, 7), fill=diamond_mid)
    draw.point((9, 7), fill=diamond_dark)

    # Lower body (row 8-9) - narrowing
    draw.point((5, 8), fill=dark_frame)
    draw.point((6, 8), fill=crystal_dark)
    draw.point((7, 8), fill=crystal_mid)
    draw.point((8, 8), fill=crystal_light)
    draw.point((9, 8), fill=crystal_mid)
    draw.point((10, 8), fill=crystal_dark)
    draw.point((11, 8), fill=dark_frame)

    draw.point((6, 9), fill=dark_frame)
    draw.point((7, 9), fill=crystal_dark)
    draw.point((8, 9), fill=crystal_mid)
    draw.point((9, 9), fill=crystal_dark)
    draw.point((10, 9), fill=dark_frame)

    # Bottom section with obsidian base (row 10-11)
    draw.point((7, 10), fill=obsidian)
    draw.point((8, 10), fill=crystal_dark)
    draw.point((9, 10), fill=obsidian)

    draw.point((7, 11), fill=obsidian)
    draw.point((8, 11), fill=obsidian)
    draw.point((9, 11), fill=obsidian)

    # Bottom point (row 12)
    draw.point((8, 12), fill=dark_frame)

    # Small sparkle effects around the crystal
    draw.point((4, 3), fill=crystal_glow)
    draw.point((12, 3), fill=crystal_glow)
    draw.point((3, 6), fill=star_white)
    draw.point((13, 6), fill=star_white)
    draw.point((5, 10), fill=crystal_light)
    draw.point((11, 10), fill=crystal_light)

    return img


# =============================================================================
# MAIN
# =============================================================================
if __name__ == '__main__':
    import os

    # Find the output directory relative to this script's location
    script_dir = os.path.dirname(os.path.abspath(__file__))
    project_root = os.path.dirname(script_dir)
    output_dir = os.path.join(project_root, 'src', 'main', 'resources', 'assets', 'promaton', 'textures', 'item')

    # Create output directory if needed
    os.makedirs(output_dir, exist_ok=True)

    print(f"Output directory: {output_dir}")
    print("Generating Promaton item textures...")
    print("=" * 50)

    # Generate Program
    img = generate_program()
    img.save(os.path.join(output_dir, 'program.png'))
    print("Generated: program.png")

    # Generate Automaton Casing
    img = generate_automaton_casing()
    img.save(os.path.join(output_dir, 'automaton_casing.png'))
    print("Generated: automaton_casing.png")

    # Generate Waypoint Wand
    img = generate_waypoint_wand()
    img.save(os.path.join(output_dir, 'waypoint_wand.png'))
    print("Generated: waypoint_wand.png")

    # Generate Anchor Crystal
    img = generate_anchor_crystal()
    img.save(os.path.join(output_dir, 'anchor_crystal.png'))
    print("Generated: anchor_crystal.png")

    # Generate slot ghost icons (go in gui/sprites folder)
    sprites_dir = os.path.join(project_root, 'src', 'main', 'resources', 'assets', 'promaton', 'textures', 'gui', 'sprites')
    os.makedirs(sprites_dir, exist_ok=True)

    print("-" * 50)
    print(f"Slot sprites directory: {sprites_dir}")

    img = generate_slot_program()
    img.save(os.path.join(sprites_dir, 'slot_program.png'))
    print("Generated: slot_program.png")

    img = generate_slot_casing()
    img.save(os.path.join(sprites_dir, 'slot_casing.png'))
    print("Generated: slot_casing.png")

    print("=" * 50)
    print("Done!")
