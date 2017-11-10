/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package KNN;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author eirinimitsopoulou
 */
public class KNN {

	public static void main(String[] args) throws FileNotFoundException, IOException {
                int k = 45;
                long stopTimeTest=0;

                ArrayList<Document> documents = new ArrayList<Document>();
				
		GetFiles getfiles = new GetFiles();
                
                //train
                long startTimeTrain = System.nanoTime();
                File[] testfiles = getfiles.getReviews("test");
                File[] posfiles = getfiles.getReviews("train/pos");
                File[] negfiles = getfiles.getReviews("train/neg");
                int trueNeg = 0;
                int truePos = 0;
                double accuracy;
		
                for(int i=0;i<testfiles.length;i++){
                    
                    String s= testfiles[i].getPath();
                    documents.add(new Document(s));
                    //System.out.println(testfiles[i]);
                }
                for(int i=0;i<posfiles.length;i++){
                    
                    String s= posfiles[i].getPath();
                    documents.add(new Document(s));
                    //System.out.println(posfiles[i]);
                }
                
                for(int i=0;i<negfiles.length;i++){
                    
                    String s= negfiles[i].getPath();
                    documents.add(new Document(s));
                    //System.out.println(negfiles[i]);
                }
		
		InvertedIndex inv = new InvertedIndex(documents);		
		CosineSim cos = new CosineSim(inv);
                long stopTimeTrain = System.nanoTime();
                //endoftrain
                
                //test
                long startTimeTest = System.nanoTime();
                Double [] possim = new Double[posfiles.length];
                Double [] negsim = new Double[negfiles.length];
                FileWriter writer = new FileWriter("Predictions.txt");
               for(int p=1;p<testfiles.length;p++){
                   //find similarity with all the positive files
                    for(int i=testfiles.length;i<posfiles.length+testfiles.length;i++){
                        possim[i-testfiles.length]=cos.cosineSimilarity(documents.get(p), documents.get(i));
                    }
                       //find similarity with all the negative files
                    int l=0;
                    for(int i=posfiles.length+testfiles.length;i<negfiles.length+posfiles.length+testfiles.length;i++){
                        negsim[l]=cos.cosineSimilarity(documents.get(p), documents.get(i)); l++;
                    }
                    // sort the similarities
                    TreeMap tm = new TreeMap();

                    for(int i=(posfiles.length-1);i>=0;i--){
                      tm.put(possim[i], "1");
                    }
                    //System.out.println() ;
                    for(int i=(negfiles.length-1);i>=0;i--){
                       tm.put(negsim[i], "0");
                    }
                    Set set = tm.descendingMap().entrySet();
                    // find the k bigger similarities
                    Iterator i = set.iterator();
                    int count=0;
                    int neg=0,pos=0;
                    while(i.hasNext()){
                        if(count < k){
                          Map.Entry me = (Map.Entry)i.next();
                          Object value = me.getValue();
                            if(value == "0"){
                                neg++;}
                            else
                                pos++;
                          count++;
                        }
                        else
                            break;
                    }
                    //System.out.println("Positive:"+pos + " Negative:" +neg);
                    // find the cluster that the test file belongs  (0 or 1)
                    int answer;
                    if(pos>neg)
                        answer = 1;
                    else
                       answer = 0;
                    String[] parts = testfiles[p].getName().split("\\.");
                    //System.out.println(parts[0]+" "+answer);
                    stopTimeTest = System.nanoTime();

                    //calculating accuracy
                    if (p <= 100) {
                        if (answer==0) {
                            trueNeg++; 
                        }
                    }
                    else
                        if (answer==1){ 
                            truePos++; 
                        }

                    writer = new FileWriter("Predictions.txt", true);
                            BufferedWriter bwriter = new BufferedWriter(writer);
                            PrintWriter pwriter = new PrintWriter(bwriter);
                            pwriter.print(parts[0]+"   "+answer);
                            pwriter.print("\r\n");
                            pwriter.close();
               }
               //endoftest
               DecimalFormat df = new DecimalFormat("#.##");
               
               accuracy = trueNeg+truePos;
               accuracy = accuracy / testfiles.length;
               System.out.println("k = " + k);
               System.out.println();
               System.out.println("Train: "+ (posfiles.length+negfiles.length) + " documents");
               System.out.println("        Positive: "+ posfiles.length + " documents");
               System.out.println("        Negative: "+ negfiles.length + " documents");
               System.out.println();
               System.out.println("Test: " + testfiles.length + " documents");
               System.out.println();
               System.out.println("Accuracy: " + df.format(accuracy));
               System.out.println();
               System.out.println("Train time: " + df.format((stopTimeTrain - startTimeTrain)/ 1000000000.0) + " seconds");
               System.out.println("Test time: " + df.format((stopTimeTest - startTimeTest)/ 1000000000.0) + " seconds");
	}
}
