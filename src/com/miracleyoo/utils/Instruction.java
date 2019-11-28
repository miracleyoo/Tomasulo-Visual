package com.miracleyoo.utils;

public class Instruction {

    public String op ="";
    String dest ="";
    String src1 ="";
    String src2 = "";
    int exeTime = 0;
    int x = 0;
    int y = 0;


    //creates Instruction object for that includes op, srcRegs, execution time, ---WIP--- maybe coordinates on Diagram
    public Instruction(String op, String dest, String src1, String src2, int exeTime){
        this.op = op;
        this. dest = dest;
        this.src1 = src1;
        this.src2 = src2;
        this.exeTime = exeTime;
        //this.x = x;
        //this.y = y;
    }
}
