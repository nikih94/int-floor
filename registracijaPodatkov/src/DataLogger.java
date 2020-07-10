import com.fazecast.jSerialComm.SerialPort;

import java.util.Scanner;

public class DataLogger
        extends Thread
{
    public static SerialPort port;
    public double delta = 3.5D;
    public FeatureExtraction theWindow;
    public Registracija reg;

    public void openSerialPort() {
        SerialPort[] available_ports = SerialPort.getCommPorts();

        for (int i = 0; i < available_ports.length; i++) {
            System.out.println(String.valueOf(i) + " : " + available_ports[i]);
        }
        port = available_ports[Main.port_id];
        if (port != null) {
            port.setComPortTimeouts(4096, 0, 0);
            System.out.println(port.openPort());
            if (!port.openPort()) {
                System.err.println("Unable to open the port.");
            }
        }
        port.setBaudRate(115200);
    }

    public void run() {
        for (int i = 0; i < Main.sensor_data[1].length; i++) {
            Main.sensor_data[1][i] = 1000.0F;
        }
        if (port != null) {
            float[] previous_datapoints = new float[16];
            Scanner s = new Scanner(port.getInputStream());
            theWindow = new FeatureExtraction(); //ustvarimo novo okno
            reg = new Registracija();
            reg.start();
            while (s.hasNextLine()) {
                String line = s.nextLine();
                line = line.substring(0, line.length() - 1);
                String[] datapoints = line.split(",");
                int [] featureArray = new int[16];
                if (datapoints.length > 16) {
                    System.out.println("Malformed payload by the controller");
                } else {
                    //nic feature extration
                    float[] currentDatapoints = new float[16];

                    for (int i = 0; i < 16 ; i++){
                        currentDatapoints[i] = Float.parseFloat(datapoints[i]);
                    }
                    //registracija
                    reg.pisiPodatke(line);


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
                //System.out.println("Line of text from serial port: " + line);


            }
        } else {
            System.out.println("Unable to read from serial port..");
        }
    }

    public void propagateForce(float[] sensor_data) {}
}