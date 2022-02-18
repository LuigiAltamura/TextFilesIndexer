package app;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {

    private Directory memoryIndex;
    private final StandardAnalyzer analyzer;
    private IndexWriter indexWriter;
    private IndexWriterConfig indexWriterConfig;
    private List<File> queue;

    /**
     * Constructor of app.Indexer
     * @param memoryPath path of the folder in which the index files are stored
     */
    public Indexer(String memoryPath){

        try {
            this.memoryIndex = FSDirectory.open(Paths.get(memoryPath));
        } catch (IOException e) {
            System.out.println("Error opening folder" + e.getMessage());
        }

        this.analyzer = new StandardAnalyzer();
        this.indexWriterConfig = new IndexWriterConfig(analyzer);

        try {
            this.indexWriter = new IndexWriter(memoryIndex, indexWriterConfig);
        } catch (IOException e) {
            System.out.println("Error creating indexWriter"+ e.getMessage());
        }
    }


    /**
     * This method create an indexWriter
     */
    public void createNewIndexWriter() {
        this.indexWriterConfig = new IndexWriterConfig(analyzer);
        try {
            this.indexWriter = new IndexWriter(memoryIndex, indexWriterConfig);
        } catch (IOException e) {
            System.out.println("Problem creating indexWriter" + e.getMessage());
        }
    }

    /**
     *This method adds a file or a group of files into the index.
     * @param filepath is the path of a file or a directory
     * @throws IOException when a problem occurs with FileReader or indexWriter commit
     */

    public void addFileToIndex(String filepath) throws IOException {

        this.queue = new ArrayList<>();

        File file = new File(filepath);

        addFiles(file);

        if(!indexWriter.isOpen()){
            createNewIndexWriter();
        }

        for (File f : queue) {
            FileReader fr = null;
            try {
                Document doc = new Document();

                //===================================================
                // add contents of file
                //===================================================
                fr = new FileReader(f);
                doc.add(new TextField("contents", fr));
                doc.add(new StringField("path", f.getPath(), Field.Store.YES));
                doc.add(new StringField("filename", f.getName(), Field.Store.YES));

                indexWriter.addDocument(doc);
                System.out.println("------------------------------------------------------------------\n"
                        + "Added: " + f + "\n"+
                        "------------------------------------------------------------------\n"
                );
            } catch (Exception e) {
                System.out.println("---------------------------------------------------------------------\n"+
                        "Could not add: " + f+ "\n"+
                        "---------------------------------------------------------------------\n");
            } finally {
                if(fr != null)
                    fr.close();
            }
        }

            /*
            Commit is 5 times faster than close and permit to update the index without create every time the IndexWriter and the indexWriterConfig.
            If I have to add the files only one time is better close the index after the operation. It depends on the uses.
             */
        indexWriter.commit();
    }

    /**
     * This method creates a list of files to add to the index. If the parameter is a folder all the text files, including those of the sub folders, will be added to the list.
     * @param file folder or file to be added to the index
     */
    public void addFiles(File file) {

        if (!file.exists()) {
            System.out.println("------------------------------------------------------------------\n"
                    +  file + " does not exist."+ "\n"+
                    "------------------------------------------------------------------\n"
            );
        }
        if (file.isDirectory()) {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                addFiles(f);
            }
        } else {
            String filename = file.getName().toLowerCase();
            //===================================================
            // Only index text files
            //===================================================
            if (filename.endsWith(".txt")) {
                queue.add(file);
            } else {
                System.out.println(
                        "------------------------------------------------------------------\n"
                                + filename + " is not a text file. File Skipped."+ "\n"+
                                "------------------------------------------------------------------\n");
            }
        }
    }

    /**
     * This method deletes a file from the index
     * @param filePath is the path of the file to be deleted
     * @return returns true if the file was successfully deleted, false otherwise
     */

    public boolean deleteFile(String filePath) {
        try {
            if(!indexWriter.isOpen())
                createNewIndexWriter();

            File file = new File(filePath);
            if(!file.exists()){
                return false;
            }else{
                Term term = new Term("path", filePath);
                indexWriter.deleteDocuments(term);
                indexWriter.close();
            }

        } catch (IOException e) {
            System.out.println("Error deleting " + filePath + " : " + e.getMessage());
        }
        return true;
    }

    /**
     * Close the index.
     * @throws java.io.IOException when exception closing
     */
    public void closeIndex() throws IOException {
        indexWriter.close();
    }

    /**
     * This method deletes all files in the index folder
     * @throws IOException when a problem occurs with memoryIndex
     */
    public void cleanIndex() throws IOException {
        String[] files = memoryIndex.listAll();
        if(files != null){
            for(String f: files){
                memoryIndex.deleteFile(f);
            }
        }

    }

    /**
     * This method checks if the indexWriter is open
     * @return true if the indexWriter is open, false otherwise
     */
    public boolean isOpen(){
        return indexWriter.isOpen();
    }

    public Directory getMemoryIndex(){
        return this.memoryIndex;
    }

    public StandardAnalyzer getAnalyzer(){
        return this.analyzer;
    }

    public List<File> getQueue(){
        return this.queue;
    }

}

