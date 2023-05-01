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

package ivorius.ivtoolkit.blocks;


import ivorius.ivtoolkit.tools.EnumFacingHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class IvTileEntityRotatable extends TileEntity
{
    public Direction facing;

    public IvTileEntityRotatable(TileEntityType<?> tileEntityTypeIn)
    {
        super(tileEntityTypeIn);
    }

    @Override
    public void load(BlockState state, CompoundNBT tagCompound)
    {
        super.load(state, tagCompound);

        if (tagCompound.contains("direction")) // Legacy
        {
            switch (tagCompound.getInt("direction")) {
                case 0:
                    facing = Direction.SOUTH;
                case 1:
                    facing = Direction.WEST;
                case 2:
                    facing = Direction.NORTH;
                case 3:
                    facing = Direction.EAST;
                default:
                    facing = Direction.SOUTH;
            }
        }
        else
            facing = EnumFacingHelper.byName(tagCompound.getString("facing"), Direction.SOUTH);
    }

    @Override
    public CompoundNBT save(CompoundNBT tagCompound)
    {
        super.save(tagCompound);

        tagCompound.putString("facing", facing.getName());

        return tagCompound;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        return IvTileEntityHelper.getStandardDescriptionPacket(this);
    }

    public AxisAlignedBB getRotatedBB(double x, double y, double z, double width, double height, double depth)
    {
        return getRotatedBB(x, y, z, width, height, depth, getFacing(), new double[]{worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5});
    }

    public Vector3f getRotatedVector(Vector3f vector3f)
    {
        return getRotatedVector(vector3f, getFacing());
    }

    public Vector3d getRotatedVector(Vector3d vec3)
    {
        return getRotatedVector(vec3, getFacing());
    }

    public Direction getFacing()
    {
        return facing;
    }

    public static List<BlockPos> getRotatedPositions(List<BlockPos> positions, Direction facing)
    {
        ArrayList<BlockPos> returnList = new ArrayList<>(positions.size());

        for (BlockPos position : positions) {
            switch (facing) {
                case NORTH:
                case SOUTH:
                    returnList.add(position);
                    break;
                case WEST:
                case EAST:
                    returnList.add(new BlockPos(position.getZ(), position.getY(), position.getX()));
                    break;
            }
        }

        return returnList;
    }

    public static AxisAlignedBB getRotatedBB(double x, double y, double z, double width, double height, double depth, Direction direction, double[] centerCoords)
    {
        AxisAlignedBB box = null;

        switch (direction) {
            case SOUTH:
                box = getBBWithLengths(centerCoords[0] + x, centerCoords[1] + y, centerCoords[2] + z, width, height, depth);
                break;
            case WEST:
                box = getBBWithLengths(centerCoords[0] - z - depth, centerCoords[1] + y, centerCoords[2] + x, depth, height, width);
                break;
            case NORTH:
                box = getBBWithLengths(centerCoords[0] - x - width, centerCoords[1] + y, centerCoords[2] - z - depth, width, height, depth);
                break;
            case EAST:
                box = getBBWithLengths(centerCoords[0] + z, centerCoords[1] + y, centerCoords[2] - x - width, depth, height, width);
                break;
        }

        return box;
    }

    public static Vector3f getRotatedVector(Vector3f vector3f, Direction facing)
    {
        switch (facing) {
            case SOUTH:
                return new Vector3f(vector3f.x(), vector3f.y(), vector3f.z());
            case WEST:
                return new Vector3f(-vector3f.z(), vector3f.y(), vector3f.x());
            case NORTH:
                return new Vector3f(-vector3f.x(), vector3f.y(), -vector3f.z());
            case EAST:
                return new Vector3f(vector3f.z(), vector3f.y(), -vector3f.x());
        }

        return null;
    }

    public static Vector3d getRotatedVector(Vector3d vec3, Direction facing)
    {
        switch (facing) {
            case SOUTH:
                return new Vector3d(vec3.x, vec3.y, vec3.z);
            case WEST:
                return new Vector3d(-vec3.z, vec3.y, vec3.x);
            case NORTH:
                return new Vector3d(-vec3.x, vec3.y, -vec3.z);
            case EAST:
                return new Vector3d(vec3.z, vec3.y, -vec3.x);
        }

        return null;
    }

    public static AxisAlignedBB getBBWithLengths(double x, double y, double z, double width, double height, double depth)
    {
        return new AxisAlignedBB(x, y, z, x + width, y + height, z + depth);
    }

    public static Direction getRotation(Entity entity)
    {
        return entity.getDirection();
    }

    public static List<BlockPos> getRotatedPositions(Direction facing, int width, int height, int length)
    {
        boolean affectsX = (facing == Direction.SOUTH) || (facing == Direction.NORTH);
        return getPositions(affectsX ? width : length, height, affectsX ? length : width);
    }

    public static List<BlockPos> getPositions(int width, int height, int length)
    {
        ArrayList<BlockPos> positions = new ArrayList<>();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    positions.add(new BlockPos(x, y, z));
                }
            }
        }

        return positions;
    }
}
