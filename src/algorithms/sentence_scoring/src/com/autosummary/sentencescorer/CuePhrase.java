package com.autosummary.sentencescorer;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Rakhi on 06/01/2017.
 */

    /**
     * Created by HOME on 06/01/2017.
     */
    public class CuePhrase          /*this class is used to calculate the score of a sentence w.r.t the number of cue-phrases
                                        that appear in it.*/ {
        static String file;

        public CuePhrase(String file) {
            this.file = file;
        }

        public static double totalCuePhrases() throws IOException {
            int totalNumberOfCuePhrases = 0;
            RandomAccessFile f1 = new RandomAccessFile(file, "rw");
            try (RandomAccessFile f2 = new RandomAccessFile("cuephrases.txt", "r")) {

                String sentence = f1.readLine();

                while (sentence != null) {
                    String cuePhrase = f2.readLine();
                    while (cuePhrase != null) {
                        if (sentence.toLowerCase().contains(cuePhrase.toLowerCase())) {
                            totalNumberOfCuePhrases++;
                        }
                        cuePhrase = f2.readLine();
                    }
                    f2.seek(0);
                    sentence = f1.readLine();
                }
            }
            return totalNumberOfCuePhrases;
        }

        public static double cuePhraseScore(String sentence,double totalNoOfCuePhrases) throws IOException
        {

            return cuePhrasesInSentence(sentence)/totalNoOfCuePhrases;
        }
        public static double cuePhrasesInSentence(String sentence) throws IOException
        {
            int cuePhraseInSentence = 0;
            RandomAccessFile f1 = new RandomAccessFile("cuephrases.txt", "r");
                String cuePhrase = f1.readLine();
                while (cuePhrase != null) {
                    if (sentence.toLowerCase().contains(cuePhrase.toLowerCase())) {
                        cuePhraseInSentence++;
                    }
                    cuePhrase = f1.readLine();
                }
            return cuePhraseInSentence;
        }
    }
