#!/usr/bin/env python3
"""
Generate GUI textures for Promaton mod in vanilla Minecraft style.
Run this script to regenerate all GUI texture PNGs.

Color analysis from vanilla inventory.png:
- Panel fill: Light gray (198, 198, 198) - this is the main background
- Panel border: Black outer, White/Light highlight on top-left, Dark on bottom-right
- Slot interior: Medium gray (139, 139, 139)
- Slot border: Dark (55) on top-left, White (255) on bottom-right

The 3D effect:
- Raised elements (panels): Light highlight on top-left
- Recessed elements (slots): Dark shadow on top-left
"""

from PIL import Image, ImageDraw

# Vanilla Minecraft GUI color palette (corrected based on analysis)
COLORS = {
    'panel_fill': (198, 198, 198),   # #c6c6c6 - Light gray (main panel background)
    'slot_fill': (139, 139, 139),    # #8b8b8b - Medium gray (slot interiors)
    'border_dark': (55, 55, 55),     # #373737 - Dark borders/shadows
    'border_black': (0, 0, 0),       # #000000 - Black (outer edges)
    'border_white': (255, 255, 255), # #ffffff - White highlights
    'border_medium': (85, 85, 85),   # #555555 - Medium dark gray
    'transparent': (0, 0, 0, 0),     # Transparent
}

def create_image(width, height):
    """Create a new RGBA image with transparent background."""
    return Image.new('RGBA', (width, height), COLORS['transparent'])

def draw_panel_background(draw, x, y, width, height):
    """
    Draw a vanilla-style panel background with 3D border effect and rounded corners.
    Based on detailed vanilla inventory.png analysis:

    LEFT/TOP edges: Black border (1px) → White highlight (2px) → Light gray fill
    RIGHT/BOTTOM edges: Light gray fill → Dark gray shadow (2px) → Black border (1px)

    Corner patterns (transparent pixels):
    - Top-left: (0,0), (1,0), (0,1)
    - Top-right: (w-1,0), (w-2,0), (w-3,0), (w-1,1), (w-2,1), (w-1,2)
    - Bottom-left: (0,h-1), (1,h-1), (2,h-1), (0,h-2), (1,h-2), (0,h-3)
    - Bottom-right: (w-1,h-1), (w-2,h-1), (w-1,h-2)
    """
    w, h = width, height
    transparent = (0, 0, 0, 0)

    # 1. Fill with light gray
    draw.rectangle([x, y, x + w - 1, y + h - 1], fill=COLORS['panel_fill'])

    # 2. Draw dark gray shadow on right edge (2 pixels wide, inside the black border)
    draw.line([(x + w - 3, y + 3), (x + w - 3, y + h - 4)], fill=COLORS['border_medium'])
    draw.line([(x + w - 2, y + 3), (x + w - 2, y + h - 4)], fill=COLORS['border_medium'])

    # 3. Draw dark gray shadow on bottom edge (2 pixels tall, inside the black border)
    draw.line([(x + 3, y + h - 3), (x + w - 4, y + h - 3)], fill=COLORS['border_medium'])
    draw.line([(x + 3, y + h - 2), (x + w - 4, y + h - 2)], fill=COLORS['border_medium'])

    # 4. Draw black border
    # Top edge
    draw.line([(x + 2, y), (x + w - 4, y)], fill=COLORS['border_black'])
    # Left edge
    draw.line([(x, y + 2), (x, y + h - 4)], fill=COLORS['border_black'])
    # Right edge
    draw.line([(x + w - 1, y + 3), (x + w - 1, y + h - 3)], fill=COLORS['border_black'])
    # Bottom edge
    draw.line([(x + 3, y + h - 1), (x + w - 3, y + h - 1)], fill=COLORS['border_black'])

    # 5. Corner black pixels for rounded effect
    # Top-left corner
    draw.point((x + 1, y + 1), fill=COLORS['border_black'])
    # Top-right corner
    draw.point((x + w - 2, y + 1), fill=COLORS['border_black'])
    draw.point((x + w - 3, y + 1), fill=COLORS['border_black'])
    draw.point((x + w - 2, y + 2), fill=COLORS['border_black'])
    # Bottom-left corner
    draw.point((x + 1, y + h - 2), fill=COLORS['border_black'])
    draw.point((x + 1, y + h - 3), fill=COLORS['border_black'])
    draw.point((x + 2, y + h - 2), fill=COLORS['border_black'])
    # Bottom-right corner
    draw.point((x + w - 2, y + h - 2), fill=COLORS['border_black'])

    # 6. Make corners transparent
    # Top-left: 2 on row 0, 1 on row 1
    draw.point((x, y), fill=transparent)
    draw.point((x + 1, y), fill=transparent)
    draw.point((x, y + 1), fill=transparent)
    # Top-right: 3 on row 0, 2 on row 1, 1 on row 2
    draw.point((x + w - 1, y), fill=transparent)
    draw.point((x + w - 2, y), fill=transparent)
    draw.point((x + w - 3, y), fill=transparent)
    draw.point((x + w - 1, y + 1), fill=transparent)
    draw.point((x + w - 2, y + 1), fill=transparent)
    draw.point((x + w - 1, y + 2), fill=transparent)
    # Bottom-left: 3 on bottom row, 2 on row above, 1 on row above that
    draw.point((x, y + h - 1), fill=transparent)
    draw.point((x + 1, y + h - 1), fill=transparent)
    draw.point((x + 2, y + h - 1), fill=transparent)
    draw.point((x, y + h - 2), fill=transparent)
    draw.point((x + 1, y + h - 2), fill=transparent)
    draw.point((x, y + h - 3), fill=transparent)
    # Bottom-right: 2 on bottom row, 1 on row above
    draw.point((x + w - 1, y + h - 1), fill=transparent)
    draw.point((x + w - 2, y + h - 1), fill=transparent)
    draw.point((x + w - 1, y + h - 2), fill=transparent)

    # 7. White highlight on top edge (inside black border)
    draw.line([(x + 2, y + 1), (x + w - 4, y + 1)], fill=COLORS['border_white'])
    draw.line([(x + 3, y + 2), (x + w - 4, y + 2)], fill=COLORS['border_white'])

    # 8. White highlight on left edge (inside black border)
    draw.line([(x + 1, y + 2), (x + 1, y + h - 4)], fill=COLORS['border_white'])
    draw.line([(x + 2, y + 3), (x + 2, y + h - 4)], fill=COLORS['border_white'])

    # 9. Fix corner transitions - fill in pixels that lines missed

    # TOP-LEFT: White curves around the black corner pixel at (1,1)
    # Need W at: (2,1), (1,2), and (2,2) to match vanilla
    draw.point((x + 2, y + 1), fill=COLORS['border_white'])
    draw.point((x + 1, y + 2), fill=COLORS['border_white'])
    draw.point((x + 2, y + 2), fill=COLORS['border_white'])  # corner of highlight

    # TOP-RIGHT: white flows into the corner area
    draw.point((x + w - 4, y + 1), fill=COLORS['border_white'])
    draw.point((x + w - 4, y + 2), fill=COLORS['border_white'])
    draw.point((x + w - 3, y + 2), fill=COLORS['panel_fill'])

    # BOTTOM-LEFT: white flows down, dark gray fills corner
    draw.point((x + 1, y + h - 4), fill=COLORS['border_white'])
    draw.point((x + 2, y + h - 4), fill=COLORS['border_white'])
    draw.point((x + 2, y + h - 3), fill=COLORS['panel_fill'])
    draw.point((x + 3, y + h - 3), fill=COLORS['border_medium'])

    # BOTTOM-RIGHT: Dark gray shadow fills corner area
    # Need d at: (w-3, h-4), (w-4, h-3), (w-3, h-3), (w-4, h-2), (w-3, h-2), (w-2, h-2)=black
    draw.point((x + w - 3, y + 3), fill=COLORS['border_medium'])
    draw.point((x + w - 3, y + h - 4), fill=COLORS['border_medium'])
    draw.point((x + w - 4, y + h - 3), fill=COLORS['border_medium'])
    draw.point((x + w - 3, y + h - 3), fill=COLORS['border_medium'])
    draw.point((x + w - 4, y + h - 2), fill=COLORS['border_medium'])
    draw.point((x + w - 3, y + h - 2), fill=COLORS['border_medium'])
    draw.point((x + w - 2, y + h - 2), fill=COLORS['border_black'])  # rounded corner pixel

