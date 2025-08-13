import java.io.IOException;
import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.print.DocFlavor.READER;
import java.io.PrintWriter;

public class HackAssembler {


    public void assembler(String input) throws IOException {

        // initialize a new SymbolTable to store predefined symbols and labels
        SymbolTable symbolTable = new SymbolTable();

        // add predefined symbols (R0 to R15) to the SymbolTable with their corresponding addresses
        for(int i = 0; i <= 15; i++){
            symbolTable.addEntry("R"+i, i);
        }

        // add more predefined symbols with fixed addresses (SCREEN, KBD, SP, etc.)
        symbolTable.addEntry("SCREEN", 16384);
        symbolTable.addEntry("KBD", 24576);
        symbolTable.addEntry("SP", 0);
        symbolTable.addEntry("LCL", 1);
        symbolTable.addEntry("ARG", 2);
        symbolTable.addEntry("THIS", 3);
        symbolTable.addEntry("THAT", 4);

        // Create a Parser object to read and process the input file provided in args[0].
        Parser parser = new Parser(input);
        String output = "";
        int countLabel = 0;

        // first pass: Identify and store all label symbols (L-instructions) in the SymbolTable
        while(parser.hasMoreLines()){
            if(parser.instructionType() == INSTRUCTION_Type.L_INSTRUCTION){
                if(!symbolTable.contains(parser.symbol())){
                    symbolTable.addEntry(parser.symbol(), parser.counterLine - countLabel);
                    countLabel++;
                }
            }
            parser.advance();
        }

        int num =16;
        Parser secPassParser = new Parser(input);
        // second pass: translate A- and C-instructions to binary code
        while(secPassParser.hasMoreLines()){
            System.out.println("Processing line: " + secPassParser.instructions.get(secPassParser.counterLine));
            INSTRUCTION_Type type = secPassParser.instructionType();
            System.out.println("Instruction type detected: " + type);
            String command = "";

            if(type == INSTRUCTION_Type.A_INSTRUCTION) {
                String symbol = secPassParser.symbol();
                System.out.println("A-instruction detected: " + symbol);
                String value = "";
                if(!isNumber(symbol)){
                    if(!symbolTable.contains(symbol)){
                        symbolTable.addEntry(symbol, num++);
                    }
                    value = intToBinary(symbolTable.getAddress(symbol), 15);
                }
                else {
                    value = intToBinary(Integer.parseInt(symbol), 15);
                }
                command = "0" + value;
            }
            else if (type == INSTRUCTION_Type.C_INSTRUCTION){
                System.out.println("C-instruction detected.");
                String comp = secPassParser.comp();
                String dest = secPassParser.dest();
                String jump = secPassParser.jump();

                System.out.println("comp: " + comp + ", dest: " + dest + ", jump: " + jump);

                String compBinary = Code.comp(comp);
                String destBinary = Code.dest(dest);
                String jumpBinary = Code.jump(jump);

                System.out.println("Binary translation -> comp: " + compBinary + ", dest: " + destBinary + ", jump: " + jumpBinary);

                command = "111" + compBinary + destBinary + jumpBinary;
            }
            if(!command.isEmpty()){
                output += command + "\n";
            }
            else {
                System.out.println("No command generated for this line.");
            }
            secPassParser.advance();
            System.out.println("Advanced to next line.");
        }

        // create a File object for the input file from the first command-line argument
        File file = new File(input.trim());

        // check if the file exists. If not, print an error and exit
        if (!file.exists()) {
            System.out.println("Error");
            return;
        }

        // generate the output file path by replacing .asm with .hack
        String outpath = file.getAbsolutePath().replace(".asm", ".hack");
        //System.out.println("Writing output to: " + outpath);
        System.out.println("Generated binary content:");
        System.out.println(output);


        // write the output data to the new .hack file.
        try (PrintWriter pw = new PrintWriter(outpath)) {
            pw.print(output);
        } catch (FileNotFoundException e) {
            System.out.println("Cannot write to file.");
        }

    }


    //checks if the given string is a number
    public static boolean isNumber(String str) {
        if (str.length() == 0) {
            return false;
        }

        for (char c : str.toCharArray())
        {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    //converts an integer into a binary string, padded with zeros to match the specified number of bits
    public static String intToBinary(int number, int bitNumber) {
        return String.format("%" + bitNumber +"s", Integer.toBinaryString(number)).replace(' ', '0');
    }

}
