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

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Created by lukas on 21.07.15.
 */
public class Mover
{
    public static void moveTileEntity(TileEntity tileEntity, BlockPos dist)
    {
        if (tileEntity instanceof Movable)
            ((Movable) tileEntity).move(dist);
        else
            moveTileEntityDefault(tileEntity, dist);
    }

    public static void moveTileEntityDefault(TileEntity tileEntity, BlockPos dist)
    {
        tileEntity.setPosition(tileEntity.getBlockPos().offset(dist));
    }

    public static void setTileEntityPos(TileEntity tileEntity, BlockPos coord)
    {
        moveTileEntity(tileEntity, coord.subtract(tileEntity.getBlockPos()));
    }

    public static void moveEntity(Entity entity, BlockPos dist)
    {
        if (entity instanceof Movable)
            ((Movable) entity).move(dist);
        else
            moveEntityDefault(entity, dist);
    }

    public static void moveEntityDefault(Entity entity, BlockPos dist)
    {
        if (entity instanceof HangingEntity)
        {
            HangingEntity entityHanging = (HangingEntity) entity;
            BlockPos hangingPosition = entityHanging.getPos().offset(dist);
            entityHanging.setPos(hangingPosition.getX(), hangingPosition.getY(), hangingPosition.getZ());
        }
        else
        {
            entity.setPos(entity.getX() + dist.getX(), entity.getY() + dist.getY(), entity.getZ() + dist.getZ());
        }
    }
}
