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

package ivorius.ivtoolkit.rendering.grid;

import ivorius.ivtoolkit.rendering.IvRenderHelper;
import ivorius.ivtoolkit.tools.BufferBuilderAccessor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import org.lwjgl.opengl.GL11;

/**
 * Created by lukas on 21.02.15.
 */
public class GridRenderer
{
    public static void renderGrid(int lines, float spacing, float lineLength, float lineWidth)
    {
        Tessellator.getInstance().getBuilder().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

        for (int x = -lines; x <= lines; x++)
            for (int z = -lines; z <= lines; z++)
                renderLine(x * spacing, -lineLength * 0.5f, z * spacing, Direction.UP, lineLength, lineWidth);

        for (int x = -lines; x <= lines; x++)
            for (int y = -lines; y <= lines; y++)
                renderLine(x * spacing, y * spacing, -lineLength * 0.5f, Direction.SOUTH, lineLength, lineWidth);

        for (int y = -lines; y <= lines; y++)
            for (int z = -lines; z <= lines; z++)
                renderLine(-lineLength * 0.5f, y * spacing, z * spacing, Direction.EAST, lineLength, lineWidth);

        Tessellator.getInstance().end();
    }

    public static void renderLine(float x, float y, float z, Direction direction, float length, float size)
    {
        float xDir = direction.getStepX() * length;
        float yDir = direction.getStepY() * length;
        float zDir = direction.getStepZ() * length;

        if (xDir == 0)
            xDir = size;
        else
            x += xDir * 0.5;

        if (yDir == 0)
            yDir = size;
        else
            y += yDir * 0.5;

        if (zDir == 0)
            zDir = size;
        else
            z += zDir * 0.5;

        BufferBuilder renderer = Tessellator.getInstance().getBuilder();

        BufferBuilderAccessor.addTranslation(renderer, x, y, z);
        IvRenderHelper.renderCuboid(renderer, xDir, yDir, zDir, 1f);
        BufferBuilderAccessor.addTranslation(renderer, -x, -y, -z);
    }
}
