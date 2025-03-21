import javax.print.attribute.HashPrintServiceAttributeSet;

/**
 * An implementation of ExpansionCodeBookInterface using an array.
 */

public class ArrayCodeBook implements ExpansionCodeBookInterface {
    private static final int R = 256;        // alphabet size
    private String[] codebook;
    private int W;       // current codeword width
    private int minW;    // minimum codeword width
    private int maxW;    // maximum codeword width
    private int L;       // maximum number of codewords with 
                         // current codeword width (L = 2^W)
    private int code;    // next available codeword value
  
    public ArrayCodeBook(int minW, int maxW){
        this.maxW = maxW;
        this.minW = minW;
        initialize();    
    }
    public int size(){
        return code;
    }

    public int getCodewordWidth(boolean flushIfFull){    
        //because detects if full one step after compression so doesn't know when full so doesn't update
        //when full should return W+1
        
        if (code == L) {
            if (W < maxW) return W+1;
            else if (W == maxW) {   //what if codebook is full
                if (flushIfFull) return minW;
                else return maxW;
            }
        } 
        return W;
    }
    
    private void initialize(){
        codebook = new String[1 << maxW];
        W = minW;
        L = 1<<W;
        code = 0;
        // initialize symbol table with all 1-character strings
        for (int i = 0; i < R; i++)
            add("" + (char) i, false);
        add("", false); //R is codeword for EOF
    }

    public void add(String str, boolean flushIfFull) {
        boolean haveRoom = false;
        if(code < L){
            haveRoom = true;
        } else {  //TO DO WHAT IF CODE BOOK IS FULL
            //check if (W<maxW) W++, double L, set haveroom to true
            //if W is less than max increase width and double max code words giving more space for more codes
            if (W < maxW) {
                W++;
                L = L*2;
                haveRoom = true;
            } else if (W == maxW) {  //if (W = maxW) look at flushIfFull
                if (flushIfFull) {
                    //true - reset codebook (call initialize), haveroom = true
                    initialize();
                    haveRoom = true;
                }
                //false - no modificatino have room stays false (do nothing)
            }
        }
        if(haveRoom){
            codebook[code] = str;
            System.err.println(str + " " + code + " W=" + W);
            code++;
        }
    }

    public String getString(int codeword) {
        return codebook[codeword];
    }
    
}
