/* CPU.java
 *
 * This class models a MIPS CPU with 32 64-bit General Purpose Register.
 * (c) 2006 Andrea Spadaccini, Simona Ullo, Antonella Scandura, Massimo Trubia (FPU modifications)
 *
 * This file is part of the EduMIPS64 project, and is released under the GNU
 * General Public License.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.edumips64.core.tomasulo;

import org.edumips64.core.*;
import org.edumips64.core.Memory;
import org.edumips64.core.fpu.*;
import org.edumips64.core.is.*;
import org.edumips64.core.tomasulo.fu.*;
import org.edumips64.core.tomasulo.fu.FuNOP;
import org.edumips64.utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/** This class models a MIPS CPU with 32 64-bit General Purpose Registers.
 *  @author Andrea Spadaccini, Simona Ullo, Antonella Scandura, Massimo Trubia (FPU modifications)
 */
public class TomasuloCPU {
    private Memory mem;
    private Register[] gpr;
    private static final Logger logger = Logger.getLogger(TomasuloCPU.class.getName());

    /** FPU Elements*/
    private RegisterFP[] fpr;
    private FCSRRegister FCSR;

    /** Program Counter*/
    private Register pc, old_pc;
    private Register LO, HI;

    private List<FunctionUnit> fus;
    private CommonDataBus cdb;

    /** CPU status.
     *  READY - the CPU has been initialized but the symbol table hasn't been
     *  already filled by the Parser. This means that you can't call the step()
     *  method, or you'll get a StoppedCPUException.
     *
     *  RUNNING - the CPU is executing a program, you can call the step()
     *  method, and the CPU will fetch additional instructions from the symbol
     *  table
     *
     *  STOPPING - the HALT instruction has entered in the pipeline. This means
     *  that no additional instructions must be fetched but the instructions
     *  that are already in the pipeline must be executed. THe step() method can
     *  be called, but won't fetch any other instruction
     *
     *  HALTED - the HALT instruction has passed the WB stage, and the step()
     *  method can't be executed.
     * */
    public enum CPUStatus {READY, RUNNING, STOPPING, HALTED}
    private CPUStatus status;

    /** Simulator configuration */
    private ConfigStore config;

    /** Statistics */
    private int cycles, instructions, RAWStalls, WAWStalls, dividerStalls, funcUnitStalls, memoryStalls, exStalls;

    /** BUBBLE */
    private InstructionInterface bubble;

    public RegisterStatus[] registerStatuses = new RegisterStatus[64];

    /** Terminating instructions */
    private static ArrayList<String> terminating = new ArrayList<>(
            Arrays.asList("0000000C",     // SYSCALL 0
                    "04000000"));   // HALT

    private Consumer<String> cpuStatusChangeCallback;

    public void setCpuStatusChangeCallback(Consumer<String> callback) {
        cpuStatusChangeCallback = callback;
    }


