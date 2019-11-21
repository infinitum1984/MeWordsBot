package Words;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class ParsWords {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(new File("src/main/resources/words.txt"));
        PrintWriter pw = new PrintWriter(new File("src/main/resources/words_out.txt"));
        while (sc.hasNext()){
            String s = sc.nextLine();
            s="\""+s+"\",";
            pw.println(s);
        }

        pw.close();
    }
}
