import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Parser {
    public enum types {
        C_ARITHMETIC,
        C_PUSH,
        C_POP;
    }

    public HashMap<Integer,String> instructions;
    public int counterLine;

    public Parser(String file) throws IOException {
        this.instructions = new HashMap<>();
        this.counterLine = -1;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // System.out.println("Attempting to read file: " + new File(file).getAbsolutePath());
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
                    i ++;
                }
            }
        } catch (IOException e) {
            System.out.println("Can't find the file");
        }
        /*
        System.out.println("Instructions loaded:");
        for (int key : instructions.keySet()) {
            System.out.println("Line " + key + ": " + instructions.get(key));
        }
        */
    }


    public boolean hasMoreLines()  throws IOException {
        // return instructions.size() > counterLine;
        return counterLine < instructions.size() - 1;

    }

    public void advance() throws IOException {
        if (!hasMoreLines()) {
            throw new Error("There are no more lines");
        }
        counterLine++;
        while (counterLine < instructions.size() &&
                (instructions.get(counterLine).isEmpty() || instructions.get(counterLine).startsWith("//"))) {
            counterLine++;
        }

    }

    public types commandType () throws IOException{
        String currentLine = instructions.get(counterLine);

        /*
        System.out.println("commandType: counterLine = " + counterLine);
        System.out.println("commandType: currentLine = " + currentLine);

        if (currentLine == null) {
            throw new Error("Null encountered at counterLine: " + counterLine);
        }
        */

        if(currentLine.startsWith("push"))
            return types.C_PUSH;
        else if(currentLine.startsWith("pop"))
            return types.C_POP;
        else
            return types.C_ARITHMETIC;
    }

    public String arg1() {
        String currentLine  = instructions.get(counterLine);
        if(currentLine.startsWith("push") || currentLine.startsWith("pop")) {
            return currentLine.trim().split(" ")[1];
        }
        else return currentLine.trim().split(" ")[0];
    }

    public int arg2() throws IOException{
        String currentLine = instructions.get(counterLine);
        if(currentLine.startsWith("push") || currentLine.startsWith("pop")) {
            return Integer.parseInt(currentLine.trim().split(" ")[2]);
        }
        else{
            throw new Error("can't perform on C_ARITHMETIC");
        }
    }

}