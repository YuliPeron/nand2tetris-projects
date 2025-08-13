import java.io.*;


public class CodeWriter {
    PrintWriter output;
    private int counter = 0;

    public CodeWriter(String file) throws IOException {
        String outputName = file + ".asm";
        this.output = new PrintWriter(outputName);

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

    public void writePushPop (Parser.types commandType, String segment, int index){

        if (commandType == Parser.types.C_PUSH) {
            if (segment.equals("constant")) {
                output.println("@" + index);
                output.println("D=A");
                output.println("@SP");
                output.println("A=M");
                output.println("M=D");
                output.println("@SP");
                output.println("M=M+1");
            } else if (segment.equals("temp") || segment.equals("pointer") || segment.equals("static")) {
                String baseAddress = RAM(segment);
                int actualAddress = segment.equals("temp") ? 5 + index : segment.equals("pointer") ? 3 + index : 16 + index;
                output.println("@" + actualAddress);
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
            if (segment.equals("temp") || segment.equals("pointer") || segment.equals("static")) {
                int actualAddress = segment.equals("temp") ? 5 + index : segment.equals("pointer") ? 3 + index : 16 + index;
                output.println("@SP");
                output.println("AM=M-1");
                output.println("D=M");
                output.println("@" + actualAddress);
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

}