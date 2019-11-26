package me.zhanghai.mipsasm.parser;

import me.zhanghai.mipsasm.assembler.Assembler;
import me.zhanghai.mipsasm.assembler.AssemblerException;
import me.zhanghai.mipsasm.assembler.AssemblyContext;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

class ParserTest {

    @Test
    void parse() throws FileNotFoundException, ParserException, AssemblerException {
        String file = "example/factorial.s";
        InputStream input = new FileInputStream(file);
        AssemblyContext context = new AssemblyContext();
        Parser.parse(input, context);
        Assembler.assemble(context);

    }
}