/*
 * Copyright 2014 Lukas Tenbrink
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ivorius.ivtoolkit.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lukas on 02.07.14.
 */
public class IvNetworkHelperServer
{
    public static <UTileEntity extends TileEntity & PartialUpdateHandler> void sendTileEntityUpdatePacket(UTileEntity tileEntity, String context, SimpleChannel network, PlayerEntity player, Object... params)
    {
        if (!(player instanceof ServerPlayerEntity))
            throw new UnsupportedOperationException();

        PacketTileEntityData packet = PacketTileEntityData.packetEntityData(tileEntity, context, params);
        sendToPlayer(network, (ServerPlayerEntity) player, packet);
    }

    public static <UTileEntity extends TileEntity & PartialUpdateHandler> void sendTileEntityUpdatePacket(UTileEntity tileEntity, String context, SimpleChannel network, Object... params)
    {
        sendToPlayersWatchingChunk(tileEntity.getLevel(), tileEntity.getBlockPos().getX() / 16, tileEntity.getBlockPos().getZ() / 16, network, PacketTileEntityData.packetEntityData(tileEntity, context, params));
    }

    public static void sendToPlayersWatchingChunk(World world, int chunkX, int chunkZ, SimpleChannel channel, Object message)
    {
        channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunk(chunkX, chunkZ)), message);
    }

    public static void sendToPlayersWatchingChunk(World world, int chunkX, int chunkZ, IPacket packet)
    {
        PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunk(chunkX, chunkZ)).send(packet);
    }

    public static void sendToPlayer(SimpleChannel channel, ServerPlayerEntity playerMP, Object message)
    {
        channel.send(PacketDistributor.PLAYER.with(() -> playerMP), message);
    }

    public static List<ServerPlayerEntity> getPlayersWatchingChunk(World world, int chunkX, int chunkZ)
    {
        if (world.isClientSide)
        {
            return Collections.emptyList();
        }

        ServerWorld server = (ServerWorld) world;

        ArrayList<ServerPlayerEntity> playersWatching = new ArrayList<>(server.getChunkSource().chunkMap.getPlayers(new ChunkPos(chunkX, chunkZ), false).collect(Collectors.toCollection(ArrayList::new)));

        return playersWatching;
    }

    public static void sendEEPUpdatePacketToPlayer(Entity entity, String capabilityKey, Direction facing, String context, SimpleChannel network, PlayerEntity player, Object... params)
    {
        if (!(player instanceof ServerPlayerEntity))
            throw new UnsupportedOperationException();

        PacketEntityCapabilityData packet = PacketEntityCapabilityData.packetEntityData(entity, capabilityKey, facing, context, params);
        sendToPlayer(network, (ServerPlayerEntity) player, packet);
    }

    public static void sendEEPUpdatePacket(LivingEntity entity, String capabilityKey, Direction facing, String context, SimpleChannel network, Object... params)
    {
        if (entity.level.isClientSide)
            throw new UnsupportedOperationException();

        for (PlayerEntity player : entity.getCommandSenderWorld().getNearbyPlayers(new EntityPredicate(), entity, entity.getBoundingBox().inflate(20)))
            sendEEPUpdatePacketToPlayer(entity, capabilityKey, facing, context, network, player, params);

        if (entity instanceof ServerPlayerEntity) // Players don't 'track' themselves
            sendEEPUpdatePacketToPlayer(entity, capabilityKey, facing, context, network, (PlayerEntity) entity, params);
    }
}
