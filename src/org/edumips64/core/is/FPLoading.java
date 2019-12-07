/*
 * FPLoading.java
 *
 * 27th may 2007
 * Subclass of the MIPS64 Instruction Set
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

/** This is the base class for loading instruction
 *
 * @author  Trubia Massimo
 */
public abstract class FPLoading extends FPLDSTInstructions {

  FPLoading(Memory memory) {
    super(memory);
  }

  public abstract void doEX() throws IrregularStringOfBitsException, IntegerOverflowException, MemoryElementNotFoundException, NotAlignException, IrregularWriteOperationException;

  public final void EX() throws IrregularStringOfBitsException, IntegerOverflowException, MemoryElementNotFoundException, NotAlignException, IrregularWriteOperationException {
    //if the base register is valid ...
    Integer base = Integer.parseInt(this.reservationStation.getValueJ(), 2);

    //calculating  address (base+offset)
    this.offsetPlusBase = base + params.get(OFFSET_FIELD);
    doEX();
  }

  @Override
  public Integer op1() {
    return params.get(BASE_FIELD);
  }

  @Override
  public Integer op2() {
    return null;
  }

  @Override
  public Integer dest() {
    return cpu.IntegerRegisters() + params.get(FT_FIELD);
  }

  @Override
  public Integer imme() {
    return params.get(OFFSET_FIELD);
  }
}


