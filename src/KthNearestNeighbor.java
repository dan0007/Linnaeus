import java.util.ArrayList;
import java.util.Collections;

public class KthNearestNeighbor {

    ArrayList<DistanceIndexPair> indexOfNearestAndDistance = new ArrayList<>();
    Feature theProspectiveFeature;

    public KthNearestNeighbor(Feature Unclassified, ArrayList<Feature> TrainingSet, int k){
        theProspectiveFeature = Unclassified;
        distance(Unclassified, TrainingSet);
        classifyQuantized(k,Unclassified);
    }

    private double classify(int k) {
//        if (k == 0){
//            short bOrW = 0;
//            for (int h = 0; h < 100; h++) {
//                if (indexOfNearestAndDistance.get(h).distance < .1) {
//                    if (indexOfNearestAndDistance.get(h).Clas) {
//                        bOrW--;
//                    } else {
//                        bOrW++;
//                    }
//                }
//            }
//            if(bOrW > 0){
//                return false;
//            }
//            else{
//                return true;
//            }
//        }
        if (k == 1 && indexOfNearestAndDistance.get(0).Clas){
            return -1.0;
        } else if(k > 1 && k < 6) {
            short bOrW = 0;

            for (int h = 0; h < 10; h++) {
                if (indexOfNearestAndDistance.get(h).Clas) {
                    bOrW--;
                }
                else {
                    bOrW++;
                }
                if (k == 3 && h == 2) {
                    return (bOrW/3);
                }
                if (k == 5 && h == 4) {
                    return (bOrW/5);

                }
            }
            return 0.0;
        }else {
            System.out.println("k is not 1-3");
            return 0.0;
        }
    }
    private void classifyQuantized(int k, Feature ClassifyIT) {
//        if (k == 0){
//            short bOrW = 0;
//            for (int h = 0; h < 100; h++) {
//                if (indexOfNearestAndDistance.get(h).distance < .1) {
//                    if (indexOfNearestAndDistance.get(h).Clas) {
//                        bOrW--;
//                    } else {
//                        bOrW++;
//                    }
//                }
//            }
//            if(bOrW > 0){
//                return false;
//            }
//            else{
//                return true;
//            }
//        }
        if (k == 1 && indexOfNearestAndDistance.get(0).Clas){
            ClassifyIT.setGradientClassificaton(-1);
        }

        else if (k==1 && !indexOfNearestAndDistance.get(0).Clas) {
            ClassifyIT.setGradientClassificaton(1);
        }
        else if (k > 1 && k < 6) {
            short bOrW = 0;

            for (int h = 0; h < 10; h++) {
                if (indexOfNearestAndDistance.get(h).Clas) {
                    bOrW--;
                } else {
                    bOrW++;
                }
                if (k == 3 && h == 2) {
                    ClassifyIT.setGradientClassificaton(bOrW/3);
                }
                if (k == 5 && h == 4) {
                    ClassifyIT.setGradientClassificaton(bOrW/3);
                }
            }
        }
    }
    private void distance (Feature anUnclassified, ArrayList<Feature> aTrainingSet) {
        double distanceBetween = 0;

        for (int j = 0; j < aTrainingSet.size(); j++){

            for (int i = 0; i < 95; i++) {
                distanceBetween += (anUnclassified.FeaturePayload[i] - aTrainingSet.get(j).FeaturePayload[i])
                        * (anUnclassified.FeaturePayload[i] - aTrainingSet.get(j).FeaturePayload[i]);
            }
            distanceBetween = Math.sqrt(distanceBetween);
            indexOfNearestAndDistance.add(new DistanceIndexPair(distanceBetween,
                    aTrainingSet.get(j).givenReferenceIndex,aTrainingSet.get(j).Classification));
        }


        Collections.sort(indexOfNearestAndDistance);

    }
    private void printList(){
        for (int l = 0; l < indexOfNearestAndDistance.size(); l++){
            System.out.println(Double.toString(indexOfNearestAndDistance.get(l).distance));
        }
    }
}
