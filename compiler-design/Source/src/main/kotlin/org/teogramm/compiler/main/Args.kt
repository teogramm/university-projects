package org.teogramm.compiler.main
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

class Args(parser: ArgParser){
    val outputFile by parser.storing("-o","--output",help = "name of output file").default("")
    val inputFile by parser.positional("INPUT", help = "input file")
}