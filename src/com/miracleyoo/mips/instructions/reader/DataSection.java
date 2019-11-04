package com.miracleyoo.mips.instructions.reader;

import java.util.HashMap;
import java.util.List;

public class DataSection {
    List<Byte> raw;
    HashMap<String, DataBookmark> bookmarks;

    public List<Byte> getRaw() {
        return raw;
    }

    public void setRaw(List<Byte> raw) {
        this.raw = raw;
    }

    public HashMap<String, DataBookmark> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(HashMap<String, DataBookmark> bookmarks) {
        this.bookmarks = bookmarks;
    }
}
