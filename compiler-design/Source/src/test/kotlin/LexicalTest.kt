import org.antlr.v4.runtime.CharStreams
import org.junit.Test
import org.teogramm.projectlang.ProjectLexer
import java.util.*
import kotlin.test.assertEquals

class LexicalTest {

    fun lexerForCode(code:String) = ProjectLexer(CharStreams.fromString(code))

    fun tokens(lexer: ProjectLexer) : List<String> {
        val tokens = LinkedList<String>()
        var t = lexer.nextToken()
        while (t.type != -1) {
            if (t.type != ProjectLexer.WSP) {
                tokens.add(lexer.ruleNames[t.type - 1])
            }
            t = lexer.nextToken();
        }
        return tokens.toList()
    }

    @Test fun checkAssignment(){
        assertEquals(listOf("ID", "ASSIGN", "INT", "SEMICOL"),tokens(lexerForCode("inter=1;")))
    }

    @Test fun checkWhile(){
        assertEquals(listOf("WHILE","OPENPAR","ID","LT","FLOAT","CLOSEPAR","OPENBRACK","SEMICOL","CLOSEBRACK"),
            tokens(lexerForCode("while(i<5.5){;}")))
    }
}