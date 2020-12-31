package org.teogramm.compiler.output

import java.io.*

class AssemblyWriter(private val fileName:String) {

    companion object{
        /**
         * Returns a formatted version of the instruction given as input
         */
        fun formatInstruction(command: String):String{
            return formatInstruction(
                " ".repeat(
                    10
                ), command, ""
            )
        }

        fun formatInstruction(label: String, command: String):String{
            return formatInstruction(
                label,
                " ".repeat(10 - label.length) + command,
                ""
            )
        }


        fun formatInstruction(label: String, command: String,comment: String):String{
            return "$label $command $comment\n"
        }
    }

    /**
     * When this is disabled the writer does not org.teogramm.compiler.output to the file
     */
    var writingEnabled = true
    private val outFile = RandomAccessFile(File(fileName),"rw")

    /**
     * The number of loops that we have entered and are currently active
     */
    var loops = 0
    private set
    /**
     * Store the position of the orig keyword to modify it when we know
     * the exact number of memory we need.
     */
    private var origPos:Long = 0

    init{
        // Address 0 is reserved in floating point comparison
        this.write("ORIG 1")
        this.write("TERM","EQU 18")
        this.write("ORIG     ")
        origPos = outFile.filePointer
        this.write("TEMP","CON 0")
        this.write("TEMPF","CON 0")
        this.write("TOP","CON 0")
        this.write("STACK","ORIG *+50")
        this.write("LINE","ORIG *+24")
        this.write("BEGIN","NOP")
    }

    private fun writeOverflowMessage(){
        this.write("OVERFLOWST","ALF OVERF")
        this.write("ALF LOW  ")
        // Make space for the word
        this.write("ORIG *+22")
        this.write("OVERFLOWEX","OUT OVERFLOWST(TERM)")
        this.write("JBUS *(TERM)")
        this.write("HLT")
    }

    fun write(command: String) {
        this.write(" ".repeat(10),command,"")
    }

    fun write(label: String, command: String){
        this.write(label," ".repeat(10-label.length) + command,"")
    }

    fun write(label: String, command: String,comment: String){
        val instruction = "$label $command $comment\n"
        if(writingEnabled) {
            outFile.write(instruction.toByteArray())
        }
    }

    fun writeAll(instructionList: InstructionList){
        if(writingEnabled) {
            for (instruction in instructionList) {
                outFile.write(instruction.toByteArray())
            }
        }
    }

    /**
     * Called when we enter a control sequence (while,for,if). This causes the writer to stop writing
     * instructions directly to a file.
     */
    fun enterControlSequence(){
        loops++
        writingEnabled = false
    }

    /**
     * Called when we exit a control sequence.
     */
    fun exitControlSequence(){
        loops--
        if(loops == 0){
            writingEnabled = true
        }
    }

    fun end(startAddress:Int){
        if(writingEnabled) {
            this.write("HLT")
            this.writeOverflowMessage()
            this.write("END BEGIN")
            outFile.seek(origPos - 6)
            outFile.write(startAddress.toString().toByteArray())
        }else{
            outFile.close()
            val f = File(fileName)
            f.delete()
        }
    }

    /**
     * Deletes file and disables further writing calls
     */
    fun error(){
        writingEnabled=false
        loops+= 100
    }
}