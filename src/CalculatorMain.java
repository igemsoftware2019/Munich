import java.io.*;
import java.nio.Buffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class CalculatorMain implements Runnable{
    public final static String[][] codesun = new String[][]{
            {"Stop", "TAA", "TAG", "TGA"},
            {"Ala", "GCT", "GCC", "GCA", "GCG"},
            {"Arg", "CGT", "CGC", "CGA", "CGG", "AGA", "AGG"},
            {"Asn", "AAT", "AAC"},
            {"Asp", "GAT", "GAC"},
            {"Cys", "TGT", "TGC"},
            {"Gln", "CAA", "CAG"},
            {"Glu", "GAA", "GAG"},
            {"Gly", "GGT", "GGC", "GGA", "GGG"},
            {"His", "CAT", "CAC"},
            {"Ile", "ATT", "ATC", "ATA"},
            {"Leu", "TTA", "TTG", "CTT", "CTC", "CTA", "CTG"},
            {"Lys", "AAA", "AAG"},
            {"Met", "ATG"},
            {"Phe", "TTT", "TTC"},
            {"Pro", "CCT", "CCC", "CCA", "CCG"},
            {"Ser", "TCT", "TCC", "TCA", "TCG", "AGT", "AGC"},
            {"Thr", "ACT", "ACC", "ACA", "ACG"},
            {"Trp", "TGG"},
            {"Tyr", "TAT", "TAC"},
            {"Val", "GTT", "GTC", "GTA", "GTG"}
    };
    public final static int windowsrange = 4;
    public final static double gcMinThreshold = 0.4;
    public final static double gcMaxThreshold = 0.6;
    public final static int minPoss = 10;
    public static DecimalFormat df = new DecimalFormat();
    public String myRawSequence;
    public BufferedWriter myOutfile;
    public boolean vocal = true;

    public static void main(String[] args){
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));
        generate(args[0], out, true);
    }

    public CalculatorMain(String rawseq, BufferedWriter outfile, boolean vocal){
        myRawSequence = rawseq;
        myOutfile = outfile;
        this.vocal = vocal;
    }

    @Override
    public void run() {
        generate(myRawSequence, myOutfile, vocal);
    }

    public static boolean generate(String rawseq, BufferedWriter outfile, boolean vocal){
        df.setMaximumFractionDigits(2);
        BufferedReader strR = new BufferedReader(new StringReader(rawseq));
        ArrayList<String[]> fasta = new ArrayList<>();
        String header = "";
        String body = "";
        String line = "";
        try{
            while ((line = strR.readLine()) != null){
                if(line.length()>0 && line.charAt(0)=='>'){
                    if(body.length()>0){
                        fasta.add(new String[]{header, body});
                    }
                    header = line;
                    body = "";
                }else{
                    body = body + line;
                }
            }
            if(body.length()>0){
                if(body.length()>0){
                    fasta.add(new String[]{header, body});
                }
            }
        }catch(IOException e){}
        for (int f = 0; f < fasta.size(); f++) {
            write(fasta.get(f)[0], outfile);
            StringBuilder sequence = new StringBuilder(fasta.get(f)[1]);
            if(vocal) {
                write("sequence: " + sequence, outfile);
                write("bases: " + sequence.length(), outfile);
            }
            for (int i = 0; 3+(4*i) < sequence.length(); i++) {
                sequence.insert(3 + (4 * i), ' ');
            }
            //System.out.println(sequence.toString());
            String[] triplets = sequence.toString().split("\\s");
            if(vocal) {
                write("triplets: " + triplets.length, outfile);
            }

            //GC content around base
            int[][] windowGCContent = new int[triplets.length][8];
            for (int i = 0; i < windowGCContent.length; i++) {
                for (int j = 0; j < triplets[i].length(); j++) {
                    if (triplets[i].charAt(j) == 'A') {
                        windowGCContent[i][0]++;
                    } else if (triplets[i].charAt(j) == 'C') {
                        windowGCContent[i][1]++;
                    } else if (triplets[i].charAt(j) == 'G') {
                        windowGCContent[i][2]++;
                    } else if (triplets[i].charAt(j) == 'T') {
                        windowGCContent[i][3]++;
                    }
                }
            }
            for (int i = 0; i < windowGCContent.length; i++) {
                for (int j = 1; j < windowsrange; j++) {
                    for (int k = 0; k < 4; k++) {
                        if (i - j >= 0) {
                            windowGCContent[i][k + 4] += windowGCContent[i - j][k];
                        }
                        if (i + j < windowGCContent.length) {
                            windowGCContent[i][k + 4] += windowGCContent[i + j][k];
                        }
                    }
                }
            }

            //number of silent variants
            double possibilities = 1;
            boolean stop = false;
            int lastNum = 0;
            if (vocal) {
                write("AA \t BP \t Triplet (AA name)\t codons " /*+ "(" + possibilities + " total)" */ + "\tGC", outfile);
            }
            for (int i = 0; i < triplets.length; i++) {
                stop = false;
                for (int j = 0; !stop && j < codesun.length; j++) {
                    for (int k = 1; !stop && k < codesun[j].length; k++) {
                        if (triplets[i].equals(codesun[j][k])) {
                            possibilities = possibilities * (codesun[j].length - 1);
                            double myGC = gcContent(windowGCContent[i]);
                            if (vocal) {
                                write(i + "\t" + 3 * i + "\t" + ((lastNum + codesun[j].length > minPoss) ? ((myGC >= gcMinThreshold && myGC <= gcMaxThreshold) ? "\033[31m" : "\033[33m") : ((myGC >= gcMinThreshold && myGC <= gcMaxThreshold) ? "\033[32m" : "")) + triplets[i] + " (" + codesun[j][0] + "):\t\t" + (codesun[j].length - 1) + " " /*+ "(" + possibilities + " total)" */ + "\t" + df.format(myGC) + "\033[0m", outfile);
                            }
                            lastNum = codesun[j].length - 1;
                            stop = true;
                        }
                    }
                }
            }
            write("Possible combinations of silent mutations: " + possibilities + " = 2^" + (int) Math.floor(Math.log(possibilities) / Math.log(2)) + " of 2^" + 2 * rawseq.length(), outfile);
            write(("Storage capacity: " + (Math.floor(Math.log(possibilities) / Math.log(2))) / 8) + " Byte of " + rawseq.length() / 4 + " Byte", outfile);
        }

        try {
            outfile.flush();
            outfile.close();
        }catch(Exception e){}
        return true;
    }

    public static boolean write(String string, BufferedWriter file){
        try {
            file.write(string);
            file.write('\n');
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }

    public static double gcContent(int[] counts){
        double out;
        if(counts.length == 8){
            out = ((double)(counts[5]+counts[6]))/((double)(counts[4]+counts[5]+counts[6]+counts[7]));
        }else{
            out = Double.NaN;
        }
        return out;
    }
}
