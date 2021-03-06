import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class KthClassify {

    ArrayList<Feature> TrainSetAL;
    Feature Classify;
    short method;
    int k;
    private static short verbosity = 0;
    private static short VB2 = 4;
    private static short DSID = 0;

    ArrayList<Feature> candidatePos;
    ArrayList<Feature> candidateNeg;



    public static void main(String[] args) {
        KthClassify IsItBad = new KthClassify(new Feature(new double[] {0.103273398, 1.54E-4, 0.03534655,
                0.001075765, 3.07E-4, 1.54E-4, 0.001383126, 0.0, 4.61E-4, 4.61E-4, 0.0, 0.0, 6.15E-4, 0.023359459,
                0.006300907, 0.022130014, 0.00230521, 0.00245889, 0.003227294, 0.001690487, 3.07E-4, 0.001229445, 3.07E-4,
                0.001075765, 0.001075765, 4.61E-4, 0.003688336, 0.001844168, 0.029199324, 0.017980636, 0.029199324,
                6.15E-4, 0.0, 0.003688336, 0.003842016, 0.002151529, 0.0047641, 7.68E-4, 0.001844168, 1.54E-4,
                0.003073613, 0.001075765, 3.07E-4, 0.0, 3.07E-4, 0.001383126, 3.07E-4, 4.61E-4, 0.003534655,
                0.0, 0.001075765, 0.001229445, 0.001383126, 1.54E-4, 3.07E-4, 0.001229445, 0.0, 1.54E-4, 0.0,
                1.54E-4, 0.0, 1.54E-4, 0.0, 0.002151529, 0.0, 0.065006916, 0.005993545, 0.033195021, 0.038420163,
                0.06131858, 0.010911326, 0.012601813, 0.022744736, 0.051329338, 0.001383126, 0.006608268, 0.035039189,
                0.015368065, 0.032119256, 0.036883356, 0.013370217, 3.07E-4, 0.044413708, 0.05179038,
                0.066390041, 0.012755494, 0.022283694, 0.009681881, 0.001536807, 0.00937452, 1.54E-4, 1.54E-4,
                3.07E-4, 1.54E-4, 0.0}), (short)2, 3);


                ArrayList<Feature> tmpTSet = new ArrayList<>(IsItBad.TrainSetAL);
                tmpTSet.remove(0);
                for (Feature atmp : IsItBad.TrainSetAL){
                    System.out.println(atmp.FeaturePayload[3]);
                }
                IsItBad.DataSetSerializer(IsItBad.TrainSetAL);
                IsItBad.TrainSetAL = IsItBad.DataSetDeserialiser("Linnaeus_DataSet.sp");
    }

    public KthClassify(Feature aFeature, short aMethod, int k){
        method = aMethod;
        TrainSetAL = TSetToArrayList(OpenSetFromFile("./our_dataset.txt"));
        Normalize(TrainSetAL);
        Classify = aFeature;


        if(aMethod == 3) {
            Classify.TSetAccuracy = AccuracyKNN();
            KthNearestNeighbor toClassify = new KthNearestNeighbor(aFeature, TrainSetAL, k);
            if (verbosity == 1) {
                printLogVB1();
            } else if (verbosity == 2) {
                printLogVB1(true);
            }
        }
        if (aMethod == 2){
            try{
                File file = new File("./our_dataset.txt");
                GRNN g = new GRNN(5, file); //0 is all
                g.assignAccuracies();
                aFeature.TSetAccuracy = g.testInternalAccuracy()*100;
                aFeature.setGradientClassificaton(g.test(aFeature.FeaturePayload));
            }catch( IOException e){
                System.out.println("IOEXCEPT" + e);
            }
        }
        if(aMethod == 1){
            Classify.TSetAccuracy = AccuracyDWKNN();
//            KNN_distance toClassify = new KNN_distance(aFeature,TrainSetAL,k);
        }
    }


    //TODO: proceedure for online updating

    private void UpdateDataSet(Feature aNewFeat){
        //filter feature
        TrainSetAL.add(aNewFeat);
        DSID++;
    }
    private void UpdateDataSet(ArrayList<Feature> aNewListOfClassified){
        for (Feature aTemp : aNewListOfClassified){
            if (aTemp.getGradientClassificaton() == 0.0){
                aNewListOfClassified.remove(aTemp);
            }
        }
//
        TrainSetAL.addAll(aNewListOfClassified);
        DSID++;

        //add other methods to clean the current TrainSet
    }
    private void Normalize(ArrayList<Feature> anFeatureSet){

        for (int j = 0; j < anFeatureSet.size(); j++) {
            double magnitude;
            double sumMag = 0;

            for (int i = 0; i < 95; i++) {
                sumMag += (anFeatureSet.get(j).FeaturePayload[i])
                        * (anFeatureSet.get(j).FeaturePayload[i]);
            }

            magnitude = Math.sqrt(sumMag);
            for (int i = 0; i < 95; i++) {
                if (anFeatureSet.get(j).FeaturePayload[i] != 0 && magnitude != 0) {
                    anFeatureSet.get(j).FeaturePayload[i] = (anFeatureSet.get(i).FeaturePayload[i] / magnitude);
                }
            }
        }
    }
    private boolean filterDataFeature(Feature aFeature){ //returns false if feature is not candidate for the DataSet

        //if payload is all zeros return false
        boolean pass = false;
        for (double adob : aFeature.FeaturePayload){
            if (adob != 0) { pass = true;}
        }

        //other filters

        return pass;
    }
    private ArrayList<Feature> DataSetHousekeeping(ArrayList<Feature> aDataSet) {
        short numOfPos = 0;
        short numOfNeg = 0;
        int needsPosBallast = 0;
        int needsNegBallast = 0;

        //filer
        for (Feature aFeat: aDataSet){
            if (filterDataFeature(aFeat) == false){
                aDataSet.remove(aFeat);
            }
        }
        //calculate the ballast
        for (Feature aFeat: aDataSet){
            if (aFeat.Classification == true) {
                numOfPos++;
            }else{
                numOfNeg--;
            }
        }
        if ((numOfNeg + numOfPos) > 0){
            needsNegBallast = numOfNeg+numOfPos;
        }
        else if((numOfNeg + numOfPos)< 0){
            needsPosBallast = Math.abs(numOfNeg + numOfPos);
        }
        else{
            System.out.println("DS Balanced" + numOfNeg + numOfPos);
            return aDataSet;
        }

        if (candidateNeg != null || candidatePos != null){
            if (candidateNeg != null){
                UpdateDataSet(candidateNeg);
            }
            else{
                UpdateDataSet(candidatePos);
            }
            return DataSetHousekeeping(aDataSet);

        }
        else{
            System.out.println("No candidates available, removing excessive to balance");
            for (Feature aFeat: aDataSet){
                if (needsNegBallast > 0 && aFeat.Classification == true ) {
                        aDataSet.remove(aFeat);
                        needsNegBallast--;
                }else if (needsPosBallast > 0 && aFeat.Classification == false){
                    aDataSet.remove(aFeat);
                needsPosBallast--;
                }
            }
        }



        return aDataSet;


    }

    private static void DataSetSerializer(ArrayList<Feature> CurrentTrainSet){

        try {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Linnaeus_DataSet.sp"));
            oos.writeObject(CurrentTrainSet);
            oos.flush();
        }catch(EOFException eof){
            System.out.println("eof: " + eof);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static ArrayList<Feature> DataSetDeserialiser(String fileName) {

        ArrayList<Feature> inList = new ArrayList<>();
        try {
            FileInputStream file = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(file);
            inList = (ArrayList<Feature>) in.readObject();
            in.close();
            file.close();

        } catch (Exception ex) {
            System.err.println("Erreur de lecture " + ex);
        }
        return inList;

    }
    private double[][] OpenSetFromFile(String pathToDataSet){
        String line = null;
        String[] Words;
        double[][] mainList = new double[595][97];

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(pathToDataSet);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            int lineNumber = 0;
            while ((line = bufferedReader.readLine()) != null) {
                Words = line.split("\\s");
                double[] aUF = new double[96];

                for (int i = 0; i < 97; i++) {
                    try {
                        mainList[lineNumber][i] = Double.parseDouble(Words[i]);
                    } catch (NumberFormatException e) {
                        System.out.println(
                                "Unable to translate word to double");
                    }
                }
                lineNumber++;

            }

            // Always close files.
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            pathToDataSet + "'");
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + pathToDataSet + "'");
        }
        return mainList;
    }
    private ArrayList<Feature> TSetToArrayList(double[][] ATrainSet) {
        ArrayList<Feature> returnSet =  new ArrayList<>();
        Feature aFeature;
        for (int i = 0; i < ATrainSet.length; i++) {
            int aGRIndex = (int)ATrainSet[i][0];
            boolean aClassification;
            if(ATrainSet[i][1] > 0) {
                aClassification = true;
            }
            else{
                aClassification = false;
            }
            double[] aFePay = new double[ATrainSet[i].length-2];
            for (int j = 0; j < ATrainSet[i].length-2; j++) {
                aFePay[j] = ATrainSet[i][j+2];
            }

            aFeature = new Feature(aFePay, aClassification, aGRIndex);
            //System.out.println(aFeature.givenReferenceIndex + " " + aFeature.Classification + " " + Arrays.toString(aFeature.FeaturePayload));
            returnSet.add(aFeature);
        }
        return returnSet;
    }

    private double AccuracyKNN() {

        double accuracy;

        int correct = 0;
        for (int i = 0; i < TrainSetAL.size();i++){
            ArrayList<Feature> tmpTSet = new ArrayList<>(TrainSetAL);
            tmpTSet.remove(i);
            KthNearestNeighbor AccTest1 = new KthNearestNeighbor(TrainSetAL.get(i), tmpTSet, (short)0);
                if ((AccTest1.theProspectiveFeature.Classification == TrainSetAL.get(i).Classification)){
                    correct++;
                }
        }
        accuracy = ((double)correct/(double)TrainSetAL.size()) * 100;

        return accuracy;
    }
    private double AccuracyDWKNN() {

        double accuracy;

        int correct = 0;
        for (int i = 0; i < TrainSetAL.size();i++){
            ArrayList<Feature> tmpTSet = new ArrayList<>(TrainSetAL);
            Feature tmpFeat = new Feature(TrainSetAL.get(i).FeaturePayload, TrainSetAL.get(i).Classification,
                    TrainSetAL.get(i).givenReferenceIndex);
            tmpTSet.remove(i);
//            KNN_distance AccTest1 = new KNN_distance(tmpFeat, tmpTSet, k);
//            if ((AccTest1.theProspectiveFeature.Classification == TrainSetAL.get(i).Classification)){
//                correct++;
//            }
        }
        accuracy = ((double)correct/(double)TrainSetAL.size()) * 100;

        return accuracy;
    }
    private void printLogVB1(){
        if (VB2 > 3){
            System.out.println(Classify.TSetAccuracy + "% AccuracyKNN");
            VB2++;

        }
    }
    private void printLogVB1(boolean isIt2){
        System.out.println(Classify.TSetAccuracy + "% AccuracyKNN");
        for (Feature elm : TrainSetAL){
            System.out.println(elm.givenReferenceIndex + " " + elm.Classification
                    + " " + Arrays.toString(elm.FeaturePayload));

        }
    }
}