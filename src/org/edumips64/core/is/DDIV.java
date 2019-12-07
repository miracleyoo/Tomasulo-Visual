/** DDIV.java
 *
 * 30th may 2006
 * Instruction DDIV of the MIPS64 Instruction Set
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
 *      Syntax: DDIV rs, rt
 * Description: (LO, HI) = rs / rt
 *              To divide 64-bit signed integers
 *  *           The 64-bit doubleword in GPR rs is divided by the 64-bit
 *              doubleword in GPR rt, treating both operands as signed values.
 *              The 64-bit quotient is placed into special register LO and the
 *              64-bit remainder is placed into special register HI.
 *              No arithmetic exception occurs under any circumstances.
 *</pre>
 * @author Trubia Massimo, Russo Daniele
 */
class DDIV extends ALU_RType {
  private final static int RS_FIELD = 0;
  private final static int RT_FIELD = 1;
  private final static int LO_REG = 2;
  private final static int HI_REG = 3;
  private final String OPCODE_VALUE = "011110";

  DDIV() {
    super.OPCODE_VALUE = OPCODE_VALUE;
    syntax = "%R,%R";
    name = "DDIV";
  }

  public void EX() throws IrregularStringOfBitsException, IntegerOverflowException, TwosComplementSumException, DivisionByZeroException {

    //getting values from temporary registers
    long rs = Long.parseLong(this.reservationStation.getValueJ(), 2);
    long rt = Long.parseLong(this.reservationStation.getValueK(), 2);

    //performing operations
    long quozient = 0;

    try {
      quozient = rs / rt;
    } catch (ArithmeticException e) {
      throw new DivisionByZeroException();
    }

    long remainder = rs % rt;

    //writing result in temporary registers
    try {
      this.resReg.writeDoubleWord(quozient);
      this.resRegBak.writeDoubleWord(remainder);
    } catch (IrregularWriteOperationException e) {
      e.printStackTrace();
    }
  }

  public boolean WB() throws IrregularStringOfBitsException {
    //passing results from temporary registers to destination registers and unlocking them
    Register lo = cpu.getLO();
    Register hi = cpu.getHI();
    lo.setBits(this.resReg.getBinString(), 0);
    hi.setBits(this.resRegBak.getBinString(), 0);

    // TODO: renaming lo and hi registers
    return true;
  }

  public void pack() throws IrregularStringOfBitsException {
    //conversion of instruction parameters of "params" list to the "repr" form (32 binary value)
    repr.setBits(OPCODE_VALUE, OPCODE_VALUE_INIT);
    repr.setBits(Converter.intToBin(RS_FIELD_LENGTH, params.get(RS_FIELD)), RS_FIELD_INIT);
    repr.setBits(Converter.intToBin(RT_FIELD_LENGTH, params.get(RT_FIELD)), RT_FIELD_INIT);
  }

  @Override
  public Type getFUType() {
    return Type.FPDivider;
  }
}