def draw_slot(draw, x, y, size=18):
    """
    Draw a vanilla-style inventory slot (18x18 by default).
    Based on vanilla analysis:
    - Dark (55) border on TOP and LEFT (creates recessed look)
    - White (255) border on BOTTOM and RIGHT
    - Medium gray (139) interior
    """
    # Top border - dark
    draw.line([(x, y), (x + size - 2, y)], fill=COLORS['border_dark'])
    # Left border - dark
    draw.line([(x, y), (x, y + size - 2)], fill=COLORS['border_dark'])

    # Bottom border - white
    draw.line([(x, y + size - 1), (x + size - 1, y + size - 1)], fill=COLORS['border_white'])
    # Right border - white
    draw.line([(x + size - 1, y), (x + size - 1, y + size - 1)], fill=COLORS['border_white'])

    # Corner pixel (bottom-left of top border meets top of left border)
    draw.point((x + size - 1, y), fill=COLORS['slot_fill'])

    # Interior fill - medium gray
    draw.rectangle([x + 1, y + 1, x + size - 2, y + size - 2], fill=COLORS['slot_fill'])

def draw_slot_grid(draw, start_x, start_y, cols, rows, slot_size=18):
    """Draw a grid of inventory slots."""
    for row in range(rows):
        for col in range(cols):
            x = start_x + col * slot_size
            y = start_y + row * slot_size
            draw_slot(draw, x, y, slot_size)

def draw_separator_line(draw, x1, y1, x2, y2):
    """Draw a horizontal or vertical separator line (recessed style)."""
    draw.line([(x1, y1), (x2, y2)], fill=COLORS['border_dark'])
    if y1 == y2:  # Horizontal
        draw.line([(x1, y1 + 1), (x2, y2 + 1)], fill=COLORS['border_white'])
    else:  # Vertical
        draw.line([(x1 + 1, y1), (x2 + 1, y2)], fill=COLORS['border_white'])

def draw_recessed_area(draw, x, y, width, height):
    """
    Draw a recessed/inset area (like text input or content panel).
    Dark border on top-left, white on bottom-right, darker fill.
    """
    # Dark border on top and left
    draw.line([(x, y), (x + width - 1, y)], fill=COLORS['border_dark'])
    draw.line([(x, y), (x, y + height - 1)], fill=COLORS['border_dark'])

    # White border on bottom and right
    draw.line([(x, y + height - 1), (x + width - 1, y + height - 1)], fill=COLORS['border_white'])
    draw.line([(x + width - 1, y), (x + width - 1, y + height - 1)], fill=COLORS['border_white'])

    # Darker fill (slot color)
    draw.rectangle([x + 1, y + 1, x + width - 2, y + height - 2], fill=COLORS['slot_fill'])

