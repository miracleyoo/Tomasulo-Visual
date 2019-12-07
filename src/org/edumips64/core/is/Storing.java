/*
 * Storing.java
 *
 * 22th may 2006
 * Exception of the MIPS64 Instruction Set
 * (c) 2006 EduMips64 project - Trubia Massimo, Russo Daniele
 *
 * This file is part of the EduMIPS64 project, and is released under the GNU
 * General Public License. * *
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


/** This is the base class for the storing instructions
 *
 * @author Massimo
 */
public abstract class Storing extends LDSTInstructions {
  protected static final Logger logger = Logger.getLogger(Storing.class.getName());
  protected Register rt;

  Storing(Memory memory) {
    super(memory);
  }

  public void EX() throws IrregularStringOfBitsException, IntegerOverflowException, NotAlignException, AddressErrorException, MemoryElementNotFoundException, IrregularWriteOperationException {
    // Will fill in the address variable.
    //calculating  address (base+offset)
    var base  = Long.parseLong(this.reservationStation.getValueJ(), 2);
    this.offsetPlusBase = base + params.get(OFFSET_FIELD);
    super.EX();
    // long address = ;
    memEl = memory.getCellByAddress(address);

    // Save memory access for Dinero trace file
    dinero.Store(Converter.binToHex(Converter.positiveIntToBin(64, address)), memoryOpSize);
  }

  @Override
  public Integer op1() {
    return params.get(BASE_FIELD);
  }

  @Override
  public Integer op2() {
    return params.get(RT_FIELD);
  }

  @Override
  public Integer dest() {
    return null;
  }

  @Override
  public Integer imme() {
    return null;
  }
}
