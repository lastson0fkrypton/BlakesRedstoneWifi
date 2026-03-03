package com.blakesredstonewifi.block.entity;

import com.blakesredstonewifi.network.WifiNetworkManager;
import com.blakesredstonewifi.registry.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WifiReceiverBlockEntity extends BlockEntity {
    private String channel = "";
    private boolean registered;

    public WifiReceiverBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WIFI_RECEIVER, pos, state);
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String nextChannel) {
        String normalized = normalize(nextChannel);
        if (normalized.equals(this.channel)) {
            return;
        }

        this.channel = normalized;
        this.markDirty();
        registerOrUpdateNetwork();
    }

    public int getOutputPower() {
        if (this.world == null || this.world.isClient()) {
            return 0;
        }
        return WifiNetworkManager.getChannelPower(this.world, this.channel);
    }

    @Override
    public void markRemoved() {
        unregisterFromNetwork();
        super.markRemoved();
    }

    @Override
    public void setWorld(net.minecraft.world.World world) {
        super.setWorld(world);
        this.registered = false;
    }

    public static void tick(World world, BlockPos pos, BlockState state, WifiReceiverBlockEntity blockEntity) {
        if (!(world instanceof ServerWorld)) {
            return;
        }

        if (!blockEntity.registered) {
            blockEntity.registerOrUpdateNetwork();
        }
    }

    private void registerOrUpdateNetwork() {
        if (!(this.world instanceof ServerWorld serverWorld)) {
            return;
        }

        WifiNetworkManager.upsertReceiver(serverWorld, this.pos, this.channel);
        this.registered = true;
    }

    private void unregisterFromNetwork() {
        if (!(this.world instanceof ServerWorld serverWorld) || !this.registered) {
            return;
        }

        WifiNetworkManager.removeReceiver(serverWorld, this.pos);
        this.registered = false;
    }

    private static String normalize(String name) {
        if (name == null) {
            return "";
        }
        return name.trim();
    }
}
