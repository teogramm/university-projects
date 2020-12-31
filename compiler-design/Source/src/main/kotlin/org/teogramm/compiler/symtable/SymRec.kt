package org.teogramm.compiler.symtable

class SymRec(val name:String) {
    lateinit var type: VarType
    var memoryAddr = -1
    var accessed = false
    var data = Any()
    get(){
        return when(type){
            VarType.INT -> field as Int
            VarType.FLOAT -> field as Float
        }
    }
}