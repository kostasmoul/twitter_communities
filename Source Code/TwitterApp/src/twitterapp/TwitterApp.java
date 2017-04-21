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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.FilterQuery;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.TwitterObjectFactory;

/**
 *
 * @author Kostas
 */
public class TwitterApp {

    /**
     * @param args the command line arguments
     * @throws twitter4j.TwitterException
     */
    public static void main(String[] args) throws TwitterException {
       
        streamTweets();
      

    }//end main
    
    
    
    
    
    
    
    
    
    public static void streamTweets() throws TwitterException{
              /*getting the trends */
        ConfigurationBuilder cb2 = new ConfigurationBuilder();

        cb2.setDebugEnabled(true)
                .setOAuthConsumerKey("S01GsVwuCAwZFp5BLg5C4k8PT")
                .setOAuthConsumerSecret("6jo0jo4b05Ec5ZJcf74v5yGUQu5v8DryUwypOBjPD6jaItRNzd")
                .setOAuthAccessToken("794259549297446912-Z3AWruBmLa7QmCO6BnybCSj1tZXNqbB")
                .setOAuthAccessTokenSecret("6ezMQPQVziW9yxyTITZA8Wc2RJWjcBKvbXZU4dOjo4bge");
        
        
        
        TwitterFactory tf = new TwitterFactory(cb2.build());
        Twitter twitter = tf.getInstance();
        Trends trends = twitter.getPlaceTrends(23424977); 
        
        String top_trend="";
        int top=0;
        for(Trend trend:trends.getTrends()){
            if(top<1){
                 top_trend = trend.getName();
                top++;
            }
        }
        
        System.out.println("top trend : "+top_trend);  
        

        //Using the Streaming API to get real time tweets based on the trending topics as keywords
         /* configurating twiter4j */

        ConfigurationBuilder cb = new ConfigurationBuilder();

        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("S01GsVwuCAwZFp5BLg5C4k8PT")
                .setOAuthConsumerSecret("6jo0jo4b05Ec5ZJcf74v5yGUQu5v8DryUwypOBjPD6jaItRNzd")
                .setOAuthAccessToken("794259549297446912-Z3AWruBmLa7QmCO6BnybCSj1tZXNqbB")
                .setOAuthAccessTokenSecret("6ezMQPQVziW9yxyTITZA8Wc2RJWjcBKvbXZU4dOjo4bge")
                .setJSONStoreEnabled(true);
        /* end of configuration */
        
        
        
        
        MongoClient mongo = new MongoClient("localhost", 27017);
        MongoDatabase database = mongo.getDatabase("myTweetdb2");
        MongoCollection<Document> collection = database.getCollection("myTweetCol5");
        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        StatusListener listener;
        listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                     
                String rawJSON = TwitterObjectFactory.getRawJSON(status);
                Document doc = Document.parse(rawJSON);
                
                collection.insertOne(doc);
                
               
                
                

            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };

               // twitterStream.sample();
        twitterStream.addListener(listener);
        FilterQuery fq = new FilterQuery();

        String keywords[] = {top_trend};
        fq.track(keywords);
        twitterStream.filter(fq);
    }

}
