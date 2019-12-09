package com.miracleyoo.Logic;

import java.util.*;
import java.util.Map;

public class MainLogic {
    private String[] AddOps = {"ADD", "DADD", "DADDU", "ADDD", "ADDDS", "SUB", "SUBS", "SUBD", "DSUB", "DSUBU", "SUBPS", "SLT", "SLTU", "AND", "OR", "XOR", "CVTDL"};
    private String[] MulOps = {"DMUL", "DMULU", "MULS", "MULD", "MULPS"};
    private String[] DivOps = {"DDIV", "DDIVU", "DIVS", "DIVD", "DIVPS"};
    private String[] IntOps = {"DADDI", "DADDIU", "SLTI", "ANDI", "ORI", "XORI", "DSLL", "DSRL", "DSRA", "DSLLV", "DSRLV", "DSRAV"};
    private String[] SaveOps = {"SB", "SH", "SW", "SD", "SS", "MTC0", "MTC1", "MFC0", "MFC1"};
    private String[] LoadOps = {"LB", "LH", "LW", "LD", "LS", "LBU", "LHU", "LWU"};
    private String[] BranchOps = {"BEQZ", "BNEZ", "BEQ", "BNE", "J", "JR", "JAL", "JALR"};
    private String[] InstructionState = {"Issue", "EXE", "ExeEnd", "WB", "End"};
    private String[] TypeNames = {"NOP", "HALT", "ADD", "INT", "DIV", "MUL", "LOAD", "SAVE", "BRA"};

    // A data structure which store all data by parsing an instruction
    // As well as cycle data during the Tomasulo execution.
    public static class OperandInfo {
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

        // Instruction related registers, jump labels or where to jump
        public String label = null;     // The label in this line.
        public String jumpLabel = null; // Jump to label "X". For branch operands.
        public String DestReg = null;
        public String SourceReg1 = null;
        public String SourceReg2 = null;

        // If there is an numerical data in the registers' place, please store
        // it in ValueReg1 or ValueReg2
        public Number ValueReg1 = null;
        public Number ValueReg2 = null;
    }

    private static OperandInfo tempOperationInfo;

    // Struct for registers list
    public static class RegTemplate {
        public Number value = 0;
        public boolean ready = true;
        public int occupyInstId = 0;
    }

    // Struct for all kinds of functional units
    public static class FUTemplate {
        public boolean busy = false;
        public int occupyInstId = 0;
    }

    ///////////////////////////////////////////////////////////////////////////
    ////////////////   Most Important Global Parameters ///////////////////////
    ///////////////////////////////////////////////////////////////////////////

    // The current cycle number.
    public int CycleNumCur = 0;

    // The index of the line of instruction which is going to be parsed
    public int instructionLineCur = 0;
    public int totalInstructionNum = 0;

    // The number of Operand queue. Sharing opQueue for int and fp.
    public static int OpQueue = 10;//instr.length; //size of instruction array

    // Judge whether all of the instructions have been issued
    public boolean isEnd = false;

    // All Integer Registers.
    public RegTemplate[] IntRegs = new RegTemplate[32];

    // All Float Registers.
    public RegTemplate[] FloatRegs = new RegTemplate[32];

    // All statistics information.
    public int[] statisticsInfo = new int[9];

    // Architecture parameters' value. "ld, sd, int, fpAdd, fpMul, fpDiv"
    public static long[] architectureNum = new long[]{6, 6, 5, 4, 4, 3}; //may need to change this back to type long if bugs

    // Architecture parameters' max value.
    public static long[] architectureNumMax = new long[]{9, 9, 9, 9, 9, 9};

    // Architecture cycle numbers' value.
    public static long[] architectureCycle = new long[]{10, 10, 4, 7, 24, 5};

    // Architecture cycle numbers' max value.
    public static long[] architectureCycleMax = new long[]{100, 100, 100, 100, 100, 100};

    // How many cycles to proceed when "Execute multiple cycle" button is pressed.
    public static long multiStepNum = 3;

    // All of the instruction lines in this file.
    public static List<String> InstructionFullList = new ArrayList<>();

