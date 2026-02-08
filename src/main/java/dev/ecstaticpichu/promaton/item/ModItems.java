package dev.ecstaticpichu.promaton.item;

import dev.ecstaticpichu.promaton.ProgrammableAutomatons;
import dev.ecstaticpichu.promaton.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public class ModItems {

    public static <T extends Item> T register(String name, Function<Item.Properties, T> factory, Item.Properties properties) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ProgrammableAutomatons.MOD_ID, name));
        T item = factory.apply(properties.setId(key));
        Registry.register(BuiltInRegistries.ITEM, key, item);
        return item;
    }

    public static final Item AUTOMATON_CASING = register("automaton_casing", Item::new, new Item.Properties());
    public static final Item PROGRAM = register("program", Item::new, new Item.Properties());
    public static final Item WAYPOINT_WAND = register("waypoint_wand", Item::new, new Item.Properties());
    public static final Item ANCHOR_CRYSTAL = register("anchor_crystal", Item::new, new Item.Properties());

    public static final ResourceKey<CreativeModeTab> PROMATON_TAB_KEY = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(),
            Identifier.fromNamespaceAndPath(ProgrammableAutomatons.MOD_ID, "promaton_items")
    );

    public static final CreativeModeTab PROMATON_TAB = FabricItemGroup.builder()
            .icon(() -> new ItemStack(AUTOMATON_CASING))
            .title(Component.translatable("itemGroup.promaton"))
            .displayItems((params, output) -> {
                output.accept(ModBlocks.AUTOMATON_CONTROLLER.asItem());
                output.accept(AUTOMATON_CASING);
                output.accept(PROGRAM);
                output.accept(WAYPOINT_WAND);
                output.accept(ANCHOR_CRYSTAL);
            })
            .build();

    public static void registerModItems() {
        ProgrammableAutomatons.LOGGER.info("Registering Mod Items for " + ProgrammableAutomatons.MOD_ID);

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, PROMATON_TAB_KEY, PROMATON_TAB);
    }
}
