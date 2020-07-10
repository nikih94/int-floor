import java.util.ArrayList;

public class FeatureExtraction {

    public ArrayList<Integer>[] window;
    public int size = 50; //velikost okna

    public FeatureExtraction(){
        window = new ArrayList[16];
        //inicializacija
        for (int i = 0; i < 16; i++) {
            window[i] = new ArrayList<Integer>(size);
        }
    }

    public void fill(String[] datapoints){ //polni okno in ko je polno vstavi in odstrani element
        if(window[0].size()<50) { //samo polni
            for (int i = 0; i < datapoints.length; i++) {
                int force = Integer.parseInt(datapoints[i]);
                window[i].add(force);
            }
        }else{ // okno je polno
            for (int i = 0; i < datapoints.length; i++) {
                window[i].remove(0);
                int force = Integer.parseInt(datapoints[i]);
                window[i].add(force);
            }
        }
    }

    //vrne podatek na vrhu bufferja tako izris je vsklajen s featurji
    public float[] getFirstLine(){

        float[] line = new float[16];

        for (int i = 0; i < window.length; i++) {
            line[i] = window[i].get(0);
        }
        return line;
    }

    public int[] extractFeatures(){

        int[] column = new int[size];

        int[] res = new int[16];

        if(window[0].size()<50) { //cakaj da okno s napolni!!
            res = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            return res;
        }

        //printWidow();

        for (int i = 0; i < 16; i++) {
            Object[] tmp = window[i].toArray();
            for (int j = 0 ; j < tmp.length ; j++) column[j] = (int)tmp[j];
            res[i] = featureEng(column);
        }

        return res; // morda pretvori res v string??
    }


    public int featureEng(int[] column){

        int splitValue = column.length / 2;
        if(!findPeak(column,splitValue)){
            return 0;
        }else if(findTail(column,splitValue)){
            return 2;
        }else{
            return 1;
        }

    }

    //function to find peak in 25 values list
    //na zacetku je bil window size 10 sem opazil, da deluje bolje window size 5 tako,da bolje zaznamo peake ozje celo na 5 se boljse
    public boolean findPeak(int[] arr, int split){

        int w = 5; //peak finder window size

        int h = 500; // delta(max,min)>h
        int m = Integer.MAX_VALUE; //meja za min1 mora biti manjsi od te stevilke drugace ni en peak za padec
        int k = 8; // faktor repa
        int min1 = Integer.MAX_VALUE;
        int min2 = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (int i = 0; i < split-w ; i++){
            for (int j = 0; j < w ; j++){
                int value = arr[i+j];

                if((min1 > value) && ( max < 0 ) && (min2 == Integer.MAX_VALUE)){
                    min1 = value;
                }else if((max < value) && (min2 == Integer.MAX_VALUE)){
                    max = value;
                }else if(min2 > value){
                    min2 = value;
                }else if((max < value) && (min2 != Integer.MAX_VALUE)){
                    //pomisli ce postavit min1 = min2 za zaznati pravi spike drugace zaznamo tudi spice bolj siroke
                    max = value;
                    if(min1>min2){ // za testirat
                        min1 = min2;
                    }
                    min2 = Integer.MAX_VALUE;
                }

                //zadnji pogoj za previrjanje stanja
                if((min1 < m) && (min1<=min2) && ((max - min2) > (max - min1)/k ) && ( (max - min1) > h )){
                    return true;
                }
            }
            min1 = Integer.MAX_VALUE;
            min2 = Integer.MAX_VALUE;
            max = Integer.MIN_VALUE;
        }
        return false;
    }


    public boolean findTail(int[] arr, int split){

        int sum = 0;
        for (int i = split ; i < arr.length ; i++){
            sum += arr[i];
        }
        float mean = sum / (arr.length - split);

        /*
        ///Coeficient variacije ------------- nastimaj pragove
        double cv = izracunajCV(arr,split,mean) ; //Coefficient of variation

        if( (mean > 500 ) && (cv < 0.20 )){ /////POGOOOJ Doloci meje!!!!!
            return true;
        }else {
            return false;
        }
        //////
        */

        //// pragovi skozi celi signal ----- nastimaj odtotek meje

        float meja = 0.2F; //odstotek koliko se signal lahko oddali od povprecja

        float zgornja = mean + (mean * meja);
        float spodnja = mean - (mean * meja);

        for (int i = split ; i < arr.length ; i++){

            if( !((spodnja <= arr[i]) && (arr[i] <= zgornja))){
                return false;
            }

        }
        return true;
        ////////////////


    }


    public float izracunajCV(int[] arr, int split,float mean){



        double vsotaKvadratov = 0;

        for (int i = split ; i < arr.length ; i++){
            vsotaKvadratov += Math.pow(arr[i]-mean,2);
        }

        double std = Math.sqrt(vsotaKvadratov / (arr.length - split));

        return (float)(std / mean) ; //Coefficient of variation

    }

    public void printWidow(){
        System.out.println("My Window");
        for (int i = 0; i < 16 ; i++){
            for (int j = 0 ; j < window[i].size() ; j++){
                System.out.print(Integer.toString(window[i].get(j))+",");
            }
            System.out.println();
        }

    }

}
