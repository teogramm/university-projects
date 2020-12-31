package org.teogramm.compiler

import org.teogramm.compiler.exprInfo.ExprInfo
import org.teogramm.compiler.utilities.Translator
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.v4.runtime.tree.xpath.XPath
import org.teogramm.projectlang.ProjectParser
import org.teogramm.projectlang.ProjectParserBaseListener
import org.teogramm.compiler.output.AssemblyWriter
import org.teogramm.compiler.output.ErrorWriter
import org.teogramm.compiler.output.InstructionList
import org.teogramm.compiler.symtable.SymTableManager
import org.teogramm.compiler.symtable.VarType
import org.teogramm.compiler.utilities.Mover
import org.teogramm.compiler.utilities.Mover.Companion.convertToFloat
import org.teogramm.compiler.utilities.Mover.Companion.createComparisonInstructions
import org.teogramm.compiler.utilities.Mover.Companion.moveOperandToA
import org.teogramm.compiler.utilities.Mover.Companion.moveOperandToTemp
import org.teogramm.compiler.utilities.Mover.Companion.moveToStack
import kotlin.system.exitProcess

class ProjectListener(private val parser: Parser,outputFileName:String): ProjectParserBaseListener() {

    private lateinit var tableManager: SymTableManager
    private val writer = AssemblyWriter(outputFileName)

    /**
     * Adds assembly code to check for overflows
     */
    private fun checkOverflowString():String{
        return "JOV OVERFLOWEX"
    }

    /*
     The functions below simply pass information up
     */

    override fun exitStmt_assign(ctx: ProjectParser.Stmt_assignContext?) {
        if(ctx != null){
            ctx.info = ctx.assign_stmt().info
        }
    }

    override fun exitStmt_for(ctx: ProjectParser.Stmt_forContext?) {
        if(ctx != null){
            ctx.info = ctx.for_stmt().info
        }
    }

    override fun exitStmt_decl(ctx: ProjectParser.Stmt_declContext?) {
        if(ctx != null){
            ctx.info = ctx.declaration().info
        }
    }

    override fun exitStmt_while(ctx: ProjectParser.Stmt_whileContext?) {
        ctx!!.info = ctx.while_stmt().info
    }

    override fun exitAssign_stmt(ctx: ProjectParser.Assign_stmtContext?) {
        ctx!!.info = ctx.assign_expr().info
    }

    override fun exitStmt_comp(ctx: ProjectParser.Stmt_compContext?) {
        // Pass the information along
        ctx!!.info = ctx.comp_stmt().info
    }

    override fun exitOpassign_expr(ctx: ProjectParser.Opassign_exprContext?) {
        if(ctx?.assign_expr() != null) {
            ctx.info = ctx.assign_expr().info
        }
    }

    override fun exitOpbool_expr(ctx: ProjectParser.Opbool_exprContext?) {
        if(ctx?.bool_expr() != null) {
            ctx.info = ctx.bool_expr().info
        }
    }

    override fun exitStmt_null(ctx: ProjectParser.Stmt_nullContext?) {
        ctx!!.info = ExprInfo()
    }

    override fun exitStmt_if(ctx: ProjectParser.Stmt_ifContext?) {
        ctx!!.info = ctx.if_stmt().info
    }

    /*
        Start of the program
     */


    override fun enterProgram(ctx: ProjectParser.ProgramContext?) {
        // Create a symbol table manager for the program
        tableManager = SymTableManager()
    }

    override fun exitProgram(ctx: ProjectParser.ProgramContext?) {
        writer.end(tableManager.getStartAddress())
    }

    override fun enterComp_stmt(ctx: ProjectParser.Comp_stmtContext?) {
        // Every time we go into a new block we create a new symbol table
        tableManager.createTable()
    }

    override fun exitComp_stmt(ctx: ProjectParser.Comp_stmtContext?) {
        // When we exit a block we print the variables inside it and delete its symbol table
        tableManager.printCurrentTable()
        tableManager.removeTable()
        ctx!!.info = ctx.stmt_list().info
    }

    override fun enterStmt_list(ctx: ProjectParser.Stmt_listContext?) {
        ctx!!.info = ExprInfo()
    }

