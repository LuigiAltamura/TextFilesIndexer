package app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class IndexerTest {

    static Indexer indexer = new Indexer("src/main/resources/index");
    FileSearch fs = new FileSearch(indexer.getMemoryIndex(), indexer.getAnalyzer());

    @AfterEach
    public void after() throws IOException {

        indexer.closeIndex();
        indexer.cleanIndex();

    }

    /**
     * This test verifies that a file is correctly added to the index
     * @throws IOException when a problem occurs with FileReader or indexWriter commit into method addFileToIndex
     */
    @Test
    void addFileToIndex() throws IOException {
        indexer.addFileToIndex("src/main/resources/textFiles/4.txt");
        assertEquals(fs.searchFiles("Hello").get(0).get("path"),"src/main/resources/textFiles/4.txt");

        indexer.addFileToIndex("src/main/resources/textFiles/folder1");

        assertEquals(fs.searchFiles("Yellow").get(0).get("path"),"src/main/resources/textFiles/folder1/1.txt");
        assertEquals(fs.searchFiles("Hi").get(0).get("path"),"src/main/resources/textFiles/folder1/3.txt");
        assertEquals(fs.searchFiles(" praesentium voluptatum").get(0).get("path"),"src/main/resources/textFiles/folder1/folder2/5.txt");

        assertEquals(fs.searchFiles("efwef").size(),0);
        assertEquals(fs.searchFiles("bye bye").size(),1);


    }

    /**
     * This test verifies that the file into folders are correctly selected and added to the queue
     * @throws IOException when a problem occurs with FileReader or indexWriter commit into method addFileToIndex
     */
    @Test
    void addFiles() throws IOException {

        indexer.addFileToIndex("src/main/resources/textFiles/4.txt");
        assertEquals(indexer.getQueue().size(),1);
        assertEquals(indexer.getQueue().get(0).getPath(),"src/main/resources/textFiles/4.txt");

        indexer.addFileToIndex("src/main/resources/textFiles/folder1");
        assertEquals(indexer.getQueue().size(),4);
        assertEquals(indexer.getQueue().get(3).getPath(),"src/main/resources/textFiles/folder1/1.txt");
        assertEquals(indexer.getQueue().get(2).getPath(),"src/main/resources/textFiles/folder1/3.txt");
        assertEquals(indexer.getQueue().get(1).getPath(),"src/main/resources/textFiles/folder1/folder2/7.txt");
        assertEquals(indexer.getQueue().get(0).getPath(),"src/main/resources/textFiles/folder1/folder2/5.txt");


    }

    /**
     * This test checks that files are correctly deleted from the index
     */
    @Test
    void deleteFile() {
        assertTrue(indexer.deleteFile("src/main/resources/textFiles/4.txt"));
        assertTrue(indexer.deleteFile("src/main/resources/textFiles/folder1/1.txt"));
        assertTrue(indexer.deleteFile("src/main/resources/textFiles/folder1/3.txt"));
        assertTrue(indexer.deleteFile("src/main/resources/textFiles/folder1/folder2/5.txt"));
        assertFalse(indexer.deleteFile("src/main/resources/textFiles/6.xml"));

    }
}