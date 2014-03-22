/**
* Cuijun Wu cuijunwu
* John Keesling keesling
* Mami Hackl mami1203
* LING573 D3:content retrieval
*/

// include libraries
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.util.Version;

import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * Document Retriever class
 */
public class ContRet {

  // temporary set path; please change to yours when you test
  //private static String inputDocs = "duc2006_docs/";
  private static String inputDocs = "duc2007_testdocs/main/";
//  private static String queryFile = "duc2006_topics.sgml";
  private static String queryFile = "duc2007_topics.sgml";

  /**
   * Process query file.
   * @throws java.io.IOException
   */
  private static Hashtable processQueryFile(String file) throws IOException {

    File f = new File(file);
    Hashtable dict = new Hashtable();

    //added for D4: post processing purpose
//    File ppqf = new File("../output/ppquery.txt");
//    FileWriter queryfw = new FileWriter(ppqf);

    if (!f.exists()) {
      System.out.println(file + " does not exist.");
      System.exit(-1);
    } else {

      FileReader fr = new FileReader(f);
      BufferedReader br = new BufferedReader(fr);
      String line;
      Matcher matcher;

      // regular expressions to extract data from documents
      Pattern pattern = Pattern.compile("<num> (.*) </num>");
      Pattern pattern2 = Pattern.compile("<title> (.*) </title>");
      Pattern pattern3 = Pattern.compile("<narr>");
      Pattern pattern4 = Pattern.compile("</narr>");

      while((line = br.readLine()) != null){
        String topicIndex = "";
        String title = "";
        String query = "";
        String orgQuery = "";
        Set set = new LinkedHashSet();

        matcher = pattern.matcher(line);
        if(matcher.find()){
           topicIndex = matcher.group(1);
           line = br.readLine();
           matcher = pattern2.matcher(line);
           if(matcher.find()){
             String[] temp = new String[50];
             title = matcher.group(1);
             temp = title.split(" "); 
             for (int i = 0; i < temp.length;i++){
               set.add(temp[i]);
             }
             while((line = br.readLine()) != null) {
               matcher = pattern3.matcher(line);
               if(matcher.find()){
                 while((line = br.readLine()) != null) {
                    matcher = pattern4.matcher(line);
                    if(matcher.find()){
                    break;
                    }else{
                     orgQuery = orgQuery.concat(line);
                     temp = new String[50];
                     temp = line.split(" ");
                     for(int i = 0; i < temp.length;i++){
                       if (set.contains(temp[i]) == false){
                         set.add(temp[i]); 
		       }
                     }
                    }
                 }
                 for (Object obj : set) {  
                   query += ((String)obj + " ");  
                 }
                 
                 //queryfw.write(topicIndex + ": " + orgQuery + "\n");
                 query = title + " " + query;
                 dict.put(topicIndex, query);
                 break;
               }
	     }
           }
        }
      } 
    } 
    //queryfw.close();
    return dict;
  }


