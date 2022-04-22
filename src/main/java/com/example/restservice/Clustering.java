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


    static ArrayList<ArrayList<String>> clusters = new ArrayList<>();


    static int[] docsid;
    static HashMap<String, Integer> clust;


    private static Indexing index = Indexing.getInstance();
    private static Query query = Query.getInstance();


    private static Set<String> terms = new HashSet<>();
    /*
    private static RealVector v1=null;
    private static RealVector v2=null;
    private static RealVector v3=null;
    private static RealVector v4=null;
    private static RealVector v5=null;
    private static RealVector v6=null;
    private static RealVector v7=null;
    private static RealVector v8=null;
    private static RealVector v9=null;
    private static RealVector v10=null;
    */


    private static void setup(){
        clusters.add(new ArrayList<>());
        clusters.add(new ArrayList<>());
        clusters.add(new ArrayList<>());
        clusters.add(new ArrayList<>());
        clusters.add(new ArrayList<>());
    }

    static RealVector getTermFrequencies(int docId)
            throws IOException {
        Terms vector = query.reader.getTermVector(docId, "Main");
        double n=query.reader.getDocCount("Main");
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
                double docCount = query.reader.docFreq(termInstance);
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


    static void addTerms(int docId) throws IOException {
        Terms vector = query.reader.getTermVector(docId, "Main");
        TermsEnum termsEnum;
        termsEnum = vector.iterator();
        BytesRef text;
        while ((text = termsEnum.next()) != null) {
            String term = text.utf8ToString();
            terms.add(term);
        }
    }


    public static void createCSV() throws IOException {

        ArrayList<String[]> sss= new ArrayList<>(10);
        for(int i=0;i<10;i++){
            addTerms(docsid[i]);
            RealVector v = getTermFrequencies(docsid[i]);
            double[] arr = v.toArray();
            String s = Arrays.toString(arr);
            s=s.substring(1, s.length()-1);
            String[] ss = s.split(", ");
            sss.add(ss);
        }

        /*
        addTerms(docsid[0]);
        v1 = getTermFrequencies(docsid[0]);
        double[] arr1 = v1.toArray();
        String s1 = Arrays.toString(arr1);
        s1=s1.substring(1, s1.length()-1);
        String[] ss1 = s1.split(", ");
        */
        /*
        addTerms(docsid[1]);
        addTerms(docsid[2]);
        addTerms(docsid[3]);
        addTerms(docsid[4]);
        addTerms(docsid[5]);
        addTerms(docsid[6]);
        addTerms(docsid[7]);
        addTerms(docsid[8]);
        addTerms(docsid[9]);

        v2 = getTermFrequencies(docsid[1]);
        v3 = getTermFrequencies(docsid[2]);
        v4 = getTermFrequencies(docsid[3]);
        v5 = getTermFrequencies(docsid[4]);
        v6 = getTermFrequencies(docsid[5]);
        v7 = getTermFrequencies(docsid[6]);
        v8 = getTermFrequencies(docsid[7]);
        v9 = getTermFrequencies(docsid[8]);
        v10 = getTermFrequencies(docsid[9]);


        double[] arr2 = v2.toArray();
        double[] arr3 = v3.toArray();
        double[] arr4 = v4.toArray();
        double[] arr5 = v5.toArray();
        double[] arr6 = v6.toArray();
        double[] arr7 = v7.toArray();
        double[] arr8 = v8.toArray();
        double[] arr9 = v9.toArray();
        double[] arr10 = v10.toArray();



        String s2 = Arrays.toString(arr2);
        s2=s2.substring(1, s2.length()-1);
        String[] ss2 = s2.split(", ");

        String s3 = Arrays.toString(arr3);
        s3=s3.substring(1, s3.length()-1);
        String[] ss3 = s3.split(", ");

        String s4 = Arrays.toString(arr4);
        s4=s4.substring(1, s4.length()-1);
        String[] ss4 = s4.split(", ");

        String s5 = Arrays.toString(arr5);
        s5=s5.substring(1, s5.length()-1);
        String[] ss5 = s5.split(", ");

        String s6 = Arrays.toString(arr6);
        s6=s6.substring(1, s6.length()-1);
        String[] ss6 = s6.split(", ");

        String s7 = Arrays.toString(arr7);
        s7=s7.substring(1, s7.length()-1);
        String[] ss7 = s7.split(", ");

        String s8 = Arrays.toString(arr8);
        s8=s8.substring(1, s8.length()-1);
        String[] ss8 = s8.split(", ");

        String s9 = Arrays.toString(arr9);
        s9=s9.substring(1, s9.length()-1);
        String[] ss9 = s9.split(", ");

        String s10 = Arrays.toString(arr10);
        s10=s10.substring(1, s10.length()-1);
        String[] ss10 = s10.split(", ");
*/

        File file2 = new File(index.pathWrite+"\\test.csv");
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
            /*
            writer2.writeNext(ss1);
            writer2.writeNext(ss2);
            writer2.writeNext(ss3);
            writer2.writeNext(ss4);
            writer2.writeNext(ss5);
            writer2.writeNext(ss6);
            writer2.writeNext(ss7);
            writer2.writeNext(ss8);
            writer2.writeNext(ss9);
            writer2.writeNext(ss10);
*/

            // closing writer connection
            writer2.close();
        }
        catch (IOException e) {

            e.printStackTrace();
        }

        CSVLoader loader = new CSVLoader();
        loader.setSource(new File(index.pathWrite+"\\test.csv"));
        Instances data = loader.getDataSet();

        // save ARFF
        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        saver.setFile(new File(index.pathWrite+"\\tests.arff"));
        saver.writeBatch();
    }



    public static void cluster(int x) throws Exception {
        createCSV();
        BufferedReader breader;
        breader = new BufferedReader(new FileReader(
                index.pathWrite+"\\tests.arff"));
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
            Document docu = query.searcher.doc(docsid[i2]);
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
}
