#!/usr/bin/env python3
"""
Generate block textures for Promaton mod in vanilla Minecraft style.
Run this script to regenerate all block texture PNGs.

Block textures are 16x16 pixels.

Design: Crafter-style frame but inverted - iron frame around wooden center.
Industrial look inspired by the Crafter block.
"""

from PIL import Image, ImageDraw

# Color palette extracted from vanilla Minecraft textures
COLORS = {
    # Oak plank colors (from oak_planks.png)
    'oak_dark': (126, 98, 55),
    'oak_mid': (156, 127, 78),
    'oak_light': (188, 152, 98),
    'oak_highlight': (199, 166, 115),

    # Iron/metal frame colors (from crafter/iron_block)
    'iron_dark': (104, 104, 104),
    'iron_mid': (135, 135, 135),
    'iron_light': (167, 167, 167),
    'iron_highlight': (219, 219, 219),

    # Redstone colors
    'redstone_dark': (92, 12, 12),
    'redstone_mid': (140, 24, 24),
    'redstone_bright': (180, 40, 40),

    # Indicator light colors (dim blue for idle state)
    'light_blue': (90, 130, 190),
    'light_blue_dim': (60, 90, 130),

    # Dispenser opening (dark void)
    'void_dark': (20, 20, 24),
    'void_mid': (40, 40, 46),
    'void_edge': (60, 60, 66),
}


def create_block_image():
    """Create a new 16x16 RGBA image."""
    return Image.new('RGBA', (16, 16), (0, 0, 0, 255))


def draw_iron_frame(draw, frame_width=2):
    """
    Draw an iron frame around the edges of the texture.
    Frame is darker on outside, lighter on inside edge.
    """
    # Outer edge (darkest)
    for i in range(16):
        draw.point((i, 0), fill=COLORS['iron_dark'])
        draw.point((i, 15), fill=COLORS['iron_dark'])
        draw.point((0, i), fill=COLORS['iron_dark'])
        draw.point((15, i), fill=COLORS['iron_dark'])

    # Second row (mid tone)
    for i in range(1, 15):
        draw.point((i, 1), fill=COLORS['iron_mid'])
        draw.point((i, 14), fill=COLORS['iron_mid'])
        draw.point((1, i), fill=COLORS['iron_mid'])
        draw.point((14, i), fill=COLORS['iron_mid'])

    # Inner edge highlight (where frame meets center)
    if frame_width >= 2:
        for i in range(2, 14):
            draw.point((i, 2), fill=COLORS['iron_light'])
            draw.point((i, 13), fill=COLORS['iron_mid'])
            draw.point((2, i), fill=COLORS['iron_light'])
            draw.point((13, i), fill=COLORS['iron_mid'])

    # Corner accents (rivets)
    draw.point((1, 1), fill=COLORS['iron_dark'])
    draw.point((14, 1), fill=COLORS['iron_dark'])
    draw.point((1, 14), fill=COLORS['iron_dark'])
    draw.point((14, 14), fill=COLORS['iron_dark'])


def draw_wood_center(draw, x_start=3, y_start=3, x_end=13, y_end=13):
    """
    Fill the center area with oak plank pattern.
    Creates horizontal wood grain effect.
    """
    for y in range(y_start, y_end):
        for x in range(x_start, x_end):
            # Create wood grain pattern
            grain_offset = (x * 3 + y) % 7

            if grain_offset == 0:
                color = COLORS['oak_dark']
            elif grain_offset == 1 or grain_offset == 6:
                color = COLORS['oak_light']
            elif y % 4 == 0 and x % 2 == 0:
                # Horizontal plank lines
                color = COLORS['oak_dark']
            else:
                color = COLORS['oak_mid']

            draw.point((x, y), fill=color)

    # Add subtle horizontal grain lines
    for y in [5, 9]:
        if y_start <= y < y_end:
            for x in range(x_start, x_end):
                if x % 3 != 0:
                    draw.point((x, y), fill=COLORS['oak_dark'])


