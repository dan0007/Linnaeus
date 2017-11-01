/**
 * Created by dbt00_000 on 10/16/2017.
 * Modified by ata0010 on 10/31/17
 */

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

class KNN_distance {

    ArrayList<Feature> TrainSet = new ArrayList<>();
    Feature theProspectiveFeature;


    public KNN_distance(Feature aProspectiveFeature, ArrayList<Feature> aTrainSet, int k) {
        TrainSet = aTrainSet;
        theProspectiveFeature = aProspectiveFeature;
        weightedDistance(TrainSet, aProspectiveFeature, k);
        rating(TrainSet, theProspectiveFeature, k);

    }

    private static double distance(double[] a, double[] b) {
        double sum = 0.0;

        for (int i = 0; i < a.length; i++) {
            //sum += Math.abs(a[i]-b[i]);   // manhattan distanc
            sum += (a[i] - b[i]) * (a[i] - b[i]);  //euclidian dist
        }
        //return sum;
        return (Math.sqrt(sum)); // euclidian distance would be sqrt(sum)...check
    }
    private static void weightedDistance(ArrayList<Feature> trainingSet, Feature aFeature, int k){
        double b = 1; //POWER Change here or add param
        double weight = 0;
        for (Feature anotherFeature : trainingSet) {
            double dist = distance(anotherFeature.FeaturePayload, aFeature.FeaturePayload);
            if (dist == 0){
                weight = 1000000000;
            }else{
                weight = 1 / Math.pow(dist,b);
            }
            aFeature.distanceBySmallest.add(new DistanceIndexPair(dist, weight,
                    aFeature.givenReferenceIndex, aFeature.Classification));

        }//sum d*w / sum w
        Collections.sort(aFeature.distanceBySmallest);
    }
    private static double rating(ArrayList<Feature> trainingSet, Feature aFeature, int k) {
        double numerator = 0.0, output, denominator = 0.0;
        int kCount = 0;

        for (DistanceIndexPair DIP : aFeature.distanceBySmallest) {
            if (kCount  == 3)
                break;
            numerator += DIP.weight * (aFeature.boolToInt(DIP.Clas));
            denominator += DIP.weight;
            kCount++;
        }
        output = (numerator / denominator);
        aFeature.setGradientClassificaton(output);
        return output;
    }
}


