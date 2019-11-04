package com.miracleyoo.mips.instructions.reader;

public class DataBookmark {
    Integer pos;
    DataType type;

    public DataBookmark(Integer pos, DataType type) {
        this.pos = pos;
        this.type = type;
    }

    public Integer getPos() {
        return pos;
    }

    public void setPos(Integer pos) {
        this.pos = pos;
    }

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }
}
