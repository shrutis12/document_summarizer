package com.autosummary.graphconstructer;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Rakhi on 2/13/2017.
 */
public class SimilarityPair {
    int sentenceID1;
    int sentenceID2;
    double similarity;

    public SimilarityPair(int sentenceID1,int sentenceID2,double similarity)
    {
        this.sentenceID1=sentenceID1;
        this.sentenceID2=sentenceID2;
        this.similarity=similarity;
    }
    @Override
    public String toString() {
        return sentenceID1+","+sentenceID2+","+similarity;
    }
   }
