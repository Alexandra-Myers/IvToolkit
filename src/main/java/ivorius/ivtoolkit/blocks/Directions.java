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

package ivorius.ivtoolkit.blocks;

import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.tools.IvGsonHelper;
import net.minecraft.util.Direction;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;

import static net.minecraft.util.Direction.*;

/**
 * Created by lukas on 03.03.15.
 */
public class Directions
{
    public static final Direction[] HORIZONTAL = new Direction[]{NORTH, EAST, SOUTH, WEST};
    public static final Direction[] X_AXIS = new Direction[]{EAST, WEST};
    public static final Direction[] Y_AXIS = new Direction[]{UP, DOWN};
    public static final Direction[] Z_AXIS = new Direction[]{SOUTH, NORTH};
    
    @Nullable
    public static Integer getHorizontalClockwiseRotations(Direction source, Direction dest, boolean mirrorX)
    {
        if (source == dest)
            return mirrorX && ArrayUtils.contains(Directions.X_AXIS, dest) ? 2 : 0;

        int arrayIndexSrc = ArrayUtils.indexOf(HORIZONTAL, source);
        int arrayIndexDst = ArrayUtils.indexOf(HORIZONTAL, dest);

        if (arrayIndexSrc >= 0 && arrayIndexDst >= 0)
        {
            int mirrorRotations = mirrorX && ArrayUtils.contains(Directions.X_AXIS, source) ? 2 : 0;
            return ((arrayIndexDst - arrayIndexSrc + mirrorRotations) + HORIZONTAL.length) % HORIZONTAL.length;
        }

        return null;
    }

    public static Direction rotate(Direction direction, AxisAlignedTransform2D transform)
    {
        if (direction == UP || direction == DOWN)
            return direction;

        int rotations = transform.getRotation();
        if (transform.isMirrorX() && ArrayUtils.contains(X_AXIS, direction))
            rotations += 2;
        return HORIZONTAL[(ArrayUtils.indexOf(HORIZONTAL, direction) + rotations) % HORIZONTAL.length];
    }

    public static Direction deserialize(String id)
    {
        Direction direction = IvGsonHelper.enumForNameIgnoreCase(id, values());
        return direction != null ? direction : NORTH;
    }

    public static Direction deserializeHorizontal(String id)
    {
        Direction direction = IvGsonHelper.enumForNameIgnoreCase(id, HORIZONTAL);
        return direction != null ? direction : NORTH;
    }

    public static String serialize(Direction direction)
    {
        return IvGsonHelper.serializedName(direction);
    }

    public static Direction getDirectionFromVRotation(int front)
    {
        switch (front)
        {
            default:
            case 0:
                return SOUTH;
            case 1:
                return EAST;
            case 2:
                return NORTH;
            case 3:
                return WEST;
        }
    }
}
