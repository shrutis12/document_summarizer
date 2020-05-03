package com.autosummary.mds;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;


/**
 * Created by Rakhi on 2/15/2017.
 */
public class MinimalDominantSet {//int adj[][]->graph(2d matrix) which is accepted as input from GRAPH CONSTRUCT module
//int degree[][]->stores sentence id in degree[i][1] and its degree in degree[i][2]
//int reached[]->stores ids of sentences which are touched either directly or indirectly
//int selected[]->stores ids of sentences which are actually selected

    static int index=0;
    static int n=100;
    static int degree[][]=new int[n][2];
    static int selected[]=new int[n];
    static int reached[]=new int[n];
    static int adj[][]=new int[n][n];
    static ResultSet pairs;
    static TreeMap<Integer,HashSet<Integer>> adjacency=new TreeMap<Integer, HashSet<Integer>>();
    static int[][]sortedList;
    //static int selected[];

    public static void orderTable() throws Exception
    {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection= DriverManager.getConnection("jdbc:mysql://localhost:3306/summarizer","root","1234");
        //consider docid also
        PreparedStatement statement=connection.prepareStatement("select * from cosine_pairs order by id1");
        pairs=statement.executeQuery();

       // connection.close();
    }

    public static void generatePairMap() throws Exception
    {
        int tempId=0;
        HashSet<Integer> tempSet=new HashSet<Integer>();
        while(pairs.next())
        {
            int id1=pairs.getInt(1);
            if(tempId==0)
                tempId=id1;
            else if(tempId==id1)
            {

               // System.out.println("Same "+tempId+" "+id1);
                tempSet.add(new Integer(pairs.getInt(2)));
                //System.out.println(tempSet);
            }
            else
            {
                adjacency.put(new Integer(tempId),tempSet);
               // System.out.println("Map entry "+tempId+" : " +tempSet);
                //System.out.println("Changed "+tempId+" "+id1);
                tempId=id1;

                tempSet=new HashSet<Integer>();
                tempSet.add(new Integer(pairs.getInt(2)));
               // System.out.println("new tempSet :"+tempSet);

            }
        }// while of resultSet
        //System.out.println(adjacency);
        sortedList=new int[adjacency.size()][2];
        int i=0;
        for (Map.Entry<Integer,HashSet<Integer>> temp:adjacency.entrySet()
             ) {
            sortedList[i][0]=temp.getKey();
            sortedList[i][1]=temp.getValue().size();
            i++;
        }

    }

    public static void genMDS()
    {
         selected=new int[sortedList.length];
        HashSet<Integer> reached=new HashSet<>();
        selected[0]=sortedList[0][0];
        int count=1;
            reached.addAll(adjacency.get(sortedList[0][0]));
            //System.out.println("1st set:"+reached);

       for( Map.Entry<Integer,HashSet<Integer>> tempSet:adjacency.entrySet())
       {
           int oldsize=reached.size();
           reached.addAll(tempSet.getValue());
           if(oldsize<reached.size())
                selected[count++]=tempSet.getKey();


        }
        //System.out.println("reached length:"+reached.size()+" "+selected.length);
    }

    public static void printSummary(int selected[]) throws Exception
    {
        Arrays.sort(selected);
        FileOutputStream out =new FileOutputStream("E:\\BE_proj\\April_END\\BEfront\\Summary.txt");
        //out.write("<HTML><BODY bgcolor=black><center><h1 style=\"color:white\">Summary</h1><div style=\"outline:#000 thin solid; display:table-cell; height:500px; width:750px; background-color:white;color:black ;font-family:Helvetica; font-size:17px; padding : 20px 20px 20px 20px; text-align:justify;\">".getBytes());
        System.out.println("SUMMARY!!!!");
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/summarizer", "root", "1234");
        PreparedStatement statement = connection.prepareStatement("select sentence,sentence_position from sentences where docid=(select docid from modified_sentences where new_id=? ) and id=(select id from modified_sentences where new_id=?) order by sentence_position desc  ;");
        for (int i = 0; i <selected.length ; i++) {
            statement.setInt(1,selected[i]);
            statement.setInt(2,selected[i]);
            ResultSet sentence=statement.executeQuery();
            while (sentence.next()) {
                //System.out.println(sentence.getString(1));
                out.write(sentence.getString(1).getBytes());
            }//end while
        }//end for
       // out.write(new String("Threshold : "+ CosineSimilarity2.threshold).getBytes());

       // out.write("</div><br><a href=\"Summary.html\" download><input type=button value=download style=\" height:50px; width:200px; background:#00b7ea; color:white; font: 20px Helvetica, Verdana, sans-serif; text-align:center; text-transform:uppercase; \"/></a></center></BODY></HTML>".getBytes());
        out.close();
    }

    public static void generateMinimalDominantSet() throws Exception
    {
        orderTable();
        generatePairMap();
        new MergeSort().main(sortedList,sortedList.length);
        genMDS();
        printSummary(selected);


    }
}
