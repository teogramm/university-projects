package org.teogramm.compiler.symtable

import org.teogramm.compiler.output.ErrorWriter
import java.util.*

/**
 * SymTableManager manages multiple symbol tables for a single program
 */
class SymTableManager {
    private val tableStack = Stack<SymTable>()
    // Memory address of the next table
    // We do not use 0 because it is reserved in floating point comparison
    private var nextVarAddr = 1
    // Find the max number of variables at the same time so we can reserve space when programming
    private var maxVars = -1
    private var currentVars = 0

    /**
     * Create a new table and push it on top of the stack
     */
    fun createTable(){
        tableStack.push(SymTable(nextVarAddr))
    }

    /**
     * Remove the table on the top of the stack
     */
    fun removeTable(){
        if(currentVars > maxVars){
            maxVars = currentVars
        }
        currentVars -= tableStack.peek().getSize()
        nextVarAddr = tableStack.peek().startAddress
        for(unused in tableStack.peek().getUnusedVariables()){
            ErrorWriter.warning("Unused variable $unused",-1)
        }
        tableStack.pop()
    }

    /**
     * Insert a symbol at the current scope
     * @return The symbol record if the record was created, null if a symbol with the same name already exists
     */
    fun putSym(name: String): SymRec?{
        currentVars++
        nextVarAddr++
        return tableStack.peek().putSym(name)
    }

    /**
     * Checks if the symbol exists in the current scope.
     * @param name the name of the symbol
     * @return the symbol record if it exists in the current scope, null otherwise
     */
    fun getSymCurrentScope(name:String): SymRec?{
        return tableStack.peek().getSym(name)
    }

    /**
     * Checks if the symbol exists in any scope
     * @param name the name of the symbol
     * @return the symbol record if it exists, null otherwise
     */
    fun getSym(name: String): SymRec?{
        for(table in tableStack){
            val symbol = table.getSym(name)
            if(symbol != null){
                symbol.accessed = true
                return table.getSym(name)
            }
        }
        return null;
    }

    /**
     * Print all the tables that are on the stack
     */
    fun printTables(){
        val reverseIt = tableStack.iterator()
        println("LOCAL SCOPE")
        while (reverseIt.hasNext()){
            val table = reverseIt.next()
            println("----------------------------------------------")
            println(table.printTable())
        }
        println("GLOBAL SCOPE")
    }

    /**
     * Returns the start address of the program to account for variable and stack space
     */
    fun getStartAddress():Int{
        // Add 1 because adress 0 is reserved
        return maxVars + 1
    }

    /**
     * Prints the table at the top of the stack (current scope)
     */
    fun printCurrentTable(){
        println(tableStack.peek().printTable())
    }
}