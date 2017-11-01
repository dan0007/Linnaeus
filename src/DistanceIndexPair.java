public class DistanceIndexPair implements Comparable<DistanceIndexPair> {
    double distance;
    int index;
    boolean Clas;
    double weight = 0.0;
    double weightedDistance = 0.0;

    public DistanceIndexPair(double theDistance, int theIndex, boolean theClas){
        distance = theDistance;
        index = theIndex;
        Clas =theClas;
    }
    public DistanceIndexPair(double theDistance, double aWeight, int theIndex, boolean theClas){
        distance = theDistance;
        weight = aWeight;
        index = theIndex;
        Clas =theClas;
        weightedDistance = weight * distance;
    }

    public int compareTo(DistanceIndexPair DIPair) {
        return Double.compare(this.weightedDistance , DIPair.weightedDistance);
    }
}
