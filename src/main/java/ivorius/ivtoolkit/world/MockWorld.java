/*
 * Copyright 2017 Lukas Tenbrink
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

package ivorius.ivtoolkit.world;

import ivorius.ivtoolkit.blocks.IvTileEntityHelper;
import ivorius.ivtoolkit.tools.IvWorldData;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by lukas on 14.04.17.
 */
public interface MockWorld extends IBlockReader
{
    static Real of(World world)
    {
        return new Real(world);
    }

    World asWorld();

    boolean setBlockState(@Nonnull BlockPos pos, @Nonnull BlockState state, int flags);

    @Nonnull
    BlockState getBlockState(@Nonnull BlockPos pos);

    @Nullable
    TileEntity getTileEntity(@Nonnull BlockPos pos);

    void setTileEntity(@Nonnull BlockPos pos, @Nullable TileEntity tileEntity);

    List<Entity> getEntities(AxisAlignedBB bounds, @Nullable Predicate<? super Entity> predicate);

    boolean addEntity(Entity entity);

    boolean removeEntity(Entity entity);

    Random rand();

    default boolean setBlockState(BlockPos coord, BlockState block)
    {
        return setBlockState(coord, block, 3);
    }

    @OnlyIn(Dist.CLIENT)
    default int getCombinedLight(BlockPos pos, int lightValue)
    {
        return 0;
    }

    default boolean isAirBlock(BlockPos pos)
    {
        BlockState state = getBlockState(pos);
        return state.getBlock().isAir(state, this, pos);
    }

    @OnlyIn(Dist.CLIENT)
    Biome getBiome(BlockPos pos);


