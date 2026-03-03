package com.blakesredstonewifi.block;

import com.blakesredstonewifi.block.entity.WifiReceiverBlockEntity;
import com.blakesredstonewifi.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.RedstoneWireBlock;
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
import net.minecraft.util.math.Direction;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WifiReceiverBlock extends BlockWithEntity {
    public static final MapCodec<WifiReceiverBlock> CODEC = createCodec(WifiReceiverBlock::new);
    public static final IntProperty POWER = RedstoneWireBlock.POWER;

    public WifiReceiverBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(POWER, 0));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WifiReceiverBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.WIFI_RECEIVER, WifiReceiverBlockEntity::tick);
    }

    @Override
    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWER);
    }

    @Override
    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return getWeakRedstonePower(state, world, pos, direction);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (world.isClient()) {
            return;
        }

        if (world.getBlockEntity(pos) instanceof WifiReceiverBlockEntity receiver && itemStack.contains(DataComponentTypes.CUSTOM_NAME)) {
            receiver.setChannel(itemStack.getName().getString());
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        if (!(world.getBlockEntity(pos) instanceof WifiReceiverBlockEntity receiver)) {
            return ActionResult.PASS;
        }

        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
        if (stack.isOf(Items.NAME_TAG) && stack.contains(DataComponentTypes.CUSTOM_NAME)) {
            String channel = stack.getName().getString().trim();
            if (!channel.isEmpty()) {
                receiver.setChannel(channel);
                player.sendMessage(Text.literal("Receiver channel set to: " + channel), true);
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
                return ActionResult.CONSUME;
            }
        }

        if (player.isSneaking() && stack.isEmpty()) {
            receiver.setChannel("");
            player.sendMessage(Text.literal("Receiver channel cleared"), true);
            return ActionResult.CONSUME;
        }

        player.sendMessage(Text.literal("Receiver channel: " + receiver.getChannel() + " | Output power: " + receiver.getOutputPower()), true);
        return ActionResult.SUCCESS;
    }
}
