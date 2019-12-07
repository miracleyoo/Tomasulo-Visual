/*
 * SDC1.java
 *
 * 27th may 2007
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

/** <pre>
 *       Syntax: SDC1 ft, offset(base)
 *  Description: memory[base+offset] = ft
 *               The double value in ft is stored in memory.
 * </pre>
 */
public class SDC1 extends FPStoring {

  protected String OPCODE_VALUE = "111101";
  SDC1(Memory memory) {
    super(memory);
    super.OPCODE_VALUE = OPCODE_VALUE;
    this.name = "SDC1";
  }


  @Override
  public void EX() throws IrregularStringOfBitsException, IntegerOverflowException, MemoryElementNotFoundException, NotAlignException, IrregularWriteOperationException {
    super.EX();
    try {
      //restoring the address from the temporary register
      long address = this.offsetPlusBase;
      //For the trace file
      dinero.Store(Converter.binToHex(Converter.positiveIntToBin(64, address)), 8);
      MemoryElement memEl = memory.getCellByAddress(address);
      //writing on the memory element the RT register
      memEl.setBits(this.reservationStation.getValueK(), 0);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

