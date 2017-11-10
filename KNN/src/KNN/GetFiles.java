/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package KNN;


import java.io.File;

/**
 * @author eirinimitsopoulou
 */
public class GetFiles {


	public GetFiles() {

	}

	// Get  an array of files
	public File[] getReviews(String filepath) {
		File directory = new File(filepath);

		return directory.listFiles();
	}
	
}
