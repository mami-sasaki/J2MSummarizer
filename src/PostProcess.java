import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostProcess {
	public static void main(String[] args) throws IOException {	
        try{	        	
        	File dir = new File("../output/");	        	
        	if(dir.isDirectory()){
        		String[] files = dir.list();
        		for(int i = 0; i < files.length; i++){
        			File f = new File(dir, files[i]);
					if(!f.isDirectory()){						
	        				FileInputStream fis = new FileInputStream(f);
	    	            	BufferedInputStream bis = new BufferedInputStream(fis);
	    	            	DataInputStream dis = new DataInputStream(bis);
	    	            	String line = "";  
	    	            	String one_file = "";	    	            	
	    	            	// change back to the normal format
	    	            	while((line = dis.readLine()) != null){			    	            		
	    	            			line = line.replaceAll("\\[+", "");
	    	            			line = line.replaceAll("\\]+", "");
	    	            			line = line.replaceAll("\\+", " ");
	    	            			line = line.replaceAll("\\,", "\\.");
	    	            			line = line.replaceAll("\\@", "\\'");
	    	            			one_file += line.replaceAll("=", "\\,");	 	    	            			
	    	            	}  					
	    	            	String [] sentences = one_file.split("\\.");
	    	            	Stack order_sent = new Stack();
	    	            	Pattern extra = Pattern.compile("\\s+");
	    	            	for(int k=0; k<sentences.length; k++){
	    	            		sentences[k] = sentences[k].replaceAll("\\#", "\\.");
	    	            		Matcher trash = extra.matcher(sentences[k]);
	    	            		if(!trash.matches())	    	  
	    	            			order_sent.push(sentences[k].trim());	    	            	
	    	            	}
	    	            	FileOutputStream out = new FileOutputStream("../output/"+ files[i]);
	    	            	PrintStream p = new PrintStream(out);	
						while(!order_sent.empty())	   								
							p.println(order_sent.pop() + ".");
							
					}
				}
        	}
        } catch (IOException e){
        	System.out.println(e.getMessage());
        }
	}
}
