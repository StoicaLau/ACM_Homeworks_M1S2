import java.io.File;
import java.io.IOException;

public class MainEncoding {
    public static void main(String[] args) throws IOException, IOException {

        AdaptiveModel adaptiveModel = new AdaptiveModel();

        adaptiveModel.startModel();

        BitOutput.startOutputtingBits();

        ArithmeticEncoding arithmeticEncoding = new ArithmeticEncoding();
        File file = new File("test.bin");
        BitReader bitReader = new BitReader(file);

        while (true) {

            int ch = bitReader.readNBits(8);

            if (ch == -1)
                break;

            int symbol = adaptiveModel.charToIndex[ch];
            arithmeticEncoding.encodeSymbol(symbol, AdaptiveModel.cumFreq);
            adaptiveModel.updateModel(symbol);
        }


        arithmeticEncoding.encodeSymbol(AdaptiveModel.EOF_SYMBOL, AdaptiveModel.cumFreq);
        arithmeticEncoding.doneEncoding();
        BitOutput.doneOutputtingBits();

        bitReader.close();

    }
}
