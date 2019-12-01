package com.miracleyoo.Logic;

import com.miracleyoo.utils.Instruction;

import java.util.*;
import java.util.Map;

public class MainLogic {
    private String[] AddOps = {"ADD","DADD","DADDU","ADDD","ADDDS","SUB","SUBS","SUBD","DSUB","DSUBU","SUBPS","SLT","SLTU","AND","OR","XOR","CVTDL"};
    private String[] MulOps = {"DMUL","DMULU","MULS","MULD","MULPS"};
    private String[] DivOps = {"DDIV","DDIVU","DIVS","DIVD","DIVPS"};
    private String[] IntOps = {"DADDI","DADDIU","SLTI","ANDI","ORI","XORI","DSLL","DSRL","DSRA","DSLLV","DSRLV","DSRAV"};
    private String[] SaveOps = {"SB","SH","SW","SD","SS","MTC0","MTC1","MFC0","MFC1"};
    private String[] LoadOps = {"LB","LH","LW","LD","LS","LBU","LHU","LWU"};
    private String[] BranchOps = {"BEQZ", "BENZ", "BEQ", "BNE", "J", "JR", "JAL", "JALR"};
    private String[] InstructionState = {"Issue", "EXE", "ExeEnd","WB", "End"};
    private String[] TypeNames = {"NOP", "HALT", "ADD", "INT", "DIV", "MUL", "LOAD", "SAVE", "BRA"};


    public static class OperandInfo
    {
        public String operand = ""; // Only the operand name, like ADDD, MULD
        public String inst = "";    // The whole instruction, like ADDD R1, R2, R3
        public String state = "";   // Current state. It can only be
        public int issue = 0;
        public int exeStart = 0;
        public int exeEnd = 0;
        public int writeBack = 0;
        public int currentStageCycleNum = 1;
        public int absoluteIndex = 0; // This is the line number of this instruction.
        public String DestReg = null;
        public String SourceReg1 = null;
        public String SourceReg2 = null;
    };

    private static OperandInfo tempOperandsInfo;

    public static class FloatRegTemplate{
        public float value= (float) 0.0;
        public Boolean ready = Boolean.FALSE;
        public int occupyInstId = 0;
    }

    public static class IntRegTemplate{
        public int value= 0;
        public Boolean ready = Boolean.FALSE;
        public int occupyInstId = 0;
    }

    public static class FUTemplate{
        public Boolean busy = Boolean.FALSE;
        public int occupyInstId = 0;
    }


    ///////////////////////////////////////////////////////////////////////////
    ////////////////   Most Important Global Parameters ///////////////////////
    ///////////////////////////////////////////////////////////////////////////

    // The current cycle number.
    public static int CycleNumCur = 0;

    // The index of the line of instruction which is going to be parsed
    public static int instructionLineCur = 0;

    // The number of Operand queue. Sharing opQueue for int and fp.
    public static int OpQueue = 10;//instr.length; //size of instruction array

    // All Integer Registers.
    public static IntRegTemplate[] IntRegs = new IntRegTemplate[32];

    // All Float Registers.
    public static FloatRegTemplate[] FloatRegs = new FloatRegTemplate[32];

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

    // All of the instruction lines in this file.
    public static List<String> InstructionFullList = new ArrayList<>();

    //public static String[] instr = {"lw", "sw", "lw", "FPadd", "FPmul", "FPdiv", "sw", "lw", "INTadd", "INTsub", "FPsub", "sw", "INTadd", "INTmul", "FPdiv"};
    //public static String[] instr = {"ld $R5,0($R4)", "add $R3,$R2,$R5", "sw $R3,0($R8)" , "sub $R4,$R3,$R5", "mul $R10,$R11,R12", "sw $10, 0($R11)"};
    public static String[] instr = {"ld $R5,0($R4)", "ld $R5,0($R4)", "ld $R5,0($R4)", "ld $R5,0($R4)", "ld $R5,0($R4)", "ld $R5,0($R4)", "ld $R5,0($R4)"};

    // Operand info structures. It's length equals to the number of Operand cells in Diagram.
    public static LinkedList<OperandInfo> OperandsInfoStation = new LinkedList<OperandInfo>();

    // Operand classify dictionary. Key:Value -> Operand:Class
    public static Map< String, String> OperandMapper = new HashMap<>();

    // Reservation stations definition
    public static FUTemplate[] LoadFUs = new FUTemplate[(int)architectureNum[0]];
    public static FUTemplate[] SaveFUs = new FUTemplate[(int)architectureNum[1]];
    public static FUTemplate[] IntFUs = new FUTemplate[(int)architectureNum[2]];
    public static FUTemplate[] AddFUs = new FUTemplate[(int)architectureNum[3]];
    public static FUTemplate[] MulFUs = new FUTemplate[(int)architectureNum[4]];
    public static FUTemplate[] DivFUs = new FUTemplate[(int)architectureNum[5]];

