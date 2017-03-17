import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class KMeans {
	private ArrayList<ArrayList<Float>> database ; 
	private static int k = 0 ; 
	private int iteration = 0 ; 
	private static ArrayList<ArrayList<Float>> kmeans = new ArrayList<ArrayList<Float>>(); 
	private static ArrayList<ArrayList<Float>> groundMeans = new ArrayList<ArrayList<Float>>();
	private static ArrayList<ArrayList<Float>> previousMeans = new ArrayList<ArrayList<Float>>();
	private static HashMap<Float , ArrayList<ArrayList<Float>>> clusterMap = new HashMap<Float , ArrayList<ArrayList<Float>>>(); 
	private static HashMap<Float , ArrayList<ArrayList<Float>>> groundMap = new HashMap<Float , ArrayList<ArrayList<Float>>>();
	private static int numberOfIterations = 1 ; 
	private static int defaultColNums = 1 ; 
	public KMeans(ArrayList<ArrayList<Float>> database2, int k , boolean random ,  int numberOfIterations , ArrayList<ArrayList<Float>> initialIds){
		this.database = database2 ; 
		this.k = k ; 
		
		
		if(!random){
			kmeans = new ArrayList<ArrayList<Float>>(); 
			kmeans.addAll(initialIds); 
			previousMeans = new ArrayList<ArrayList<Float>>(); 
			previousMeans.addAll(kmeans);
		}
		//else 
			startAlgorithm(); 
		
		//System.out.println("Initial Centroids - ");
		//printMeans();
		 
		
		for(int i = 0 ;  ; i++){
			decideCluster(); 
			updateMeans();
			//printMeans();
			if(random){
				if(kmeans.equals(previousMeans)){
					System.out.println("Iterations - " + i);
					break ; 
				}
			}
			else {
				
				 
				if(i==numberOfIterations) break ; 
				
			}
			
		}
		//System.out.println("Final Centroids - ");
		//printMeans();
		
	 calculateGroundMeans(); 
	 calculateEfficiency(); 
	 
		
	}
	
	 
	
	
	private void calculateEfficiency() {
		int m_zero_zero = 0 , m_zero_one = 0 , m_one_zero = 0 , m_one_one = 0  ; 
		float clusterNumber = 0 ; float ground = 0 , predicted = 0 ; int p = 0 , c = 0 ;
		int count = 0 ; 
		int databaseSize = database.size() ; 
		int pairs = (databaseSize * (databaseSize-1))/2 ; 
		int listSize = database.get(0).size(); 
		 
		for(int j = 0; j < database.size()-1 ; j++){
				for(int next = j+1 ; next < databaseSize ; next++){
						count++ ; 
						p = 0 ; c=  0 ; 
						if(database.get(j).get(1)!=-1 || database.get(next).get(1)!=-1){
							if(database.get(j).get(1).equals(database.get(next).get(1))) p = 1 ;
							if(database.get(j).get(listSize-1).equals(database.get(next).get(listSize-1))) c = 1 ;
							
							if(c==1&&p==1){
								m_one_one++;
							}
							

							else if(c==0&&p==0){
								m_zero_zero++;
							}
							

							else if(c==1&&p==0){
								m_one_zero++;
							}
							
							else if(c==0&&p==1){
								m_zero_one++;
							}
							 
							}
						}
						
				
			}
		
		
		//System.out.println(m_one_one + " | " + m_zero_zero + " | " +  m_one_zero + " | " +  m_zero_one);
		float jackard = (float)m_one_one/(m_one_one + m_one_zero + m_zero_one) ; 
		float rand = (float)(m_zero_zero + m_one_one)/(m_one_one + m_one_zero + m_zero_one+m_zero_zero) ; 
		
		//System.out.println("Jackard - " + jackard  );
		System.out.println("Rand - " + rand);
		int ind = database.get(0).size() - 1 ; 
		for(ArrayList<Float> ro : database){
			//System.out.println(ro);
			//System.out.print(ro.get(ind) + "\t");
		}
	}
	private void calculateGroundMeans() {
		ArrayList<ArrayList<Float>> interm =  new ArrayList<ArrayList<Float>>(); 
		for(ArrayList<Float> point : database){
			
			if(groundMap.get(point.get(1))!=null){
				interm = new ArrayList<ArrayList<Float>>(); 
				interm.addAll(groundMap.get(point.get(1))) ;
				interm.add(point);
				groundMap.put(point.get(1), interm);
			}
			else{
				interm = new ArrayList<ArrayList<Float>>(); 
				interm.add(point); 
				groundMap.put(point.get(1), interm); 
			}
		}
		
		 ArrayList<Float> meanPoint = new ArrayList<Float>(); 
		//calcutate ground means 
		for(int i=1; i<=k ; i++){
			interm =  new ArrayList<ArrayList<Float>>(); 
			meanPoint = new ArrayList<Float>();
			float dummy = 1 ;
			meanPoint.add(dummy);
			meanPoint.add(dummy);
			interm = groundMap.get((float)i);
			//System.out.println(i + " | " + interm.size());
			float sum = 0 ; 
			for(int j = 2 ; j < interm.get(0).size()-1 ; j++){
				  sum = 0; 
				  for(ArrayList<Float> p : interm){
					  sum += p.get(j);
				  }
				  
				  sum = sum / interm.size();
				  meanPoint.add(sum); 
				  
				  
			}
			meanPoint.add((float)i);
			groundMeans.add(meanPoint);
		}

		/*System.out.println("ground means - \n");
		for(ArrayList<Float> gm: groundMeans){
			System.out.println(gm);
		}*/
		 
		/*Collections.sort(kmeans, new Comparator<ArrayList<Float>>() {

			@Override
			public int compare(ArrayList<Float> o1, ArrayList<Float> o2) {
				return (int) (o1.get(o1.size()-1) - o2.get(o1.size()-1));
			}
		});*/
		
		/*System.out.println("k means - \n");
		for(ArrayList<Float> gm: kmeans){
			System.out.println(gm);
		}
		float closestDist = 0 , edist = 0 ; 
		int size = kmeans.get(0).size();
		for(int i =  0 ; i< groundMeans.size();i++){
			System.out.print("Ref Cluster " + i);
			closestDist = 1000 ; edist = 1000 ; int cpoint = 0 ;
			for(int j = 0 ; j < kmeans.size() ; j++){
				 edist = euclDist(kmeans.get(i),groundMeans.get(j));
				 if(edist< closestDist){
					 closestDist = edist ; 
					 cpoint = j ; 
				 }
			}
			System.out.print(" | Closest " + cpoint + "\n");
		}*/
		
		
		/*Iterator itGround = groundMap.entrySet().iterator() ; 
		
		
		ArrayList<ArrayList<Float>> intermedGround = new ArrayList<ArrayList<Float>>(); 
		ArrayList<ArrayList<Float>> intermedKMeans = new ArrayList<ArrayList<Float>>(); 
		int size = kmeans.get(0).size(); 
		while(itGround.hasNext()){
			
			
			Entry pairGround = (Entry) itGround.next() ; 
			
			
			intermedGround = new ArrayList<ArrayList<Float>>(); 
			intermedGround = (ArrayList<ArrayList<Float>>) pairGround.getValue();
			System.out.println("Ground " + pairGround.getKey());
			int maxMatches = 0 ; int matches = 0 ; float matchedCluster = 0 ;
			Iterator itMeans = clusterMap.entrySet().iterator();
			while(itMeans.hasNext()){
				
				Entry pairKMeans = (Entry) itMeans.next(); 
				intermedKMeans = new ArrayList<ArrayList<Float>>();
				intermedKMeans = (ArrayList<ArrayList<Float>>) pairKMeans.getValue();
				
				int listSize = intermedKMeans.size(); 
			
				matches = 0 ; 
				for(ArrayList<Float> ground : intermedGround){
					
					
					for(ArrayList<Float> kmean : intermedKMeans){
						if(ground.subList(2, size-2).equals(kmean.subList(2, size-2))){
							matches++ ; 
						}
					
					}
				}
				
				System.out.println("Ground " + pairGround.getKey() + "(" + intermedGround.size() + ")" + " | Cluster  " + pairKMeans.getKey() + "(" + intermedKMeans.size() + ")" + " | Matches - " + matches) ; 
				if(matches>maxMatches){
					matchedCluster = (float) pairKMeans.getKey(); 
					maxMatches = matches ; 
					 
				}
				
				
			}
			
			System.out.println("Ground Point - " + pairGround.getKey() + " Max Matches with - " + matchedCluster);
			
			
		}
		
		
		 */
		
		 
	}
	public void printMeans(){
		System.out.println("=================================");
		for(ArrayList<Float> l : kmeans){
			System.out.println(l);
		}
		System.out.println("=================================");
	}
	public void startAlgorithm(){
		
		Random rand = new Random(); 
		//Collections.shuffle(database);
		
		for(int i= 0 ; i<k ; i++){
			int r = rand.nextInt(database.size()-1);
			database.get(r).add((float)(i+1));
			kmeans.add(database.get(r)); 
			//System.out.println(database.get(r));
		} 
		
		defaultColNums = kmeans.get(0).size()-1;
		
		previousMeans.addAll(kmeans); 
		
	}
	
	public float euclDist(ArrayList<Float> p1 , ArrayList<Float> p2 ){
		float dist, temp ; 
		float sqdSum=0 ; 
		for(int i=2 ; i < p1.size()-1 ; i++){
			temp = p1.get(i) - p2.get(i);
			sqdSum = sqdSum + temp*temp ; 
		}
		
		return (float) Math.sqrt(sqdSum) ;
	}
	
	public void decideCluster(){
		float minDist = 10000; 
		float edist = 10000; 
		clusterMap = new HashMap<Float , ArrayList<ArrayList<Float>>>();
		
		for(ArrayList<Float> row : database){
			minDist = 10000; 
			edist = 10000 ; 
			float desiredCluster = -1;
			//System.out.println("\n=======================");
			//System.out.println(row);
			for(ArrayList<Float> mean : kmeans){
				edist = euclDist(mean, row); 
				//System.out.print(edist + " , ");
				if(edist < minDist){
					minDist = edist ; 
					desiredCluster = mean.get(mean.size()-1);
				}
					  
			}
			
			//System.out.print(" | " + desiredCluster);
			
			row.remove(row.size()-1);
			row.add(desiredCluster); 
			//System.out.print(" | " + minDist + " , " + desiredCluster);
			
			ArrayList<ArrayList<Float>> updateCluster = new ArrayList<ArrayList<Float>>(); 
			if(clusterMap.get(desiredCluster)!=null){
				
				updateCluster = clusterMap.get(desiredCluster); 
				//System.out.println("Exists | " + desiredCluster + " | " + updateCluster.size());
				updateCluster.add(row); 
				clusterMap.put(desiredCluster, updateCluster); 
			}
			else{
				//System.out.println("First | " + desiredCluster);
				updateCluster.add(row); 
				clusterMap.put(desiredCluster, updateCluster); 
			}
			
		}
		 
	}
	
	public void updateMeans(){
		previousMeans = new ArrayList<ArrayList<Float>>();  
		previousMeans.addAll(kmeans); 
		kmeans = new ArrayList<ArrayList<Float>>();
		Iterator it = clusterMap.entrySet().iterator(); 
		float dummy = 0 ;  
		float dummy1 = 1 ; 
		System.out.println("======================================\n");
		while(it.hasNext()){
			Map.Entry pair = (Map.Entry)it.next(); 
			
			ArrayList<ArrayList<Float>> clusterList = (ArrayList<ArrayList<Float>>) pair.getValue(); 
			ArrayList<Float> newMean = new ArrayList<Float>(); 
			int numberOfPoints = clusterList.size(); 
			int numCols = clusterList.get(0).size(); 
			newMean.add(0, dummy1); newMean.add(1,dummy1);
			for(int i = 2 ; i < numCols-2 ; i++){
				 newMean.add(dummy);
			}
			newMean.add((Float) pair.getKey());
			System.out.println(pair.getKey() + " | " + numberOfPoints);
			float currVal = 0 ; 
			
			for(int i = 2 ; i < numCols-2 ; i++){
				currVal = 0 ; 
				for(ArrayList<Float> list : clusterList){
					currVal = currVal + list.get(i)/numberOfPoints; 
					newMean.set(i, currVal); 
				}
			}
			kmeans.add(newMean);
			 
		}
		
		
	}
	
}
