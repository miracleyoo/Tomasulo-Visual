/* Register.java
 *
 * This class models a 64-bit CPU's internal register.
 * (c) 2006 Salvatore Scellato
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
 *
 * 18/05/2006 - Andrea Spadaccini:
 *  * Removed lock-related functions, in order to add read and write semaphores
 */
package org.edumips64.core;

import java.util.logging.Logger;


/** This class models a 64-bit CPU's internal register.
 * @author Salvatore Scellato
 */
public class Register extends BitSet64 {

  private String name;
  // the function unit id for the result
  private Integer result;

  public final static Logger logger = Logger.getLogger(Register.class.getName());

  /** Creates a new instance of Register.
     *  @param name name of the register (for debugging purposes).
     */
  public Register(String name) {
    this.name = name;
  }

  /** Returns the signed numeric decimal value stored in this register.
   * @return signed numerical value stored in this register
   */
  public long getValue() {
    try {
      return Converter.binToLong(this.getBinString(), false);
    } catch (IrregularStringOfBitsException e) {
      e.printStackTrace();
      this.reset(false);  //azzeriamo il registro
      return 0;
    }
  }

  /** Reset the register and its associated semaphores
   */
  public void reset() {
    super.reset(false);
  }


  public String toString() {
    String s = new String();

    try {
      s = getHexString();
    } catch (IrregularStringOfBitsException e) {
      e.printStackTrace();
    } //Impossibile che si verifichi

    return s;
  }

  public Integer getResult() {
    return result;
  }

  public void setResult(Integer result) {
    this.result = result;
  }
}
