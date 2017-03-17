import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

public class MainExplorer {

	public static void main(String[] args) {
		String filePath = "C:\\Users\\sdua\\Documents\\datamining\\proj2\\cho.txt"; ; 
		int k = 3 ; 
		ArrayList<Integer> initialIds = new ArrayList<Integer>(); 
		initialIds.add(3) ; initialIds.add(5) ; initialIds.add(9) ; 
		
		ArrayList<ArrayList<Float>> initialCentroids = new ArrayList<ArrayList<Float>>(); 

		
		int iterations = 10 ; 
		//initialIds.add(3) ; initialIds.add(5); 
		
		boolean random = false ; 
		//random = true ; 
		ArrayList<ArrayList<Float>> database = new ArrayList<ArrayList<Float>>(); 
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String line = ""; String row[] ; int size = 0 ; 
			float dummy = 1; 
			ArrayList<Float> floatRow = new ArrayList<Float>(); Float val  ; 
			while((line=reader.readLine())!=null) {
				row = line.split("\t"); 
				size = row.length; 
				for(String s : row ){
					val = Float.valueOf(s);  
					floatRow.add(val); 
				}
				floatRow.add(dummy); 
				
				if(!random){
					float rowNumber = floatRow.get(0);
					float id = 0 ; 
					for(int p : initialIds){
						 id = (float)p;
						 if(id==rowNumber){
							 initialCentroids.add(floatRow);
						 }
					}
					
					
				}
				
				database.add(floatRow); 
				//System.out.println(floatRow);
				floatRow = new ArrayList<Float>(); 
				
			}
			//System.out.println(size);
			//System.out.println(database.size());
			//random = true ; 
			KMeans kmeans = new KMeans(database, k , random , iterations , initialCentroids); 
			
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

}
