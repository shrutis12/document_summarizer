package com.autosummary.sentencescorer;

/**
 * Created by Rakhi on 06/01/2017.
 */
public class SentenceLength             /*this class is used to calculate the score of a sentence w.r.t its length*/
{
    public static double sentenceLengthScore(int length,double avgSentenceLength)
    {
        return length*avgSentenceLength;
    }

    public static int noOfWords(String sentence){

        int count=1;
        for (int i = 0; i < sentence.length(); i++) {
            if(Character.isWhitespace(sentence.charAt(i)))
                count++;
        }
        return count;
    }

}
