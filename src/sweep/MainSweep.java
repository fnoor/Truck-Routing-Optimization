package sweep;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class MainSweep {

	public static void main(String[] args) {
        try {
        	//demand 
        	//update file name for each product T1P1.txt, T1P2.txt etc 
        	ReadFile readFile = new ReadFile("src/sweep/T1P1.txt"); 
            ArrayList<String> demandMatrix = readFile.openFile();
            List<HashMap<Integer, Double>> demands = new ArrayList<HashMap<Integer, Double>>(); 
            
            //distance from warehouse
            ReadFile readFile2 = new ReadFile("src/sweep/distance.txt"); 
            ArrayList<String> disMatrix = readFile2.openFile();
            List<HashMap<Integer, Integer>> distances = new ArrayList<HashMap<Integer, Integer>>();
            
            //retailer distances 
            ReadFile readFile3 = new ReadFile("src/sweep/RDistance.txt"); 
            ArrayList<String> rDisMatrix = readFile3.openFile();
            HashMap<String, Double> rPairs = new HashMap<>();
            
            //structure for distance between retailer to retailer
            for (int i = 0; i < rDisMatrix.size(); i++) {
            	
                String[] adjacencyList = rDisMatrix.get(i).split("\\s+");
                
                for (int j = 0; j < adjacencyList.length; j++) {
                    double connection = Double.parseDouble(adjacencyList[j]);
                    if (connection > 0) {
                        rPairs.put(""+i+j,connection); 
                    }
                }
            }
         
            //create respective structures for demand and distances for each warehouse 
            for(int i = 0; i < demandMatrix.size(); i++){
            	
            	String[] demandList = demandMatrix.get(i).split("\\s+");
            	HashMap<Integer, Double> d = new HashMap<Integer, Double>(); 
            	String[] disList = disMatrix.get(i).split("\\s+");
            	HashMap<Integer, Integer> di = new HashMap<Integer, Integer>();
            	
            	for (int j = 0; j < demandList.length; j++) {
                    double demandAtRet = Double.parseDouble(demandList[j]);
                    
                    int disAtRet = Integer.parseInt(disList[j]);
                    if(demandAtRet != 0){
                    	d.put(j, demandAtRet); 
                		di.put(j, disAtRet);
                    }
            	}
            	demands.add(i, d);
            	distances.add(i, di);
            }
            
            //truck assignments 
            List<HashMap<String, List<Integer>>> allRoutes = new ArrayList<HashMap<String,List<Integer>>>(); 
            double cost = 0; 
            double oldCost = 250*10; 
            for(int i = 0; i < distances.size(); i++){
            	HashMap<String, List<Integer>> route = new HashMap<>();
            	int truck = 1; 
                double tCap = 40; 
                int current = minIndex(distances.get(i));
                double retDem = demands.get(i).get(current); 
                int count = 0; 
                
                while(!distances.get(i).isEmpty()){
                	if(count > 0){
                		tCap = 40;
                	} 
                	List<Integer> retailers = new ArrayList<>(); 
                    while(tCap >= retDem && !distances.get(i).isEmpty()){
                    	retailers.add(current+1);
                    	count = 1; 
                    	tCap = tCap - retDem; 
                    	oldCost += 10*distances.get(i).get(current)*2; 
                        distances.get(i).remove(current);
                        
                        
                        double min = 1000; 
                        double rrDis = 0; 
                        for (Integer key : distances.get(i).keySet()) {
                            int index = (int) key; 
                            String pair; 
                            
                            	if(current > index){
                                	pair = ""+ index+current; 
                                }else{
                                	pair = ""+current+index; 
                                }
                            	//create new hashmap with the pairs and their distances, find lowest distance 
                            	
                            	rrDis = rPairs.get(pair); 
                            	
                            	if(rrDis < min){
                            		min = rrDis; 
                            		current = index; 
                            	}
                        }
                        cost += 10*rrDis; 
                        retDem = demands.get(i).get(current); 
                    }
                    route.put("W"+(i+1)+"T"+truck, retailers);
                    
                	truck++;
                	cost += 250;
                }
                 
                allRoutes.add(route);
            }
            System.out.println("Truck Capacity: 40 pallets/truck, 3 units per pallet, 120 units per truck" );
            System.out.println("");
            System.out.println("Total Old Cost ($250 per truck + $10 per KM): $" + oldCost);
            System.out.println(""); 
            System.out.println(allRoutes); 
            System.out.println("New Route: 1 truck for W1, 1 truck for W2"); 
            System.out.println("Total New Cost ($250 per truck + $10 per KM): $" + cost); 
            System.out.println("");
            System.out.println("Old Cost: $" + oldCost);
            System.out.println("New Cost: $" + cost);
            System.out.println("Total Savings: $" + (oldCost - cost));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
	}
	public static int minIndex(HashMap<Integer, Integer> map) {
		Entry<Integer, Integer> min = null;
		for (Entry<Integer, Integer> entry : map.entrySet()) {
		    if (min == null || min.getValue() > entry.getValue()) {
		        min = entry;
		    }
		}
		return min.getKey();
	}
	
	
}
