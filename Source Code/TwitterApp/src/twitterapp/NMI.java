/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twitterapp;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
/**
 *
 * @author Kostas
 */
public class NMI {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
         //file 1 
         String csvFile2 = "/Users/Kostas/Desktop/hashtag_table.csv";
         System.out.println(csvFile2);
        String line = "";
        ArrayList<Integer> one = new ArrayList<Integer>();
         int classNumber;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile2))) {
           line = br.readLine();
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String theClass = line;
                classNumber = Integer.parseInt(theClass);
                one.add(classNumber);

                

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
      
       //file 2
        String csvFile = "/Users/Kostas/Desktop/url_table.csv";
        String line2 = "";
        System.out.println(csvFile);
        ArrayList<Integer> two = new ArrayList<Integer>();
         int classNumber2;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
           line2 = br.readLine();
            while ((line2 = br.readLine()) != null) {

                // use comma as separator
                String theClass2 = line2;
                classNumber2 = Integer.parseInt(theClass2);
                two.add(classNumber2);

                

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
       
        
        int smallest=0;
        int largest=0;
        if(one.size()<two.size()){
            smallest=one.size();
            largest=two.size();
              int toCut = largest-smallest;
          two.subList(two.size() - toCut, two.size()).clear();
        }else{
            smallest=two.size();
            largest=one.size();
            int toCut = largest-smallest;
           one.subList(one.size() - toCut, one.size()).clear();
        }
        
        //System.out.println(one);
        //System.out.println(two);
       

  
     
           int totalObjects=one.size();
           
   
           
           
           
           Set<Integer> uniqueC = new HashSet<>(two);
           Set<Integer> uniqueR = new HashSet<>(one);
           
           
           HashMap<Integer,ArrayList<Integer>> antistoixies = new HashMap<>();
           for(int i=0; i<one.size();i++){
               int elementR = one.get(i);
               
               int elementC = two.get(i);
               if(antistoixies.containsKey(elementR)){
                   
                        antistoixies.get(elementR).add(elementC);
                   
                  
               }else{
                   ArrayList<Integer> list = new ArrayList<>();
                   list.add(elementC);
                   antistoixies.put(elementR,list);
               }
              
               
               
           }
           
            HashMap<Integer,Integer> frequenciesR = new HashMap<>();
            HashMap<Integer,Integer> frequenciesC = new HashMap<>();
            
            for (Integer one1 : uniqueR) {
                int occurrences = Collections.frequency(one, one1);
                frequenciesR.put(one1, occurrences);
               
             }
            
             for (Integer two1 : uniqueC) {
                int occurrences2 = Collections.frequency(two, two1);
               frequenciesC.put(two1, occurrences2);
             }
             
                   
             
            double I = 0.0;
            
            int freqx=0;
            int freqy=0;
            double probX;
            double probY;
            double jointProb;
            double logFraction;
            double log;
           for(Integer x : uniqueR){
               for(Integer y : uniqueC){
                   ArrayList<Integer> values = antistoixies.get(x);
                   
                   int count=0;
                   for(int j=0;j<values.size();j++){
                       if(values.get(j).equals(y)){
                           count++;
                       }
                   }
                 
                    freqx=frequenciesR.get(x);
                    freqy=frequenciesC.get(y);
                   
                 
                    
                 probX = (double)freqx/(double)totalObjects;
                 probY = (double)freqy/(double)totalObjects;
                 
                     jointProb=(double)count/(double)totalObjects;
                     logFraction=jointProb/(probX*probY);
                     log=Math.log(logFraction)/Math.log(2);
                  
                   if(count!=0){
                       I = I + (jointProb*log);
                       
                   }
                   
               }
           }
       System.out.println("MI : "+I);
          //entropies
       double entropyX=0.0;
        for(Integer x : uniqueR){
             freqx=frequenciesR.get(x);
             probX = (double)freqx/(double)totalObjects;
             entropyX= entropyX + (probX * (Math.log(probX)/Math.log(2)) ) ;
                   
        }
        double realEntropyX = - entropyX;
        System.out.println("entropyX : "+realEntropyX);
        
        double entropyY=0.0;
        for(Integer y : uniqueC){
             freqy=frequenciesC.get(y);
             probY = (double)freqy/(double)totalObjects;
             entropyY= entropyY + (probY * (Math.log(probY)/Math.log(2)) ) ;
                   
        }
        double realEntropyY = - entropyY;
        System.out.println("entropyY : "+realEntropyY);
        
        double normalized=(2*I)/(realEntropyX+realEntropyY);
        System.out.println("NMI : "+normalized);
        
    }
    
   
}
