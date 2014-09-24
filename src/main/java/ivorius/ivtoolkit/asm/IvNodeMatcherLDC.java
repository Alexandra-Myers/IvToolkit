/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
 */
package ivorius.ivtoolkit.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

/**
 * Created by lukas on 26.04.14.
 */
public class IvNodeMatcherLDC implements IvSingleNodeMatcher
{
    public String cst;

    public IvNodeMatcherLDC(String cst)
    {
        this.cst = cst;
    }

    @Override
    public boolean matchNode(AbstractInsnNode node)
    {
        if (node.getOpcode() != Opcodes.LDC)
        {
            return false;
        }

        LdcInsnNode ldcInsnNode = (LdcInsnNode) node;

        if (cst != null && !cst.equals(ldcInsnNode.cst))
        {
            return false;
        }

        return true;
    }
}
