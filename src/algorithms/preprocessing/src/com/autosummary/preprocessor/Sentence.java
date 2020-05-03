package com.autosummary.preprocessor;

/**
 * Created by asus on 1/12/2017.
 */
public class Sentence {
    public int DOCID;
    public int ID;
   public String text;
    public double sentenceLengthScore;
    public double sentencePositionScore;
    public double cuePhraseScore;
    public double titleResemblanceScore;

    Sentence(int DOCID,int ID,String text,double sentenceLengthScore,double sentencePositionScore,double cuePhraseScore,double titleResemblanceScore)
    {
        this.DOCID=DOCID;
        this.ID=ID;
        this.text=text;
        this.sentenceLengthScore=sentenceLengthScore;
        this.sentencePositionScore=sentencePositionScore;
        this.cuePhraseScore=cuePhraseScore;
        this.titleResemblanceScore=titleResemblanceScore;
    }

   /* @Override
    public String toString() {
     return "["+DOCID+"] ["+ID+"] ["+text+"] ["+sentenceLengthScore+"] ["+sentencePositionScore+"] ["+cuePhraseScore+"] ["+titleResemblanceScore+"]";
    }*/

    @Override
    public String toString() {
        int flag=0;
        return DOCID+"#"+ID+"#"+text+"#"+sentenceLengthScore+"#"+sentencePositionScore+"#"+cuePhraseScore+"#"+titleResemblanceScore+"#"+flag;
    }
}