def draw_text_field(draw, x, y, width, height):
    """
    Draw a text input field (black border, dark inner edge, black fill).
    """
    # Black border
    draw.rectangle([x, y, x + width - 1, y + height - 1], outline=COLORS['border_black'])

    # Slightly lighter fill for text visibility
    draw.rectangle([x + 1, y + 1, x + width - 2, y + height - 2], fill=COLORS['border_dark'])

def draw_button_area(draw, x, y, width, height):
    """Draw a raised button background area (matches panel style but smaller)."""
    # Outer edge
    draw.rectangle([x, y, x + width - 1, y + height - 1], outline=COLORS['border_dark'])

    # White highlight top-left
    draw.line([(x + 1, y + 1), (x + width - 2, y + 1)], fill=COLORS['border_white'])


def fix_panel_corners(draw, x, y, w, h):
    """
    Fix the panel corner pixels after all content has been drawn.
    Call this at the END of each GUI generation function to ensure
    corners aren't overwritten by content drawing.
    """
    transparent = (0, 0, 0, 0)

    # TOP-LEFT: Ensure white curves around black corner at (1,1)
    draw.point((x + 2, y + 1), fill=COLORS['border_white'])
    draw.point((x + 1, y + 2), fill=COLORS['border_white'])
    draw.point((x + 2, y + 2), fill=COLORS['border_white'])
    draw.point((x + 3, y + 3), fill=COLORS['border_white'])  # extends the highlight corner

    # TOP-RIGHT: Ensure proper corner rounding
    draw.point((x + w - 2, y + 1), fill=COLORS['border_black'])
    draw.point((x + w - 3, y + 1), fill=COLORS['border_black'])
    draw.point((x + w - 2, y + 2), fill=COLORS['border_black'])
    draw.point((x + w - 1, y), fill=transparent)
    draw.point((x + w - 2, y), fill=transparent)
    draw.point((x + w - 3, y), fill=transparent)
    draw.point((x + w - 1, y + 1), fill=transparent)
    draw.point((x + w - 2, y + 1), fill=transparent)
    draw.point((x + w - 1, y + 2), fill=transparent)

    # BOTTOM-LEFT: Ensure proper corner rounding
    draw.point((x + 1, y + h - 2), fill=COLORS['border_black'])
    draw.point((x + 1, y + h - 3), fill=COLORS['border_black'])
    draw.point((x + 2, y + h - 2), fill=COLORS['border_black'])
    draw.point((x, y + h - 1), fill=transparent)
    draw.point((x + 1, y + h - 1), fill=transparent)
    draw.point((x + 2, y + h - 1), fill=transparent)
    draw.point((x, y + h - 2), fill=transparent)
    draw.point((x + 1, y + h - 2), fill=transparent)
    draw.point((x, y + h - 3), fill=transparent)

    # BOTTOM-RIGHT: Ensure dark shadow fills corner and black rounding pixel
    draw.point((x + w - 4, y + h - 4), fill=COLORS['border_medium'])
    draw.point((x + w - 3, y + h - 4), fill=COLORS['border_medium'])
    draw.point((x + w - 4, y + h - 3), fill=COLORS['border_medium'])
    draw.point((x + w - 3, y + h - 3), fill=COLORS['border_medium'])
    draw.point((x + w - 2, y + h - 3), fill=COLORS['border_medium'])  # This was missing!
    draw.point((x + w - 4, y + h - 2), fill=COLORS['border_medium'])
    draw.point((x + w - 3, y + h - 2), fill=COLORS['border_medium'])
    draw.point((x + w - 2, y + h - 2), fill=COLORS['border_black'])
    draw.point((x + w - 1, y + h - 1), fill=transparent)
    draw.point((x + w - 2, y + h - 1), fill=transparent)
    draw.point((x + w - 1, y + h - 2), fill=transparent)


# =============================================================================
# CONTROLLER INTERFACE - Shared constants and helper
# =============================================================================
# All Controller Interface tabs share:
# - Same panel dimensions (176 x 222)
# - Title area at top (17px)
# - Content area (full width)
# - Horizontal control bar (Program slot + Run/Stop | Casing slot + Summon)
# - Player inventory + hotbar at bottom

CTRL_GUI_WIDTH = 176
CTRL_GUI_HEIGHT = 222
CTRL_GUI_X = (256 - CTRL_GUI_WIDTH) // 2
CTRL_GUI_Y = 28  # Leave space for 4 tabs

# Calculate from bottom up to position inventory correctly:
# Panel bottom = 28 + 222 = 250, bottom border ~7px
# Hotbar (18px) ends at 243, starts at 225
# Gap (4px), player inv (54px) starts at 167
# Gap (10px), control bar (20px) starts at 137
# Gap (4px), content area starts at 45

CTRL_PLAYER_INV_Y = 167  # Positioned near panel bottom
CTRL_HOTBAR_Y = CTRL_PLAYER_INV_Y + 3 * 18 + 4  # = 225

CTRL_CONTROL_BAR_Y = CTRL_PLAYER_INV_Y - 10 - 20  # = 137
CTRL_CONTROL_BAR_HEIGHT = 20

