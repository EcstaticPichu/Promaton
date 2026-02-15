package dev.ecstaticpichu.promaton.entity;

import dev.ecstaticpichu.promaton.ProgrammableAutomatons;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {

    private static final ResourceKey<EntityType<?>> AUTOMATON_KEY = ResourceKey.create(Registries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(ProgrammableAutomatons.MOD_ID, "automaton"));

    public static final EntityType<AutomatonEntity> AUTOMATON = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            AUTOMATON_KEY,
            EntityType.Builder.<AutomatonEntity>of(AutomatonEntity::new, MobCategory.MISC)
                    .sized(0.6f, 1.8f)
                    .clientTrackingRange(10)
                    .build(AUTOMATON_KEY)
    );

    public static void initialize() {
        ProgrammableAutomatons.LOGGER.info("Registering Entities for " + ProgrammableAutomatons.MOD_ID);
        FabricDefaultAttributeRegistry.register(AUTOMATON, AutomatonEntity.createAttributes());
    }
}
