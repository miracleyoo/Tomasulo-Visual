/*
 * LUI.java
 *
 * 21th may 2006
 * Instruction LUI of the MIPS64 Instruction Set
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


/** <pre>
 *  Format:        LUI rt, rs, immediate
 *  Description:   The 16-bit immediate is shifted left 16 bits and concatenated
 *                 with 16 bits of low-order zeros.
 *</pre>
  * @author Trubia Massimo, Russo Daniele
 */
class LUI extends ALU_IType {
  private final static int RT_FIELD = 0;
  private final static int IMM_FIELD = 1;
  private final static int RT_FIELD_INIT = 11;
  private final static int RS_FIELD_INIT = 6;
  private final static int IMM_FIELD_INIT = 16;
  private final static int RT_FIELD_LENGTH = 5;
  private final static int RS_FIELD_LENGTH = 5;
  private final static int IMM_FIELD_LENGTH = 16;
  private final String OPCODE_VALUE = "001111";

  LUI() {
    syntax = "%R,%I";
    //super.OPCODE_VALUE = OPCODE_VALUE;
    this.name = "LUI";
  }

  public void EX() throws IrregularStringOfBitsException, IrregularWriteOperationException {
    //getting strings from temporary registers
    String imm = Long.toBinaryString(this.reservationStation.getImme()).substring(16, 64);
    String imm_shift = imm + "0000000000000000";
    long imm_shift_lng = Converter.binToLong(imm_shift, false);
    this.resReg.writeDoubleWord(imm_shift_lng);
  }
  public void pack() throws IrregularStringOfBitsException {
    repr.setBits(OPCODE_VALUE, 0);
    repr.setBits(Converter.intToBin(RS_FIELD_LENGTH, 0), RS_FIELD_INIT);
    repr.setBits(Converter.intToBin(RT_FIELD_LENGTH, params.get(RT_FIELD)), RT_FIELD_INIT);
    repr.setBits(Converter.intToBin(IMM_FIELD_LENGTH, params.get(IMM_FIELD)), IMM_FIELD_INIT);
  }
  @Override
  public Type getFUType() {
    return Type.Integer;
  }
}
