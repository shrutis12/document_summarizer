package com.autosummary.preprocessor; /**
 * Created by Rakhi on 1/12/2017.
 * This class is used to extract raw text data from any type of file .
 * P.S. unstructuerd sentences are obtained from this class using Apache Tika API.
 *
 */
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

//import java.util.Scanner;

public class TikaExtraction {

    String path; // path of file to be extracted
    int documentID;

    public TikaExtraction(String path,int documentID)
    {
        this.path=path;
        this.documentID=documentID;
    }

    public void extract() throws IOException, SAXException,TikaException {

        BodyContentHandler handler=new BodyContentHandler(-1);
        File file = new File(path);
        Tika tika = new Tika();
        FileOutputStream out=new FileOutputStream(new File(documentID+"_sample.txt")); // save the string to sample.txt file
        out.write(tika.parseToString(file).getBytes()); // extract text from input file a1nd write into sample file
        out.close();
    }
}