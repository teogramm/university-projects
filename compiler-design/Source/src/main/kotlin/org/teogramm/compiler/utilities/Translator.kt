package org.teogramm.compiler.utilities

/**
 * Translates symbols and instructions
 */
class Translator {
    companion object{
        fun comparisonToInstruction(operator:String):String{
            var instruction = "J"
            instruction += if(operator.contains('<')){
                "L"
            }else if(operator.contains('>')){
                "G"
            }else if(operator.contains("!")){
                "N"
            }else{""}

            if(operator.contains('=')) {
                instruction += 'E'
            }
            return instruction
        }

        /**
         * Converts the string of a comparison to its reverse assembly instruction
         * @param operator the boolean operator
         */
        fun reverseComparisonToInstruction(operator:String):String{
            var instruction = "J"
            instruction += if(operator.contains('<')){
                "G"
            }else if(operator.contains('>')){
                "L"
            }else if(operator.contains("!")){
                "E"
            }else{
                "NE"
            }

            if(!operator.contains('=')) {
                instruction += 'E'
            }
            return instruction
        }
    }
}