    override fun exitStmt_list(ctx: ProjectParser.Stmt_listContext?) {
        if(ctx?.stmt() != null){
            if(ctx.stmt_list().info != null){
                ctx.info.instructionList.addAll(ctx.stmt_list().info.instructionList)
            }
            if(ctx.stmt().info != null){
                ctx.info.instructionList.addAll(ctx.stmt().info.instructionList)
            }
        }
    }

    override fun exitStmt_println(ctx: ProjectParser.Stmt_printlnContext?) {
        if(ctx != null){
            ctx.info = ExprInfo()
            val instructionList = ctx.info.instructionList
            val op = ctx.expr().info
            // Add the instructions to evaluate the operand
            instructionList.addAll(op.instructionList)
            // Move the operand to the A register
            instructionList.addAll(moveOperandToA(op))
            if(op.type == VarType.INT) {
                // If number is not negative do not print a sign, else print a sign
                instructionList.addInstruction("JANN *+3")
                instructionList.addInstruction("ENT1 45")
                instructionList.addInstruction("ST1 LINE")
                instructionList.addInstruction("CHAR")
                instructionList.addInstruction("STA LINE+1")
                instructionList.addInstruction("STX LINE+2")
                instructionList.addInstruction("OUT LINE(TERM)")
                instructionList.addInstruction("JBUS *(TERM)")
                //Reset negative sign
                instructionList.addInstruction("ENT1 0")
                instructionList.addInstruction("ST1 LINE")
            }
            else{
                instructionList.addAll(Mover.floatPrintInstructions())
            }
            if(writer.loops > 0 ){
                // There are other loops remaining just put the instructions in the list and pass it on
            }else{
                // There is no loop, so write the instructions to the file
                writer.writeAll(instructionList)
            }
        }
    }

    /*
     *--------------------IF---------------------------
     */

    override fun enterIf_stmt(ctx: ProjectParser.If_stmtContext?) {
        if(ctx != null){
            writer.enterControlSequence()
        }
    }

    override fun exitIf_stmt(ctx: ProjectParser.If_stmtContext?) {
        if(ctx != null){
            ctx.info = ctx.bool_expr().info
            // Instruction list already has the instructions necessary for the comparison
            val instructionList = ctx.info.instructionList
            val boolOp = ctx.bool_expr().c_op().text
            val ifInstructions = ctx.stmt().info.instructionList
            val hasElse = ctx.else_part().text != ""
            if(hasElse) {
                // If there is an else we will add an additional jump instruction to skip the else part
                // to the IF PART
                ifInstructions.addInstruction("JMP *+" + (ctx.else_part().info.instructionList.size +1))
            }
            // If condition is not satisfied skip the if part
            instructionList.addInstruction(Translator.reverseComparisonToInstruction(boolOp) + " *+"+ (ifInstructions.size +1).toString())
            instructionList.addAll(ifInstructions)
            if(hasElse){
                // Add the else instructions
                instructionList.addAll(ctx.else_part().info.instructionList)
            }
            writer.exitControlSequence()
            if(writer.loops > 0 ){
                // There are other loops remaining just put the instructions in the list and pass it on
            }else{
                // This is the outermost loop, so write the instructions to the file
                writer.writeAll(instructionList)
            }
        }
    }

    override fun exitElse_part(ctx: ProjectParser.Else_partContext?) {
        if(ctx?.stmt() != null) {
            ctx.info = ctx.stmt().info
        }
    }

    /*
     *-------------------LOOPS-------------------------
     */

    override fun enterFor_stmt(ctx: ProjectParser.For_stmtContext?) {
        if(ctx != null){
            writer.enterControlSequence()
        }
    }

