/*
 * SLL.java
 *
 * 18th may 2007
 * Instruction SLL of the MIPS64 Instruction Set
 * (c) 2006 EduMips64 project - Erik UrzÃ¬ - Giorgio Scibilia - Sciuto Lorenzo
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
 *      Syntax: SLL rd, rt, sa word shift left logical
 * Description: To execute a left-shift of a word by a fixed amount of 0 to 31 bits
 *              The 32-bit word contents of GPR rt are shifted left,
 *              inserting zeros into the emptied bits; the word result is sign-extended and placed in GPR rd.
 *              The bit-shift amount in the range 0 to 31 is specified by sa.
 *</pre>
 * @author Erik UrzÃ¬ - Giorgio Scibilia - Sciuto Lorenzo
 */
public class SLL extends ALU_RType {
  private final int RD_FIELD = 0;
  private final int RT_FIELD = 1;
  private final int SA_FIELD = 2;
  private final int RD_FIELD_INIT = 16;
  private final int RT_FIELD_INIT = 11;
  private final int SA_FIELD_INIT = 21;
  private final int RD_FIELD_LENGTH = 5;
  private final int RT_FIELD_LENGTH = 5;
  private final int SA_FIELD_LENGTH = 5;
  private final String OPCODE_VALUE = "000000";
  SLL() {
    super.OPCODE_VALUE = OPCODE_VALUE;
    name = "SLL";
    syntax = "%R,%R,%U";
  }

  public void EX() throws IrregularStringOfBitsException, IntegerOverflowException, TwosComplementSumException {
    //getting strings from temporary registers
    int sa = this.reservationStation.getImme();
    String rt = this.reservationStation.getValueJ();
    //cutting the high part of register
    rt = rt.substring(32, 64);
    //composing new shifted value and performing sign extension
    StringBuffer sb = new StringBuffer();

    for (int i = 0; i < 32; i++) {
      sb.append(rt.charAt(0));
    }

    //copy the rd register after cutting a number of bits specificated into sa
    sb.append(rt.substring(sa));

    //filling the remaining bits with 0
    for (int i = 0; i < sa; i++) {
      sb.append('0');
    }

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
  public Integer imme() {
    return params.get(SA_FIELD);
  }
}