    public TomasuloCPU(Memory memory, ConfigStore config, InstructionInterface bubble) {
        this.config = config;
        this.bubble = bubble;

        logger.info("Creating the CPU...");
        cycles = 0;
        setStatus(CPUStatus.READY);
        mem = memory;
        logger.info("Got Memory instance..");

        // Registers initialization
        gpr = new Register[32];
        gpr[0] = new R0();

        for (int i = 1; i < 32; i++) {
            gpr[i] = new Register("R" + i);
        }

        pc = new Register("PC");
        old_pc = new Register("Old PC");
        LO = new Register("LO");
        HI = new Register("HI");

        //Floating point registers initialization
        fpr = new RegisterFP[32];

        for (int i = 0; i < 32; i++) {
            fpr[i] = new RegisterFP("F" + i);
        }

        FCSR = new FCSRRegister();
        configFPExceptionsAndRM();

        // initialize function units
        this.cdb = new CommonDataBus();
        this.fus = new ArrayList<>();
        int num_fus = 0;
        for (int i = 0; i < this.config.getInt(ConfigKey.INT_ADDERS); i++) {
            this.fus.add(new Integers(num_fus, this));
            num_fus ++;
        }
        for (int i = 0; i < this.config.getInt(ConfigKey.FP_ADDERS); i++) {
            this.fus.add(new FPAdders(num_fus, this));
            num_fus ++;
        }
        for (int i = 0; i < this.config.getInt(ConfigKey.FP_MULTIPLIERS); i++) {
            this.fus.add(new FPMultipliers(num_fus, this));
            num_fus ++;
        }
        for (int i = 0; i < this.config.getInt(ConfigKey.FP_DIVIDERS); i++) {
            this.fus.add(new FPDividers(num_fus, this));
            num_fus ++;
        }
        this.fus.add(new FuMemory(num_fus, this, memory));
        this.fus.add(new FuNOP(num_fus, this));

        logger.info("Tomasulo CPU Created.");
    }


// SETTING PROPERTIES ------------------------------------------------------------------
    /** Sets the CPU status.
     *  @param status a CPUStatus value
     */
    public void setStatus(CPUStatus status) {
        logger.info("Changing CPU status to " + status.name());
        this.status = status;
        if (cpuStatusChangeCallback != null) {
            cpuStatusChangeCallback.accept(status.name());
        }
    }

    /** Sets the flag bits of the FCSR
     * @param tag a string value between  V  Z O U I
     * @param value a binary value
     */
    public void setFCSRFlags(String tag, int value) throws IrregularStringOfBitsException {
        FCSR.setFCSRFlags(tag, value);
    }

    /** Sets the cause bits of the FCSR
     * @param tag a string value between  V  Z O U I
     * @param value a binary value
     */
    public void setFCSRCause(String tag, int value) throws IrregularStringOfBitsException {
        FCSR.setFCSRCause(tag, value);
    }

    /** Sets the selected FCC bit of the FCSR
     * @param cc condition code is an int value in the range [0,7]
     * @param condition the binary value of the relative bit
     */
    public void setFCSRConditionCode(int cc, int condition) throws IrregularStringOfBitsException {
        FCSR.setFCSRConditionCode(cc, condition);
    }

//GETTING PROPERTIES -----------------------------------------------------------------

    /** Gets the CPU status
     *  @return status a CPUStatus value representing the current CPU status
     */
    public CPUStatus getStatus() {
        return status;
    }

    public Register[] getRegisters() {
        return gpr;
    }

    public RegisterFP[] getRegistersFP() {
        return fpr;
    }

    /** This method returns a specific GPR
     * @param index the register number (0-31)
     */
    public Register getRegister(int index) {
        return gpr[index];
    }

    public int IntegerRegisters() {
        return gpr.length;
    }

    public RegisterFP getRegisterFP(int index) {
        return fpr[index];
    }

    /** Gets the Floating Point Control Status Register*/
    public FCSRRegister getFCSR() {
        return FCSR;
    }

    /** Gets the selected FCC bit of the FCSR
     * @param cc condition code is an int value in the range [0,7]
     */
    public int getFCSRConditionCode(int cc) {
        return FCSR.getFCSRConditionCode(cc);
    }

    /** Gets the current rounding mode readeng the FCSR
     * @return the rounding mode */
    public FCSRRegister.FPRoundingMode getFCSRRoundingMode() {
        return FCSR.getFCSRRoundingMode();
    }

    /** Returns the number of cycles performed by the CPU.
     *  @return an integer
     */
    public int getCycles() {
        return cycles;
    }

    /** Returns the number of instructions executed by the CPU
     *  @return an integer
     */
    public int getInstructions() {
        return instructions;
    }

    /** Returns the number of RAW Stalls that happened inside the pipeline
     * @return an integer
     */
    public int getRAWStalls() {
        return RAWStalls;
    }