    //public static String[] instr = {"lw", "sw", "lw", "FPadd", "FPmul", "FPdiv", "sw", "lw", "INTadd", "INTsub", "FPsub", "sw", "INTadd", "INTmul", "FPdiv"};
    //public static String[] instr = {"ld $R5,0($R4)", "add $R3,$R2,$R5", "sw $R3,0($R8)" , "sub $R4,$R3,$R5", "mul $R10,$R11,R12", "sw $10, 0($R11)"};
//    public static String[] instr = {"ld $R5,0($R4)", "ld $R5,0($R4)", "ld $R5,0($R4)", "ld $R5,0($R4)", "ld $R5,0($R4)", "ld $R5,0($R4)", "ld $R5,0($R4)"};

    // Operand info structures. It's length equals to the number of Operand cells in Diagram.
    public LinkedList<OperandInfo> OperationInfoStation = new LinkedList<OperandInfo>();
    public int OperationInfoStationActualSize = 0;

    // Operand classify dictionary. Key:Value -> Operand:Class
    public static Map<String, String> OperationMapper = new HashMap<>();

    public static Map<String, Integer> label2index = new HashMap<>();

    // Reservation stations definition
    private static FUTemplate[] LoadFUs = new FUTemplate[(int) architectureNum[0]];
    private static FUTemplate[] SaveFUs = new FUTemplate[(int) architectureNum[1]];
    private static FUTemplate[] IntFUs = new FUTemplate[(int) architectureNum[2]];
    private static FUTemplate[] AddFUs = new FUTemplate[(int) architectureNum[3]];
    private static FUTemplate[] MulFUs = new FUTemplate[(int) architectureNum[4]];
    private static FUTemplate[] DivFUs = new FUTemplate[(int) architectureNum[5]];

    private static Map<String, FUTemplate[]> Type2FUsMap = new HashMap<>(); // Find corresponding FUs Struct List using Map

    public static Map<String, Number> dataMap = new HashMap<>(); // Counters for data and code


    ///////////////////////////////////////////////////////////////////////////
    ///////////////   Most Important Global Parameters End ////////////////////
    ///////////////////////////////////////////////////////////////////////////

    public void initLabelMap(){
        for(int i=0;i<InstructionFullList.size();i++){
            String operandLine = InstructionFullList.get(i).split(";")[0].trim();
            if (operandLine.split(":").length > 1) {
                label2index.put(operandLine.split(":")[0].trim(), i);
            }
        }
    }


    /*
    Instruction in;
    public static Instruction blankInstr = new Instruction("", "", "", "", -1, 0);
    Instruction cdb = blankInstr; //holds instruction that has finished executing and pushes it to destination/waiting RS
    */
    //public static OperandInfo[] loadBuffer = new OperandInfo[(int)architectureNum[0]]; //used to interface with diagram for display

    // Push the list item to corresponding dictionary Key:Value pair
    private void mapListItems(String[] inputList, String listName) {
        for (String operand : inputList) {
            OperationMapper.put(operand, listName);
        }
    }

    // Judge whether a string toCheckValue is in the string list arr
    private static boolean contains(String[] arr, String toCheckValue) {
        for (String element : arr) {
            if (element.equals(toCheckValue)) {
                return true;
            }
        }
        return false;
    }

