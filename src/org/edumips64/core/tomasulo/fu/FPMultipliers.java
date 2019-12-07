package org.edumips64.core.tomasulo.fu;

import org.edumips64.core.tomasulo.TomasuloCPU;
import org.edumips64.utils.ConfigKey;

public class FPMultipliers extends FunctionUnit {
    public FPMultipliers(int id, TomasuloCPU cpu) {
        super(id, cpu);
    }

    @Override
    public int executionSteps() {
        return this.getCpu().getConfig().getInt(ConfigKey.FU_FPMULT_CYCLES);
    }

    @Override
    public Type fuType() {
        return Type.FPMultiplier;
    }
}