    /** Returns the number of WAW stalls that happened inside the pipeline
     * @return an integer
     */
    public int getWAWStalls() {
        return WAWStalls;
    }

    /** Returns the number of Structural Stalls (Divider not available) that happened inside the pipeline
     * @return an integer
     */
    public int getStructuralStallsDivider() {
        return dividerStalls;
    }

    /** Returns the number of Structural Stalls (Memory not available) that happened inside the pipeline
     * @return an integer
     */
    public int getStructuralStallsMemory() {
        return memoryStalls;
    }

    /** Returns the number of Structural Stalls (EX not available) that happened inside the pipeline
     * @return an integer
     */
    public int getStructuralStallsEX() {
        return exStalls;
    }

    /** Returns the number of Structural Stalls (FP Adder and FP Multiplier not available) that happened inside the pipeline
     * @return an integer
     */
    public int getStructuralStallsFuncUnit() {
        return funcUnitStalls;
    }

    /** Gets the floating point unit enabled exceptions
     *  @return true if exceptionName is enabled, false in the other case
     */
    public boolean getFPExceptions(FCSRRegister.FPExceptions exceptionName) {
        return FCSR.getFPExceptions(exceptionName);
    }

    /** Gets the Program Counter register
     *  @return a Register object
     */
    public Register getPC() {
        return pc;
    }
    /** Gets the Last Program Counter register
     *  @return a Register object
     */
    public Register getLastPC() {
        return old_pc;
    }

    /** Gets the LO register. It contains integer results of doubleword division
     * @return a Register object
     */
    public Register getLO() {
        return LO;
    }

    /** Gets the HI register. It contains integer results of doubleword division
     * @return a Register object
     */
    public Register getHI() {
        return HI;
    }

    /** Gets the structural stall counter
     *@return the memory stall counter
     */
    public int getMemoryStalls() {
        return memoryStalls;
    }

    public CommonDataBus getCdb() {
        return cdb;
    }

    /** This method performs a single pipeline step
     */
    public void step() throws AddressErrorException, HaltException, IrregularWriteOperationException, StoppedCPUException, MemoryElementNotFoundException, IrregularStringOfBitsException, TwosComplementSumException, SynchronousException, BreakException, NotAlignException {
        configFPExceptionsAndRM();
        Optional<String> syncex;
        if (status != CPUStatus.RUNNING && status != CPUStatus.STOPPING) {
            throw new StoppedCPUException();
        }

        cycles ++;
        try {
            for (int i = 0; i < 4 * mem.getInstructionsNumber(); i += 4) {
                update_instruction(mem.getInstruction(i));
            }
            var cdb = this.getCdb();
            if (cdb.isBusy()) {
                var reg = cdb.getReg();
                if (this.registerStatuses[reg] == null) {
                    if (reg < this.IntegerRegisters()) {
                        this.getRegister(reg).setBits(cdb.getValue(), 0);
                    } else {
                        this.getRegisterFP(reg - this.IntegerRegisters()).setBits(cdb.getValue(), 0);
                    }
                }
                cdb.reset();
            }
            InstructionInterface next_if = mem.getInstruction((int) pc.getValue());
            logger.info("Fetched new instruction " + next_if);
            if (this.reserve(next_if)) {
                old_pc.writeDoubleWord((pc.getValue()));
                pc.writeDoubleWord((pc.getValue()) + 4);
            }
        } catch (JumpException ex) {
            logger.info("Executing a Jump.");
            old_pc.writeDoubleWord((pc.getValue()));
            pc.writeDoubleWord((pc.getValue()) + 4);
        } catch (WAWException ex) {
            WAWStalls++;
        } catch (SynchronousException ex) {
            logger.info("Exception: " + ex.getCode());
            throw ex;

        } catch (HaltException ex) {
            setStatus(CPUStatus.HALTED);
            // The last tick does not execute a full CPU cycle, it just removes the last instruction from the pipeline.
            // Decrementing the cycles counter by one.
            cycles--;
            throw ex;

        } catch (StoppingException e) {
            e.printStackTrace();
        } finally {
            logger.info("End of cycle " + cycles + "\n---------------------------------------------\n");
        }
    }

