import java.io.*;

public class CodeWriter {
    private PrintWriter output;
    private int counter = 0;
    private String fileName;
    private int counterLabel=0;

    public CodeWriter(String file, boolean isBoot) throws IOException {
        this.output = new PrintWriter(file);
        if (isBoot) {
            BootstrapCode();
        }
    }

    private void BootstrapCode() throws IOException {
        output.println("@256");
        output.println("D=A");
        output.println("@SP");
        output.println("M=D");
        writeCall("Sys.init", 0);
    }

    public void setFileName(String fileName){
        this.fileName=fileName;
    }

    public void writeLabel(String label){
        output.println("("+label+")");
    }

    public void writeGoto(String label){
        output.println("@" + label);
        output.println("0;JMP");
    }

    public void writeIf(String label) {
        output.println("@SP");
        output.println("AM=M-1"); //Reduce SP and access the top value of the stack
        output.println("D=M");   // Store the top stack value into D
        output.println("@" + label);
        output.println("D;JNE"); //Jump to label if D != 0
    }

    public void writeFunction(String functionName, int nVars) {
        output.println("(" + functionName + ")");
        for (int i =0; i< nVars; i ++)
        {
            output.println("@SP");
            output.println("A=M");
            output.println("M=0");
            output.println("@SP");
            output.println("M=M+1"); // Increment SP to move to the next stack position
        }
    }

    public void writeCall(String functionName, int nArgs)  throws IOException{
        String labelReturn = "RETURN_" + counterLabel ;
        counterLabel++;
        output.println("@" + labelReturn);
        output.println("D=A");
        pushDReg();

        //push and save the caller's segment pointers
        saveSegment("LCL");
        saveSegment("ARG");
        saveSegment("THIS");
        saveSegment("THAT");

        //Reposition ARG
        output.println("@SP");
        output.println("D=M");
        output.println("@"+ (nArgs+5));
        output.println("D=D-A");
        output.println("@ARG");
        output.println("M=D");

        //Reposition LCL
        output.println("@SP");
        output.println("D=M");
        output.println("@LCL");
        output.println("M=D");

        //goto to function
        //writeGoto(functionName);
        output.println("@" + functionName);
        output.println("0;JMP");

        //return Label
        output.println("(" + labelReturn + ")");
    }

    //push to D register
    private void pushDReg() throws IOException{
        output.println("@SP");
        output.println("A=M");
        output.println("M=D");
        output.println("@SP");
        output.println("M=M+1");
    }

    private void saveSegment(String segment) throws  IOException{
        output.println("@" + segment);
        output.println("D=M");
        pushDReg();
    }

    private void popDReg() throws IOException{
        output.println("@SP");
        output.println("AM=M-1");
        output.println("D=M");
    }

    public void writeReturn()  throws IOException{

        //gets the address at the frame's end
        output.println("@LCL");
        output.println("D=M");
        output.println("@R13");
        output.println("M=D");

        //gets the return address
        output.println("@5");
        output.println("A=D-A");
        output.println("D=M");
        output.println("@R14");
        output.println("M=D");

        //puts the return value for the caller
        popDReg();
        output.println("@ARG");
        output.println("A=M");
        output.println("M=D");

        //reposition SP
        output.println("@ARG");
        output.println("D=M+1");
        output.println("@SP");
        output.println("M=D");

        // Restore THAT, THIS, ARG, LCL
        restoreSegment("THAT", 1);
        restoreSegment("THIS", 2);
        restoreSegment("ARG", 3);
        restoreSegment("LCL", 4);

        // Jump to RET
        output.println("@R14");
        output.println("A=M");
        output.println("0;JMP");


    }


    private void restoreSegment(String segment, int offset) throws IOException {
        output.println("@R13");
        output.println("D=M");
        output.println("@" + offset);
        output.println("A=D-A");
        output.println("D=M");
        output.println("@" + segment);
        output.println("M=D");
    }

