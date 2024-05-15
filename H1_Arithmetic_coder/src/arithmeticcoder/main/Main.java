package arithmeticcoder.main;

import arithmeticcoder.CodingDecoding.ArithmeticDecoding;
import arithmeticcoder.CodingDecoding.ArithmeticEncoding;
import arithmeticcoder.bittools.BitReader;
import arithmeticcoder.bittools.BitWriter;
import arithmeticcoder.model.AdaptiveModelConstants;

import java.io.File;
import java.io.IOException;

public class Main {
    public static final String INPUT_FILE_EXE = "P2_2_1_BNB_M5_V3_R1.exe";
    public static final String OUTPUT_FILE_EXE = "P2_2_1_BNB_M5_V3_R1_out.exe";
    public static final String INPUT_FILE_BIN = "test.bin";
    public static final String OUTPUT_FILE_BIN = "test_output.bin";
    public static String PATH = "C:\\Users\\stoic\\Desktop\\master\\Sem2\\ACM\\Laburi\\ACM_Homeworks_M1S2\\H1_Arithmetic_coder\\out";
    public static final String INPUT_FILE = PATH + "\\" + INPUT_FILE_EXE;

    public static final String AUX_FILE = PATH + "\\" + "test_aux.bin";

    public static final String OUTPUT_FILE = PATH + "\\" + OUTPUT_FILE_EXE;


    public static void main(String[] args) throws IOException {

        encoding();
        System.out.println();
        decoding();

    }

    public static void encoding() throws IOException {
        System.out.println("Start encoding");
        File file = new File(AUX_FILE);
        BitWriter bitWriter = new BitWriter(file);
        ArithmeticEncoding arithmeticEncoding = new ArithmeticEncoding(bitWriter);

        file = new File(INPUT_FILE);

        BitReader bitReader = new BitReader(file);

        while (true) {

            int ch = bitReader.readNBits(8);

            if (ch == -1)
                break;

            int symbol = ch;
            arithmeticEncoding.encodeSymbol(symbol);
        }


        arithmeticEncoding.encodeSymbol(AdaptiveModelConstants.EOF_SYMBOL);
        arithmeticEncoding.doneEncoding();

        bitWriter.close();
        bitReader.close();
        System.out.println("Done encoding");
    }

    public static void decoding() throws IOException {
        System.out.println("Start decoding");
        File file = new File(AUX_FILE);
        BitReader bitReader = new BitReader(file);
        ArithmeticDecoding arithmeticDecoding = new ArithmeticDecoding(bitReader);

        file = new File(OUTPUT_FILE);
        BitWriter bitWriter = new BitWriter(file);

        while (true) {
            int symbol;
            symbol = arithmeticDecoding.decodeSymbol();
//            System.out.println(symbol);
            if (symbol == AdaptiveModelConstants.EOF_SYMBOL)
                break;
            bitWriter.writeNBits(symbol, 8);
        }

        bitReader.close();
        bitWriter.close();

        System.out.println("Done decoding");
    }
}