    public static Map< String, FUTemplate[]> Type2FUsMap = new HashMap<>();

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

    // Judge whether it is possible to issue a new instruction
    // 1. Check whether there are some free operation stations
    // 2. Check whether there are some free and corresponding FUs
    private Boolean judgeIssue() {
        Boolean flag = Boolean.FALSE;
        String type_ = OperandMapper.get(tempOperandsInfo.operand);

        // Check available reservation station at first. BRA, NOP, HALT don't need RS.
        if(type_.equals("BRA") || type_.equals("NOP") || type_.equals("HALT")){
            flag = Boolean.TRUE;
        }
        else {
            for (FUTemplate FUs : Type2FUsMap.get(type_)) {
                if (FUs.busy == Boolean.TRUE) {
                    flag = Boolean.TRUE;
                    break;
                }
            }
        }

        // If no corresponding RS available, return false.
        if(flag == Boolean.FALSE){
            return Boolean.FALSE;
        }

        // Check whether there are free OperandsInfoStation, if not, check whether the last Instruction is end.
        if (OperandsInfoStation.size()<OpQueue){
            return Boolean.TRUE;
        }
        else if(OperandsInfoStation.getLast().state.equals("End")){
            return Boolean.TRUE;
        }
        else {
            return Boolean.FALSE;
        }
    }

    // Judge whether it is available to start execution
    private Boolean judgeExe(){
        //
        return Boolean.FALSE;
    }

    // Judge whether it is available to write back
    private Boolean judgeWB(){
        //
        return Boolean.FALSE;
    }

    // Parse the next instruction and return a tempOperandsInfo
    private void parseInstruction(String operandLine){
        String operand, srcTemp, operandType, destinationReg;

        tempOperandsInfo = new OperandInfo();
        operandLine=operandLine.split(";")[0].trim();
        operand = operandLine.split("\\s+")[0];
        operand = operand.replace(".","").toUpperCase().trim();
        operandType = OperandMapper.get(operand);
        srcTemp = operandLine.split("\\s+")[1].toUpperCase().trim();

        tempOperandsInfo.operand = operand; ;
        tempOperandsInfo.DestReg = srcTemp.split(",")[0];
        tempOperandsInfo.SourceReg1 = srcTemp.split(",")[1];
        if(!operandType.equals("LOAD") && !operandType.equals("SAVE")) {
            tempOperandsInfo.SourceReg2 = srcTemp.split(",")[2];
        }
    }

    // Update the OperandsInfoStation(current Operands station infos)
    // 1. Put tempOperandsInfo in the right place
    // 2. Update the Issue value and state of newly placed member
    private void updateOperandsInfoStation(){
        if (OperandsInfoStation.size()>=OpQueue){
            OperandsInfoStation.removeLast();
        }
        OperandsInfoStation.addFirst(tempOperandsInfo);
        OperandsInfoStation.getFirst().issue = CycleNumCur;
        OperandsInfoStation.getFirst().state = InstructionState[0];
        OperandsInfoStation.getFirst().absoluteIndex = instructionLineCur;
        OperandsInfoStation.getFirst().inst = InstructionFullList.get(instructionLineCur);
    }

    // Sequentially check all of the items in the Operands station,
    // And do corresponding operation to them according to state
    private void checkAllOperandMember(){
        //
        for(int i=0; i<OperandsInfoStation.size(); i++){
            switch (OperandsInfoStation.get(i).state){
                case "Issue":
                    if (CycleNumCur - OperandsInfoStation.get(i).issue >= OperandsInfoStation.get(i).currentStageCycleNum){
                        if(judgeExe()){
                            OperandsInfoStation.get(i).state = InstructionState[1];
                            OperandsInfoStation.get(i).exeStart = CycleNumCur;
                            OperandsInfoStation.get(i).currentStageCycleNum = 1;
                        }
                    }
                    break;
                case "EXE":
                    if (CycleNumCur - OperandsInfoStation.get(i).exeStart >= OperandsInfoStation.get(i).currentStageCycleNum){
                        if(judgeExe()){
                            OperandsInfoStation.get(i).state = InstructionState[2];
                            OperandsInfoStation.get(i).exeEnd = CycleNumCur;
                            ExeOps(i);
                        }
                    }
                    break;
                case "ExeEnd":
                    if(judgeWB()){
                        OperandsInfoStation.get(i).state = InstructionState[3];
                        OperandsInfoStation.get(i).writeBack = CycleNumCur;
                        OperandsInfoStation.get(i).currentStageCycleNum = 1;
                    }
                case "WB":
                    if (CycleNumCur - OperandsInfoStation.get(i).writeBack >= OperandsInfoStation.get(i).currentStageCycleNum){
                        OperandsInfoStation.get(i).state = InstructionState[4];
                    }
                    break;
                case "End":
                    break;
            }
        }
    }


