package dev.ecstaticpichu.promaton.entity;

import net.minecraft.core.NonNullList;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class AutomatonEntity extends Mannequin implements Container {

    public static final int INVENTORY_SIZE = 27;

    // Synced data fields
    private static final EntityDataAccessor<Float> DATA_HUNGER =
            SynchedEntityData.defineId(AutomatonEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_SATURATION =
            SynchedEntityData.defineId(AutomatonEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<String> DATA_REST_STATUS =
            SynchedEntityData.defineId(AutomatonEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> DATA_XP_BUFFER =
            SynchedEntityData.defineId(AutomatonEntity.class, EntityDataSerializers.INT);

    // General inventory (27 slots)
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

    // Non-synced persistent fields
    private float foodEatenToday = 0.0f;
    private int sleepTicks = 0;
    @Nullable
    private int[] boundController = null;
    private String customSkin = "";
    @Nullable
    private UUID companionOf = null;
    private int companionPosition = 0;
    private int activeTab = 0;

    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> activeTab;
                case 1 -> 0; // Automaton status — placeholder
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) activeTab = value;
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public ContainerData getContainerData() {
        return containerData;
    }

    @SuppressWarnings("unchecked")
    public AutomatonEntity(EntityType<? extends Mannequin> type, Level level) {
        super((EntityType<Mannequin>) type, level);
    }

    // --- Attributes ---

    public static AttributeSupplier.Builder createAttributes() {
        return Mannequin.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.1)
                .add(Attributes.ATTACK_DAMAGE, 1.0)
                .add(Attributes.ATTACK_SPEED, 4.0);
    }

    // --- Synced Data ---

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_HUNGER, 20.0f);
        builder.define(DATA_SATURATION, 5.0f);
        builder.define(DATA_REST_STATUS, "tired");
        builder.define(DATA_XP_BUFFER, 0);
    }

    // --- Hunger ---

    public float getHunger() {
        return this.entityData.get(DATA_HUNGER);
    }

    public void setHunger(float hunger) {
        this.entityData.set(DATA_HUNGER, Math.max(0.0f, Math.min(20.0f, hunger)));
    }

    public float getSaturation() {
        return this.entityData.get(DATA_SATURATION);
    }

    public void setSaturation(float saturation) {
        this.entityData.set(DATA_SATURATION, Math.max(0.0f, saturation));
    }

    public float getFoodEatenToday() {
        return foodEatenToday;
    }

    public void setFoodEatenToday(float amount) {
        this.foodEatenToday = amount;
    }

    // --- Rest ---

    public String getRestStatus() {
        return this.entityData.get(DATA_REST_STATUS);
    }

    public void setRestStatus(String status) {
        this.entityData.set(DATA_REST_STATUS, status);
    }

    public int getSleepTicks() {
        return sleepTicks;
    }

    public void setSleepTicks(int ticks) {
        this.sleepTicks = ticks;
    }

    // --- XP ---

    public int getXPBuffer() {
        return this.entityData.get(DATA_XP_BUFFER);
    }

    public void setXPBuffer(int xp) {
        this.entityData.set(DATA_XP_BUFFER, Math.max(0, xp));
    }

    // --- Binding ---

    @Nullable
    public int[] getBoundController() {
        return boundController;
    }

    public void setBoundController(@Nullable int[] pos) {
        this.boundController = pos;
    }

    // --- Skin ---

    public String getCustomSkin() {
        return customSkin;
    }

    public void setCustomSkin(String skin) {
        this.customSkin = skin;
    }

    // --- Companion ---

    @Nullable
    public UUID getCompanionOf() {
        return companionOf;
    }

    public void setCompanionOf(@Nullable UUID playerUuid) {
        this.companionOf = playerUuid;
    }

    public int getCompanionPosition() {
        return companionPosition;
    }

    public void setCompanionPosition(int position) {
        this.companionPosition = position;
    }

    // --- Container (27 general inventory slots) ---

    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.inventory) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot < 0 || slot >= INVENTORY_SIZE) {
            return ItemStack.EMPTY;
        }
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.inventory, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot >= 0 && slot < INVENTORY_SIZE) {
            this.inventory.set(slot, stack);
            stack.limitSize(this.getMaxStackSize(stack));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.isAlive() && player.distanceToSqr(this) <= 64.0;
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
    }

    @Override
    public void setChanged() {
        // No-op for entity-based container; entity data is saved via NBT methods
    }

    // --- Item Pickup ---

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.isAlive() && this.tickCount % 10 == 0) {
            pickUpNearbyItems();
        }
    }

    private void pickUpNearbyItems() {
        AABB pickupBox = this.getBoundingBox().inflate(2.0);
        List<ItemEntity> items = this.level().getEntitiesOfClass(ItemEntity.class, pickupBox);
        for (ItemEntity itemEntity : items) {
            if (!itemEntity.isAlive()) continue;
            ItemStack stack = itemEntity.getItem();
            ItemStack remaining = addToInventory(stack.copy());
            if (remaining.isEmpty()) {
                itemEntity.discard();
            } else if (remaining.getCount() < stack.getCount()) {
                itemEntity.setItem(remaining);
            }
        }
    }

    private ItemStack addToInventory(ItemStack stack) {
        // Try to merge with existing stacks first
        for (int i = 0; i < INVENTORY_SIZE && !stack.isEmpty(); i++) {
            ItemStack existing = this.inventory.get(i);
            if (!existing.isEmpty() && ItemStack.isSameItemSameComponents(existing, stack)) {
                int space = existing.getMaxStackSize() - existing.getCount();
                if (space > 0) {
                    int toAdd = Math.min(space, stack.getCount());
                    existing.grow(toAdd);
                    stack.shrink(toAdd);
                }
            }
        }
        // Then try empty slots
        for (int i = 0; i < INVENTORY_SIZE && !stack.isEmpty(); i++) {
            if (this.inventory.get(i).isEmpty()) {
                this.inventory.set(i, stack.copy());
                stack.setCount(0);
            }
        }
        return stack;
    }

    // --- Interaction ---

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            return super.interact(player, hand);
        }
        if (!this.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new AutomatonMenuProvider(this));
        }
        return InteractionResult.SUCCESS;
    }

    // --- Death Drops ---

    @Override
    protected void dropAllDeathLoot(ServerLevel serverLevel, DamageSource damageSource) {
        // Drop general inventory
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            ItemStack stack = this.inventory.get(i);
            if (!stack.isEmpty()) {
                this.spawnAtLocation(serverLevel, stack);
                this.inventory.set(i, ItemStack.EMPTY);
            }
        }
        // Drop equipment explicitly (Mannequin may not have a loot table)
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack equip = this.getItemBySlot(slot);
            if (!equip.isEmpty()) {
                this.spawnAtLocation(serverLevel, equip);
                this.setItemSlot(slot, ItemStack.EMPTY);
            }
        }
        super.dropAllDeathLoot(serverLevel, damageSource);
    }

    // --- NBT Persistence ---

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);

        // Inventory
        ContainerHelper.saveAllItems(output, this.inventory);

        // Synced data fields
        output.putFloat("Hunger", this.getHunger());
        output.putFloat("Saturation", this.getSaturation());
        output.putString("RestStatus", this.getRestStatus());
        output.putInt("XPBuffer", this.getXPBuffer());

        // Non-synced fields
        output.putFloat("FoodEatenToday", this.foodEatenToday);
        output.putInt("SleepTicks", this.sleepTicks);
        output.putString("CustomSkin", this.customSkin);
        output.putInt("CompanionPosition", this.companionPosition);

        if (this.boundController != null) {
            output.putIntArray("BoundController", this.boundController);
        }

        if (this.companionOf != null) {
            long most = this.companionOf.getMostSignificantBits();
            long least = this.companionOf.getLeastSignificantBits();
            output.putIntArray("CompanionOf", new int[]{
                    (int) (most >> 32), (int) most,
                    (int) (least >> 32), (int) least
            });
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);

        // Inventory
        this.inventory.clear();
        ContainerHelper.loadAllItems(input, this.inventory);

        // Synced data fields
        this.setHunger(input.getFloatOr("Hunger", 20.0f));
        this.setSaturation(input.getFloatOr("Saturation", 5.0f));
        this.setRestStatus(input.getStringOr("RestStatus", "tired"));
        this.setXPBuffer(input.getIntOr("XPBuffer", 0));

        // Non-synced fields
        this.foodEatenToday = input.getFloatOr("FoodEatenToday", 0.0f);
        this.sleepTicks = input.getIntOr("SleepTicks", 0);
        this.customSkin = input.getStringOr("CustomSkin", "");
        this.companionPosition = input.getIntOr("CompanionPosition", 0);

        this.boundController = null;
        input.getIntArray("BoundController").ifPresent(arr -> {
            if (arr.length == 3) {
                this.boundController = arr;
            }
        });

        this.companionOf = null;
        input.getIntArray("CompanionOf").ifPresent(arr -> {
            if (arr.length == 4) {
                long most = ((long) arr[0] << 32) | (arr[1] & 0xFFFFFFFFL);
                long least = ((long) arr[2] << 32) | (arr[3] & 0xFFFFFFFFL);
                this.companionOf = new UUID(most, least);
            }
        });
    }
}
