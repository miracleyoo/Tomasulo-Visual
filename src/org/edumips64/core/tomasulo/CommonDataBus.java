package org.edumips64.core.tomasulo;

public class CommonDataBus {
    private boolean busy;
    private int fuSorce;
    private String value;

    public boolean set(int source, String value) {
        if (busy) {
            return false;
        }
        this.fuSorce = source;
        this.value = value;
        this.busy = true;
        return true;
    }

    public String get(int source) {
        if (fuSorce != source) {
            return null;
        } else if (busy) {
            busy = false;
            return value;
        } else {
            return null;
        }
    }
}
