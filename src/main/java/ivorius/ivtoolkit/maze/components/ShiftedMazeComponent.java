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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Created by lukas on 15.04.15.
 */
public class ShiftedMazeComponent<M extends MazeComponent<C>, C> implements MazeComponent<C>
{
    private final M component;
    private final MazeRoom shift;

    private final ImmutableSet<MazeRoom> rooms;
    private final ImmutableMap<MazeRoomConnection, C> exits;
    private final Set<Pair<MazeRoomConnection, MazeRoomConnection>> reachability;

    @Deprecated
    public ShiftedMazeComponent(M component, MazeRoom shift, ImmutableSet<MazeRoom> rooms, ImmutableMap<MazeRoomConnection, C> exits)
    {
        this.component = component;
        this.shift = shift;
        this.rooms = rooms;
        this.exits = exits;

        ImmutableSet.Builder<Pair<MazeRoomConnection, MazeRoomConnection>> builder = ImmutableSet.builder();
        for (MazeRoomConnection left : exits.keySet())
            for (MazeRoomConnection right : exits.keySet())
                builder.add(Pair.of(left, right));
        this.reachability = builder.build();
    }

    public ShiftedMazeComponent(M component, MazeRoom shift, ImmutableSet<MazeRoom> rooms, ImmutableMap<MazeRoomConnection, C> exits, Set<Pair<MazeRoomConnection, MazeRoomConnection>> reachability)
    {
        this.component = component;
        this.shift = shift;
        this.rooms = rooms;
        this.exits = exits;
        this.reachability = reachability;
    }

    public M getComponent()
    {
        return component;
    }

    public MazeRoom getShift()
    {
        return shift;
    }

    @Override
    public Set<MazeRoom> rooms()
    {
        return rooms;
    }

    @Override
    public Map<MazeRoomConnection, C> exits()
    {
        return exits;
    }

    @Override
    public Set<Pair<MazeRoomConnection, MazeRoomConnection>> reachability()
    {
        return reachability;
    }
}