    private boolean reserve(InstructionInterface ins) throws IrregularStringOfBitsException, WAWException, IrregularWriteOperationException, StoppingException, BreakException, TwosComplementSumException, FPInvalidOperationException, JumpException {
        for (FunctionUnit fu : this.fus) {
            if (fu.fuType() == ins.getFUType() && fu.getStatus() == Status.Idle) {
                return fu.issue(ins, this.cycles);
            }
        }
        return false;
    }

    private void update_instruction(InstructionInterface ins) throws NotAlignException, IntegerOverflowException, FPDivideByZeroException, AddressErrorException, FPInvalidOperationException, JumpException, DivisionByZeroException, BreakException, HaltException, IrregularWriteOperationException, IrregularStringOfBitsException, MemoryElementNotFoundException, TwosComplementSumException, FPOverflowException, FPUnderflowException {
        var fu_id = ins.getFunctionUnit();
        if (fu_id == null) {
            return;
        }
        var fu = this.fus.get(fu_id);
        if (fu.getStatus() == Status.Issued || fu.getStatus() == Status.Waiting) {
            // try execute
            var all_clear = true;
            var station = fu.getReservationStation();
            if (station.getQj() != null && station.getValueJ() == null) {
                var cdb_res = this.cdb.get(station.getQj());
                if (cdb_res != null) {
                    station.setValueJ(cdb_res);
                } else {
                    all_clear = false;
                }
            }
            if (station.getQk() != null && station.getValueK() == null) {
                var cdb_res = this.cdb.get(station.getQk());
                if (cdb_res != null) {
                    station.setValueK(cdb_res);
                } else {
                    all_clear = false;
                }
            }
            if (all_clear) {
                // all operands ready, can execute
                fu.setStatus(Status.Running);
                ins.setExecCycle(cycles);
                ins.EX();
            } else {
                fu.setStatus(Status.Waiting);
            }
        } else if (fu.getStatus() == Status.Running) {
            // decrease runtime counter
            var countDown = ins.getCountDown();
            if (countDown == 0) {
                // should write back to cdb
                if (ins.WB()) {
                    // if success, reset the function unit and reservation station
                    fu.setStatus(Status.Idle);
                    fu.getReservationStation().reset();
                    ins.setWBCycle(cycles);
                }
            } else {
                countDown -= 1;
                ins.setCountDown(countDown);
            }
        }
    }

    /** This method resets the CPU components (GPRs, memory,statistics,
     *   PC, pipeline and Symbol table).
     *   It resets also the Dinero Tracefile object associated to the current
     *   CPU.
     */
    public void reset() {
        // Reset CPU state.
        setStatus(CPUStatus.READY);
        cycles = 0;
        instructions = 0;
        RAWStalls = 0;
        WAWStalls = 0;
        dividerStalls = 0;
        funcUnitStalls = 0;
        exStalls = 0;
        memoryStalls = 0;

        // Reset registers.
        for (int i = 0; i < 32; i++) {
            gpr[i].reset();
        }

        //reset FPRs
        for (int i = 0; i < 32; i++) {
            fpr[i].reset();
        }


        try {
            // Reset the FCSR condition codes.
            for (int cc = 0; cc < 8; cc++) {
                setFCSRConditionCode(cc, 0);
            }

            // Reset the FCSR flags.
            setFCSRFlags("V", 0);
            setFCSRFlags("O", 0);
            setFCSRFlags("U", 0);
            setFCSRFlags("Z", 0);

            // Reset the FCSR cause bits.
            setFCSRCause("V", 0);
            setFCSRCause("O", 0);
            setFCSRCause("U", 0);
            setFCSRCause("Z", 0);
        } catch (IrregularStringOfBitsException ex) {
            ex.printStackTrace();
        }

        for (var fu: fus) {
            fu.getReservationStation().reset();
            fu.setStatus(Status.Idle);
        }

        LO.reset();
        HI.reset();

        // Reset program counter
        pc.reset();
        old_pc.reset();

        // Reset the memory.
        mem.reset();

        logger.info("CPU Resetted");
    }

