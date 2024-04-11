import java.io.*;

public class BitOutput {

    private static int buffer;
    private static int bitsToGo;

    private static BufferedOutputStream outputStream;
    public static void startOutputtingBits() throws FileNotFoundException {
        buffer=0;
        bitsToGo=8;

        File file=new File("test_output.bin");
        outputStream = new BufferedOutputStream(new FileOutputStream(file));
    }

    static void outputBit(int bit) throws IOException{
        buffer>>=1;
        if(bit==1){
            buffer|=0x80;
        }
        bitsToGo-=1;
        if(bitsToGo==0){
            outputStream.write(buffer);
            bitsToGo=8;
        }
    }

    static void doneOutputtingBits() throws IOException {
        outputStream.write(buffer >> bitsToGo);
        outputStream.close();
    }
}
