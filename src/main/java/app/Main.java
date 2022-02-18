package app;

import org.apache.lucene.document.Document;
import java.io.*;
import java.util.List;


public class Main {


    public static void main(String[] args) throws IOException {

        String indexLocation = "src/main/resources/index";
        Indexer indexer =  new Indexer(indexLocation);
        FileSearch fileSearch = new FileSearch(indexer.getMemoryIndex(), indexer.getAnalyzer());

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        List<Document> searchResult;


        String s = "";


        //read input from user until he enters q for quit
        while (!s.equalsIgnoreCase("q")) {

            System.out.println(
                    """
                            ------------------------------------------
                            |  Type "a" to add files or folders      |
                            |  Type "s" to search files by word      |
                            |  Type "d" to delete a file             |
                            |  Type "q" to quit                      |
                            ------------------------------------------"""

            );
            s = br.readLine();
            if(s.equalsIgnoreCase("a")){
                System.out.println("""
                                -----------------------------------------------------------------------------------
                                Enter the full path to add file or directory to be included into the index :
                                (e.g. /Users/username/mydir) (q=quit)
                                [Acceptable file types: .txt]
                                -----------------------------------------------------------------------------------""");

                s = br.readLine();

                indexer.addFileToIndex(s);

            }

            if(s.equalsIgnoreCase("s")){
                if(indexer.isOpen())
                    indexer.closeIndex();

                System.out.println("""
                            -----------------------------------------
                            Enter the search query (q=quit):
                            -----------------------------------------""");
                s = br.readLine();

                searchResult = fileSearch.searchFiles(s);


                if(searchResult.size() == 0)
                    System.out.println("""
                            -----------------------------------------
                            The search did not return any results
                            -----------------------------------------""");
                else{

                    System.out.print("-----------------------------------------\n");
                    for(Document d : searchResult){
                        System.out.println(d.get("path"));
                    }
                    System.out.print("-----------------------------------------\n");
                }



            }
            if(s.equalsIgnoreCase("d")){
                System.out.println("""
                                --------------------------------------------------------------------------------------
                                Enter the full path to delete file from the index : (e.g. /Users/username/mydir)
                                --------------------------------------------------------------------------------------"""

                );
                s = br.readLine();

                if(indexer.deleteFile(s)){
                    System.out.println("""
                            -----------------------------------------
                            File deleted successfully!
                            -----------------------------------------""");
                }else{
                    System.out.println("""
                            -----------------------------------------
                            File does not exist
                            -----------------------------------------""");
                }

            }

        }


        if(indexer.isOpen()){
            indexer.closeIndex();
        }

        indexer.cleanIndex();


    }

}
