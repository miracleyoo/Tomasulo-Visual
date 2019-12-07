package org.edumips64.core.tomasulo.fu;

import org.edumips64.core.tomasulo.TomasuloCPU;

public class FuNOP extends FunctionUnit {

    public FuNOP(int id, TomasuloCPU cpu) {
        super(id, cpu);
    }

    @Override
    public int executionSteps() {
        return 0;
    }


    @Override
    public Type fuType() {
        return Type.NOP;
    }
}
