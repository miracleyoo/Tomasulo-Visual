package org.edumips64.core.is;

import org.edumips64.core.*;
import org.edumips64.core.fpu.FPDivideByZeroException;
import org.edumips64.core.fpu.FPInvalidOperationException;
import org.edumips64.core.fpu.FPOverflowException;
import org.edumips64.core.fpu.FPUnderflowException;
import org.edumips64.core.tomasulo.fu.ReservationStation;
import org.edumips64.core.tomasulo.fu.Type;

/** Interface representing an instruction. It is essentially the view of an instruction
* that the CPU has, and its purpose is breaking the circular dependency between the CPU class
* and the Instruction class.*/
public interface InstructionInterface {
  /**
   * <pre>
   * Decode stage of the Pipeline
   * In this method all instructions that modify GPRs lock the involved register
   *
   * Returns true if there are RAW conflict, false if there is none.
   *
   * This is an optimization, since in large programs with no forwarding, RAW is
   * pretty common, and the code for handling it takes a significant amount of
   * time.
   *
   * For example, before this optimization was implemented the testSetBitSort unit
   * test took ~21.4 seconds to finish, and after it it takes ~15.5 seconds (under
   * Windows).
   *</pre>
   **/
  // boolean ISSUE() throws IrregularWriteOperationException, IrregularStringOfBitsException, TwosComplementSumException, JumpException, BreakException, WAWException, FPInvalidOperationException, StoppingException;

  /**
   * <pre>
   * Execute stage of the Pipeline
   * In this stage all Alu Instructions perform their computations and save results in temporary registers
   * </pre>
   **/

  void EX() throws IrregularStringOfBitsException, IntegerOverflowException, TwosComplementSumException, IrregularWriteOperationException, DivisionByZeroException, NotAlignException, FPInvalidOperationException, FPUnderflowException, FPOverflowException, FPDivideByZeroException, AddressErrorException, JumpException, MemoryElementNotFoundException, BreakException, HaltException;

  /**
   * <pre>
   * Write Back stage of the Pipeline
   * In this stage all instructions that modify registers write and unlock them
   * </pre>
   **/
  boolean WB() throws HaltException, IrregularStringOfBitsException;

  String getName();
  String getLabel();
  String getComment();
  String getFullName();
  int getSerialNumber();
  BitSet32 getRepr();
  boolean isBubble();

  void setLabel(String label);

  Type getFUType();

  Integer op1();
  Integer op2();
  Integer dest();
  Integer imme();

  Integer getIssueCycle();
  void setIssueCycle(Integer issueCycle);
  Integer getExecCycle();
  void setExecCycle(Integer execCycle);
  Integer getWBCycle();
  void setWBCycle(Integer WBCycle);
  Integer getCountDown();
  void setCountDown(Integer countDown);

  Integer getFunctionUnit();
  void setFunctionUnit(int functionUnit);
  void setReservationStation(ReservationStation reservationStation);
}
