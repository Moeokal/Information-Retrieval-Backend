package com.example.restservice;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

public class Indexing {  //singleton

    FieldType Main=new FieldType();
    FieldType Topic=new FieldType();
    String pathRead;
    String pathWrite;
    Directory index;
    IndexWriter indexWriter;
    private static volatile Indexing instance = null;

    private Indexing(){
        setFields();
    }
    public static Indexing getInstance() {
        if (instance == null) {
            synchronized(Indexing.class) {
                if (instance == null) {
                    instance = new Indexing();
                }
            }
        }

        return instance;
    }

    private void setFields(){
        Main.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS); //enable storing the reuired statistics
        Main.setStored(true);
        Main.setStoreTermVectors(true);
        Main.setStoreTermVectorPositions(true);
        Main.setStoreTermVectorPayloads(true);
        Main.setStoreTermVectorOffsets(true);
        Topic.setStored(true);
    }


    private void addDoc(String content, String titel) throws IOException {
        Document doc = new Document();
        Topic.setStored(true);
        doc.add(new Field("Main", content, Main));
        doc.add(new Field("Topic", titel, Topic));
        indexWriter.addDocument(doc);
        //docnum++;
    }


    public static Analyzer analyzer() {
        /*
		 * add config as parameter
		 *
		 * if stem then
		 * else if lower then
		 * 		Analyzer analyzer = CustomAnalyzer.builder()
				.withTokenizer(StandardTokenizerFactory.class)
                .addTokenFilter(LowerCaseFilterFactory.class)
                .addTokenFilter("stop", "ignoreCase", "false", "words", "stopwords.txt", "format", "wordset")
				.addTokenFilter("porterstem")
                .build();

		 */
        return new EnglishAnalyzer();
    }

    public void setDirectory(String pathW,String pathR) {
        pathWrite=pathW;
        pathRead=pathR;
    }

    public void CreateWriter() throws IOException {
        index = FSDirectory.open(Paths.get(pathWrite)); //makes a new directory for storing the index
        IndexWriterConfig config = new IndexWriterConfig(analyzer());
        indexWriter = new IndexWriter(index, config);
    }

    public void clearDirectory() {
        File dir = new File(pathWrite);
        File[] files = dir.listFiles();
        assert files != null;
        boolean success = true;
        for(File file: files)
            success = file.delete();
        assert success;
    }


    public void addFilesToIndex() throws IOException {
        File dir = new File(pathRead);
        File[] files = dir.listFiles();
        String docMain;
        String docTitel;
        assert files != null;
        for (File file : files) {
            FileReader fileReader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);
            StringBuilder sb = new StringBuilder();

            String line = br.readLine();
            while (line != null) {
                // reading lines until the end of the file
                sb.append(line).append("\n");
                line = br.readLine();
            }
            docMain = sb.toString();
            docTitel = file.getName().substring(0, file.getName().length()-4);
            br.close();
            addDoc(docMain, docTitel);
        }
        indexWriter.close();
    }

    /*
    public void reIndex(Analyzer analyzer, String path) throws IOException {
        clearDirectory();

        setDirectory(path, pathRead);
        CreateWriter();
        addFilesToIndex();
    }
    */

    /*
    public static void setSimType(String s) {
        if (Objects.equals(s, "BM25")) {
            simType = new BM25Similarity();
        } else {
            simType = new ClassicSimilarity();
        }
    }
    */
}
