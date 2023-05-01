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

import com.mojang.blaze3d.platform.GlStateManager;
import ivorius.ivtoolkit.blocks.BlockArea;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

/**
 * Created by lukas on 09.02.15.
 */
@OnlyIn(Dist.CLIENT)
public class AreaRenderer
{
    public static void renderAreaLined(BlockArea area, float sizeP)
    {
        renderArea(area, true, false, sizeP);
    }

    public static void renderArea(BlockArea area, boolean lined, boolean insides, float sizeP)
    {
        drawCuboid(area.getLowerCorner(), area.getHigherCorner().offset(1, 1, 1), lined, insides, sizeP);
    }

    @OnlyIn(Dist.CLIENT)
    private static void drawCuboid(BlockPos min, BlockPos max, boolean lined, boolean insides, float sizeP)
    {
        float width2 = ((float) max.getX() - (float) min.getX()) * 0.5f;
        float height2 = ((float) max.getY() - (float) min.getY()) * 0.5f;
        float length2 = ((float) max.getZ() - (float) min.getZ()) * 0.5f;

        double centerX = min.getX() + width2;
        double centerY = min.getY() + height2;
        double centerZ = min.getZ() + length2;

        int sizeCE = insides ? -1 : 1;

        GlStateManager._pushMatrix();
        GlStateManager._translated(centerX, centerY, centerZ);
        if (lined)
        {
            GlStateManager._disableTexture();
            drawLineCuboid(Tessellator.getInstance().getBuilder(), width2 + sizeP, height2 + sizeP, length2 + sizeP, 1);
            GlStateManager._enableTexture();
        }
        else
            drawCuboid(Tessellator.getInstance().getBuilder(), width2 * sizeCE + sizeP, height2 * sizeCE + sizeP, length2 * sizeCE + sizeP, 1);
        GlStateManager._popMatrix();
    }

    public static void drawCuboid(BufferBuilder renderer, float sizeX, float sizeY, float sizeZ, float in)
    {
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        renderer.normal(-sizeX * in, -sizeY * in, -sizeZ).uv(0, 0).endVertex();
        renderer.normal(-sizeX * in, sizeY * in, -sizeZ).uv(0, 1).endVertex();
        renderer.normal(sizeX * in, sizeY * in, -sizeZ).uv(1, 1).endVertex();
        renderer.normal(sizeX * in, -sizeY * in, -sizeZ).uv(1, 0).endVertex();

        renderer.normal(-sizeX * in, -sizeY * in, sizeZ).uv(0, 0).endVertex();
        renderer.normal(sizeX * in, -sizeY * in, sizeZ).uv(1, 0).endVertex();
        renderer.normal(sizeX * in, sizeY * in, sizeZ).uv(1, 1).endVertex();
        renderer.normal(-sizeX * in, sizeY * in, sizeZ).uv(0, 1).endVertex();

        renderer.normal(-sizeX, -sizeY * in, -sizeZ * in).uv(0, 0).endVertex();
        renderer.normal(-sizeX, -sizeY * in, sizeZ * in).uv(0, 1).endVertex();
        renderer.normal(-sizeX, sizeY * in, sizeZ * in).uv(1, 1).endVertex();
        renderer.normal(-sizeX, sizeY * in, -sizeZ * in).uv(1, 0).endVertex();

        renderer.normal(sizeX, -sizeY * in, -sizeZ * in).uv(0, 0).endVertex();
        renderer.normal(sizeX, sizeY * in, -sizeZ * in).uv(0, 1).endVertex();
        renderer.normal(sizeX, sizeY * in, sizeZ * in).uv(1, 1).endVertex();
        renderer.normal(sizeX, -sizeY * in, sizeZ * in).uv(1, 0).endVertex();

        renderer.normal(-sizeX * in, sizeY, -sizeZ * in).uv(0, 0).endVertex();
        renderer.normal(-sizeX * in, sizeY, sizeZ * in).uv(0, 1).endVertex();
        renderer.normal(sizeX * in, sizeY, sizeZ * in).uv(1, 1).endVertex();
        renderer.normal(sizeX * in, sizeY, -sizeZ * in).uv(1, 0).endVertex();

        renderer.normal(-sizeX * in, -sizeY, -sizeZ * in).uv(0, 0).endVertex();
        renderer.normal(sizeX * in, -sizeY, -sizeZ * in).uv(1, 0).endVertex();
        renderer.normal(sizeX * in, -sizeY, sizeZ * in).uv(1, 1).endVertex();
        renderer.normal(-sizeX * in, -sizeY, sizeZ * in).uv(0, 1).endVertex();

        Tessellator.getInstance().end();
    }

    public static void drawLineCuboid(BufferBuilder renderer, float sizeX, float sizeY, float sizeZ, float in)
    {
        renderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);

        renderer.normal(-sizeX * in, -sizeY * in, -sizeZ).endVertex();
        renderer.normal(-sizeX * in, sizeY * in, -sizeZ).endVertex();
        renderer.normal(sizeX * in, sizeY * in, -sizeZ).endVertex();
        renderer.normal(sizeX * in, -sizeY * in, -sizeZ).endVertex();
        renderer.normal(-sizeX * in, -sizeY * in, -sizeZ).endVertex();

        renderer.normal(-sizeX * in, -sizeY * in, sizeZ).endVertex();
        renderer.normal(-sizeX * in, sizeY * in, sizeZ).endVertex();
        renderer.normal(sizeX * in, sizeY * in, sizeZ).endVertex();
        renderer.normal(sizeX * in, -sizeY * in, sizeZ).endVertex();
        renderer.normal(-sizeX * in, -sizeY * in, sizeZ).endVertex();

        Tessellator.getInstance().end();

        renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

        renderer.normal(-sizeX * in, sizeY * in, -sizeZ).endVertex();
        renderer.normal(-sizeX * in, sizeY * in, sizeZ).endVertex();

        renderer.normal(sizeX * in, sizeY * in, -sizeZ).endVertex();
        renderer.normal(sizeX * in, sizeY * in, sizeZ).endVertex();

        renderer.normal(sizeX * in, -sizeY * in, -sizeZ).endVertex();
        renderer.normal(sizeX * in, -sizeY * in, sizeZ).endVertex();

        Tessellator.getInstance().end();
    }
}
