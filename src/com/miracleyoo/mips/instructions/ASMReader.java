package com.miracleyoo.mips.instructions;

import com.miracleyoo.mips.instructions.reader.DataSection;

import java.util.List;

public class ASMReader {
   DataSection data;

    public DataSection getData() {
        return data;
    }

    public void setData(DataSection data) {
        this.data = data;
    }
}