    public void writeArithmetic (String command){
        if (command.equals("add")) {
            output.println("@SP");
            output.println("AM=M-1");
            output.println("D=M");
            output.println("@SP");
            output.println("AM=M-1");
            output.println("M=D+M");
            output.println("@SP");
            output.println("M=M+1");
        } else if (command.equals("sub")) {
            output.println("@SP");
            output.println("AM=M-1");
            output.println("D=M");
            output.println("@SP");
            output.println("AM=M-1");
            output.println("M=M-D");
            output.println("@SP");
            output.println("M=M+1");
        } else if (command.equals("neg")) {
            output.println("@SP");
            output.println("AM=M-1");
            output.println("M=-M");
            output.println("@SP");
            output.println("M=M+1");
        } else if (command.equals("eq") || command.equals("gt") || command.equals("lt")) {
            String trueLabel = "TRUE_" + counter;
            String endLabel = "END_" + counter;
            counter++;

//            //
//            String trueLabel = "TRUE_" + counterLabel;
//            String endLabel = "END_" + counterLabel;
//            counterLabel++;
//            //

            output.println("@SP");
            output.println("AM=M-1");
            output.println("D=M");
            output.println("@SP");
            output.println("AM=M-1");
            output.println("D=M-D");
            output.println("@" + trueLabel);
            if (command.equals("eq")) {
                output.println("D;JEQ");
            } else if (command.equals("gt")) {
                output.println("D;JGT");
            } else if (command.equals("lt")) {
                output.println("D;JLT");
            }
            output.println("@SP");
            output.println("A=M");
            output.println("M=0");
            output.println("@" + endLabel);
            output.println("0;JMP");
            output.println("(" + trueLabel + ")");
            output.println("@SP");
            output.println("A=M");
            output.println("M=-1");
            output.println("(" + endLabel + ")");
            output.println("@SP");
            output.println("M=M+1");
        } else if (command.equals("and")) {
            output.println("@SP");
            output.println("AM=M-1");
            output.println("D=M");
            output.println("@SP");
            output.println("AM=M-1");
            output.println("M=D&M");
            output.println("@SP");
            output.println("M=M+1");
        } else if (command.equals("or")) {
            output.println("@SP");
            output.println("AM=M-1");
            output.println("D=M");
            output.println("@SP");
            output.println("AM=M-1");
            output.println("M=D|M");
            output.println("@SP");
            output.println("M=M+1");
        } else if (command.equals("not")) {
            output.println("@SP");
            output.println("AM=M-1");
            output.println("M=!M");
            output.println("@SP");
            output.println("M=M+1");
        }


    }

    public void writePushPop(Parser.types commandType, String segment, int index) {
        if (commandType == Parser.types.C_PUSH) {
            if (segment.equals("constant")) {
                output.println("@" + index);
                output.println("D=A");
                output.println("@SP");
                output.println("A=M");
                output.println("M=D");
                output.println("@SP");
                output.println("M=M+1");
            } else if (segment.equals("static")) {
                output.println("@" + fileName + "." + index);
                output.println("D=M");
                output.println("@SP");
                output.println("A=M");
                output.println("M=D");
                output.println("@SP");
                output.println("M=M+1");
            } else if (segment.equals("temp") || segment.equals("pointer")) {
                int baseAddress = segment.equals("temp") ? 5 : 3;
                output.println("@" + (baseAddress + index));
                output.println("D=M");
                output.println("@SP");
                output.println("A=M");
                output.println("M=D");
                output.println("@SP");
                output.println("M=M+1");
            } else {
                output.println("@" + index);
                output.println("D=A");
                output.println("@" + RAM(segment));
                output.println("A=D+M");
                output.println("D=M");
                output.println("@SP");
                output.println("A=M");
                output.println("M=D");
                output.println("@SP");
                output.println("M=M+1");
            }
        } else if (commandType == Parser.types.C_POP) {
            if (segment.equals("static")) {
                output.println("@SP");
                output.println("AM=M-1");
                output.println("D=M");
                output.println("@" + fileName + "." + index);
                output.println("M=D");
            } else if (segment.equals("temp") || segment.equals("pointer")) {
                int baseAddress = segment.equals("temp") ? 5 : 3;
                output.println("@SP");
                output.println("AM=M-1");
                output.println("D=M");
                output.println("@" + (baseAddress + index));
                output.println("M=D");
            } else {
                output.println("@" + index);
                output.println("D=A");
                output.println("@" + RAM(segment));
                output.println("D=D+M");
                output.println("@R13");
                output.println("M=D");
                output.println("@SP");
                output.println("AM=M-1");
                output.println("D=M");
                output.println("@R13");
                output.println("A=M");
                output.println("M=D");
            }
        }
    }

    //

    public String RAM (String segment) {
        if (segment.equals("constant")) {
            return "SP";
        }
        if (segment.equals("local")) {
            return "LCL";
        }
        if (segment.equals("argument")) {
            return "ARG";
        }
        if (segment.equals("this")) {
            return "THIS";
        }
        if (segment.equals("that")) {
            return "THAT";
        }
        return null;
    }
    public void close() {
        output.close();
    }


    //




}