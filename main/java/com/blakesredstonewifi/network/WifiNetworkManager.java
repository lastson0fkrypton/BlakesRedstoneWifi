package com.blakesredstonewifi.network;

import com.blakesredstonewifi.block.WifiReceiverBlock;
import com.blakesredstonewifi.block.entity.WifiReceiverBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class WifiNetworkManager {
    private static final Map<RegistryKey<World>, WorldNetwork> NETWORKS = new HashMap<>();

    private WifiNetworkManager() {
    }

    public static int getChannelPower(World world, String channel) {
        if (world == null || channel == null || channel.trim().isEmpty()) {
            return 0;
        }

        WorldNetwork network = NETWORKS.get(world.getRegistryKey());
        if (network == null) {
            return 0;
        }

        return network.getChannelPower(channel.trim());
    }

    public static void upsertEmitter(ServerWorld world, BlockPos pos, String channel, int power) {
        WorldNetwork network = getNetwork(world);
        Set<String> changedChannels = network.upsertEmitter(pos.toImmutable(), channel, power);
        notifyReceivers(world, network, changedChannels);
    }

    public static void removeEmitter(ServerWorld world, BlockPos pos) {
        WorldNetwork network = getNetwork(world);
        Set<String> changedChannels = network.removeEmitter(pos);
        notifyReceivers(world, network, changedChannels);
    }

    public static void upsertReceiver(ServerWorld world, BlockPos pos, String channel) {
        WorldNetwork network = getNetwork(world);
        network.upsertReceiver(pos.toImmutable(), channel);
        notifyReceivers(world, network, Set.of(channel == null ? "" : channel.trim()));
    }

    public static void removeReceiver(ServerWorld world, BlockPos pos) {
        WorldNetwork network = getNetwork(world);
        network.removeReceiver(pos);
    }

    private static WorldNetwork getNetwork(ServerWorld world) {
        return NETWORKS.computeIfAbsent(world.getRegistryKey(), ignored -> new WorldNetwork());
    }

    private static void notifyReceivers(ServerWorld world, WorldNetwork network, Set<String> channels) {
        if (channels.isEmpty()) {
            return;
        }

        Set<BlockPos> positions = network.getReceiverPositions(channels);
        for (BlockPos receiverPos : positions) {
            if (!world.isChunkLoaded(receiverPos)) {
                continue;
            }

            BlockState receiverState = world.getBlockState(receiverPos);
            if (!(receiverState.getBlock() instanceof WifiReceiverBlock)) {
                continue;
            }

            if (world.getBlockEntity(receiverPos) instanceof WifiReceiverBlockEntity blockEntity) {
                blockEntity.markDirty();
            }

            world.updateNeighbors(receiverPos, receiverState.getBlock());
            world.updateComparators(receiverPos, receiverState.getBlock());
        }
    }

    private static final class WorldNetwork {
        private final Map<BlockPos, EmitterData> emitters = new HashMap<>();
        private final Map<BlockPos, String> receiverChannels = new HashMap<>();
        private final Map<String, Set<BlockPos>> receiversByChannel = new HashMap<>();
        private final Map<String, Integer> channelPower = new HashMap<>();

        private Set<String> upsertEmitter(BlockPos pos, String channel, int power) {
            String normalized = normalize(channel);
            int clampedPower = Math.max(0, Math.min(15, power));

            EmitterData previous = emitters.put(pos, new EmitterData(normalized, clampedPower));
            Set<String> affected = new HashSet<>();
            if (previous != null) {
                affected.add(previous.channel());
            }
            affected.add(normalized);

            return recomputeChangedChannels(affected);
        }

        private Set<String> removeEmitter(BlockPos pos) {
            EmitterData previous = emitters.remove(pos);
            if (previous == null) {
                return Set.of();
            }
            return recomputeChangedChannels(Set.of(previous.channel()));
        }

        private void upsertReceiver(BlockPos pos, String channel) {
            String normalized = normalize(channel);
            String previousChannel = receiverChannels.put(pos, normalized);

            if (previousChannel != null && !previousChannel.equals(normalized)) {
                Set<BlockPos> previousSet = receiversByChannel.get(previousChannel);
                if (previousSet != null) {
                    previousSet.remove(pos);
                    if (previousSet.isEmpty()) {
                        receiversByChannel.remove(previousChannel);
                    }
                }
            }

            receiversByChannel.computeIfAbsent(normalized, ignored -> new HashSet<>()).add(pos);
        }

        private void removeReceiver(BlockPos pos) {
            String previousChannel = receiverChannels.remove(pos);
            if (previousChannel == null) {
                return;
            }

            Set<BlockPos> previousSet = receiversByChannel.get(previousChannel);
            if (previousSet == null) {
                return;
            }

            previousSet.remove(pos);
            if (previousSet.isEmpty()) {
                receiversByChannel.remove(previousChannel);
            }
        }

        private int getChannelPower(String channel) {
            return channelPower.getOrDefault(normalize(channel), 0);
        }

        private Set<BlockPos> getReceiverPositions(Set<String> channels) {
            Set<BlockPos> positions = new HashSet<>();
            for (String channel : channels) {
                Set<BlockPos> perChannel = receiversByChannel.get(normalize(channel));
                if (perChannel != null) {
                    positions.addAll(perChannel);
                }
            }
            return positions;
        }

        private Set<String> recomputeChangedChannels(Set<String> channels) {
            Set<String> changed = new HashSet<>();
            for (String channel : channels) {
                String normalized = normalize(channel);
                int previous = channelPower.getOrDefault(normalized, 0);
                int next = 0;

                if (!normalized.isEmpty()) {
                    for (EmitterData emitterData : emitters.values()) {
                        if (normalized.equals(emitterData.channel())) {
                            next = Math.max(next, emitterData.power());
                            if (next == 15) {
                                break;
                            }
                        }
                    }
                }

                if (next == 0) {
                    channelPower.remove(normalized);
                } else {
                    channelPower.put(normalized, next);
                }

                if (previous != next) {
                    changed.add(normalized);
                }
            }

            return changed;
        }

        private static String normalize(String channel) {
            return channel == null ? "" : channel.trim();
        }
    }

    private record EmitterData(String channel, int power) {
    }
}