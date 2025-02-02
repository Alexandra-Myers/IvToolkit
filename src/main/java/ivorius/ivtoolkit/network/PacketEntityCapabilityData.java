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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

/**
 * Created by lukas on 01.07.14.
 */
public class PacketEntityCapabilityData implements IvPacket
{
    private int entityID;
    private String context;
    private String capabilityKey;
    private Direction direction;
    private ByteBuf payload;

    public PacketEntityCapabilityData()
    {
    }

    public PacketEntityCapabilityData(int entityID, String context, String capabilityKey, Direction direction, ByteBuf payload)
    {
        this.entityID = entityID;
        this.context = context;
        this.capabilityKey = capabilityKey;
        this.direction = direction;
        this.payload = payload;
    }

    public static <T> PacketEntityCapabilityData packetEntityData(Entity entity, String capabilityKey, Direction facing, String context, Object... params)
    {
        Capability<T> capability = CapabilityUpdateRegistry.INSTANCE.capability(capabilityKey);
        T t = entity.getCapability(capability, facing).orElse(null);

        if (!(t instanceof PartialUpdateHandler))
            throw new IllegalArgumentException("Capability must implement PartialUpdateHandler to send update packets!");

        ByteBuf buf = Unpooled.buffer();
        ((PartialUpdateHandler) t).writeUpdateData(buf, context, params);

        return new PacketEntityCapabilityData(entity.getId(), context, capabilityKey, facing, buf);
    }

    public int getEntityID()
    {
        return entityID;
    }

    public void setEntityID(int entityID)
    {
        this.entityID = entityID;
    }

    public String getContext()
    {
        return context;
    }

    public void setContext(String context)
    {
        this.context = context;
    }

    public String getCapabilityKey()
    {
        return capabilityKey;
    }

    public void setCapabilityKey(String capabilityKey)
    {
        this.capabilityKey = capabilityKey;
    }

    public Direction getDirection()
    {
        return direction;
    }

    public void setDirection(Direction direction)
    {
        this.direction = direction;
    }

    public ByteBuf getPayload()
    {
        return payload;
    }

    public void setPayload(ByteBuf payload)
    {
        this.payload = payload;
    }

    @Override
    public void decode(PacketBuffer buf)
    {
        entityID = buf.readInt();
        context = buf.readUtf(1000);
        capabilityKey = buf.readUtf(1000);
        direction = IvPacketHelper.maybeRead(buf, null,
                () -> direction = Direction.from3DDataValue(buf.readInt()));
        payload = IvPacketHelper.readByteBuffer(buf);
    }

    @Override
    public void encode(PacketBuffer buf)
    {
        buf.writeInt(entityID);
        buf.writeUtf(context, 1000);
        buf.writeUtf(capabilityKey, 1000);
        IvPacketHelper.maybeWrite(buf, direction,
                () -> buf.writeInt(direction.get3DDataValue()));
        IvPacketHelper.writeByteBuffer(buf, payload);
    }

}
