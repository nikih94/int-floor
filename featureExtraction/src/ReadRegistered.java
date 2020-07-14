import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ReadRegistered {

    //datoteka
    public String path;
    public String name;

    //za pisanje na file
    public File file = null;
    public FileWriter fw = null;
    public PrintWriter pw = null;

    public ReadRegistered(String name,String path){
        this.path = path + "features/";
        this.name = name;
        file = new File(this.path + this.name);

        try { // definiraj writer
            fw = new FileWriter(file);
            pw = new PrintWriter(fw);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!file.exists()) { //ustvari file
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Nekaj je narobe, datoteka ze obstaja");
        }

    }


    public void saveLine(int[] datapoints){

        String line = "";

        for (int i = 0 ; i < datapoints.length ; i++){
            line += Integer.toString(datapoints[i]) + ",";
        }

        line = line.substring(0, line.length() - 1);

        pw.println(line);
        pw.flush();

    }

    public void writeNext(){
        pw.println("next");
    }


}
