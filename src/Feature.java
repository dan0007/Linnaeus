import java.util.ArrayList;

public class Feature {

    String address = "notSet";
    int givenReferenceIndex;
    double [] FeaturePayload; //in this case its a frequency set for char of ascii 37-127
    boolean Classification = true;
    private double gradientClassificaton = 0.0;
    double TSetAccuracy;

    ArrayList<DistanceIndexPair> distanceBySmallest = new ArrayList<DistanceIndexPair>();


    public static short quadrant1Sum = 0;
    public static short quadrant2Sum = 0;
    public static short quadrant3Sum = 0;
    public static short quadrant4Sum = 0;

    public Feature(double[] aFP){
        FeaturePayload = aFP;
    }
    public Feature(double[] aFP, boolean aMN){
        FeaturePayload = aFP;
        Classification = aMN;
    }
    public Feature(double[] aFP, boolean aMN, int aGRI){
        FeaturePayload = aFP;
        Classification = aMN;
        givenReferenceIndex = aGRI;
    }
    public Feature(String aUrl, double[] aFP, double aMN, int aGRI){
        address = aUrl;
        FeaturePayload = aFP;
        gradientClassificaton = aMN;
        givenReferenceIndex = aGRI;
    }

    public int boolToInt(boolean YouBool){
        if (YouBool == true){
            return -1;
        }
        else {
            return 1;
        }
    }

    public void qSumsIncrement(){

        if (gradientClassificaton >.5){
            quadrant1Sum++;
            Classification = false;
        }
        if (gradientClassificaton > 0 && gradientClassificaton <= 0.5 ) {
            quadrant2Sum++;
        }
        if (gradientClassificaton > -0.5 && gradientClassificaton <= 0.0 ) {
            quadrant3Sum++;
        }
        if (gradientClassificaton >= -1.0 && gradientClassificaton <= -0.5 ) {
            quadrant4Sum++;
        }

//        if (quadrant1Sum < 10 || quadrant2Sum < 10 || quadrant3Sum < 10 || quadrant4Sum < 10){
//            System.out.println("Q1: " + quadrant1Sum + " Q2: " + quadrant2Sum
//                    + " Q3: " + quadrant3Sum + " Q4: " + quadrant4Sum);
//        }
    }

    public double getGradientClassificaton() {
        return gradientClassificaton;
    }

    public void setGradientClassificaton(double aGradientClassificaton) {
        this.gradientClassificaton = aGradientClassificaton;
        qSumsIncrement();
    }

}
