/*
 * DSRA.java
 *
 * 25th may 2006
 * Instruction DSRA of the MIPS64 Instruction Set
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
 *      Format: DSRA rd, rt, sa
 * Description: To execute an arithmetic right-shift of a doubleword by a fixed amountÂ0 to 31 bits
 *</pre>
 * @author Trubia Massimo, Russo Daniele
 */
public class DSRA extends ALU_RType {
  private final int RD_FIELD = 0;
  private final int RT_FIELD = 1;
  private final int SA_FIELD = 2;
  private final int RD_FIELD_INIT = 16;
  private final int RT_FIELD_INIT = 11;
  private final int SA_FIELD_INIT = 21;
  private final int RD_FIELD_LENGTH = 5;
  private final int RT_FIELD_LENGTH = 5;
  private final int SA_FIELD_LENGTH = 5;
  private final String OPCODE_VALUE = "111011";

  DSRA() {
    super.OPCODE_VALUE = OPCODE_VALUE;
    name = "DSRA";
    syntax = "%R,%R,%U";
  }

  public void EX() throws IrregularStringOfBitsException, IntegerOverflowException, TwosComplementSumException {
    //getting strings from temporary registers
    int sa = this.reservationStation.getImme();
    String rt = this.reservationStation.getValueJ();
    //composing new shifted value
    StringBuffer sb = new StringBuffer();
    char c = rt.charAt(0);

    for (int i = 0; i < sa; i++) {
      sb.append(c);
    }

    sb.append(rt.substring(0, 64 - sa));
    this.resReg.setBits(sb.substring(0), 0);
  }
  public void pack() throws IrregularStringOfBitsException {
    //conversion of instruction parameters of "params" list to the "repr" form (32 binary value)
    repr.setBits(OPCODE_VALUE, OPCODE_VALUE_INIT);
    repr.setBits(Converter.intToBin(SA_FIELD_LENGTH, params.get(SA_FIELD)), SA_FIELD_INIT);
    repr.setBits(Converter.intToBin(RT_FIELD_LENGTH, params.get(RT_FIELD)), RT_FIELD_INIT);
    repr.setBits(Converter.intToBin(RD_FIELD_LENGTH, params.get(RD_FIELD)), RD_FIELD_INIT);
  }

  public Type getFUType() {
    return Type.Integer;
  }

  @Override
  public Integer op1() {
    return params.get(RT_FIELD);
  }

  @Override
  public Integer op2() {
    return null;
  }

  @Override
  public Integer dest() {
    return params.get(RD_FIELD);
  }

  @Override
  public Integer imme() {
    return params.get(SA_FIELD);
  }
}
