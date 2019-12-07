/*
 * MFHI.java
 *
 * 1th june 2006
 * Instruction MFHI of the MIPS64 Instruction Set
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
import org.edumips64.core.tomasulo.fu.Type;

//per diagnostica


/**
 * <pre>
 * Syntax:      MFHI rd
 * Description: rd = HI
 *              To copy the special purpose HI register to a GPR
 *              The contents of special register HI are loaded into GPR rd.
 *</pre>
 * @author Trubia Massimo, Russo Daniele
 *
 */
class MFHI extends ALU_RType {
  private final int RD_FIELD = 0;
  private final int HI_REG = 1;
  private final String OPCODE_VALUE = "010000";

  MFHI() {
    super.OPCODE_VALUE = OPCODE_VALUE;
    syntax = "%R";
    name = "MFHI";
  }

  public void EX() throws IrregularStringOfBitsException, IntegerOverflowException, TwosComplementSumException {
    // TODO: renaming HI abd LO registers
    this.resReg.setBits(cpu.getHI().getBinString(), 0);
  }

  @Override
  public Integer op1() {
    return null;
  }

  @Override
  public Integer op2() {
    return null;
  }

  public void pack() throws IrregularStringOfBitsException {
    //conversion of instruction parameters of "params" list to the "repr" form (32 binary value)
    repr.setBits(OPCODE_VALUE, OPCODE_VALUE_INIT);
    repr.setBits(Converter.intToBin(RD_FIELD_LENGTH, params.get(RD_FIELD)), RD_FIELD_INIT);
  }

  public Type getFUType() {
    return Type.Integer;
  }
}
