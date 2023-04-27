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

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

/**
 * Created by lukas on 21.06.16.
 */
public class NBTWalker
{
    public static boolean walk(INBT tag, Visitor<INBT> consumer)
    {
        if (tag instanceof CompoundNBT)
            return walk((CompoundNBT) tag, consumer);
        else if (tag instanceof ListNBT)
            return walk((ListNBT) tag, consumer);
        else
            return consumer.visit(tag);
    }

    public static boolean walk(CompoundNBT compound, Visitor<INBT> consumer)
    {
        return consumer.visit(compound) && compound.getAllKeys().stream().allMatch(key -> walk(compound.get(key), consumer));
    }

    public static boolean walk(ListNBT list, Visitor<INBT> consumer)
    {
        return consumer.visit(list) && list.stream().allMatch(nbt -> walk(nbt, consumer));
    }

    public static boolean walkCompounds(INBT tag, Visitor<CompoundNBT> consumer)
    {
        if (tag instanceof CompoundNBT)
            return walkCompounds((CompoundNBT) tag, consumer);
        else if (tag instanceof ListNBT)
            return walkCompounds((ListNBT) tag, consumer);
        else
            return true;
    }

    public static boolean walkCompounds(CompoundNBT compound, Visitor<CompoundNBT> consumer)
    {
        return consumer.visit(compound) && compound.getAllKeys().stream().allMatch(key -> walkCompounds(compound.get(key), consumer));
    }

    public static boolean walkCompounds(ListNBT list, Visitor<CompoundNBT> consumer)
    {
        return list.stream().allMatch(nbt -> walkCompounds(nbt, consumer));
    }
}
