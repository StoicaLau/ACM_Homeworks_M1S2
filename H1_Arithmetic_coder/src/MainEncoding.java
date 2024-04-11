import java.io.File;
import java.io.IOException;

public class MainEncoding {
    public static void main(String[] args) throws IOException, IOException {

        AdaptiveModel adaptiveModel=new AdaptiveModel();

        adaptiveModel.startModel(); //start module

        BitOutput.startOutputtingBits();   //start bit reader

        ArithmeticEncoding.startEncoding();           //start encoding
        File file=new File("test.bin");
        BitReader bitReader=new BitReader(file);

        while (true) {

            int ch = bitReader.readNBits(8);

            if (ch ==-1)
                break;

            int symbol = adaptiveModel.charToIndex[ch]; // translate from char index to symbol
            ArithmeticEncoding.encodeSymbol(symbol, AdaptiveModel.cumFreq); // Codifică acel simbol
            adaptiveModel.updateModel(symbol);      // Actualizează modelul
        }


        ArithmeticEncoding.encodeSymbol(AdaptiveModel.EOF_SYMBOL, AdaptiveModel.cumFreq); // Codifică simbolul EOF
        ArithmeticEncoding.doneEncoding();                     // Trimite ultimii biți
        BitOutput.doneOutputtingBits();     // Finalizează ieșirea de biți

        bitReader.close();


        ////decoding

    }
}
