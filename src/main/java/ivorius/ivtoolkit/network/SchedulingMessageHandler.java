/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://ivorius.net
 */

package ivorius.ivtoolkit.network;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SchedulingMessageHandler
{
    public static <MSG> BiConsumer<MSG, Supplier<NetworkEvent.Context>> wrap(BiConsumer<MSG, NetworkEvent.Context> handler)
    {
        return (msg, contextSupplier) -> {
            NetworkEvent.Context context = contextSupplier.get();
            schedule(context, () -> handler.accept(msg, context));
        };
    }

    public static void schedule(NetworkEvent.Context context, Runnable runnable)
    {
        if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            scheduleClient(runnable);
        }
        else {
            scheduleServer(context, runnable);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void scheduleClient(Runnable runnable)
    {
        Minecraft.getInstance().execute(runnable);
    }

    public static void scheduleServer(NetworkEvent.Context context, Runnable runnable)
    {
        MinecraftServer server = context.getSender().getLevel().getServer();
        server.execute(runnable);
    }
}