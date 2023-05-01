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

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public class NBTCompoundObjectCapabilityStorage<T extends NBTCompoundObject> implements Capability.IStorage<T>
{
    public Class<T> tClass;

    public NBTCompoundObjectCapabilityStorage(Class<T> tClass)
    {
        this.tClass = tClass;
    }

    @Override
    public INBT writeNBT(Capability<T> capability, T instance, Direction side)
    {
        return NBTCompoundObjects.write(instance);
    }

    @Override
    public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt)
    {
        instance.readFromNBT(nbt instanceof CompoundNBT ? (CompoundNBT) nbt : new CompoundNBT());
    }
}
