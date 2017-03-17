import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.commons.collections.set.SynchronizedSortedSet;
import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.fs.Path;

import org.apache.hadoop.io.IntWritable;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;

import org.apache.hadoop.mapreduce.Mapper;

import org.apache.hadoop.mapreduce.Reducer;

import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class KMeans {
	private static int k = 0 ; 
	public static  ArrayList<ArrayList<Float>> kmeans = new ArrayList<ArrayList<Float>>();  
	private static ArrayList<ArrayList<Float>> previousMeans = new ArrayList<ArrayList<Float>>(); 
	private static ArrayList<ArrayList<Float>> database = new ArrayList<ArrayList<Float>>(); 
	private static HashMap<Float , ArrayList<ArrayList<Float>>> clusterMap = new HashMap<Float , ArrayList<ArrayList<Float>>>(); 
	private static int reducerCount = 0 ; 
	private static String valueString = "";
	private static ArrayList<String> numValues = new ArrayList<String>(); 
	private static int defaultDatasize = 0 ; 
	
	public static void startAlgorithm(){
		String filePath = "/home/sdua/workspace/KMeansHadoop/src/input/iyer.txt";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			ArrayList<Float> floatRow = new ArrayList<Float>(); Float val  ;
			String line = ""; int size = 0 ; 
			while((line=reader.readLine())!=null) {
				String row[] = line.split("\t"); 
				size = row.length; 
				for(String s : row ){
					val = Float.valueOf(s);  
					floatRow.add(val); 
				}
				database.add(floatRow); 
				
				floatRow = new ArrayList<Float>(); 
			}
			//System.out.println("Database " + database.size());
			Random rand = new Random(); 
			//Collections.shuffle(database);
			
			for(int i= 0 ; i<k ; i++){
				int r = rand.nextInt(database.size()-1);
				database.get(r).add((float)(i+1));
				kmeans.add(database.get(r)); 
				//System.out.println(database.get(r));
			} 
			
			previousMeans.addAll(kmeans); 
			//for(ArrayList<Float> k : kmeans)
				//System.out.println("K size " + k.size() + " | " + k);
			
			defaultDatasize = kmeans.get(0).size() - 1 ;
			
		   } catch (Exception e) {
		
			e.printStackTrace();
			
		}
		
	}
	
	
	public static float euclDist(ArrayList<Float> p1 , ArrayList<Float> p2 ){
		float dist, temp ; 
		float sqdSum=0 ; 
		for(int i=2 ; i < defaultDatasize ; i++){
			temp = p1.get(i) - p2.get(i);
			sqdSum = sqdSum + temp*temp ; 
		}
		
		return (float) Math.sqrt(sqdSum) ;
	}
	public static float decideCluster(ArrayList<Float> inputRow){
		
		float minDist = 10000; 
		float edist = 10000; 
		float decidedCluster = -1 ; 
		clusterMap = new HashMap<Float , ArrayList<ArrayList<Float>>>();
		

		 
			minDist = 10000; 
			edist = 10000 ; 
			
			//System.out.println();
			for(ArrayList<Float> mean : kmeans){
				edist = euclDist(mean, inputRow); 
				 
				if(edist < minDist){
					minDist = edist ; 
					decidedCluster = mean.get(mean.size()-1);
				}	  
			}
		
		
		
		return decidedCluster ; 
	}
	
	public static class TokenizerMapper

	extends Mapper<Object, Text, IntWritable , Text> {

		 

		private Text word = new Text();

		public void map(Object key, Text value, Context context

				) throws IOException, InterruptedException {
				try {
					
					String newLine = value.toString();
					//System.out.println(newLine);
					ArrayList<Float> row = new ArrayList<Float>();  
					String[] rowElements = newLine.split("\t"); 
					int size = rowElements.length; 
					float val = 0 ; 
					
					for(String s : rowElements ){
						val = Float.valueOf(s.trim());  
						row.add(val); 
					}
					
					//System.out.println("Mapper - " + row.size());
					
					int cluster = (int) decideCluster(row); 
					IntWritable clus = new IntWritable(cluster) ; 
					ListWritable lwritable = new ListWritable(row); 
					 
					 
					context.write(clus, new Text(row.toString()));
					
					
				} catch (Exception e) {
					System.out.println("Exception");
					//e.printStackTrace();
				}
					
				}
  
		
		}

	

	public static class IntSumReducer

	extends Reducer<IntWritable, Text , IntWritable, Text > {

	
		public void reduce(IntWritable key, Iterable<Text> values,

				Context context

				) throws IOException, InterruptedException {
			
		 
			reducerCount++ ; 
			
			//System.out.println("\n\nReducer - ");
			ArrayList<ArrayList<Float>> clusterList = new ArrayList<ArrayList<Float>>(); 
			ArrayList<Float> row = new ArrayList<Float>(); 
			String rowArr[] ; 
			String tToList = "";
			for(Text t : values){
				row = new ArrayList<Float>(); 
				tToList = t.toString().replace("[", "");
				tToList = tToList.replace("]" , "");
				rowArr = tToList.split(",");
				for(String el : rowArr){
					row.add(Float.valueOf(el.trim()));
					
				}
			
				clusterList.add(row);
				
				
				
			}
		
			//System.out.println(key.toString() + " | " + j + " | " + clusterList.size());
			
			
			int size = clusterList.get(0).size();
			//System.out.println("Assumed mean size - " + size);
			float dummy = 0 ; 
			float colSum = 0 ; 
			int numberOfPoints = clusterList.size(); 
			ArrayList<Float> mean = new ArrayList<Float>(); 
			
			mean.add(dummy); mean.add(dummy); 
			
			
			for(int i = 2 ; i < size; i++){
				colSum = 0 ; 
				for(ArrayList<Float> list : clusterList){
					colSum += list.get(i);
				}
				colSum = colSum / numberOfPoints ; 
				mean.add(colSum);
			}
			float kfloat = key.get() ; 
			
			mean.add(kfloat);
			
			//Text text = newm Text(values.toString());
			//valueString = values.toString() ; 
			//System.out.println("New Mean Size " + mean.size());
			//System.out.println("New Mean " + mean + " \nk float " + kfloat) ;
			
			
			previousMeans = new ArrayList<ArrayList<Float>>(); 
			previousMeans.addAll(kmeans);
		    int meanSize = kmeans.get(0).size(); 
		    
			for(int i = 0 ; i < kmeans.size() ; i++){
				//System.out.println(kmeans.get(i).get(meanSize-1) + " | " + kfloat);
				if(kmeans.get(i).get(meanSize-1) == kfloat){
					//System.out.println("Cluster identf " + kfloat);
					kmeans.set(i, mean);
				}
			}
			
			
			//for(ArrayList<Float> f : kmeans) System.out.println(f);
			
			
			//System.out.println(kmeans);
			
		 
			
			//context.write(key,new Text(String

		}

	}
	
	public static void main(String[] args) throws Exception {
		
		k = Integer.valueOf(args[2]);
		
		startAlgorithm(); 
		int i = 0 ; 
		for(int j = 0 ; ; j++){
			reducerCount = 0 ; 
			
			previousMeans = new ArrayList<ArrayList<Float>>(); 
			previousMeans.addAll(kmeans);
			
			Configuration conf = new Configuration();
			
		
			Job job = Job.getInstance(conf, "word count");

			job.setJarByClass(KMeans.class);

			job.setMapperClass(TokenizerMapper.class);

			job.setCombinerClass(IntSumReducer.class);

			job.setReducerClass(IntSumReducer.class);

			job.setOutputKeyClass(IntWritable.class);

			job.setOutputValueClass(Text.class);

			FileInputFormat.addInputPath(job, new Path(args[0]));
			String outPath = args[1] + i++ + ""; 

			FileOutputFormat.setOutputPath(job, new Path(outPath));
			job.waitForCompletion(true);
			
			if(previousMeans.equals(kmeans)) {
				//for(ArrayList<Float> f : kmeans) System.out.println(f);
				//calculateEfficiency();
				System.out.println("Iterations - " + i);
				
				break ; 
			}
			
		}
		
		
		
		
		calculateEfficiency(); 

		//System.exit(job.waitForCompletion(true) ? 0 : 1);
		
		
	}


	private static void calculateEfficiency() {
		//assign labels
		ArrayList<ArrayList<Float>> datawithLables = new ArrayList<ArrayList<Float>>();
		ArrayList<Float> row = new ArrayList<Float>(); 
		for(ArrayList<Float> point : database){
			row = new ArrayList<Float>();
			row.addAll(point);
			row.add(decideCluster(row));
			datawithLables.add(row);
			
			//System.out.print(row.get(row.size()-1) + "\t"); 
			//System.out.println(row); 
		}
		
		int m_zero_zero = 0 , m_zero_one = 0 , m_one_zero = 0 , m_one_one = 0  ; 
		float clusterNumber = 0 ; float ground = 0 , predicted = 0 ; int p = 0 , c = 0 ;
		int count = 0 ; 
		int databaseSize = datawithLables.size() ; 
		int pairs = (databaseSize * (databaseSize-1))/2 ; 
		int listSize = datawithLables.get(0).size(); 
		
		
		for(int j = 0; j < databaseSize-1 ; j++){
			for(int next = j+1 ; next < databaseSize ; next++){
					count++ ; 
					p = 0 ; c=  0 ; 
					if(datawithLables.get(j).get(1)!=-1 || datawithLables.get(next).get(1)!=-1){
						if(datawithLables.get(j).get(1).equals(datawithLables.get(next).get(1))) p = 1 ;
						if(datawithLables.get(j).get(listSize-1).equals(datawithLables.get(next).get(listSize-1))) c = 1 ;
						
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
		
		
		
		
		
		
		float jackard = (float)m_one_one/(m_one_one + m_one_zero + m_zero_one) ; 
		float rand = (float)(m_zero_zero + m_one_one)/(m_one_one + m_one_zero + m_zero_one+m_zero_zero) ; 
		
		//System.out.println("Jackard - " + jackard  );
		System.out.println("\nRand - " + rand);
				 
	}

}