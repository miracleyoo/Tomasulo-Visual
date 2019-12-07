/*
 * Instruction.java
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

package org.edumips64.core.is;

import org.edumips64.core.*;
import org.edumips64.core.fpu.*;
import org.edumips64.core.tomasulo.TomasuloCPU;
import org.edumips64.core.tomasulo.fu.ReservationStation;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**Abstract class: it provides all methods and attributes for each instruction type
 *
 * @author Trubia Massimo, Russo Daniele
 */
public abstract class Instruction implements InstructionInterface {

  protected BitSet32 repr;
  protected List<Integer> params;
  protected int paramCount;
  protected String syntax;
  protected String name;
  protected ReservationStation reservationStation;
  private String comment;

  protected Register resReg;
  protected Register resRegBak;
  protected RegisterFP resRegFP;
  protected RegisterFP resRegFPBak;

  protected String fullname;
  protected String label;
  protected static final Logger logger = Logger.getLogger(Instruction.class.getName());
  private int serialNumber;
  private int pc;
  private Integer functionUnit;

  public Integer IssueCycle;
  public Integer ExecCycle;
  public Integer WBCycle;
  public Integer CountDown;

  // Set to true if the Write Back stage should write data.
  protected boolean should_write = true;

  /** CPU instance. It is set through setCPU, and it should always be set before the instruction is considered
   * fully built. InstructionBuilder + package-local instruction constructors enforce this.
   */
  protected TomasuloCPU cpu;
  protected void setCPU(TomasuloCPU cpu) {
    this.cpu = cpu;
  }

  /** Dinero instance. It is set through setDinero, and it should always be set before the instruction is considered
   * fully built. InstructionBuilder + package-local instruction constructors enforce this.
   */
  protected Dinero dinero;
  protected void setDinero(Dinero dinero) {
    this.dinero = dinero;
  }

  protected void setSerialNumber(int serialNumber) {
    this.serialNumber = serialNumber;
  }

  public ReservationStation getReservationStation() {
    return reservationStation;
  }

  public void setReservationStation(ReservationStation reservationStation) {
    this.reservationStation = reservationStation;
  }

  public Register getResReg() {
    return resReg;
  }

  public RegisterFP getResRegFP() {
    return resRegFP;
  }

  public void setResReg(Register resReg) {
    this.resReg = resReg;
  }

  public void setResRegFP(RegisterFP resRegFP) {
    this.resRegFP = resRegFP;
  }

  public Register getResRegBak() {
    return resRegBak;
  }

  public RegisterFP getResRegFPBak() {
    return resRegFPBak;
  }

  /** Creates a new instance of Instruction */
  Instruction() {
    params = new LinkedList<>();
    repr = new BitSet32();
    syntax = "";
    repr.reset(false);
    resReg = new Register("Inst.Result");
    resRegFP = new RegisterFP("FPInst.Result");
    resRegBak = new Register("Inst.ResultBak");
    resRegFPBak = new RegisterFP("FPInst.ResultBak");
    functionUnit = null;
  }

  /** <pre>
   *  Returns a BitSet32 holding the binary representation of the Instruction
   *  @return the Bitset32 representing the instruction
   *  </pre>
   * */
  public BitSet32 getRepr() {
    return repr;
  }

  /**
   * <pre>
   * Builds the binary encoding of instructions.
   * Every instruction is represented by a 32 bit field
   * </pre>
   **/
  public abstract void pack() throws IrregularStringOfBitsException;

  /**
   * <pre>
   * Gets the syntax of any instruction as string composed by the following simbols
   * %R   Register
   * %I   Immediate
   * %U   Unsigned Immediate
   * %L   Memory Label
   * %E   Program Label used for Jump Instructions
   * %B   Program Label used for Brench Instructions
   *
   * examples:
   *   Instruction -----> Syntax
   * DADD  R1,R2,R3   |   %R,%R,%R
   * DADDI R1,R2,-3   |   %R,%R,%I
   * DSLL  R1,R2,15   |   %R,%R,%U
   * LD    R1,vet(R0) |   %R,%L(%R)
   * J     loop       |   %E
   * BNE   R1,R2,loop |   %R,%R,%B
   * </pre>
   **/
  public String getSyntax() {
    return syntax;
  }

  /**
   * Returns the name of the instruction as string.
   * @return the instruction name(e.g. "DADD")
   **/
  public String getName() {
    return name;
  }


  /**
   *<pre>
   * Returns a list with the instruction parameters
   * e.g. DADD R1,R2,R3 --> params= { 1, 2, 3}
   *      LD R1, var(R0)--> params= { 1, address memory corresponding with var, 0}
   * </pre>
   *@return the list of parameters
   **/
  public List<Integer> getParams() {
    return params;
  }


