package com.thelogicmaster.clearwing.bytecode;

import java.util.List;

import static com.thelogicmaster.clearwing.bytecode.Instruction.LABEL_PREFIX;

public interface JumpingInstruction {
    
    List<Integer> getJumpLabels();
}