    /** Test method that returns a string containing the values of every
     * register.
     * @return string representation of the register file contents
     */
    public String gprString() {
        StringBuilder s = new StringBuilder();

        int i = 0;

        for (Register r : gpr) {
            s.append("Register ").append(i++).append(":\t").append(r.toString()).append("\n");
        }

        return s.toString();
    }

    /** Test method that returns a string containing the values of every
     * FPR.
     * @return a string
     */
    private String fprString() {
        StringBuilder s = new StringBuilder();
        int i = 0;

        for (RegisterFP r: fpr) {
            s.append("FP Register ").append(i++).append(":\t").append(r.toString()).append("\n");
        }

        return s.toString();
    }

    private void configFPExceptionsAndRM() {
        try {
            FCSR.setFPExceptions(FCSRRegister.FPExceptions.INVALID_OPERATION, config.getBoolean(ConfigKey.FP_INVALID_OPERATION));
            FCSR.setFPExceptions(FCSRRegister.FPExceptions.OVERFLOW, config.getBoolean(ConfigKey.FP_OVERFLOW));
            FCSR.setFPExceptions(FCSRRegister.FPExceptions.UNDERFLOW, config.getBoolean(ConfigKey.FP_UNDERFLOW));
            FCSR.setFPExceptions(FCSRRegister.FPExceptions.DIVIDE_BY_ZERO, config.getBoolean(ConfigKey.FP_DIVIDE_BY_ZERO));

            //setting the rounding mode
            if (config.getBoolean(ConfigKey.FP_NEAREST)) {
                FCSR.setFCSRRoundingMode(FCSRRegister.FPRoundingMode.TO_NEAREST);
            } else if (config.getBoolean(ConfigKey.FP_TOWARDS_ZERO)) {
                FCSR.setFCSRRoundingMode(FCSRRegister.FPRoundingMode.TOWARD_ZERO);
            } else if (config.getBoolean(ConfigKey.FP_TOWARDS_PLUS_INFINITY)) {
                FCSR.setFCSRRoundingMode(FCSRRegister.FPRoundingMode.TOWARDS_PLUS_INFINITY);
            } else if (config.getBoolean(ConfigKey.FP_TOWARDS_MINUS_INFINITY)) {
                FCSR.setFCSRRoundingMode(FCSRRegister.FPRoundingMode.TOWARDS_MINUS_INFINITY);
            }
        } catch (IrregularStringOfBitsException ex) {
            Logger.getLogger(TomasuloCPU.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String toString() {
        String s = "";
        s += mem.toString() + "\n";
        s += gprString();
        s += fprString();
        return s;
    }

    /** Private class, representing the R0 register */
    // TODO: DEVE IMPOSTARE I SEMAFORI?????
    private class R0 extends Register {
        public R0() {
            super("R0");
        }
        public long getValue() {
            return (long) 0;
        }
        public String getBinString() {
            return "0000000000000000000000000000000000000000000000000000000000000000";
        }
        public String getHexString() {
            return "0000000000000000";
        }
        public void setBits(String bits, int start) {
        }
        public void writeByteUnsigned(int value) {}
        public void writeByte(int value, int offset) {}
        public void writeHalfUnsigned(int value) {}
        public void writeHalf(int value) {}
        public void writeHalf(int value, int offset) {}
        public void writeWordUnsigned(long value) {}
        public void writeWord(int value) {}
        public void writeWord(long value, int offset) {}
        public void writeDoubleWord(long value) {}

    }

    public ConfigStore getConfig() {
        return config;
    }
}