  /**
   *<pre>
   * Sets the instruction with a list of parameters
   *          Passed list                                      | Instruction to set
   * e.g. list= { 1, 2, 3}                                     |   DADD R1,R2,R3
   *      list= { 1, address memory corresponding with var, 0} |   LD R1, var(R0)
   *@param params The list of parameters
   **/
  public void setParams(List<Integer> params) {
    this.params = params;
  }

  /**
   * Sets the full name of the instruction as string
   *@param value full name of the instruction (e.g. "DADD R1,R2,R3")
   */
  public void setFullName(String value) {
    fullname = value;
  }

  /** Sets the comment of the instruction as string. The comment is the text
   *  after every semicolon in the file .s
   * @param comment the comment associated with the instruction
   */
  public void setComment(String comment) {
    this.comment = comment;
  }


  /** Gets the comment of the instruction as string.The comment is the text
   *  after every semicolon in the file .s
   * @return the comment
   */
  public String getComment() {
    return comment;
  }

  /** Gets the full name of the instruction as string.
    * @return the full name of the instruction  (e.g. "DADD R1,R2,R3")
    */
  public String getFullName() {
    return fullname;
  }

  /** Gets the serial number of this instruction */
  public int getSerialNumber() {
    return serialNumber;
  }

  public String toString() {
    String repr = name + " (" + fullname + ") [# " + serialNumber + "]";
    if (label != null && label.length() > 0) {
      repr += " {label: " + label + "}";
    }
    return repr;
  }

  /**<pre>
   * Gets the label of the instruction. Labels may be assigned to instructions
   * when they are inserted in the symbol table
   *</pre>
   * @return label of the instruction
   */
  public String getLabel() {
    return label;
  }

  /**<pre>
   * Sets the label of the instruction. Labels may be assigned to instructions
   * when they are inserted in the symbol table
   *</pre>
   * @param value label of the instruction
   */
  public void setLabel(String value) {
    label = value;
  }

  /**<pre>
   * The repr field of the passed instruction is compared with the repr field
   * of this instruction. If they are identical then true is returned else false is returned
   * </pre>
   * @param instr instruction to compare with this
   * @return the result of the comparison
   */
  @Override
  public boolean equals(Object instr) {
    if (instr == null) {
      return false;
    }

    if (instr == this) {
      return true;
    }

    if (!(instr instanceof Instruction)) {
      return false;
    }

    Instruction i = (Instruction) instr;
    return i.getSerialNumber() == serialNumber;
  }

  /** Use the serial number as the hash code for the instruction.
   * This is consistent with the overridden equals().
   * @return the serial number of the instruction
   */
  @Override
  public int hashCode() {
    return serialNumber;
  }

  public boolean WB() throws IrregularStringOfBitsException {
    assert this.dest() != null;
    if (!this.should_write) {
      return true;
    }
    String out;
    if (this.dest() < this.cpu.IntegerRegisters()) {
      out = this.resReg.getBinString();
    } else {
      out = this.resRegFP.getBinString();
    }
    return this.cpu.getCdb().set(this.functionUnit, out);
  }

  public void setFunctionUnit(int functionUnit) {
    this.functionUnit = functionUnit;
  }

  /**<pre>
   * Returns true if the instruction is a BUBBLE, false otherwise. BUBBLE is used to fill
   * the pipeline and is not a real instruction, so some parts of the UI code need to know
   * if the instruction is a BUBBLE or not. This method abstracts the details of how to check
   * if an instruction is a BUBBLE.
   * </pre>
   */
  public boolean isBubble() {
    return name.equals(" ");
  }

  public int getPc() {
    return pc;
  }

  public void setPc(int pc) {
    this.pc = pc;
  }

  public Integer getIssueCycle() {
    return IssueCycle;
  }

  public void setIssueCycle(Integer issueCycle) {
    IssueCycle = issueCycle;
  }

  public Integer getExecCycle() {
    return ExecCycle;
  }

  public void setExecCycle(Integer execCycle) {
    ExecCycle = execCycle;
  }

  public Integer getWBCycle() {
    return WBCycle;
  }

  public void setWBCycle(Integer WBCycle) {
    this.WBCycle = WBCycle;
  }

  public Integer getCountDown() {
    return CountDown;
  }

  public void setCountDown(Integer countDown) {
    CountDown = countDown;
  }

  @Override
  public Integer getFunctionUnit() {
    return functionUnit;
  }
}
