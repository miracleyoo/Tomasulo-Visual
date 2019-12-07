/** DIVU.java
 *
 * 18th may 2007
 * Instruction DIVU of the MIPS64 Instruction Set
 * (c) 2006 EduMips64 project - Giorgio Scibilia - Lorenzo Sciuto - Erik Urzi'
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
 *      Syntax: DIVU rs, rt
 * Description: (LO, HI) = rs / rt
 *              To divide 32-bit unsigned integers
 *  *           The 32-bit word in GPR rs is divided by the 32-bit
 *              word in GPR rt, treating both operands as unsigned values.
 *              The 32-bit quotient is sign-extended and placed into special register LO and the
 *              32-bit remainder is sign-extended and placed into special register HI.
 *              No arithmetic exception occurs under any circumstances.
 *</pre>
 * @author Giorgio Scibilia - Lorenzo Sciuto - Erik Urzi'
 */
class DIVU extends ALU_RType {
  private final static int RS_FIELD = 0;
  private final static int RT_FIELD = 1;
  private final String OPCODE_VALUE = "011011";

  DIVU() {
    super.OPCODE_VALUE = OPCODE_VALUE;
    syntax = "%R,%R";
    name = "DIVU";
  }
  public void EX() throws IrregularStringOfBitsException, IntegerOverflowException, TwosComplementSumException, DivisionByZeroException {
    //getting String from temporary register
    String rt = this.reservationStation.getValueK();
    String rs = this.reservationStation.getValueJ();
    //cutting the high part of registers
    rt = rt.substring(32, 64);
    rs = rs.substring(32, 64);
    long l_rt = Converter.binToLong(rt, true);
    long l_rs = Converter.binToLong(rs, true);
    //performing operations
    long quotient = 0;

    try {
      quotient = l_rs / l_rt;
    } catch (ArithmeticException e) {
      throw new DivisionByZeroException();
    }

    long remainder = l_rs % l_rt;

    String str_quotient = Long.toString(quotient, 2);
    String str_remainder = Long.toString(remainder, 2);

    //performing sign extension
    for (int i = str_quotient.length(); i < 32; i++) {
      str_quotient = '0' + str_quotient;
    }

    for (int i = str_remainder.length(); i < 32; i++) {
      str_remainder = '0' + str_remainder;
    }

    for (int i = 0; i < 32; i++) {
      str_quotient = '0' + str_quotient;
      str_remainder = '0' + str_remainder;
    }

    //writing result in temporary registers
    //the result will never have more than 32-bits
    this.resReg.setBits(str_quotient, 0);
    this.resRegBak.setBits(str_remainder, 0);
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
  }  public Type getFUType() {
    return Type.FPDivider;
  }

}
