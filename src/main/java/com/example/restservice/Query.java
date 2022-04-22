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

    IndexSearcher searcher;
    IndexReader reader;



    private static Indexing index = Indexing.getInstance();
    private static volatile Query instance = null;

    private Query(){

    }
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


    private void clearClust(){
        for(ArrayList<String> x:Clustering.clusters){
            x.clear();
        }
    }

    private void setup() throws IOException {
        clearClust();
        reader = DirectoryReader.open(index.index);	//reader to read the index
        searcher = new IndexSearcher(reader);
    }
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

    public int[] query(String querys) throws IOException, ParseException {
        setup();
        return search_rank(querys);
    }
}
