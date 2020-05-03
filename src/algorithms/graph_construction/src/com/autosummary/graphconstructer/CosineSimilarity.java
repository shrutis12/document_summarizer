package com.autosummary.graphconstructer;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HOME on 25/03/2017.
 */
public class CosineSimilarity implements Runnable{
    static ResultSet sentences;
    static double edges[][];
    static int adj[][]=new int[50][50];
    int sentenceID;
    ILexicalDatabase db= new NictWordNet();
    public final static double threshold=0.7;
    static TreeMap<Integer, String> sentenceMap1 = new TreeMap<>();

    public CosineSimilarity(int sentenceID){

        this.sentenceID=sentenceID;
    }
    public CosineSimilarity(){}

    static void getSentences() throws Exception
    {// retrive sentenecs from original sentences table
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection= DriverManager.getConnection("jdbc:mysql://localhost:3306/summarizer","root","1234");
        // for multiple documents access both document and sentence id
        PreparedStatement statement=connection.prepareStatement("Select docid,id,sentence from sentences where selected=true");
        sentences=statement.executeQuery();
    }

    static void removeStopWords() throws Exception
    {
        getSentences();

        while (sentences.next())
        {
            int docid=sentences.getInt(1);
            int id=sentences.getInt(2);
            String text=sentences.getString(3);
            Pattern p = Pattern.compile("\\b(a|about|above|after|again|against|all|am|an|and|any|are|aren't|as|at|be|because|been|before|being|below|between|both|but|by|can't|cannot|could|couldn't|did|didn't|do|does|doesn't|doing|don't|down|during|each|few|for|from|further|had|hadn't|has|hasn't|have|haven't|having|he|he'd|he'll|he's|her|here|here's|hers|herself|him|himself|his|how|how's|i|i'd|i'll|i'm|i've|if|in|into|is|isn't|it|it's|its|itself|let's|me|more|most|mustn't|my|myself|no|nor|not|of|off|on|once|only|or|other|ought|our|ours|ourselves|out|over|own|said|same|shan't|she|she'd|she'll|she's|should|shouldn't|so|some|such|than|that|that's|the|their|theirs|them|themselves|then|there|there's|these|they|they'd|they'll|they're|they've|this|those|through|to|too|under|until|up|very|was|wasn't|we|we'd|we'll|we're|we've|were|weren't|what|what's|when|when's|where|where's|which|while|who|who's|whom|why|why's|with|won't|would|wouldn't|you|you'd|you'll|you're|you've|your|yours|yourself|yourselves)\\b\\s?",Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(text);
            text= m.replaceAll("");
            // for multiple documents consider doc id too
            insertSentence(docid,id,text);
        }

    }

    static void insertSentence(int docid,int id,String text) throws Exception
    {// inserts the modifies sentences into Database

        Class.forName("com.mysql.jdbc.Driver");
        Connection connection= DriverManager.getConnection("jdbc:mysql://localhost:3306/summarizer","root","1234");
        // for multiple docs insert into modified sentences both doc and sentence id
        PreparedStatement statement=connection.prepareStatement("insert into modified_sentences(docid,id,sentence) values(?,?,?)");
        statement.setInt(1,docid);
        statement.setInt(2,id);
        statement.setString(3,text);
        statement.executeUpdate();

        connection.close();
    }

    static void calculateSemanticSimilarity() throws Exception {
         //sentenceMap stores id and corresponding sentence
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/summarizer", "root", "1234");
        PreparedStatement statement = connection.prepareStatement("Select * from modified_sentences order by new_id");
        sentences = statement.executeQuery();
        // get sentences into map with id,text (for multiple get doc id too)
        while (sentences.next()) {
            sentenceMap1.put(sentences.getInt(3), sentences.getString(4));
        }
        for (int key:sentenceMap1.keySet()) {
            Thread sentenceThread=new Thread(new CosineSimilarity(key));
            sentenceThread.start();
            sentenceThread.join();




        } // end sentence 1 loop

    }
    static void insertCosinePair(int id1,int id2,double value) throws Exception
    {// inserts the modified sentences into Database
        //consider docid

        Class.forName("com.mysql.jdbc.Driver");
        Connection connection= DriverManager.getConnection("jdbc:mysql://localhost:3306/summarizer","root","1234");
        PreparedStatement statement=connection.prepareStatement("insert into cosine_pairs values(?,?,?)");
        statement.setInt(1,id1);
        statement.setInt(2,id2);
        statement.setDouble(3,value);
        int i = statement.executeUpdate();

        connection.close();
    }
    public static double cosineSimilarity(double[] vector1,double[] vector2)
    {
        double similarity,dotProduct=0.0,normA=0,normB=0;
      /* if(vector1.length<vector2.length)
            length=vector1.length;
        else
            length=vector2.length;*/
        int length= Integer.min(vector1.length,vector2.length);
        for (int i = 0,j=0; i < length; i++,j++) {
            dotProduct+= vector1[i]* vector2[i];
            normA+=Math.pow( vector1[i],2);
            normB+=Math.pow( vector2[i],2);
        }
        similarity= dotProduct/ (Math.sqrt(normA)* Math.sqrt(normB));
        return similarity;
    }

    public static String union(String []sentence1,String[] sentence2)
    {
        HashSet<String> sentenceSet=new HashSet<>();
        for (String s:sentence1
                ) {
            sentenceSet.add(s.toLowerCase());
        }
        for (String s:sentence2
                ) {
            sentenceSet.add(s.toLowerCase());
        }
        return  sentenceSet.toString();
    }


    // compute wu palmer similarity
    public static double compute(String word1, String word2) {
        WS4JConfiguration.getInstance().setMFS(true);
        double value = new WuPalmer(new CosineSimilarity().db).calcRelatednessOfWords(word1,word2);
        return value;
    }

    public static void calcSemanticSimilarity() throws Exception
    {
        System.out.println("cosine");
        removeStopWords();
        calculateSemanticSimilarity();

        //   new MinimalDominantSet(NODES,adj).main();
    }

    @Override
    public void run() {
        TreeMap<Integer, String> sentenceMap2 = new TreeMap<>();
        sentenceMap2.putAll(sentenceMap1);
        double[] vector1=null;
        double[] vector2 =null;
        String sentence1=sentenceMap2.get(sentenceID);
        int key2=sentenceID+1;
        for (int i=key2;i<sentenceMap2.size();i++)
        {
                String sentence2=sentenceMap2.get(i).toLowerCase(); // extract next sentence from second map

                String[] words1 = sentence1.split("\\s+"); // convert sentence 1 into bag of words
                for (int k = 0; k < words1.length; k++)
                    words1[k] = words1[k].replaceAll("[^\\w]", "");

                String[] words2 = sentence2.split("\\s+"); // convert sentence 2 into bag of words
                for (int k = 0; k < words2.length; k++)
                    words2[k] = words2[k].replaceAll("[^\\w]", "");

                String sentenceUnion = union(words1, words2).toLowerCase(); // unoin of both sentences

                String[] words3 = sentenceUnion.split("\\s+"); // convert union into bag of words
                for (int k = 0; k < words3.length; k++)
                    words3[k] = words3[k].replaceAll("[^\\w]", "");

                // sort all the arrays using alphabetical order
                Arrays.sort(words1);
                Arrays.sort(words2);
                Arrays.sort(words3);

                vector1 = new double[words3.length-1];
                // System.out.println(words3.length+" union is :"+Arrays.toString(words3));
                vector2 = new double[words3.length-1];

                for (int k = 1,index=0; k < words3.length; k++,index++) {       // loop for union
                    double max=0;
                    String str=Arrays.toString(words1);
                    if(str.contains(words3[k])) // if exact match found in sentence1
                    {
                        vector1[index]=1;
                    }
                    else
                    {
                        for (int l = 0; l < words1.length; l++) {       // loop for sentence1
                            double wordNetMeasure=compute(words3[k],words1[l]);
                            if(wordNetMeasure>max)
                                max=wordNetMeasure;
                        }   // sentence 1 wu palmer calculation to obtain max similarity

                        vector1[index]=max; // assign max similarity to vector
                    }    // end else


                    max=0;
                    str=Arrays.toString(words2);
                    if(str.contains(words3[k])) // if exact match found in sentence2
                    {
                        vector2[index]=1;
                    }
                    else
                    {
                        for (int l = 0; l < words2.length; l++) {       // loop for sentence2
                            double wordNetMeasure=compute(words3[k],words2[l]);
                            if(wordNetMeasure>max)
                                max=wordNetMeasure;
                        }   // sentence 2 wu palmer calculation to obtain max similarity

                        vector2[index]=max; // assign max similarity to vector
                    }    // end else

                }//  end union loop
                double val = cosineSimilarity(vector1, vector2);
                //threshold=0.5;
                if(val>=threshold){
                    try {
                        insertCosinePair(sentenceID,i,val);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //System.out.println(Arrays.toString(vector2));

        } // end sentence 2 loop

    }
}
