package com.autosummary.preprocessor; /**
 * Created by Rakhi on 1/12/2017.
 * This class convertes the unstructured sentences into proper sentences using breakIterator interface
 * calculates sentence id their respective scores
 */

import com.autosummary.databaseconnections.DBConnectivity;
import com.autosummary.sentencescorer.CuePhrase;
import com.autosummary.sentencescorer.SentenceLength;
import com.autosummary.sentencescorer.SentencePosition;

import java.io.File;
import java.io.FileOutputStream;
import java.text.BreakIterator;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Scanner;




public class Tokenizer extends TikaExtraction{

   // public static final Logger logger = new LoggerFactory.getLo(Tokenizer.class);

    public Tokenizer(String path,int documentID)
    {
        super(path,documentID);
    }

    public void tokenize() throws Exception{

        extract();  // extract raw text apache tika
        Locale locale = Locale.US;
        BreakIterator wordIterator =
                BreakIterator.getWordInstance(locale);
        String temp="";
        Scanner scan=new Scanner(new File(documentID+"_sample.txt"));   // open raw text data file obtained using tika
        while(scan.hasNext())
            temp+=" "+scan.next();

        // remove special characters from words.
        String text="";
        wordIterator.setText(temp);
        int wordStart = wordIterator.first();
        for (int wordEnd = wordIterator.next();
             wordEnd != BreakIterator.DONE;
             wordStart = wordEnd, wordEnd = wordIterator.next()) {
            String value= Normalizer.normalize(temp.substring(wordStart,wordEnd), Normalizer.Form.NFD);
            value=value.replaceAll("[^\\p{ASCII}]", "");
            text+=" "+value;
        }
        temp=""+text;
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US); // for sentence breaking English grammar is used.
        iterator.setText(temp);
        int start = iterator.first();
        int index=0;

        FileOutputStream out=new FileOutputStream(new File(documentID+"_tokens.txt"));  // to write structured sentences into a temp file
        for (int end = iterator.next();
             end != BreakIterator.DONE;
             start = end, end = iterator.next()) {
            String finaltext=temp.substring(start, end).trim().replaceAll("   "," ");
            if(SentenceLength.noOfWords(finaltext)<3)
                continue;
            out.write(finaltext.getBytes());
            out.write("\n".getBytes());
            index++;
        }
            out.flush();

        out=new FileOutputStream(new File("C:\\ProgramData\\MySQL\\MySQL Server 5.7\\Data\\summarizer\\"+documentID+"_sentences.txt"));    // opens another file to write for tokenized sentence

        int noOfLines=index;    // get total no of lines for sentence length score

        double totalNoOfCuePhrases=new CuePhrase(documentID+"_tokens.txt").totalCuePhrases(); // get total no of cue phrases for cue phrase score

        scan=new Scanner(new File(documentID+"_tokens.txt"));

        index=0;
        while(scan.hasNext())
        {
            // this loop writes tokenized sentences into a file with its id and respective scores
            temp=scan.nextLine();
            temp=temp.replaceAll("[^\\p{ASCII}]", "");
            if(SentenceLength.noOfWords(temp)<2)
                continue;
            Sentence sentence=new Sentence(documentID,++index,temp, SentenceLength.noOfWords(temp), SentencePosition.sentencePositionScore(index,noOfLines), new CuePhrase(null).cuePhraseScore(temp,totalNoOfCuePhrases),0.0);
            temp=sentence.toString()+"\n";
            out.write(temp.getBytes());
          }
        new DBConnectivity().loadFile(documentID);

        //csvFile.close();
        out.close();
    }
   /* public boolean insertData(Sentence sentence)
    {
        boolean flag=false;
        try {


            Class.forName("com.mysql.jdbc.Driver");
            Connection connection=DriverManager.getConnection("jdbc:mysql://localhost:3307/summarizer","root","root" +
                    "");
            try (PreparedStatement statement = connection.prepareStatement("insert into sentences values(?,?,?,?,?,?,?)")) {
                statement.setInt(1,(int) sentence.ID);
                statement.setString(2, sentence.text);
                statement.setDouble(3, sentence.sentenceLengthScore);
                statement.setDouble(4, sentence.sentencePositionScore);
                statement.setDouble(5, sentence.cuePhraseScore);
                statement.setDouble(6, sentence.titleResemblanceScore);
                statement.setBoolean(7,false);
                int rows=statement.executeUpdate();
                if(rows>0)
                    flag=true;
                connection.close();
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return flag;
    }*/
}