    default int getStrongPower(BlockPos pos, Direction direction)
    {
        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    DimensionType getWorldType();

    class Real implements MockWorld
    {
        public World world;

        public Real(World world)
        {
            this.world = world;
        }

        @Override
        public World asWorld()
        {
            return world;
        }

        @Override
        public boolean setBlockState(@Nonnull BlockPos pos, @Nonnull BlockState state, int flags)
        {
            return world.setBlock(pos, state, flags);
        }

        @Nullable
        @Override
        public TileEntity getBlockEntity(BlockPos p_175625_1_) {
            return world.getBlockEntity(p_175625_1_);
        }

        @Nonnull
        @Override
        public BlockState getBlockState(@Nonnull BlockPos pos)
        {
            return world.getBlockState(pos);
        }

        @Override
        public FluidState getFluidState(BlockPos pos)
        {
            return world.getFluidState(pos);
        }

        @Override
        public int getMaxLightLevel()
        {
            return world.getMaxLightLevel();
        }

        @Override
        public TileEntity getTileEntity(@Nonnull BlockPos pos)
        {
            return world.getBlockEntity(pos);
        }

        @Override
        public void setTileEntity(@Nonnull BlockPos pos, TileEntity tileEntity)
        {
            world.setBlockEntity(pos, tileEntity);
        }

        @Override
        public List<Entity> getEntities(AxisAlignedBB bounds, @Nullable Predicate<? super Entity> predicate)
        {
            return world.getEntities((Entity) null, bounds, predicate != null ? predicate::test : null);
        }

        @Override
        public boolean addEntity(Entity entity)
        {
            return world.addFreshEntity(entity);
        }

        @Override
        public boolean removeEntity(Entity entity)
        {
            entity.remove();
            return true;
        }

        @Override
        public Random rand()
        {
            return world.random;
        }

        @Override
        public int getCombinedLight(BlockPos pos, int lightValue)
        {
            return world.getMaxLocalRawBrightness(pos, lightValue);
        }

        @Override
        public Biome getBiome(BlockPos pos)
        {
            return world.getBiome(pos);
        }

        @Override
        public int getStrongPower(BlockPos pos, Direction direction)
        {
            return world.getDirectSignal(pos, direction);
        }

        @Override
        public DimensionType getWorldType()
        {
            return world.dimensionType();
        }
    }

    class Cache extends Real
    {
        public WorldCache cache;

        public Cache(WorldCache cache)
        {
            super(cache.world);
            this.cache = cache;
        }

        @Override
        public boolean setBlockState(@Nonnull BlockPos pos, @Nonnull BlockState state, int flags)
        {
            return cache.setBlockState(pos, state, flags);
        }

        @Nonnull
        @Override
        public BlockState getBlockState(@Nonnull BlockPos pos)
        {
            return cache.getBlockState(pos);
        }
    }

    class WorldData implements MockWorld
    {
        public IvWorldData worldData;
        public Random random = new Random();

        public WorldData(IvWorldData worldData)
        {
            this.worldData = worldData;
        }

        public static boolean isAt(@Nonnull BlockPos pos, CompoundNBT nbt)
        {
            return pos.getX() == nbt.getInt("x")
                    && pos.getY() == nbt.getInt("y")
                    && pos.getZ() == nbt.getInt("z");
        }

        @Override
        public World asWorld()
        {
            throw new VirtualWorldException();
        }

        @Override
        public boolean setBlockState(@Nonnull BlockPos pos, @Nonnull BlockState state, int flags)
        {
            worldData.blockCollection.setBlockState(pos, state);

            return true;
        }

        @Nullable
        @Override
        public TileEntity getBlockEntity(BlockPos p_175625_1_) {
            return null;
        }

        @Nonnull
        @Override
        public BlockState getBlockState(@Nonnull BlockPos pos)
        {
            return worldData.blockCollection.getBlockState(pos);
        }

        @Override
        public FluidState getFluidState(BlockPos pos)
        {
            return Fluids.EMPTY.defaultFluidState();
        }

        @Override
        public TileEntity getTileEntity(@Nonnull BlockPos pos)
        {
            for (CompoundNBT nbt : worldData.tileEntities) {
                if (isAt(pos, nbt))
                    return TileEntity.loadStatic(worldData.blockCollection.getBlockState(pos), nbt);
            }

            return null;
        }

        @Override
        public void setTileEntity(@Nonnull BlockPos pos, TileEntity tileEntity)
        {
            worldData.tileEntities.removeIf(nbt -> isAt(pos, nbt));
            worldData.tileEntities.add(tileEntity.save(new CompoundNBT()));
        }

        public List<Entity> getEntities(AxisAlignedBB bounds, @Nullable Predicate<? super Entity> predicate)
        {
            List<Optional<Entity>> entities = worldData.entities.stream()
                .filter(nbt ->
                {
                    ListNBT pos = nbt.getList("Pos", 6);
                    return bounds.contains(new Vector3d(pos.getDouble(0), pos.getDouble(1), pos.getDouble(2)));
                })
                .map(nbt -> EntityType.create(nbt, IvTileEntityHelper.getAnyWorld()))
                .filter((Predicate<? super Optional<Entity>>) predicate)
                .collect(Collectors.toList());
            List<Entity> result = new ArrayList<>();
            entities.forEach((optionalEntity) -> optionalEntity.ifPresent((entity) -> result.add(entity)));
            return result;
        }

        @Override
        public boolean addEntity(Entity entity)
        {
            // To make sure we don't have doubles
            removeEntity(entity);

            CompoundNBT compound = new CompoundNBT();
            if (!entity.save(compound)) {
                return false;
            }

            return worldData.entities.add(compound);

        }

        @Override
        public boolean removeEntity(Entity entity)
        {
            return worldData.entities.removeIf(nbt -> entity.getUUID().equals(nbt.getUUID("UUID")));
        }

        @Override
        public Random rand()
        {
            return random;
        }

        @Override
        public Biome getBiome(BlockPos pos) {
            return null;
        }

        @Override
        public DimensionType getWorldType() {
            return null;
        }
    }

    class VirtualWorldException extends RuntimeException
    {

    }
}
