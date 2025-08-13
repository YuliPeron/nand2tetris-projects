import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        // Check if the correct number of arguments is provided
        if (args.length != 1) {
            System.out.println("Usage: Main <file.vm | directory>");
            return;
        }

        File inputPath = new File(args[0]);

        // Determine whether the input is a directory or a .vm file
        if (inputPath.isDirectory()) {
            processDirectory(inputPath);
        } else if (inputPath.isFile() && inputPath.getName().endsWith(".vm")) {
            processFile(inputPath);
        } else {
            System.out.println("Input must be a .vm file or a directory containing .vm files.");
        }
    }

    private static void processDirectory(File dir) throws IOException {
        // Generate the output file name based on the directory name
        String outputFileName = dir.getPath() + "/" + dir.getName() + ".asm";
        // Enable bootstrap code for directories
        CodeWriter output = new CodeWriter(outputFileName, true);

        // Retrieve all .vm files in the directory
        File[] vmFiles = dir.listFiles((d, name) -> name.endsWith(".vm"));
        if (vmFiles == null || vmFiles.length == 0) {
            System.out.println("No .vm files found in directory: " + dir.getPath());
            return;
        }

        // Process each .vm file in the directory
        for (File vmFile : vmFiles) {
            System.out.println("Processing file: " + vmFile.getName());
            output.setFileName(vmFile.getName().replace(".vm", ""));
            processSingleVMFile(vmFile, output);
        }

        output.close();
        System.out.println("Translation complete: " + outputFileName);
    }

    private static void processFile(File vmFile) throws IOException {
        // Determine the output file name for the single .vm file
        String parentPath = vmFile.getParent();
        if (parentPath == null) {
            parentPath = ".";
        }

        String outputFileName = parentPath + "/" + vmFile.getName().replace(".vm", ".asm");
        // Disable bootstrap code for single files
        CodeWriter output = new CodeWriter(outputFileName, false);

        System.out.println("Processing file: " + vmFile.getName());
        output.setFileName(vmFile.getName().replace(".vm", ""));
        processSingleVMFile(vmFile, output);

        output.close();
        System.out.println("Translation complete: " + outputFileName);
    }

    private static void processSingleVMFile(File vmFile, CodeWriter output) throws IOException {
        // Initialize the parser for the given .vm file
        Parser parser = new Parser(vmFile.getPath());

        // Iterate through all commands in the .vm file
        while (parser.hasMoreLines()) {
            parser.advance();
            // Translate each command based on its type
            switch (parser.commandType()) {
                case C_ARITHMETIC:
                    output.writeArithmetic(parser.arg1());
                    break;
                case C_PUSH:
                case C_POP:
                    output.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
                    break;
                case C_LABEL:
                    output.writeLabel(parser.arg1());
                    break;
                case C_GOTO:
                    output.writeGoto(parser.arg1());
                    break;
                case C_IF:
                    output.writeIf(parser.arg1());
                    break;
                case C_FUNCTION:
                    output.writeFunction(parser.arg1(), parser.arg2());
                    break;
                case C_CALL:
                    output.writeCall(parser.arg1(), parser.arg2());
                    break;
                case C_RETURN:
                    output.writeReturn();
                    break;
            }
        }
    }
}