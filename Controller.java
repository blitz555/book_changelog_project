import java.io.*;
import java.util.Scanner;
import org.apache.commons.lang3.*;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import javax.swing.*;

/**
 * toc class is the controller
 * @author Z
 *
 */

//read in and compares two table of contents from text files
public class Controller extends Constants implements ActionListener{
	private Model model;
	private View view;
	
	public Controller(Model themodel, View theview ){
		this.model = themodel;
		this.view = theview;
		this.view.addEventListener((ActionListener)this);
	}
	
	//inner class listener for compare button
	public void actionPerformed(ActionEvent e){
		String newtextstring = new String();
		String oldtextstring = new String();

		int similaritylevel1 = view.slider1.getValue();//get high similarity match value 
		int similaritylevel2 = view.slider2.getValue();//get high similarity match value 
		if(similaritylevel1 == 100){
			similaritylevel1-=1;//set max value to 99
		}
		
		double similaritylevel1_percent = 1.0 * similaritylevel1 / 100;	
		double similaritylevel2_percent = 1.0 * similaritylevel2 / 100;	
		System.out.println("slider value1 " + similaritylevel1 + "% " +  similaritylevel1_percent );
		System.out.println("slider value2 " + similaritylevel2 + "% " +  similaritylevel2_percent );
		
		//read string from textareas and turn write files
		
		try{
			PrintWriter newwriter = new PrintWriter("src/newtest.txt", "UTF-8");
			newwriter.print(newtextstring);
			newwriter.close();
				
			PrintWriter oldwriter = new PrintWriter("src/oldtest.txt", "UTF-8");
			oldwriter.print(oldtextstring);
			oldwriter.close();
		}
		catch(IOException f){
			System.out.println("error while writing file");
		}		
			
		//this.model.book_string_new = inputFileToString("src/newtest.txt");
		//this.model.book_string_old = inputFileToString("src/oldtest.txt");
		//alternative test with no input
		this.model.book_string_new = inputFileToString("src/discrete4.txt");
		this.model.book_string_old = inputFileToString("src/discrete3.txt");
		
		//System.out.println("Book (new) TOC #1:\n" + this.model.book_string_new + "\n");
		//System.out.println("Book (old) TOC #2:\n" + this.model.book_string_old + "\n");
		
		String[] book_array_new = this.model.book_string_new.split("\\s+");
		String[] book_array_old = this.model.book_string_old.split("\\s+");
		
		//start to process book tokens
		this.model.pair.processTokens("NEW EDITION", book_array_new, this.model.pair.keywordCounter_new);
		this.model.pair.processTokens("OLD EDITION", book_array_old, this.model.pair.keywordCounter_old);

		this.model.pair.bookhead_new = this.model.pair.scan("NEW EDITION", this.model.pair.keywordCounter_new, this.model.pair.bookhead_new);
		this.model.pair.bookhead_old = this.model.pair.scan("OLD EDITION", this.model.pair.keywordCounter_old, this.model.pair.bookhead_old);
		
		//initialize list to hold all sections
		ArrayList<Section> newlist = this.model.pair.bookhead_new.createonelist( new ArrayList<Section>() );
		ArrayList<Section> oldlist = this.model.pair.bookhead_old.createonelist( new ArrayList<Section>() );
		ArrayList<ArrayList<Section>> newlevels = createlevels(newlist); 
		ArrayList<ArrayList<Section>> oldlevels = createlevels(oldlist); 
		
		//keep chapter/section statistics prior to matching
		int newbook_chapter_num = newlevels.get(0).size();
		int oldbook_chapter_num = oldlevels.get(0).size();
		int newbook_section_num = newlevels.get(1).size();
		int oldbook_section_num = oldlevels.get(1).size();
		
		//perform high similarity matching
		int match = 0;
		int nomatch = 0;
		int match_section = 0;
		boolean matched = false;//signal whether match has been found for current section index, if matched, increment, else return
		for(int k = 0; k < newlevels.size() || k < oldlevels.size(); k++){
			for(int i = 0; i < newlevels.get(k).size(); ){//match in level 2
				for(int j = 0; j < oldlevels.get(k).size();  ){
					Section newsection = newlevels.get(k).get(i);
					Section oldsection = oldlevels.get(k).get(j);
						//oldsectiontitle = oldlevels.get(i).get(k).gettitle();//update old title
					int n = StringUtils.getLevenshteinDistance( newsection.gettitle(), oldsection.gettitle() );
					double percentmatch;
					if( newsection.gettitle().length() > oldsection.gettitle().length()){//normalize wrt newsection title, greater
						percentmatch = 1.0 * (newsection.gettitle().length() - n ) / newsection.gettitle().length();
					}
					else{//normalize wrt oldsection title, greater length
						percentmatch = 1.0 * (oldsection.gettitle().length() - n ) / oldsection.gettitle().length();
					}
					//System.out.println(percentmatch + " " + similaritylevel1_percent );
					if( percentmatch - similaritylevel1_percent > 0){//complete title match found, j not incremented
						/*print match percent and titles
						System.out.printf("%.2f match ", percentmatch );
						System.out.printf( " %-60s ", newsection.gettitle() );
						System.out.printf( " %-60s\n", oldsection.gettitle() );
						*/
						match++;
						
						if(k == 1){//subsection match case
							match_section++;
						}
						//replace with turning matched flag in section object
						newlevels.get(k).get(i).matchfound = true;//change matched state
						oldlevels.get(k).get(j).matchfound = true;//change matched state
						
						newlevels.get(k).get(i).similarityscore = percentmatch;//change score
						oldlevels.get(k).get(j).similarityscore = percentmatch;//change score
						
						newlevels.get(k).get(i).match = oldlevels.get(k).get(j);//change matching section
						oldlevels.get(k).get(j).match = newlevels.get(k).get(i);//change matching section
						
						newlevels.get(k).remove(i);//removed from comparison once match found
						oldlevels.get(k).remove(j);//removed from comparison once match found
						
						matched = true;
						break;
					}
					else{//not complete title match, increment j
						matched = false;
					
						j++;//
						nomatch++;
					}
				}
				if(matched == false){
					i++;//increment only in case of no match
				}//end of for loop j
			}//end of for loop i
		}//end of for loop k

		//match iteration 2: yellow
		for(int k = 0; k < newlevels.size() || k < oldlevels.size(); k++){
			for(int i = 0; i < newlevels.get(k).size(); ){//match in level 2
				for(int j = 0; j < oldlevels.get(k).size();  ){
					Section newsection = newlevels.get(k).get(i);
					Section oldsection = oldlevels.get(k).get(j);
						//oldsectiontitle = oldlevels.get(i).get(k).gettitle();//update old title
					int n = StringUtils.getLevenshteinDistance( newsection.gettitle(), oldsection.gettitle() );
					double percentmatch;
					if( newsection.gettitle().length() > oldsection.gettitle().length()){//normalize wrt newsection title, greater
						percentmatch = 1.0 * (newsection.gettitle().length() - n ) / newsection.gettitle().length();
					}
					else{//normalize wrt oldsection title, greater length
						percentmatch = 1.0 * (oldsection.gettitle().length() - n ) / oldsection.gettitle().length();
					}
					//System.out.println(percentmatch + " " + similaritylevel1_percent );
					if( percentmatch - similaritylevel2_percent > 0){//complete title match found, j not incremented
						
						match++;
						
						if(k == 1){//subsection match case
							match_section++;
						}
						//replace with turning matched flag in section object
						newlevels.get(k).get(i).matchfound = true;//change matched state
						oldlevels.get(k).get(j).matchfound = true;//change matched state
						
						newlevels.get(k).get(i).similarityscore = percentmatch;//change score
						oldlevels.get(k).get(j).similarityscore = percentmatch;//change score
						
						newlevels.get(k).get(i).match = oldlevels.get(k).get(j);//change matching section
						oldlevels.get(k).get(j).match = newlevels.get(k).get(i);//change matching section
						
						newlevels.get(k).remove(i);//removed from comparison once match found
						oldlevels.get(k).remove(j);//removed from comparison once match found
						
						matched = true;
						break;
					}
					else{//not complete title match, increment j
						matched = false;
					
						j++;//
						nomatch++;
					}
				}
				if(matched == false){
					i++;//increment only in case of no match
				}//end of for loop j
			}//end of for loop i
		}//end of for loop k
		
		//test print after matching
		//this.model.pair.bookhead_new.printTable(" ");
		//this.model.pair.bookhead_old.printTable(" ");
		
		System.out.println("matched: " + match + " nomatch: " + nomatch);
		/*print new/old matched titles side by side
		for(int i = 0; i < newlevels.get(1).size() || i < oldlevels.get(1).size(); i++){
			if(i < newlevels.get(1).size() - 1 ){
				System.out.printf( "%-50s ", newlevels.get(1).get(i).gettitle() );
			}
			else if ( i == newlevels.get(1).size() - 1 ){
				System.out.printf( "%-50d ", newlevels.get(1).size() );
			}
			if(i < oldlevels.get(1).size() - 1 ){
				System.out.printf( " %-50s", oldlevels.get(1).get(i).gettitle() );
			}
			else if ( i == oldlevels.get(1).size() - 1 ){
				System.out.printf( "%-50d ", oldlevels.get(1).size() );
			}
			System.out.printf("\n");
		}
		*/
		//working here		
		ArrayList<JLabel> newbook = new ArrayList<JLabel>();//keep storage of title labels
		ArrayList<JLabel> oldbook = new ArrayList<JLabel>();//keep storage of title labels
		this.model.pair.bookhead_new.transferAllLabels(newbook, similaritylevel1_percent, similaritylevel2_percent);//create labels for each title
		this.model.pair.bookhead_old.transferAllLabels(oldbook, similaritylevel1_percent, similaritylevel2_percent);//create labels for each title
		
		//label display statistics
		double chapternum_change_percent = 0;	
		if( newbook_chapter_num > oldbook_chapter_num ){//find bigger chapter number
			chapternum_change_percent = ((double)oldbook_chapter_num ) / newbook_chapter_num;
		}
		else{
			chapternum_change_percent = ((double)newbook_chapter_num ) / oldbook_chapter_num;
		}
		int chapternum_change_num = (int)(chapternum_change_percent * 100);//parameter value
		
		double sectionnum_change_percent = 0;
		if( newbook_section_num > oldbook_section_num ){//find bigger sections number
			sectionnum_change_percent = ((double)oldbook_section_num  ) / newbook_section_num ;
		}
		else{
			sectionnum_change_percent = ((double)newbook_section_num ) / oldbook_section_num ;
		}
		int sectionnum_change_num = (int)(sectionnum_change_percent * 100);//parameter value
		
		double section_match_percent = ((double)match_section ) / newbook_section_num ;
		int section_match_num = (int)(section_match_percent * 100);//parameter value
		int compositescore_num = (chapternum_change_num + sectionnum_change_num + section_match_num) / 3;//parameter value
		
		int greaterlabelscount = 0;//keeps maximum size of 2d array
		if( newbook.size() > oldbook.size() ){
			greaterlabelscount = newbook.size();
		}
		else{
			greaterlabelscount = oldbook.size();
		}
		
		int[][] matchedindices = new int[greaterlabelscount][2];
		matchedindices = this.model.pair.bookhead_new.getmatchinglabelindices(matchedindices);
		/*
		for( int i = 0; i < matchedindices.length; i++ ){
			System.out.println(i + " " + matchedindices[i][0] + " " + matchedindices[i][1]);
		}
		*/
		System.out.println("match sections" + match_section + " " + newbook_section_num);
		
		/*change starts here
		View result = new View(newbook, oldbook, chapternum_change_num, sectionnum_change_num, 
				similaritylevel1, similaritylevel2, section_match_num, compositescore_num, matchedindices);
		result.setVisible(true);
		updated print out labels here
		*/
		System.out.println("before update view");
		this.view.updateView(newbook, oldbook, chapternum_change_num, sectionnum_change_num, 
				similaritylevel1, similaritylevel2, section_match_num, compositescore_num, matchedindices);
		
		System.out.println("after update view");
	}
	