    // Judge whether it is possible to issue a new instruction
    // 1. Check whether there are some free operation stations
    // 2. Check whether there are some free and corresponding FUs
    private boolean judgeIssue() {
        boolean flag = false;
        String type_ = OperationMapper.get(tempOperationInfo.operand);

        // Check available reservation station at first. BRA, NOP, HALT don't need RS.
        if(type_.equals("HALT")){
            instructionLineCur++;
            isEnd = true;
            return false;
        }
        if(type_.equals("NOP")){
            instructionLineCur++;
            return false;
        }
        if (type_.equals("BRA")) {
            flag = true;
        } else {
            for (FUTemplate FUs : Type2FUsMap.get(type_)) {
                if (!FUs.busy) {
                    flag = true;
                    break;
                }
            }
        }

        // If no corresponding RS available, return false.
        if (!flag) {
            return false;
        }

        // Check whether there are free OperandsInfoStation, if not, check whether the last Instruction is end.
        if (OperationInfoStationActualSize < OpQueue) {
            return true;
        } else if (OperationInfoStation.get(OperationInfoStationActualSize-1).state.equals("End")) {
            return true;
        } else {
            return false;
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
    private boolean judgeExeStart(int i) {
        if ((OperationInfoStation.get(i).SourceReg1==null || getReg(i, "Src1").ready ||
                getReg(i, "Src1").occupyInstId == OperationInfoStation.get(i).absoluteIndex) &&
                (OperationInfoStation.get(i).SourceReg2==null || getReg(i, "Src2").ready ||
                        getReg(i, "Src2").occupyInstId == OperationInfoStation.get(i).absoluteIndex)){
            if(OperationInfoStation.get(i).SourceReg1!=null) {
                getReg(i, "Src1").ready = false;
                getReg(i, "Src1").occupyInstId=OperationInfoStation.get(i).absoluteIndex;
            }
            if(OperationInfoStation.get(i).SourceReg2!=null) {
                getReg(i, "Src2").ready = false;
                getReg(i, "Src2").occupyInstId=OperationInfoStation.get(i).absoluteIndex;
            }
            if(OperationInfoStation.get(i).DestReg!=null) {
                getReg(i, "Dest").ready = false;
                getReg(i, "Dest").occupyInstId=OperationInfoStation.get(i).absoluteIndex;
            }
            return true;
        }
        return false;
    }

    private boolean judgeExeEnd(int i) {
        boolean flag = true;
        for (int j=i;j<OperationInfoStationActualSize; j++){
            if (OperationInfoStation.get(j).exeEnd == CycleNumCur) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    // Judge whether it is available to write back
    private boolean judgeWB(int start) {
        boolean flag = true;
        for (int i=start;i<OperationInfoStationActualSize; i++){
            if(OperationInfoStation.get(i).writeBack==CycleNumCur){
                flag=false;
            }
        }
        return flag;
    }

    // Parse the next instruction and return a tempOperandsInfo
    private void parseInstruction(String operandLine) {
        String operand, srcTemp, operandType;

        tempOperationInfo = new OperandInfo();
        operandLine = operandLine.split(";")[0].trim();
        tempOperationInfo.inst = operandLine;

        operandLine = operandLine.replaceAll("[$]","");
        // There can be some label in front of a instruction, like `start: xxx xx xxx `
        if (operandLine.split(":").length > 1) {
            tempOperationInfo.label = operandLine.split(":")[0];
            operandLine = operandLine.split(":")[1].trim();
        }

        String[] separateEmpty = operandLine.split("\\s+");
        operand = separateEmpty[0].replace(".", "").toUpperCase().trim();
        operandType = OperationMapper.get(operand);

        tempOperationInfo.operand = operand;
        if (separateEmpty.length <= 1) { // HALT, NOP
            return;
        } else {
            if (operandType.equals("BRA")) {
                tempOperationInfo.jumpLabel = separateEmpty[1].trim();
                return;
            }

            srcTemp = separateEmpty[1].toUpperCase().trim();
            String[] regParts = srcTemp.split(",");
            tempOperationInfo.DestReg = regParts[0];

            if (regParts[0].toUpperCase().startsWith("R") || regParts[0].toUpperCase().startsWith("F")) {
                tempOperationInfo.DestReg = regParts[0].trim().toUpperCase();
            } else {
                throw new AssertionError("Destination Register wrong");
            }

            if (contains(new String[]{"LOAD", "SAVE"}, operandType)) {
                String[] address_ = regParts[1].split("[(]");
                address_[1] = address_[1].replaceAll("[)]", "");

                // Split things like 4(R1), CONTROL(r0)
                if (address_[0].replaceAll("\\d+", "").length() == 0) {
                    if(address_[0].length()==0){
                        tempOperationInfo.ValueReg1 = 0;
                    }
                    else {
                        tempOperationInfo.ValueReg1 = Integer.parseInt(address_[0]);
                    }
                } else {
                    //////////////////        TODO

                    // Here we have not consider the case that the load or save use an
                    // Address from .data part. After the completion of data parser in
                    // ParseFile.java(Already written TODO there too), we need to come
                    // back to finish this part. Temporarily use 0 here.
                    tempOperationInfo.ValueReg1 = 0;
                }
                // When parsing LOAD or SAVE operands whose address is like ADD1(ADD2), ADD1 will be put into ValueReg1 or SourceReg1
                if (address_[1].toUpperCase().startsWith("R") || address_[1].toUpperCase().startsWith("F")) {
                    tempOperationInfo.SourceReg2 = address_[1];
                } else if (address_[1].trim().replaceAll("\\d+", "").length() == 0) {
                    tempOperationInfo.ValueReg2 = Integer.parseInt(address_[1]);
                } else {
                    //////////////////        TODO
                    // Same for here, some destination register is a variable in .data
//                    throw new AssertionError("Destination Register wrong");
                    tempOperationInfo.ValueReg2=0;
                }
                return;
            }

            if (regParts[1].toUpperCase().startsWith("R") || regParts[1].toUpperCase().startsWith("F")) {
                tempOperationInfo.SourceReg1 = regParts[1];
            } else if (regParts[1].trim().replaceAll("\\d+", "").length() == 0) {
                tempOperationInfo.ValueReg1 = Integer.parseInt(regParts[1]);
            } else {
                //////////////////        TODO
//                throw new AssertionError("Destination Register wrong");
                tempOperationInfo.ValueReg1=0;
            }

            if (regParts[2].toUpperCase().startsWith("R") || regParts[2].toUpperCase().startsWith("F")) {
                tempOperationInfo.SourceReg2 = regParts[2];
            } else if (regParts[2].trim().replaceAll("\\d+", "").length() == 0) {
                tempOperationInfo.ValueReg2 = Integer.parseInt(regParts[2]);
            } else {
                //////////////////        TODO
//                throw new AssertionError("Destination Register wrong");
                tempOperationInfo.ValueReg2=0;
            }
        }
    }

    // Update the OperandsInfoStation(current Operands station infos)
    // 1. Put tempOperandsInfo in the right place
    // 2. Update the Issue value and state of newly placed member
    private void updateOperandsInfoStation() {
        if (OperationInfoStationActualSize >= OpQueue) {
            OperationInfoStationActualSize--;
        }
        OperationInfoStation.addFirst(tempOperationInfo);
        OperationInfoStation.getFirst().issue = CycleNumCur;
        OperationInfoStation.getFirst().state = InstructionState[0];
        OperationInfoStation.getFirst().absoluteIndex = totalInstructionNum;
        OperationInfoStation.getFirst().currentStageCycleNum = 1;
        //        OperandsInfoStation.getFirst().inst = InstructionFullList.get(instructionLineCur);
    }

    // Sequentially check all of the items in the Operands station,
    // And do corresponding operation to them according to state
    private void checkAllOperandMember() {
        for (int i = OperationInfoStationActualSize-1; i >= 0; i--) {
            switch (OperationInfoStation.get(i).state) {
                case "Issue":
                    if (CycleNumCur - OperationInfoStation.get(i).issue >= OperationInfoStation.get(i).currentStageCycleNum) {
                        if (judgeExeStart(i)) {
                            OperationInfoStation.get(i).state = InstructionState[1];
                            OperationInfoStation.get(i).exeStart = CycleNumCur;
                            IssueOps();
                            SetExeOpsNum(i);
                        }
                    }
                    break;
                case "EXE":
                    if (CycleNumCur - OperationInfoStation.get(i).exeStart >= OperationInfoStation.get(i).currentStageCycleNum) {
                        if (judgeExeEnd(i)) {
                            OperationInfoStation.get(i).state = InstructionState[2];
                            OperationInfoStation.get(i).exeEnd = CycleNumCur;
                            ExeOps(i);
                            OperationInfoStation.get(i).currentStageCycleNum = 1;
                        }
                    }
                    break;
                case "ExeEnd":
                    if (CycleNumCur - OperationInfoStation.get(i).exeEnd >= OperationInfoStation.get(i).currentStageCycleNum) {
                        if (judgeWB(i)) {
                            OperationInfoStation.get(i).state = InstructionState[3];
                            OperationInfoStation.get(i).writeBack = CycleNumCur;
                            OperationInfoStation.get(i).currentStageCycleNum = 1;
                        }
                    }
                case "WB":
                    if (CycleNumCur - OperationInfoStation.get(i).writeBack >= OperationInfoStation.get(i).currentStageCycleNum) {
                        OperationInfoStation.get(i).state = InstructionState[4];
                    }
                    WBOps(i);
                    break;
                case "End":
                    break;
            }
        }
    }

    // The operations applied to an instruction which is in issue state
    private void IssueOps() {
        String regName;
        int index_;
        regName = OperationInfoStation.getFirst().DestReg;
        if (regName!=null) {
            index_ = Integer.parseInt(regName.replaceAll("\\D+", ""));
            if (regName.toUpperCase().startsWith("R")) {
                IntRegs[index_].ready = false;
                IntRegs[index_].occupyInstId = OperationInfoStation.getFirst().absoluteIndex;
            } else if (regName.toUpperCase().startsWith("F")) {
                FloatRegs[index_].ready = false;
                FloatRegs[index_].occupyInstId = OperationInfoStation.getFirst().absoluteIndex;
            }
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

    // Set Execution cycle number
    private void SetExeOpsNum(int operandInfoIndex) {
        String operandType = OperationMapper.get(OperationInfoStation.get(operandInfoIndex).operand);
        switch (operandType) {
            case "DIV":
                OperationInfoStation.get(operandInfoIndex).currentStageCycleNum = (int) architectureCycle[5];
                break;
            case "MUL":
                OperationInfoStation.get(operandInfoIndex).currentStageCycleNum = (int) architectureCycle[4];
                break;
            case "LOAD":
                OperationInfoStation.get(operandInfoIndex).currentStageCycleNum = (int) architectureCycle[0];
                break;
            case "SAVE":
                OperationInfoStation.get(operandInfoIndex).currentStageCycleNum = (int) architectureCycle[1];
                break;
            case "BRA":
                OperationInfoStation.get(operandInfoIndex).currentStageCycleNum = 1;
                break;
            default:
                if (operandType.equals("ADD")) {
                    OperationInfoStation.get(operandInfoIndex).currentStageCycleNum = (int) architectureCycle[3];
                } else {
                    OperationInfoStation.get(operandInfoIndex).currentStageCycleNum = (int) architectureCycle[2];
                }
        }
    }


    // The operations applied to an instruction which is in execute state
    private void ExeOps(int operandInfoIndex) {
        String operandType = OperationMapper.get(OperationInfoStation.get(operandInfoIndex).operand);
        switch (operandType) {
            case "DIV":
                OpsDIV(operandInfoIndex);
                break;
            case "MUL":
                OpsMUL(operandInfoIndex);
                break;
            case "LOAD":
                OpsLOAD(operandInfoIndex);
                break;
            case "SAVE":
                OpsSAVE(operandInfoIndex);
                break;
            case "BRA":
                OpsBRANCH(operandInfoIndex);
                break;
            default:
                if (operandType.contains("ADD")) {
                    OpsADD(operandInfoIndex);
                } else if (operandType.contains("SUB")) {
                    OpsSUB(operandInfoIndex);
                } else if (operandType.contains("SLT")) {
                    OpsSLT(operandInfoIndex);
                } else if (operandType.contains("CVT")) {
                    OpsCVT(operandInfoIndex);
                } else if (operandType.contains("AND") || operandType.contains("OR")) {
                    OpsLogic(operandInfoIndex);
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
    private void WBOps(int i) {
        if(OperationInfoStation.get(i).DestReg!=null && getReg(i, "Dest").occupyInstId==OperationInfoStation.get(i).absoluteIndex) {
            getReg(i, "Dest").ready = true;
        }
        if(OperationInfoStation.get(i).SourceReg1!=null && getReg(i, "Src1").occupyInstId==OperationInfoStation.get(i).absoluteIndex) {
            getReg(i, "Src1").ready = true;
        }
        if(OperationInfoStation.get(i).SourceReg2!=null && getReg(i, "Src2").occupyInstId==OperationInfoStation.get(i).absoluteIndex) {
            getReg(i, "Src2").ready = true;
        }
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

    private Number[] getTwoOperand(int i){
        Number src1, src2;
        if(OperationInfoStation.get(i).SourceReg1==null){
            src1 = OperationInfoStation.get(i).ValueReg1;
        }
        else{
            src1 = getReg(i, "Src1").value;
        }
        if(OperationInfoStation.get(i).SourceReg2==null){
            src2 = OperationInfoStation.get(i).ValueReg2;
        }
        else{
            src2 = getReg(i, "Src2").value;
        }
        return new Number[]{src1, src2};
    }

    private void OpsADD(int i) {
        Number[] operands = getTwoOperand(i);

        getReg(i, "Dest").value = operands[0].floatValue() + operands[1].floatValue();
        getReg(i, "Dest").ready = true;
    }

    private void OpsSUB(int i) {
        Number[] operands = getTwoOperand(i);

        getReg(i, "Dest").value = operands[0].floatValue() - operands[1].floatValue();
        getReg(i, "Dest").ready = true;
    }

    private void OpsSLT(int i) {
    }

    private void OpsLogic(int i) {
        // AND, OR, XOR
    }

    private void OpsCVT(int i) {
        // CVTDL: to double precision, here we don't need to care. Leave the function empty.
    }

    private void OpsMUL(int i) {
        Number[] operands = getTwoOperand(i);
        getReg(i, "Dest").value = (float)operands[0] * (float)operands[1];
        getReg(i, "Dest").ready = true;
    }

    private void OpsDIV(int i) {
        Number[] operands = getTwoOperand(i);
        getReg(i, "Dest").value = (float)operands[0] / (float)operands[1];
        getReg(i, "Dest").ready = true;
    }

    private void OpsBRANCH(int i) {
        try{
            instructionLineCur = label2index.get(OperationInfoStation.get(i).jumpLabel);
        }
        catch (Exception e){
            isEnd = true;
        }
    }

    private void OpsLOAD(int i) {
        setRegValue(i, "Dest", 1);
    }

    private void OpsSAVE(int i) {
        // Two types, normal save and directly save to register like MTC0
    }

    private RegTemplate getReg(int i, String type){
        String regName;
        int index_;
        if (type.equals("Dest")) {
            regName = OperationInfoStation.get(i).DestReg;
        }
        else if(type.equals("Src1")){
            regName = OperationInfoStation.get(i).SourceReg1;
        }
        else if(type.equals("Src2")){
            regName = OperationInfoStation.get(i).SourceReg2;
        }
        else{
            throw new Error("getRegValue Type Error");
        }

        index_ = Integer.parseInt(regName.replaceAll("\\D+", ""));
        if (regName.toUpperCase().startsWith("R")) {
            return IntRegs[index_];
        } else if (regName.toUpperCase().startsWith("F")) {
            return FloatRegs[index_];
        }
        else {
            throw new Error("getRegValue Type Error");
        }
    }

    private void setRegValue(int i, String type, Number value){
        String regName;
        int index_;
        if (type.equals("Dest")) {
            regName = OperationInfoStation.get(i).DestReg;
        }
        else if(type.equals("Src1")){
            regName = OperationInfoStation.get(i).SourceReg1;
        }
        else if(type.equals("Src2")){
            regName = OperationInfoStation.get(i).SourceReg2;
        }
        else{
            throw new Error("getRegValue Type Error");
        }

        index_ = Integer.parseInt(regName.replaceAll("\\D+", ""));
        if (regName.toUpperCase().startsWith("R")) {
            IntRegs[index_].value= (int) value;
            IntRegs[index_].ready = true;
        } else if (regName.toUpperCase().startsWith("F")) {
            FloatRegs[index_].value= (float) value;
            FloatRegs[index_].ready = true;
        }
        else {
            throw new Error("getRegValue Type Error");
        }
    }



    // The core logic. Called for each cycle update.
    public void parseStep() {
        if(!isEnd) {
            boolean issueAvailable;
            parseInstruction(InstructionFullList.get(instructionLineCur));

            issueAvailable = judgeIssue();
            if (issueAvailable) {
                updateOperandsInfoStation();
                OperationInfoStationActualSize++;
                instructionLineCur++;
                totalInstructionNum++;
            } else {
                statisticsInfo[3]++; //if issue not available, it is due to structural stall
            }
        }
        checkAllOperandMember();
        CycleNumCur++;
        statisticsInfo[0] = CycleNumCur;
        statisticsInfo[1] = instructionLineCur;
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

        for (int i = 0; i < IntRegs.length; i++) {
            IntRegs[i] = new RegTemplate();
        }

        for (int i = 0; i < FloatRegs.length; i++) {
            FloatRegs[i] = new RegTemplate();
        }

        for (int i = 0; i < AddFUs.length; i++) {
            AddFUs[i] = new FUTemplate();
        }

        for (int i = 0; i < MulFUs.length; i++) {
            MulFUs[i] = new FUTemplate();
        }

        for (int i = 0; i < DivFUs.length; i++) {
            DivFUs[i] = new FUTemplate();
        }

        for (int i = 0; i < SaveFUs.length; i++) {
            SaveFUs[i] = new FUTemplate();
        }

        for (int i = 0; i < LoadFUs.length; i++) {
            LoadFUs[i] = new FUTemplate();
        }

        for (int i = 0; i < IntFUs.length; i++) {
            IntFUs[i] = new FUTemplate();
        }

        Type2FUsMap.put("ADD", AddFUs);
        Type2FUsMap.put("MUL", MulFUs);
        Type2FUsMap.put("DIV", DivFUs);
        Type2FUsMap.put("SAVE", SaveFUs);
        Type2FUsMap.put("LOAD", LoadFUs);
        Type2FUsMap.put("INT", IntFUs);
    }
}