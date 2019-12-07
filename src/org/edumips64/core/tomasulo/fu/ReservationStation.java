package org.edumips64.core.tomasulo.fu;

public class ReservationStation {

    private Type rsType;

    private boolean busy;
    private String op;
    private String valueJ;
    private String valueK;
    private Integer qj;
    private Integer qk;
    private Integer imme;

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getValueJ() {
        return valueJ;
    }

    public void setValueJ(String valueJ) {
        this.valueJ = valueJ;
    }

    public String getValueK() {
        return valueK;
    }

    public void setValueK(String valueK) {
        this.valueK = valueK;
    }

    public Integer getQj() {
        return qj;
    }

    public void setQj(Integer qj) {
        this.qj = qj;
    }

    public Integer getQk() {
        return qk;
    }

    public void setQk(Integer qk) {
        this.qk = qk;
    }

    public Integer getImme() {
        return imme;
    }

    public void setImme(Integer imme) {
        this.imme = imme;
    }

    public Type getRsType() {
        return rsType;
    }

    public void reset() {
        this.busy = false;
        this.op = null;
        this.valueJ = null;
        this.valueK = null;
        this.qj = null;
        this.qk = null;
        this.imme = null;
    }
}
