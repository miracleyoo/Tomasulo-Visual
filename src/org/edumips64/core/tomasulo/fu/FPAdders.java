package org.edumips64.core.tomasulo.fu;

import org.edumips64.core.tomasulo.TomasuloCPU;
import org.edumips64.utils.ConfigKey;

public class FPAdders extends FunctionUnit {

    public FPAdders(int id, TomasuloCPU cpu) {
        super(id, cpu);
    }

    @Override
    public int executionSteps() {
        return this.getCpu().getConfig().getInt(ConfigKey.FU_FPADDER_CYCLES);
    }

    @Override
    public Type fuType() {
        return Type.FPAdder;
    }
}
