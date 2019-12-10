package com.miracleyoo.Logic;

import java.util.*;
import java.util.Map;

public class MainLogic {
    ///////////////////////////////////////////////////////////////////////////
    ////////////////   Most Important Global Parameters ///////////////////////
    ///////////////////////////////////////////////////////////////////////////

    // -- ARCHITECTURE PART -- \\
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
    // The number of Operand queue. Sharing opQueue for int and fp.
    public int OpQueue = 10;
    // The artificial memory
    public Number[] Memory=new Number[100];

    // -- STATISTICS PART -- \\
    // Judge whether all of the instructions have been issued
    public boolean isEnd = false;
    // The current cycle number.
    public int CycleNumCur = 0;
    // The index of the line of instruction which is going to be parsed
    public int instructionLineCur = 0;
    // The number of the instructions issued
    public int totalInstructionNum = 0;
    // Statistics information which will be shown in DataUI's statistics part
    public int[] statisticsInfo = new int[9];

    // -- FUNCTIONAL UNITS PART -- \\
    // All of the instruction lines in the currently opened file. Initialized during the ParseFile.
    public static List<String> InstructionFullList = new ArrayList<>();
    // A map which enable the search for certain functional units list by its name
    private static Map<String, FUTemplate[]> Type2FUsMap = new HashMap<>(); // Find corresponding FUs Struct List using Map
    // All Integer Registers.
    public RegTemplate[] IntRegs = new RegTemplate[32];
    // All Float Registers.
    public RegTemplate[] FloatRegs = new RegTemplate[32];
    // Operand info structures. It's length equals to the number of Operand cells in Diagram.
    public LinkedList<InstructionInfo> OperationInfoStation = new LinkedList<InstructionInfo>();
    // How many instruction which is being executed in the whole Tomasulo process
    public int OperationInfoStationActualSize = 0;
    // The temporal InstructionInfo in
    //List to hold all instructions that have been written back. This is used for Diagram register part
    public LinkedList<String> wbList = new LinkedList<String>();

    private static InstructionInfo tempOperationInfo;
    // Operand classify dictionary. Key:Value -> Operand:Class
    private static Map<String, String> OperationMapper = new HashMap<>();
    // Map the label string shown in the file to the line number
    private static Map<String, Integer> label2index = new HashMap<>();

    // -- Reservation stations definition -- \\
    private static FUTemplate[] LoadFUs = new FUTemplate[(int) architectureNum[0]];
    private static FUTemplate[] SaveFUs = new FUTemplate[(int) architectureNum[1]];
    private static FUTemplate[] IntFUs = new FUTemplate[(int) architectureNum[2]];
    private static FUTemplate[] AddFUs = new FUTemplate[(int) architectureNum[3]];
    private static FUTemplate[] MulFUs = new FUTemplate[(int) architectureNum[4]];
    private static FUTemplate[] DivFUs = new FUTemplate[(int) architectureNum[5]];

    ///////////////////////////////////////////////////////////////////////////
    ///////////////   Most Important Global Parameters End ////////////////////
    ///////////////////////////////////////////////////////////////////////////

    private String[] AddOps = {"ADD", "DADD", "DADDU", "ADDD", "ADDDS", "SUB", "SUBS", "SUBD", "DSUB", "DSUBU", "SUBPS", "SLT", "SLTU", "AND", "OR", "XOR", "CVTDL"};
    private String[] MulOps = {"DMUL", "DMULU", "MULS", "MULD", "MULPS"};
    private String[] DivOps = {"DDIV", "DDIVU", "DIVS", "DIVD", "DIVPS"};
    private String[] IntOps = {"DADDI", "DADDIU", "SLTI", "ANDI", "ORI", "XORI", "DSLL", "DSRL", "DSRA", "DSLLV", "DSRLV", "DSRAV"};
    private String[] SaveOps = {"SB", "SH", "SW", "SD", "SS", "MTC0", "MTC1", "MFC0", "MFC1"};
    private String[] LoadOps = {"LB", "LH", "LW", "LD", "LS", "LBU", "LHU", "LWU"};
    private String[] BranchOps = {"BEQZ", "BNEZ", "BEQ", "BNE", "J", "JR", "JAL", "JALR"};
    private String[] InstructionState = {"Issue", "EXE", "ExeEnd", "WB", "End"};
//    private String[] TypeNames = {"NOP", "HALT", "ADD", "INT", "DIV", "MUL", "LOAD", "SAVE", "BRA"};

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