CTRL_CONTENT_Y = CTRL_GUI_Y + 17  # = 45, after title area
CTRL_CONTENT_HEIGHT = CTRL_CONTROL_BAR_Y - 4 - CTRL_CONTENT_Y  # = 88


def draw_controller_base(draw, gui_x, gui_y, gui_width, gui_height, include_clear_button=False):
    """Draw the common elements for all Controller Interface tabs.

    Args:
        include_clear_button: If True, adds a Clear button at the end of the control bar (for Logs tab)
    """

    # Draw main panel background
    draw_panel_background(draw, gui_x, gui_y, gui_width, gui_height)

    # Horizontal control bar: [Program Slot][Run/Stop] | [Casing Slot][Summon] [| Clear]
    bar_y = CTRL_CONTROL_BAR_Y
    bar_x = gui_x + 7

    # Use narrower buttons to fit Clear button when needed (consistent across all tabs)
    btn_width = 35

    # Left group: Program slot + Run/Stop button
    draw_slot(draw, bar_x, bar_y + 1)  # +1 to center in 20px bar
    run_btn_x = bar_x + 18 + 3
    draw_button_area(draw, run_btn_x, bar_y + 1, btn_width, 18)

    # Divider line (vertical) - centered between groups
    div_x = run_btn_x + btn_width + 3
    draw_separator_line(draw, div_x, bar_y + 2, div_x, bar_y + CTRL_CONTROL_BAR_HEIGHT - 2)

    # Right group: Casing slot + Summon button
    casing_x = div_x + 5
    draw_slot(draw, casing_x, bar_y + 1)
    summon_btn_x = casing_x + 18 + 3
    draw_button_area(draw, summon_btn_x, bar_y + 1, btn_width, 18)

    # Optional Clear button (for Logs tab)
    if include_clear_button:
        div2_x = summon_btn_x + btn_width + 3
        draw_separator_line(draw, div2_x, bar_y + 2, div2_x, bar_y + CTRL_CONTROL_BAR_HEIGHT - 2)
        clear_btn_x = div2_x + 5
        clear_btn_width = gui_x + gui_width - 7 - clear_btn_x  # Fill to right edge
        draw_button_area(draw, clear_btn_x, bar_y + 1, clear_btn_width, 18)

    # Player inventory (3 rows of 9)
    draw_slot_grid(draw, gui_x + 7, CTRL_PLAYER_INV_Y, 9, 3)

    # Hotbar (1 row of 9)
    draw_slot_grid(draw, gui_x + 7, CTRL_HOTBAR_Y, 9, 1)


# =============================================================================
# CONTROLLER INTERFACE - STATUS TAB
# =============================================================================
def generate_controller_status():
    """
    Generate Controller Interface STATUS tab.
    Content: Info panel with automaton details (Name, Status, Health, Controller, Rest, Hunger, XP, Task, Location)
    """
    img = create_image(256, 256)
    draw = ImageDraw.Draw(img)

    gui_x, gui_y = CTRL_GUI_X, CTRL_GUI_Y
    gui_width, gui_height = CTRL_GUI_WIDTH, CTRL_GUI_HEIGHT

    # Draw common elements
    draw_controller_base(draw, gui_x, gui_y, gui_width, gui_height)

    # Content: Info panel (recessed area for status info)
    content_x = gui_x + 6
    content_y = CTRL_CONTENT_Y
    content_width = gui_width - 12
    content_height = CTRL_CONTENT_HEIGHT
    draw_recessed_area(draw, content_x, content_y, content_width, content_height)

    # Status info will be rendered by code:
    # - Name, Status, Health (left column)
    # - Controller, Rest, Hunger (right column)
    # - XP, Current Task, Location (additional rows)

    # Fix corners
    fix_panel_corners(draw, gui_x, gui_y, gui_width, gui_height)

    img.save('container/controller_status.png')
    print("Generated: container/controller_status.png")
    return img


# =============================================================================
# CONTROLLER INTERFACE - CONTROL TAB
# =============================================================================
def generate_controller_control():
    """
    Generate Controller Interface CONTROL tab.
    Content: Manual command buttons (Return Home, Stay, Follow Me, Enlist/Dismiss)
    """
    img = create_image(256, 256)
    draw = ImageDraw.Draw(img)

    gui_x, gui_y = CTRL_GUI_X, CTRL_GUI_Y
    gui_width, gui_height = CTRL_GUI_WIDTH, CTRL_GUI_HEIGHT

    # Draw common elements
    draw_controller_base(draw, gui_x, gui_y, gui_width, gui_height)

    # Content: 2-column button layout for manual commands, centered vertically
    content_x = gui_x + 7
    total_width = gui_width - 14  # 162px
    btn_gap = 4
    btn_width = (total_width - btn_gap) // 2  # ~79px per button
    btn_height = 20

    # Center the 2x2 button grid vertically in content area
    # Grid height = 2 rows * 20px + 1 gap * 4px = 44px
    grid_height = 2 * btn_height + btn_gap
    vertical_offset = (CTRL_CONTENT_HEIGHT - grid_height) // 2
    grid_y = CTRL_CONTENT_Y + vertical_offset

    # Row 1: Return Home (left), Stay (right)
    draw_button_area(draw, content_x, grid_y, btn_width, btn_height)
    draw_button_area(draw, content_x + btn_width + btn_gap, grid_y, btn_width, btn_height)

    # Row 2: Follow Me (left), Enlist/Dismiss (right)
    row2_y = grid_y + btn_height + btn_gap
    draw_button_area(draw, content_x, row2_y, btn_width, btn_height)
    draw_button_area(draw, content_x + btn_width + btn_gap, row2_y, btn_width, btn_height)

    # Fix corners
    fix_panel_corners(draw, gui_x, gui_y, gui_width, gui_height)

    img.save('container/controller_control.png')
    print("Generated: container/controller_control.png")
    return img


