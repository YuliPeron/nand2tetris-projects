import java.util.HashMap;

public class SymbolTable {
    private HashMap<String,Integer> symbolTable;


    public SymbolTable() {
        this.symbolTable= new HashMap<>();
    }
    public void addEntry(String symbol, int address) {
        this.symbolTable.put(symbol, address);
    }

    public boolean contains(String symbol){
        return this.symbolTable.containsKey(symbol);
    }

    public int getAddress(String symbol){
        return this.symbolTable.get(symbol);
    }
}
