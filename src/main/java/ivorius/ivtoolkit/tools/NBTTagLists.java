/*
 * Copyright 2015 Lukas Tenbrink
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

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by lukas on 30.03.15.
 */
public class NBTTagLists
{
    public static List<INBT> nbtBases(ListNBT nbt)
    {
        return IntStream.range(0, nbt.size()).mapToObj(nbt::get).collect(Collectors.toList());
    }

    public static void writeTo(CompoundNBT compound, String key, List<? extends INBT> lists)
    {
        compound.put(key, write(lists));
    }

    public static ListNBT write(List<? extends INBT> lists)
    {
        ListNBT list = new ListNBT();
        lists.forEach(list::add);
        return list;
    }

    public static List<CompoundNBT> compoundsFrom(CompoundNBT compound, String key)
    {
        return compounds(compound.getList(key, Constants.NBT.TAG_COMPOUND));
    }

    public static List<CompoundNBT> compounds(final ListNBT nbt)
    {
        return IntStream.range(0, nbt.size()).mapToObj(nbt::getCompound).collect(Collectors.toList());
    }

    @Deprecated
    public static void writeCompoundsTo(CompoundNBT compound, String key, List<CompoundNBT> list)
    {
        compound.put(key, writeCompounds(list));
    }

    @Deprecated
    public static ListNBT writeCompounds(List<CompoundNBT> list)
    {
        return write(list);
    }

    public static List<int[]> intArraysFrom(CompoundNBT compound, String key)
    {
        return intArrays(compound.getList(key, Constants.NBT.TAG_INT_ARRAY));
    }

    public static List<int[]> intArrays(final ListNBT nbt)
    {
        return IntStream.range(0, nbt.size()).mapToObj(nbt::getIntArray).collect(Collectors.toList());
    }

    public static void writeIntArraysTo(CompoundNBT compound, String key, List<int[]> list)
    {
        compound.put(key, writeIntArrays(list));
    }

    public static ListNBT writeIntArrays(List<int[]> list)
    {
        ListNBT tagList = new ListNBT();
        list.forEach(array -> tagList.add(new IntArrayNBT(array)));
        return tagList;
    }

    public static List<ListNBT> listsFrom(CompoundNBT compound, String key)
    {
        return lists(compound.getList(key, Constants.NBT.TAG_LIST));
    }

    public static List<ListNBT> lists(ListNBT nbt)
    {
        return (List) IntStream.range(0, nbt.size()).mapToObj(i -> nbt.get(i).getId() == Constants.NBT.TAG_LIST ? nbt.get(i) : new ListNBT()).collect(Collectors.toList());
    }
}
