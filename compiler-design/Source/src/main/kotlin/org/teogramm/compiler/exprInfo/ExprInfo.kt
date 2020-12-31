package org.teogramm.compiler.exprInfo

import org.teogramm.compiler.output.InstructionList
import org.teogramm.compiler.symtable.VarType

class ExprInfo {
    companion object{
        // Used when the result is stored in the A register
        const val LOCATION_A=-1
        const val LOCATION_TEMP=-2
        // No location (literal)
        const val LITERAL=-3
    }
    var type = VarType.INT
    var location = -2
    var literalValue = String()
    // Records the number of instructions that have been added
    var instructionList = InstructionList()
}