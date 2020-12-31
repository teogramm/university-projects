package org.teogramm.compiler.main

import com.xenomachina.argparser.*
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.misc.ParseCancellationException
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.teogramm.compiler.ProjectListener
import org.teogramm.compiler.output.ErrorWriter
import org.teogramm.compiler.utilities.ThrowingErrorListener
import org.teogramm.projectlang.ProjectLexer
import org.teogramm.projectlang.ProjectParser
import java.io.File
import kotlin.system.exitProcess


fun main(args: Array<String>) = mainBody{
    // Get the arguments
    val parsedArgs:Args = ArgParser(args).parseInto(::Args)
    val inputFile = File(parsedArgs.inputFile)
    // Check that input file exists
    if(!inputFile.exists()){
        ErrorWriter.error("File " + parsedArgs.inputFile +" does not exist!")
        exitProcess(1)
    }
    // Check that input file is not a directory
    if(inputFile.isDirectory){
        ErrorWriter.error(inputFile.name + " is a directory!")
        exitProcess(1)
    }
    // Check that input file can be read
    if(!inputFile.canRead()){
        ErrorWriter.error("File "+ inputFile.name +" cannot be read!")
        exitProcess(1)
    }
    var outputFileName = parsedArgs.outputFile
    if(outputFileName == ""){
        // If no output file name was specified, use the input file name with a different extension
        outputFileName = inputFile.nameWithoutExtension + ".mix"
    }

    // Process the file
    val c = CharStreams.fromFileName(inputFile.absolutePath)
    val lexer = ProjectLexer(c)
    val tokenStream = CommonTokenStream(lexer)
    val parser = ProjectParser(tokenStream)
    parser.buildParseTree = true
    parser.removeErrorListeners()
    parser.addErrorListener(ThrowingErrorListener())
    var synaxError = false
    var tree: ProjectParser.ProgramContext? = null
    // If there is a syntax error do not compile the program
    try {
        tree = parser.program()
    }catch (e: ParseCancellationException){
        println(e.message)
        synaxError = true
    }
    if(!synaxError) {
        val walker = ParseTreeWalker()
        walker.walk(ProjectListener(parser,outputFileName), tree)
    }
}