/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
 */

package ivorius.ivtoolkit.asm;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

/**
 * Created by lukas on 26.04.14.
 */
public interface IvMultiNodeMatcher
{
    public boolean matchFromNodeInList(InsnList nodes, AbstractInsnNode node);
}
