/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package KNN;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author eirinimitsopoulou
 */

public class InvertedIndex {
	

	private ArrayList<Document> documents;
	private HashMap<String, Set<Document>> invertedIndex;
	

	public InvertedIndex(ArrayList<Document> documents) {
		this.documents = documents;
		invertedIndex = new HashMap<String, Set<Document>>();
		
		createInvertedIndex();
	}
	

       // create an inverted index.
	private void createInvertedIndex() {
		
		for (Document document : documents) {
			Set<String> terms = document.getTermList();
			
			for (String term : terms) {
				if (invertedIndex.containsKey(term)) {
					Set<Document> list = invertedIndex.get(term);
					list.add(document);
				} else {
					Set<Document> list = new TreeSet<>();
					list.add(document);
					invertedIndex.put(term, list);
				}
			}
		}
	}

         //return the idf for a word
	public double getInverseDocumentFrequency(String word) {
		if (invertedIndex.containsKey(word)) {
			double size = documents.size();
			Set<Document> list = invertedIndex.get(word);
			double documentFrequency = list.size();
			
			return Math.log10(size / documentFrequency);
		} else {
			return 0;
		}
	}


	public ArrayList<Document> getDocuments() {
		return documents;
	}


	public HashMap<String, Set<Document>> getInvertedIndex() {
		return invertedIndex;
	}
}