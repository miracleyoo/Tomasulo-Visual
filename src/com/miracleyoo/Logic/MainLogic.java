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
import jdk.dynalink.Operation;

import java.util.*;
import java.util.Map;

/*
Need to create temporary src regs for every instruction so that WAW and WAR aren't an issue!
 */

public class MainLogic {
    private String[] AddOps = {"ADD","DADD","DADDU","ADDD","ADDDS","SUB","SUBS","SUBD","DSUB","DSUBU","SUBPS","SLT","SLTU","AND","OR","XOR","CVTDL"};
    private String[] MulOps = {"MUL", "DMUL","DMULU","MULS","MULD","MULPS"};
    private String[] DivOps = {"DIV", "DDIV","DDIVU","DIVS","DIVD","DIVPS"};
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
        public String op = ""; //holds sub/add, etc.

        // Tomasulo execution cycle data
        public int prgmCount = 0;
        public int issue = 0;
        public int exeStart = 0;
        public int exeEnd = 0;
        public int writeBack = 0;
        public int currentStageCycleNum = 1;
        public int absoluteIndex = 0; // This is the line number of this instruction.

        // Instruction related registers, jump labels or where to jump
        public String label = null;     // The label in this line.
        public String jumpLabel = null; // Jump to label "X". For branch operands.
        public String DestReg = null;
        public String SourceReg1 = null;
        public String SourceReg2 = null;

        public int src1 = 0;
        public int src2 = 0;
        public int destination = 0;

