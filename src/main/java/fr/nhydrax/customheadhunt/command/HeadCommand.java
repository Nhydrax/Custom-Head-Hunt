package fr.nhydrax.customheadhunt.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static fr.nhydrax.customheadhunt.utils.ModUtils.*;
import static net.minecraft.server.command.CommandManager.literal;

public class HeadCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
          literal("chh").then(
                  literal("add")
                          .requires(src -> src.hasPermissionLevel(4)).then(
                                  CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                                          .executes(HeadCommand::addHeadEntry)
                          )

          )
        );
        dispatcher.register(
                literal("chh").then(
                        literal("remove")
                                .requires(src -> src.hasPermissionLevel(4)).then(
                                        CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                                                .executes(HeadCommand::removeHeadEntry)
                                )

                )
        );
    }

    private static int addHeadEntry(CommandContext<ServerCommandSource> context) {
        int res = 0;
        ServerCommandSource src = context.getSource();
        MutableText feedback = null;
        World world = src.getWorld();
        BlockPos blockPos;
        try {
            blockPos = BlockPosArgumentType.getLoadedBlockPos(context, "pos");
        } catch (CommandSyntaxException e) {
            res = -1;
            blockPos = null;
            feedback = Text.translatable("blockpos_error", e.getMessage()).formatted(Formatting.RED);
        }

        if (blockPos != null){
            String posKey = getPosKey(world, blockPos);
            if(isHead(world.getBlockState(blockPos))) {
                res = addHead(posKey);
                feedback = Text.translatable("command.chh.head_" + (res == 0 ? "added" : "already_exists"), posKey).formatted(Formatting.GREEN);
            } else {
                feedback = Text.translatable("command.chh.not_a_head", posKey).formatted(Formatting.RED);
                res = -1;
            }
        }

        src.sendFeedback(feedback, false);
        return res;
    }

    private static int removeHeadEntry(CommandContext<ServerCommandSource> context) {
        int res = 0;
        ServerCommandSource src = context.getSource();
        MutableText feedback = null;
        World world = src.getWorld();
        BlockPos blockPos;
        try {
            blockPos = BlockPosArgumentType.getLoadedBlockPos(context, "pos");
        } catch (CommandSyntaxException e) {
            res = -1;
            blockPos = null;
            feedback = Text.translatable("blockpos_error", e.getMessage()).formatted(Formatting.RED);
        }

        if (blockPos != null){
            String posKey = getPosKey(world, blockPos);
            if(isHead(world.getBlockState(blockPos))) {
                res = removeHead(posKey);
                feedback = Text.translatable("command.chh.head_" + (res == 0 ? "removed" : "not_in_list"), posKey).formatted(Formatting.GREEN);
            } else {
                feedback = Text.translatable("command.chh.not_a_head", posKey).formatted(Formatting.RED);
                res = -1;
            }
        }

        src.sendFeedback(feedback, false);
        return res;
    }
}
