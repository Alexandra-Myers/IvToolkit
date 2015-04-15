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

package ivorius.ivtoolkit.maze.components;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Objects;

/**
 * Created by lukas on 15.04.15.
 */
public class MazeRoomConnection extends Pair<MazeRoom, MazeRoom>
{
    private final MazeRoom left;
    private final MazeRoom right;

    public MazeRoomConnection(MazeRoom left, MazeRoom right)
    {
        boolean lower = lower(left, right);
        this.left = lower ? left : right;
        this.right = lower ? right : left;
    }

    private static boolean lower(MazeRoom left, MazeRoom right)
    {
        for (int i = 0; i < left.getDimensions(); i++)
            if (left.getCoordinate(i) != right.getCoordinate(i))
                return left.getCoordinate(i) < right.getCoordinate(i);

        return false; // Same
    }

    public MazeRoomConnection add(MazeRoom add)
    {
        return new MazeRoomConnection(left.add(add), right.add(add));
    }

    @Override
    public MazeRoom getLeft()
    {
        return left;
    }

    @Override
    public MazeRoom getRight()
    {
        return right;
    }

    public boolean has(MazeRoom room)
    {
        return Objects.equals(left, room) || Objects.equals(right, room);
    }

    @Override
    public MazeRoom setValue(MazeRoom value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString()
    {
        return String.format("%s <-> %s", getLeft(), getRight());
    }
}
