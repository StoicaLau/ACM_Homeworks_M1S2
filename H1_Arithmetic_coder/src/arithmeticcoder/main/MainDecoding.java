package arithmeticcoder.main;

import arithmeticcoder.CodingDecoding.ArithmeticDecoding;
import arithmeticcoder.bittools.BitReader;
import arithmeticcoder.model.AdaptiveModel;
import arithmeticcoder.model.AdaptiveModelConstants;

import java.io.File;
import java.io.IOException;

public class MainDecoding {
    public static void main(String[] args) throws IOException {
        File file = new File("test_output.bin");

        BitReader bitReader = new BitReader(file);
        AdaptiveModel adaptiveModel = new AdaptiveModel();
        ArithmeticDecoding arithmeticDecoding = new ArithmeticDecoding(bitReader);
        while (true) {
            int symbol;
            symbol = arithmeticDecoding.decodeSymbol();
//            System.out.println(symbol);
            if (symbol == AdaptiveModelConstants.EOF_SYMBOL)
                break;
            int ch = symbol;
            System.out.print((char) ch);
            adaptiveModel.updateModel(symbol);
        }

        bitReader.close();
    }
}
