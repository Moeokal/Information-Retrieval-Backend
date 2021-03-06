package com.example.restservice;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import java.io.*;
import java.util.*;

public class Clustering {


    private static ArrayList<ArrayList<String>> clusters = new ArrayList<>();


    private static int[] docsid;
    private static HashMap<String, Integer> clust;


    private static Indexing index = Indexing.getInstance();
    private static Query query = Query.getInstance();


    private static Set<String> terms = new HashSet<>();

    /**
     * cleares and makes the clusters ready for the next search
     */
    private static void setup(){
        if(!(clusters.isEmpty())){
            clusters.clear();
        }
        clusters.add(new ArrayList<>());
        clusters.add(new ArrayList<>());
        clusters.add(new ArrayList<>());
        clusters.add(new ArrayList<>());
        clusters.add(new ArrayList<>());
    }

    /**
     * calculates the term Frequencies of the document
     * @param docId ID of the document
     * @return a realvector that saves the statistics of the document
     */
    static RealVector getTermFrequencies(int docId)
            throws IOException {
        Terms vector = query.getReader().getTermVector(docId, "Main");
        double n=query.getReader().getDocCount("Main");
        TermsEnum termsEnum;
        termsEnum = vector.iterator();
        Map<String, Integer> frequencies = new HashMap<>();
        RealVector rvector = new ArrayRealVector(terms.size());
        BytesRef text;
        ArrayList<Term> v= new ArrayList<>();
        ArrayList<Long> g= new ArrayList<>();
        while ((text = termsEnum.next()) != null) {
            String term = text.utf8ToString();
            int freq = (int) termsEnum.totalTermFreq();
            Term termInstance = new Term("Main", term);
            frequencies.put(term, freq);
            v.add(termInstance);
            g.add(termsEnum.totalTermFreq());
        }
        int i = 0;
        double idf;
        double tf;
        double tfidf;
        for (String term1 : terms) {
            if(frequencies.containsKey(term1)) {
                Term termm = new Term("Main", term1);
                int index=v.indexOf(termm);
                Term termInstance=v.get(index);
                tf=g.get(index);
                double docCount = query.getReader().docFreq(termInstance);
                double z=n/docCount;
                idf=Math.log10(z);
                tfidf=tf*idf;
            } else {
                tfidf=0.0;
            }
            rvector.setEntry(i++, tfidf);
        }
        return rvector;
    }


    /**
     * adds all the terms of the document to the list of all terms
     * @param docId the document to check
     */
    static void addTerms(int docId) throws IOException {
        Terms vector = query.getReader().getTermVector(docId, "Main");
        TermsEnum termsEnum;
        termsEnum = vector.iterator();
        BytesRef text;
        while ((text = termsEnum.next()) != null) {
            String term = text.utf8ToString();
            terms.add(term);
        }
    }

    /**
     * Creates a csv file of all the documents and their term frequnecies for each term from the list
     * of all terms to represent the docs as vectors in a n-dimensional room with n=number of terms
     */
    public static void createCSV() throws IOException {

        ArrayList<String[]> sss= new ArrayList<>(10);
        for(int i=0;i<10;i++){
            addTerms(docsid[i]);
        }
        for(int i=0;i<10;i++){
            RealVector v = getTermFrequencies(docsid[i]);
            double[] arr = v.toArray();
            String s = Arrays.toString(arr);
            s=s.substring(1, s.length()-1);
            String[] ss = s.split(", ");
            sss.add(ss);
        }



        File file2 = new File(index.getPathWrite()+"\\test.csv");
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file2);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer2 = new CSVWriter(outputfile);

            // adding header to csv
            String[] header = new String[sss.get(0).length];
            int a=0;
            for(int i=0;i<header.length;i++) {
                a=a+1;
                header[i]=String.valueOf(a);
            }
            writer2.writeNext(header);

           for(int i=0;i<10;i++){
                writer2.writeNext(sss.get(i));
            }

            // closing writer connection
            writer2.close();
        }
        catch (IOException e) {

            e.printStackTrace();
        }

        CSVLoader loader = new CSVLoader();
        loader.setSource(new File(index.getPathWrite()+"\\test.csv"));
        Instances data = loader.getDataSet();

        // save ARFF
        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        saver.setFile(new File(index.getPathWrite()+"\\tests.arff"));
        saver.writeBatch();
    }


    /**
     * uses kmeans clustering to assign each document to 1 cluster according to the euk distance
     * @param x number of clusters
     */
    public static void cluster(int x) throws Exception {
        createCSV();
        BufferedReader breader;
        breader = new BufferedReader(new FileReader(
                index.getPathWrite()+"\\tests.arff"));
        Instances Train = new Instances(breader);
        SimpleKMeans kMeans = new SimpleKMeans();
        kMeans.setSeed(10);
        kMeans.setPreserveInstancesOrder(true);
        kMeans.setNumClusters(x);
        kMeans.buildClusterer(Train);
        int[] assignments = kMeans.getAssignments();
        int i2 = 0;
        clust = new HashMap<>();
        for (int clusterNum : assignments) {
            Document docu = query.getSearcher().doc(docsid[i2]);
            clust.put(docu.get("Topic"), clusterNum);
            i2++;
        }
        breader.close();

        setup();

        for (String name: clust.keySet()) {
            int value = clust.get(name);
            clusters.get(value).add(name);
        }

    }

    public static ArrayList<ArrayList<String>> getClusters() {
        return clusters;
    }

    public static int[] getDocsid() {
        return docsid;
    }

    public static void setDocsid(int[] docsid) {
        Clustering.docsid = docsid;
    }
}
