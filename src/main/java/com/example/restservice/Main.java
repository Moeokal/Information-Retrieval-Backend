package com.example.restservice;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

    private static Indexing index = Indexing.getInstance();
    private static Query query = Query.getInstance();

    /**
     * "if skip or continue was clicked" set the directories, clear them, create index in the write dir and add
     * the docs from the read dir to it
     * @param s1 write path
     * @param s2 read path
     */
    public static void button1continue(String s1, String s2) throws IOException {
        index.setDirectory(s1, s2);
        index.clearDirectory();
        index.CreateWriter();
        index.addFilesToIndex();
    }

    // IMPORTANT PAGE

    /**
     * searches for a query and clusters the results
     * @param querys the query
     * @param x number of clusters between 2 and 5 . else 2
     * @return the clusters
     */
    public static ArrayList<ArrayList<String>> clusterFront(String querys,int x) throws Exception {

//from user
        Clustering.setDocsid(query.query(querys));
        if(x<2||x>5) {
            x=2;
        }
        Clustering.cluster(x);

        for(int id: Clustering.getDocsid()) {//list them
            String name = query.getSearcher().doc(id).get("Topic");
            if(name.equals("Abdullah Al Shami")){
                for(int i=0;i<x;i++) {
                    Clustering.getClusters().get(i).remove(name);
                }
            }

        }


        return Clustering.getClusters();
    }

    /**
     * lists the clusters documents to show the result to the user
     * @param y the number of the cluster
     * @return list of ranked docs
     */
    public static ArrayList<String> listCluster(int y) throws IOException {
        ArrayList<String> z = new ArrayList<>();
        for(int id: Clustering.getDocsid()) {//list them
            String name = query.getSearcher().doc(id).get("Topic");
            if(Clustering.getClusters().get(y-1).contains(name)) {
                if(!(name.equals("Abdullah Al Shami"))){
                    z.add(name);
                }
            }
        }
        return z;
    }

}
