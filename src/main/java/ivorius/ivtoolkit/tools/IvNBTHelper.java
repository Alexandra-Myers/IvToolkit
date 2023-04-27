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

package ivorius.ivtoolkit.tools;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import ivorius.ivtoolkit.math.IvBytePacker;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by lukas on 23.04.14.
 */
public class IvNBTHelper
{

    public static byte readByte(CompoundNBT compound, String key, byte defaultValue)
    {
        return compound != null && compound.contains(key, Constants.NBT.TAG_BYTE)
                ? compound.getByte(key)
                : defaultValue;
    }

    public static byte[] readByteArray(CompoundNBT compound, String key, byte[] defaultValue)
    {
        return compound != null && compound.contains(key, Constants.NBT.TAG_BYTE_ARRAY)
                ? compound.getByteArray(key)
                : defaultValue;
    }

    public static double readDouble(CompoundNBT compound, String key, double defaultValue)
    {
        return compound != null && compound.contains(key, Constants.NBT.TAG_DOUBLE)
                ? compound.getDouble(key)
                : defaultValue;
    }

    public static float readFloat(CompoundNBT compound, String key, float defaultValue)
    {
        return compound != null && compound.contains(key, Constants.NBT.TAG_FLOAT)
                ? compound.getFloat(key)
                : defaultValue;
    }

    public static int readInt(CompoundNBT compound, String key, int defaultValue)
    {
        return compound != null && compound.contains(key, Constants.NBT.TAG_INT)
                ? compound.getInt(key)
                : defaultValue;
    }

    public static int[] readIntArray(CompoundNBT compound, String key, int[] defaultValue)
    {
        return compound != null && compound.contains(key, Constants.NBT.TAG_INT_ARRAY)
                ? compound.getIntArray(key)
                : defaultValue;
    }

    public static long readLong(CompoundNBT compound, String key, long defaultValue)
    {
        return compound != null && compound.contains(key, Constants.NBT.TAG_LONG)
                ? compound.getLong(key)
                : defaultValue;
    }

    public static short readShort(CompoundNBT compound, String key, short defaultValue)
    {
        return compound != null && compound.contains(key, Constants.NBT.TAG_SHORT)
                ? compound.getShort(key)
                : defaultValue;
    }

    public static String readString(CompoundNBT compound, String key, String defaultValue)
    {
        return compound != null && compound.contains(key, Constants.NBT.TAG_STRING)
                ? compound.getString(key)
                : defaultValue;
    }

    public static double[] readDoubleArray(String key, CompoundNBT compound)
    {
        if (compound.contains(key))
        {
            ListNBT list = compound.getList(key, Constants.NBT.TAG_DOUBLE);
            double[] array = new double[list.size()];

            for (int i = 0; i < array.length; i++)
                array[i] = list.getDouble(i);

            return array;
        }

        return null;
    }

    public static void writeDoubleArray(String key, double[] array, CompoundNBT compound)
    {
        if (array != null)
        {
            ListNBT list = new ListNBT();

            for (double d : array) {
                list.add(DoubleNBT.valueOf(d));
            }

            compound.put(key, list);
        }
    }

    public static String[] readNBTStrings(String id, CompoundNBT compound)
    {
        if (compound.contains(id))
        {
            ListNBT nbtTagList = compound.getList(id, Constants.NBT.TAG_STRING);
            String[] strings = new String[nbtTagList.size()];

            for (int i = 0; i < strings.length; i++)
                strings[i] = nbtTagList.getString(i);

            return strings;
        }

        return null;
    }

    public static void writeNBTStrings(String id, String[] strings, CompoundNBT compound)
    {
        if (strings != null)
        {
            ListNBT nbtTagList = new ListNBT();

            for (String s : strings) {
                nbtTagList.add(StringNBT.valueOf(s));
            }

            compound.put(id, nbtTagList);
        }
    }

    public static ItemStack[] readNBTStacks(String id, CompoundNBT compound)
    {
        if (compound.contains(id))
        {
            ListNBT nbtTagList = compound.getList(id, Constants.NBT.TAG_COMPOUND);
            ItemStack[] itemStacks = new ItemStack[nbtTagList.size()];

            for (int i = 0; i < itemStacks.length; i++)
                itemStacks[i] = ItemStack.of(nbtTagList.getCompound(i));

            return itemStacks;
        }

        return null;
    }

