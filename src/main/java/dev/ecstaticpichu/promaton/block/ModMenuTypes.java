package dev.ecstaticpichu.promaton.block;

import dev.ecstaticpichu.promaton.ProgrammableAutomatons;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;

public class ModMenuTypes {

    public static final MenuType<AutomatonControllerMenu> AUTOMATON_CONTROLLER =
            Registry.register(
                    BuiltInRegistries.MENU,
                    Identifier.fromNamespaceAndPath(ProgrammableAutomatons.MOD_ID, "automaton_controller"),
                    new ExtendedScreenHandlerType<>(AutomatonControllerMenu::new, BlockPos.STREAM_CODEC)
            );

    public static void initialize() {
        ProgrammableAutomatons.LOGGER.info("Registering Menu Types for " + ProgrammableAutomatons.MOD_ID);
    }
}
