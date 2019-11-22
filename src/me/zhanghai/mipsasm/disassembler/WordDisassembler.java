/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.mipsasm.disassembler;

import me.zhanghai.mipsasm.util.BitArray;

public class WordDisassembler {

    public static void disassemble(BitArray bitArray, DisassemblyContext context) throws DisassemblerException {
        try {
            InstructionWordDisassembler.disassemble(bitArray, context);
        } catch (DisassemblerException e) {
            StorageDirectiveDisassembler.disassemble(bitArray, context);
        }
    }
}