        // Initialize the Memory
        Random random = new Random();
        for (int i_=0; i_<100; i_++) {
            Memory[i_] = (Number)(random.nextInt(100) + 1);
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

    // Initialized the label map
    public void initLabelMap() {
        for (int i = 0; i < InstructionFullList.size(); i++) {
            String operandLine = InstructionFullList.get(i).split(";")[0].trim();
            if (operandLine.split(":").length > 1) {
                label2index.put(operandLine.split(":")[0].trim(), i);
            }
        }
    }

    // Push the list item to corresponding dictionary Key:Value pair
    private void mapListItems(String[] inputList, String listName) {
        for (String operand : inputList) {
            OperationMapper.put(operand, listName);
        }
    }

    // Judge whether it is possible to issue a new instruction
    // 1. Check whether there are some free operation stations
    // 2. Check whether there are some free and corresponding FUs
    private boolean judgeIssue() {
        boolean flag = false;
        String type_ = OperationMapper.get(tempOperationInfo.operation);

        // Check available reservation station at first. BRA, NOP, HALT don't need RS.
        if (type_.equals("HALT")) {
            instructionLineCur++;
            isEnd = true;
            return false;
        }
        if (type_.equals("NOP")) {
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
        } else if (OperationInfoStation.get(OperationInfoStationActualSize - 1).state.equals("End")) {
            return true;
        } else {
            return false;
        }
    }

    // Judge whether it is available to start execution
    private boolean judgeExeStart(int i) {
        int tempIndex = 0;
        InstructionInfo tempOperation = OperationInfoStation.get(i);
        if (tempOperation.waiForIndexReg1 != null) {
            tempIndex = tempOperation.waiForIndexReg1;
            if (OperationInfoStation.get(OperationInfoStation.size() - 1 - tempIndex).state.equals(InstructionState[4])) {
                tempOperation.ValueReg1 = getReg(OperationInfoStation.size() - 1 - tempIndex, "Dest").value;
            }
        }
        if (tempOperation.waiForIndexReg2 != null) {
            tempIndex = tempOperation.waiForIndexReg2;
            if (OperationInfoStation.get(OperationInfoStation.size() - 1 - tempIndex).state.equals(InstructionState[4])) {
                tempOperation.ValueReg2 = getReg(OperationInfoStation.size() - 1 - tempIndex, "Dest").value;
            }
        }
        if (tempOperation.waiForIndexDest != null) {
            tempIndex = tempOperation.waiForIndexDest;
            if (OperationInfoStation.get(OperationInfoStation.size() - 1 - tempIndex).state.equals(InstructionState[4])) {
                tempOperation.ValueDest = getReg(OperationInfoStation.size() - 1 - tempIndex, "Dest").value;
            }
        }
        if ((tempOperation.ValueReg1 != null || tempOperation.waiForIndexReg1 == null) &&
                (tempOperation.ValueReg2 != null || tempOperation.waiForIndexReg2 == null) &&
                (tempOperation.ValueDest != null || tempOperation.waiForIndexDest == null)) {
            return true;
        }
        return false;
    }

    // Judge whether it is available to go to the end of execution
    private boolean judgeExeEnd(int i) {
        boolean flag = true;
        for (int j = i; j < OperationInfoStationActualSize; j++) {
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
        for (int i = start; i < OperationInfoStationActualSize; i++) {
            if (OperationInfoStation.get(i).writeBack == CycleNumCur) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    // Parse the next instruction and return a tempOperandsInfo
    private void parseInstruction(String operandLine) {
        String operand, srcTemp, operandType;

        tempOperationInfo = new InstructionInfo();
        operandLine = operandLine.split(";")[0].trim();
        tempOperationInfo.inst = operandLine;

        operandLine = operandLine.replaceAll("[$]", "");
        // There can be some label in front of a instruction, like `start: xxx xx xxx `
        if (operandLine.split(":").length > 1) {
            tempOperationInfo.label = operandLine.split(":")[0];
            operandLine = operandLine.split(":")[1].trim();
        }

        String[] separateEmpty = operandLine.split("\\s+");
        operand = separateEmpty[0].replace(".", "").toUpperCase().trim();
        operandType = OperationMapper.get(operand);
        tempOperationInfo.op = operandType;
        tempOperationInfo.operation = operand;
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
                    if (address_[0].length() == 0) {
                        tempOperationInfo.ValueReg1 = 0;
                    } else {
                        tempOperationInfo.ValueReg1 = Integer.parseInt(address_[0]);
                    }
                } else {
                    tempOperationInfo.ValueReg1 = 0;
                }
                // When parsing LOAD or SAVE operands whose address is like ADD1(ADD2), ADD1 will be put into ValueReg1 or SourceReg1
                if (address_[1].toUpperCase().startsWith("R") || address_[1].toUpperCase().startsWith("F")) {
                    tempOperationInfo.SourceReg2 = address_[1];
                } else if (address_[1].trim().replaceAll("\\d+", "").length() == 0) {
                    tempOperationInfo.ValueReg2 = Integer.parseInt(address_[1]);
                } else {
                    tempOperationInfo.ValueReg2 = 0;
                }
                return;
            }

            if (regParts[1].toUpperCase().startsWith("R") || regParts[1].toUpperCase().startsWith("F")) {
                tempOperationInfo.SourceReg1 = regParts[1];
            } else if (regParts[1].trim().replaceAll("\\d+", "").length() == 0) {
                tempOperationInfo.ValueReg1 = Integer.parseInt(regParts[1]);
            } else {
                tempOperationInfo.ValueReg1 = 0;
            }

            if (regParts[2].toUpperCase().startsWith("R") || regParts[2].toUpperCase().startsWith("F")) {
                tempOperationInfo.SourceReg2 = regParts[2];
            } else if (regParts[2].trim().replaceAll("\\d+", "").length() == 0) {
                tempOperationInfo.ValueReg2 = Integer.parseInt(regParts[2]);
            } else {
                tempOperationInfo.ValueReg2 = 0;
            }
            tempOperationInfo.s1 = regParts[1];
            tempOperationInfo.s2 = regParts[2];
            System.out.println(tempOperationInfo.s1 + " " + tempOperationInfo.s2);
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
        InstructionInfo fistOperation = OperationInfoStation.getFirst();
        fistOperation.issue = CycleNumCur;
        fistOperation.state = InstructionState[0];
        fistOperation.absoluteIndex = totalInstructionNum;
        fistOperation.currentStageCycleNum = 1;
        if (fistOperation.SourceReg1 != null) {
            if (getReg(0, "Src1").ready) {
                fistOperation.ValueReg1 = getReg(0, "Src1").value;
            } else {
                fistOperation.waiForIndexReg1 = getReg(0, "Src1").occupyInstId;
            }
        }
        if (fistOperation.SourceReg2 != null) {
            if (getReg(0, "Src2").ready) {
                fistOperation.ValueReg2 = getReg(0, "Src2").value;
            } else {
                fistOperation.waiForIndexReg2 = getReg(0, "Src2").occupyInstId;
            }
        }
        if (fistOperation.DestReg != null) {
            if (!OperationMapper.get(fistOperation.operation).equals("SAVE")) {
                getReg(0, "Dest").ready = false;
                getReg(0, "Dest").occupyInstId = fistOperation.absoluteIndex;
            }
            else{
                if (getReg(0, "Dest").ready) {
                    fistOperation.ValueDest = getReg(0, "Dest").value;
                } else {
                    fistOperation.waiForIndexDest = getReg(0, "Dest").occupyInstId;
                }
            }
        }

    }

    // Sequentially check all of the items in the Operands station,
    // And do corresponding operation to them according to state
    private void checkAllOperandMember() {
        for (int i = OperationInfoStationActualSize - 1; i >= 0; i--) {
            switch (OperationInfoStation.get(i).state) {
                case "Issue":
                    if (CycleNumCur - OperationInfoStation.get(i).issue >= OperationInfoStation.get(i).currentStageCycleNum) {
                        if (judgeExeStart(i)) {
                            OperationInfoStation.get(i).state = InstructionState[1];
                            OperationInfoStation.get(i).exeStart = CycleNumCur;
//                            IssueOps();
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
                            if(!"BRA".equals(OperationInfoStation.get(i).op) && !"SAVE".equals(OperationInfoStation.get(i).op)){
                                wbList.addFirst(OperationInfoStation.get(i).operation);
                            }
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

    // Set Execution cycle number
    private void SetExeOpsNum(int operandInfoIndex) {
        String operandType = OperationMapper.get(OperationInfoStation.get(operandInfoIndex).operation);
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
        String operandType = OperationMapper.get(OperationInfoStation.get(operandInfoIndex).operation);
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

    // The operations applied to an instruction which is in write back state
    private void WBOps(int i) {
        if (OperationInfoStation.get(i).DestReg != null && getReg(i, "Dest").occupyInstId == OperationInfoStation.get(i).absoluteIndex) {
            getReg(i, "Dest").ready = true;
        }
        if (OperationInfoStation.get(i).SourceReg1 != null && getReg(i, "Src1").occupyInstId == OperationInfoStation.get(i).absoluteIndex) {
            getReg(i, "Src1").ready = true;
        }
        if (OperationInfoStation.get(i).SourceReg2 != null && getReg(i, "Src2").occupyInstId == OperationInfoStation.get(i).absoluteIndex) {
            getReg(i, "Src2").ready = true;
        }
    }

    private Number[] getTwoOperand(int i) {
        Number src1, src2;
        if (OperationInfoStation.get(i).SourceReg1 == null) {
            src1 = OperationInfoStation.get(i).ValueReg1;
        } else {
            src1 = getReg(i, "Src1").value;
        }
        if (OperationInfoStation.get(i).SourceReg2 == null) {
            src2 = OperationInfoStation.get(i).ValueReg2;
        } else {
            src2 = getReg(i, "Src2").value;
        }
        return new Number[]{src1, src2};
    }

    private void OpsADD(int i) {
        getReg(i, "Dest").value = OperationInfoStation.get(i).ValueReg1.floatValue() + OperationInfoStation.get(i).ValueReg2.floatValue();
        getReg(i, "Dest").ready = true;
    }

    private void OpsSUB(int i) {
        getReg(i, "Dest").value = OperationInfoStation.get(i).ValueReg1.floatValue() - OperationInfoStation.get(i).ValueReg2.floatValue();
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
        getReg(i, "Dest").value = OperationInfoStation.get(i).ValueReg1.floatValue() * OperationInfoStation.get(i).ValueReg2.floatValue();
        getReg(i, "Dest").ready = true;
    }

    private void OpsDIV(int i) {
        getReg(i, "Dest").value = OperationInfoStation.get(i).ValueReg1.floatValue() / OperationInfoStation.get(i).ValueReg2.floatValue();
        getReg(i, "Dest").ready = true;
    }

    private void OpsBRANCH(int i) {
        try {
            instructionLineCur = label2index.get(OperationInfoStation.get(i).jumpLabel);
        } catch (Exception e) {
            isEnd = true;
        }
    }

    private void OpsLOAD(int i) {
        setRegValue(i, "Dest", Memory[(int)(OperationInfoStation.get(i).ValueReg1.floatValue() + OperationInfoStation.get(i).ValueReg2.floatValue())%100]);
    }

    private void OpsSAVE(int i) {
        Memory[(int)(OperationInfoStation.get(i).ValueReg1.floatValue() + OperationInfoStation.get(i).ValueReg2.floatValue())%100] = OperationInfoStation.get(i).ValueDest;
        // Two types, normal save and directly save to register like MTC0
    }

    private RegTemplate getReg(int i, String type) {
        String regName;
        int index_;
        if (type.equals("Dest")) {
            regName = OperationInfoStation.get(i).DestReg;
        } else if (type.equals("Src1")) {
            regName = OperationInfoStation.get(i).SourceReg1;
        } else if (type.equals("Src2")) {
            regName = OperationInfoStation.get(i).SourceReg2;
        } else {
            throw new Error("getRegValue Type Error");
        }

        index_ = Integer.parseInt(regName.replaceAll("\\D+", ""));
        if (regName.toUpperCase().startsWith("R")) {
            return IntRegs[index_];
        } else if (regName.toUpperCase().startsWith("F")) {
            return FloatRegs[index_];
        } else {
            throw new Error("getRegValue Type Error");
        }
    }

    private void setRegValue(int i, String type, Number value) {
        String regName;
        int index_;
        switch (type) {
            case "Dest":
                regName = OperationInfoStation.get(i).DestReg;
                break;
            case "Src1":
                regName = OperationInfoStation.get(i).SourceReg1;
                break;
            case "Src2":
                regName = OperationInfoStation.get(i).SourceReg2;
                break;
            default:
                throw new Error("getRegValue Type Error");
        }

        index_ = Integer.parseInt(regName.replaceAll("\\D+", ""));
        if (regName.toUpperCase().startsWith("R")) {
            IntRegs[index_].value = value;
            IntRegs[index_].ready = true;
        } else if (regName.toUpperCase().startsWith("F")) {
            FloatRegs[index_].value = value;
            FloatRegs[index_].ready = true;
        } else {
            throw new Error("getRegValue Type Error");
        }
    }

    // The core logic. Called for each cycle update.
    public void parseStep() {
        if(instructionLineCur>=InstructionFullList.size()-1){
            isEnd=true;
        }
        if (!isEnd) {
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
        statisticsInfo[1] = totalInstructionNum;
    }

    // A data structure which store all data by parsing an instruction
    // As well as cycle data during the Tomasulo execution.
    public static class InstructionInfo {
        public String operation = ""; // Only the operand name, like ADDD, MULD
        public String inst = "";    // The whole instruction, like ADDD R1, R2, R3
        public String op = "";
        public String state = "";   // Current state. It can only be

        public String s1 = "";
        public String s2 = "";

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

        String SourceReg1 = null;
        String SourceReg2 = null;

        Integer waiForIndexReg1 = null;
        Integer waiForIndexReg2 = null;
        Integer waiForIndexDest = null;

        // If there is an numerical data in the registers' place, please store
        // it in ValueReg1 or ValueReg2
        Number ValueReg1 = null;
        Number ValueReg2 = null;
        Number ValueDest = null;
    }

    // Struct for registers list
    public static class RegTemplate {
        public Number value = 0;
        boolean ready = true;
        int occupyInstId = 0;
    }

    // Struct for all kinds of functional units
    public static class FUTemplate {
        boolean busy = false;
        public int occupyInstId = 0;
    }

    //used for highlighting instructions
    public class instructionTrack{
        String s = "";
        int i;
    }
}