/*
 * FPConditionalCC_DMoveInstructions.java
 *
 * 17th july 2007
 * (c) 2006 EduMips64 project - Trubia Massimo
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

/**This is the base class of the conditional move to and from instructions
 *
 * @author Trubia Massimo
 */

public abstract class FPConditionalCC_DMoveInstructions extends ComputationalInstructions {
  private final static int FD_FIELD = 0;
  private final static int FD_FIELD_INIT = 21;
  private final static int FD_FIELD_LENGTH = 5;
  private final static int FS_FIELD = 1;
  private final static int FS_FIELD_INIT = 16;
  private final static int FS_FIELD_LENGTH = 5;
  private final static int CC_FIELD = 2;
  private final static int CC_FIELD_INIT = 11;
  private final static int CC_FIELD_LENGTH = 3;
  private final static String COP1_FIELD = "010001";
  private final static int COP1_FIELD_INIT = 0;
  private final static int MOVCF_FIELD_INIT = 26;
  private final static String MOVCF_FIELD_VALUE = "010001";
  private final static String ZERO_FIELD = "0";
  private final static int ZERO_FIELD_INIT = 14;
  private final static String FMT_FIELD = "10001"; //17 for double
  private final static int FMT_FIELD_INIT = 6;
  private final static int TF_FIELD_INIT = 15;

  protected int TF_FIELD_VALUE;

  FPConditionalCC_DMoveInstructions() {
    this.syntax = "%F,%F,%C";
    this.paramCount = 3;
  }

  public void pack() throws IrregularStringOfBitsException {
    //conversion of instruction parameters of params list to the "repr" 32 binary value
    repr.setBits(COP1_FIELD, COP1_FIELD_INIT);
    repr.setBits(FMT_FIELD, FMT_FIELD_INIT);
    repr.setBits(Converter.intToBin(CC_FIELD_LENGTH, params.get(CC_FIELD)), CC_FIELD_INIT);
    repr.setBits(ZERO_FIELD, ZERO_FIELD_INIT);
    repr.setBits(String.valueOf(TF_FIELD_VALUE), TF_FIELD_INIT);
    repr.setBits(Converter.intToBin(FS_FIELD_LENGTH, params.get(FS_FIELD)), FS_FIELD_INIT);
    repr.setBits(Converter.intToBin(FD_FIELD_LENGTH, params.get(FD_FIELD)), FD_FIELD_INIT);
    repr.setBits(MOVCF_FIELD_VALUE, MOVCF_FIELD_INIT);
  }

  public void EX() throws IrregularStringOfBitsException {
    String fs = this.reservationStation.getValueJ();

    if (cpu.getFCSRConditionCode(params.get(CC_FIELD)) == TF_FIELD_VALUE) {
      this.resRegFP.setBits(fs, 0);
    }
  }

  public Type getFUType() {
    return Type.FPAdder;
  }

  @Override
  public Integer op1() {
    return cpu.IntegerRegisters() + params.get(FS_FIELD);
  }

  @Override
  public Integer op2() {
    return null;
  }

  @Override
  public Integer dest() {
    return cpu.IntegerRegisters() + params.get(FD_FIELD);
  }

  @Override
  public Integer imme() {
    return null;
  }
}
