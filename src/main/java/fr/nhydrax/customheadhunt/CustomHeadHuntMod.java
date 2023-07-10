package fr.nhydrax.customheadhunt;

import fr.nhydrax.customheadhunt.command.ModCommands;
import fr.nhydrax.customheadhunt.event.UseBlockHandler;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomHeadHuntMod implements ModInitializer {
	public static final String MOD_ID = "chh";
	public static final Logger LOGGER = LoggerFactory.getLogger("chh");

	/*
	* TODO :
	*  	- Ajouter un retour au clic d'une tête si victoire avec gain configurable
	*   - Ajouter un nom custom dans les commandes pour les têtes à trouver (oeuf/egg...)
	*   - Ajouter une commande pour aider à la recherche ? (tirage aléatoire d'une tête pas trouvée, retour "moins de 25 blocs" ou distance...)
	* */

	@Override
	public void onInitialize() {
		LOGGER.info("Loading Custom Head Hunt mod !");
		ModCommands.register();
		UseBlockCallback.EVENT.register(new UseBlockHandler());
	}
}