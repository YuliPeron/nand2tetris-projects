import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Main {

    public static void main(String[] args) {
        // Check if there is exactly one argument - file or folder
        if (args.length != 1) {
            System.out.println("Usage: JackAnalyzer <input file or folder>");
            return;
        }

        String inputPath = args[0]; // Input file or folder path.
        File inputFile = new File(inputPath);

        try {
            if (inputFile.isDirectory()) {
                analyzeFolder(inputFile); // Process all .jack files in folder
            } else if (inputFile.isFile() && inputPath.endsWith(".jack")) {
                analyzeFile(inputFile); // Process the single .jack file.
            } else {
                System.out.println("Invalid input. Provide a .jack file or a folder containing .jack files.");
            }
        } catch (Exception e) {
            System.err.println("An error occurred while processing: " + inputPath);
            e.printStackTrace();
        }
    }

    // Process all .jack files in a folder
    private static void analyzeFolder(File folder) {
        try {
            // Filter to get only .jack files in the folder.
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".jack"));
            if (files == null || files.length == 0) {
                System.out.println("No .jack files found in the folder: " + folder.getAbsolutePath());
                return;
            }

            // Process each .jack file in the folder.
            for (File file : files) {
                analyzeFile(file);
            }
        } catch (Exception e) {
            System.err.println("An error occurred while processing the folder: " + folder.getAbsolutePath());
            e.printStackTrace();
        }
    }

    // Turn a .jack file into XML
    private static void analyzeFile(File file) {
        String inputFileName = file.getAbsolutePath(); // Full path of the input file.
        String parseFileName = inputFileName.replace(".jack", ".xml"); // Parsed structure output file.

        try {
            System.out.println("Analyzing file: " + inputFileName);

            // Use the CompilationEngine to parse the file and generate the XML structure.
            CompilationEngine engine = new CompilationEngine(inputFileName, parseFileName);
            engine.compileClass();
            engine.close();

            System.out.println("Output written to: " + parseFileName);
        } catch (Exception e) {
            System.err.println("Error occurred while processing file: " + inputFileName);
            e.printStackTrace();
        }
    }
}