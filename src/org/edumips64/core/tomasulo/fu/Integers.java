package org.edumips64.core.tomasulo.fu;

import org.edumips64.core.tomasulo.TomasuloCPU;
import org.edumips64.utils.ConfigKey;

public class Integers extends FunctionUnit {

    public Integers(int id, TomasuloCPU cpu) {
        super(id, cpu);
    }

    @Override
    public int executionSteps() {
        return this.getCpu().getConfig().getInt(ConfigKey.FU_INT_CYCLES);
    }


    @Override
    public Type fuType() {
        return Type.Integer;
    }
}