    override fun exitFor_stmt(ctx: ProjectParser.For_stmtContext?) {
        if(ctx != null){
            // If any list is empty we just replace it with an empty one
            // For loop is just while with extra steps
            // We have the assignment which should be run only once during the beginning
            val preAssignmentInstructionList = ctx.opassign_expr(0)?.info?.instructionList ?: InstructionList()
            // Comparison instructions
            val comparisonInstructionList = ctx.opbool_expr()?.info?.instructionList ?: InstructionList()
            val boolOp = ctx.opbool_expr().bool_expr().c_op().text
            // Assignment that is run after each loop
            val postAssignmentInstructionList = ctx.opassign_expr(1)?.info?.instructionList ?: InstructionList()
            val loopInstructions = ctx.stmt().info.instructionList
            ctx.info = ExprInfo()
            val instructionList = ctx.info.instructionList
            instructionList.addAll(preAssignmentInstructionList)
            instructionList.addAll(comparisonInstructionList)
            // Jump ahead to after the end of the loop
            instructionList.addInstruction(Translator.reverseComparisonToInstruction(boolOp) + " *+" + (loopInstructions.size + postAssignmentInstructionList.size + 2).toString())
            instructionList.addAll(loopInstructions)
            instructionList.addAll(postAssignmentInstructionList)
            instructionList.addInstruction("JMP *-" + (instructionList.size-preAssignmentInstructionList.size).toString())
            writer.exitControlSequence()
            if(writer.loops > 0 ){
                // There are other loops remaining just put the instructions in the list and pass it on
            }else{
                // This is the outermost loop, so write the instructions to the file
                writer.writeAll(instructionList)
            }
        }
    }

    override fun enterWhile_stmt(ctx: ProjectParser.While_stmtContext?) {
        if(ctx != null) {
            writer.enterControlSequence()
        }
    }

    override fun exitWhile_stmt(ctx: ProjectParser.While_stmtContext?) {
        if(ctx != null) {
            // Add instructions needed to compare the expression
            ctx.info = ExprInfo()
            ctx.info.instructionList = ctx.bool_expr().info.instructionList
            val instructionList = ctx.info.instructionList
            // Skip the loop if condition is not satisfied
            val boolOp = ctx.bool_expr().c_op().text
            val loopInstructionList = ctx.stmt().info.instructionList
            // +2 Because we add 1 instruction later (JMP *-...) and we want to go to the next instruction
            instructionList.addInstruction(Translator.reverseComparisonToInstruction(boolOp) + " *+" + (loopInstructionList.size+2).toString())
            // Add all the loop instructions
            instructionList.addAll(loopInstructionList)
            // Go back to the start
            instructionList.addInstruction("JMP *-" + instructionList.size)
            writer.exitControlSequence()
            if(writer.loops > 0 ){
                // There are other loops remaining just put the instructions in the list and pass it on
            }else{
                // This is the outermost loop, so write the instructions to the file
                writer.writeAll(instructionList)
            }
        }
    }

    override fun exitDeclaration(ctx: ProjectParser.DeclarationContext?) {
        // Once we have processed the whole subtree of a declaration we know the all the variable names
        // and the type
        ctx!!.info = ExprInfo()
        val instructionList = ctx.info.instructionList
        // Zero the A register
        instructionList.addInstruction("ENTA 0")
        // Convert the type from string to enum VarType
        val variableType = VarType.valueOf(ctx.type().text.toUpperCase())
        // Find all IDs in the subtree
        for(match in XPath.findAll(ctx,"//ID",parser)){
            val symName = (match as  TerminalNode).text
            val symbolRecord = tableManager.putSym(symName)
            if(symbolRecord == null){
                ErrorWriter.error("Variable $symName has already been declared in this scope!",ctx.getStart().line)
                exitProcess(1)
            }else{
                symbolRecord.type = variableType
                instructionList.addInstruction("STA " + symbolRecord.memoryAddr)
            }
        }
        if(writer.writingEnabled ){
            writer.writeAll(instructionList)
        }
    }

    override fun exitExpr(ctx: ProjectParser.ExprContext?) {
        if(ctx != null){
            when(val child = ctx.getChild(0)){
                is ProjectParser.RvalContext -> {
                    ctx.info = child.info
                }
                is ProjectParser.Assign_exprContext->{
                    ctx.info = child.info
                }
            }
        }
    }

