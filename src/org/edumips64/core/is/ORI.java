/*
 * ORI.java
 *
 * 22th may 2006
 * Instruction ORI of the MIPS64 Instruction Set
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

/**
 * <pre>
 * Syntax:        ORI rt, rs, immediate
 * Description:   To do a bitwise logical OR with a constant
 * </pre>
 * @author Trubia Massimo, Russo Daniele
 */

class ORI extends ALU_IType {
  private final String OPCODE_VALUE = "001101";
  ORI() {
    super.OPCODE_VALUE = OPCODE_VALUE;
    this.name = "ORI";
  }

  public void EX() throws IrregularStringOfBitsException, IntegerOverflowException, TwosComplementSumException, IrregularWriteOperationException {
    //getting values from temporary registers
    String imm = Long.toBinaryString(this.reservationStation.getImme());
    String rs = this.reservationStation.getValueJ();
    StringBuffer sb = new StringBuffer();
    boolean immbit, rsbit, resbit;

    //performing bitwise OR between immediate and rs register
    for (int i = 0; i < 64; i++) {
      rsbit = rs.charAt(i) == '1';
      immbit = imm.charAt(i) == '1';
      resbit = rsbit || immbit;
      sb.append(resbit ? '1' : '0');
    }

    this.resReg.setBits(sb.substring(0), 0);
  }

  @Override
  public Type getFUType() {
    return Type.Integer;
  }
}
