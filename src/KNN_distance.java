/**
 * Created by dbt00_000 on 10/16/2017.
 */

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

class KNN_distance {
    static int stupidCheck = 0;

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        List<Sample> validationSet = readFile("our_dataset.txt");
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter number of neighbors");
        int k = sc.nextInt();
        sc.close();

        int correct = 0;
        double accuracy;
        for (int i = 0; i < validationSet.size(); i++) {
            Sample temp = validationSet.get(i);
            validationSet.remove(i);
            double testRating = rating(validationSet, temp.unigramFreq, k);
            System.out.println(i+" "+testRating + " Dk " + temp.Dk);
            if (testRating > 0 && temp.Dk > 0) //>0 72.773% AND WITH 0.34 and 74.621% AND WITH
                correct++;
            if (testRating < 0 && temp.Dk < 0) //<0
                correct++;
            validationSet.add(i,temp);
        }
        System.out.print(validationSet.size());
        accuracy = (double)correct/validationSet.size();
        System.out.print("\n\nAccuracy " + accuracy*100 + "% ");
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + TimeUnit.MILLISECONDS.toSeconds(endTime - startTime) + " seconds");



    }


    private static List<Sample> readFile(String file) throws IOException {
        List<Sample> samples = new ArrayList<Sample>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        try {
            String line;
            double sum = 0.0;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(" ");
                Sample sample = new Sample();
                sample.index = Integer.parseInt(tokens[0]);
                sample.Dk = Double.parseDouble(tokens[1]);
                sample.unigramFreq = new double[tokens.length - 2];

                for (int i = 2; i < tokens.length; i++) {
                    sample.unigramFreq[i - 2] = Double.parseDouble(tokens[i]);
                }

                for(int i=0; i<95;i++)
                {
                    sum+= sample.unigramFreq[i]*sample.unigramFreq[i];
                }
                for(int i=0; i<95;i++)
                {
                    sample.unigramFreq[i]= sample.unigramFreq[i]/(Math.sqrt(sum));
                }
                samples.add(sample);
            }
        } finally {
            reader.close();
        }
        return samples;
    }
    private static double distance(double[] a, double[] b) throws IOException {
        double sum = 0.0;
        File log1 = new File("sum.txt");

       /* if(log1.exists()==false){
            System.out.println("We had to make a new file.");
            log1.createNewFile();
        }
        PrintWriter out1 = new PrintWriter(new FileWriter(log1, true));*/
            for (int i = 0; i < a.length; i++) {


            sum += Math.abs(a[i]-b[i]);
            //     sum += (a[i] - b[i]) * (a[i] - b[i]);

        }
        return sum;
        //return (Math.sqrt(sum)); // euclidian distance would be sqrt(sum)...check
    }
    private static double rating(List<Sample> trainingSet, double[] unigramFreq, int k) throws IOException {
        int index = 0, numCount = 0;
        final double c = 0.001;
        File log = new File("log.txt");

            if(log.exists()==false){
                System.out.println("We had to make a new file.");
                log.createNewFile();
            }
            PrintWriter out = new PrintWriter(new FileWriter(log, true));


            List<Result> resultList = new ArrayList<Result>();
        for (Sample sample : trainingSet) {
            double dist = distance(sample.unigramFreq, unigramFreq);
            //System.out.print(numCount+" "+dist+" \n");
            numCount++;
            double weight = 1 / (dist + c);
            out.write("weight " + numCount+" "+String.valueOf(weight)+"\r\n");

            resultList.add(new Result(weight, sample.index, sample.Dk));
        }
        out.close();
        //writer2.close();
        Collections.sort(resultList, new DistanceComparator());

        double numerator = 0.0, output = 0.0, denominator = 0.0;
        BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
        int kCount = 0;

        resultList.remove(0); // gets rid of wrong weight
        for (Result result : resultList) {
            writer.write("result " + String.valueOf(result.weight));
            writer.newLine();
            if (kCount  == k)
                break;
            numerator += result.weight * result.Dk;
            denominator += result.weight;
            kCount++;
        }
        writer.close();
        output = (numerator / denominator);
        out.write(++stupidCheck+" r: " + " "+String.valueOf(output)+"\r\n");
        out.close();
        return output;
    }

    static class DistanceComparator implements Comparator<Result> {
        @Override
        public int compare(Result a, Result b) {
            if (a.weight == b.weight)
                return 0;
            else if (a.weight > b.weight)
                return -1;

            return 1;
        }
    }

    static class Sample {
        int index;
        double Dk;
        double[] unigramFreq;

    }
    //simple class to model results (distance, index, weight)
    static class Result {
        double weight;
        int index;
        double Dk;


        public Result(double weight, int index, double Dk) {
            this.weight = weight;
            this.index = index;
            this.Dk = Dk;

        }
    }
}