    /*
     * ********************ASSIGNMENT************************************
     */
    override fun exitAssign_expr(ctx: ProjectParser.Assign_exprContext?) {
        if(ctx != null) {
            ctx.info = ctx.expr().info
            val exprInfo = ctx.info
            val record = tableManager.getSym(ctx.ID().text)
            val instructionList = ctx.info.instructionList
            if(record == null){
                // When there a variable is used without declaration, we create a new symbol for it and continue parsing
                ErrorWriter.error("Variable " + ctx.ID().text + " used without prior declaration.", ctx.getStart().line)
                exitProcess(1)
            }else{
                // Move expression to Accumulator
                when (val location = exprInfo.location) {
                    // Variable is already in Accumulator
                    ExprInfo.LOCATION_A -> {/*Nothing*/}
                    ExprInfo.LITERAL -> instructionList.addAll(moveOperandToA(exprInfo))
                    else -> instructionList.addInstruction("LDA $location")
                }
                if(exprInfo.type == record.type) {
                    // No conversion needed
                    instructionList.addInstruction("STA " + record.memoryAddr)
                }
                else{
                    if(record.type == VarType.FLOAT && exprInfo.type == VarType.INT){
                        // Int expression to float variable
                        instructionList.addInstruction("FLOT")
                        instructionList.addInstruction("STA " + record.memoryAddr)
                    }else{
                        // Float value to int variable
                        instructionList.addInstruction("FIX")
                        instructionList.addInstruction("STA " + record.memoryAddr)
                        ErrorWriter.warning("Float expression cast to int variable",ctx.getStart().line)
                    }
                }
                // The result of the assignment expression is stored in the variable
                ctx.info.location = record.memoryAddr
            }
            if(writer.writingEnabled ){
                writer.writeAll(instructionList)
            }
        }
    }

    /*
     ******************************BOOLEAN***********************************
     */

    override fun exitBool_expr(ctx: ProjectParser.Bool_exprContext?) {
        if(ctx != null){
            ctx.info = ExprInfo()
            val instructionList = ctx.info.instructionList
            val op1 = ctx.expr(0).info
            val op2 = ctx.expr(1).info
            instructionList.addAll(createComparisonInstructions(op1,op2))
        }
    }

    /*
    ********************************RVAL***********************************
     */

    private fun rvalOperandSetup(op1: ExprInfo, op2: ExprInfo): InstructionList {
        // Things are more complicated here as we can have 2
        // operands of rval and term that need to be calculated separately
        // In order to achieve that we calculate the second (term) operand and store it in
        // the stack
        val instructionList = InstructionList()
        // Calculate second operand
        instructionList.addAll(op2.instructionList)
        // Convert the second operand to float if needed
        if(op1.type == VarType.FLOAT){
            instructionList.addAll(convertToFloat(op2))
        }
        // We need to move op2 to the stack if it is in A or TEMP
        var op2InStack = false
        if(op2.location == ExprInfo.LOCATION_TEMP || op2.location == ExprInfo.LOCATION_A){
            instructionList.addAll(moveToStack(op2))
            op2InStack = true
        }
        // Calculate operand 1
        instructionList.addAll(op1.instructionList)
        // Move operand to the A register
        if(op1.location != ExprInfo.LOCATION_A){
            instructionList.addAll(moveOperandToA(op1))
        }
        // If the second operand is float we need to convert op1 (stored in A) to float
        if(op2.type == VarType.FLOAT){
            instructionList.addAll(convertToFloat(op1))
        }
        // If we need to insert a float literal as the second parameter we need to put op1 in the stack
        var op1InStack = false
        if(op2.type == VarType.FLOAT && op2.location == ExprInfo.LITERAL){
            instructionList.addAll(moveToStack(op1))
            op1InStack = true
        }
        // Insert the literal
        if(op2.location == ExprInfo.LITERAL){
            instructionList.addAll(moveOperandToTemp(op2))
        }
        // Restore op2 result to temp register if it was moved to the stack
        if(op2InStack){
            instructionList.addInstruction("LD5 TOP")
            instructionList.addInstruction("DEC5 1")
            instructionList.addInstruction("LDX STACK,5")
            instructionList.addInstruction("ST5 TOP")
            instructionList.addInstruction("STX TEMP")
            op2.location = ExprInfo.LOCATION_TEMP
        }
        // Restore op1 to the A register
        if(op1InStack){
            instructionList.addInstruction("LD5 TOP")
            instructionList.addInstruction("DEC5 1")
            instructionList.addInstruction("LDA STACK,5")
            instructionList.addInstruction("ST5 TOP")
            op1.location = ExprInfo.LOCATION_A
        }
        return instructionList
    }

