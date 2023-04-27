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

package ivorius.ivtoolkit.tools;

import com.google.common.primitives.Ints;
import ivorius.ivtoolkit.IvToolkit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Created by lukas on 21.06.16.
 */
public class NBTStateInjector
{
    public static final String ID_FIX_TAG_KEY = "SG_ID_FIX_TAG";

    public static void recursivelyInject(INBT nbt)
    {
        NBTWalker.walkCompounds(nbt, cmp ->
        {
            inject(cmp);
            return true;
        });
    }

    public static void inject(CompoundNBT compound)
    {
        ListNBT list = new ListNBT();

        injectTEBlockFixTags(compound, "vanishingTileEntity", list, "BlockID");
        injectTEBlockFixTags(compound, "fenceGateTileEntity", list, "camoBlock");
        injectTEBlockFixTags(compound, "mixedBlockTileEntity", list, "block1", "block2");
        injectTEBlockFixTags(compound, "customDoorTileEntity", list, "frame", "topMaterial", "bottomMaterial");

        if (list.size() > 0)
        {
            compound.put(ID_FIX_TAG_KEY, list);
        }
    }

    private static boolean hasPrimitive(CompoundNBT compound, String key)
    {
        return compound.contains(key) && compound.get(key) != null;
    }

    public static void injectTEBlockFixTags(CompoundNBT compound, String tileEntityID, ListNBT list, String... keys)
    {
        if (tileEntityID.equals(compound.getString("id")))
        {
            for (String key : keys)
                if (hasPrimitive(compound, key))
                    addBlockTag(compound.getInt(key), list, key);
        }
    }

    public static void addBlockTag(int blockID, ListNBT tagList, String tagDest)
    {
        BlockState state = Block.stateById(blockID);
        if (state != Blocks.AIR.defaultBlockState())
        {
            String stringID = ForgeRegistries.BLOCKS.getKey(state.getBlock()).toString();

            CompoundNBT idCompound = new CompoundNBT();
            idCompound.putString("type", "block");
            idCompound.putString("tagDest", tagDest);
            idCompound.putString("blockID", stringID);

            tagList.add(idCompound);
        }
        else
        {
            IvToolkit.logger.warn("Failed to apply block tag for structure with ID '" + blockID + "'");
        }
    }

    public static void recursivelyApply(INBT nbt, MCRegistry registry, boolean remove)
    {
        NBTWalker.walkCompounds(nbt, cmp ->
        {
            apply(cmp, registry);
            if (remove) cmp.remove(ID_FIX_TAG_KEY);
            return true;
        });
    }

    public static void apply(CompoundNBT compound, MCRegistry registry)
    {
        if (compound.contains(ID_FIX_TAG_KEY))
        {
            ListNBT list = compound.getList(ID_FIX_TAG_KEY, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++)
                applyIDFixTag(compound, registry, list.getCompound(i));
        }

        if (compound.contains("id") && compound.contains("Count") && compound.contains("Damage")) // Prooobably an item stack
        {
            if (Ints.tryParse(compound.getString("id")) == null) // If this is null, we have a String ID
                registry.modifyItemStackCompound(compound, new ResourceLocation(compound.getString("id")));
        }
    }

    public static void applyIDFixTag(CompoundNBT compound, MCRegistry registry, CompoundNBT fixTag)
    {
        String type = fixTag.getString("type");

        switch (type)
        {
            case "item":
            {
                // Items now read Strings \o/
                compound.putString(fixTag.getString("tagDest"), fixTag.getString("itemID"));

                break;
            }
            case "block":
            {
                String dest = fixTag.getString("tagDest");
                ResourceLocation blockID = new ResourceLocation(fixTag.getString("blockID"));

                Block block = registry.blockFromID(blockID);
                if (block != null)
                    compound.putInt(dest, Block.getId(block.defaultBlockState()));
                else
                    IvToolkit.logger.warn("Failed to fix block tag from structure with ID '" + fixTag.getString("blockID") + "'");
                break;
            }
            default:
                IvToolkit.logger.warn("Unrecognized ID fix tag in structure with type '" + type + "'");
                break;
        }
    }
}
