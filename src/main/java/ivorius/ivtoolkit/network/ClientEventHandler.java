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
import net.minecraft.entity.player.ServerPlayerEntity;

/**
 * A interface for some object types that need extra information to be communicated
 * between the client and server on client input.
 */
public interface ClientEventHandler
{
    /**
     * Called on the client when constructing the event packet.
     * Data should be added to the provided stream.
     *
     * @param buffer The packet data stream
     */
    void assembleClientEvent(ByteBuf buffer, String context, Object... params);

    /**
     * Called on the server when it receives an update packet.
     * Data should be read out of the stream in the same way as it was written.
     *
     * @param buffer The packet data stream
     */
    void onClientEvent(ByteBuf buffer, String context, ServerPlayerEntity player);
}
