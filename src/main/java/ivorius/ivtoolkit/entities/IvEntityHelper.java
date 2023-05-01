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

package ivorius.ivtoolkit.entities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public class IvEntityHelper
{
    public static boolean addAsCurrentItem(PlayerEntity player, ItemStack stack)
    {
        return addAsCurrentItem(player.inventory, stack, player.level.isClientSide);
    }

    public static boolean addAsCurrentItem(PlayerInventory inventory, ItemStack stack, boolean isRemote)
    {
        int var6;

        if (inventory.getItem(inventory.selected) != ItemStack.EMPTY)
        {
            var6 = inventory.getFreeSlot();
        }
        else
        {
            var6 = inventory.selected;
        }

        if (var6 >= 0 && var6 < 9)
        {
            inventory.selected = var6;

            if (!isRemote)
            {
                inventory.setItem(inventory.selected, stack);
            }

            return true;
        }

        return false;
    }
}
