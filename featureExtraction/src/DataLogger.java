import com.fazecast.jSerialComm.SerialPort;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DataLogger
        extends Thread
{
    public static SerialPort port;
    public double delta = 3.5D;
    public FeatureExtraction theWindow;

    public boolean shraniPadec = false; //definira kdaj bomo shranjevali padec
    public int dolzinaRegistracije = 100;
    public int registriraneVrstice = 0;
    public ReadRegistered resi;
    public Scanner s=null;

    public void openSerialPort() {
        SerialPort[] available_ports = SerialPort.getCommPorts();

        for (int i = 0; i < available_ports.length; i++) {
            System.out.println(String.valueOf(i) + " : " + available_ports[i]);
        }
        //kommentiraj za feature extraction iz file ------------------------------------------------------------------------
        //port = available_ports[Main.port_id];
        if (port != null) {
            port.setComPortTimeouts(4096, 0, 0);
            System.out.println(port.openPort());
            if (!port.openPort()) {
                System.err.println("Unable to open the port.");
            }
        }
        //kommentiraj za feature extraction iz file ------------------------------------------------------------------------
        //port.setBaudRate(115200);
    }

    public void run() {
        for (int i = 0; i < Main.sensor_data[1].length; i++) {
            Main.sensor_data[1][i] = 1000.0F;
        }

        //postavi port == null za feature extraction iz file, drugace port != null   --------------------------------------------------------
        if (port == null) {
            s = null;
            /// Branje iz ARDUINO
            //s = new Scanner(port.getInputStream());


            //// Branje iz file:_______________________________________
            ///moras commentirat tudi vrstico,ki odstrani zadnjo vejico iz vrstice (line = line.substring(0, line.length() - 1);)


            String path = "/home/niki/Desktop/tesi/data_registered/testnipodatkiTRUE/";
            String fileName = "10padcev.txt";
            int f = 0; //index za branje filenames

            // create instance of directory
            File dir = new File(path);
            // Get list of all the files in form of String Array
            File[] files = dir.listFiles(File::isFile);

            File text = files[f];
            resi = new ReadRegistered(fileName,path);

            try {
                s = new Scanner(text);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            f++;
            ///Konec branje iz file___________________________________



            float[] previous_datapoints = new float[16];
            theWindow = new FeatureExtraction(); //ustvarimo novo okno


            while (true) {

                if(!s.hasNextLine()){

                    sprazniOkno();

                    if(f < files.length){ //doker so datoteke v direktorij
                        text = files[f];
                        try {
                            s = new Scanner(text);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        f++;



                        resi.writeNext(); //napise next, da oznaci zacetek nove registracije padca
                        registriraneVrstice = 0;
                        shraniPadec = false;
                        theWindow.pocisti();

                    }else{ // ni vec datotek v direktoriju pojdi ven iz loop
                        break;
                    }
                }


                String line = s.nextLine();
                //line = line.substring(0, line.length() - 1);       ////PREGLEDI DA JE ODKOMENTIRANA KO RABIS ARDUINO
                String[] datapoints = line.split(",");
                int [] featureArray = new int[16];
                float[] currentDatapoints = new float[16];

                if (datapoints.length > 16) {
                    System.out.println("Malformed payload by the controller");
                } else {
                    //Feature extraction
                    //System.out.println(Arrays.toString(datapoints));


                    theWindow.fill(datapoints);
                    featureArray = theWindow.extractFeatures();
                    currentDatapoints = theWindow.getPetindvajsetaVrstica();

                    //pregleda odvod za razumeti ali je potrebno shraniti padec
                    if(!shraniPadec){
                        shraniPadec = theWindow.startRecording();
                    }

                    //drugo
                    for (int i = 0; i < datapoints.length; i++) {
                        float force = currentDatapoints[i];
                        if (force < 0.0F)
                            force = Math.abs(force);
                        if (Main.sensor_data[1][i] < force) {
                            Main.sensor_data[1][i] = Main.sensor_data[1][i] + (Main.sensor_data[1][i] + force) / 2.0F;
                        }
                        if (force - Main.sensor_data[2][i] > 0.0F) {
                            if ((force - Main.sensor_data[2][i]) / Main.sensor_data[1][i] <= 1.0F)
                            {

                                Main.sensor_data[0][i] = (force - Main.sensor_data[2][i]) / Main.sensor_data[1][i];
                            }
                        } else {
                            Main.sensor_data[0][i] = 0.0F;
                        }

                        if (Math.abs(currentDatapoints[i] - previous_datapoints[i]) < this.delta && currentDatapoints[i] < 1000.0F) {
                            Main.sensor_data[2][i] = currentDatapoints[i] ;
                        }
                        previous_datapoints[i] = currentDatapoints[i] ;
                    }
                }
                System.out.print("Line of text from serial port: ");
                for (int i = 0 ; i < currentDatapoints.length ; i++){
                    System.out.print(Integer.toString((int)currentDatapoints[i])+",");
                }

                System.out.print(" features: ");

                for (int i = 0 ; i < featureArray.length ; i++){
                    System.out.print(Integer.toString(featureArray[i])+",");
                }
                System.out.println();

                /////////SHRANI FEATURE PODATKE!!!
                if(shraniPadec && registriraneVrstice < dolzinaRegistracije) {
                    resi.saveLine(featureArray);
                    registriraneVrstice++;
                }
                    ///////_____________________________________________

            }
        } else {
            System.out.println("Unable to read from serial port..");
        }
    }

    //pregledaj ali so se vrednosti v oknu, ki morajo biti registrirane
    public void sprazniOkno(){

        while(shraniPadec && registriraneVrstice < dolzinaRegistracije){
                String[] datapoints = new String[] {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
                theWindow.fill(datapoints);
                int [] featureArray = new int[16];
                featureArray = theWindow.extractFeatures();

                //resi
                resi.saveLine(featureArray);
                registriraneVrstice++;

            }


    }



    public void propagateForce(float[] sensor_data) {}
}