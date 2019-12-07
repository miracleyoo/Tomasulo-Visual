/*
 * MOVZ.java
 *
 * 26th may 2006
 * Instruction MOVZ of the MIPS64 Instruction Set
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
import org.edumips64.core.IrregularStringOfBitsException;
import org.edumips64.core.tomasulo.fu.Type;

import java.util.logging.Logger;

/**
 * <pre>
 * Syntax:      MOVZ rd, rs, rt
 * Description: if rt = 0 then rd = rs
 *              If the value in GPR rt is equal to zero, then the contents
 *              of GPR rs are placed into GPR rd.
 *</pre>
 * @author Trubia Massimo, Russo Daniele
 */
class MOVZ extends ALU_RType {
  private final String OPCODE_VALUE = "001010";
  private static final Logger logger = Logger.getLogger(MOVZ.class.getName());

  // Set to true if the Write Back stage should write data.
  private boolean should_write = false;

  MOVZ() {
    super.OPCODE_VALUE = OPCODE_VALUE;
    name = "MOVZ";
  }
  public void EX() throws IrregularStringOfBitsException, IntegerOverflowException, TwosComplementSumException {
    if (Long.parseLong(this.reservationStation.getValueK(), 2) == 0) {
      this.resReg.setBits(this.reservationStation.getValueJ(), 0);
    } else {
      should_write = false;
    }
  }

  public Type getFUType() {
    return Type.Integer;
  }
}
