//////////////////////////////////////////////////////////////////////
//////////////////  Main Workflow TODO   /////////////////////////////
//////////////////////////////////////////////////////////////////////

//          Initialize data structures and dictionaries
//                            ↓
//                Parse the next instruction
//                            ↓
//               Judge whether it can be issued now
//                            ↓
//  Update the Information for the newly issued instruction (like issue cycle)
//                            ↓
//  Sequentially check all of the instructions in the Operands station now,
//         And do corresponding operation to them according to state
//          ↓               ↓                   ↓               ↓
// | check can exe | check can exeEnd |  check can WB |  check can end |
//          ↓                                   ↓               ↓
//          ↓                                 ExeOps          EndOps
//          ↓
//  OpsNOP / OpsADD / OpsSUB / OpsMUL / OpsDIV / ......
//                            ↓
//                         cycle ++

//////////////////////////////////////////////////////////////////////
//////////////////  Main Workflow TODO End  //////////////////////////
//////////////////////////////////////////////////////////////////////

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

    // A data structure which store all data by parsing an instruction
    // As well as cycle data during the Tomasulo execution.
    public static class OperandInfo
    {
        public String operand = ""; // Only the operand name, like ADDD, MULD
        public String inst = "";    // The whole instruction, like ADDD R1, R2, R3
        public String state = "";   // Current state. It can only be

        // Tomasulo execution cycle data
        public int issue = 0;
        public int exeStart = 0;
        public int exeEnd = 0;
        public int writeBack = 0;
        public int currentStageCycleNum = 1;
        public int absoluteIndex = 0; // This is the line number of this instruction.

        // Instruction related registers, jump lables or where to jump
        public String label = null;     // The label in this line.
        public String jumpLabel = null; // Jump to label "X". For branch operands.
        public String DestReg = null;
        public String SourceReg1 = null;
        public String SourceReg2 = null;

        // If there is an numerical data in the registers' place, please store
        // it in ValueReg1 or ValueReg2
        public Number ValueReg1 = null;
        public Number ValueReg2 = null;
    };

    private static OperandInfo tempOperationInfo;

    // Struct for float registers list
    public static class FloatRegTemplate{
        public float value= (float) 0.0;
        public Boolean ready = Boolean.FALSE;
        public int occupyInstId = 0;
    }

    // Struct for integer registers list
    public static class IntRegTemplate{
        public int value= 0;
        public Boolean ready = Boolean.FALSE;
        public int occupyInstId = 0;
    }

    // Struct for all kinds of functional units
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
    public static LinkedList<OperandInfo> OperationInfoStation = new LinkedList<OperandInfo>();

    //////////////////////////////////////////////////////////////////////
    //////////////////        TODO       /////////////////////////////////
    //////////////////////////////////////////////////////////////////////

    // Keep track of the new change of OperandsInfoStation element

    //////////////////////////////////////////////////////////////////////
    //////////////////       END TODO       //////////////////////////////
    //////////////////////////////////////////////////////////////////////
    public static LinkedList<OperandInfo> OperationInfoFull = new LinkedList<OperandInfo>();


    // Operand classify dictionary. Key:Value -> Operand:Class
    public static Map< String, String> OperationMapper = new HashMap<>();

    // Reservation stations definition
    public static FUTemplate[] LoadFUs = new FUTemplate[(int)architectureNum[0]];
    public static FUTemplate[] SaveFUs = new FUTemplate[(int)architectureNum[1]];
    public static FUTemplate[] IntFUs = new FUTemplate[(int)architectureNum[2]];
    public static FUTemplate[] AddFUs = new FUTemplate[(int)architectureNum[3]];
    public static FUTemplate[] MulFUs = new FUTemplate[(int)architectureNum[4]];
    public static FUTemplate[] DivFUs = new FUTemplate[(int)architectureNum[5]];

    public static Map< String, FUTemplate[]> Type2FUsMap = new HashMap<>(); // Find corresponding FUs Struct List using Map

    public static Map< String, Number> dataMap = new HashMap<>(); // Counters for data and code


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
            OperationMapper.put(operand, listName);
        }
    }

    // Judge whether a string toCheckValue is in the string list arr
    private static Boolean contains(String[] arr, String toCheckValue)
    {
        for (String element : arr) {
            if (element.equals(toCheckValue)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }


    // Judge whether it is possible to issue a new instruction
    // 1. Check whether there are some free operation stations
    // 2. Check whether there are some free and corresponding FUs
    private Boolean judgeIssue() {
        Boolean flag = Boolean.FALSE;
        String type_ = OperationMapper.get(tempOperationInfo.operand);

        // Check available reservation station at first. BRA, NOP, HALT don't need RS.
        if(type_.equals("BRA") || type_.equals("NOP") || type_.equals("HALT")){
            flag = Boolean.TRUE;
        }
        else {
            for (FUTemplate FUs : Type2FUsMap.get(type_)) {
                if (FUs.busy == Boolean.FALSE) {
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
        if (OperationInfoStation.size()<OpQueue){
            return Boolean.TRUE;
        }
        else if(OperationInfoStation.getLast().state.equals("End")){
            return Boolean.TRUE;
        }
        else {
            return Boolean.FALSE;
        }
    }


    //////////////////////////////////////////////////////////////////////
    //////////////////        TODO       /////////////////////////////////
    //////////////////////////////////////////////////////////////////////

    // Judge

    //////////////////////////////////////////////////////////////////////
    //////////////////       END TODO       //////////////////////////////
    //////////////////////////////////////////////////////////////////////

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

        tempOperationInfo = new OperandInfo();
        operandLine=operandLine.split(";")[0].trim();
        tempOperationInfo.inst = operandLine;

        // There can be some label in front of a instruction, like `start: xxx xx xxx `
        if (operandLine.split(":").length>1){
            tempOperationInfo.label = operandLine.split(":")[0];
            operandLine = operandLine.split(":")[1].trim();
        }

        String[] separateEmpty = operandLine.split("\\s+");
        operand = separateEmpty[0].replace(".","").toUpperCase().trim();
        operandType = OperationMapper.get(operand);

        tempOperationInfo.operand = operand;
        if (separateEmpty.length<=1){ // HALT, NOP
            return;
        }
        else{
            if(operandType.equals("BRA")){
                tempOperationInfo.jumpLabel = separateEmpty[1].trim();
                return;
            }

            srcTemp = separateEmpty[1].toUpperCase().trim();
            String[] regParts = srcTemp.split(",");
            tempOperationInfo.DestReg = regParts[0];

            if(regParts[0].toUpperCase().startsWith("R") || regParts[0].toUpperCase().startsWith("F")){
                tempOperationInfo.DestReg = regParts[0].trim().toUpperCase();
            }
            else{
                throw new AssertionError("Destination Register wrong");
            }

            if(contains(new String[]{"LOAD", "SAVE"}, operandType)) {
                String[] address_ = regParts[1].split("[(]");
                address_[1] = address_[1].replaceAll("[)]","");

                // Split things like 4(R1), CONTROL(r0)
                if (address_[0].replaceAll("\\d+","").length() == 0){
                    tempOperationInfo.ValueReg1 = Integer.parseInt(address_[0]);
                }
                else{
                    //////////////////////////////////////////////////////////////////////
                    //////////////////        TODO       /////////////////////////////////
                    //////////////////////////////////////////////////////////////////////

                    // Here we have not consider the case that the load or save use an
                    // Address from .data part. After the completion of data parser in
                    // ParseFile.java(Already written TODO there too), we need to come
                    // back to finish this part. Temporarily use 0 here.

                    //////////////////////////////////////////////////////////////////////
                    //////////////////       END TODO       //////////////////////////////
                    //////////////////////////////////////////////////////////////////////
                    tempOperationInfo.ValueReg1 = 0;
                }
                // When parsing LOAD or SAVE operands whose address is like ADD1(ADD2), ADD1 will be put into ValueReg1 or SourceReg1
                if(address_[1].toUpperCase().startsWith("R") || address_[1].toUpperCase().startsWith("F")){
                    tempOperationInfo.SourceReg2 = address_[1];
                }
                else if(address_[1].trim().replaceAll("\\d+","").length() == 0){
                    tempOperationInfo.ValueReg2 = Integer.parseInt(address_[1]);
                }
                else{
                    //////////////////////////////////////////////////////////////////////
                    //////////////////        TODO       /////////////////////////////////
                    //////////////////////////////////////////////////////////////////////

                    // Same for here, some destination register is a variable in .data

                    //////////////////////////////////////////////////////////////////////
                    //////////////////       END TODO       //////////////////////////////
                    //////////////////////////////////////////////////////////////////////
                    throw new AssertionError("Destination Register wrong");
                }
                return;
            }

            if(regParts[1].toUpperCase().startsWith("R") || regParts[1].toUpperCase().startsWith("F")){
                tempOperationInfo.SourceReg1 = regParts[1];
            }
            else if(regParts[1].trim().replaceAll("\\d+","").length() == 0){
                tempOperationInfo.ValueReg1 = Integer.parseInt(regParts[1]);
            }
            else{
                //////////////////////////////////////////////////////////////////////
                //////////////////        TODO       /////////////////////////////////
                //////////////////////////////////////////////////////////////////////

                // Same as above

                //////////////////////////////////////////////////////////////////////
                //////////////////       END TODO       //////////////////////////////
                //////////////////////////////////////////////////////////////////////
                throw new AssertionError("Destination Register wrong");
            }

            if(regParts[2].toUpperCase().startsWith("R") || regParts[2].toUpperCase().startsWith("F")){
                tempOperationInfo.SourceReg2 = regParts[2];
            }
            else if(regParts[2].trim().replaceAll("\\d+","").length() == 0){
                tempOperationInfo.ValueReg2 = Integer.parseInt(regParts[2]);
            }
            else{
                //////////////////////////////////////////////////////////////////////
                //////////////////        TODO       /////////////////////////////////
                //////////////////////////////////////////////////////////////////////

                // Same as above

                //////////////////////////////////////////////////////////////////////
                //////////////////       END TODO       //////////////////////////////
                //////////////////////////////////////////////////////////////////////
                throw new AssertionError("Destination Register wrong");
            }
        }
    }

    // Update the OperandsInfoStation(current Operands station infos)
    // 1. Put tempOperandsInfo in the right place
    // 2. Update the Issue value and state of newly placed member
    private void updateOperandsInfoStation(){
        if (OperationInfoStation.size()>=OpQueue){
            OperationInfoStation.removeLast();
        }
        OperationInfoStation.addFirst(tempOperationInfo);
        OperationInfoStation.getFirst().issue = CycleNumCur;
        OperationInfoStation.getFirst().state = InstructionState[0];
        OperationInfoStation.getFirst().absoluteIndex = instructionLineCur;

        OperationInfoFull.addLast(OperationInfoStation.getFirst());
//        OperandsInfoStation.getFirst().inst = InstructionFullList.get(instructionLineCur);
    }

    // Sequentially check all of the items in the Operands station,
    // And do corresponding operation to them according to state
    private void checkAllOperandMember(){
        for(int i = 0; i< OperationInfoStation.size(); i++){
            switch (OperationInfoStation.get(i).state){
                case "Issue":
                    if (CycleNumCur - OperationInfoStation.get(i).issue >= OperationInfoStation.get(i).currentStageCycleNum){
                        if(judgeIssue()){
                            OperationInfoStation.get(i).state = InstructionState[1];
                            OperationInfoStation.get(i).exeStart = CycleNumCur;
                            OperationInfoStation.get(i).currentStageCycleNum = 1;
                            IssueOps();
                        }
                    }
                    break;
                case "EXE":
                    if (CycleNumCur - OperationInfoStation.get(i).exeStart >= OperationInfoStation.get(i).currentStageCycleNum){
                        if(judgeExe()){
                            OperationInfoStation.get(i).state = InstructionState[2];
                            OperationInfoStation.get(i).exeEnd = CycleNumCur;
                            ExeOps(i);
                        }
                    }
                    break;
                case "ExeEnd":
                    if(judgeWB()){
                        OperationInfoStation.get(i).state = InstructionState[3];
                        OperationInfoStation.get(i).writeBack = CycleNumCur;
                        OperationInfoStation.get(i).currentStageCycleNum = 1;
                    }
                case "WB":
                    if (CycleNumCur - OperationInfoStation.get(i).writeBack >= OperationInfoStation.get(i).currentStageCycleNum){
                        OperationInfoStation.get(i).state = InstructionState[4];
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
        regName = OperationInfoStation.getFirst().DestReg;
        index_ =  Integer.parseInt(regName.replaceAll("\\D+",""));
        if(regName.toUpperCase().startsWith("R")){
            IntRegs[index_].ready=Boolean.FALSE;
            IntRegs[index_].occupyInstId = OperationInfoStation.getFirst().absoluteIndex;
        }
        else if(regName.toUpperCase().startsWith("F")){
            FloatRegs[index_].ready=Boolean.FALSE;
            FloatRegs[index_].occupyInstId = OperationInfoStation.getFirst().absoluteIndex;
        }
    }

    //////////////////////////////////////////////////////////////////////
    //////////////////        TODO       /////////////////////////////////
    //////////////////////////////////////////////////////////////////////

    // Execution part. This function will be applied to every instruction
    // in the reservation station at the end of its EXE state when user
    // click the step button. Some operations related to registers and
    // data availability may need to be added here.

    //////////////////////////////////////////////////////////////////////
    //////////////////       END TODO       //////////////////////////////
    //////////////////////////////////////////////////////////////////////

    // The operations applied to an instruction which is in execute state
    private void ExeOps(int operandInfoIndex){
        String operandType = OperationMapper.get(OperationInfoStation.get(operandInfoIndex).operand);
        switch(operandType)
        {
            case "NOP" :
                OperationInfoStation.get(operandInfoIndex).currentStageCycleNum = 1;
                OpsNOP();
            case "HALT" :
                OperationInfoStation.get(operandInfoIndex).currentStageCycleNum = 0;
                OpsHALT();
                break;
            case "DIV" :
                OperationInfoStation.get(operandInfoIndex).currentStageCycleNum = (int) architectureCycle[5];
                OpsDIV();
                break;
            case "MUL" :
                OperationInfoStation.get(operandInfoIndex).currentStageCycleNum = (int) architectureCycle[4];
                OpsMUL();
                break;
            case "LOAD" :
                OperationInfoStation.get(operandInfoIndex).currentStageCycleNum = (int) architectureCycle[0];
                OpsLOAD();
                break;
            case "SAVE":
                OperationInfoStation.get(operandInfoIndex).currentStageCycleNum = (int) architectureCycle[1];
                OpsSAVE();
                break;
            case "BRA":
                OperationInfoStation.get(operandInfoIndex).currentStageCycleNum = 1;
                OpsBRANCH();
                break;
            default :
                if(operandType.equals("ADD")){
                    OperationInfoStation.get(operandInfoIndex).currentStageCycleNum = (int) architectureCycle[3];
                }
                else{
                    OperationInfoStation.get(operandInfoIndex).currentStageCycleNum = (int) architectureCycle[2];
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

    //////////////////////////////////////////////////////////////////////
    //////////////////        TODO       /////////////////////////////////
    //////////////////////////////////////////////////////////////////////

    // WB part. Called at the end of WB state. Please set the occupied
    // registers free here and update the data computed.

    //////////////////////////////////////////////////////////////////////
    //////////////////       END TODO       //////////////////////////////
    //////////////////////////////////////////////////////////////////////

    // The operations applied to an instruction which is in write back state
    private void WBOps(){
        //
    }


    //////////////////////////////////////////////////////////////////////
    //////////////////        TODO       /////////////////////////////////
    //////////////////////////////////////////////////////////////////////

    // Operations going to be executed when this type of instructions are
    // called. Please refer to mips instruction guide to make them functional.
    // Detailed data type may not need to be considered except the big
    // difference between integer and float.

    //////////////////////////////////////////////////////////////////////
    //////////////////       END TODO       //////////////////////////////
    //////////////////////////////////////////////////////////////////////

    private void OpsNOP(){}

    private void OpsHALT(){}

    private void OpsADD(){
//        OperandMapper.get(OperandsInfoStation.get(i).operand);
//        OperandsInfoStation.get(i).DestReg=OperandsInfoStation.get(i).SourceReg1+OperandsInfoStation.get(i).SourceReg2;
//        int index = Integer.parseInt("R11".replaceAll("\D+",""));
//        FloatRegs[index].value = OperandsInfoStation.get(i).issue;
//        FloatRegs[index].ready = Boolean.TRUE;
    }

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
        CycleNumCur ++;
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



    //////////////////////////////////////////////////////////////////////
    //////////////////        Attention       ////////////////////////////
    //////////////////////////////////////////////////////////////////////

    // Unvalid part. This part is build for test. Please ignore it.

    //////////////////////////////////////////////////////////////////////
    //////////////////       END Attention       /////////////////////////
    //////////////////////////////////////////////////////////////////////

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