# =============================================================================
# CONTROLLER INTERFACE - LOGS TAB
# =============================================================================
def generate_controller_logs():
    """
    Generate Controller Interface LOGS tab.
    Content: Scrollable log area (full height)
    Clear button is in the control bar
    """
    img = create_image(256, 256)
    draw = ImageDraw.Draw(img)

    gui_x, gui_y = CTRL_GUI_X, CTRL_GUI_Y
    gui_width, gui_height = CTRL_GUI_WIDTH, CTRL_GUI_HEIGHT

    # Draw common elements with Clear button in control bar
    draw_controller_base(draw, gui_x, gui_y, gui_width, gui_height, include_clear_button=True)

    # Content: Log area (recessed) + scroll track - full height now!
    content_x = gui_x + 7
    content_y = CTRL_CONTENT_Y

    scroll_track_width = 14
    log_width = gui_width - 14 - scroll_track_width - 4  # -4 for gap before scroll
    log_height = CTRL_CONTENT_HEIGHT  # Full height - Clear button moved to control bar

    # Log text area (recessed)
    draw_recessed_area(draw, content_x, content_y, log_width, log_height)

    # Scroll track (recessed, like vanilla creative)
    scroll_x = content_x + log_width + 4
    draw.line([(scroll_x, content_y), (scroll_x + scroll_track_width - 1, content_y)], fill=COLORS['border_dark'])
    draw.line([(scroll_x, content_y), (scroll_x, content_y + log_height - 1)], fill=COLORS['border_dark'])
    draw.line([(scroll_x, content_y + log_height - 1), (scroll_x + scroll_track_width - 1, content_y + log_height - 1)], fill=COLORS['border_white'])
    draw.line([(scroll_x + scroll_track_width - 1, content_y), (scroll_x + scroll_track_width - 1, content_y + log_height - 1)], fill=COLORS['border_white'])
    draw.rectangle([scroll_x + 1, content_y + 1, scroll_x + scroll_track_width - 2, content_y + log_height - 2], fill=COLORS['slot_fill'])

    # Fix corners
    fix_panel_corners(draw, gui_x, gui_y, gui_width, gui_height)

    img.save('container/controller_logs.png')
    print("Generated: container/controller_logs.png")
    return img


# =============================================================================
# CONTROLLER INTERFACE - SKIN TAB
# =============================================================================
def generate_controller_skin():
    """
    Generate Controller Interface SKIN tab.
    Uses same panel size as other controller tabs (176×222) but NO control bar.
    Layout: Preview (left), skin list (middle), scroll track (right)
    """
    img = create_image(256, 256)
    draw = ImageDraw.Draw(img)

    # Use standard controller panel dimensions (matches other tabs)
    gui_width = CTRL_GUI_WIDTH  # 176
    gui_height = CTRL_GUI_HEIGHT  # 222
    gui_x = CTRL_GUI_X
    gui_y = CTRL_GUI_Y  # 28

    # Draw main panel (no control bar)
    draw_panel_background(draw, gui_x, gui_y, gui_width, gui_height)

    # Content layout - no control bar means more space for skin content
    content_y = gui_y + 17  # After title area

    # Player inventory at same position as other controller tabs
    player_inv_y = CTRL_PLAYER_INV_Y  # 155
    hotbar_y = CTRL_HOTBAR_Y  # 213

    # Content height: from title area to gap before player inventory
    content_height = player_inv_y - 10 - content_y  # 128px (vs 76px with control bar!)

    preview_width = 51
    gap = 4
    scroll_track_width = 14
    gap_before_scroll = 2
    list_width = gui_width - 14 - preview_width - gap - gap_before_scroll - scroll_track_width

    # Preview area (black, for entity rendering)
    preview_x = gui_x + 7
    preview_height = 70
    draw.rectangle([preview_x, content_y, preview_x + preview_width - 1, content_y + preview_height - 1],
                   fill=COLORS['border_black'])

    # Buttons under preview (stacked vertically)
    btn_height = 16
    btn_gap = 4
    select_btn_y = content_y + preview_height + btn_gap
    draw_button_area(draw, preview_x, select_btn_y, preview_width, btn_height)
    open_btn_y = select_btn_y + btn_height + btn_gap
    draw_button_area(draw, preview_x, open_btn_y, preview_width, btn_height)

    # Skin list (recessed, full height)
    list_x = preview_x + preview_width + gap
    draw_recessed_area(draw, list_x, content_y, list_width, content_height)

    # Scroll track (recessed)
    scroll_x = list_x + list_width + gap_before_scroll
    draw.line([(scroll_x, content_y), (scroll_x + scroll_track_width - 1, content_y)], fill=COLORS['border_dark'])
    draw.line([(scroll_x, content_y), (scroll_x, content_y + content_height - 1)], fill=COLORS['border_dark'])
    draw.line([(scroll_x, content_y + content_height - 1), (scroll_x + scroll_track_width - 1, content_y + content_height - 1)], fill=COLORS['border_white'])
    draw.line([(scroll_x + scroll_track_width - 1, content_y), (scroll_x + scroll_track_width - 1, content_y + content_height - 1)], fill=COLORS['border_white'])
    draw.rectangle([scroll_x + 1, content_y + 1, scroll_x + scroll_track_width - 2, content_y + content_height - 2], fill=COLORS['slot_fill'])

    # Player inventory (same position as other controller tabs)
    draw_slot_grid(draw, gui_x + 7, player_inv_y, 9, 3)

    # Hotbar
    draw_slot_grid(draw, gui_x + 7, hotbar_y, 9, 1)

    # Fix corners
    fix_panel_corners(draw, gui_x, gui_y, gui_width, gui_height)

    img.save('container/controller_skin.png')
    print("Generated: container/controller_skin.png")
    return img


