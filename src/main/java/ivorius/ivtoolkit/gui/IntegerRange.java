/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
 */
package ivorius.ivtoolkit.gui;

import net.minecraft.util.MathHelper;

/**
 * Created by lukas on 13.06.14.
 */
public class IntegerRange
{
    public final int min;
    public final int max;

    public IntegerRange(int min, int max)
    {
        this.min = min;
        this.max = max;
    }

    public IntegerRange(FloatRange floatRange)
    {
        min = MathHelper.floor_float(floatRange.getMin());
        max = MathHelper.floor_float(floatRange.getMax());
    }

    public int getMin()
    {
        return min;
    }

    public int getMax()
    {
        return max;
    }

    @Override
    public String toString()
    {
        return "IntegerRange{" +
                "min=" + min +
                ", max=" + max +
                '}';
    }
}
