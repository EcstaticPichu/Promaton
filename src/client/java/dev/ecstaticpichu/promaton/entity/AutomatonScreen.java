package dev.ecstaticpichu.promaton.entity;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AutomatonScreen extends AbstractContainerScreen<AutomatonMenu> {

    private static final Identifier[] TAB_TEXTURES = {
            Identifier.fromNamespaceAndPath("promaton", "textures/gui/container/automaton_gui_inventory.png"),
            Identifier.fromNamespaceAndPath("promaton", "textures/gui/container/automaton_gui_skin.png"),
    };

    private static final Component[] TAB_TITLES = {
            Component.translatable("gui.promaton.automaton.tab.inventory"),
            Component.translatable("gui.promaton.automaton.tab.skin"),
    };

    private static final ItemStack[] TAB_ICONS = {
            new ItemStack(Items.CHEST),
            new ItemStack(Items.LEATHER_CHESTPLATE),
    };

    // Vanilla empty equipment slot sprites: H, C, L, B, Off, Main
    private static final Identifier[] EQUIP_SLOT_SPRITES = {
            Identifier.withDefaultNamespace("textures/gui/sprites/container/slot/helmet.png"),
            Identifier.withDefaultNamespace("textures/gui/sprites/container/slot/chestplate.png"),
            Identifier.withDefaultNamespace("textures/gui/sprites/container/slot/leggings.png"),
            Identifier.withDefaultNamespace("textures/gui/sprites/container/slot/boots.png"),
            Identifier.withDefaultNamespace("textures/gui/sprites/container/slot/shield.png"),
            Identifier.withDefaultNamespace("textures/gui/sprites/container/slot/sword.png"),
    };

    // Equipment slot positions (GUI-relative, matching AutomatonMenu)
    private static final int EQUIP_X = 8;
    private static final int EQUIP_Y = 60;

    // GUI dimensions matching the texture
    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 232;
    private static final int TEXTURE_X = 40;
    private static final int TEXTURE_Y = 24;

    // Tab bar constants (same as controller screen)
    private static final int TAB_WIDTH = 28;
    private static final int TAB_ACTIVE_HEIGHT = 24;
    private static final int TAB_INACTIVE_HEIGHT = 22;
    private static final int TAB_START_X = 5;
    private static final int TAB_COUNT = 2;

    // Content area (GUI-relative)
    private static final int CONTENT_X = 6;
    private static final int CONTENT_Y = 17;
    private static final int INFO_HEIGHT = 38;

    // HUD-style heart sprites (9x9 each)
    private static final Identifier HEART_CONTAINER = Identifier.withDefaultNamespace("textures/gui/sprites/hud/heart/container.png");
    private static final Identifier HEART_FULL = Identifier.withDefaultNamespace("textures/gui/sprites/hud/heart/full.png");
    private static final Identifier HEART_HALF = Identifier.withDefaultNamespace("textures/gui/sprites/hud/heart/half.png");

    // HUD-style food sprites (9x9 each)
    private static final Identifier FOOD_EMPTY = Identifier.withDefaultNamespace("textures/gui/sprites/hud/food_empty.png");
    private static final Identifier FOOD_FULL = Identifier.withDefaultNamespace("textures/gui/sprites/hud/food_full.png");
    private static final Identifier FOOD_HALF = Identifier.withDefaultNamespace("textures/gui/sprites/hud/food_half.png");

    // Enlist button position (GUI-relative)
    private static final int ENLIST_BTN_X = 119;
    private static final int ENLIST_BTN_Y = 59;
    private static final int ENLIST_BTN_WIDTH = 50;
    private static final int ENLIST_BTN_HEIGHT = 18;

    private Button enlistButton;
    private int lastSyncedTab = -1;

    public AutomatonScreen(AutomatonMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
        this.inventoryLabelY = this.imageHeight - 93;
    }

    @Override
    protected void init() {
        super.init();

        this.enlistButton = addRenderableWidget(Button.builder(
                Component.translatable("gui.promaton.automaton.button.enlist"),
                btn -> clickButton(AutomatonMenu.BUTTON_ENLIST_DISMISS)
        ).bounds(this.leftPos + ENLIST_BTN_X, this.topPos + ENLIST_BTN_Y,
                ENLIST_BTN_WIDTH, ENLIST_BTN_HEIGHT).build());

        updateButtonVisibility();
    }

    private void clickButton(int buttonId) {
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
        }
    }

    private void clickTab(int tabIndex) {
        clickButton(AutomatonMenu.BUTTON_TAB_INVENTORY + tabIndex);
    }

    private void updateButtonVisibility() {
        int tab = this.menu.getActiveTab();
        boolean isInventoryTab = tab == AutomatonTab.INVENTORY.getIndex();
        this.enlistButton.visible = isInventoryTab;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        int currentTab = this.menu.getActiveTab();
        if (currentTab != lastSyncedTab) {
            lastSyncedTab = currentTab;
            updateButtonVisibility();
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        renderTabTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderTabTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int panelTop = this.topPos;
        for (int i = 0; i < TAB_COUNT; i++) {
            int tabX = this.leftPos + TAB_START_X + i * (TAB_WIDTH + 2);
            int tabTop = panelTop - (i == this.menu.getActiveTab() ? TAB_ACTIVE_HEIGHT : TAB_INACTIVE_HEIGHT);
            if (mouseX >= tabX && mouseX < tabX + TAB_WIDTH && mouseY >= tabTop && mouseY < panelTop) {
                guiGraphics.setTooltipForNextFrame(TAB_TITLES[i], mouseX, mouseY);
                return;
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int activeTab = this.menu.getActiveTab();

        if (activeTab >= 0 && activeTab < TAB_TEXTURES.length) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TAB_TEXTURES[activeTab],
                    this.leftPos, this.topPos,
                    (float) TEXTURE_X, (float) TEXTURE_Y,
                    this.imageWidth, this.imageHeight, 256, 256);
        }

        // Draw empty equipment slot sprites on Inventory tab
        if (activeTab == AutomatonTab.INVENTORY.getIndex()) {
            for (int i = 0; i < AutomatonMenu.EQUIP_SLOT_COUNT; i++) {
                if (!this.menu.getSlot(AutomatonMenu.EQUIP_SLOT_START + i).hasItem()) {
                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED, EQUIP_SLOT_SPRITES[i],
                            this.leftPos + EQUIP_X + i * 18, this.topPos + EQUIP_Y,
                            0f, 0f, 16, 16, 16, 16);
                }
            }
        }

        renderTabs(guiGraphics, activeTab);

        // Tab-specific content
        switch (activeTab) {
            case 0 -> renderInventoryTab(guiGraphics);
            case 1 -> renderSkinTab(guiGraphics);
        }
    }

    private void renderInventoryTab(GuiGraphics guiGraphics) {
        // Look up entity on client for real data
        float health = 20f;
        float maxHealth = 20f;
        float hunger = 20f;
        String restStatus = "tired";
        int xpBuffer = 0;

        if (this.minecraft != null && this.minecraft.level != null) {
            Entity entity = this.minecraft.level.getEntity(this.menu.getEntityId());
            if (entity instanceof AutomatonEntity automaton) {
                health = automaton.getHealth();
                maxHealth = automaton.getMaxHealth();
                hunger = automaton.getHunger();
                restStatus = automaton.getRestStatus();
                xpBuffer = automaton.getXPBuffer();
            }
        }

        int x = this.leftPos + CONTENT_X + 4;
        int y = this.topPos + CONTENT_Y + 4;
        int rx = this.leftPos + CONTENT_X + 86;
        int rightEdge = this.leftPos + CONTENT_X + 161;
        int textColor = 0xFF404040;

        // Row 1: Automaton name (left) / XP (right)
        guiGraphics.drawString(this.font, Component.literal("Automaton"), x, y, textColor, false);
        Component xpText = Component.literal("XP: " + xpBuffer);
        guiGraphics.drawString(this.font, xpText, rightEdge - this.font.width(xpText), y, textColor, false);

        // Row 2: Status (left-aligned) / Rest (right-aligned via translation key)
        guiGraphics.drawString(this.font, Component.literal("Idle"), x, y + 10, textColor, false);
        Component restComponent = Component.translatable("gui.promaton.automaton.rest." + restStatus);
        guiGraphics.drawString(this.font, restComponent, rightEdge - this.font.width(restComponent), y + 10, textColor, false);

        // Row 3: Heart icons (left) and hunger icons (right)
        int iconY = y + 21;
        renderHeartBar(guiGraphics, x - 4, iconY, health, maxHealth);
        renderHungerBar(guiGraphics, rx - 3, iconY, hunger);
    }

    private void renderHeartBar(GuiGraphics g, int x, int y, float health, float maxHealth) {
        int hearts = (int) Math.ceil(maxHealth / 2.0f);
        for (int i = 0; i < hearts; i++) {
            int hx = x + i * 8;
            // Draw container (background)
            g.blit(RenderPipelines.GUI_TEXTURED, HEART_CONTAINER, hx, y, 0f, 0f, 9, 9, 9, 9);
            // Draw full or half heart
            float threshold = i * 2;
            if (health > threshold + 1) {
                g.blit(RenderPipelines.GUI_TEXTURED, HEART_FULL, hx, y, 0f, 0f, 9, 9, 9, 9);
            } else if (health > threshold) {
                g.blit(RenderPipelines.GUI_TEXTURED, HEART_HALF, hx, y, 0f, 0f, 9, 9, 9, 9);
            }
        }
    }

    private void renderHungerBar(GuiGraphics g, int x, int y, float hunger) {
        for (int i = 0; i < 10; i++) {
            int hx = x + i * 8;
            // Draw empty background
            g.blit(RenderPipelines.GUI_TEXTURED, FOOD_EMPTY, hx, y, 0f, 0f, 9, 9, 9, 9);
            // Draw full or half shank
            float threshold = i * 2;
            if (hunger > threshold + 1) {
                g.blit(RenderPipelines.GUI_TEXTURED, FOOD_FULL, hx, y, 0f, 0f, 9, 9, 9, 9);
            } else if (hunger > threshold) {
                g.blit(RenderPipelines.GUI_TEXTURED, FOOD_HALF, hx, y, 0f, 0f, 9, 9, 9, 9);
            }
        }
    }

    private void renderSkinTab(GuiGraphics guiGraphics) {
        int x = this.leftPos + CONTENT_X + 4;
        int y = this.topPos + CONTENT_Y + 40;
        guiGraphics.drawString(this.font, Component.literal("Skin selection coming soon..."),
                x, y, 0xFFA0A0A0, false);
    }

    private void renderTabs(GuiGraphics guiGraphics, int activeTab) {
        int panelTop = this.topPos;

        for (int i = 0; i < TAB_COUNT; i++) {
            int tabX = this.leftPos + TAB_START_X + i * (TAB_WIDTH + 2);

            if (i == activeTab) {
                renderActiveTab(guiGraphics, tabX, panelTop);
            } else {
                renderInactiveTab(guiGraphics, tabX, panelTop);
            }

            int inactiveTop = panelTop - TAB_INACTIVE_HEIGHT;
            int iconX = tabX + (TAB_WIDTH - 16) / 2;
            int iconY = inactiveTop + (TAB_INACTIVE_HEIGHT - 16) / 2 + 1;
            guiGraphics.renderItem(TAB_ICONS[i], iconX, iconY);
        }
    }

    // Active tab rendering — same pixel-perfect pattern as AutomatonControllerScreen
    private void renderActiveTab(GuiGraphics g, int tabX, int panelTop) {
        int tabTop = panelTop - TAB_ACTIVE_HEIGHT;
        int w = TAB_WIDTH;

        int BLACK = 0xFF000000;
        int WHITE = 0xFFFFFFFF;
        int FILL = 0xFFC6C6C6;
        int SHADOW = 0xFF555555;

        int borderBottom = panelTop + 3;

        g.fill(tabX + 2, tabTop, tabX + w - 3, tabTop + 1, FILL);
        g.fill(tabX + 1, tabTop + 1, tabX + w - 2, tabTop + 2, FILL);
        g.fill(tabX, tabTop + 2, tabX + w - 1, tabTop + 3, FILL);
        g.fill(tabX, tabTop + 3, tabX + w, borderBottom, FILL);

        g.fill(tabX + 2, tabTop, tabX + w - 3, tabTop + 1, BLACK);
        g.fill(tabX + 1, tabTop + 1, tabX + 2, tabTop + 2, BLACK);
        g.fill(tabX + w - 3, tabTop + 1, tabX + w - 2, tabTop + 2, BLACK);
        g.fill(tabX + w - 2, tabTop + 2, tabX + w - 1, tabTop + 3, BLACK);
        g.fill(tabX, tabTop + 2, tabX + 1, borderBottom, BLACK);
        g.fill(tabX + w - 1, tabTop + 3, tabX + w, borderBottom, BLACK);

        g.fill(tabX + 2, tabTop + 1, tabX + w - 3, tabTop + 2, WHITE);
        g.fill(tabX + 2, tabTop + 2, tabX + w - 3, tabTop + 3, WHITE);
        g.fill(tabX + 1, tabTop + 2, tabX + 2, borderBottom, WHITE);
        g.fill(tabX + 2, tabTop + 3, tabX + 3, borderBottom, WHITE);

        g.fill(tabX + w - 3, tabTop + 3, tabX + w - 2, borderBottom, SHADOW);
        g.fill(tabX + w - 2, tabTop + 3, tabX + w - 1, borderBottom, SHADOW);

        g.fill(tabX, panelTop + 1, tabX + 1, panelTop + 3, WHITE);
        g.fill(tabX + 1, panelTop + 1, tabX + 2, panelTop + 2, WHITE);
        g.fill(tabX + w - 1, panelTop + 1, tabX + w, panelTop + 3, WHITE);
        g.fill(tabX + w - 2, panelTop + 2, tabX + w - 1, panelTop + 3, WHITE);
    }

    private void renderInactiveTab(GuiGraphics g, int tabX, int panelTop) {
        int tabTop = panelTop - TAB_INACTIVE_HEIGHT;
        int w = TAB_WIDTH;

        int BLACK = 0xFF000000;
        int WHITE = 0xFFFFFFFF;
        int FILL = 0xFF8B8B8B;
        int SHADOW = 0xFF555555;

        g.fill(tabX + 2, tabTop, tabX + w - 3, tabTop + 1, FILL);
        g.fill(tabX + 1, tabTop + 1, tabX + w - 2, tabTop + 2, FILL);
        g.fill(tabX, tabTop + 2, tabX + w - 1, tabTop + 3, FILL);
        g.fill(tabX, tabTop + 3, tabX + w, panelTop, FILL);

        g.fill(tabX + 2, tabTop, tabX + w - 3, tabTop + 1, BLACK);
        g.fill(tabX, tabTop + 2, tabX + 1, panelTop, BLACK);
        g.fill(tabX + w - 1, tabTop + 3, tabX + w, panelTop, BLACK);
        g.fill(tabX + 1, tabTop + 1, tabX + 2, tabTop + 2, BLACK);
        g.fill(tabX + w - 3, tabTop + 1, tabX + w - 2, tabTop + 2, BLACK);
        g.fill(tabX + w - 2, tabTop + 2, tabX + w - 1, tabTop + 3, BLACK);

        g.fill(tabX + 2, tabTop + 1, tabX + w - 3, tabTop + 2, WHITE);
        g.fill(tabX + 2, tabTop + 2, tabX + w - 3, tabTop + 3, WHITE);
        g.fill(tabX + 1, tabTop + 2, tabX + 2, panelTop, WHITE);
        g.fill(tabX + 2, tabTop + 3, tabX + 3, panelTop, WHITE);

        g.fill(tabX + w - 3, tabTop + 3, tabX + w - 2, panelTop, SHADOW);
        g.fill(tabX + w - 2, tabTop + 3, tabX + w - 1, panelTop, SHADOW);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int activeTab = this.menu.getActiveTab();
        Component fullTitle;
        if (activeTab >= 0 && activeTab < TAB_TITLES.length) {
            fullTitle = this.title.copy().append(" - ").append(TAB_TITLES[activeTab]);
        } else {
            fullTitle = this.title;
        }
        guiGraphics.drawString(this.font, fullTitle, this.titleLabelX, this.titleLabelY, 0xFF404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFF404040, false);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean forwarded) {
        double mouseX = event.x();
        double mouseY = event.y();

        int panelTop = this.topPos;
        for (int i = 0; i < TAB_COUNT; i++) {
            int tabX = this.leftPos + TAB_START_X + i * (TAB_WIDTH + 2);
            int tabTop = (i == this.menu.getActiveTab())
                    ? panelTop - TAB_ACTIVE_HEIGHT
                    : panelTop - TAB_INACTIVE_HEIGHT;

            if (mouseX >= tabX && mouseX < tabX + TAB_WIDTH && mouseY >= tabTop && mouseY < panelTop) {
                if (i != this.menu.getActiveTab()) {
                    clickTab(i);
                    return true;
                }
            }
        }
        return super.mouseClicked(event, forwarded);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();
        // Keys 1-2 for tab switching (49-50 are keycodes for 1-2)
        if (keyCode >= 49 && keyCode <= 50) {
            int tab = keyCode - 49;
            if (tab != this.menu.getActiveTab()) {
                clickTab(tab);
                return true;
            }
        }
        return super.keyPressed(event);
    }
}
