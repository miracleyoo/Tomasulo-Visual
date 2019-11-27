package com.miracleyoo.Logic;

import java.util.HashMap;
import java.util.Map;

public class MainLogic {
    private String[] AddOps = {"ADD","DADD","DADDU","ADDD","ADDDS","SUB","SUBS","SUBD","DSUB","DSUBU","SUBPS","SLT","SLTU","AND","OR","XOR"};
    private String[] MulOps = {"DMUL","DMULU","MULS","MULD","MULPS"};
    private String[] DivOps = {"DDIV","DDIVU","DIVS","DIVD","DIVPS"};
    private String[] IntOps = {"DADDI","DADDIU","SLTI","ANDI","ORI","XORI","DSLL","DSRL","DSRA","DSLLV","DSRLV","DSRAV"};
    private String[] SaveOps = {"SB","SH","SW","SD","SS"};
    private String[] LoadOps = {"LB","LH","LW","LD","LS","LBU","LHU","LWU"};
    private String[] BranchOps = {"BEQZ", "BENZ", "BEQ", "BNE", "J", "JR", "JAL", "JALR"};
    Map< String, String> OperandMapper = new HashMap<>();

    private void mapListItems (String[]inputList, String listName){
        for (String operand : inputList) {
            OperandMapper.put(operand, listName);
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
    }

}
