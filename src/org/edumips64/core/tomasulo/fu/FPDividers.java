package org.edumips64.core.tomasulo.fu;

import org.edumips64.core.tomasulo.TomasuloCPU;

public class FPDividers extends FunctionUnit {

    public FPDividers(int id, TomasuloCPU cpu) {
        super(id, cpu);
    }

    @Override
    public int steps_remain() {
        return 0;
    }

    @Override
    public Type fuType() {
        return Type.FPDivider;
    }


}
