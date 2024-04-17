import java.io.IOException;

public class MainDecoding {
    public static void main(String[] args) throws IOException {
        AdaptiveModel adaptiveModel=new AdaptiveModel();
        adaptiveModel.startModel();
        BitInput.startInputtingBits();
        ArithmeticDecoding arithmeticDecoding=new ArithmeticDecoding();
        while (true) {
            int symbol;

            symbol = arithmeticDecoding.decodeSymbol(AdaptiveModel.cumFreq);
            if (symbol == AdaptiveModel.EOF_SYMBOL)
                break;
            int ch = AdaptiveModel.indexToChar[symbol];
            System.out.print((char) ch);
            adaptiveModel.updateModel(symbol);
        }
    }
}
