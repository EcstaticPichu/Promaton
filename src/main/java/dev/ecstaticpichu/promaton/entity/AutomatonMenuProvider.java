package dev.ecstaticpichu.promaton.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public class AutomatonMenuProvider implements ExtendedScreenHandlerFactory<Integer> {

    private final AutomatonEntity automaton;

    public AutomatonMenuProvider(AutomatonEntity automaton) {
        this.automaton = automaton;
    }

    @Override
    public Integer getScreenOpeningData(ServerPlayer player) {
        return this.automaton.getId();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.promaton.automaton");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new AutomatonMenu(syncId, playerInventory, this.automaton);
    }
}
