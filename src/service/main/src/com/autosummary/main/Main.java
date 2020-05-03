
package com.autosummary.main;

import com.autosummary.databaseconnections.DBConnectivity;
import com.autosummary.graphconstructer.CosineSimilarity;
import com.autosummary.graphconstructer.CosineSimilarity2;
import com.autosummary.mds.MinimalDominantSet;
import com.autosummary.mds.MinimalDominantSet2;
import com.autosummary.preprocessor.Preprocessor;

import java.io.File;


public class Main {
    public static void main(String[]args) throws Exception
    {
        new DBConnectivity().flushPreviousData();

       Preprocessor.preprocess();

      CosineSimilarity2.calcSemanticSimilarity();
       // CosineSimilarity.calcSemanticSimilarity();
     MinimalDominantSet.generateMinimalDominantSet();
       // MinimalDominantSet2.generateMinimalDominantSet();
        String path="E:\\BE_proj\\April_END\\BEfront\\uploads";
        File folder = new File(path);

        //FileUtils.cleanDirectory(folder); // delete all the files in the directory
    }

}

