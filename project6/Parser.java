import java.io.*;
import java.util.HashMap;


//Defining the types of instructions.
enum INSTRUCTION_Type {
    A_INSTRUCTION,
    C_INSTRUCTION,
    L_INSTRUCTION
}

public class Parser {
    public HashMap<Integer,String> instructions;
    public int counterLine;


    public Parser(String file) throws IOException {
        this.instructions = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            System.out.println("Attempting to read file: " + new File(file).getAbsolutePath());
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
    }


    public boolean hasMoreLines()  throws IOException {
        return instructions.size() > counterLine;

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

    public INSTRUCTION_Type instructionType() {
        String currentLine = instructions.get(counterLine);
        if(currentLine.startsWith("@"))
            return INSTRUCTION_Type.A_INSTRUCTION;
        else if(currentLine.startsWith("(") && currentLine.endsWith(")"))
            return INSTRUCTION_Type.L_INSTRUCTION;
        else
            return INSTRUCTION_Type.C_INSTRUCTION;
    }

    public String symbol (){
        INSTRUCTION_Type instruction = instructionType();
        if(instruction == INSTRUCTION_Type.C_INSTRUCTION)
            throw new Error("C instruction cann't give symbol");
        String currentInstruction  = instructions.get(counterLine);
        if(instruction == INSTRUCTION_Type.L_INSTRUCTION)
            return currentInstruction .replace("(", "").replace(")", "");
        return currentInstruction .replace("@", "");
    }

    public String dest() {
        String [] split = instructions.get(counterLine).split("=");
        if(split.length==1)
            return "";
        return split[0];
    }

    public String comp() {
        String [] split = instructions.get(counterLine).split("=");
        if(split.length==1) {
            String [] comp = split[0].split(";");
            if(comp.length==1)
                return "";
            return comp[0];
        }
        return split[1].split(";")[0];
    }

    public String jump() {
        String [] split = instructions.get(counterLine).split(";");
        if (split.length == 1)
            return "";
        return split[1];
    }

}

