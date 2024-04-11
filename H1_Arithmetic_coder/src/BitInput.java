import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BitInput {
    private static FileInputStream fis;
    private static int buffer;
    private static int bitsToGo;
    private static int garbageBits;

    public static void startInputtingBits() throws FileNotFoundException {
        bitsToGo = 0;
        garbageBits = 0;

        File file = new File("test_output.bin");
        fis = new FileInputStream(file);
    }

    static int inputBit() throws IOException {
        int bit;
        if (bitsToGo == 0) {
            buffer = fis.read();
            if (buffer == -1) {
                garbageBits += 1;
                if (garbageBits > ArithmeticParameters.CODE_VALUE_BITS - 2) {
                    throw new IOException("Bed input file\n");
                }
            }
            bitsToGo = 8;
        }
        bit = buffer & 1;
        buffer >>= 1;
        bitsToGo -= 1;
        return bit;
    }
}
