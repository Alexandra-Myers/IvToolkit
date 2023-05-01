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

import net.minecraft.util.Direction ;
import net.minecraft.util.math.BlockPos;

/**
 * Created by lukas on 22.09.16.
 */
public class IvMutableBlockPos
{
    public static BlockPos.Mutable add(BlockPos.Mutable pos, BlockPos add)
    {
        return pos.set(pos.getX() + add.getX(), pos.getY() + add.getY(), pos.getZ() + add.getZ());
    }

    public static BlockPos.Mutable offset(BlockPos pos, BlockPos.Mutable dest, Direction  facing)
    {
        return offset(pos, dest, facing, 1);
    }

    public static BlockPos.Mutable offset(BlockPos pos, BlockPos.Mutable dest, Direction  facing, int amount)
    {
        return dest.set(pos.getX() + facing.getStepX() * amount, pos.getY() + facing.getStepY() * amount, pos.getZ() + facing.getStepZ() * amount);
    }
}
