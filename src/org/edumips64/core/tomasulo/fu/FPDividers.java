package org.edumips64.core.tomasulo.fu;

import org.edumips64.core.tomasulo.TomasuloCPU;
import org.edumips64.utils.ConfigKey;

public class FPDividers extends FunctionUnit {

    public FPDividers(int id, TomasuloCPU cpu) {
        super(id, cpu);
    }

    @Override
    public int executionSteps() {
        return this.getCpu().getConfig().getInt(ConfigKey.FU_FPDIVIDER_CYCLES);
    }

    @Override
    public Type fuType() {
        return Type.FPDivider;
    }


}
