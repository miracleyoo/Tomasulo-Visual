package com.miracleyoo.utils;

public class Instruction {

    public String op ="";
    public String dest ="";
    public String src1 ="";
    public String src2 = "";
    public int exeTime = 0;
    public int currentClk = 0;
    public int startTime = 0;
    public String state;
    int x = 0;
    int y = 0;
    public int index = 0;


    //creates Instruction object used in Diagram for that includes op, destReg, srcRegs, currentClk, startTime, and absoluteIndex of instruction
    public Instruction(String op, String dest, String src1, String src2, String state, int currentClk, int startTime, int index){
        this.op = op;
        this. dest = dest;
        this.src1 = src1;
        this.src2 = src2;
        //this.exeTime = exeTime;
        this.currentClk = currentClk;
        this.startTime = startTime;
        this.state = state;
        this.index = index;
        //this.x = x;
        //this.y = y;
    }
}
