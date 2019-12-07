package org.edumips64.core.tomasulo;

public class CommonDataBus {
    private boolean busy;
    private int fuSorce;
    private int reg;
    private String value;

    public boolean set(int source, String value, int reg) {
        if (busy) {
            return false;
        }
        this.fuSorce = source;
        this.value = value;
        this.busy = true;
        this.reg = reg;
        return true;
    }

    public String get(int source) {
        if (fuSorce != source) {
            return null;
        } else if (busy) {
            busy = false;
            return this.value;
        } else {
            return null;
        }
    }

    public boolean isBusy() {
        return busy;
    }

    public int getFuSorce() {
        return fuSorce;
    }

    public int getReg() {
        return reg;
    }

    public String getValue() {
        return value;
    }

    public void reset() {
        this.busy = false;
        this.value = null;
    }
}
