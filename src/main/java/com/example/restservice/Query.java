package com.example.restservice;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;

import java.io.IOException;
import java.util.ArrayList;

public class Query {

    private IndexSearcher searcher;
    private IndexReader reader;



    private static Indexing index = Indexing.getInstance();
    private static volatile Query instance = null;

    private Query(){

    }

    /**
     * if no Objects -> creates an Object and returns it
     * if an object is already created -> returns that object
     * (singleton)
     * @return The only Object of the class
     */
    public static Query getInstance() {
        if (instance == null) {
            synchronized(Query.class) {
                if (instance == null) {
                    instance = new Query();
                }
            }
        }

        return instance;
    }

    /**
     * clears all the clusters from the results of the last search to prepare it for the new one
     */
    private void clearClust(){
        for(ArrayList<String> x:Clustering.getClusters()){
            x.clear();
        }
    }

    /**
     * prepares the system for the next query (cleares clusters, sets the Index path to read from)
     * @throws IOException
     */
    private void setup() throws IOException {
        clearClust();
        reader = DirectoryReader.open(index.getIndex());	//reader to read the index
        searcher = new IndexSearcher(reader);
    }

    /**
     * searches for the query and ranks the top 10 hits according to their sim scores using the bm25 similarity method
     * @param querys the Query
     * @return a list of the documents IDs ranked from 1st to last
     * @throws ParseException
     * @throws IOException
     */
    private int[] search_rank(String querys) throws ParseException, IOException {
        QueryParser parser = new QueryParser("Main", Indexing.analyzer());
        org.apache.lucene.search.Query query = parser.parse(querys);
        TopScoreDocCollector docCollcetor = TopScoreDocCollector.create(10);
        searcher.search(query, docCollcetor);

        ScoreDoc[] docs = docCollcetor.topDocs().scoreDocs;
        int[] docsid=new int[10];
        for (int j = 0; j < docs.length && j < 10; j++) {
            docsid[j]=docs[j].doc;
        }
        return docsid;
    }

    /**
     * preforms a Query and finds the Top 10 Hits (Documents) after it prepares the System for a new Query
     * @param querys the Query
     * @return a list of the documents IDs ranked from 1st to last
     * @throws IOException
     * @throws ParseException
     */
    public int[] query(String querys) throws IOException, ParseException {
        setup();
        return search_rank(querys);
    }

    public IndexSearcher getSearcher() {
        return searcher;
    }

    public IndexReader getReader() {
        return reader;
    }
}