    override fun exitRval_rem(ctx: ProjectParser.Rval_remContext?) {
        if(ctx != null) {
            ctx.info = ExprInfo()
            val instructionList = ctx.info.instructionList
            val op1 = ctx.rval().info
            val op2 = ctx.term().info
            instructionList.addAll(rvalOperandSetup(op1,op2))
            if(op1.type == VarType.INT && op2.type == VarType.INT) {
                if (op2.location == ExprInfo.LOCATION_TEMP) {
                    instructionList.addInstruction("SUB TEMP")
                } else {
                    instructionList.addInstruction("SUB " + op2.location)
                }
            }else{
                if (op2.location == ExprInfo.LOCATION_TEMP) {
                    instructionList.addInstruction("FSUB TEMP")
                } else {
                    instructionList.addInstruction("FSUB " + op2.location)
                }
                ctx.info.type = VarType.FLOAT
            }
            ctx.info.location = ExprInfo.LOCATION_A
            instructionList.addInstruction(checkOverflowString())
        }
    }

    override fun exitRval_add(ctx: ProjectParser.Rval_addContext?) {
        // Term location will either be a memory address, the A register or a literal
        if(ctx != null) {
            ctx.info = ExprInfo()
            val instructionList = ctx.info.instructionList
            val op1 = ctx.rval().info
            val op2 = ctx.term().info
            instructionList.addAll(rvalOperandSetup(op1,op2))
            if(op1.type == VarType.INT && op2.type == VarType.INT) {
                if (op2.location == ExprInfo.LOCATION_TEMP) {
                    instructionList.addInstruction("ADD TEMP")
                } else {
                    instructionList.addInstruction("ADD " + op2.location)
                }
            }else{
                if (op2.location == ExprInfo.LOCATION_TEMP) {
                    instructionList.addInstruction("FADD TEMP")
                } else {
                    instructionList.addInstruction("FADD " + op2.location)
                }
                ctx.info.type = VarType.FLOAT
            }
            ctx.info.location = ExprInfo.LOCATION_A
            instructionList.addInstruction(checkOverflowString())
        }
    }

    override fun exitRval_term(ctx: ProjectParser.Rval_termContext?) {
        if(ctx != null){
            ctx.info = ctx.term().info
        }
    }

    /*
    ********************************TERM***********************************
     */

    private fun termOperandSetup(op1: ExprInfo, op2: ExprInfo): InstructionList {
        // Things are more complicated here as we can have 2
        // operands of rval and term that need to be calculated separately
        // In order to achieve that we calculate the second (term) operand and store it in
        // the stack
        val instructionList = InstructionList()
        // Calculate first operand
        instructionList.addAll(op1.instructionList)
        // Move the operand to Accumulator
        if(op1.location != ExprInfo.LOCATION_A){
            instructionList.addAll(moveOperandToA(op1))
        }
        // If the second result will be float we need to convert this
        // result to float too. For this we need to store op1 in the stack
        if(op2.type == VarType.FLOAT){
            instructionList.addAll(convertToFloat(op1))
        }
        // We will need to store the value in the stack if:
        // The second operand will store its result in the accumulator
        // We will need to convert a float literal
        // We will need to convert op2 to float
        var op1inStack = false
        if(op2.location == ExprInfo.LOCATION_A || (op2.type == VarType.FLOAT && op2.location == ExprInfo.LITERAL)
            ||(op1.type == VarType.FLOAT && op2.type != VarType.FLOAT)){
            instructionList.addAll(moveToStack(op1))
            op1inStack = true
        }
        // Calculate second operand
        instructionList.addAll(op2.instructionList)
        // Move the operand to temp register
        if(op2.location < 0) {
            instructionList.addAll(moveOperandToTemp(op2))
        }
        if(op1.type == VarType.FLOAT && op2.type != VarType.FLOAT){
            instructionList.addAll(convertToFloat(op2))
        }
        // Restore stack top to A, if needed
        if(op1inStack) {
            instructionList.addInstruction("LD5 TOP")
            instructionList.addInstruction("DEC5 1")
            instructionList.addInstruction("LDA STACK,5")
            instructionList.addInstruction("ST5 TOP")
        }
        return instructionList
    }

