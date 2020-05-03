package com.autosummary.mds;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import com.autosummary.graphconstructer.CosineSimilarity;
import com.autosummary.graphconstructer.CosineSimilarity2;
import com.autosummary.graphconstructer.CosineSimilarity2.*;

/**
 * Created by HOME on 25/03/2017.
 */
public class MinimalDominantSet2 {
    static ResultSet pairs;
    static int count;
   static int selected[];
   static TreeSet<Integer> reached;
    static TreeMap<Integer,HashSet<Integer>> adjacency=new TreeMap<Integer, HashSet<Integer>>();
    static int[][] cosine_pairs;
    static int[][] degree;


    public static void orderTable() throws Exception
    {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection= DriverManager.getConnection("jdbc:mysql://localhost:3306/summarizer","root","1234");
        //consider docid also
        PreparedStatement statement=connection.prepareStatement("select * from cosine_pairs order by id1");
        PreparedStatement statement1=connection.prepareStatement("select count(*) from cosine_pairs order by id1");
        pairs=statement.executeQuery();
        ResultSet res=statement1.executeQuery();
        while(res.next())
            count=res.getInt(1);
        // connection.close();
    }

    public static void generatePairMap() throws Exception {
        int i = 0, index = 0;
        TreeSet<Integer> set1=new TreeSet<>();
        cosine_pairs = new int[count][2];
        int[] nodes = new int[count * 2];
       degree = new int[count * 2][2];
        System.out.println("cosine pairs before sorting");
        while (pairs.next()) {
            cosine_pairs[i][0] = pairs.getInt(1);
            cosine_pairs[i][1] = pairs.getInt(2);

            System.out.println(cosine_pairs[i][0]+" "+cosine_pairs[i][1]);
            if (Arrays.binarySearch(nodes, cosine_pairs[i][0]) != -1) {
                nodes[index] = cosine_pairs[i][0];
                index++;
            }
            if (Arrays.binarySearch(nodes, cosine_pairs[i][1]) != -1) {
                nodes[index] = cosine_pairs[i][1];
                index++;
            }
            i++;
           // Arrays.sort(nodes);
        }
        Arrays.sort(nodes);
        System.out.println(Arrays.toString(nodes));
        int index1=0;
        index=0;
        degree[0][0]=nodes[0];
        degree[0][1]=1;
        for (int k = 1; k <nodes.length ; k++) {
            if(nodes[k]==nodes[k-1])
                degree[index1][1]++;
            else
            {
                index1++;
                degree[index1][0]=nodes[k];
                degree[index1][1]++;
            }
        }

        for (int j = 0; j <=index1; j++) {
            System.out.println(degree[j][0] +" "+degree[j][1]);
        }
       new MergeSort().main(degree,degree.length);
        System.out.println("after sorting");
        for (int j = 0; j <=index1; j++) {
            System.out.println(degree[j][0] +" "+degree[j][1]);
        }

    }

    public static void calculatingAdjacency(){
        int tempId=0;

        for (int i = 0; i<count ; i++) {
            adjacency.put(degree[i][0],null);

        }//end of for loop for creating null set
        System.out.println("adj:"+adjacency);
        for (int i = 0; i<count ; i++) {
            int id1=cosine_pairs[i][0];
            int id2=cosine_pairs[i][1];
            HashSet<Integer> tempSet=new HashSet<Integer>();
            try {
                tempSet.addAll(adjacency.get(id1));
            }
            catch (Exception e){}
            tempSet.add(id2);
            adjacency.put(id1,tempSet);


            tempSet=new HashSet<Integer>();
            try {
                tempSet.addAll(adjacency.get(id2));
            }
            catch (Exception e){}
            tempSet.add(id1);
            adjacency.put(id2,tempSet);
        }
        System.out.println("adj after:"+adjacency);

    }
        public static void generateMDS(){
            selected=new int[degree.length];
            HashSet<Integer> reached=new HashSet<>();
            selected[0]=degree[0][0];
            int count=1;
            reached.addAll(adjacency.get(degree[0][0]));
            //System.out.println("1st set:"+reached);

            for (int i = 1; i < count; i++) {
                int oldsize=reached.size();
                reached.addAll(adjacency.get(degree[i][0]));
                if(oldsize<reached.size())
                    selected[count++]=degree[i][0];

            }



            }


    public static void generateMinimalDominantSet() throws Exception
    {
        orderTable();
        generatePairMap();
        calculatingAdjacency();
        generateMDS();

        //new MergeSort().main(degree,degree.length);
        //genMDS();

        printSummary(selected);


    }
    public static void printSummary(int selected[]) throws Exception
    {
       // Arrays.sort(selected);
        FileOutputStream out =new FileOutputStream("E:\\BEfront\\summary.txt");
       // out.write("<html><body style=\"font-size:20px;font-family:Helvetica;\">".getBytes());
        System.out.println("SUMMARY!!!!:");
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/summarizer", "root", "1234");
        PreparedStatement statement = connection.prepareStatement("Select sentence from sentences where id=?");
        for (int i = 0; i <selected.length ; i++) {
            statement.setInt(1,selected[i]);
            ResultSet sentence=statement.executeQuery();
            while (sentence.next()) {
                //System.out.println(sentence.getString(1));
                out.write(sentence.getString(1).getBytes());
            }//end while
        }//end for
       // out.write(new String("Threshold : "+CosineSimilarity2.threshold).getBytes());
        //out.write("</body></html>".getBytes());
        out.close();
    }


}
