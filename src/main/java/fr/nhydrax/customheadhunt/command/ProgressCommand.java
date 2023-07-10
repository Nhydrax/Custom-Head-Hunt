package fr.nhydrax.customheadhunt.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import fr.nhydrax.customheadhunt.CustomHeadHuntMod;
import fr.nhydrax.customheadhunt.utils.ModUtils;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.literal;

public class ProgressCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
                literal("chh").then(
                        literal("progress")
                                .executes(ProgressCommand::displayProgress)
                )
        );
    }

    private static int displayProgress(CommandContext<ServerCommandSource> context) {
        ServerCommandSource src = context.getSource();
        int[] progress = ModUtils.getPlayerProgress(src.getPlayer().getUuidAsString());
        MutableText feedback;
        if (progress[0] == progress[1]) {
            feedback = Text.translatable("command.chh.all_head_found").formatted(Formatting.GOLD);
        } else {
            feedback = Text.translatable("command.chh.progress_feedback", progress[0], progress[1]).formatted(Formatting.AQUA);
        }
        src.sendFeedback(feedback, false);
        return 0;
    }
}
