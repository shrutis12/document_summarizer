package com.autosummary.threshold;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
/**
 * Created by HOME on 29/04/2017.
 */
public class Threshold2 {
    public static void applyThreshold(int docid)
    {
        boolean flag=false;
        try {

            Class.forName("com.mysql.jdbc.Driver");
            Connection connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/summarizer","root","1234");
            PreparedStatement avgStatement=connection.prepareStatement("Select avg(cue_phrases) from sentences where docid=?");

            PreparedStatement update=connection.prepareStatement("update sentences set selected=true where docid=? and  id=?");

            avgStatement.setInt(1,docid);
            ResultSet avg=avgStatement.executeQuery();
            double avgCuePhrases=0;
            while (avg.next())
                avgCuePhrases=avg.getDouble(1);

            PreparedStatement  statement=connection.prepareStatement("Select * from sentences where docid=?");
            statement.setInt(1,docid);
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
            {
                int id=resultSet.getInt(2);
                double sentenceLength=resultSet.getDouble(4)/10;
                double sentencePositon=resultSet.getDouble(5);
                double cuePhrases=resultSet.getDouble(6);
//                double titleResemblance=resultSet.getDouble(6);

                if(((sentenceLength)>=9 && (sentenceLength)<=25))
                {
                    // System.out.println("Sentence length");
                    flag=true;
                }
                else if(sentencePositon<0.2 || sentencePositon>0.8) {
                    // System.out.println("Sentence Postion");
                    flag = true;
                }

                else if(cuePhrases >= (avgCuePhrases))
                {
                    //  System.out.println("Cue phrases");
                    flag=true;
                }


                else flag=false;

                if(flag==true)
                {
                    update.setInt(1,docid);
                    update.setInt(2,id);
                    update.executeUpdate();
                }

            }
            connection.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }
}
