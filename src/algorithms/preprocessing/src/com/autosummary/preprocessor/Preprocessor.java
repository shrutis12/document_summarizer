package com.autosummary.preprocessor;


import com.autosummary.threshold.Threshold;

import java.io.File;
/**
 * Created by asus on 1/12/2017.
 */
public class Preprocessor implements Runnable{

    static String path="E:\\BE_proj\\April_END\\BEfront\\uploads\\";       //path to the document to be summarized
   static File folder = new File(path);
   static File[] listOfFiles = folder.listFiles();
   int docid;

   public Preprocessor(int docID)
   {
        this.docid=docID;
   }
    public static void preprocess() throws Exception {

        for (int i = 1; i <= listOfFiles.length; i++) {
            Thread thread=new Thread(new Preprocessor(i));
            thread.start();
            thread.join();
        }
    }

    @Override
    public void run() {
        try {
            String fileName = listOfFiles[docid - 1].getName();
            System.out.println(path + fileName);
            Tokenizer extractor = new Tokenizer(path + fileName, docid);
            extractor.tokenize();
            Threshold.applyThreshold(docid);
        }

        catch(Exception e)
        {
            System.out.println(e);
        }
    }
}
