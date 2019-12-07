package org.edumips64.core.tomasulo.fu;

import org.edumips64.core.Memory;
import org.edumips64.core.tomasulo.TomasuloCPU;
import org.edumips64.utils.ConfigKey;

public class FuMemory extends FunctionUnit {
    Memory memory;

    public FuMemory(int id, TomasuloCPU cpu, Memory memory) {
        super(id, cpu);
        this.memory = memory;
    }

    @Override
    public int executionSteps() {
        return this.getCpu().getConfig().getInt(ConfigKey.FU_MEM_CYCLES);
    }

    @Override
    public Type fuType() {
        return Type.Memory;
    }
}