        //temp reg to hold values to prevent WAR
        public Number valReg1 = 0;
        public Number valReg2 = 0;
    };

    private static OperandInfo tempOperationInfo;

    // Struct for float registers list
    public static class FloatRegTemplate{
        public float value= (float) 0.0;
        public Boolean ready = true;
        public int occupyInstId = 0;
    }

    // Struct for integer registers list
    public static class IntRegTemplate{
        public int value= 0;
        public Boolean ready = true;
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

    //Memory
    public static Number[] memory = new Number[1000];

    // All statistics information.
    public static int[] statisticsInfo = new int[9];

    // Architecture parameters' value. "ld, sd, int, fpAdd, fpMul, fpDiv"
    public static long[] architectureNum = new long[]{6, 6, 5, 4, 4, 3};

    // Architecture parameters' max value.
    public static long[] architectureNumMax = new long[]{9, 9, 9, 9, 9, 9};

    // Architecture cycle numbers' value. "ld, sd, int, fpAdd, fpMul, fpDiv"
    public static long[] architectureCycle = new long[]{2, 2, 2, 2, 4, 2}; //new long[]{10, 10, 4, 7, 24, 5};

    // Architecture cycle numbers' max value.
    public static long[] architectureCycleMax = new long[]{100,100,100,100,100,100};

    // How many cycles to proceed when "Execute multiple cycle" button is pressed.
    public static long multiStepNum = 3;

    // All of the instruction lines in this file.
    public static List<String> InstructionFullList = new ArrayList<>();

    //public static String[] instr = {"lw", "sw", "lw", "FPadd", "FPmul", "FPdiv", "sw", "lw", "INTadd", "INTsub", "FPsub", "sw", "INTadd", "INTmul", "FPdiv"};
    //public static String[] instr = {"ld $R6,0($R1)", "ld $R5,0($R4)", "add $f3,$f2,$f5", "sw $R3,0($R8)" , "sub $R4,$R3,$R5", "mul $R10,$R11,R12", "sw $R10, 0($R11)", "div $R2,$R7,$R9", "add $f1,$f1,$f1", "add $f2,$f2,$f2", "sub $R3,$R3,$R3", "sw $R3, 0($R3)"};
    //public static  String[] instr = {"add $R3,$R2,$R5", "sub $R4,$R3,$R5", "mul $R10,$R11,R12"};
    public static String[] instr = {"MUL $f2,$f1,$f3", "ADD $f3,$f1,$f1"}; //to test WAR
    //public static String[] instr = {"ld $R5,0($R4)", "ld $R5,0($R4)", "ld $R5,0($R4)", "ld $R5,0($R4)", "ld $R5,0($R4)", "ld $R5,0($R4)", "ld $R5,0($R4)"};

    // Operand info structures. It's length equals to the number of Operand cells in Diagram.
    public static LinkedList<OperandInfo> OperationInfoStation = new LinkedList<OperandInfo>();

    boolean cdbBusy = false; //if true cdb is currently being used for write back!
    boolean WBoccurred = false;
    boolean src1Ready = true;
    boolean src2Ready = true;

    // Keep track of the new change of OperandsInfoStation element
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
        //String type_ = OperationMapper.get(tempOperationInfo.operand);
        String type_ = tempOperationInfo.operand;
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


    // Judge whether it is available to start execution
    private Boolean judgeExe(String s, int i){
        //Start execution once data dependencies have been resolved --> ---WIP---
        if("INT".equals(s)) {
            src1Ready = IntRegs[OperationInfoStation.get(i).src1].ready;
            src2Ready = IntRegs[OperationInfoStation.get(i).src2].ready;
        }

        else{
            src1Ready = FloatRegs[OperationInfoStation.get(i).src1].ready;
            src2Ready = FloatRegs[OperationInfoStation.get(i).src2].ready;
        }


        if (src1Ready && src2Ready){
            return true;
        }
        else{
            return false;
        }
    }



    // Parse the next instruction and return a tempOperandsInfo
    private void parseInstruction(String operandLine){
        String tag, operation, regs, srcTemp, operandType, destinationReg;

        // There can be some label in front of a instruction, like `start: xxx xx xxx `
        if (operandLine.split(":").length>1){
            tempOperationInfo.label = operandLine.split(":")[0];
            tag = operandLine.split(":")[1].trim();
        }

        //parse operation
        tempOperationInfo = new OperandInfo();
        //operation = operandLine.split("\\s+")[0].toUpperCase().trim(); //operation=operandLine.split(";")[0].trim();

        tempOperationInfo.inst = operandLine;

        String[] separateEmpty = operandLine.split("\\s+");
        operation = separateEmpty[0].toUpperCase().trim();
        //tempOperationInfo.operand = operation;
        operandType = OperationMapper.get(operation);
        tempOperationInfo.operand = operandType;
        System.out.println("OperationType: " + tempOperationInfo.operand);
        tempOperationInfo.op = operation; //specifies if sub/add, etc.

        //Need to deal with SAVE and LOAD
/*
        //tempOperationInfo.operand = operand;
 */
        if(operandType.equals("BRA")){
            tempOperationInfo.jumpLabel = separateEmpty[1].trim();
            return;
        }

        if(operandType.equals("HALT") || operandType.equals("NOP")){
            return;
        }

        srcTemp = separateEmpty[1].toUpperCase().trim();
        String[] regParts = srcTemp.split(",");
        if(!"SAVE".equals((operandType))) {
            tempOperationInfo.DestReg = regParts[0].toUpperCase().trim();
        }


        if("LOAD".equals(operandType)){
            tempOperationInfo.SourceReg1 = regParts[1].toUpperCase().trim(); //the thing loading from memory. This will correspond to an index from mem
            tempOperationInfo.SourceReg2 = "";

            tempOperationInfo.destination = Integer.parseInt(tempOperationInfo.DestReg.replaceAll("[^\\d]", ""));
            tempOperationInfo.src1 = Integer.parseInt(tempOperationInfo.SourceReg1.replaceAll("[^\\d]", ""));
        }

        else if("SAVE".equals(operandType)){
            tempOperationInfo.SourceReg1 = regParts[0].toUpperCase().trim(); //The first reg is the source register
            tempOperationInfo.SourceReg2 = "";
            tempOperationInfo.DestReg = "0";//second reg is the address in memory to store the src register's value
        }

        else{
            //distinguish between int and fp instructions
            if(regParts[0].startsWith("R") || regParts[0].startsWith("$R")){
                tempOperationInfo.operand = "INT"; //By default an integer operation
                //System.out.println("integer operation!");
            }

            tempOperationInfo.destination = Integer.parseInt(tempOperationInfo.DestReg.replaceAll("[^\\d]", ""));

            tempOperationInfo.SourceReg1 = regParts[1].toUpperCase().trim();
            tempOperationInfo.src1 = Integer.parseInt(tempOperationInfo.SourceReg1.replaceAll("[^\\d]", ""));


            tempOperationInfo.SourceReg2 = regParts[2].toUpperCase().trim();
            tempOperationInfo.src2 = Integer.parseInt(tempOperationInfo.SourceReg2.replaceAll("[^\\d]", ""));


        }
    }



    // Update the OperandsInfoStation(current Operands station infos)
    // 1. Put tempOperandsInfo in the right place
    // 2. Update the Issue value and state of newly placed member
    private void updateOperandsInfoStation(){
        if (OperationInfoStation.size()>=OpQueue){
            //OperationInfoStation.removeLast();
        }
        OperationInfoStation.addFirst(tempOperationInfo);
        OperationInfoStation.getFirst().issue = CycleNumCur;
        OperationInfoStation.getFirst().state = InstructionState[0];
        OperationInfoStation.getFirst().absoluteIndex = instructionLineCur;

        //set respective destination register to busy as it may be a source register for another instruction
        if("INT".equals(OperationInfoStation.getFirst().operand)){
            IntRegs[OperationInfoStation.getFirst().destination].ready = false; //busy until instruction writes back
        }

        else{
            FloatRegs[OperationInfoStation.getFirst().destination].ready = false;
        }

        //OperationInfoFull.addLast(OperationInfoStation.getFirst());
        //OperationInfoStation.getFirst().inst = InstructionFullList.get(instructionLineCur);
    }

    // Judge whether it is available to write back
    private Boolean judgeWB(boolean w){
        //Writeback can occur if cdb is free. ONE WRITEBACK PER CLOCK CYCLE
        if(!w){
            return true;
        }
        else{
            return false;
        }
    }

    // Sequentially check all of the items in the Operands station,
    // And do corresponding operation to them according to state
    private void checkAllOperandMember(){
        for(int i = 0; i < OperationInfoStation.size(); i++){
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
                    switch(OperationInfoStation.get(i).operand){
                        case "LOAD":
                            OperationInfoStation.get(i).currentStageCycleNum = (int) architectureCycle[0];
                            break;

                        case "SAVE":
                            OperationInfoStation.get(i).currentStageCycleNum = (int) architectureCycle[1];
                            break;

                        case "INT":
                            OperationInfoStation.get(i).currentStageCycleNum = (int) architectureCycle[2];
                            break;

                        case "ADD":
                            OperationInfoStation.get(i).currentStageCycleNum = (int) architectureCycle[3];
                            //Hold values in valReg to prevent WAR
                            OperationInfoStation.get(i).valReg1 = FloatRegs[OperationInfoStation.get(i).src1].value;
                            OperationInfoStation.get(i).valReg2 = FloatRegs[OperationInfoStation.get(i).src2].value;
                            break;

                        case "MUL":
                            OperationInfoStation.get(i).currentStageCycleNum = (int) architectureCycle[4];
                            //Hold values in valReg to prevent WAR
                            OperationInfoStation.get(i).valReg1 = FloatRegs[OperationInfoStation.get(i).src1].value;
                            OperationInfoStation.get(i).valReg2 = FloatRegs[OperationInfoStation.get(i).src2].value;
                            break;

                        case "DIV":
                            OperationInfoStation.get(i).currentStageCycleNum = (int) architectureCycle[5];
                            break;

                        case "NOP":
                            OperationInfoStation.get(i).currentStageCycleNum = 1;
                            break;

                        case "BRA":
                            OperationInfoStation.get(i).currentStageCycleNum = 1;
                            break;
                    }
                    if (CycleNumCur - OperationInfoStation.get(i).exeStart >= OperationInfoStation.get(i).currentStageCycleNum){
                        if(judgeExe(OperationInfoStation.get(i).operand, i)){
                            OperationInfoStation.get(i).state = InstructionState[2];
                            OperationInfoStation.get(i).exeEnd = CycleNumCur;
                            //Execute using switch statement here
                            switch(OperationInfoStation.get(i).operand){
                                case "ADD":
                                    OpsADD(i); //FPaddition
                                break;

                                case "MUL":
                                    OpsMUL(i); //FPmultiplication
                                break;
                            }
                            System.out.println(OperationInfoStation.get(i).operand + " Instruction EXE done!");
                        }
                    }
                    break;

                case "ExeEnd":
                    if(judgeWB(WBoccurred)){ //---WIP---
                        //reverse OperationInfoStation to get instructions in chronological order
                        System.out.println(OperationInfoStation.get(i).operand + " Writeback");
                        OperationInfoStation.get(i).state = InstructionState[3];
                        //if two instructions finish execution at the same time, need to stagger the WB
                        OperationInfoStation.get(i).writeBack = CycleNumCur;
                        OperationInfoStation.get(i).currentStageCycleNum = 1;
                        WBoccurred = true; //don't writeback twice in one clock!
                    }
                    break;

                case "WB":
                    //cdbBusy = false;
                    if (CycleNumCur - OperationInfoStation.get(i).writeBack >= OperationInfoStation.get(i).currentStageCycleNum){
                        System.out.println(OperationInfoStation.get(i).operand + " WB done!");
                        OperationInfoStation.get(i).state = InstructionState[4];
                        IntRegs[OperationInfoStation.get(i).destination].ready = true; //ready until instruction writes back
                        WBOps(OperationInfoStation.get(i).SourceReg1, OperationInfoStation.get(i).SourceReg2);
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

    // WB part. Called at the end of WB state. Please set the occupied
    // registers free here and update the data computed.

    // The operations applied to an instruction which is in write back state
    private void WBOps(String s1, String s2){
        //write computed value to destination register specified in instruction. Display on GUI

    }

    // Operations going to be executed when this type of instructions are
    // called. Please refer to mips instruction guide to make them functional.
    // Detailed data type may not need to be considered except the big
    // difference between integer and float.

    private void OpsNOP(int i){}

    private void OpsHALT(int i){}

    private void OpsADD(int i){
        //fp addition (or subtraction)
        if("SUB".equals(OperationInfoStation.get(i).op)){
            FloatRegs[OperationInfoStation.get(i).destination].value = (float) OperationInfoStation.get(i).valReg1 - (float) OperationInfoStation.get(i).valReg2;;
        }

        else{
            FloatRegs[OperationInfoStation.get(i).destination].value = (float) OperationInfoStation.get(i).valReg1 + (float) OperationInfoStation.get(i).valReg2;
        }
    }

    private void OpsLogic(int i){
        // AND, OR, XOR
    }

    private void OpsMUL(int i){
        FloatRegs[OperationInfoStation.get(i).destination].value = (float) OperationInfoStation.get(i).valReg1 * (float) OperationInfoStation.get(i).valReg2;
    }

    private void OpsDIV(int i){
        FloatRegs[OperationInfoStation.get(i).destination].value = FloatRegs[OperationInfoStation.get(i).src1].value / FloatRegs[OperationInfoStation.get(i).src2].value;
    }

    private void OpsBRANCH(int i){}

    private void OpsLOAD(int i){

        //load data into int register
        if(OperationInfoStation.get(i).DestReg.startsWith("R") || OperationInfoStation.get(i).DestReg.startsWith("$R")){
            IntRegs[OperationInfoStation.get(i).destination].value = (int) memory[OperationInfoStation.get(i).src1]; //Loaded data value from memory
        }

        //load data into floating pt register
        else{
            FloatRegs[OperationInfoStation.get(i).destination].value = (float) memory[OperationInfoStation.get(i).src1]; //Loaded data value from memory
        }


    }

    private void OpsSAVE(int i){
        // Two types, normal save and directly save to register like MTC0
        //mem[] = FloatRegs[OperationInfoStation.get(i).destination].value
    }

    // The core logic. Called for each cycle update.
    public void parseStep(){
        Boolean issueAvailable;
        if(instructionLineCur < instr.length) { //if there is an instruction in instr, parse and push updateOperandsInfoStation
            //parseInstruction(InstructionFullList.get(instructionLineCur));
            //for debugging
            //System.out.println(instr[instructionLineCur]);
            parseInstruction(instr[instructionLineCur]);
            issueAvailable = judgeIssue();
            if (issueAvailable) {
                updateOperandsInfoStation();
                instructionLineCur++;
            } else {
                statisticsInfo[3]++; //if issue not available, it is due to structural stall
            }

            checkAllOperandMember();
            statisticsInfo[1] = instructionLineCur;
        }

        else{
            checkAllOperandMember();
        }

        //for debugging
        for(int i = 0; i < OperationInfoStation.size(); i++) {
            System.out.println(OperationInfoStation.get(i).operand + " " + OperationInfoStation.get(i).DestReg + " " + OperationInfoStation.get(i).SourceReg1 + " " + OperationInfoStation.get(i).SourceReg2 + " " +  OperationInfoStation.get(i).state);
        }

        CycleNumCur++;
        WBoccurred = false;
        statisticsInfo[0] = CycleNumCur;
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
            IntRegs[i].value = 1; //init values
        }

        for (int i=0; i< FloatRegs.length; i++){
            FloatRegs[i] = new FloatRegTemplate();
            FloatRegs[i].value = 2; //Test init values
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

/*
    //clock set function --> Main logic updates every clock cycle
    public void runLogic(int clk){
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
 */
}

/*
To-Do
Run full cycle with ld instr to check logic
Display to diagram!

The rest of the FUs have to be implemented once ld works!
 */

//////////////////////////////////////////////////////////////////////
//////////////////        TODO       /////////////////////////////////
//////////////////////////////////////////////////////////////////////

// Execution part. This function will be applied to every instruction
// in the reservation station at the end of its EXE state when user
// click the step button. Some operations related to registers and
// data availability may need to be added here.
/*
    // The operations applied to an instruction which is in execute state
    private void ExeOps(int operandInfoIndex){
        String operandType = OperationInfoStation.get(operandInfoIndex).operand;
        switch(operandType)
        {
            case "NOP" :
                //OperationInfoStation.get(operandInfoIndex).currentStageCycleNum = 1;
                OpsNOP(operandInfoIndex);
            case "HALT" :
                //OperationInfoStation.get(operandInfoIndex).currentStageCycleNum = 0;
                OpsHALT(operandInfoIndex);
                break;
            case "DIV" :
                //OperationInfoStation.get(operandInfoIndex).currentStageCycleNum = (int) architectureCycle[5];
                OpsDIV(operandInfoIndex);
                break;
            case "MUL" :
                //OperationInfoStation.get(operandInfoIndex).currentStageCycleNum = (int) architectureCycle[4];
                OpsMUL(operandInfoIndex);
                break;
            case "LOAD" :
                //OperationInfoStation.get(operandInfoIndex).currentStageCycleNum = (int) architectureCycle[0];
                OpsLOAD(operandInfoIndex);
                break;
            case "SAVE":
                //OperationInfoStation.get(operandInfoIndex).currentStageCycleNum = (int) architectureCycle[1];
                OpsSAVE(operandInfoIndex);
                break;
            case "BRA":
                //OperationInfoStation.get(operandInfoIndex).currentStageCycleNum = 1;
                OpsBRANCH(operandInfoIndex);
                break;
            default :
                if(operandType.equals("ADD")){
                    //OperationInfoStation.get(operandInfoIndex).currentStageCycleNum = (int) architectureCycle[3];
                }
                else{
                    //OperationInfoStation.get(operandInfoIndex).currentStageCycleNum = (int) architectureCycle[2];
                }
                if(operandType.contains("ADD")){
                    OpsADD(operandInfoIndex);
                }
                else if(operandType.contains("SUB")){
                    OpsSUB(operandInfoIndex);
                }
                else if(operandType.contains("SLT")){
                    OpsSLT(operandInfoIndex);
                }
                else if(operandType.contains("CVT")){
                    OpsCVT(operandInfoIndex);
                }
                else if(operandType.contains("AND") || operandType.contains("OR")){
                    OpsLogic(operandInfoIndex);
                }
        }
    }

 */