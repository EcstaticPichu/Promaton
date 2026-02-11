package dev.ecstaticpichu.promaton.block;

import dev.ecstaticpichu.promaton.item.ModItems;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AutomatonControllerBlockEntity extends BlockEntity
        implements WorldlyContainer, ExtendedScreenHandlerFactory<BlockPos> {

    public static final int SLOT_PROGRAM = 0;
    public static final int SLOT_CASING = 1;
    public static final int INVENTORY_SIZE = 2;
    public static final int MAX_LOG_ENTRIES = 100;

    private static final int[] SIDE_SLOTS = new int[]{SLOT_CASING};
    private static final int[] BOTTOM_SLOTS = new int[]{};

    private final NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
    private AutomatonStatus automatonStatus = AutomatonStatus.DEAD;
    @Nullable
    private UUID automatonUuid = null;
    private String automatonName = "";
    private final List<String> logs = new ArrayList<>();
    private int activeTab = 0;

    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> activeTab;
                case 1 -> automatonStatus.getIndex();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> activeTab = value;
                case 1 -> automatonStatus = AutomatonStatus.fromIndex(value);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public AutomatonControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AUTOMATON_CONTROLLER, pos, state);
    }

    // --- ExtendedScreenHandlerFactory ---

    @Override
    public BlockPos getScreenOpeningData(ServerPlayer player) {
        return this.worldPosition;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.promaton.automaton_controller");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new AutomatonControllerMenu(syncId, playerInventory, this, this.containerData);
    }

    // --- Container ---

    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(this.items, slot, amount);
        if (!result.isEmpty()) {
            this.setChanged();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        stack.limitSize(this.getMaxStackSize(stack));
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return this.level != null
                && this.level.getBlockEntity(this.worldPosition) == this
                && player.distanceToSqr(this.worldPosition.getX() + 0.5,
                        this.worldPosition.getY() + 0.5,
                        this.worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == SLOT_PROGRAM) {
            return stack.is(ModItems.PROGRAM);
        } else if (slot == SLOT_CASING) {
            return stack.is(ModItems.AUTOMATON_CASING);
        }
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    // --- WorldlyContainer ---

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return BOTTOM_SLOTS;
        }
        return SIDE_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction) {
        if (direction == Direction.DOWN) {
            return false;
        }
        return slot == SLOT_CASING && canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return false;
    }

    // --- NBT Persistence ---

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putInt("Status", this.automatonStatus.getIndex());
        output.putString("AutomatonName", this.automatonName);
        if (this.automatonUuid != null) {
            long most = this.automatonUuid.getMostSignificantBits();
            long least = this.automatonUuid.getLeastSignificantBits();
            output.putIntArray("AutomatonUUID", new int[]{
                    (int) (most >> 32), (int) most,
                    (int) (least >> 32), (int) least
            });
        }

        ValueOutput.TypedOutputList<String> logList = output.list("Logs", com.mojang.serialization.Codec.STRING);
        for (String entry : this.logs) {
            logList.add(entry);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.items.clear();
        ContainerHelper.loadAllItems(input, this.items);
        this.automatonStatus = AutomatonStatus.fromIndex(input.getIntOr("Status", 0));
        this.automatonName = input.getStringOr("AutomatonName", "");

        this.automatonUuid = null;
        input.getIntArray("AutomatonUUID").ifPresent(arr -> {
            if (arr.length == 4) {
                long most = ((long) arr[0] << 32) | (arr[1] & 0xFFFFFFFFL);
                long least = ((long) arr[2] << 32) | (arr[3] & 0xFFFFFFFFL);
                this.automatonUuid = new UUID(most, least);
            }
        });

        this.logs.clear();
        for (String entry : input.listOrEmpty("Logs", com.mojang.serialization.Codec.STRING)) {
            this.logs.add(entry);
        }
    }

    // --- Status & State ---

    public AutomatonStatus getAutomatonStatus() {
        return automatonStatus;
    }

    public void setAutomatonStatus(AutomatonStatus status) {
        this.automatonStatus = status;
        this.updateIndicatorState();
        this.setChanged();
    }

    public void updateIndicatorState() {
        if (this.level != null && !this.level.isClientSide()) {
            BlockState state = this.level.getBlockState(this.worldPosition);
            if (state.getBlock() instanceof AutomatonControllerBlock) {
                IndicatorState indicator = this.automatonStatus.getIndicatorState();
                if (state.getValue(AutomatonControllerBlock.INDICATOR) != indicator) {
                    this.level.setBlock(this.worldPosition,
                            state.setValue(AutomatonControllerBlock.INDICATOR, indicator), 3);
                }
            }
        }
    }

    @Nullable
    public UUID getAutomatonUuid() {
        return automatonUuid;
    }

    public void setAutomatonUuid(@Nullable UUID uuid) {
        this.automatonUuid = uuid;
        this.setChanged();
    }

    public String getAutomatonName() {
        return automatonName;
    }

    public void setAutomatonName(String name) {
        this.automatonName = name;
        this.setChanged();
    }

    public List<String> getLogs() {
        return logs;
    }

    public void addLog(String entry) {
        this.logs.add(entry);
        while (this.logs.size() > MAX_LOG_ENTRIES) {
            this.logs.remove(0);
        }
        this.setChanged();
    }

    public void clearLogs() {
        this.logs.clear();
        this.setChanged();
    }

    public int getActiveTab() {
        return activeTab;
    }

    public void setActiveTab(int tab) {
        this.activeTab = tab;
    }

    public ContainerData getContainerData() {
        return containerData;
    }
}
