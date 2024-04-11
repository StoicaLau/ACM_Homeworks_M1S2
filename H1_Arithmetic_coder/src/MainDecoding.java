import java.io.IOException;

public class MainDecoding {
    public static void main(String[] args) throws IOException {
        AdaptiveModel adaptiveModel=new AdaptiveModel();
        adaptiveModel.startModel();
        BitInput.startInputtingBits();
        ArithmeticDecoding.startDecoding();
        while (true) {
            int symbol;

            symbol = ArithmeticDecoding.decodeSymbol(AdaptiveModel.cumFreq);
            if (symbol == AdaptiveModel.EOF_SYMBOL)
                break;
            int ch = AdaptiveModel.indexToChar[symbol];
            System.out.print((char) ch);
            adaptiveModel.updateModel(symbol);
        }
    }
}
