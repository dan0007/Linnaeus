/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.IOException;
/**
 *
 * @author Owner
 */
public class main {
    public static void main(String[] args) throws IOException{
        File file = new File("data/our_dataset.txt");
        GRNN g = new GRNN(5, file); //0 is all
        g.assignAccuracies();
        System.out.println("Accuracy: " + g.testInternalAccuracy());
        
        //double pageRating = g.test(uniGram);
    }
}
