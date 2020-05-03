package com.autosummary.databaseconnections;
import com.autosummary.preprocessor.*;

import java.io.File;
import java.sql.*;

/**
 * Created by HOME on 22/03/2017.
 */
public class DBConnectivity {
    static Connection connection;
    public DBConnectivity() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/summarizer", "root", "1234");
    }
    public boolean insertSentenceScores(Sentence sentence) throws Exception
    {
        boolean flag=false;
        try (PreparedStatement statement = connection.prepareStatement("insert into sentences values(?,?,?,?,?,?,?,?)")) {
            statement.setInt(1,(int) sentence.DOCID);
            statement.setInt(2,(int) sentence.ID);
            statement.setString(3, sentence.text);
            statement.setDouble(4, sentence.sentenceLengthScore);
            statement.setDouble(5, sentence.sentencePositionScore);
            statement.setDouble(6, sentence.cuePhraseScore);
            statement.setDouble(7, sentence.titleResemblanceScore);
            statement.setBoolean(8,false);
            int rows=statement.executeUpdate();
            if(rows>0)
                flag=true;
         }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
         //   connection.close();
        }
        return flag;
    }

    public void flushPreviousData() throws Exception
    {
        // truncate tables
        createTables();
        PreparedStatement statement = connection.prepareStatement("truncate table sentences;");
        statement.executeUpdate();
        statement = connection.prepareStatement("truncate table modified_sentences;");
        statement.executeUpdate();
        statement = connection.prepareStatement("truncate table cosine_pairs;");
        statement.executeUpdate();
        connection.close();

        // delete summary file
        if(new File("G:\\Final_project\\BEfront\\summary.txt").exists())
            new File("G:\\Final_project\\BEfront\\summary.txt").delete();

    }

    public void createTables() throws Exception
    {
        Statement statement=connection.createStatement();
        statement.execute("create table if not exists sentences (\n" +
                "    DOCID int,ID int,\n" +
                "     sentence text,\n" +
                "     sentence_length float(20,10),\n" +
                "     sentence_position float(20,10),\n" +
                "     cue_phrases float(20,10),\n" +
                "     title_resemblance float(20,10),\n" +
                "\t selected tinyint,\n" +
                "     primary key(DOCID,ID));\n");

        statement.execute("create table if not exists modified_sentences (\n" +
                "DOCID int,\n" +
                " ID int,\n" +
                " new_id int auto_increment primary key,\n" +
                "    sentence text\n" +
                "    );\n");

        statement.execute("create table if not exists cosine_pairs (\n" +
                "    ID1 bigint,\n" +
                "    ID2 bigint,\n" +
                "    value float(20,10)\n" +
                "     );\n");
    }

    public void loadFile(int docid) throws Exception
    {
        Statement statement=connection.createStatement();
        statement.executeUpdate("LOAD DATA INFILE '"+docid+"_sentences.txt' INTO TABLE sentences FIELDS TERMINATED BY '#'");
    }
}
