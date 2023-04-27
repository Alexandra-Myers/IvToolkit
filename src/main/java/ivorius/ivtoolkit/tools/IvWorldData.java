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

package ivorius.ivtoolkit.tools;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import ivorius.ivtoolkit.blocks.BlockArea;
import ivorius.ivtoolkit.blocks.BlockPositions;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.ivtoolkit.transform.Mover;
import ivorius.ivtoolkit.world.MockWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by lukas on 24.05.14.
 */
public class IvWorldData
{
    public IvBlockCollection blockCollection;
    public List<CompoundNBT> tileEntities;
    public List<CompoundNBT> entities;

    public IvWorldData(IvBlockCollection blockCollection, List<CompoundNBT> tileEntities, List<CompoundNBT> entities)
    {
        this.blockCollection = blockCollection;
        this.tileEntities = tileEntities;
        this.entities = entities;
    }

    public IvWorldData(CompoundNBT compound, MCRegistry registry)
    {
        compound = compound.copy(); // Copy since ID fix tags are being removed when being applied
        final DataFixer fixer = DataFixesManager.getDataFixer();

        blockCollection = new IvBlockCollection(compound.getCompound("blockCollection"), registry);

        tileEntities = new ArrayList<>();
        tileEntities.addAll(NBTTagLists.compoundsFrom(compound, "tileEntities"));
        tileEntities.forEach(teCompound -> NBTStateInjector.recursivelyApply(teCompound, registry, false));
        tileEntities.replaceAll(teCompound -> update(fixer, TypeReferences.BLOCK_ENTITY, teCompound, 0));

        entities = new ArrayList<>();
        entities.addAll(NBTTagLists.compoundsFrom(compound, "entities"));
        entities.forEach(entityCompound -> NBTStateInjector.recursivelyApply(entityCompound, registry, false));
        tileEntities.replaceAll(teCompound -> update(fixer, TypeReferences.ENTITY, teCompound, 0));
    }

    public static CompoundNBT update(DataFixer p_210822_0_, DSL.TypeReference p_210822_1_, CompoundNBT p_210822_2_, int p_210822_3_) {
        return update(p_210822_0_, p_210822_1_, p_210822_2_, p_210822_3_, SharedConstants.getCurrentVersion().getWorldVersion());
    }

    public static CompoundNBT update(DataFixer p_210821_0_, DSL.TypeReference p_210821_1_, CompoundNBT p_210821_2_, int p_210821_3_, int p_210821_4_) {
        return (CompoundNBT)p_210821_0_.update(p_210821_1_, new Dynamic<>(NBTDynamicOps.INSTANCE, p_210821_2_), p_210821_3_, p_210821_4_).getValue();
    }

    public static IvWorldData capture(World world, BlockArea blockArea, boolean captureEntities)
    {
        return capture(MockWorld.of(world), blockArea, captureEntities);
    }

    public static IvWorldData capture(MockWorld world, BlockArea blockArea, boolean captureEntities)
    {
        BlockPos referenceCoord = blockArea.getLowerCorner();
        BlockPos invertedReference = BlockPositions.invert(referenceCoord);

        int[] size = blockArea.areaSize();
        IvBlockCollection blockCollection = new IvBlockCollection(size[0], size[1], size[2]);

        List<CompoundNBT> tileEntities = new ArrayList<>();
        for (BlockPos worldCoord : blockArea)
        {
            BlockPos dataCoord = worldCoord.subtract(blockArea.getLowerCorner());

            blockCollection.setBlockState(dataCoord, world.getBlockState(worldCoord));

            TileEntity tileEntity = world.getTileEntity(worldCoord);
            if (tileEntity != null)
            {
                Mover.moveTileEntity(tileEntity, invertedReference);

                CompoundNBT teCompound = NBTCompoundObjectsMC.write(tileEntity, true);
                NBTStateInjector.recursivelyInject(teCompound);
                tileEntities.add(teCompound);

                Mover.moveTileEntity(tileEntity, referenceCoord);
            }
        }

        List<CompoundNBT> entities = captureEntities
                ? world.getEntities(blockArea.asAxisAlignedBB(), saveableEntityPredicate()).stream()
                .map(entity ->
                {
                    Mover.moveEntity(entity, invertedReference);

                    CompoundNBT entityCompound = NBTCompoundObjectsMC.write(entity, true);
                    NBTStateInjector.recursivelyInject(entityCompound);

                    Mover.moveEntity(entity, referenceCoord);

                    return entityCompound;
                }).collect(Collectors.toList())
                : Collections.emptyList();

        return new IvWorldData(blockCollection, tileEntities, entities);
    }

    public static Predicate<Entity> saveableEntityPredicate()
    {
        return entity -> !(entity instanceof PlayerEntity);
    }

    public CompoundNBT createTagCompound(MCRegistry registry)
    {
        CompoundNBT compound = new CompoundNBT();

        compound.put("blockCollection", blockCollection.createTagCompound(registry));
        compound.put("tileEntities", NBTTagLists.write(tileEntities));
        compound.put("entities", NBTTagLists.write(entities));

        return compound;
    }
}