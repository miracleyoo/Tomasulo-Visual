package com.miracleyoo.Logic;

import com.miracleyoo.utils.Instruction;

import java.util.HashMap;
import java.util.Map;

public class MainLogic {
    private String[] AddOps = {"ADD","DADD","DADDU","ADDD","ADDDS","SUB","SUBS","SUBD","DSUB","DSUBU","SUBPS","SLT","SLTU","AND","OR","XOR","CVTDL"};
    private String[] MulOps = {"DMUL","DMULU","MULS","MULD","MULPS"};
    private String[] DivOps = {"DDIV","DDIVU","DIVS","DIVD","DIVPS"};
    private String[] IntOps = {"DADDI","DADDIU","SLTI","ANDI","ORI","XORI","DSLL","DSRL","DSRA","DSLLV","DSRLV","DSRAV"};
    private String[] SaveOps = {"SB","SH","SW","SD","SS","MTC0","MTC1","MFC0","MFC1"};
    private String[] LoadOps = {"LB","LH","LW","LD","LS","LBU","LHU","LWU"};
    private String[] BranchOps = {"BEQZ", "BENZ", "BEQ", "BNE", "J", "JR", "JAL", "JALR"};
    private String[] InstructionState = {"Issue", "EXE", "WB", "End"};


    public class OperandInfo
    {
        public String operand = ""; // Only the operand name, like ADDD, MULD
        public String inst = "";    // The whole instruction, like ADDD R1, R2, R3
        public String state = "";   // Current state. It can only be
        public int issue = 0;
        public int exeStart = 0;
        public int exeEnd = 0;
        public int writeBack = 0;
        //public String DestReg = null;
        //public String SourceReg1 = null;
        //public String SourceReg2 = null;
    };

    private static OperandInfo tempOperandsInfo;

    ///////////////////////////////////////////////////////////////////////////
    ////////////////   Most Important Global Parameters ///////////////////////
    ///////////////////////////////////////////////////////////////////////////

    // The current cycle number.
    public static int CycleNumCur = 0;

    // All Integer Registers.
    public static int[] IntRegs = new int[32];

    // All Float Registers.
    public static float[] FloatRegs = new float[32];

    // All statistics information.
    public static int[] statisticsInfo = new int[9];

    // Architecture parameters' value. "ld, sd, int, fpAdd, fpMul, fpDiv"
    public static long[] architectureNum = new long[]{6, 6, 5, 4, 4, 3}; //may need to change this back to type long if bugs

    // Architecture parameters' max value.
    public static long[] architectureNumMax = new long[]{9, 9, 9, 9, 9, 9};

    // Architecture cycle numbers' value.
    public static long[] architectureCycle = new long[]{10, 10, 4, 7, 24, 5};

    // Architecture cycle numbers' max value.
    public static long[] architectureCycleMax = new long[]{100,100,100,100,100,100};

    // How many cycles to proceed when "Execute multiple cycle" button is pressed.
    public static long multiStepNum = 3;

    // The number of Operand queue. Sharing opQueue for int and fp.
    //public static String[] instr = {"lw", "sw", "lw", "FPadd", "FPmul", "FPdiv", "sw", "lw", "INTadd", "INTsub", "FPsub", "sw", "INTadd", "INTmul", "FPdiv"};
    //public static String[] instr = {"ld $R5,0($R4)", "add $R3,$R2,$R5", "sw $R3,0($R8)" , "sub $R4,$R3,$R5", "mul $R10,$R11,R12", "sw $10, 0($R11)"};
    public static String[] instr = {"ld $R5,0($R4)", "ld $R5,0($R4)", "ld $R5,0($R4)", "ld $R5,0($R4)", "ld $R5,0($R4)", "ld $R5,0($R4)", "ld $R5,0($R4)"};
    public static int OpQueue = instr.length; //size of instruction array


    // Operand info structures. It's length equals to the number of Operand cells in Diagram.
    public static OperandInfo[] OperandsInfoCur = new OperandInfo[OpQueue];

