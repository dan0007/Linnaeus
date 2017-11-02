import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

import java.util.Random;


public class GRNN {

    public ArrayList<Node> nodes = new ArrayList();
    private int k;
    private Random myRand = new Random();

    public GRNN(int k, File data) throws IOException {

        Scanner sc = new Scanner(data);
        double rating;
        double[] features = new double[95];
        double magnitude = 0;
        while (sc.hasNext()) {

            sc.next(); //Discard label
            rating = Float.parseFloat(sc.next());
            for (int i = 0; i < 95; i++) {
                features[i] = Float.parseFloat(sc.next());
                magnitude += Math.pow(features[i], 2);
            }
            magnitude = Math.sqrt(magnitude);
            for (int i = 0; i < 95; i++) {
                features[i] = features[i] / magnitude;
            }

            nodes.add(new Node(features, rating));
        }
        if (k == 0) {
            this.k = nodes.size();
        } else {
            this.k = k;
        }
    }

    public double test(double[] f) {
        double avg = 0;
        double var = 0;
        double sd = 0;

        for (Node n : nodes) {
            n.calculateDistance(f);
        }
        nodes.sort(new NodeComparator());
        if (k == 1) {
            return nodes.get(0).getRating();
        }

        
        double distances[] = new double[k];
        double ratings[] = new double[k];
        double hf[] = new double[k];

        for (int i = 0; i < k; i++) {  //Calculate the Average Distance to K nearest Nodes
            distances[i] = nodes.get(i).getDistance();
            ratings[i] = nodes.get(i).getRating();            
            avg += distances[i];
        }
        avg = avg / k;

        for (int i = 0; i < k; i++) {
            var += Math.pow((distances[i] - avg), 2) / k;

        }
        sd = Math.sqrt(var);
        sd += 0.02; //Increase the SD so that it can't be almost zero :)

        for (int i = 0; i < k; i++) {
            hf[i] = Math.exp(-Math.pow(distances[i], 2) / (2 * Math.pow(sd, 2))) * nodes.get(i).getWeight() + 0.0001;
            //System.out.println(nodes.get(i).getAccuracy());
            if (hf[i] == 0) {
                System.out.println("HF is Zero. Distance: " + distances[i] + " SD: " + sd + " Stuff: " + -Math.pow(distances[i], 2) / (2 * Math.pow(sd, 2)));
            }
        }
        //Final Calc
        double rating = 0;
        double numerator = 0;
        double denominator = 0;

        if (sd == 0) {
            for (int i = 0; i < k; i++) {
                rating += ratings[i] / k;
            }
            System.out.println(rating);
            return rating;
        }

        for (int i = 0; i < k; i++) {
            numerator += hf[i] * ratings[i];
        }
        for (int i = 0; i < k; i++) {
            denominator += hf[i];
        }

        rating = numerator / denominator;
        if (denominator == 0) {
            System.out.println("Denominator is ZERO!!");
        }
        //System.out.println(rating);
        if (k > 1) {
            for (int i = 0; i < k; i++) {
                nodes.get(i).updateAccuracy(rating);
            }
        }
        return rating;

    }

    public double testInternalAccuracy() {
        if (k == nodes.size()) //If using all nodes
        {
            k--;              //prepare k for the left out node
        }
        
        double correct = 0;
        double accuracy;

        for (int i = 0; i < nodes.size(); i++) {
            Node temp = nodes.get(i);
            nodes.remove(i);
            double testRating = test(temp.getFeatures());

            if (testRating > 0 && temp.getRating() > 0) {
                correct++;
            }
            if (testRating < 0 && temp.getRating() < 0) {
                correct++;
            }

            nodes.add(i, temp);
        }

        accuracy = correct / nodes.size();
        return accuracy;
    }
    public void assignAccuracies() {
        if (k == nodes.size()) //If using all nodes
        {
            k--;              //prepare k for the left out node
        }
        for (Node n : nodes) {
            n.clearAccuracy();
        }

        for (int i = 0; i < nodes.size(); i++) {
            Node temp = nodes.get(i);
            nodes.remove(i);
            test(temp.getFeatures());
            nodes.add(i, temp);
        }

        for (Node n : nodes) {
            n.setWeight(n.getAccuracy());

        }

    }

    public double train(int iterations) {
        double oldAccuracy = testInternalAccuracy();
        double newAccuracy;
        ArrayList<Node> oldNodes;
        for (int i = 0; i < iterations; i++) {

            oldNodes = new ArrayList<Node>();
            for (Node n : nodes) {
                oldNodes.add(n.clone());
            }

            for (Node n : nodes) {
                //n.setWeight(n.getWeight() + (((myRand.nextDouble() * 2) - 1) / 10));
                n.setWeight(0);
            }
            newAccuracy = testInternalAccuracy();
            System.out.println("A" + newAccuracy);

            if (oldAccuracy > newAccuracy) {
                nodes = oldNodes;
            } else {
                oldAccuracy = newAccuracy;
            }

        }

        return oldAccuracy;
    }

}
