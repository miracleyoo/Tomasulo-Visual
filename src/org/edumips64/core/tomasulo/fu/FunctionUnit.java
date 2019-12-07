package org.edumips64.core.tomasulo.fu;

import org.edumips64.core.IrregularStringOfBitsException;
import org.edumips64.core.IrregularWriteOperationException;
import org.edumips64.core.fpu.FPInvalidOperationException;
import org.edumips64.core.is.*;
import org.edumips64.core.tomasulo.CommonDataBus;
import org.edumips64.core.tomasulo.TomasuloCPU;

public abstract class FunctionUnit {
    private int id;
    private ReservationStation reservationStation;
    private Status status;
    private CommonDataBus cdb;
    private InstructionInterface instruction;
    private TomasuloCPU cpu;

    public FunctionUnit(int id, TomasuloCPU cpu) {
        this.id = id;
        this.reservationStation = new ReservationStation();
        this.status = Status.Idle;
        this.cdb =  cpu.getCdb();
        this.cpu = cpu;
        this.instruction = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ReservationStation getReservationStation() {
        return reservationStation;
    }

    public void setReservationStation(ReservationStation reservationStation) {
        this.reservationStation = reservationStation;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public CommonDataBus getCdb() {
        return cdb;
    }

    public InstructionInterface getInstruction() {
        return this.instruction;
    }

    public boolean issue(InstructionInterface instruction, int cycle) throws WAWException, IrregularWriteOperationException, StoppingException, BreakException, FPInvalidOperationException, TwosComplementSumException, JumpException, IrregularStringOfBitsException {
        assert this.getStatus() == Status.Idle;
        Integer op1 = instruction.op1();
        Integer op2 = instruction.op2();
        Integer dest = instruction.dest();
        Integer imme = instruction.imme();

        if (dest != null) {
            if (this.cpu.registerStatuses[dest].functionUnit != null) {
                return false;
            } else {
                this.cpu.registerStatuses[dest].functionUnit = this.id;
            }
        }

        if (op1 != null) {
            var fu = this.cpu.registerStatuses[op1].functionUnit;
            if (fu == null) {
                this.reservationStation.setValueJ(this.get_register_data(op1));
            } else {
                this.reservationStation.setQj(fu);
            }
        }

        if (op2 != null) {
            var fu = this.cpu.registerStatuses[op2].functionUnit;
            if (fu == null) {
                this.reservationStation.setValueK(this.get_register_data(op2));
            } else {
                this.reservationStation.setQk(fu);
            }
        }

        if (imme != null) {
            this.reservationStation.setImme(imme);
        }

        instruction.setIssueCycle(cycle);
        instruction.setFunctionUnit(this.id);
        instruction.setReservationStation(this.reservationStation);
        instruction.setCountDown(this.executionSteps());
        this.status = Status.Issued;

        return true;
    }

    private String get_register_data(int id) {
        int intRegs = this.cpu.IntegerRegisters();
        if (id > intRegs) {
            return this.cpu.getRegisterFP(id - intRegs).getBinString();
        } else {
            return this.cpu.getRegister(id).getBinString();
        }
    }

    public abstract int executionSteps();

    public abstract Type fuType();

    public TomasuloCPU getCpu() {
        return cpu;
    }
}