	public static void main(String[] args) {
		// load MVC model
		Model themodel = new Model();
		View theview = new View();
		Controller thecontroller = new Controller(themodel, theview);
	}//end of main
	
	/**
	 * convert text file into a string
	 * @param str a string containing text file name
	 * @return book_string_new 
	 */
	public static String inputFileToString(String str){
		String book_string_new = null;//return parameter, a string
		FileInputStream book_file_new = null;
		String temp_line = null;
		BufferedReader bufferedReader = null;
		StringBuilder stringBuilder = new StringBuilder();
		InputStreamReader inputStreamReader = null; 
		
		try{
			book_file_new = new FileInputStream(str);
			inputStreamReader = new InputStreamReader(book_file_new, "UTF-8");
			bufferedReader = new BufferedReader(inputStreamReader);
			
			while((temp_line = bufferedReader.readLine())!= null){
				if(temp_line.equals("")){
					stringBuilder.append(" ");
				}
				else{
					stringBuilder.append(temp_line);
				}	
			}//end of while loop
			
			book_string_new = stringBuilder.toString();
			if( !Character.isDigit( book_string_new.charAt(0) ) && !Character.isLetter( book_string_new.charAt(0) ) ){
				book_string_new = book_string_new.substring(1); 
			}
		}	
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		catch(IOException e){
			System.out.println(e);
		}//end of try-catch block
		
		return book_string_new;
	}
	
	/**
	 * split string into tokens, separated by whitespace
	 * @param in_str
	 * @return string array out_str_array
	 */
	public static String[] tokenizeFileString(String in_str){
		String[] out_str_array = in_str.split("\\s+");
		return out_str_array; 
	}//end of tokenizeFileString
	
}//end of toc