    // The operations applied to an instruction which is in issue state
    private void IssueOps(){
        String regName;
        int index_;
        regName = OperandsInfoStation.getFirst().DestReg;
        index_ =  Integer.parseInt(regName.replaceAll("\\D+",""));
        if(regName.startsWith("R")){
            IntRegs[index_].ready=Boolean.FALSE;
            IntRegs[index_].occupyInstId = OperandsInfoStation.getFirst().absoluteIndex;
        }
        else if(regName.startsWith("F")){
            FloatRegs[index_].ready=Boolean.FALSE;
            FloatRegs[index_].occupyInstId = OperandsInfoStation.getFirst().absoluteIndex;
        }
    }

    // The operations applied to an instruction which is in execute state
    private void ExeOps(int operandInfoIndex){
        String operandType = OperandMapper.get(OperandsInfoStation.get(operandInfoIndex).operand);
        switch(operandType)
        {
            case "NOP" :
                OperandsInfoStation.get(operandInfoIndex).currentStageCycleNum = 1;
                OpsNOP();
            case "HALT" :
                OperandsInfoStation.get(operandInfoIndex).currentStageCycleNum = 0;
                OpsHALT();
                break;
            case "DIV" :
                OperandsInfoStation.get(operandInfoIndex).currentStageCycleNum = (int) architectureCycle[5];
                OpsDIV();
                break;
            case "MUL" :
                OperandsInfoStation.get(operandInfoIndex).currentStageCycleNum = (int) architectureCycle[4];
                OpsMUL();
                break;
            case "LOAD" :
                OperandsInfoStation.get(operandInfoIndex).currentStageCycleNum = (int) architectureCycle[0];
                OpsLOAD();
                break;
            case "SAVE":
                OperandsInfoStation.get(operandInfoIndex).currentStageCycleNum = (int) architectureCycle[1];
                OpsSAVE();
                break;
            case "BRA":
                OperandsInfoStation.get(operandInfoIndex).currentStageCycleNum = 1;
                OpsBRANCH();
                break;
            default :
                if(operandType.equals("ADD")){
                    OperandsInfoStation.get(operandInfoIndex).currentStageCycleNum = (int) architectureCycle[3];
                }
                else{
                    OperandsInfoStation.get(operandInfoIndex).currentStageCycleNum = (int) architectureCycle[2];
                }
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
    public void parseStep(){
        Boolean issueAvailable;
        parseInstruction(InstructionFullList.get(instructionLineCur));
        issueAvailable = judgeIssue();
        if (issueAvailable){
            updateOperandsInfoStation();
            instructionLineCur++;
        }
        checkAllOperandMember();
//        in = new Instruction(operandType, destinationReg, src[0], src[1], 3, 0); //example
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

        for (int i=0; i< IntRegs.length; i++){
            IntRegs[i] = new IntRegTemplate();
        }

        for (int i=0; i< FloatRegs.length; i++){
            FloatRegs[i] = new FloatRegTemplate();
        }

        for (int i=0; i< AddFUs.length; i++){
            AddFUs[i] = new FUTemplate();
        }

        for (int i=0; i< MulFUs.length; i++){
            MulFUs[i] = new FUTemplate();
        }

        for (int i=0; i< DivFUs.length; i++){
            DivFUs[i] = new FUTemplate();
        }

        for (int i=0; i< SaveFUs.length; i++){
            SaveFUs[i] = new FUTemplate();
        }

        for (int i=0; i< LoadFUs.length; i++){
            LoadFUs[i] = new FUTemplate();
        }

        for (int i=0; i< IntFUs.length; i++){
            IntFUs[i] = new FUTemplate();
        }

        Type2FUsMap.put("ADD", AddFUs);
        Type2FUsMap.put("MUL", MulFUs);
        Type2FUsMap.put("DIV", DivFUs);
        Type2FUsMap.put("SAVE", SaveFUs);
        Type2FUsMap.put("LOAD", LoadFUs);
        Type2FUsMap.put("INT", IntFUs);
    }

    //clock set function --> Main logic updates every clock cycle
    public void runLogic(int clk){
        CycleNumCur = clk;

        if(clk < instr.length) {
            parseStep();//instr[clk]);

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