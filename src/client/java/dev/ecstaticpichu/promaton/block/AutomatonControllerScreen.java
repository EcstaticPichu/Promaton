package dev.ecstaticpichu.promaton.block;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AutomatonControllerScreen extends AbstractContainerScreen<AutomatonControllerMenu> {

    private static final Identifier SLOT_PROGRAM_SPRITE =
            Identifier.fromNamespaceAndPath("promaton", "textures/gui/sprites/slot_program.png");
    private static final Identifier SLOT_CASING_SPRITE =
            Identifier.fromNamespaceAndPath("promaton", "textures/gui/sprites/slot_casing.png");

    private static final Identifier[] TAB_TEXTURES = {
            Identifier.fromNamespaceAndPath("promaton", "textures/gui/container/controller_status.png"),
            Identifier.fromNamespaceAndPath("promaton", "textures/gui/container/controller_control.png"),
            Identifier.fromNamespaceAndPath("promaton", "textures/gui/container/controller_logs.png"),
            Identifier.fromNamespaceAndPath("promaton", "textures/gui/container/controller_skin.png"),
    };

    private static final Component[] TAB_TITLES = {
            Component.translatable("gui.promaton.tab.status"),
            Component.translatable("gui.promaton.tab.control"),
            Component.translatable("gui.promaton.tab.logs"),
            Component.translatable("gui.promaton.tab.skin"),
    };

    private static final ItemStack[] TAB_ICONS = {
            new ItemStack(Items.CLOCK),
            new ItemStack(Items.LEAD),
            new ItemStack(Items.PAPER),
            new ItemStack(Items.LEATHER_CHESTPLATE),
    };

    private static final Component[] STATUS_LABELS = {
            Component.translatable("gui.promaton.status.dead"),
            Component.translatable("gui.promaton.status.error"),
            Component.translatable("gui.promaton.status.idle"),
            Component.translatable("gui.promaton.status.sleeping"),
            Component.translatable("gui.promaton.status.working"),
            Component.translatable("gui.promaton.status.companion"),
    };

    // GUI dimensions matching the texture
    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 222;
    private static final int TEXTURE_X = 40;
    private static final int TEXTURE_Y = 28;

    // Tab bar constants
    private static final int TAB_WIDTH = 28;
    private static final int TAB_ACTIVE_HEIGHT = 24;
    private static final int TAB_INACTIVE_HEIGHT = 22;
    private static final int TAB_START_X = 5;

    // Content area (GUI-relative)
    private static final int CONTENT_X = 7;
    private static final int CONTENT_Y = 17;
    private static final int CONTENT_HEIGHT = 86;

    // Control button positions (GUI-relative, for Control tab 2x2 grid)
    private static final int CTRL_BTN_WIDTH = 79;
    private static final int CTRL_BTN_HEIGHT = 20;
    private static final int CTRL_BTN_GAP = 4;
    private static final int CTRL_GRID_X = 7;
    private static final int CTRL_GRID_Y = 38;

    // Control bar button positions (GUI-relative)
    private static final int RUN_BTN_X = 28;
    private static final int RUN_BTN_Y = 108;
    private static final int RUN_BTN_WIDTH = 28;
    private static final int RUN_BTN_HEIGHT = 18;
    private static final int SUMMON_BTN_X = 85;
    private static final int SUMMON_BTN_WIDTH = 42;
    private static final int CLEAR_BTN_X = 135;
    private static final int CLEAR_BTN_WIDTH = 34;

    // Buttons
    private Button runStopButton;
    private Button summonButton;
    private Button returnHomeButton;
    private Button stayButton;
    private Button followMeButton;
    private Button enlistDismissButton;
    private Button clearLogsButton;
    private Button openSkinButton;

    private int lastSyncedTab = -1;

    public AutomatonControllerScreen(AutomatonControllerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
        this.inventoryLabelY = this.imageHeight - 93;
    }

    @Override
    protected void init() {
        super.init();

        // Control bar buttons (visible on all tabs)
        this.runStopButton = addRenderableWidget(Button.builder(
                Component.translatable("gui.promaton.button.run"),
                btn -> clickButton(AutomatonControllerMenu.BUTTON_RUN_STOP)
        ).bounds(this.leftPos + RUN_BTN_X, this.topPos + RUN_BTN_Y, RUN_BTN_WIDTH, RUN_BTN_HEIGHT).build());

        this.summonButton = addRenderableWidget(Button.builder(
                Component.translatable("gui.promaton.button.summon"),
                btn -> clickButton(AutomatonControllerMenu.BUTTON_SUMMON)
        ).bounds(this.leftPos + SUMMON_BTN_X, this.topPos + RUN_BTN_Y, SUMMON_BTN_WIDTH, RUN_BTN_HEIGHT).build());

        this.clearLogsButton = addRenderableWidget(Button.builder(
                Component.translatable("gui.promaton.button.clear_logs"),
                btn -> clickButton(AutomatonControllerMenu.BUTTON_CLEAR_LOGS)
        ).bounds(this.leftPos + CLEAR_BTN_X, this.topPos + RUN_BTN_Y, CLEAR_BTN_WIDTH, RUN_BTN_HEIGHT).build());

        this.openSkinButton = addRenderableWidget(Button.builder(
                Component.translatable("gui.promaton.button.open"),
                btn -> {} // Stub - will open skins folder
        ).bounds(this.leftPos + CLEAR_BTN_X, this.topPos + RUN_BTN_Y, CLEAR_BTN_WIDTH, RUN_BTN_HEIGHT).build());

        // Control tab buttons (2x2 grid)
        this.returnHomeButton = addRenderableWidget(Button.builder(
                Component.translatable("gui.promaton.button.return_home"),
                btn -> clickButton(AutomatonControllerMenu.BUTTON_RETURN_HOME)
        ).bounds(this.leftPos + CTRL_GRID_X, this.topPos + CTRL_GRID_Y, CTRL_BTN_WIDTH, CTRL_BTN_HEIGHT).build());

        this.stayButton = addRenderableWidget(Button.builder(
                Component.translatable("gui.promaton.button.stay"),
                btn -> clickButton(AutomatonControllerMenu.BUTTON_STAY)
        ).bounds(this.leftPos + CTRL_GRID_X + CTRL_BTN_WIDTH + CTRL_BTN_GAP, this.topPos + CTRL_GRID_Y, CTRL_BTN_WIDTH, CTRL_BTN_HEIGHT).build());

        this.followMeButton = addRenderableWidget(Button.builder(
                Component.translatable("gui.promaton.button.follow_me"),
                btn -> clickButton(AutomatonControllerMenu.BUTTON_FOLLOW_ME)
        ).bounds(this.leftPos + CTRL_GRID_X, this.topPos + CTRL_GRID_Y + CTRL_BTN_HEIGHT + CTRL_BTN_GAP, CTRL_BTN_WIDTH, CTRL_BTN_HEIGHT).build());

        this.enlistDismissButton = addRenderableWidget(Button.builder(
                Component.translatable("gui.promaton.button.enlist"),
                btn -> clickButton(AutomatonControllerMenu.BUTTON_ENLIST_DISMISS)
        ).bounds(this.leftPos + CTRL_GRID_X + CTRL_BTN_WIDTH + CTRL_BTN_GAP, this.topPos + CTRL_GRID_Y + CTRL_BTN_HEIGHT + CTRL_BTN_GAP, CTRL_BTN_WIDTH, CTRL_BTN_HEIGHT).build());

        updateButtonVisibility();
    }

    private void clickButton(int buttonId) {
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
        }
    }

    private void clickTab(int tabIndex) {
        clickButton(AutomatonControllerMenu.BUTTON_TAB_STATUS + tabIndex);
    }

    private void updateButtonVisibility() {
        int tab = this.menu.getActiveTab();

        boolean isControlTab = tab == ControllerTab.CONTROL.getIndex();
        boolean isLogsTab = tab == ControllerTab.LOGS.getIndex();
        boolean isSkinTab = tab == ControllerTab.SKIN.getIndex();

        // Control bar buttons: visible on all tabs
        this.runStopButton.visible = true;
        this.summonButton.visible = true;
        this.clearLogsButton.visible = isLogsTab;
        this.openSkinButton.visible = isSkinTab;

        // Control tab buttons: only visible on Control tab
        this.returnHomeButton.visible = isControlTab;
        this.stayButton.visible = isControlTab;
        this.followMeButton.visible = isControlTab;
        this.enlistDismissButton.visible = isControlTab;

        // All command buttons disabled when no automaton (status DEAD)
        boolean hasAutomaton = this.menu.getAutomatonStatus() != AutomatonStatus.DEAD.getIndex();
        this.runStopButton.active = hasAutomaton;
        this.returnHomeButton.active = hasAutomaton;
        this.stayButton.active = hasAutomaton;
        this.followMeButton.active = hasAutomaton;
        this.enlistDismissButton.active = hasAutomaton;
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
        for (int i = 0; i < 4; i++) {
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

        // Draw main GUI background from the appropriate tab texture
        if (activeTab >= 0 && activeTab < TAB_TEXTURES.length) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TAB_TEXTURES[activeTab],
                    this.leftPos, this.topPos,
                    (float) TEXTURE_X, (float) TEXTURE_Y,
                    this.imageWidth, this.imageHeight, 256, 256);
        }

        // Draw empty slot background icons
        if (!this.menu.getSlot(0).hasItem()) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, SLOT_PROGRAM_SPRITE,
                    this.leftPos + 8, this.topPos + 109, 0f, 0f, 16, 16, 16, 16);
        }
        if (!this.menu.getSlot(1).hasItem()) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, SLOT_CASING_SPRITE,
                    this.leftPos + 65, this.topPos + 109, 0f, 0f, 16, 16, 16, 16);
        }

        // Draw tab indicators along the top
        renderTabs(guiGraphics, activeTab);

        // Render tab-specific content
        switch (activeTab) {
            case 0 -> renderStatusTab(guiGraphics);
            case 2 -> renderLogsTab(guiGraphics);
            case 3 -> renderSkinTab(guiGraphics);
        }
    }

    private void renderTabs(GuiGraphics guiGraphics, int activeTab) {
        int panelTop = this.topPos;

        for (int i = 0; i < 4; i++) {
            int tabX = this.leftPos + TAB_START_X + i * (TAB_WIDTH + 2);

            if (i == activeTab) {
                renderActiveTab(guiGraphics, tabX, panelTop);
            } else {
                renderInactiveTab(guiGraphics, tabX, panelTop);
            }

            // Tab icon at a fixed position — same Y regardless of active/inactive
            int inactiveTop = panelTop - TAB_INACTIVE_HEIGHT;
            int iconX = tabX + (TAB_WIDTH - 16) / 2;
            int iconY = inactiveTop + (TAB_INACTIVE_HEIGHT - 16) / 2 + 1;
            guiGraphics.renderItem(TAB_ICONS[i], iconX, iconY);
        }
    }

    /**
     * Draw an active tab matching the panel border style (draw_panel_background):
     * - Rounded top corners (asymmetric: TL 2+1, TR 3+2+1)
     * - 1px black outer, 2px white highlight (top+left), 2px dark shadow (right)
     * - Bottom open: extends into panel's border zone with corner rounding
     *   that curves into the panel's top border
     */
    private void renderActiveTab(GuiGraphics g, int tabX, int panelTop) {
        int tabTop = panelTop - TAB_ACTIVE_HEIGHT;
        int w = TAB_WIDTH;

        int BLACK = 0xFF000000;
        int WHITE = 0xFFFFFFFF;
        int FILL = 0xFFC6C6C6;
        int SHADOW = 0xFF555555;

        // Panel border = 3px (1px black + 2px white). Tab extends through these rows.
        int borderBottom = panelTop + 3;

        // --- Fill (sections for top corner transparency) ---
        g.fill(tabX + 2, tabTop, tabX + w - 3, tabTop + 1, FILL);          // row 0
        g.fill(tabX + 1, tabTop + 1, tabX + w - 2, tabTop + 2, FILL);      // row 1
        g.fill(tabX, tabTop + 2, tabX + w - 1, tabTop + 3, FILL);           // row 2
        g.fill(tabX, tabTop + 3, tabX + w, borderBottom, FILL);              // rows 3+ through panel border

        // --- Black border ---
        g.fill(tabX + 2, tabTop, tabX + w - 3, tabTop + 1, BLACK);          // top edge
        g.fill(tabX + 1, tabTop + 1, tabX + 2, tabTop + 2, BLACK);          // TL corner (1,1)
        g.fill(tabX + w - 3, tabTop + 1, tabX + w - 2, tabTop + 2, BLACK);  // TR corner (w-3,1)
        g.fill(tabX + w - 2, tabTop + 2, tabX + w - 1, tabTop + 3, BLACK);  // TR corner (w-2,2)
        g.fill(tabX, tabTop + 2, tabX + 1, borderBottom, BLACK);             // left edge through panel
        g.fill(tabX + w - 1, tabTop + 3, tabX + w, borderBottom, BLACK);     // right edge through panel

        // --- White highlight (top + left, 2px) ---
        g.fill(tabX + 2, tabTop + 1, tabX + w - 3, tabTop + 2, WHITE);      // top row 1
        g.fill(tabX + 2, tabTop + 2, tabX + w - 3, tabTop + 3, WHITE);      // top row 2
        g.fill(tabX + 1, tabTop + 2, tabX + 2, borderBottom, WHITE);         // left col 1
        g.fill(tabX + 2, tabTop + 3, tabX + 3, borderBottom, WHITE);         // left col 2

        // --- Dark shadow (right, 2px) ---
        g.fill(tabX + w - 3, tabTop + 3, tabX + w - 2, borderBottom, SHADOW);  // col w-3
        g.fill(tabX + w - 2, tabTop + 3, tabX + w - 1, borderBottom, SHADOW);  // col w-2

        // --- Bottom corner rounding (curves into panel's top border) ---
        // Bottom-left: at panelTop+1,+2 the left border curves inward
        g.fill(tabX, panelTop + 1, tabX + 1, panelTop + 3, WHITE);           // col 0 becomes panel highlight
        g.fill(tabX + 1, panelTop + 1, tabX + 2, panelTop + 2, WHITE);       // corner pixel at (1, +1)
        // Bottom-right: at panelTop+1,+2 the right border curves inward
        g.fill(tabX + w - 1, panelTop + 1, tabX + w, panelTop + 3, WHITE);   // col w-1 becomes panel highlight
        g.fill(tabX + w - 2, panelTop + 2, tabX + w - 1, panelTop + 3, WHITE); // (w-2, +2) becomes highlight
    }

    /**
     * Draw an inactive tab with the same panel border style as the active tab:
     * - Same asymmetric top corner rounding (TL 2+1, TR 3+2+1)
     * - 1px black outer, 2px white highlight (top+left), 2px dark shadow (right)
     * - No bottom border (sits flush against panel's top edge)
     * - Darker fill (#8B8B8B)
     */
    private void renderInactiveTab(GuiGraphics g, int tabX, int panelTop) {
        int tabTop = panelTop - TAB_INACTIVE_HEIGHT;
        int w = TAB_WIDTH;

        int BLACK = 0xFF000000;
        int WHITE = 0xFFFFFFFF;
        int FILL = 0xFF8B8B8B;
        int SHADOW = 0xFF555555;

        // --- Fill (top corners only, bottom is flat against panel) ---
        // Top-left: 2+1, Top-right: 3+2+1
        g.fill(tabX + 2, tabTop, tabX + w - 3, tabTop + 1, FILL);          // row 0
        g.fill(tabX + 1, tabTop + 1, tabX + w - 2, tabTop + 2, FILL);      // row 1
        g.fill(tabX, tabTop + 2, tabX + w - 1, tabTop + 3, FILL);           // row 2
        g.fill(tabX, tabTop + 3, tabX + w, panelTop, FILL);                  // rows 3 to panelTop

        // --- Black border ---
        // Top
        g.fill(tabX + 2, tabTop, tabX + w - 3, tabTop + 1, BLACK);
        // Left — extends to panelTop
        g.fill(tabX, tabTop + 2, tabX + 1, panelTop, BLACK);
        // Right — extends to panelTop
        g.fill(tabX + w - 1, tabTop + 3, tabX + w, panelTop, BLACK);
        // TL corner: (1,1)
        g.fill(tabX + 1, tabTop + 1, tabX + 2, tabTop + 2, BLACK);
        // TR corners: (w-3,1), (w-2,2)
        g.fill(tabX + w - 3, tabTop + 1, tabX + w - 2, tabTop + 2, BLACK);
        g.fill(tabX + w - 2, tabTop + 2, tabX + w - 1, tabTop + 3, BLACK);

        // --- White highlight (top + left, 2px) ---
        g.fill(tabX + 2, tabTop + 1, tabX + w - 3, tabTop + 2, WHITE);     // top row 1
        g.fill(tabX + 2, tabTop + 2, tabX + w - 3, tabTop + 3, WHITE);     // top row 2
        g.fill(tabX + 1, tabTop + 2, tabX + 2, panelTop, WHITE);            // left col 1
        g.fill(tabX + 2, tabTop + 3, tabX + 3, panelTop, WHITE);            // left col 2

        // --- Dark shadow (right only, 2px) ---
        g.fill(tabX + w - 3, tabTop + 3, tabX + w - 2, panelTop, SHADOW);  // right col w-3
        g.fill(tabX + w - 2, tabTop + 3, tabX + w - 1, panelTop, SHADOW);  // right col w-2
    }

    private void renderStatusTab(GuiGraphics guiGraphics) {
        int x = this.leftPos + CONTENT_X + 4;
        int y = this.topPos + CONTENT_Y + 4;

        int status = this.menu.getAutomatonStatus();
        if (status == AutomatonStatus.DEAD.getIndex()) {
            Component noAutomaton = Component.translatable("gui.promaton.no_automaton");
            int textWidth = this.font.width(noAutomaton);
            guiGraphics.drawString(this.font, noAutomaton,
                    this.leftPos + (this.imageWidth - textWidth) / 2,
                    this.topPos + CONTENT_Y + CONTENT_HEIGHT / 2 - 4,
                    0xFFA0A0A0, false);
        } else {
            if (status >= 0 && status < STATUS_LABELS.length) {
                guiGraphics.drawString(this.font, Component.literal("Status: ").append(STATUS_LABELS[status]),
                        x, y, 0xFF404040, false);
            }
        }
    }

    private void renderLogsTab(GuiGraphics guiGraphics) {
        int x = this.leftPos + CONTENT_X + 2;
        int y = this.topPos + CONTENT_Y + 2;

        Component noLogs = Component.literal("No logs");
        guiGraphics.drawString(this.font, noLogs, x, y, 0xFFA0A0A0, false);
    }

    private void renderSkinTab(GuiGraphics guiGraphics) {
        int x = this.leftPos + CONTENT_X + 4;
        int y = this.topPos + CONTENT_Y + CONTENT_HEIGHT / 2 - 4;
        guiGraphics.drawString(this.font, Component.literal("Skin selection coming soon..."),
                x, y, 0xFFA0A0A0, false);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Dynamic title: "Automaton Controller - <Tab Name>"
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

        // Handle tab clicks
        int panelTop = this.topPos;
        for (int i = 0; i < 4; i++) {
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
        // Keys 1-4 for tab switching (49-52 are keycodes for 1-4)
        if (keyCode >= 49 && keyCode <= 52) {
            int tab = keyCode - 49;
            if (tab != this.menu.getActiveTab()) {
                clickTab(tab);
                return true;
            }
        }
        return super.keyPressed(event);
    }
}
