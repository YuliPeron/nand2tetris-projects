import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String inputFile = args[0];
        String outputFileName = inputFile.substring(0,inputFile.indexOf("."));
        CodeWriter outputFile = new CodeWriter(outputFileName);
        Parser parser = new Parser(inputFile);


        while (parser.hasMoreLines()){
            parser.advance();
            if (parser.commandType() == Parser.types.C_ARITHMETIC) {
                outputFile.writeArithmetic(parser.arg1());
            }
            else {
                outputFile.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
            }
        }
        outputFile.close();
    }
}