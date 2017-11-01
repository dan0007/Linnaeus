/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Comparator;

/**
 *
 * @author Owner
 */
public class NodeComparator implements Comparator<Node>{
    @Override
    public int compare(Node n1, Node n2) {
        if (n1.getDistance() > n2.getDistance())
            return 1;
        else if (n1.getDistance() < n2.getDistance())
            return -1;
        
        return 0;    
    }
}
