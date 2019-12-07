/*
 * JAL.java
 *
 *  20th may 2006
 * Instruction JAL of the MIPS64 Instruction Set
 * (c) 2006 EduMips64 project - Trubia Massimo, Russo Daniele
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

/**
 * <pre>
 *      Syntax: JAL target
 * Description: To execute a procedure call within the current 256 MB-aligned region
 *              Place the return address link in GPR 31.  This is a
 *              PC-region branch (not PC-relative);
 *</pre>
 * @author Trubia Massimo, Russo Daniele
 *
 */

public class JAL extends FlowControl_JType {
  private final String OPCODE_VALUE = "000011";
  private final int PC_VALUE = 0;

  /** Creates a new instance of J */
  JAL() {
    super.OPCODE_VALUE = OPCODE_VALUE;
    this.name = "JAL";
  }

  public void EX() throws IrregularStringOfBitsException, IntegerOverflowException, IrregularWriteOperationException, JumpException {
    //converting INSTR_INDEX into a bynary value of 26 bits in length
    String instr_index = Converter.positiveIntToBin(28, params.get(INSTR_INDEX));
    //appending the 35 most significant bits of the program counter on the left of "instr_index"
    Register pc = cpu.getPC();
    String pc_all = Long.toBinaryString(getPc());
    String pc_significant = pc_all.substring(0, 36);
    String pc_new = pc_significant + instr_index;
    pc.setBits(pc_new, 0);
    this.resReg.setBits(Long.toBinaryString(getPc() - 4), 0);
    throw new JumpException();
  }

  @Override
  public Integer op1() {
    return null;
  }

  @Override
  public Integer op2() {
    return null;
  }

  @Override
  public Integer dest() {
    return 31;
  }

  @Override
  public Integer imme() {
    return params.get(INSTR_INDEX);
  }

  public void doWB() throws IrregularStringOfBitsException {}

}
