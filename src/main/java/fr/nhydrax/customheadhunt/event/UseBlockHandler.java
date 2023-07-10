package fr.nhydrax.customheadhunt.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import static fr.nhydrax.customheadhunt.utils.ModUtils.*;

public class UseBlockHandler implements UseBlockCallback {
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (!world.isClient() && hand == Hand.MAIN_HAND){
            BlockPos clickedBlockPos = hitResult.getBlockPos();
            Block clickedBlock = world.getBlockState(clickedBlockPos).getBlock();

            if (clickedBlock == Blocks.PLAYER_HEAD || clickedBlock == Blocks.PLAYER_WALL_HEAD) {
                String posKey = getPosKey(world, clickedBlockPos);
                if (isValidHead(posKey)) {
                    int ret = addPlayerFoundHead(player.getUuidAsString(), posKey);
                    player.sendMessage(
                            Text.translatable("chh.head_" + (ret == 0 ? "" : "already_") + "found")
                                    .formatted(ret == 0 ? Formatting.GREEN : Formatting.AQUA)
                    );
                    if (ret == 0) {
                        int[] progress = getPlayerProgress(player.getUuidAsString());
                        MutableText feedback;
                        if (progress[0] == progress[1]) {
                            feedback = Text.translatable("command.chh.all_head_found").formatted(Formatting.GOLD);
                            player.sendMessage(feedback);
                            player.giveItemStack(getRewardItemStack());
                            feedback = Text.translatable("chh.reward_received").formatted(Formatting.AQUA);
                        } else {
                            feedback = Text.translatable("command.chh.progress_feedback", progress[0], progress[1]).formatted(Formatting.AQUA);
                        }
                        player.sendMessage(feedback);
                    }
                }
            }
        }
        return ActionResult.PASS;
    }
}
