package com.miracleyoo.Logic;

import java.util.HashMap;
import java.util.Map;

public class MainLogic {
    private String[] AddOps = {"ADD","DADD","DADDU","ADDD","ADDDS","SUB","SUBS","SUBD","DSUB","DSUBU","SUBPS","SLT","SLTU","AND","OR","XOR","CVTDL"};
    private String[] MulOps = {"DMUL","DMULU","MULS","MULD","MULPS"};
    private String[] DivOps = {"DDIV","DDIVU","DIVS","DIVD","DIVPS"};
    private String[] IntOps = {"DADDI","DADDIU","SLTI","ANDI","ORI","XORI","DSLL","DSRL","DSRA","DSLLV","DSRLV","DSRAV"};
    private String[] SaveOps = {"SB","SH","SW","SD","SS","MTC0","MTC1","MFC0","MFC1"};
    private String[] LoadOps = {"LB","LH","LW","LD","LS","LBU","LHU","LWU"};
    public String[] BranchOps = {"BEQZ", "BENZ", "BEQ", "BNE", "J", "JR", "JAL", "JALR"};

    public static int[] IntRegs = new int[32]; // Integer Registers
    public static float[] FloatRegs = new float[32]; // Float Registers

    public static Map< String, String> OperandMapper = new HashMap<>();

    private void mapListItems (String[]inputList, String listName){
        for (String operand : inputList) {
            OperandMapper.put(operand, listName);
        }
    }

    private void OpsNOP(){}

    private void OpsHALT(){}

    private void OpsADD(){}

    private void OpsSUB(){}

    private void OpsSLT(){}

    private void OpsLogic(){
        // AND, OR, XOR
    }

    private void OpsCVT(){
        // CVTDL: to double precision, here we don't need to care. Leave the function empty.
    }

    private void OpsMUL(){}

    private void OpsDIV(){}

    private void OpsBRANCH(){}

    private void OpsLOAD(){}

    private void OpsSAVE(){
        // Two types, normal save and directly save to register like MTC0
    }

    public void parseStep(String operandLine){
        String destinationReg;
        String operand;
        String operandType;

        operandLine=operandLine.split(";")[0].trim();
        operand = operandLine.split("[ \t]]+")[0];
        operand = operand.replace(".","").toUpperCase().trim();
        operandType = OperandMapper.get(operand);
        switch(operandType)
        {
            case "NOP" :
                OpsNOP();
            case "HALT" :
                OpsHALT();
                break;
            case "DIV" :
                OpsDIV();
                break;
            case "MUL" :
                OpsMUL();
                break;
            case "LOAD" :
                OpsLOAD();
                break;
            case "SAVE":
                OpsSAVE();
                break;
            case "BRA":
                OpsBRANCH();
                break;
            default :
                if(operandType.contains("ADD")){
                    OpsADD();
                }
                else if(operandType.contains("SUB")){
                    OpsSUB();
                }
                else if(operandType.contains("SLT")){
                    OpsSLT();
                }
                else if(operandType.contains("CVT")){
                    OpsCVT();
                }
                else if(operandType.contains("AND") || operandType.contains("OR")){
                    OpsLogic();
                }
        }
    }

    public MainLogic() {
        mapListItems(AddOps, "ADD");
        mapListItems(MulOps, "MUL");
        mapListItems(DivOps, "DIV");
        mapListItems(IntOps, "INT");
        mapListItems(SaveOps, "SAVE");
        mapListItems(LoadOps, "LOAD");
        mapListItems(BranchOps, "BRA");
        mapListItems(new String[]{"NOP"}, "NOP");
        mapListItems(new String[]{"HALT"}, "HALT");
    }
}
