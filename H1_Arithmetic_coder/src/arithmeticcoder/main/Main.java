package arithmeticcoder.main;

import arithmeticcoder.CodingDecoding.ArithmeticDecoding;
import arithmeticcoder.CodingDecoding.ArithmeticEncoding;
import arithmeticcoder.bittools.BitReader;
import arithmeticcoder.bittools.BitWriter;
import arithmeticcoder.model.AdaptiveModel;

import java.io.File;
import java.io.IOException;

public class Main {
    public static String PATH = "C:\\Users\\stoic\\Desktop\\master\\Sem2\\ACM\\Laburi\\ACM_Homeworks_M1S2\\H1_Arithmetic_coder\\out";
    public static String INPUT_FILE = PATH + "\\" + "test.bin";

    public static String AUX_FILE = PATH + "\\" + "test_aux.bin";
    ;
    public static String OUTPUT_FILE = PATH + "\\" + "test_output.bin";
    ;

    public static void main(String[] args) throws IOException {
            encoding();
            decoding();

    }

    public static void encoding() throws IOException {
        AdaptiveModel adaptiveModel = new AdaptiveModel();
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
            arithmeticEncoding.encodeSymbol(symbol, adaptiveModel.getCumFreq());
            adaptiveModel.updateModel(symbol);
        }


        arithmeticEncoding.encodeSymbol(adaptiveModel.getEOF_SYMBOL(), adaptiveModel.getCumFreq());
        arithmeticEncoding.doneEncoding();

        bitWriter.close();
        bitReader.close();
        System.out.println("Done encoding");
    }

    public static void decoding() throws IOException {
        File file = new File(AUX_FILE);
        BitReader bitReader = new BitReader(file);
        AdaptiveModel adaptiveModel = new AdaptiveModel();
        ArithmeticDecoding arithmeticDecoding = new ArithmeticDecoding(bitReader);

        file = new File(OUTPUT_FILE);
        BitWriter bitWriter = new BitWriter(file);

        while (true) {
            int symbol;
            symbol = arithmeticDecoding.decodeSymbol(adaptiveModel.getCumFreq());
//            System.out.println(symbol);
            if (symbol == adaptiveModel.getEOF_SYMBOL())
                break;
            int ch = symbol;
            System.out.print((char)(ch));
            bitWriter.writeNBits(ch, 8);
            adaptiveModel.updateModel(symbol);
        }

        bitReader.close();
        bitWriter.close();
        System.out.println();
        System.out.println("Done decoding");
    }
}