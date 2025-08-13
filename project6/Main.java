import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String parser = args[0];
        String output = "";
        HackAssembler ha = new HackAssembler();
        ha.assembler(parser);
    }
}