# =============================================================================
# PROGRAM EDITOR (256x256)
# =============================================================================
def generate_program_editor():
    """
    Generate the Program Editor GUI texture.
    Layout:
    - Main panel with tab row space at top
    - Large text editing area (recessed)
    - Button row at bottom (Compile, Save)

    Tabs are rendered as widgets by code.
    """
    img = create_image(256, 256)
    draw = ImageDraw.Draw(img)

    # Wide for text editing
    gui_width = 220
    gui_height = 180
    gui_x = (256 - gui_width) // 2
    gui_y = 28  # Leave space for 6 tabs

    # Draw main panel background
    draw_panel_background(draw, gui_x, gui_y, gui_width, gui_height)

    # Text editing area (recessed) with scroll track outside
    # 17px title area at top (matching other GUIs)
    text_x = gui_x + 6
    text_y = gui_y + 17
    scroll_track_width = 14
    gap_before_scroll = 4
    text_width = gui_width - 12 - gap_before_scroll - scroll_track_width
    text_height = gui_height - 47  # 17px title + 30px button area
    draw_recessed_area(draw, text_x, text_y, text_width, text_height)

    # Scroll bar track (outside text area, recessed style)
    scroll_x = text_x + text_width + gap_before_scroll
    scroll_y = text_y
    scroll_height = text_height
    # Dark border on top and left
    draw.line([(scroll_x, scroll_y), (scroll_x + scroll_track_width - 1, scroll_y)], fill=COLORS['border_dark'])
    draw.line([(scroll_x, scroll_y), (scroll_x, scroll_y + scroll_height - 1)], fill=COLORS['border_dark'])
    # White border on bottom and right
    draw.line([(scroll_x, scroll_y + scroll_height - 1), (scroll_x + scroll_track_width - 1, scroll_y + scroll_height - 1)], fill=COLORS['border_white'])
    draw.line([(scroll_x + scroll_track_width - 1, scroll_y), (scroll_x + scroll_track_width - 1, scroll_y + scroll_height - 1)], fill=COLORS['border_white'])
    # Interior fill
    draw.rectangle([scroll_x + 1, scroll_y + 1, scroll_x + scroll_track_width - 2, scroll_y + scroll_height - 2], fill=COLORS['slot_fill'])

    # Button row at bottom
    btn_y = gui_y + gui_height - 24
    btn_width = 60
    btn_height = 18

    # Compile button
    compile_x = gui_x + gui_width // 2 - btn_width - 8
    draw_button_area(draw, compile_x, btn_y, btn_width, btn_height)

    # Save button
    save_x = gui_x + gui_width // 2 + 8
    draw_button_area(draw, save_x, btn_y, btn_width, btn_height)

    # Fix corners after all content is drawn
    fix_panel_corners(draw, gui_x, gui_y, gui_width, gui_height)

    # Save
    img.save('container/program_editor.png')
    print("Generated: container/program_editor.png")
    return img


# =============================================================================
# AUTOMATON GUI - INVENTORY TAB (256x256)
# =============================================================================
def generate_automaton_gui_inventory():
    """
    Generate the Automaton GUI texture for the INVENTORY tab.
    Layout (176 wide to match vanilla inventory):
    - Title area for tab name
    - Info panel (face preview, name area)
    - Equipment slots (6 horizontal)
    - Automaton inventory (27 slots)
    - Player inventory (27 + 9 hotbar)

    Tabs are rendered as widgets by code.
    """
    img = create_image(256, 256)
    draw = ImageDraw.Draw(img)

    # Match vanilla inventory width
    gui_width = 176
    gui_height = 232  # Includes 14px title area at top + 38px info panel + vanilla-style inventory spacing
    gui_x = (256 - gui_width) // 2
    gui_y = 24  # Leave space for tabs (24 + 232 = 256, fits exactly)

    # Draw main panel
    draw_panel_background(draw, gui_x, gui_y, gui_width, gui_height)

    # Title area: 17px from panel top to content (3px border + 14px for tab name text)
    # Text is rendered by code, not in texture

    # Info panel area (recessed, top section)
    # Fits 3 rows of info in 2 columns:
    # Left column: Name, Status, Health bar
    # Right column: Controller, Rest, Hunger bar
    info_x = gui_x + 6
    info_y = gui_y + 17  # After title area
    info_width = gui_width - 12  # 164px wide
    info_height = 38  # Fits 3 lines per column
    draw_recessed_area(draw, info_x, info_y, info_width, info_height)

    # No face preview - player can use Skin tab to see the automaton's appearance
    # Full width available for 2 text columns (~80px each)
    # Health/Hunger bars (10 icons × 8px = 80px) will fit!
    #
    # Layout (rendered by code):
    # ┌────────────────────────────────────────────┐
    # │ Name: ________      Controller: __________ │
    # │ Status: ______      Rest: ________________ │
    # │ ♥♥♥♥♥♥♥♥♥♥          🍖🍖🍖🍖🍖🍖🍖🍖🍖🍖 │
    # └────────────────────────────────────────────┘

    # Equipment slots row (6 slots horizontal, left-aligned)
    equip_y = info_y + info_height + 4
    equip_start_x = gui_x + 7  # Left-aligned with inventory below
    draw_slot_grid(draw, equip_start_x, equip_y, 6, 1)

    # Enlist/Dismiss button (right of equipment slots)
    # 6 slots = 108px, gap = 4px, remaining width for button
    enlist_btn_x = equip_start_x + 6 * 18 + 4
    enlist_btn_width = gui_x + gui_width - 7 - enlist_btn_x  # Fill to right edge padding
    enlist_btn_height = 18  # Same height as equipment slot row
    draw_button_area(draw, enlist_btn_x, equip_y, enlist_btn_width, enlist_btn_height)

    # Automaton inventory (27 slots - 3 rows of 9)
    auto_inv_y = equip_y + 22
    auto_inv_x = gui_x + 7
    draw_slot_grid(draw, auto_inv_x, auto_inv_y, 9, 3)

    # Player inventory (27 slots - 3 rows of 9)
    # 14px gap matches vanilla chest GUI spacing between container and player inventory
    player_inv_y = auto_inv_y + 3 * 18 + 14
    draw_slot_grid(draw, gui_x + 7, player_inv_y, 9, 3)

    # Hotbar (9 slots)
    # 4px gap matches vanilla spacing between main inventory and hotbar
    hotbar_y = player_inv_y + 3 * 18 + 4
    draw_slot_grid(draw, gui_x + 7, hotbar_y, 9, 1)

    # Fix corners after all content is drawn
    fix_panel_corners(draw, gui_x, gui_y, gui_width, gui_height)

    # Save
    img.save('container/automaton_gui_inventory.png')
    print("Generated: container/automaton_gui_inventory.png")
    return img


