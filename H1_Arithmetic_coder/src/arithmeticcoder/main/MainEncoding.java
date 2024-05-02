package arithmeticcoder.main;

import arithmeticcoder.CodingDecoding.ArithmeticEncoding;
import arithmeticcoder.bittools.BitReader;
import arithmeticcoder.bittools.BitWriter;
import arithmeticcoder.model.AdaptiveModel;

import java.io.File;
import java.io.IOException;
//TODO ce ar trebui sa fac daca nu exista tipul de 32 de biti fara semn?
//TODO tot ce inseamna operatii trebuie facut bitewise?
public class MainEncoding {
    public static void main(String[] args) throws IOException {

        AdaptiveModel adaptiveModel = new AdaptiveModel();
        File file = new File("test_output.bin");
        BitWriter bitWriter = new BitWriter(file);
        ArithmeticEncoding arithmeticEncoding = new ArithmeticEncoding(bitWriter);

        file = new File("test.bin");

        BitReader bitReader = new BitReader(file);

        while (true) {

            int ch = bitReader.readNBits(8);

            if (ch == -1)
                break;

            int symbol = ch;

            arithmeticEncoding.encodeSymbol(symbol, adaptiveModel.getSums());
            adaptiveModel.updateModel(symbol);
        }


        arithmeticEncoding.encodeSymbol(adaptiveModel.getEOF_SYMBOL(), adaptiveModel.getSums());
        arithmeticEncoding.doneEncoding();

        bitWriter.close();
        bitReader.close();

    }
}
