package com.blakesredstonewifi.block.entity;

import com.blakesredstonewifi.network.WifiNetworkManager;
import com.blakesredstonewifi.registry.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WifiEmitterBlockEntity extends BlockEntity {
    private String channel = "";
    private int inputPower;
    private boolean registered;

    public WifiEmitterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WIFI_EMITTER, pos, state);
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

    public void refreshInputPower() {
        if (this.world == null || this.world.isClient()) {
            return;
        }

        int nextPower = this.world.getReceivedRedstonePower(this.pos);
        if (nextPower == this.inputPower) {
            return;
        }

        this.inputPower = nextPower;
        this.markDirty();
        registerOrUpdateNetwork();
    }

    public int getInputPower() {
        return inputPower;
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

    public static void tick(World world, BlockPos pos, BlockState state, WifiEmitterBlockEntity blockEntity) {
        if (!(world instanceof ServerWorld)) {
            return;
        }

        if (!blockEntity.registered) {
            blockEntity.registerOrUpdateNetwork();
        }

        blockEntity.refreshInputPower();
    }

    private void registerOrUpdateNetwork() {
        if (!(this.world instanceof ServerWorld serverWorld)) {
            return;
        }

        WifiNetworkManager.upsertEmitter(serverWorld, this.pos, this.channel, this.inputPower);
        this.registered = true;
    }

    private void unregisterFromNetwork() {
        if (!(this.world instanceof ServerWorld serverWorld) || !this.registered) {
            return;
        }

        WifiNetworkManager.removeEmitter(serverWorld, this.pos);
        this.registered = false;
    }

    private static String normalize(String name) {
        if (name == null) {
            return "";
        }
        return name.trim();
    }
}
