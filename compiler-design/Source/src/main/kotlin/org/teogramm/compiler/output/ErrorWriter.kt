package org.teogramm.compiler.output

class ErrorWriter {
    companion object {
        private const val ESCAPE = '\u001B'
        private const val RESET = "$ESCAPE[0m"

        fun error(message: String, lineNumber: Int) {
            println("$ESCAPE[31m error: $message : line $lineNumber $RESET")
        }

        fun error(message: String){
            println("$ESCAPE[31m error: $message")
        }

        fun warning(message: String, lineNumber: Int) {
            println("$ESCAPE[33m warning: $message : line $lineNumber $RESET")
        }

        fun warning(message: String) {
            println("$ESCAPE[33m warning: $message")
        }
    }
}