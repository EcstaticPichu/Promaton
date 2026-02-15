package dev.ecstaticpichu.promaton;

import dev.ecstaticpichu.promaton.block.AutomatonControllerScreen;
import dev.ecstaticpichu.promaton.block.ModMenuTypes;
import dev.ecstaticpichu.promaton.entity.AutomatonRenderer;
import dev.ecstaticpichu.promaton.entity.AutomatonScreen;
import dev.ecstaticpichu.promaton.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
public class ProgrammableAutomatonsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(ModMenuTypes.AUTOMATON_CONTROLLER, AutomatonControllerScreen::new);
		MenuScreens.register(ModMenuTypes.AUTOMATON, AutomatonScreen::new);

		EntityRendererRegistry.register(ModEntities.AUTOMATON, AutomatonRenderer::new);
	}
}
