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

package ivorius.ivtoolkit.transform;

import ivorius.ivtoolkit.blocks.Directions;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.math.MinecraftTransforms;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.state.Property;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Created by lukas on 09.05.16.
 */
public class PosTransformer
{
    public static void transformTileEntityPos(TileEntity tileEntity, AxisAlignedTransform2D transform, int[] size)
    {
        if (tileEntity instanceof AreaTransformable)
            ((AreaTransformable) tileEntity).transform(transform.getRotation(), transform.isMirrorX(), size);
        else
        {
            Pair<Rotation, Mirror> mct = MinecraftTransforms.to(transform);
            tileEntity.mirror(mct.getRight());
            tileEntity.rotate(mct.getLeft());
            Mover.setTileEntityPos(tileEntity, transform.apply(tileEntity.getBlockPos(), size));
        }
    }

    public static void transformEntityPos(Entity entity, AxisAlignedTransform2D transform, int[] size)
    {
        if (entity instanceof AreaTransformable)
            ((AreaTransformable) entity).transform(transform.getRotation(), transform.isMirrorX(), size);
        else
        {
            BlockPos.Mutable newEntityPos = transform.applyOn(entity.blockPosition().mutable(), size);

            Pair<Rotation, Mirror> mct = MinecraftTransforms.to(transform);
            float yaw = entity.mirror(mct.getRight());
            yaw = yaw + (entity.yRot - entity.rotate(mct.getLeft()));

            entity.setPos(newEntityPos.getX(), newEntityPos.getY(), newEntityPos.getZ());
            entity.setYBodyRot(yaw);
        }
    }

    public static BlockState transformBlockState(BlockState state, AxisAlignedTransform2D transform)
    {
        return MinecraftTransforms.map(transform, (rotation, mirror) -> state.mirror(mirror).rotate(rotation));
    }

    @Deprecated
    public static void transformBlock(World world, BlockPos coord, AxisAlignedTransform2D transform)
    {
    }

    @Deprecated
    public static void transformBlock(AxisAlignedTransform2D transform, World world, BlockState state, BlockPos coord, Block block)
    {
        transformBlockDefault(transform, world, state, coord, block);
    }

    @Deprecated
    public static void transformBlockDefault(AxisAlignedTransform2D transform, World world, BlockState state, BlockPos coord, Block block)
    {
        BlockState newState = state;

        for (Property<?> property : state.getProperties())
        {
            if (property.getValueClass() == Direction.class && new HashSet<>(property.getAllValues().collect(Collectors.toList())).containsAll(Arrays.asList(Directions.HORIZONTAL)))
            {
                Direction value = (Direction) state.getValue(property);
                newState = newState.setValue((Property) property, transform.apply(value));
            }
        }

        if (newState != state && !world.isClientSide)
            world.setBlockAndUpdate(coord, newState);
    }
}
