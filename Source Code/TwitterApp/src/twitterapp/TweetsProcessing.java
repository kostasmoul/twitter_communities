/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twitterapp;



import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.Iterator;
import twitter4j.JSONObject;
import twitter4j.JSONException;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.List;
import org.bson.conversions.Bson;

/**
 *
 * @author Κωστής
 */
public class TweetsProcessing {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws JSONException {
        
        //createSeparateEntities(); 
//create the collections with the separate entities that are extracted from the tweet collections
        
        
        //creating separate collections from the entities (hashtags collection, mentioned collection etc)
      String[] collections = {"separateEntities","separateEntities2","separateEntities3","separateEntities4"};
       
     for(int p=0;p<collections.length;p++){
       MongoClient mongo = new MongoClient("localhost", 27017);
       MongoDatabase database = mongo.getDatabase("myTweetdb");
       
       int counterHashtag=0;
       int counterMentioned=0;
       int counterUrl=0;
       int counterRetweeted=0;
       
       Bson filter = Filters.exists("hashtag");
       MongoCollection<Document> h = database.getCollection("hashtagAll");
       Iterator<Document> h2 = database.getCollection(collections[p]).find(filter).iterator();
       while((h2.hasNext())&&(counterHashtag<250))
       {
           Document doc = h2.next();
           h.insertOne(doc);
           counterHashtag++;
       }
       filter = Filters.exists("mentioned_users");
       MongoCollection<Document> m = database.getCollection("mentionedAll");
       h2 = database.getCollection(collections[p]).find(filter).iterator();
       while((h2.hasNext())&&(counterMentioned<250))
       {
           Document doc = h2.next();
           m.insertOne(doc);
           counterMentioned++;
       }
       filter = Filters.exists("url");
       MongoCollection<Document> u = database.getCollection("urlAll");
       h2 = database.getCollection(collections[p]).find(filter).iterator();
       while((h2.hasNext())&&(counterUrl<250))
       {
           Document doc = h2.next();
           u.insertOne(doc);
           counterUrl++;
       }
       filter = Filters.exists("retweeted_tweet");
       MongoCollection<Document> r = database.getCollection("retweetedAll");
       h2 = database.getCollection(collections[p]).find(filter).iterator();
       while((h2.hasNext())&&(counterRetweeted<250))
       {
           Document doc = h2.next();
           r.insertOne(doc);
           counterRetweeted++;
       }
    }
        
     
        
       
    }
    
    
    
    
    
    
    
    
    
    
    
    public static void createSeparateEntities() throws JSONException{
        
     String[] collectionsTweets = {"myTweetCol","myTweetCol2","myTweetCol3","myTweetCol4"};
     String[] colEntities = {"separateEntities","separateEntities2","separateEntities3","separateEntities4"};
     for(int col=0;col<collectionsTweets.length;col++){
               MongoClient mongo = new MongoClient("localhost", 27017);
       MongoDatabase database = mongo.getDatabase("myTweetdb");
        MongoCollection<Document> collection = database.getCollection(collectionsTweets[col]);
       Iterator<Document> kati = collection.find().iterator();
      
       
       while(kati.hasNext())
       {
           
           
       Document doc = kati.next();
      
       
       String user,url,hashtag,mentioned,id,timestamp;
       user=url=hashtag=mentioned=id=timestamp="";
       
       JSONObject a = new JSONObject(doc);
       String temp = a.getString("user");
       String tokens[] = temp.split(",");
       for(int j=0;j<tokens.length;j++)
             {
                 if(tokens[j].contains("screen_name"))
                 {
                    
                    temp = tokens[j].replace("\"screen_name\":","");
                    user = temp.replace("\"", "");
                    
                 }
                 
                 
             }
       timestamp = String.valueOf(a.getLong("timestamp_ms"));
       JSONObject b = a.getJSONObject("entities");
       tokens = b.toString().split(",");
       for(int j=0;j<tokens.length;j++)
       {
             if(tokens[j].contains("text"))
                 {
                    String temp2 = tokens[j].replace("\"","");
                    temp2 = temp2.replace(":","");
                    temp2 = temp2.replace("}","");
                    temp2 = temp2.replace("]","");
                    temp2 = temp2.replace("text","");
                    hashtag = hashtag.concat(temp2 + " ").trim();
                   
                 }
             if(tokens[j].contains("expanded_url"))
             {
                 String temp2 = tokens[j].replace("\":\"","");
                 temp2 = temp2.replace("\"","");
                 temp2 = temp2.replace("expanded_url", "");
                 url = url.concat(temp2 + " ");
             }
             if(tokens[j].contains("screen_name"))
             {
                 String temp2 = tokens[j].replace(":","");
                 temp2 = temp2.replace("\"","");
                 temp2 = temp2.replace("screen_name","");
                 mentioned = mentioned.concat(temp2 + " ");
             }
             
       }
       
      if(a.toString().contains("retweeted_status"))
      {
          b= (JSONObject) a.getJSONObject("retweeted_status");
          id = b.getString("id_str");
          
      }
      
      
      Document object = new Document("user",user)
                        .append("timestamp",timestamp)
                        .append("hashtag",hashtag);
      Document object1 = new Document("user",user)
                        .append("timestamp", timestamp)
                        .append("url",url);
      Document object2 = new Document("user",user)
                        .append("timestamp", timestamp)
                        .append("mentioned_users",mentioned);
      Document object3 = new Document("user",user)
                        .append("timestamp", timestamp)
                        .append("retweeted_tweet",id);
      
      MongoCollection<Document> collection2 = database.getCollection(colEntities[col]);
      
      
      collection2.insertOne(object);
      collection2.insertOne(object1);
      collection2.insertOne(object2);
      collection2.insertOne(object3);
       
       
           
          
           
       }
    }
        
      
    }
    
}
