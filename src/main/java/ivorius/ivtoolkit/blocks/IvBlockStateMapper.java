/*
 * Copyright 2019 Lukas Tenbrink
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

import ivorius.ivtoolkit.tools.IvNBTHelper;
import ivorius.ivtoolkit.tools.MCRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lukas on 11.02.14.
 */
public class IvBlockStateMapper
{
    private MCRegistry registry;
    private List<BlockState> mapping;

    public IvBlockStateMapper(MCRegistry registry)
    {
        this.registry = registry;
        mapping = new ArrayList<>();
    }

    public IvBlockStateMapper(CompoundNBT compound, String key, MCRegistry registry)
    {
        this(compound.getList(key, Constants.NBT.TAG_STRING), registry);
    }

    public IvBlockStateMapper(ListNBT list, MCRegistry registry)
    {
        this.registry = registry;
        mapping = new ArrayList<>(list.size());

        for (int i = 0; i < list.size(); i++)
            mapping.add(BlockStates.readBlockState(registry, list.getCompound(i)));
    }

    public void addMapping(BlockState state)
    {
        if (!mapping.contains(state))
            mapping.add(state);
    }

    public void addMapping(List<BlockState> states)
    {
        states.forEach(this::addMapping);
    }

    public int getMapping(BlockState state)
    {
        return mapping.indexOf(state);
    }

    public BlockState getState(int mapping)
    {
        return this.mapping.get(mapping);
    }

    public int getMapSize()
    {
        return mapping.size();
    }

    public ListNBT createTagList()
    {
        return mapping.stream()
                .map(state -> BlockStates.writeBlockState(registry, state))
                .collect(Collectors.toCollection(ListNBT::new));
    }

    public CompoundNBT createNBTForStates(List<BlockState> states)
    {
        CompoundNBT compound = new CompoundNBT();

        int[] vals = new int[states.size()];
        for (int i = 0; i < states.size(); i++)
            vals[i] = getMapping(states.get(i));
        CompoundNBT compressed = new CompoundNBT();
        IvNBTHelper.writeCompressed("data", vals, getMapSize() - 1, compressed);
        compound.put("blocksCompressed", compressed);

//        if (getMapSize() <= Byte.MAX_VALUE)
//        {
//            byte[] byteArray = new byte[states.length];
//
//            for (int i = 0; i < states.length; i++)
//            {
//                byteArray[i] = (byte) getMapping(states[i]);
//            }
//
//            compound.setByteArray("blockBytes", byteArray);
//        }
//        else
//        {
//            int[] intArray = new int[states.length];
//
//            for (int i = 0; i < states.length; i++)
//            {
//                intArray[i] = getMapping(states[i]);
//            }
//
//            compound.setIntArray("blockInts", intArray);
//        }

        return compound;
    }

    public BlockState[] createStatesFromNBT(CompoundNBT compound)
    {
        BlockState[] blocks;

        if (compound.contains("blocksCompressed"))
        {
            CompoundNBT compressed = compound.getCompound("blocksCompressed");
            int[] vals = IvNBTHelper.readCompressed("data", compressed);

            blocks = new BlockState[vals.length];

            for (int i = 0; i < vals.length; i++)
                blocks[i] = getState(vals[i]);
        }
//        else if (compound.hasKey("blockBytes"))
//        {
//            byte[] byteArray = compound.getByteArray("blockBytes");
//            blocks = new BlockState[byteArray.length];
//
//            for (int i = 0; i < byteArray.length; i++)
//                blocks[i] = getState(byteArray[i]);
//        }
//        else if (compound.hasKey("blockInts"))
//        {
//            int[] intArray = compound.getIntArray("blockInts");
//            blocks = new BlockState[intArray.length];
//
//            for (int i = 0; i < intArray.length; i++)
//                blocks[i] = getState(intArray[i]);
//        }
        else
        {
            throw new RuntimeException("Unrecognized block collection type " + compound);
        }

        return blocks;
    }
}
