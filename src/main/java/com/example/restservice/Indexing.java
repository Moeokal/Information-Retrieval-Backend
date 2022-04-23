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

    private FieldType Main=new FieldType();
    private FieldType Topic=new FieldType();
    private String pathRead;
    private String pathWrite;
    private Directory index;
    private IndexWriter indexWriter;
    private static volatile Indexing instance = null;

    private Indexing(){
        setFields();
    }

    /**
     * if no Objects -> creates an Object and returns it
     * if an object is already created -> returns that object
     * (singleton)
     * @return The only Object of the class
     */
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

    /**
     * sets the Fields to save important statistics about the Terms
     */
    private void setFields(){
        Main.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS); //enable storing the reuired statistics
        Main.setStored(true);
        Main.setStoreTermVectors(true);
        Main.setStoreTermVectorPositions(true);
        Main.setStoreTermVectorPayloads(true);
        Main.setStoreTermVectorOffsets(true);
        Topic.setStored(true);
    }

    /**
     * Indexes the document, analyzes it and adds it to the Index
     * @param content the main part of the document
     * @param titel the title of the documenet
     * @throws IOException
     */
    private void addDoc(String content, String titel) throws IOException {
        Document doc = new Document();
        Topic.setStored(true);
        doc.add(new Field("Main", content, Main));
        doc.add(new Field("Topic", titel, Topic));
        indexWriter.addDocument(doc);
        //docnum++;
    }


    /**
     * creates an Analyzer (StandardTokenizer/Lowercase/stopwords removal/portertemming/keeps URLs)
     * @return Englisch Analyzer
     */
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

    /**
     * changes the read/write directories
     * @param pathW new Write Path
     * @param pathR new Read Path
     */
    public void setDirectory(String pathW,String pathR) {
        pathWrite=pathW;
        pathRead=pathR;
    }

    /**
     * creates an Index Writer with the Analyzer of the method analyzer()
     * @throws IOException
     */
    public void CreateWriter() throws IOException {
        index = FSDirectory.open(Paths.get(pathWrite)); //makes a new directory for storing the index
        IndexWriterConfig config = new IndexWriterConfig(analyzer());
        indexWriter = new IndexWriter(index, config);
    }

    /**
     * deletes the Index and it's Files
     */
    public void clearDirectory() {
        File dir = new File(pathWrite);
        File[] files = dir.listFiles();
        assert files != null;
        boolean success = true;
        for(File file: files)
            success = file.delete();
        assert success;
    }


    /**
     * Indexes, anlayzes all the Documents in the Read Directory and adds them to the Index
     * @throws IOException
     */
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

    public Directory getIndex() {
        return index;
    }

    public String getPathWrite() {
        return pathWrite;
    }

}
