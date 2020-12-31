package org.teogramm.compiler.output

import org.teogramm.compiler.output.AssemblyWriter
import java.util.*

/**
 * A String LinkedList that contains formatted instructions
 */
class InstructionList:LinkedList<String>() {
    /**
     * Adds the given instruction to the end of the list
     */
    fun addInstruction(command: String){
        super.addLast(AssemblyWriter.formatInstruction(command))
    }

    fun addInstruction(label: String, command: String){
        super.addLast(AssemblyWriter.formatInstruction(label,command))
    }

    fun addInstruction(label: String, command: String,comment: String){
        super.addLast(AssemblyWriter.formatInstruction(label,command, comment))
    }
}