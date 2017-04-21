/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twitterapp;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.Iterator;
import twitter4j.JSONObject;
import twitter4j.JSONException;
import java.util.HashMap;
import java.util.Map;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import com.mongodb.client.MongoCursor;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;
import org.bson.BSONObject;
import com.mongodb.client.model.Filters;
import java.util.Arrays;
import java.util.Map.Entry;
import org.bson.conversions.Bson;

/**
 *
 * @author Κωστής
 */


public class TweetsSimilarity {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws JSONException,Exception{
        System.out.println(intersection("unpresidented","unpresidented","unpresidented"));
        System.out.println(union("unpresidented","unpresidented","unpresidented"));
        MongoClient mongo = new MongoClient("localhost", 27017);
       MongoDatabase database = mongo.getDatabase("myTweetdb");
      
       Document temp;
       JSONObject b,c;
       Double sim[] = new Double[100];
      // String one = a.getString("hashtag");
       String two = "";
       int j=0;
       int k=0;
       int y=0;
       String[] collections = {"hashtagAll","mentionedAll","urlAll","retweetedAll"};
       String[] entities = {"hashtag","mentioned_users","url","retweeted_tweet"};
       Double[][] hashtag = new Double[1000][1000];
       for(j=0;j<hashtag.length;j++)
       {
           Arrays.fill(hashtag[j],0.0);
       }
       j=0;
       for(int p=0;p<collections.length;p++)
       {
           
       String file = collections[p].concat(".csv");
       System.out.println(file);
       PrintWriter writer = new PrintWriter(file);
       writer.println("Source,Target,Weight,Type");
       MongoCursor<Document> manasou;
       MongoCollection collection2 = database.getCollection(collections[p]);
       manasou = collection2.find().iterator();
       HashMap<String,Integer> count = new HashMap<>();
       String common="";
       
       while(manasou.hasNext() && y<1000)
       {
           b = new JSONObject(manasou.next());
           String temp1 = b.getString(entities[p]);
           String tokens[] = temp1.split(" ");
           for(int i=0;i<tokens.length;i++)
           {
               if(count.containsKey(tokens[i]))
               {
                   Integer temp2 = count.get(tokens[i]);
                   temp2++;
                   count.replace(tokens[i],temp2);
               }
               else
               {
                   count.put(tokens[i],1);
               }
           }
           y++; 
       }
       Iterator<Entry<String,Integer>> count_it = count.entrySet().iterator();
       while(count_it.hasNext())
       {
           Entry entry = count_it.next();
           if((Integer)entry.getValue()>500)
           {
               common = common.concat(entry.getKey() + " ");
           }
       }
       System.out.println(common);
       manasou = collection2.find().iterator();
       j=0;
       while(manasou.hasNext() && j<1000)
       {
           b = new JSONObject(manasou.next());
           System.out.println(j);
           MongoCursor<Document> kati2 = collection2.find().iterator();
           k=0;
           while(kati2.hasNext() && k<1000)
           {
               c = new JSONObject(kati2.next());
               if(j<k)
               {
                  String temp1 = b.getString(entities[p]);
                  String temp2 = c.getString(entities[p]);
                  double temp3 = intersection(temp1,temp2,common)/union(temp1,temp2,common);
                  if(Double.isNaN(temp3))
                  {
                   temp3 = 1;
                  }
                  String lel = "tweet" + j +"," + "tweet" + k + "," +temp3 + ",Undirected" ;
                  
                  
                  hashtag[j][k]= hashtag[j][k] + temp3;
                  
                  writer.flush();
                  if(temp3>0.2)
                  {
                    writer.println(lel);
                  }
                  
               }
               else
               {
                   k++;
                   continue;
               }
               k++;
           }
           
           j++;
            
       }
       
       }
       PrintWriter writer = new PrintWriter("all.csv");
       writer.println("Source,Target,Weight,Type");
       for(j=0;j<hashtag.length;j++)
       {
           for(k=0;k<hashtag.length;k++)
           {
               if(j<k)
               {
                   double temp1 = hashtag[j][k]/4.0;
                   if(temp1>0.2)
                   {
                      String lel = "tweet" + j +"," + "tweet" + k + "," +temp1 + ",Undirected" ; 
                      writer.flush();
                      writer.println(lel);
                   }
               }
           }
       }
      
       
       
       
    }
    
    public static double intersection(String a, String b,String common)
    {
        String tokens[] = a.split(" ");
        String intersection = "";
        for(int i=0;i<tokens.length;i++)
        {
            if(b.contains(tokens[i]))
            {
                intersection = intersection.concat(tokens[i] + " ");
            }
        }
        
        tokens = b.split(" ");
        for(int i=0;i<tokens.length;i++)
        {
            if(a.contains(tokens[i]) && !intersection.contains(tokens[i]))
            {
                intersection = intersection.concat(tokens[i] + " ");
            }
        }
        if(common.compareTo("")!=0)
        {
        String tokens2[] = common.split(" ");
        for(int i=0;i<tokens2.length;i++)
        {
            if(intersection.contains(tokens2[i]))
            {
             intersection = intersection.replace(tokens2[i],"");
            }
        }
        }
        intersection = intersection.trim();
        tokens = intersection.split("\\s+");
        if(intersection.compareTo("")==0)
        {
            return 0.0;
        }
        return (double) tokens.length;
    }
    
    public static double union(String a, String b, String common)
    {
        String union = a + " ";
        String tokens[] = b.split(" ");
        for(int i=0;i<tokens.length;i++)
        {
            if(!union.contains(tokens[i]))
            {
                union = union.concat(tokens[i] + " ");
            }
        }
        if(common.compareTo("")!=0)
        {
        String tokens2[] = common.split(" ");
        
        for(int i=0;i<tokens2.length;i++)
        {
            
            if(union.contains(tokens2[i]))
            {
             union = union.replace(tokens2[i],"");
            }
            
        }
        }
        union=union.trim();
        tokens = union.trim().split("\\s+");
       
       if(union.compareTo("")==0)
       {
           
           return 0.0;
       }
       
       return (double) tokens.length;
    }
    
}
