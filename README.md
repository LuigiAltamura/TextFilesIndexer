This Java program allows you to index text files.

To develop this application, I decided to index the files using the Apache Lucene library, in particular version 8.11.1 released on December 16, 2021.

For the sake of simplicity, I placed the folder where the index files are saved inside the resources folder (src/main/resources). Same thing holds for the text files I used to do tests.
When the user launches the app they can choose whether to add, delete, search for a file or quit.

User can add a text file or folder containing text files. In the second case, all and only text files in that folder and sub-folders will be added.
If the user wants to delete a file from the index, it is sufficient to type the path of the file.
To search for a file, the user can type a word or a sentence. This function already contains a tokenization algorithm.

To avoid creating many files for testing, I decided that when the user quits, all the files into the index are deleted. By deleting line 111 of Main class (src/main/java/ app/Main.java), the app would keep the saved files in the subsequent sessions.

To verify the correct functioning of the program, I developed some tests using JUnit and the files inside the directory /resources/textFiles.