# =============================================================================
# AUTOMATON GUI - SKIN TAB (256x256)
# =============================================================================
def generate_automaton_gui_skin():
    """
    Generate the Automaton GUI texture for the SKIN tab.
    Layout (176 wide to match vanilla inventory):
    - Title area for tab name
    - Preview area (LEFT, dark - like vanilla inventory player preview)
    - Buttons stacked under preview (Select on top, Open Folder below)
    - Skin list (MIDDLE, recessed, full height)
    - Scroll bar track (RIGHT, recessed like vanilla creative inventory)
    - Player inventory (27 + 9 hotbar) - same as inventory tab

    Tabs are rendered as widgets by code.
    Scroll bar thumb is rendered as widget on top of the recessed track.
    """
    img = create_image(256, 256)
    draw = ImageDraw.Draw(img)

    # Same dimensions as inventory tab
    gui_width = 176
    gui_height = 232  # Matches inventory tab
    gui_x = (256 - gui_width) // 2
    gui_y = 24  # Leave space for tabs (24 + 232 = 256, fits exactly)

    # Draw main panel
    draw_panel_background(draw, gui_x, gui_y, gui_width, gui_height)

    # Title area: 17px from panel top to content
    content_y = gui_y + 17

    # Calculate where player inventory starts (must match inventory tab)
    # From inventory tab: player_inv_y = gui_y + 149 (increased by 12px for taller info panel)
    player_inv_y = gui_y + 149

    # Content area height (from title to player inventory gap)
    # Content ends 14px before player inventory
    content_height = player_inv_y - 14 - content_y  # 106px available

    # Layout widths:
    # - Preview + buttons column: 51px
    # - Gap: 4px
    # - Skin list: flexible
    # - Gap before scroll: 2px
    # - Scroll track: 14px (1px dark + 12px interior + 1px white, like vanilla creative)
    # Total content width: 176 - 14 = 162px

    preview_width = 51
    gap = 4
    scroll_track_width = 14
    gap_before_scroll = 2
    list_width = 162 - preview_width - gap - gap_before_scroll - scroll_track_width  # = 91px

    # LEFT COLUMN: Preview area + buttons stacked vertically
    preview_x = gui_x + 7
    preview_y = content_y
    preview_height = 70  # Match vanilla inventory player preview height

    # Black filled rectangle for entity preview
    draw.rectangle([preview_x, preview_y, preview_x + preview_width - 1, preview_y + preview_height - 1],
                   fill=COLORS['border_black'])

    # Select button (under preview)
    btn_height = 16
    btn_gap = 4  # Restored spacing now that panel is taller
    select_btn_y = preview_y + preview_height + btn_gap
    draw_button_area(draw, preview_x, select_btn_y, preview_width, btn_height)

    # Open Folder button (under Select)
    open_btn_y = select_btn_y + btn_height + btn_gap
    draw_button_area(draw, preview_x, open_btn_y, preview_width, btn_height)

    # MIDDLE: Skin list area (recessed, full height)
    list_x = preview_x + preview_width + gap
    list_y = content_y
    list_height = content_height  # Full height now that buttons are on the left
    draw_recessed_area(draw, list_x, list_y, list_width, list_height)

    # RIGHT: Scroll bar track (recessed, like vanilla creative inventory)
    # Structure: 1px dark border (left) + 12px slot fill interior + 1px white border (right)
    scroll_x = list_x + list_width + gap_before_scroll
    scroll_y = content_y
    scroll_height = content_height

    # Draw recessed scroll track (same style as slots/recessed areas)
    # Dark border on top and left
    draw.line([(scroll_x, scroll_y), (scroll_x + scroll_track_width - 1, scroll_y)], fill=COLORS['border_dark'])
    draw.line([(scroll_x, scroll_y), (scroll_x, scroll_y + scroll_height - 1)], fill=COLORS['border_dark'])
    # White border on bottom and right
    draw.line([(scroll_x, scroll_y + scroll_height - 1), (scroll_x + scroll_track_width - 1, scroll_y + scroll_height - 1)], fill=COLORS['border_white'])
    draw.line([(scroll_x + scroll_track_width - 1, scroll_y), (scroll_x + scroll_track_width - 1, scroll_y + scroll_height - 1)], fill=COLORS['border_white'])
    # Interior fill (slot gray)
    draw.rectangle([scroll_x + 1, scroll_y + 1, scroll_x + scroll_track_width - 2, scroll_y + scroll_height - 2],
                   fill=COLORS['slot_fill'])

    # Player inventory (27 slots - 3 rows of 9) - same position as inventory tab
    draw_slot_grid(draw, gui_x + 7, player_inv_y, 9, 3)

    # Hotbar (9 slots) - same position as inventory tab
    hotbar_y = player_inv_y + 3 * 18 + 4
    draw_slot_grid(draw, gui_x + 7, hotbar_y, 9, 1)

    # Fix corners after all content is drawn
    fix_panel_corners(draw, gui_x, gui_y, gui_width, gui_height)

    # Save
    img.save('container/automaton_gui_skin.png')
    print("Generated: container/automaton_gui_skin.png")
    return img


