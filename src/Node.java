/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Owner
 */
public class Node {
    private double[] features = new double[95];
    private double rating;
    private double distance = 0;
    
    private double weight = 1;
    //////////////////////////////
    private double accuracy = 1;
    double correct = 0;
    double numTests = 0;
    /////////////////////////////
    
    
    public Node(double[] features, double rating) {
        for (int i = 0; i < 95; i++)
            this.features[i] = features[i];
        this.rating = rating;
        
    }
    @Override
    public Node clone() {
        Node newNode = new Node(this.features, this.rating);
        newNode.setWeight(this.weight);
        return newNode;
        
    }
    public double getRating() {
        return rating; //* weight;
    }
    public void randomizeWeight() {
        //weight = Math.random();
    }
    public void setWeight(double w) {
        weight = w;
    }
    public double getWeight(){
        return weight;
    }
    public double getAccuracy() {
        return accuracy;
    }
    public void updateAccuracy(double result) {
        if (result > 0 && rating > 0) {
            correct++;
        }
        if (result < 0 && rating < 0) {
            correct++;
        }
        numTests++;
        accuracy = correct / numTests;
    }
    public void clearAccuracy() {
        accuracy = 0;
        numTests = 0;
        correct = 0;
    }
    public double[] getFeatures() {
        return features;
    }
    
    
    public double getDistance() {
        return distance;
    }
    
    public void calculateDistance(double[] f) {
        double dis = 0;
        
        for (int i = 0; i < 95; i++) {
            dis += ((features[i] - f[i]) * (features[i] - f[i]));
        }
        distance = Math.sqrt(dis);
        
    }
    
    public double calculateHF(double sd) {
        return Math.exp( -Math.pow(distance, 2)  / (2 * Math.pow(sd, 2))   ); 
    }
    
}
