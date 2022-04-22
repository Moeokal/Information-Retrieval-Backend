package com.example.restservice;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

    private static Indexing index = Indexing.getInstance();
    private static Query query = Query.getInstance();

    public static void button1continue(String s1, String s2) throws IOException {
        index.setDirectory(s1, s2);
        index.clearDirectory();
        index.CreateWriter();
        index.addFilesToIndex();
    }

    // IMPORTANT PAGE
    public static ArrayList<ArrayList<String>> clusterFront(String querys,int x) throws Exception {

//from user
        Clustering.docsid=query.query(querys);
        if(x<2||x>5) {
            x=2;
        }
        Clustering.cluster(x);
        return Clustering.clusters;
    }

    public static ArrayList<String> listCluster(int y) throws IOException {
        ArrayList<String> z = new ArrayList<>();
        for(int id: Clustering.docsid) {//list them
            String name = query.searcher.doc(id).get("Topic");
            if(Clustering.clusters.get(y-1).contains(name)) {
                z.add(name);
            }
        }
        return z;
    }

}
