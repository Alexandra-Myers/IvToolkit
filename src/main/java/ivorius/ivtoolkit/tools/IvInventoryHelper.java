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

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class IvInventoryHelper
{
    public static boolean consumeInventoryItem(PlayerInventory inventory, ItemStack itemStack)
    {
        int var2 = getInventorySlotContainItem(inventory, itemStack);

        if (var2 < 0)
        {
            return false;
        }
        else
        {
            inventory.items.get(var2).setCount(inventory.items.get(var2).getCount() - 1);

            return true;
        }
    }

    public static int getInventorySlotContainItem(PlayerInventory inventory, ItemStack itemStack)
    {
        for (int var2 = 0; var2 < inventory.items.size(); ++var2)
        {
            if (!inventory.items.get(var2).isEmpty() && inventory.items.get(var2).sameItem(itemStack))
            {
                return var2;
            }
        }

        return -1;
    }

    public static int getInventorySlotContainItem(PlayerInventory inventory, Item item)
    {
        for (int var2 = 0; var2 < inventory.items.size(); ++var2)
        {
            if (!inventory.items.get(var2).isEmpty() && inventory.items.get(var2).getItem() == item)
            {
                return var2;
            }
        }

        return -1;
    }
}
