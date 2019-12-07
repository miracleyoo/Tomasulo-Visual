/*
 * ALU_IType.java
 *
 * 5th may 2006
 * Subgroup of the MIPS64 Instruction Set
 * (c) 2006 EduMips64 project - Trubia Massimo, Russo Daniele
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
 * Foundation, Inc., 59 Temple  Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.edumips64.core.is;

import org.edumips64.core.*;

import java.util.logging.Logger;

//per diagnostica

/** This is the base class for all the immediate ALU instructions
 *
 * @author Trubia Massimo, Russo Daniele
 */
public abstract class ALU_IType extends ComputationalInstructions {
  protected final static int RT_FIELD = 0;
  protected final static int RS_FIELD = 1;
  protected final static int IMM_FIELD = 2;
  private final static int RT_FIELD_INIT = 11;
  private final static int RS_FIELD_INIT = 6;
  private final static int IMM_FIELD_INIT = 16;
  private final static int RT_FIELD_LENGTH = 5;
  private final static int RS_FIELD_LENGTH = 5;
  private final static int IMM_FIELD_LENGTH = 16;
  protected String OPCODE_VALUE = "";

  private static final Logger logger = Logger.getLogger(ALU_IType.class.getName());

  ALU_IType() {
    this.syntax = "%R,%R,%I";
    this.paramCount = 3;
  }

  public void EX() throws IrregularStringOfBitsException, IntegerOverflowException, TwosComplementSumException, IrregularWriteOperationException {
  }

  public void pack() throws IrregularStringOfBitsException {
    repr.setBits(OPCODE_VALUE, 0);
    repr.setBits(Converter.intToBin(RS_FIELD_LENGTH, params.get(RS_FIELD)), RS_FIELD_INIT);
    repr.setBits(Converter.intToBin(RT_FIELD_LENGTH, params.get(RT_FIELD)), RT_FIELD_INIT);
    repr.setBits(Converter.intToBin(IMM_FIELD_LENGTH, params.get(IMM_FIELD)), IMM_FIELD_INIT);
  }

  @Override
  public Integer op1() {
    return params.get(RS_FIELD);
  }

  @Override
  public Integer op2() {
    return null;
  }

  @Override
  public Integer dest() {
    return params.get(RT_FIELD);
  }

  @Override
  public Integer imme() {
    return params.get(IMM_FIELD);
  }
}