    // Cycle table show index list.
    public static int[] cycleTableIndex = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};

    // The number of current in-stack operands
    public static int cycleTableItemNum = 0;

    // Operand classify dictionary. Key:Value -> Operand:Class
    public static Map< String, String> OperandMapper = new HashMap<>();

    ///////////////////////////////////////////////////////////////////////////
    ///////////////   Most Important Global Parameters End ////////////////////
    ///////////////////////////////////////////////////////////////////////////

    Instruction in;
    public static Instruction blankInstr = new Instruction("", "", "", "", -1, 0);
    Instruction cdb = blankInstr; //holds instruction that has finished executing and pushes it to destination/waiting RS
    public static Instruction[] ldBuffer = new Instruction[(int)architectureNum[0]];

    // Push the list item to corresponding dictionary Key:Value pair
    private void mapListItems (String[]inputList, String listName){
        for (String operand : inputList) {
            OperandMapper.put(operand, listName);
        }
    }



    private void updateCycleTableIndex(){
        if (cycleTableItemNum<OpQueue){
            for(int i=cycleTableItemNum; i>0; i--){
                cycleTableIndex[i]=cycleTableIndex[i-1];
            }
            cycleTableIndex[cycleTableItemNum] = cycleTableItemNum;
        }
        else{
            cycleTableIndex[cycleTableItemNum%OpQueue] = cycleTableItemNum%OpQueue;
        }
    }

    // Judge whether it is possible to issue a new instruction
    // 1. Check whether there are some free operation stations
    // 2. Check whether there are some free and corresponding FUs
    private void judgeIssue() {
        //
    }

    // Parse the next instruction and return a tempOperandsInfo
    private void parseInstruction(String operandLine){
        String operand, srcTemp, operandType, destinationReg;
        String[] src = new String[2];
        operandLine=operandLine.split(";")[0].trim();
        operand = operandLine.split("\\s+")[0];
        operand = operand.replace(".","").toUpperCase().trim();
        srcTemp = operandLine.split("\\s+")[1].toUpperCase().trim();

        System.out.println(operandLine + " ");
        //System.out.println(srcTemp);

        operandType = OperandMapper.get(operand);
        System.out.println("Operand type: " + operandType);

        destinationReg = srcTemp.split(",")[0];
        src[0] = srcTemp.split(",")[1];
        if(!operandType.equals("LOAD") || !operandType.equals("SAVE")) {
            src[1] = srcTemp.split(",")[2]; //this won't exist for ld/sw!
        }
    }

    // Update the OperandsInfoCur(current Operands station infos)
    // 1. Put tempOperandsInfo in the right place
    // 2. Update the Issue value and state of newly placed member
    private void updateOperandsInfoCur(){
        if (cycleTableItemNum<OpQueue){
            for(int i=cycleTableItemNum; i>0; i++){
//                cycleTableIndex[]
            }
            cycleTableIndex[cycleTableItemNum] = cycleTableItemNum;
        }
        else {

        }
    }

    // Sequentially check all of the items in the Operands station,
    // And do corresponding operation to them according to state
    private void checkAllOperandMember(){
        //
    }

    // The operations applied to an instruction which is in issue state
    private void IssueOps(){
        //
    }

    // The operations applied to an instruction which is in execute state
    private void ExeOps(){
        //
    }

    // The operations applied to an instruction which is in write back state
    private void WBOps(){
        //
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

    // The core logic. Called for each cycle update.
    public void parseStep(String operandLine){
        String destinationReg;
        String srcTemp;
        String[] src = new String[2];
        String operand;
        String operandType;

        // Update the Cycle Table Index to make Cycle Table run correctly.
        updateCycleTableIndex();

        operandLine=operandLine.split(";")[0].trim();
//        operand = operandLine.split("[ \t]]+")[0];
        operand = operandLine.split("\\s+")[0];
        operand = operand.replace(".","").toUpperCase().trim();

        srcTemp = operandLine.split("\\s+")[1];
        //srcTemp = srcTemp.replace("\\s+", "");
        srcTemp = srcTemp.toUpperCase().trim();



        System.out.println(operandLine);
        System.out.println(srcTemp);
        srcTemp = operandLine.split("\\s+")[1].toUpperCase().trim();

        System.out.println(operandLine + " ");
        //System.out.println(srcTemp);


        operandType = OperandMapper.get(operand);
        System.out.println("Operand type: " + operandType);

        destinationReg = srcTemp.split(",")[0];
        src[0] = srcTemp.split(",")[1];
        if(operandType.equals("LOAD") || operandType.equals("SAVE")) {
            src[1] = ""; //this won't exist for ld/sw!
        }
        else{
            src[1] = srcTemp.split(",")[2];
        }

        System.out.println("destination: " + destinationReg);
        System.out.println("src1: " + src[0]);
        System.out.println("src2: " + src[1]);

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
        in = new Instruction(operandType, destinationReg, src[0], src[1], 3, 0); //example
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

    //clock set function --> Main logic updates every clock cycle
    public void runLogic(int clk){
        CycleNumCur = clk;

        if(clk < instr.length) {
            parseStep(instr[clk]);

            switch(in.op){
                case "LOAD":
                //push to load buffer on diagram if no structural hazard!
                System.out.println("ld detected!");
                for (int x = 0; x < ldBuffer.length; x++) {
                    if (ldBuffer[x] == null || ldBuffer[x] == blankInstr) {
                        ldBuffer[x] = in;
                        System.out.println("ldBuffer[" + x + "] " + ldBuffer[x].op);
                        break;
                    } else {
                        System.out.println("Structural hazard in ldBuffer detected! Stalling issue");
                    }
                }
                break;

                case "ADD":
                    System.out.println("fpAdd detected");
                break;

                case "MUL":
                    System.out.println("fpMul detected");
                break;

                case "DIV":
                    System.out.println("fpDiv detected");
                break;

                case "INT":
                    System.out.println("intOp detected");
                break;
            }
        }

        //---ldBuffer-- run execution cycles once inside ldBuffer
        for (int x = 0; x < ldBuffer.length; x++) {
            if (ldBuffer[x] != null && ldBuffer[x] != blankInstr) {
                if(ldBuffer[x].currentClk < ldBuffer[x].exeTime) {
                    //This is gonna get messy with multiple clock steps...
                    ldBuffer[x].currentClk = ldBuffer[x].currentClk + clk; //increment every clock the instruction is in the ldBuffer
                }

                else{
                    //put on cdb if clear
                    if(cdb == null || cdb == blankInstr){
                        cdb = ldBuffer[x]; //move completed instruction onto CDB and proceed with WB

                        //shift all elements in ldBuffer down 1 index after a ld completes
                        Instruction templd = blankInstr;
                        for(int z = 1; z < ldBuffer.length-1; z++){
                            ldBuffer[z-1] = ldBuffer[z];
                        }
                    }
                }
            }
        }
    } //---end method---
}

/*
To-Do
Run full cycle with ld instr to check logic
Display to diagram!

The rest of the FUs have to be implemented once ld works!
 */