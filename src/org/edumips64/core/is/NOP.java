/*
 * NOP.java
 *
 * 26th may 2006
 * Instruction NOP of the MIPS64 Instruction Set
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

/**Syntax:     NOP
 * Description:Creating null spaces in the pipeline
 *</pre>
 * @author Trubia Massimo, Russo Daniele
 */
public class NOP extends Instruction {

  /** Creates a new instance of HALT */
  NOP() {
    name = "NOP";
  }

  public void EX() throws IrregularStringOfBitsException, IntegerOverflowException, TwosComplementSumException {
  }

  @Override
  public Type getFUType() {
    return Type.NOP;
  }

  @Override
  public Integer op1() {
    return null;
  }

  @Override
  public Integer op2() {
    return null;
  }

  @Override
  public Integer dest() {
    return null;
  }

  @Override
  public Integer imme() {
    return null;
  }

  public void pack() throws IrregularStringOfBitsException {
  }

}
