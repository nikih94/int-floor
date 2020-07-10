import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Registracija extends Thread {



    public long regTime = 5000; //koliko casa bo registriralo padec v ms

    //datoteka
    public String path = "/home/niki/Desktop/tesi/data_registered/testnipodatkiTRUE/";
    public String name = "padec";
    public int numFiles = 0;

    //za pisanje na file
    public File file = null;
    public FileWriter fw = null;
    public PrintWriter pw = null;

    //progress count
    public int progress = 0;

    public volatile long targetTime = 0;
    public Scanner scan = new Scanner(System.in);

    public Registracija(){
        System.out.println("Program za registracijo podatkov");

    }

    public void pisiPodatke(String line){

        if(System.currentTimeMillis() < targetTime){//registriraj

            pw.println(line);

            progress++;

            if(  progress > 10 ) {
                System.out.print(".");
                progress = 0;
            }
            //System.out.println("Line of text from serial port: " + line);
        }//drugace ne registrirat



    }


    public void run(){


        while(true) {

            file = new File(path + name + numFiles + ".txt");

            if (!file.exists()) { //ustvari file
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Nekaj je narobe, datoteka ze obstaja");
            }

            System.out.println("Pritisni ENTER za začet registracijo, ki traja: " + (regTime / 1000)+"s " + "   Na lokaciji: "+path+name+numFiles+".txt");
            scan.nextLine();

            System.out.println("Registriram.....");
            Toolkit.getDefaultToolkit().beep();

            try { // definiraj writer
                fw = new FileWriter(file);
                pw = new PrintWriter(fw);
            } catch (IOException e) {
                e.printStackTrace();
            }


            //start registracije
            targetTime = System.currentTimeMillis() + regTime;

            try {
                Thread.sleep(regTime + 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println();
            Toolkit.getDefaultToolkit().beep();

            //zapri writer

            pw.close();

            //vprasaj ali zelis shraniti


            boolean repeate = false;
            do {
                System.out.println("Zelis shraniti registracijo? -> y = shrani     n = zbrisi");
                repeate = false;

                char input = scan.nextLine().charAt(0);

                if (input  == 'y') {
                    numFiles++; //incrementiraj stevilko fila
                    System.out.println("Shranil");

                } else if (input == 'n') {
                    if(file.delete()){               //returns Boolean value
                        System.out.println(file.getName() + " deleted");   //getting and printing the file name
                    }
                    else {
                        System.out.println("failed");
                    }

                } else {
                    repeate = true;
                }

            }while(repeate);

        }

    }


}