def generate_controller_top():
    """
    Generate the Automaton Controller top face.

    Design:
    - Iron frame around edge
    - Iron panel center with redstone circuit traces
    - Central indicator light (dim blue for idle)
    """
    img = create_block_image()
    draw = ImageDraw.Draw(img)

    # === IRON FRAME ===
    draw_iron_frame(draw)

    # === IRON CENTER PANEL (instead of wood - top is all iron) ===
    for y in range(3, 13):
        for x in range(3, 13):
            # Subtle variation in iron
            if (x + y) % 5 == 0:
                color = COLORS['iron_light']
            elif (x + y) % 7 == 0:
                color = COLORS['iron_dark']
            else:
                color = COLORS['iron_mid']
            draw.point((x, y), fill=color)

    # === REDSTONE CIRCUIT TRACES ===
    # Corner circuits
    # Top-left
    draw.point((4, 4), fill=COLORS['redstone_mid'])
    draw.point((5, 4), fill=COLORS['redstone_bright'])
    draw.point((4, 5), fill=COLORS['redstone_mid'])

    # Top-right
    draw.point((10, 4), fill=COLORS['redstone_bright'])
    draw.point((11, 4), fill=COLORS['redstone_mid'])
    draw.point((11, 5), fill=COLORS['redstone_mid'])

    # Bottom-left
    draw.point((4, 10), fill=COLORS['redstone_mid'])
    draw.point((4, 11), fill=COLORS['redstone_mid'])
    draw.point((5, 11), fill=COLORS['redstone_bright'])

    # Bottom-right
    draw.point((11, 10), fill=COLORS['redstone_mid'])
    draw.point((10, 11), fill=COLORS['redstone_bright'])
    draw.point((11, 11), fill=COLORS['redstone_mid'])

    # Traces leading to center
    for x in [6, 9]:
        draw.point((x, 7), fill=COLORS['redstone_dark'])
        draw.point((x, 8), fill=COLORS['redstone_dark'])

    for y in [6, 9]:
        draw.point((7, y), fill=COLORS['redstone_dark'])
        draw.point((8, y), fill=COLORS['redstone_dark'])

    # === CENTRAL INDICATOR LIGHT (2x2) ===
    draw.point((7, 7), fill=COLORS['light_blue'])
    draw.point((8, 7), fill=COLORS['light_blue'])
    draw.point((7, 8), fill=COLORS['light_blue_dim'])
    draw.point((8, 8), fill=COLORS['light_blue_dim'])

    return img


def generate_controller_side():
    """
    Generate the Automaton Controller side face.

    Design:
    - Iron frame (2-3 pixels) around edge
    - Wooden center panel
    """
    img = create_block_image()
    draw = ImageDraw.Draw(img)

    # === IRON FRAME ===
    draw_iron_frame(draw)

    # === WOOD CENTER ===
    draw_wood_center(draw, 3, 3, 13, 13)

    return img


def generate_controller_front():
    """
    Generate the Automaton Controller front face.

    Design:
    - Iron frame around edge
    - Wooden center panel
    - Dispenser-like opening (dark rectangle)
    - Small indicator light near top
    """
    img = create_block_image()
    draw = ImageDraw.Draw(img)

    # === IRON FRAME ===
    draw_iron_frame(draw)

    # === WOOD CENTER ===
    draw_wood_center(draw, 3, 3, 13, 13)

    # === DISPENSER OPENING (4x4 in center) ===
    # Position: rows 5-8, cols 6-9
    for y in range(5, 9):
        for x in range(6, 10):
            if x == 6 or x == 9 or y == 5 or y == 8:
                # Edge of opening
                draw.point((x, y), fill=COLORS['void_edge'])
            else:
                # Dark interior
                draw.point((x, y), fill=COLORS['void_dark'])

    # Inner shadow gradient
    draw.point((7, 6), fill=COLORS['void_mid'])
    draw.point((8, 6), fill=COLORS['void_mid'])

    # === INDICATOR LIGHT (small, top-right of wood area) ===
    draw.point((11, 4), fill=COLORS['light_blue'])
    draw.point((11, 5), fill=COLORS['light_blue_dim'])

    return img


def generate_controller_bottom():
    """
    Generate the Automaton Controller bottom face.

    Design:
    - Iron frame around edge
    - Wooden center panel (plain)
    """
    img = create_block_image()
    draw = ImageDraw.Draw(img)

    # === IRON FRAME ===
    draw_iron_frame(draw)

    # === WOOD CENTER (simpler pattern for bottom) ===
    for y in range(3, 13):
        for x in range(3, 13):
            # Simpler grain pattern
            if (x + y * 2) % 6 == 0:
                color = COLORS['oak_dark']
            elif (x * 2 + y) % 5 == 0:
                color = COLORS['oak_light']
            else:
                color = COLORS['oak_mid']
            draw.point((x, y), fill=color)

    return img


# =============================================================================
# MAIN
# =============================================================================
if __name__ == '__main__':
    import os

    # Find the output directory relative to this script's location
    script_dir = os.path.dirname(os.path.abspath(__file__))
    project_root = os.path.dirname(script_dir)
    output_dir = os.path.join(project_root, 'src', 'main', 'resources', 'assets', 'promaton', 'textures', 'block')

    # Create output directory if needed
    os.makedirs(output_dir, exist_ok=True)

    print(f"Output directory: {output_dir}")
    print("Generating Promaton block textures...")
    print("=" * 50)

    # Generate Controller Top
    img = generate_controller_top()
    img.save(os.path.join(output_dir, 'automaton_controller_top.png'))
    print("Generated: automaton_controller_top.png")

    # Generate Controller Side
    img = generate_controller_side()
    img.save(os.path.join(output_dir, 'automaton_controller_side.png'))
    print("Generated: automaton_controller_side.png")

    # Generate Controller Front
    img = generate_controller_front()
    img.save(os.path.join(output_dir, 'automaton_controller_front.png'))
    print("Generated: automaton_controller_front.png")

    # Generate Controller Bottom
    img = generate_controller_bottom()
    img.save(os.path.join(output_dir, 'automaton_controller_bottom.png'))
    print("Generated: automaton_controller_bottom.png")

    print("=" * 50)
    print("Done!")
