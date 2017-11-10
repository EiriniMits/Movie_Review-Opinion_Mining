/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author kon
 */
public class Main {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        
                Set<String> vocabularly;
		HashMap<String, Integer> positiveCatalog;
		HashMap<String, Integer> negativeCatalog;
		HashMap<String, Double> wordProbabilityPositive;
		HashMap<String, Double> wordProbabilityNegative;
		double probabilityOfClassPositive;
		double probabilityOfClassNegative;
		int totalPositiveWords;
		int totalNegativeWords;
		
		NaiveBayes naiveBayes = new NaiveBayes();
         
                File[] posfiles = naiveBayes.getReviews("train/pos");
                File[] negfiles = naiveBayes.getReviews("train/neg");
                
                //train
                long startTimeTrain = System.nanoTime();
		// Get positive word list
		ArrayList<String> positiveWords = naiveBayes.getWords("train/pos");

		// Get negative word list
		ArrayList<String> negativeWords = naiveBayes.getWords("train/neg");

		// Get instance of HashMap for positive and negative words
		positiveCatalog = naiveBayes.getPositiveWordList();
		negativeCatalog = naiveBayes.getNegativeWordList();

		// create positive and negative catalogs with word frequences
		positiveCatalog = naiveBayes.createCatalog(positiveCatalog, positiveWords);
		negativeCatalog = naiveBayes.createCatalog(negativeCatalog, negativeWords);

		// create a a set for vocabulary
		vocabularly = naiveBayes.getVocabularly();
		vocabularly.addAll(positiveCatalog.keySet());
		vocabularly.addAll(negativeCatalog.keySet());
		
		
		wordProbabilityPositive = new HashMap<>();
		wordProbabilityNegative = new HashMap<>();
		
		totalPositiveWords = positiveWords.size();
		totalNegativeWords = negativeWords.size();
		
		// Set the probability for a word given the class (Positive, Negative)
		for(int x = 0; x < 2; x++){
			if(x == 0){
				naiveBayes.computeWordProbability(wordProbabilityPositive, totalPositiveWords, positiveCatalog, vocabularly);
			}
			else{
				naiveBayes.computeWordProbability(wordProbabilityNegative, totalNegativeWords, negativeCatalog, vocabularly);
			}
		}
		
		// Probability of class been positive and negative 
		probabilityOfClassPositive = (naiveBayes.getNumberOfPositiveDocuments() / naiveBayes.getNumberOfDocuments());
		probabilityOfClassNegative = (naiveBayes.getNumberOfNegativeDocuments() / naiveBayes.getNumberOfDocuments());
		long stopTimeTrain = System.nanoTime();
                //endoftrain
		
                //test
                long startTimeTest = System.nanoTime();
                // Get arrays of test reviews
		File[] testReviews = naiveBayes.getReviews("test");
                System.out.println("Train: "+ (posfiles.length+negfiles.length) + " documents");
                System.out.println("        Positive: "+ posfiles.length + " documents");
                System.out.println("        Negative: "+ negfiles.length + " documents");
                System.out.println();
                System.out.println("Test: " + testReviews.length + " documents");
                System.out.println();
		TestReviewResults(testReviews, probabilityOfClassPositive,probabilityOfClassNegative, naiveBayes, wordProbabilityPositive, wordProbabilityNegative);
                long stopTimeTest = System.nanoTime();
                //endoftest
                
                DecimalFormat df = new DecimalFormat("#.####");

                System.out.println();
                System.out.println("Train time: " + df.format((stopTimeTrain - startTimeTrain)/ 1000000000.0) + " seconds");
                System.out.println("Test time:" + df.format((stopTimeTest - startTimeTest)/ 1000000000.0) + " seconds");
	}
	
	// classify each test review
	public static void TestReviewResults(File[] listOfReviews, double probabilityOfClassPositive,double probabilityOfClassNegative, NaiveBayes naiveBayes, HashMap<String, Double> wordVocabProbabilityPositive, HashMap<String, Double> wordVocabProbabilityNegative) throws IOException{
		
		HashMap<String, Integer> reviewTestResult = new HashMap<>();
                
                int trueNeg = 0;
                int truePos = 0;
                double accuracy;
		String wordArray[];
		String reviewClass;
		ArrayList<String> reviewWordList;
		//FileWriter writer = new FileWriter("Predictions.txt");
		for(int x = 1; x < listOfReviews.length; x++){
			
			String reviewText = naiveBayes.readFile(listOfReviews[x]);
			wordArray = reviewText.split("\\s+");
			reviewWordList = new ArrayList<>(Arrays.asList(wordArray));
			reviewClass = naiveBayes.testReviewClassification(reviewWordList, probabilityOfClassPositive,probabilityOfClassNegative, wordVocabProbabilityPositive, wordVocabProbabilityNegative);
		        String [] fileparts = listOfReviews[x].getName().split("\\.");
                        String filename = fileparts[0]; //Get first part
                        //System.out.println("File "+filename);
                        
                        /*writer = new FileWriter("Predictions.txt", true);
                        BufferedWriter bwriter = new BufferedWriter(writer);
                        PrintWriter pwriter = new PrintWriter(bwriter);
                        pwriter.print(filename); 
                        pwriter.print("\t\t");*/
			if(reviewClass.equals("Negative")){
                               // pwriter.print("0\n");  /*System.out.println(filename+" "+0);*/
                                if ( x <= 100) trueNeg++;
			}
			else{
                               // pwriter.print("1\n");  /*System.out.println(filename+" "+1);*/
                                if ( x > 100) truePos++;
			}
                        //pwriter.flush();
                        //pwriter.close();
		}
                DecimalFormat df = new DecimalFormat("#.##");
                accuracy = trueNeg + truePos;
                accuracy = accuracy/listOfReviews.length;
                System.out.println("Accuracy: " + df.format(accuracy));

	}

    
}