    override fun exitTerm_div(ctx: ProjectParser.Term_divContext?) {
        if (ctx != null) {
            ctx.info = ExprInfo()
            val instructionList = ctx.info.instructionList
            val op1 = ctx.term().info
            val op2 = ctx.factor().info
            instructionList.addAll(termOperandSetup(op1,op2))
            // Division expects the number in X register
            if(op1.type== VarType.INT && op2.type == VarType.INT) {
                instructionList.addInstruction("SRAX 5")
                if (op2.location == ExprInfo.LOCATION_TEMP) {
                    instructionList.addInstruction("DIV TEMP")
                } else {
                    instructionList.addInstruction("DIV " + ctx.factor().info.location)
                }
            }else{
                ctx.info.type = VarType.FLOAT
                if (op2.location == ExprInfo.LOCATION_TEMP) {
                    instructionList.addInstruction("FDIV TEMP")
                } else {
                    instructionList.addInstruction("FDIV " + ctx.factor().info.location)
                }
            }
            ctx.info.location = ExprInfo.LOCATION_A
            instructionList.addInstruction(checkOverflowString())
        }
    }

    override fun exitTerm_multipl(ctx: ProjectParser.Term_multiplContext?) {
        // We always want the left term to be in the A register
        if (ctx != null) {
            ctx.info = ExprInfo()
            val instructionList = ctx.info.instructionList
            val op1 = ctx.term().info
            val op2 = ctx.factor().info
            instructionList.addAll(termOperandSetup(op1,op2))
            if(op1.type== VarType.INT && op2.type == VarType.INT) {
                if (op2.location == ExprInfo.LOCATION_TEMP) {
                    instructionList.addInstruction("MUL TEMP")
                } else {
                    instructionList.addInstruction("MUL " + ctx.factor().info.location)
                }
                instructionList.addInstruction("JANZ OVERFLOWEX")
                instructionList.addInstruction("SLAX 5")
            }else{
                ctx.info.type = VarType.FLOAT
                if (op2.location == ExprInfo.LOCATION_TEMP) {
                    instructionList.addInstruction("FMUL TEMP")
                } else {
                    instructionList.addInstruction("FMUL " + ctx.factor().info.location)
                }
                instructionList.addInstruction("JOV OVERFLOWEX")
            }
            ctx.info.location = ExprInfo.LOCATION_A
        }
    }

    override fun exitSimplefactor(ctx: ProjectParser.SimplefactorContext?) {
        // If no operation simply pass the information up
        ctx!!.info = ctx.factor().info
    }

    /*
     ************************FACTOR************************************
     */

    override fun exitFactor_par(ctx: ProjectParser.Factor_parContext?) {
        // We need to make sure this is evaluated first
        ctx!!.info = ctx.expr().info
    }

    override fun exitFactor_int(ctx: ProjectParser.Factor_intContext?) {
        ctx!!.info = ExprInfo()
        ctx.info.location = ExprInfo.LITERAL
        ctx.info.literalValue = ctx.INT().text
        ctx.info.type = VarType.INT
    }

    override fun exitFactor_float(ctx: ProjectParser.Factor_floatContext?) {
        ctx!!.info = ExprInfo()
        ctx.info.location = ExprInfo.LITERAL
        ctx.info.literalValue = ctx.FLOAT().text
        ctx.info.type = VarType.FLOAT
    }

    override fun exitFactor_uminus(ctx: ProjectParser.Factor_uminusContext?) {
        if(ctx != null) {
            ctx.info = ctx.factor().info
            val instructionList = ctx.info.instructionList
            when(ctx.info.location){
                ExprInfo.LOCATION_A->{
                    // If the factor is in A we need to first move it into TEMP and
                    // restore it to A with negative sign
                    instructionList.addInstruction("STA TEMP")
                    instructionList.addInstruction("LDAN TEMP")
                }
                ExprInfo.LITERAL->{
                    // If we lave a literal we can just prepend the minus sign
                    ctx.info.literalValue = "-" + ctx.info.literalValue
                }
                else->{
                    // Else we need to store the variable in TEMP
                    // and change its location
                    val location = ctx.info.location
                    instructionList.addInstruction("LDXN $location")
                    instructionList.addInstruction("STX TEMP")
                    ctx.info.location = ExprInfo.LOCATION_TEMP
                }
            }
        }
    }

    override fun exitFactor_var_reference(ctx: ProjectParser.Factor_var_referenceContext?) {
        val record = tableManager.getSym(ctx!!.ID().text)
        if ( record == null) {
            ErrorWriter.error("Variable " + ctx.ID().text + " used without prior declaration.",ctx.getStart().line)
            exitProcess(1)
        }
        ctx.info = ExprInfo()
        ctx.info.location = record.memoryAddr
        ctx.info.type = record.type
    }
}