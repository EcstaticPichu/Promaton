package dev.ecstaticpichu.promaton.entity;

import dev.ecstaticpichu.promaton.block.ModMenuTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import org.jetbrains.annotations.Nullable;

public class AutomatonMenu extends AbstractContainerMenu {

    private final Container automatonInventory;
    private final ContainerData containerData;
    @Nullable
    private final LivingEntity automaton;
    private final int entityId;

    // Slot index ranges
    public static final int EQUIP_SLOT_START = 0;   // 6 equipment slots (H, C, L, B, Off, Main)
    public static final int EQUIP_SLOT_COUNT = 6;
    public static final int AUTO_INV_START = 6;      // 27 automaton inventory slots
    public static final int AUTO_INV_COUNT = 27;
    public static final int PLAYER_INV_START = 33;   // 27 player inventory slots
    public static final int PLAYER_HOTBAR_START = 60; // 9 hotbar slots
    public static final int TOTAL_SLOTS = 69;

    // Equipment slot order: H, C, L, B, Off, Main
    private static final EquipmentSlot[] EQUIP_ORDER = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET,
            EquipmentSlot.OFFHAND, EquipmentSlot.MAINHAND
    };

    // Button IDs
    public static final int BUTTON_ENLIST_DISMISS = 0;
    public static final int BUTTON_TAB_INVENTORY = 1;
    public static final int BUTTON_TAB_SKIN = 2;

    // ContainerData indices
    public static final int DATA_ACTIVE_TAB = 0;
    public static final int DATA_AUTOMATON_STATUS = 1;

    // GUI slot positions (relative to GUI top-left, +1 inside slot border)
    private static final int EQUIP_X = 8;
    private static final int EQUIP_Y = 60;
    private static final int AUTO_INV_X = 8;
    private static final int AUTO_INV_Y = 82;
    private static final int PLAYER_INV_X = 8;
    private static final int PLAYER_INV_Y = 150;
    private static final int HOTBAR_Y = 208;

    // Client constructor (from network)
    public AutomatonMenu(int syncId, Inventory playerInventory, int entityId) {
        this(syncId, playerInventory, new SimpleContainer(AutomatonEntity.INVENTORY_SIZE),
                new SimpleContainerData(2), null, entityId);
    }

    // Server constructor
    public AutomatonMenu(int syncId, Inventory playerInventory, AutomatonEntity automaton) {
        this(syncId, playerInventory, automaton, automaton.getContainerData(), automaton, automaton.getId());
    }

    private AutomatonMenu(int syncId, Inventory playerInventory,
                           Container automatonInventory, ContainerData containerData,
                           @Nullable LivingEntity automaton, int entityId) {
        super(ModMenuTypes.AUTOMATON, syncId);
        this.automatonInventory = automatonInventory;
        this.containerData = containerData;
        this.automaton = automaton;
        this.entityId = entityId;

        checkContainerSize(automatonInventory, AutomatonEntity.INVENTORY_SIZE);
        checkContainerDataCount(containerData, 2);

        // Equipment slots (6): H, C, L, B, Off, Main
        for (int i = 0; i < EQUIP_SLOT_COUNT; i++) {
            this.addSlot(new EquipmentEntitySlot(automaton, EQUIP_ORDER[i],
                    EQUIP_X + i * 18, EQUIP_Y, this));
        }

        // Automaton inventory (27 slots: 3 rows of 9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new TabAwareSlot(this, automatonInventory, col + row * 9,
                        AUTO_INV_X + col * 18, AUTO_INV_Y + row * 18));
            }
        }

        // Player inventory (3 rows of 9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9,
                        PLAYER_INV_X + col * 18, PLAYER_INV_Y + row * 18));
            }
        }

        // Player hotbar (1 row of 9)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col,
                    PLAYER_INV_X + col * 18, HOTBAR_Y));
        }

        this.addDataSlots(containerData);
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        switch (buttonId) {
            case BUTTON_ENLIST_DISMISS:
                // Placeholder — companion enlist/dismiss logic added later
                return true;
            case BUTTON_TAB_INVENTORY:
            case BUTTON_TAB_SKIN:
                this.containerData.set(DATA_ACTIVE_TAB, buttonId - BUTTON_TAB_INVENTORY);
                return true;
            default:
                return false;
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (!slot.hasItem()) {
            return result;
        }

        ItemStack slotStack = slot.getItem();
        result = slotStack.copy();

        // From equipment slots (0-5) → player inventory
        if (slotIndex < EQUIP_SLOT_START + EQUIP_SLOT_COUNT) {
            if (!this.moveItemStackTo(slotStack, PLAYER_INV_START, TOTAL_SLOTS, true)) {
                return ItemStack.EMPTY;
            }
        }
        // From automaton inventory (6-32) → player inventory
        else if (slotIndex < AUTO_INV_START + AUTO_INV_COUNT) {
            if (!this.moveItemStackTo(slotStack, PLAYER_INV_START, TOTAL_SLOTS, true)) {
                return ItemStack.EMPTY;
            }
        }
        // From player inventory/hotbar → try equipment, then automaton inventory
        else {
            // Try equipment slots first
            if (!tryMoveToEquipment(slotStack)) {
                // Then try automaton inventory
                if (!this.moveItemStackTo(slotStack, AUTO_INV_START, AUTO_INV_START + AUTO_INV_COUNT, false)) {
                    // Move between player inv and hotbar
                    if (slotIndex < PLAYER_HOTBAR_START) {
                        if (!this.moveItemStackTo(slotStack, PLAYER_HOTBAR_START, TOTAL_SLOTS, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else {
                        if (!this.moveItemStackTo(slotStack, PLAYER_INV_START, PLAYER_HOTBAR_START, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
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
        return result;
    }

    private boolean tryMoveToEquipment(ItemStack stack) {
        // Armor and shields: match via Equippable component
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable != null) {
            for (int i = 0; i < EQUIP_SLOT_COUNT; i++) {
                if (EQUIP_ORDER[i] == equippable.slot()) {
                    int targetSlot = EQUIP_SLOT_START + i;
                    if (this.slots.get(targetSlot).getItem().isEmpty()) {
                        return this.moveItemStackTo(stack, targetSlot, targetSlot + 1, false);
                    }
                    break;
                }
            }
            return false;
        }

        // Weapons and tools: shift-click into main hand
        if (stack.has(DataComponents.TOOL) || stack.has(DataComponents.WEAPON)
                || stack.has(DataComponents.PIERCING_WEAPON) || stack.has(DataComponents.KINETIC_WEAPON)) {
            int mainHandSlot = EQUIP_SLOT_START + 5; // MAINHAND is index 5 in EQUIP_ORDER
            if (this.slots.get(mainHandSlot).getItem().isEmpty()) {
                return this.moveItemStackTo(stack, mainHandSlot, mainHandSlot + 1, false);
            }
        }

        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.automatonInventory.stillValid(player);
    }

    public int getActiveTab() {
        return this.containerData.get(DATA_ACTIVE_TAB);
    }

    public int getAutomatonStatus() {
        return this.containerData.get(DATA_AUTOMATON_STATUS);
    }

    public int getEntityId() {
        return this.entityId;
    }

    // --- Custom Slot for Entity Equipment ---

    private static class EquipmentEntitySlot extends Slot {
        private static final Container EMPTY_CONTAINER = new SimpleContainer(0);
        private final EquipmentSlot equipmentSlot;
        @Nullable
        private final LivingEntity entity;
        @Nullable
        private final AutomatonMenu menu;
        // Client-side fallback storage (entity is null on client)
        private ItemStack clientStack = ItemStack.EMPTY;

        public EquipmentEntitySlot(@Nullable LivingEntity entity, EquipmentSlot equipmentSlot, int x, int y,
                                    @Nullable AutomatonMenu menu) {
            super(EMPTY_CONTAINER, 0, x, y);
            this.entity = entity;
            this.equipmentSlot = equipmentSlot;
            this.menu = menu;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            if (equipmentSlot.getType() == EquipmentSlot.Type.HAND) {
                return true;
            }
            Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
            return equippable != null && equippable.slot() == this.equipmentSlot;
        }

        @Override
        public ItemStack getItem() {
            if (entity != null) return entity.getItemBySlot(equipmentSlot);
            return clientStack;
        }

        @Override
        public void setByPlayer(ItemStack stack, ItemStack previousStack) {
            if (entity != null) {
                entity.setItemSlot(equipmentSlot, stack);
            } else {
                clientStack = stack;
            }
        }

        @Override
        public void set(ItemStack stack) {
            if (entity != null) {
                entity.setItemSlot(equipmentSlot, stack);
            } else {
                clientStack = stack;
            }
        }

        @Override
        public ItemStack remove(int amount) {
            ItemStack current = getItem();
            if (current.isEmpty()) return ItemStack.EMPTY;
            ItemStack removed = current.split(amount);
            if (current.isEmpty()) {
                if (entity != null) {
                    entity.setItemSlot(equipmentSlot, ItemStack.EMPTY);
                } else {
                    clientStack = ItemStack.EMPTY;
                }
            }
            return removed;
        }

        @Override
        public boolean hasItem() {
            return !getItem().isEmpty();
        }

        @Override
        public int getMaxStackSize() {
            return equipmentSlot.getType() == EquipmentSlot.Type.HAND ? 64 : 1;
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return equipmentSlot.getType() == EquipmentSlot.Type.HAND ? stack.getMaxStackSize() : 1;
        }

        @Override
        public boolean mayPickup(Player player) {
            return true;
        }

        @Override
        public boolean isActive() {
            return menu != null ? menu.isInventoryTabActive() : true;
        }
    }

    // Slot that is only active on the Inventory tab
    private static class TabAwareSlot extends Slot {
        private final AutomatonMenu menu;

        public TabAwareSlot(AutomatonMenu menu, Container container, int slot, int x, int y) {
            super(container, slot, x, y);
            this.menu = menu;
        }

        @Override
        public boolean isActive() {
            return menu.isInventoryTabActive();
        }
    }

    public boolean isInventoryTabActive() {
        return this.containerData.get(DATA_ACTIVE_TAB) == AutomatonTab.INVENTORY.getIndex();
    }
}
