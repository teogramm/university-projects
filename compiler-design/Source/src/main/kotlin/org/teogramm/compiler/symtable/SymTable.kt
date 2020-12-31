package org.teogramm.compiler.symtable

import java.util.*
import kotlin.collections.HashMap

class SymTable(startAddress:Int) {
    private val table = HashMap<String, SymRec>()

    var startAddress:Int
    private set

    init {
        this.startAddress = startAddress
    }

    /**
     * Puts a symbol in the table
     * @param name The name of the symbol
     * @return the created symbol record, null if a symbol with the given name already exists
     */
    fun putSym(name:String): SymRec?{
        return if(table.containsKey(name)){
            // Return null if the symbol already exists
            null
        }else{
            table[name] = SymRec(name)
            table[name]!!.memoryAddr = startAddress
            startAddress += 1;
            table[name]
        }
    }

    /**
     * Get a symbol from the current table
     * @return the symbol record if the symbol exists, null otherwise
     */
    fun getSym(name: String): SymRec?{
        return if(!table.containsKey(name)){
            null
        }else{
            table[name]
        }
    }

    /**
     * Gives a string representation of the symbol table
     */
    fun printTable():String{
        val tempStr = StringBuilder("SYMBOL TABLE FOR SCOPE\n")
        tempStr.append(String.format("%15s | %15s | %15s%n","VARIABLE NAME","VARIABLE TYPE","VARIABLE ADDRESS"))
        for(variable in table.values){
            tempStr.append(String.format("%15s | %15s | %15s%n",variable.name,variable.type.toString(),variable.memoryAddr.toString()))
        }
        return tempStr.toString()
    }

    fun getSize():Int{
        return table.size
    }

    fun getUnusedVariables():LinkedList<String>{
        val unusedList = LinkedList<String>()
        for(symbol in table.values){
            if(!symbol.accessed){
                unusedList.add(symbol.name)
            }
        }
        return unusedList
    }
}