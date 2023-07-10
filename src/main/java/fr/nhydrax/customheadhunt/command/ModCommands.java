package fr.nhydrax.customheadhunt.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ModCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register(ProgressCommand::register);
        CommandRegistrationCallback.EVENT.register(HeadCommand::register);
    }
}