    public static void writeNBTStacks(String id, ItemStack[] stacks, CompoundNBT compound)
    {
        if (stacks != null)
        {
            ListNBT nbtTagList = new ListNBT();

            for (ItemStack stack : stacks)
            {
                CompoundNBT tagCompound = new CompoundNBT();
                stack.setTag(tagCompound);
                nbtTagList.add(tagCompound);
            }

            compound.put(id, nbtTagList);
        }
    }

    public static Block[] readNBTBlocks(String id, CompoundNBT compound, MCRegistry registry)
    {
        if (compound.contains(id))
        {
            ListNBT nbtTagList = compound.getList(id, Constants.NBT.TAG_STRING);
            Block[] blocks = new Block[nbtTagList.size()];

            for (int i = 0; i < blocks.length; i++)
                blocks[i] = registry.blockFromID(new ResourceLocation(nbtTagList.getString(i)));

            return blocks;
        }

        return null;
    }

    public static void writeNBTBlocks(String id, Block[] blocks, CompoundNBT compound)
    {
        if (blocks != null)
        {
            ListNBT nbtTagList = new ListNBT();

            for (Block b : blocks) {
                nbtTagList.add(StringNBT.valueOf(ForgeRegistries.BLOCKS.getKey(b).toString()));
            }

            compound.put(id, nbtTagList);
        }
    }

    public static long[] readNBTLongs(String id, CompoundNBT compound)
    {
        if (compound.contains(id))
        {
            ByteBuf bytes = Unpooled.copiedBuffer(compound.getByteArray(id));
            long[] longs = new long[bytes.capacity() / 8];
            for (int i = 0; i < longs.length; i++) longs[i] = bytes.readLong();
            return longs;
        }

        return null;
    }

    public static void writeNBTLongs(String id, long[] longs, CompoundNBT compound)
    {
        if (longs != null)
        {
            ByteBuf bytes = Unpooled.buffer(longs.length * 8);
            for (long aLong : longs) bytes.writeLong(aLong);
            compound.putByteArray(id, bytes.array());
        }
    }

    public static EffectInstance[] readNBTPotions(String id, CompoundNBT compound)
    {
        if (compound.contains(id))
        {
            ListNBT nbtTagList = compound.getList(id, Constants.NBT.TAG_STRING);
            EffectInstance[] potions = new EffectInstance[nbtTagList.size()];

            for (int i = 0; i < potions.length; i++)
                potions[i] = EffectInstance.load(nbtTagList.getCompound(i));

            return potions;
        }

        return null;
    }

    public static void writeNBTPotions(String id, EffectInstance[] potions, CompoundNBT compound)
    {
        if (potions != null)
        {
            ListNBT nbtTagList = new ListNBT();

            for (EffectInstance p : potions)
                nbtTagList.add(p.save(new CompoundNBT()));

            compound.put(id, nbtTagList);
        }
    }

    public static int[] readIntArrayFixedSize(String id, int length, CompoundNBT compound)
    {
        int[] array = compound.getIntArray(id);
        return array.length != length ? new int[length] : array;
    }

    public static void writeCompressed(String idBase, int[] intArray, int maxValueInArray, CompoundNBT compound)
    {
        byte bitLength = IvBytePacker.getRequiredBitLength(maxValueInArray);
        byte[] bytes = IvBytePacker.packValues(intArray, bitLength);
        compound.putByteArray(idBase + "_bytes", bytes);
        compound.putByte(idBase + "_bitLength", bitLength);
        compound.putInt(idBase + "_length", intArray.length);
    }

    public static int[] readCompressed(String idBase, CompoundNBT compound)
    {
        byte[] bytes = compound.getByteArray(idBase + "_bytes");
        byte bitLength = compound.getByte(idBase + "_bitLength");
        int intArrayLength = compound.getInt(idBase + "_length");
        return IvBytePacker.unpackValues(bytes, bitLength, intArrayLength);
    }
}
