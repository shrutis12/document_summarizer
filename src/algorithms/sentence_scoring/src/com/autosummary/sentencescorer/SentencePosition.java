package com.autosummary.sentencescorer;

/**
 * Created by Rakhi 06/01/2017.
 */
public class SentencePosition                  /*this class is used to calculate the score of a sentence w.r.t
                                             to its position in the document*/
{
    public static double sentencePositionScore(double position,double noOfLines)
    {
        return ((noOfLines-position+1)/noOfLines);
    }
}
