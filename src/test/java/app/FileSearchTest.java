package app;

import org.apache.lucene.document.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileSearchTest {


    static Indexer indexer = new Indexer("src/main/resources/index");
    FileSearch fs = new FileSearch(indexer.getMemoryIndex(), indexer.getAnalyzer());

    @AfterEach
    public void after() throws IOException {
        indexer.closeIndex();
        indexer.cleanIndex();

    }

    /**
     * This method checks that the search results are consistent with the query and the files that are into the index.
     * @throws IOException when a problem occurs with FileReader or indexWriter commit into method addFileToIndex
     */
    @Test
    void searchFiles() throws IOException {
        indexer.addFileToIndex("src/main/resources/textFiles/4.txt");
        assertEquals(fs.searchFiles("Hello").get(0).get("path"),"src/main/resources/textFiles/4.txt");
        assertEquals(fs.searchFiles("wefe").size(),0);

        indexer.addFileToIndex("src/main/resources/textFiles/folder1");

        assertEquals(fs.searchFiles("Yellow").get(0).get("path"),"src/main/resources/textFiles/folder1/1.txt");
        assertEquals(fs.searchFiles("wefe").size(),0);

        assertEquals(fs.searchFiles("Hi").get(0).get("path"),"src/main/resources/textFiles/folder1/3.txt");
        assertEquals(fs.searchFiles("wefe").size(),0);

        assertEquals(fs.searchFiles(" praesentium voluptatum").get(0).get("path"),"src/main/resources/textFiles/folder1/folder2/5.txt");
        assertEquals(fs.searchFiles("wefe").size(),0);


        assertEquals(fs.searchFiles("bye bye").size(),1);

        indexer.addFileToIndex("src/main/resources/textFiles/2.txt");

        List<Document> search = fs.searchFiles("bye bye");
        assertEquals(search.size(),2);
        assertEquals(search.get(0).get("path"),"src/main/resources/textFiles/2.txt");
        assertEquals(search.get(1).get("path"),"src/main/resources/textFiles/folder1/folder2/7.txt");

    }
}