  /**
   * main
   * @param args
   * @throws java.io.IOException and ParseException
   */
  public static void main(String[] args) throws IOException,ParseException,InterruptedException {

//     String path = args.length > 0 ? args[0] : "~/dropbox/09-10/573/corpora/testdata/";
     String path = args.length > 0 ? args[0] : "~/dropbox/09-10/573/D4/duc07.results.data/testdata/";

    //process query file
    String query = path + queryFile;
    Hashtable dict = processQueryFile(query);

    String dir = "temp";
    StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
    ContIndexer indexer = null;

    //add file into the index
    for(Enumeration e = dict.keys(); e.hasMoreElements();) {

       String k = (String)e.nextElement();
       
    
       //if(k.equals("D0721E")){ 
       try {
         indexer = new ContIndexer(dir,analyzer);
       } catch (Exception ex) {
         System.out.println("Cannot create index..." + ex.getMessage());
         System.exit(-1);
       }

       //int hitsPerPage = indexer.indexFileOrDirectory("../output/" + k);
       indexer.indexFileOrDirectory(path + inputDocs + k);

       //closeIndex
       indexer.closeIndex();

       //query
       //String querystr = args.length > 0 ? args[0] : "Reservation";
       String querystr = (String)dict.get(k);
       String newQuery1 = "";
       String newQuery2 = "";

       //call query processing modules
       Runtime r = Runtime.getRuntime();

       //write original query to file
       File file = new File("../output/orgQ.txt");
       FileWriter filewriter = new FileWriter(file);

       filewriter.write(querystr);
       filewriter.close();

       //first expand words by calling WordNet
       Process p;

       //p = r.exec("expandWord.py");
       //p.waitFor();

       //Second tag words and find keywords
       p = r.exec("tagger.py");
       p.waitFor();

       FileReader fr = new FileReader("../output/taggedQ.txt");
       BufferedReader br = new BufferedReader(fr);
       String line = "";
       while((line = br.readLine()) != null){
          newQuery1 = newQuery1.concat(line);
       }

       p.waitFor();
       file.delete();
       file = new File("../output/taggedQ.txt");
       file.delete();
       //file = new File("../output/expQ.txt");
       //file.delete();
 
       //Finally porter-stemming
/*
       String[] cmd = {"/bin/sh","-c", "cat ../output/expQ.txt | ./porter_stemmer.pl"};
       p = r.exec(cmd);
       p.waitFor();

       br = new BufferedReader(new InputStreamReader(p.getInputStream()));
       line = "";
       while ((line = br.readLine()) != null)
       {
           newQuery2 = newQuery2.concat(line);
       }
       p.waitFor();

       newQuery1 = newQuery1 + newQuery2;
*/     
       Pattern pat = Pattern.compile("[^\\w\\s]");
       Matcher matcher;
       matcher = pat.matcher(newQuery1);
       if (matcher.find()) {
          newQuery1 = matcher.replaceAll("");
       }
       System.out.println(k + ": " + newQuery1 + "\n");
        
       // the "contents" arg specifies the default field to use
       // when no field is explicitly specified in the query.
       Query q = new QueryParser(
       Version.LUCENE_CURRENT, "contents", analyzer).parse(newQuery1);

       //search
       int hitsPerPage = 30;
       IndexSearcher searcher = new IndexSearcher(dir, true);
       TopScoreDocCollector collector =
       TopScoreDocCollector.create(hitsPerPage, true);
       searcher.search(q, collector);
       ScoreDoc[] hits = collector.topDocs().scoreDocs;
 
       //display results
       File out = new File("../output/" + k);
       FileWriter fw = new FileWriter(out);
       //fw.write("Found " + hits.length + " hits." + "\n");

       Set set = new LinkedHashSet();
       // check if there's any duplicates
       for(int i=0;i<hits.length;++i) {
          int docId = hits[i].doc;
          Document d = searcher.doc(docId);
          String cand = d.get("contents");
          if(set.contains(cand) == false){
            
             pat = Pattern.compile("( [Ii]t | [Ss]he | [Hh]e | His | [Hh]im | Her | [Tt]hey | Their | [Th]hem | Its )");
             matcher = pat.matcher(cand);
             // call anaphora resolution
             if (matcher.find()){
               //System.out.println(matcher.group(0) + '\n' + cand);
               String filepath = d.get("path"); 
               String[] cmd = {"./anaphoraSolver.py", filepath, cand};
               p = r.exec(cmd);
               p.waitFor();

               br = new BufferedReader(new InputStreamReader(p.getInputStream()));
               line = "";
               String cand1 = "";
               while ((line = br.readLine()) != null)
               {
                  cand1 = cand1.concat(line);
               }  
               p.waitFor();
               //System.out.println(cand1);
               cand = cand1;
             } 

            set.add(cand); 
          }
       }
       // output candidate sentences to file
       int counter = 0;
       //Pattern pat = Pattern.compile("\\s*['`\"]{1,2}");
       pat = Pattern.compile("^&");
       String temp = "";
       int len = 0;

       for (Object obj: set){
          temp = (String)obj;
          //System.out.println(temp);
          len = temp.split(" ").length;
          matcher = pat.matcher((String)obj);
          if (len > 5){
            if (!(matcher.find())){
              if (counter < 20){
                fw.write((String)obj + "\n");
                counter++;
              }else{break;}
            } 
          }
       }
       fw.close();
       
       //remove similar sentences 
       String filename = "../output/" + k;
       p = r.exec("./helper.pl " + filename +  ' ' + filename + ".txt");
       p.waitFor();
 
     //}
    } // end of for loop
  }
}
