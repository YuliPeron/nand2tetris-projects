import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CompilationEngine {

    private JackTokenizer tokenizer;
    private BufferedWriter writer;

    // creates a new compilation engine with the given input and output files
    public CompilationEngine(String inputFile, String outputFile) throws IOException {
        tokenizer = new JackTokenizer(inputFile);
        writer = new BufferedWriter(new FileWriter(outputFile));
    }

    // Closes the writer
    public void close() throws IOException {
        writer.close();
    }

    // Compiles a complete class
    public void compileClass() throws IOException {
        tokenizer.advance();
        writer.write("<class>\n");

        writeToken("keyword"); // class keyword

        writeToken("identifier"); // class name

        writeToken("symbol"); // opening curly brace

        // class variable declarations
        while (tokenizer.hasMoreTokens() &&
                tokenizer.tokenType() == JackTokenizer.tokenTypes.KEYWORD &&
                (tokenizer.getCurrentToken().equals("static") ||
                        tokenizer.getCurrentToken().equals("field"))) {
            compileClassVarDec();
        }
        // subroutine declarations
        while (tokenizer.hasMoreTokens() &&
                tokenizer.tokenType() == JackTokenizer.tokenTypes.KEYWORD &&
                (tokenizer.getCurrentToken().equals("constructor") ||
                        tokenizer.getCurrentToken().equals("function") ||
                        tokenizer.getCurrentToken().equals("method"))) {
            compileSubroutine();
        }

            writeToken("symbol");  // writes final '}'

        writer.write("</class>\n");
    }

    // Compiles a static variable declaration or a field declaration
    public void compileClassVarDec() throws IOException {
        writer.write("<classVarDec>\n");
        writeToken("keyword"); // static or field
        if (tokenizer.tokenType() == JackTokenizer.tokenTypes.KEYWORD) {
            writeToken("keyword"); // type like int, boolean, etc.
        } else {
            writeToken("identifier"); // class type
        }
        writeToken("identifier"); // varName
        while (tokenizer.tokenType() == JackTokenizer.tokenTypes.SYMBOL && tokenizer.symbol() == ',') {
            writeToken("symbol"); // comma
            writeToken("identifier"); // varName
        }
        writeToken("symbol"); // semicolon
        writer.write("</classVarDec>\n");
    }

    // Compiles a complete method, function, or constructor
    public void compileSubroutine() throws IOException {
        writer.write("<subroutineDec>\n");
        writeToken("keyword"); // constructor, function, or method
        if (tokenizer.tokenType() == JackTokenizer.tokenTypes.KEYWORD) {
            writeToken("keyword"); // return type like void, int, etc.
        } else {
            writeToken("identifier"); // class return type
        }
        writeToken("identifier"); // subroutine name
        writeToken("symbol"); // opening parenthesis
        compileParameterList(); // parameter list
        writeToken("symbol"); // closing parenthesis
        compileSubroutineBody(); // subroutine body
        writer.write("</subroutineDec>\n");
    }

    // Compiles the parameter list of a subroutine
    public void compileParameterList() throws IOException {
        writer.write("<parameterList>\n");
        if (tokenizer.tokenType() == JackTokenizer.tokenTypes.KEYWORD || tokenizer.tokenType() == JackTokenizer.tokenTypes.IDENTIFIER) {
            writeToken("keyword"); // type
            writeToken("identifier"); // varName
            while (tokenizer.tokenType() == JackTokenizer.tokenTypes.SYMBOL && tokenizer.symbol() == ',') {
                writeToken("symbol"); // comma
                writeToken("keyword"); // type
                writeToken("identifier"); // varName
            }
        }
        writer.write("</parameterList>\n");
    }

    // Compiles the body of a subroutine
    public void compileSubroutineBody() throws IOException {
        writer.write("<subroutineBody>\n");
        writeToken("symbol"); // opening curly brace
        while (tokenizer.tokenType() == JackTokenizer.tokenTypes.KEYWORD && tokenizer.getCurrentToken().equals("var")) {
            compileVarDec();
        }
        compileStatements(); // compile statements inside the body
        writeToken("symbol"); // closing curly brace
        writer.write("</subroutineBody>\n");
    }

    // Compiles a var declaration
    public void compileVarDec() throws IOException {
        writer.write("<varDec>\n");
        writeToken("keyword"); // var
        if (tokenizer.tokenType() == JackTokenizer.tokenTypes.KEYWORD) {
            writeToken("keyword"); // type
        } else {
            writeToken("identifier"); // type (class name)
        }
        writeToken("identifier"); // varName
        while (tokenizer.tokenType() == JackTokenizer.tokenTypes.SYMBOL && tokenizer.symbol() == ',') {
            writeToken("symbol"); // comma
            writeToken("identifier"); // varName
        }
        writeToken("symbol"); // semicolon
        writer.write("</varDec>\n");
    }

    // Compiles a sequence of statements
    public void compileStatements() throws IOException {
            writer.write("<statements>\n");
            while (tokenizer.getCurrentToken().equals("let") || tokenizer.getCurrentToken().equals("if") ||
                    tokenizer.getCurrentToken().equals("while") || tokenizer.getCurrentToken().equals("do") ||
                    tokenizer.getCurrentToken().equals("return")) {
                if (tokenizer.getCurrentToken().equals("let")) {
                    compileLet();
                } else if (tokenizer.getCurrentToken().equals("if")) {
                    compileIf();
                } else if (tokenizer.getCurrentToken().equals("while")) {
                    compileWhile();
                } else if (tokenizer.getCurrentToken().equals("do")) {
                    compileDo();
                } else if (tokenizer.getCurrentToken().equals("return")) {
                    compileReturn();
                }
            }
            writer.write("</statements>\n");
    }


    // Compiles a let statement
    public void compileLet() throws IOException {
        writer.write("<letStatement>\n");
        writeToken("keyword"); // let
        writeToken("identifier"); // varName

        // Check if this is an array assignment (varName[expression] = expression)
        if (tokenizer.tokenType() == JackTokenizer.tokenTypes.SYMBOL && tokenizer.symbol() == '[') {
            writeToken("symbol"); // [
            compileExpression();
            writeToken("symbol"); // ]
            writeToken("symbol"); // =
            compileExpression();
        } else {
            writeToken("symbol"); // =
            compileExpression();
        }

        writeToken("symbol"); // ;
        writer.write("</letStatement>\n");
    }

    // Compiles an if statement
    public void compileIf() throws IOException {
        writer.write("<ifStatement>\n");
        writeToken("keyword"); // if
        writeToken("symbol"); // (
        compileExpression(); // condition
        writeToken("symbol"); // )
        writeToken("symbol"); // {
        compileStatements(); // if body
        writeToken("symbol"); // }
        // Handles optional else clause
        if (tokenizer.tokenType() == JackTokenizer.tokenTypes.KEYWORD && tokenizer.getCurrentToken().equals("else")) {
            writeToken("keyword"); // else
            writeToken("symbol"); // {
            compileStatements(); // else body
            writeToken("symbol"); // }
        }
        writer.write("</ifStatement>\n");
    }

    // Compiles a while statement
    public void compileWhile() throws IOException {
        writer.write("<whileStatement>\n");
        writeToken("keyword"); // while
        writeToken("symbol"); // (
        compileExpression(); // condition
        writeToken("symbol"); // )
        writeToken("symbol"); // {
        compileStatements(); // while body
        writeToken("symbol"); // }
        writer.write("</whileStatement>\n");
    }

    // Compiles a do statement
    public void compileDo() throws IOException {
        writer.write("<doStatement>\n");
        writeToken("keyword"); // do
        writeToken("identifier"); // className/varName
        if (tokenizer.tokenType() == JackTokenizer.tokenTypes.SYMBOL && tokenizer.symbol() == '.') {
            writeToken("symbol"); // .
            writeToken("identifier"); // subroutineName
        }

        writeToken("symbol"); // (
        compileExpressionList(); // arguments
        writeToken("symbol"); // )
        writeToken("symbol"); // ;
        writer.write("</doStatement>\n");

    }

    // Compiles a return statement
    public void compileReturn() throws IOException {
        writer.write("<returnStatement>\n");
        writeToken("keyword"); // return
        if (tokenizer.tokenType() != JackTokenizer.tokenTypes.SYMBOL || tokenizer.symbol() != ';') {
            compileExpression(); // optional return value
        }
        writeToken("symbol"); // semicolon
        writer.write("</returnStatement>\n");
    }

    // Compiles an expression
    public void compileExpression() throws IOException {
        writer.write("<expression>\n");
        compileTerm(); // first term

        // Look for possible operators
        while (tokenizer.tokenType() == JackTokenizer.tokenTypes.SYMBOL &&
                "+-*/&|<>=".indexOf(tokenizer.symbol()) != -1) {
            writeToken("symbol"); // operator
            compileTerm(); // next term
        }

        writer.write("</expression>\n");
    }

    // Compiles a term
    public void compileTerm() throws IOException {
        writer.write("<term>\n");
        if (tokenizer.tokenType() == JackTokenizer.tokenTypes.STRING_CONST) {
            writeToken("stringConstant");
        }
        if (tokenizer.tokenType() == JackTokenizer.tokenTypes.IDENTIFIER) {
            writeToken("identifier");
            if (tokenizer.tokenType() == JackTokenizer.tokenTypes.SYMBOL) {
                if (tokenizer.symbol() == '[') {
                    writeToken("symbol"); // [
                    compileExpression();
                    writeToken("symbol"); // ]
                } else if (tokenizer.symbol() == '(' || tokenizer.symbol() == '.') {
                    if (tokenizer.symbol() == '.') {
                        writeToken("symbol"); // .
                        writeToken("identifier");
                    }
                    writeToken("symbol"); // (
                    compileExpressionList();
                    writeToken("symbol"); // )
                }
            }
        } else if (tokenizer.tokenType() == JackTokenizer.tokenTypes.INT_CONST) {
            writeToken("integerConstant");
        } else if (tokenizer.tokenType() == JackTokenizer.tokenTypes.KEYWORD) {
            writeToken("keyword");
        } else if (tokenizer.tokenType() == JackTokenizer.tokenTypes.SYMBOL) {
            if (tokenizer.symbol() == '(') {
                writeToken("symbol"); // (
                compileExpression();
                writeToken("symbol"); // )
            } else if ("~-".indexOf(tokenizer.symbol()) != -1) {
                writeToken("symbol"); // unary op
                compileTerm();
            }
        }

        writer.write("</term>\n");
    }

    // Compiles a list of expressions
    public int compileExpressionList() throws IOException {
        writer.write("<expressionList>\n");
        int expressionCount = 0;
        if (tokenizer.tokenType() != JackTokenizer.tokenTypes.SYMBOL || tokenizer.symbol() != ')') {
            compileExpression(); // first expression
            expressionCount++;
            while (tokenizer.tokenType() == JackTokenizer.tokenTypes.SYMBOL && tokenizer.symbol() == ',') {
                writeToken("symbol"); // comma
                compileExpression(); // next expression
                expressionCount++;
            }
        }
        writer.write("</expressionList>\n");
        return expressionCount;
    }

    // Writes the current token to the output as an XML element
    private void writeToken(String expectedType) throws IOException {
        JackTokenizer.tokenTypes actualType = tokenizer.tokenType();
        String value = "";

        if ((expectedType.equals("keyword") && actualType != JackTokenizer.tokenTypes.KEYWORD) ||
                (expectedType.equals("symbol") && actualType != JackTokenizer.tokenTypes.SYMBOL) ||
                (expectedType.equals("identifier") && actualType != JackTokenizer.tokenTypes.IDENTIFIER) ||
                (expectedType.equals("integerConstant") && actualType != JackTokenizer.tokenTypes.INT_CONST) ||
                (expectedType.equals("stringConstant") && actualType != JackTokenizer.tokenTypes.STRING_CONST)) {
            throw new IllegalStateException("Expected " + expectedType + " but got " + actualType);
        }

        switch (actualType) {
            case KEYWORD:
                value = tokenizer.keyWord().toString().toLowerCase();
                break;
            case SYMBOL:
                char symbol = tokenizer.symbol();
                switch (symbol) {
                    case '<':
                        value = "&lt;";
                        break;
                    case '>':
                        value = "&gt;";
                        break;
                    case '"':
                        value = "&quot;";
                        break;
                    case '&':
                        value = "&amp;";
                        break;
                    default:
                        value = String.valueOf(symbol);
                }
                break;
            case IDENTIFIER:
                value = tokenizer.identifier();
                break;
            case INT_CONST:
                value = String.valueOf(tokenizer.intVal());
                break;
            case STRING_CONST:
                value = tokenizer.stringVal();
                break;
        }
        writer.write("<" + expectedType + "> " + value + " </" + expectedType + ">\n");
        if (tokenizer.hasMoreTokens()) {
            tokenizer.advance();
        }
    }
}