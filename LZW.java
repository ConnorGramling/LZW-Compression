/*************************************************************************
 *  Compilation:  javac LZWmod.java
 *  Execution:    java LZWmod - < input.txt > output.lzw  (compress input.txt 
 *                                                         into output.lzw)
 *  Execution:    java LZWmod + < output.lzw > input.rec  (expand output.lzw 
 *                                                         into input.rec)
 *  Dependencies: BinaryStdIn.java BinaryStdOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *
 *************************************************************************/

public class LZW {
    private static final int R = 256;        // alphabet size
    private static boolean flushIfFull = false;

    public static void compress() {
        
        CompressionCodeBookInterface codebook = 
            new DLBCodeBook(9, 16);
        //TO DO WRITE FIRST BIT TO RESET OR NOT
        byte flushIfFullByte = 0; //default don't reset dict
        if (flushIfFull) flushIfFullByte = 1; //if told reset dict
        BinaryStdOut.write(flushIfFullByte, 1); //writes 1st byte to reset or not
                
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            if(!codebook.advance(c)){ //found longest match
                int codeword = codebook.getCodeWord();
                BinaryStdOut.write(codeword, codebook.getCodewordWidth()); 
                codebook.add(flushIfFull);
                codebook.advance(c);
            }
        }
        int codeword = codebook.getCodeWord();
        BinaryStdOut.write(codeword, codebook.getCodewordWidth()); 

        BinaryStdOut.write(R, codebook.getCodewordWidth()); 
        BinaryStdOut.close();
    }


    public static void expand() {
        ExpansionCodeBookInterface codebook = new ArrayCodeBook(9, 16);

        flushIfFull = BinaryStdIn.readBoolean(); // checks 1st byte to see if reset

        int codeword = BinaryStdIn.readInt(codebook.getCodewordWidth(flushIfFull));
        String val = codebook.getString(codeword);

        while (true) {
            BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(codebook.getCodewordWidth(flushIfFull));

            if (codeword == R) break;
            String s = codebook.getString(codeword);
            if (codebook.size() == codeword) s = val + val.charAt(0); // special case hack

            codebook.add(val + s.charAt(0), flushIfFull);
            val = s;
        }
        BinaryStdOut.close();
    }



    public static void main(String[] args) {
        if      (args[0].equals("-")) { 
            //TO DO: READ ANOTHER ARG (N OR R)
            // reads if reset dict or not
            if      (args[1].equalsIgnoreCase("n")) flushIfFull = false;
            else if (args[1].equalsIgnoreCase("r")) flushIfFull = true;
            else throw new RuntimeException("Illegal command line argument");
            compress();
        }
        else if (args[0].equals("+")) expand();
        else throw new RuntimeException("Illegal command line argument");
    }

}
