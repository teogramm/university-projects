package org.teogramm.compiler.utilities

import org.teogramm.compiler.exprInfo.ExprInfo
import org.teogramm.compiler.output.InstructionList
import org.teogramm.compiler.symtable.VarType
import kotlin.math.pow

/**
 * The mover class contains some methods that help move ExprInfo objects between registers.
 */
class Mover {
    companion object{
        /**
         * Puts the given operand in the accumulator
         */
        fun moveOperandToA(info: ExprInfo): InstructionList {
            val tempList = InstructionList()
            when(info.location){
                ExprInfo.LOCATION_TEMP-> tempList.addInstruction("LDA TEMP")
                ExprInfo.LOCATION_A->{}
                ExprInfo.LITERAL->{
                    if(info.type == VarType.INT) {
                        tempList.addInstruction("ENTA " + info.literalValue)
                    }else if(info.type == VarType.FLOAT){
                        tempList.addAll(
                            storeFloatLiteralInA(
                                info
                            )
                        )
                    }
                }
                else->{
                    tempList.addInstruction("LDA " + info.location)
                }
            }
            info.location = ExprInfo.LOCATION_A
            return tempList
        }

        /**
         * Returns the instructions that put the given float literal
         * in the accumulator register
         */
        private fun storeFloatLiteralInA(op: ExprInfo): InstructionList {
            // Essentially, multiply number by a power of 10 to remove the decimal point
            // then divide it by that power of 10 to store it in A
            val tempList = InstructionList()
            val splitNumber = op.literalValue.split(".")
            tempList.addInstruction("ENTA " + op.literalValue.replace(".",""))
            tempList.addInstruction("ENTX " + 10.0.pow(splitNumber[1].length).toInt().toString())
            tempList.addInstruction("STX TEMPF")
            tempList.addInstruction("FDIV TEMPF")
            return tempList
        }

        /**
         * Converts the given operand to float. If it is a variable, it creates a float copy and
         * stores it in the TEMP register.
         */
        fun convertToFloat(op: ExprInfo): InstructionList {
            val instructionList = InstructionList()
            if(op.type != VarType.FLOAT) {
                when (op.location) {
                    ExprInfo.LOCATION_A -> instructionList.addInstruction("FLOT")
                    ExprInfo.LOCATION_TEMP -> {
                        instructionList.addInstruction("LDA TEMP")
                        instructionList.addInstruction("FLOT")
                        instructionList.addInstruction("STA TEMP")
                    }
                    ExprInfo.LITERAL-> op.literalValue += ".0"
                    else->{
                        // We need to create a temporary copy of this variable
                        instructionList.addInstruction("LDA "+op.location)
                        instructionList.addInstruction("FLOT")
                        instructionList.addInstruction("STA TEMP")
                        op.location = ExprInfo.LOCATION_TEMP
                    }
                }
                op.type = VarType.FLOAT
            }
            return instructionList
        }

        /**
         * Moves the operand to temporary location without affecting the A register
         * NOTE: Works only with operands in Accumulator or Literals (i.e. does not
         * work for variables)
         * @return An instruction list with the instructions that perform the move
         */
        fun moveOperandToTemp(info: ExprInfo): InstructionList {
            val tempList = InstructionList()
            when(info.location){
                ExprInfo.LOCATION_A-> tempList.addInstruction("STA TEMP")
                ExprInfo.LITERAL->{
                    if(info.type == VarType.INT) {
                        tempList.addInstruction("ENTX " + info.literalValue)
                        tempList.addInstruction("STX TEMP")
                    }
                    else if(info.type == VarType.FLOAT){
                        tempList.addAll(
                            storeFloatLiteralInA(
                                info
                            )
                        )
                        tempList.addInstruction("STA TEMP")
                    }
                }
            }
            info.location = ExprInfo.LOCATION_TEMP
            return tempList
        }
        /**
         * Returns the required instructions to move the operand to the stack
         */
        fun moveToStack(op: ExprInfo): InstructionList {
            val instructionList = InstructionList()
            instructionList.addInstruction("LD5 TOP")
            when(op.location){
                ExprInfo.LOCATION_A-> instructionList.addInstruction("STA STACK,5")
                ExprInfo.LOCATION_TEMP->{
                    instructionList.addInstruction("LDX TEMP")
                    instructionList.addInstruction("STX STACK,5")
                }
                ExprInfo.LITERAL->{
                    instructionList.addInstruction("ENTX " + op.literalValue)
                    instructionList.addInstruction("STX STACK,5")
                }else->{
                instructionList.addInstruction("LDX "+op.location)
                instructionList.addInstruction("STX STACK,5")
            }
            }
            instructionList.addInstruction("INC5 1")
            instructionList.addInstruction("ST5 TOP")
            return instructionList
        }

        /**
         * Creates the instruction that compare op1 with op2
         */
        fun createComparisonInstructions(op1: ExprInfo, op2: ExprInfo): InstructionList {
            val instructionList = InstructionList()
            // First operand should be in A second operand should be in temp
            // Calculate the first operand
            instructionList.addAll(op1.instructionList)
            instructionList.addAll(moveOperandToA(op1))
            // If op1 is not float and op2 is float we need to convert op1 to float, since it is in a
            // it just adds the FLOT instruction
            if(op1.type != VarType.FLOAT && op2.type == VarType.FLOAT){
                instructionList.addAll(
                    convertToFloat(
                        op1
                    )
                )
            }
            // We need to move op1 (currently in A to the stack) if:
            // We need to convert op2 to float, op2 is not a literal or a variable (since arithmetic operations
            // overwrite the Accumulator)
            // op2 is a float literal
            var stackUsed = false
            if((op1.type == VarType.FLOAT && op2.type != VarType.FLOAT) || op2.location == ExprInfo.LOCATION_A ||
                op2.location == ExprInfo.LOCATION_TEMP|| (op2.location == ExprInfo.LITERAL && op2.type == VarType.FLOAT)){
                instructionList.addAll(
                    moveToStack(
                        op1
                    )
                )
                stackUsed = true
            }
            // Evaluate op2 (if needed)
            instructionList.addAll(op2.instructionList)
            if(op2.location < 0){
                instructionList.addAll(
                    moveOperandToTemp(
                        op2
                    )
                )
            }
            // Check if we need to convert op2 to float
            if(op1.type == VarType.FLOAT && op2.type != VarType.FLOAT){
                instructionList.addAll(
                    convertToFloat(
                        op2
                    )
                )
            }
            // Move stack top back to A if needed
            if(stackUsed){
                instructionList.addInstruction("LD5 TOP")
                instructionList.addInstruction("DEC5 1")
                instructionList.addInstruction("LDA STACK,5")
                instructionList.addInstruction("ST5 TOP")
            }
            var tempInstruction = StringBuilder()
            // If either one is float we use the float compare instruction
            tempInstruction = if(op1.type == VarType.FLOAT || op2.type == VarType.FLOAT){
                tempInstruction.append("FCMP ")
            }else{
                tempInstruction.append("CMPA ")
            }
            tempInstruction = if(op2.location == ExprInfo.LOCATION_TEMP){
                tempInstruction.append("TEMP")
            }else{
                tempInstruction.append(op2.location)
            }
            instructionList.addInstruction(tempInstruction.toString())
            return instructionList
        }

        /**
         * Returns the instructions that display a floating point number stored in the
         * A register
         */
        fun floatPrintInstructions(): InstructionList {
            val instructionList = InstructionList()
            //Store the initial number in TEMP
            instructionList.addInstruction("STA TEMP")
            // Subtract 0.5 before calculating the integer part so it is always right
            // If the number is negative we need to add 0.5 instead of subtracting
            instructionList.addInstruction("ENTA 1000")
            instructionList.addInstruction("STA TEMPF")
            instructionList.addInstruction("ENTA 499")
            instructionList.addInstruction("FDIV TEMPF")
            instructionList.addInstruction("STA TEMPF")
            instructionList.addInstruction("LDA TEMP")
            // If number is negative subtract 0.5 instead of adding, jump to the add operation
            instructionList.addInstruction("JAN *+3")
            instructionList.addInstruction("FSUB TEMPF")
            // If number is positive skip skip the ADD instruction
            instructionList.addInstruction("JMP *+2")
            instructionList.addInstruction("FADD TEMPF")
            instructionList.addInstruction("FIX")
            // If number is not negative do not print a sign, else print a sign
            instructionList.addInstruction("JANN *+3")
            instructionList.addInstruction("ENT1 45")
            instructionList.addInstruction("ST1 LINE")
            // Store the integer part
            instructionList.addInstruction("STA TEMPF")
            instructionList.addInstruction("CHAR")
            instructionList.addInstruction("STX LINE+1")
            // Convert integer part to float and store it
            instructionList.addInstruction("LDA TEMPF")
            instructionList.addInstruction("FLOT")
            instructionList.addInstruction("STA TEMPF")
            // Add the dot to the display
            instructionList.addInstruction("ENTA 40")
            instructionList.addInstruction("SLA 4")
            instructionList.addInstruction("STA LINE+2")
            // Subtract the integer part
            instructionList.addInstruction("LDA TEMP")
            instructionList.addInstruction("FSUB TEMPF")
            instructionList.addInstruction("STA TEMPF")
            // Multiply by 10.000 (4 digit accuracy)
            instructionList.addInstruction("ENTA 100")
            instructionList.addInstruction("STA TEMP")
            instructionList.addInstruction("MUL TEMP")
            instructionList.addInstruction("SLAX 5")
            instructionList.addInstruction("FLOT")
            instructionList.addInstruction("FMUL TEMPF")
            // Convert to integer
            instructionList.addInstruction("FIX")
            instructionList.addInstruction("CHAR")
            instructionList.addInstruction("STX LINE+4(2:5)")
            instructionList.addInstruction("OUT LINE(TERM)")
            instructionList.addInstruction("JBUS *(TERM)")
            //Reset negative sign
            instructionList.addInstruction("ENT1 0")
            instructionList.addInstruction("ST1 LINE")
            return instructionList
        }
    }
}