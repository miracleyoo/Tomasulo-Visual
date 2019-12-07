/*
 * FPFormattedOperandMoveInstructions.java
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
import org.edumips64.core.fpu.*;
import org.edumips64.core.tomasulo.fu.Type;

/**This is the base class of the move to and from instructions
 *
 * @author Trubia Massimo
 */

public abstract class FPFormattedOperandMoveInstructions extends ComputationalInstructions {
  protected final static int FD_FIELD = 0;
  private final static int FD_FIELD_INIT = 21;
  private final static int FD_FIELD_LENGTH = 5;
  protected final static int FS_FIELD = 1;
  private final static int FS_FIELD_INIT = 16;
  private final static int FS_FIELD_LENGTH = 5;
  private final static String COP1_FIELD = "010001";
  private final static int COP1_FIELD_INIT = 0;
  private final static int OPCODE_VALUE_INIT = 26;
  private final static int FMT_FIELD_INIT = 6;
  private final static String ZERO_FIELD = "00000";
  private final static int ZERO_FIELD_INIT = 11;

  protected String OPCODE_VALUE = "";
  protected String FMT_FIELD = "";
  FPFormattedOperandMoveInstructions() {
    this.syntax = "%F,%F";
    this.paramCount = 2;
  }

  public abstract void EX() throws IrregularStringOfBitsException, FPInvalidOperationException, IrregularWriteOperationException, FPUnderflowException, FPOverflowException;

  public void pack() throws IrregularStringOfBitsException {
    //conversion of instruction parameters of params list to the "repr" 32 binary value
    repr.setBits(COP1_FIELD, COP1_FIELD_INIT);
    repr.setBits(FMT_FIELD, FMT_FIELD_INIT);
    repr.setBits(ZERO_FIELD, ZERO_FIELD_INIT);
    repr.setBits(Converter.intToBin(FS_FIELD_LENGTH, params.get(FS_FIELD)), FS_FIELD_INIT);
    repr.setBits(Converter.intToBin(FD_FIELD_LENGTH, params.get(FD_FIELD)), FD_FIELD_INIT);
    repr.setBits(OPCODE_VALUE, OPCODE_VALUE_INIT);
  }

  @Override
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
