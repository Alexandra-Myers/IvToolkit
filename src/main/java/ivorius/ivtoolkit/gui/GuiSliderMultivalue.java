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

package ivorius.ivtoolkit.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lukas on 28.05.14.
 */
public class GuiSliderMultivalue extends Button
{
    private float[] values;
    public int mousePressedInsideIndex = -1;
    private List<GuiControlListener<GuiSliderMultivalue>> listeners = new ArrayList<>();
    private float minValue = 0.0f;
    private float maxValue = 1.0f;

    public GuiSliderMultivalue(int x, int y, int width, int height, int values, ITextComponent displayString, IPressable handler)
    {
        super(x, y, width, height, displayString, handler);
        this.values = new float[values];
    }

    public GuiSliderMultivalue(int x, int y, int width, int height, int values, ITextComponent displayString, IPressable handler, ITooltip tooltip)
    {
        super(x, y, width, height, displayString, handler, tooltip);
        this.values = new float[values];
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            if (this.mousePressedInsideIndex >= 0)
            {
                values[mousePressedInsideIndex] = (float) (x - (this.x + 4)) / (float) (this.width - 8);
                values[mousePressedInsideIndex] = (values[mousePressedInsideIndex]) * (maxValue - minValue) + minValue;

                if (values[mousePressedInsideIndex] < minValue)
                {
                    values[mousePressedInsideIndex] = minValue;
                }

                if (values[mousePressedInsideIndex] > maxValue)
                {
                    values[mousePressedInsideIndex] = maxValue;
                }

                notifyOfChanges();
            }

            GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);

            for (float value : values)
            {
                float drawVal = (value - this.minValue) / (this.maxValue - this.minValue);
                GuiUtils.drawTexturedModalRect(matrixStack, this.x + (int) (drawVal * (float) (this.width - 8)), this.y, 0, 66, 4, height, 0);
                GuiUtils.drawTexturedModalRect(matrixStack, this.x + (int) (drawVal * (float) (this.width - 8)) + 4, this.y, 196, 66, 4, height, 0);
            }
        }
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double mouseDX, double mouseDY)
    {
        float value = (float) (x - (this.x + 4)) / (float) (this.width - 8);
        value = value * (maxValue - minValue) + minValue;

        float nearestDist = -1;
        for (int i = 0; i < values.length; i++)
        {
            float dist = Math.abs(values[i] - value);

            if (dist < nearestDist || nearestDist < 0)
            {
                mousePressedInsideIndex = i;
                nearestDist = dist;
            }
        }

        values[mousePressedInsideIndex] = value;

        if (values[mousePressedInsideIndex] < minValue)
        {
            values[mousePressedInsideIndex] = minValue;
        }

        if (values[mousePressedInsideIndex] > maxValue)
        {
            values[mousePressedInsideIndex] = maxValue;
        }

        notifyOfChanges();
    }

    private void notifyOfChanges()
    {
        for (GuiControlListener<GuiSliderMultivalue> listener : listeners)
        {
            listener.valueChanged(this);
        }
    }

    public float getValue(int index)
    {
        return this.values[index];
    }

    public void setValue(int index, float value)
    {
        this.values[index] = value;
    }

    public float getMinValue()
    {
        return minValue;
    }

    public void setMinValue(float minValue)
    {
        this.minValue = minValue;
    }

    public float getMaxValue()
    {
        return maxValue;
    }

    public void setMaxValue(float maxValue)
    {
        this.maxValue = maxValue;
    }

    public void addListener(GuiControlListener<GuiSliderMultivalue> listener)
    {
        listeners.add(listener);
    }

    public void removeListener(GuiControlListener<GuiSliderMultivalue> listener)
    {
        listeners.remove(listener);
    }

    public List<GuiControlListener<GuiSliderMultivalue>> listeners()
    {
        return Collections.unmodifiableList(listeners);
    }
}