package dev.ecstaticpichu.promaton.block;

import dev.ecstaticpichu.promaton.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AutomatonControllerMenu extends AbstractContainerMenu {

    private final Container container;
    private final ContainerData containerData;
    private final BlockPos blockPos;

    // Button IDs
    public static final int BUTTON_RUN_STOP = 0;
    public static final int BUTTON_SUMMON = 1;
    public static final int BUTTON_RETURN_HOME = 2;
    public static final int BUTTON_STAY = 3;
    public static final int BUTTON_FOLLOW_ME = 4;
    public static final int BUTTON_ENLIST_DISMISS = 5;
    public static final int BUTTON_CLEAR_LOGS = 6;
    public static final int BUTTON_TAB_STATUS = 7;
    public static final int BUTTON_TAB_CONTROL = 8;
    public static final int BUTTON_TAB_LOGS = 9;
    public static final int BUTTON_TAB_SKIN = 10;

    // ContainerData indices
    public static final int DATA_ACTIVE_TAB = 0;
    public static final int DATA_AUTOMATON_STATUS = 1;

    // Client-side constructor (from network)
    public AutomatonControllerMenu(int syncId, Inventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, new SimpleContainer(AutomatonControllerBlockEntity.INVENTORY_SIZE),
                new SimpleContainerData(2), pos);
    }

    // Server-side constructor
    public AutomatonControllerMenu(int syncId, Inventory playerInventory,
                                   AutomatonControllerBlockEntity blockEntity, ContainerData containerData) {
        this(syncId, playerInventory, blockEntity, containerData, blockEntity.getBlockPos());
    }

    // Common constructor
    private AutomatonControllerMenu(int syncId, Inventory playerInventory,
                                    Container container, ContainerData containerData, BlockPos pos) {
        super(ModMenuTypes.AUTOMATON_CONTROLLER, syncId);
        this.container = container;
        this.containerData = containerData;
        this.blockPos = pos;

        checkContainerSize(container, AutomatonControllerBlockEntity.INVENTORY_SIZE);
        checkContainerDataCount(containerData, 2);

        // Program slot (slot 0) - in the control bar area
        this.addSlot(new ProgramSlot(container, AutomatonControllerBlockEntity.SLOT_PROGRAM, 8, 109));

        // Casing slot (slot 1) - in the control bar area
        this.addSlot(new CasingSlot(container, AutomatonControllerBlockEntity.SLOT_CASING, 65, 109));

        // Player inventory (3 rows of 9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
            }
        }

        // Player hotbar (1 row of 9)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 198));
        }

        this.addDataSlots(containerData);
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        switch (buttonId) {
            case BUTTON_RUN_STOP:
                // Stub - will toggle automaton run state
                return true;
            case BUTTON_SUMMON:
                // Stub - will summon automaton
                return true;
            case BUTTON_RETURN_HOME:
                // Stub - will command automaton to return home
                return true;
            case BUTTON_STAY:
                // Stub - will command automaton to stay
                return true;
            case BUTTON_FOLLOW_ME:
                // Stub - will command automaton to follow player
                return true;
            case BUTTON_ENLIST_DISMISS:
                // Stub - will enlist/dismiss automaton as companion
                return true;
            case BUTTON_CLEAR_LOGS:
                if (this.container instanceof AutomatonControllerBlockEntity blockEntity) {
                    blockEntity.clearLogs();
                }
                return true;
            case BUTTON_TAB_STATUS:
            case BUTTON_TAB_CONTROL:
            case BUTTON_TAB_LOGS:
            case BUTTON_TAB_SKIN:
                this.containerData.set(DATA_ACTIVE_TAB, buttonId - BUTTON_TAB_STATUS);
                return true;
            default:
                return false;
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();

            // Moving from container slots (0-1) to player inventory (2-37)
            if (slotIndex < AutomatonControllerBlockEntity.INVENTORY_SIZE) {
                if (!this.moveItemStackTo(slotStack, AutomatonControllerBlockEntity.INVENTORY_SIZE,
                        AutomatonControllerBlockEntity.INVENTORY_SIZE + 36, true)) {
                    return ItemStack.EMPTY;
                }
            }
            // Moving from player inventory to container
            else {
                if (slotStack.is(ModItems.PROGRAM)) {
                    if (!this.moveItemStackTo(slotStack, AutomatonControllerBlockEntity.SLOT_PROGRAM,
                            AutomatonControllerBlockEntity.SLOT_PROGRAM + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotStack.is(ModItems.AUTOMATON_CASING)) {
                    if (!this.moveItemStackTo(slotStack, AutomatonControllerBlockEntity.SLOT_CASING,
                            AutomatonControllerBlockEntity.SLOT_CASING + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }

        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    public int getActiveTab() {
        return this.containerData.get(DATA_ACTIVE_TAB);
    }

    public int getAutomatonStatus() {
        return this.containerData.get(DATA_AUTOMATON_STATUS);
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    // --- Custom Slot Classes ---

    private static class ProgramSlot extends Slot {
        public ProgramSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.is(ModItems.PROGRAM);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }

    private static class CasingSlot extends Slot {
        public CasingSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.is(ModItems.AUTOMATON_CASING);
        }

        @Override
        public int getMaxStackSize() {
            return 16;
        }
    }
}
