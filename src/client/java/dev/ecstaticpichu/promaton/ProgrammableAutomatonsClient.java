package dev.ecstaticpichu.promaton;

import dev.ecstaticpichu.promaton.block.AutomatonControllerScreen;
import dev.ecstaticpichu.promaton.block.ModMenuTypes;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class ProgrammableAutomatonsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(ModMenuTypes.AUTOMATON_CONTROLLER, AutomatonControllerScreen::new);
	}
}
