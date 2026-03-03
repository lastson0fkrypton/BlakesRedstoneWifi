package com.blakesredstonewifi.block;

import com.blakesredstonewifi.block.entity.WifiEmitterBlockEntity;
import com.blakesredstonewifi.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WifiEmitterBlock extends BlockWithEntity {
    public static final MapCodec<WifiEmitterBlock> CODEC = createCodec(WifiEmitterBlock::new);

    public WifiEmitterBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WifiEmitterBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.WIFI_EMITTER, WifiEmitterBlockEntity::tick);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (world.isClient()) {
            return;
        }

        if (world.getBlockEntity(pos) instanceof WifiEmitterBlockEntity emitter && itemStack.contains(DataComponentTypes.CUSTOM_NAME)) {
            emitter.setChannel(itemStack.getName().getString());
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        if (!(world.getBlockEntity(pos) instanceof WifiEmitterBlockEntity emitter)) {
            return ActionResult.PASS;
        }

        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
        if (stack.isOf(Items.NAME_TAG) && stack.contains(DataComponentTypes.CUSTOM_NAME)) {
            String channel = stack.getName().getString().trim();
            if (!channel.isEmpty()) {
                emitter.setChannel(channel);
                player.sendMessage(Text.literal("Emitter channel set to: " + channel), true);
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
                return ActionResult.CONSUME;
            }
        }

        if (player.isSneaking() && stack.isEmpty()) {
            emitter.setChannel("");
            player.sendMessage(Text.literal("Emitter channel cleared"), true);
            return ActionResult.CONSUME;
        }

        player.sendMessage(Text.literal("Emitter channel: " + emitter.getChannel() + " | Input power: " + emitter.getInputPower()), true);
        return ActionResult.SUCCESS;
    }
}
