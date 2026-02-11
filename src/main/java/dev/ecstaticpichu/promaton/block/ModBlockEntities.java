package dev.ecstaticpichu.promaton.block;

import dev.ecstaticpichu.promaton.ProgrammableAutomatons;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {

    public static final BlockEntityType<AutomatonControllerBlockEntity> AUTOMATON_CONTROLLER =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    ResourceKey.create(Registries.BLOCK_ENTITY_TYPE,
                            Identifier.fromNamespaceAndPath(ProgrammableAutomatons.MOD_ID, "automaton_controller")),
                    FabricBlockEntityTypeBuilder.create(AutomatonControllerBlockEntity::new, ModBlocks.AUTOMATON_CONTROLLER)
                            .build()
            );

    public static void initialize() {
        ProgrammableAutomatons.LOGGER.info("Registering Block Entities for " + ProgrammableAutomatons.MOD_ID);
    }
}
