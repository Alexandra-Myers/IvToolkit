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

package ivorius.ivtoolkit.maze.classic;

import com.google.common.base.Function;
import net.minecraft.nbt.CompoundNBT;

/**
 * Created by lukas on 13.04.15.
 */
public class MazePaths
{
    public static Function<MazePath, CompoundNBT> toNBT()
    {
        return input -> input.storeInNBT();
    }

    public static Function<CompoundNBT, MazePath> fromNBT()
    {
        return input -> new MazePath(input);
    }
}
