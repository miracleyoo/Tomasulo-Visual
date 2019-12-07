package com.miracleyoo.utils;

public class Instruction {

    public String op ="";
    public String dest ="";
    public String src1 ="";
    public String src2 = "";
    public int exeTime = 0;
    public int currentClk = 0;
    public String state;
    int x = 0;
    int y = 0;


    //creates Instruction object for that includes op, srcRegs, execution time
    public Instruction(String op, String dest, String src1, String src2, String state, int currentClk){
        this.op = op;
        this. dest = dest;
        this.src1 = src1;
        this.src2 = src2;
        //this.exeTime = exeTime;
        this.currentClk = currentClk;
        this.state = state;
        //this.x = x;
        //this.y = y;
    }
}
