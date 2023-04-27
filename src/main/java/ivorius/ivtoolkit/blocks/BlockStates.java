/*
 * Copyright 2016 Lukas Tenbrink
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

package ivorius.ivtoolkit.blocks;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import ivorius.ivtoolkit.IvToolkit;
import ivorius.ivtoolkit.tools.MCRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.StateHolder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.fixes.BlockStateFlatteningMap;
import net.minecraft.util.datafix.fixes.BlockStateFlatternEntities;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;

/**
 * Created by lukas on 06.05.16.
 */
public class BlockStates
{
    @Nonnull
    public static BlockState readBlockState(@Nonnull MCRegistry registry, @Nonnull CompoundNBT compound)
    {
        if (!compound.contains("Name", 8)) {
            return Blocks.AIR.defaultBlockState();
        }
        else {
            Block block = registry.blockFromID(new ResourceLocation(compound.getString("Name")));
            BlockState BlockState = block.defaultBlockState();

            if (compound.contains("Properties", 10)) {
                CompoundNBT propertyCompound = compound.getCompound("Properties");
                StateContainer<Block, BlockState> stateContainer = block.getStateDefinition();

                for (String name : propertyCompound.getAllKeys()) {
                    Property<?> iproperty = stateContainer.getProperty(name);
                    if (iproperty != null) {
                        BlockState = setValueHelper(BlockState, iproperty, name, propertyCompound, compound);
                    }
                }
            }

            return BlockState;
        }

    }

    private static <O, S extends StateHolder<O, S>, T extends Comparable<T>> S setValueHelper(S val, Property<T> property, String name, CompoundNBT propertyCompound, CompoundNBT p_193590_4_)
    {
        Optional<T> optional = property.getValue(propertyCompound.getString(name));
        if (optional.isPresent()) {
            return val.setValue(property, optional.get());
        }
        else {
            IvToolkit.logger.warn("Unable to read property: {} with value: {} for blockstate: {}", name, propertyCompound.getString(name), p_193590_4_.toString());
            return val;
        }
    }

    @Nonnull
    public static CompoundNBT writeBlockState(@Nonnull MCRegistry registry, @Nonnull BlockState state)
    {
        CompoundNBT CompoundNBT = new CompoundNBT();

        CompoundNBT.putString("Name", registry.idFromBlock(state.getBlock()).toString());

        ImmutableMap<Property<?>, Comparable<?>> properties = state.getValues();
        if (!properties.isEmpty()) {
            CompoundNBT propertyCompound = new CompoundNBT();

            for (Map.Entry<Property<?>, Comparable<?>> entry : properties.entrySet()) {
                Property<?> property = entry.getKey();

                propertyCompound.putString(
                        property.getName(),
                        ((Property) property).getName(entry.getValue())
                );
            }

            CompoundNBT.put("Properties", propertyCompound);
        }

        return CompoundNBT;
    }

    @Nonnull
    public static BlockState readBlockState(@Nonnull MCRegistry registry, @Nonnull PacketBuffer buf)
    {
        CompoundNBT tag = buf.readNbt();
        return tag != null
                ? readBlockState(registry, tag)
                : Blocks.AIR.defaultBlockState();
    }

    public static void writeBlockState(@Nonnull MCRegistry registry, @Nonnull PacketBuffer buf, @Nonnull BlockState state)
    {
        buf.writeNbt(writeBlockState(registry, state));
    }

    public static BlockState fromLegacyMetadata(String blockID, byte metadata)
    {
        int legacyBlockID = BlockStateFlatternEntities.getBlockId(blockID);
        Dynamic<?> dynamicNBT = BlockStateFlatteningMap.getTag((legacyBlockID << 4) + metadata & 15);
        CompoundNBT compound = (CompoundNBT) dynamicNBT.getValue();
        return NBTUtil.readBlockState(compound);
    }
}
