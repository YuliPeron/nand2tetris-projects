import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Parser {
    public enum types {
        C_ARITHMETIC,
        C_PUSH,
        C_POP,
        C_LABEL,
        C_GOTO,
        C_IF,
        C_FUNCTION,
        C_RETURN,
        C_CALL;
    }

    private final HashMap<Integer, String> instructions;
    private int counterLine;

    // Constructor to initialize the parser with a file
    public Parser(String file) throws IOException {
        this.instructions = new HashMap<>();
        this.counterLine = -1;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("//")) continue;

                int index = line.indexOf("//");
                if (index != -1) {
                    line = line.substring(0, index).trim();
                }

                if (!line.isEmpty()) {
                    instructions.put(i, line);
                    i++;
                }
            }
        } catch (IOException e) {
            throw new IOException("Failed to load the file: " + file, e);
        }
    }

    // Checks if there are more lines to process
    public boolean hasMoreLines() {
        return counterLine < instructions.size() - 1;
    }

    // Advances to the next line in the instruction set
    public void advance() {
        if (!hasMoreLines()) {
            throw new IllegalStateException("No more lines to process.");
        }
        counterLine++;
        while (counterLine < instructions.size() &&
                (instructions.get(counterLine).isEmpty() || instructions.get(counterLine).startsWith("//"))) {
            counterLine++;
        }
    }

    // Determines the type of the current command
    public types commandType() {
        String currentLine = instructions.get(counterLine);
        if (currentLine == null) {
            throw new IllegalStateException("Current line is null.");
        }
        if (currentLine.startsWith("push")) return types.C_PUSH;
        else if (currentLine.startsWith("pop")){
            return types.C_POP;
        }
        else if (currentLine.startsWith("label")){
            return types.C_LABEL;
        }
        else if (currentLine.startsWith("goto")){
            return types.C_GOTO;
        }
        else if (currentLine.startsWith("if-goto")){
            return types.C_IF;
        }
        else if (currentLine.startsWith("function")){
            return types.C_FUNCTION;
        }
        else if (currentLine.startsWith("return")){
            return types.C_RETURN;
        }
        else if (currentLine.startsWith("call")){
            return types.C_CALL;
        }
        else return types.C_ARITHMETIC;
    }

    // Returns the first argument of the current command
    public String arg1() {
        String currentLine = instructions.get(counterLine);
        if (commandType() == types.C_RETURN) {
            throw new UnsupportedOperationException("arg1 is not supported for C_RETURN commands.");
        }

        if (commandType() == types.C_ARITHMETIC) {
            return currentLine; // For arithmetic commands, return the command itself
        }

        return currentLine.split(" ")[1]; // For other commands, return the segment
    }

    // Returns the second argument of the current command
    public int arg2() {
        if (commandType() == types.C_PUSH ||
                commandType() == types.C_POP ||
                commandType() == types.C_FUNCTION ||
                commandType() == types.C_CALL) {
            String currentLine = instructions.get(counterLine);
            return Integer.parseInt(currentLine.split(" ")[2]);
        }

        throw new UnsupportedOperationException("arg2 is not supported for this command type.");
    }
}