# =============================================================================
# SKIN SELECTOR PANEL (content for Skin tab)
# =============================================================================
def generate_skin_selector():
    """
    Generate a skin selector panel texture.
    This is the content that appears in the Skin tab.
    Layout:
    - Left: Skin list (scrollable, recessed)
    - Right: Preview area (dark, for entity rendering)
    - Bottom: Buttons
    """
    img = create_image(256, 128)
    draw = ImageDraw.Draw(img)

    # This panel fits inside a tab content area
    panel_width = 180
    panel_height = 100
    panel_x = (256 - panel_width) // 2
    panel_y = 4

    # Draw panel background
    draw_panel_background(draw, panel_x, panel_y, panel_width, panel_height)

    # Left side: Skin list (recessed)
    list_x = panel_x + 5
    list_y = panel_y + 5
    list_width = 70
    list_height = panel_height - 28
    draw_recessed_area(draw, list_x, list_y, list_width, list_height)

    # Scroll bar track for list
    scroll_x = list_x + list_width - 10
    scroll_y = list_y + 2
    scroll_height = list_height - 4
    draw.rectangle([scroll_x, scroll_y, scroll_x + 6, scroll_y + scroll_height],
                   fill=COLORS['border_dark'])
    # Scroll thumb
    draw.rectangle([scroll_x + 1, scroll_y + 1, scroll_x + 5, scroll_y + 15],
                   fill=COLORS['panel_fill'])

    # Right side: Preview area (dark for entity rendering)
    preview_x = list_x + list_width + 8
    preview_y = panel_y + 5
    preview_width = panel_width - list_width - 18
    preview_height = panel_height - 28
    # Dark recessed area for 3D entity preview
    draw.rectangle([preview_x, preview_y, preview_x + preview_width - 1, preview_y + preview_height - 1],
                   outline=COLORS['border_black'])
    draw.rectangle([preview_x + 1, preview_y + 1, preview_x + preview_width - 2, preview_y + preview_height - 2],
                   fill=COLORS['border_dark'])

    # Button row at bottom
    btn_y = panel_y + panel_height - 19
    btn_height = 14

    # Open Folder button (left)
    draw_button_area(draw, list_x, btn_y, 55, btn_height)

    # Select button (right)
    draw_button_area(draw, preview_x + preview_width - 45, btn_y, 45, btn_height)

    # Fix corners after all content is drawn
    fix_panel_corners(draw, panel_x, panel_y, panel_width, panel_height)

    # Save
    img.save('container/skin_selector.png')
    print("Generated: container/skin_selector.png")
    return img


# =============================================================================
# MAIN
# =============================================================================
if __name__ == '__main__':
    import os

    # Find the output directory relative to this script's location
    # Script is in: /tools/generate_gui_textures.py
    # Output goes to: /src/main/resources/assets/promaton/textures/gui/
    script_dir = os.path.dirname(os.path.abspath(__file__))
    project_root = os.path.dirname(script_dir)  # Go up from /tools to project root
    output_dir = os.path.join(project_root, 'src', 'main', 'resources', 'assets', 'promaton', 'textures', 'gui')

    # Verify output directory exists
    if not os.path.exists(output_dir):
        print(f"Error: Output directory not found: {output_dir}")
        print("Make sure you're running this from the Promaton project.")
        exit(1)

    os.chdir(output_dir)
    print(f"Output directory: {output_dir}")

    # Create subdirectories if needed
    os.makedirs('container', exist_ok=True)
    os.makedirs('sprites', exist_ok=True)

    print("Generating Promaton GUI textures...")
    print("=" * 50)

    # Controller Interface (4 tabs)
    generate_controller_status()
    generate_controller_control()
    generate_controller_logs()
    generate_controller_skin()

    # Program Editor
    generate_program_editor()

    # Automaton GUI (2 tabs)
    generate_automaton_gui_inventory()
    generate_automaton_gui_skin()

    print("=" * 50)
    print("Done! All textures generated.")
