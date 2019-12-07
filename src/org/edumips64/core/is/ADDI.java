/*
 * ADDI.java
 *
 * 18th may 2007
 * Instruction ADDI of the MIPS64 Instruction Set
 * (c) 2006 EduMips64 project - Lorenzo Sciuto - Giorgio Scibilia - Erik UrzÃ¬
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

import org.edumips64.core.IrregularStringOfBitsException;
import org.edumips64.core.tomasulo.fu.Type;

/**
 * <pre>
 *       Format: ADDI rt, rs, immediate
 *  Description:  To add a constant to a 32-bit integer. If overflow occurs, then trap.
 *      If the addition does not overflow, the 32-bit result is sign-extended and placed into GPR rt.
 * </pre>
 * @author Lorenzo Sciuto - Giorgio Scibilia - Erik UrzÃ¬
 */

class ADDI extends ALU_IType {
  private final String OPCODE_VALUE = "001000";
  ADDI() {
    super.OPCODE_VALUE = OPCODE_VALUE;
    this.name = "ADDI";
  }

  public void EX() throws IrregularStringOfBitsException, IntegerOverflowException, TwosComplementSumException {
    //getting strings from temporary registers
    String imm = Long.toBinaryString(this.reservationStation.getImme());
    String rs = this.reservationStation.getValueJ();
    //cutting the high part of registers
    imm = imm.substring(32, 64);
    rs = rs.substring(32, 64);
    //performing mips operations to detect IntegerOverflow
    imm = imm.charAt(0) + imm;
    rs = rs.charAt(0) + rs;
    String outputstring = InstructionsUtils.twosComplementSum(rs, imm);

    //comparison between the two most significant bits of the outputstring and
    //raising integer overflow if the first bit is different from the second one
    if (outputstring.charAt(0) != outputstring.charAt(1)) {
      //if the enable forwarding is turned on we have to ensure that registers
      //should be unlocked also if a synchronous exception occurs. This is performed
      //by executing the WB method before raising the trap
      throw new IntegerOverflowException();
    } else {
      //performing sign extension
      outputstring = outputstring.substring(1, 33);
      String filledOutputstring = outputstring;

      for (int i = 0; i < 32; i++) {
        filledOutputstring = filledOutputstring.charAt(0) + filledOutputstring;
      }

      this.resReg.setBits(filledOutputstring, 0);
    }
  }

  @Override
  public Type getFUType() {
    return Type.Integer;
  }
}
