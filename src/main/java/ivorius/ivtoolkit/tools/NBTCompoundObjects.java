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

import com.google.common.collect.ImmutableMap;
import ivorius.ivtoolkit.IvToolkit;
import ivorius.ivtoolkit.lang.IvClasses;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by lukas on 30.03.15.
 */
public class NBTCompoundObjects
{
    @Deprecated
    public static <T extends NBTCompoundObject> Function<CompoundNBT, T> readFunction(final Class<T> tClass)
    {
        return input -> read(input, tClass);
    }

    @Deprecated
    public static Function<? extends NBTCompoundObject, CompoundNBT> writeFunction()
    {
        return (Function<NBTCompoundObject, CompoundNBT>) NBTCompoundObjects::write;
    }

    public static void writeTo(CompoundNBT compound, String key, NBTCompoundObject compoundObject)
    {
        compound.put(key, NBTCompoundObjects.write(compoundObject));
    }

    public static CompoundNBT write(NBTCompoundObject compoundObject)
    {
        CompoundNBT compound = new CompoundNBT();
        compoundObject.writeToNBT(compound);
        return compound;
    }

    @Deprecated
    public static <T extends NBTCompoundObject> T readFrom(CompoundNBT compound, String key, Class<? extends T> keyClass)
    {
        return readFrom(compound, key, classSupplier(keyClass));
    }

    public static <T extends NBTCompoundObject> T readFrom(CompoundNBT compound, String key, Supplier<? extends T> instantiator)
    {
        return NBTCompoundObjects.read(compound.getCompound(key), instantiator);
    }

    @Deprecated
    public static <T extends NBTCompoundObject> T read(CompoundNBT compound, Class<T> tClass)
    {
        return read(compound, classSupplier(tClass));
    }

    public static <T extends NBTCompoundObject> T read(CompoundNBT compound, Supplier<? extends T> instantiator)
    {
        T t = instantiator.get();
        if (t != null)
            t.readFromNBT(compound);
        return t;
    }

    public static void writeListTo(CompoundNBT compound, String key, Iterable<? extends NBTCompoundObject> objects)
    {
        compound.put(key, writeList(objects));
    }

    public static ListNBT writeList(Iterable<? extends NBTCompoundObject> objects)
    {
        ListNBT tagList = new ListNBT();
        for (NBTCompoundObject object : objects)
        {
            CompoundNBT compound = new CompoundNBT();
            object.writeToNBT(compound);
            tagList.add(compound);
        }
        return tagList;
    }

    public static <T extends NBTCompoundObject> List<T> readListFrom(CompoundNBT compound, String key, Supplier<? extends T> instantiator)
    {
        return readList(compound.getList(key, Constants.NBT.TAG_COMPOUND), instantiator);
    }

    @Deprecated
    public static <T extends NBTCompoundObject> List<T> readListFrom(CompoundNBT compound, String key, Class<T> tClass)
    {
        return readList(compound.getList(key, Constants.NBT.TAG_COMPOUND), tClass);
    }

    @Deprecated
    public static <T extends NBTCompoundObject> List<T> readList(ListNBT list, Class<T> tClass)
    {
        return readList(list, classSupplier(tClass));
    }

    public static <T extends NBTCompoundObject> List<T> readList(ListNBT list, Supplier<? extends T> instantiator)
    {
        List<T> rList = new ArrayList<>(list.size());

        for (int i = 0; i < list.size(); i++)
            rList.add(read(list.getCompound(i), instantiator));

        return rList;
    }

    @Deprecated
    public static <K extends NBTCompoundObject, V extends NBTCompoundObject> Map<K, V> readMapFrom(CompoundNBT compound, String key, Class<? extends K> keyClass, Class<? extends V> valueClass)
    {
        return readMapFrom(compound, key, classSupplier(keyClass), classSupplier(valueClass));
    }

    public static <K extends NBTCompoundObject, V extends NBTCompoundObject> Map<K, V> readMapFrom(CompoundNBT compound, String key, Supplier<? extends K> keySupplier, Supplier<? extends V> valueSupplier)
    {
        return readMap(compound.getList(key, Constants.NBT.TAG_COMPOUND), keySupplier, valueSupplier);
    }

    @Deprecated
    public static <K extends NBTCompoundObject, V extends NBTCompoundObject> Map<K, V> readMap(ListNBT nbt, Class<? extends K> keyClass, Class<? extends V> valueClass)
    {
        return readMap(nbt, classSupplier(keyClass), classSupplier(valueClass));
    }

    public static <K extends NBTCompoundObject, V extends NBTCompoundObject> Map<K, V> readMap(ListNBT nbt, Supplier<? extends K> keyInstantiator, Supplier<? extends V> valueInstantiator)
    {
        ImmutableMap.Builder<K, V> map = new ImmutableMap.Builder<>();
        for (int i = 0; i < nbt.size(); i++)
        {
            CompoundNBT compound = nbt.getCompound(i);
            map.put(readFrom(compound, "key", keyInstantiator), readFrom(compound, "value", valueInstantiator));
        }
        return map.build();
    }

    public static <K extends NBTCompoundObject, V extends NBTCompoundObject> void writeMapTo(CompoundNBT compound, String key, Map<K, V> map)
    {
        compound.put(key, writeMap(map));
    }

    public static <K extends NBTCompoundObject, V extends NBTCompoundObject> ListNBT writeMap(Map<K, V> map)
    {
        ListNBT nbt = new ListNBT();
        for (Map.Entry<K, V> entry : map.entrySet())
        {
            CompoundNBT compound = new CompoundNBT();
            writeTo(compound, "key", entry.getKey());
            writeTo(compound, "value", entry.getValue());
            nbt.add(compound);
        }
        return nbt;
    }

    private static <T extends NBTCompoundObject> Supplier<T> classSupplier(Class<T> tClass)
    {
        return () -> IvClasses.instantiate(tClass);
    }
}
