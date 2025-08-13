import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

// JackTokenizer.java - Splits source code into tokens
public class JackTokenizer {

    private final BufferedReader reader;
    private String currentToken;
    private String nextToken;
    private int tokenCounter;
    private ArrayList<String> tokenList;

    public enum tokenTypes {
        KEYWORD,
        SYMBOL,
        IDENTIFIER,
        INT_CONST,
        STRING_CONST;
    }
    private static final ArrayList<String> keyWordList = new ArrayList<>(Arrays.asList("class", "constructor", "function",
            "method", "field", "static", "var",
            "int", "char", "boolean", "void", "true", "false", "null", "this", "do",
            "if", "else", "while", "return", "let"));

    private static final ArrayList<String> symbolList = new ArrayList<>(Arrays.asList(
            "{", "}", "(", ")", "[", "]", ".", ",", ";",
            "+", "-", "*", "/", "&", "|", "<", ">", "=", "~"));

    // Create tokenizer and process input file
    public JackTokenizer(String filePath) throws IOException {
        this.tokenList = new ArrayList<>();
        this.reader = new BufferedReader(new FileReader(filePath));
        StringBuilder token = new StringBuilder(); // Temporary storage for building the token.
        int i;
        this.tokenCounter = -1;

        while ((i = reader.read()) != -1) { // Read the file character by character.
            char c = (char) i;

            // Handle comments
            if (c == '/') {
                reader.mark(2); // Mark the current position in case it’s not a comment.
                int nextChar = reader.read();
                if (nextChar == '/') {
                    reader.readLine(); // Skip single-line comments.
                    continue;
                } else if (nextChar == '*') {
                    // Skip multi-line comments using the second snippet style.
                    while ((i = reader.read()) != -1) {
                        if ((char) i == '*' && reader.read() == '/') {
                            break;
                        }
                    }
                    continue;
                }
                reader.reset(); // Reset if it’s not a comment.
            }

            // Skip whitespace
            if (Character.isWhitespace(c)) {
                if (token.length() > 0) {
                    tokenList.add(token.toString());
                    token.setLength(0);
                }
                continue;
            }

            // Handle symbols as separate tokens
            if (symbolList.contains(String.valueOf(c))) {
                if (token.length() > 0) {
                    tokenList.add(token.toString());
                    token.setLength(0);
                }
                tokenList.add(String.valueOf(c));
                continue;
            }

            // Handle string constants
            if (c == '"') {
                if (token.length() > 0) {
                    tokenList.add(token.toString());
                    token.setLength(0);
                }
                token.append(c); // Add opening quote.
                while ((i = reader.read()) != -1 && (char) i != '"') {
                    token.append((char) i); // Add characters inside the string.
                }
                if (i != -1) {
                    token.append((char) i); // Add closing quote.
                }
                tokenList.add(token.toString());
                token.setLength(0);
                continue;
            }

            // Add character to the token builder.
            token.append(c);
        }

        // Add any remaining token in the builder.
        if (token.length() > 0) {
            tokenList.add(token.toString());
        }
//        nextToken = tokenList.getFirst();
       // this.tokenCounter++;
        reader.close(); // Close the reader.
    }

    // Checks if there are more tokens in the input file
    public boolean hasMoreTokens() throws IOException {
       // return reader.ready();
        return tokenCounter < tokenList.size() -1;
    }

    // Move to next token
    public void advance() throws IOException{
        if (hasMoreTokens()) {
            tokenCounter++;
           // System.out.println("Advanced to token: " + getCurrentToken());
        }
        else {
            throw new IllegalStateException("No more tokens to process.");
        }

    }

    // Get current token text
    public String getCurrentToken(){
        String token = null;
        if (tokenCounter >= 0 && tokenCounter < tokenList.size()) {
            token= tokenList.get(tokenCounter);
            //System.out.println("Getting token at position " + tokenCounter + ": '" + token + "'");
        }
        else{
           // System.out.println("Token counter " + tokenCounter + " is out of bounds (size: " + tokenList.size() + ")");
        }
        currentToken = token;
        return currentToken;
    }

    // Get type of current token
    public tokenTypes tokenType() throws IOException{
        getCurrentToken();
        if (currentToken == null) {
            throw new IllegalStateException("Current token is null.");
        }
        else if(keyWordList.contains(currentToken)){
            return tokenTypes.KEYWORD;
        }
        else if (symbolList.contains(currentToken)) {
            return tokenTypes.SYMBOL;
        }
        else if (currentToken.matches("\\d+")) {
            return tokenTypes.INT_CONST;
        }
        else if (currentToken.startsWith("\"") && currentToken.endsWith("\"")) {
            return tokenTypes.STRING_CONST;
        }
        else {
            return tokenTypes.IDENTIFIER;
        }
    }

    // returns the keyword which is the current token
    public String keyWord(){
        if (currentToken != null && keyWordList.contains(currentToken)) {
            return currentToken.toUpperCase();
        }
        throw new IllegalStateException("Current token is not a keyword.");
    }

    // returns the character which is the current token
    public char symbol(){
        if (currentToken != null && symbolList.contains(currentToken)) {
            return currentToken.charAt(0);
        }
        throw new IllegalStateException("Current token is not a symbol.");
    }

    // returns the string which is the current token
    public String identifier() throws IOException{
        tokenTypes tokenType1= tokenType();
        if(currentToken != null && tokenType1 == tokenTypes.IDENTIFIER){
            return currentToken;
        }
        throw new IllegalStateException("Current token is not an identifier.");
    }

    // returns the integer which is the current token
    public int intVal() throws IOException{
        tokenTypes tokenType1= tokenType();
        if(currentToken != null && tokenType1 == tokenTypes.INT_CONST){
            return Integer.parseInt(currentToken);
        }
        throw new IllegalStateException("Current token is not an intVal.");
    }

    // returns the string which is the current token
    public String stringVal() throws IOException{
        tokenTypes tokenType1= tokenType();
        if(currentToken != null && tokenType1 == tokenTypes.STRING_CONST){
            return currentToken.substring(1, currentToken.length() - 1);
        }
        throw new IllegalStateException("Current token is not an stringVal.");
    }


}
