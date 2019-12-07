/*
 * ALU_RType.java
 *
 * 5th may 2006
 * Subgroup of the MIPS64 Instruction Set
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

import java.util.logging.Logger;

//per diagnostica

/**This is the base class for the R-Type instructions
 *
 * @author Trubia Massimo, Russo Daniele
 */
public abstract class ALU_RType extends ComputationalInstructions {
  protected final static int RD_FIELD = 0;
  protected final static int RS_FIELD = 1;
  protected final static int RT_FIELD = 2;
  protected final static int RD_FIELD_INIT = 16;
  protected final static int RS_FIELD_INIT = 6;
  protected final static int RT_FIELD_INIT = 11;
  protected final static int RD_FIELD_LENGTH = 5;
  protected final static int RS_FIELD_LENGTH = 5;
  protected final static int RT_FIELD_LENGTH = 5;
  protected String OPCODE_VALUE = "";
  protected final static int OPCODE_VALUE_INIT = 26;
  private static final Logger logger = Logger.getLogger(ALU_RType.class.getName());
  ALU_RType() {
    syntax = "%R,%R,%R";
    paramCount = 3;
  }

  public void pack() throws IrregularStringOfBitsException {
    //conversion of instruction parameters of "params" list to the "repr" form (32 binary value)
    repr.setBits(OPCODE_VALUE, OPCODE_VALUE_INIT);
    repr.setBits(Converter.intToBin(RS_FIELD_LENGTH, params.get(RS_FIELD)), RS_FIELD_INIT);
    repr.setBits(Converter.intToBin(RT_FIELD_LENGTH, params.get(RT_FIELD)), RT_FIELD_INIT);
    repr.setBits(Converter.intToBin(RD_FIELD_LENGTH, params.get(RD_FIELD)), RD_FIELD_INIT);
  }

  @Override
  public Integer op1() {
    return params.get(RS_FIELD);
  }

  @Override
  public Integer op2() {
    return params.get(RT_FIELD);
  }

  @Override
  public Integer dest() {
    return params.get(RD_FIELD);
  }

  @Override
  public Integer imme() {
    return null;
  }
}
