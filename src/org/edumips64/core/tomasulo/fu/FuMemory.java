package org.edumips64.core.tomasulo.fu;

import org.edumips64.core.Memory;
import org.edumips64.core.tomasulo.TomasuloCPU;

public class FuMemory extends FunctionUnit {
    Memory memory;

    public FuMemory(int id, TomasuloCPU cpu, Memory memory) {
        super(id, cpu);
        this.memory = memory;
    }

    @Override
    void step_fu() {

    }

    @Override
    public int steps_remain() {
        return 0;
    }

    @Override
    long get_fu_result() {
        return 0;
    }

    @Override
    public Type fuType() {
        return Type.Memory;
    }
}
