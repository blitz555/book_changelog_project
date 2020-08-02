import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;
import org.apache.commons.lang3.*;

/**
 * Constants class contains all constants for tokentypes
 * @author Z
 *
 */
public class Constants {
	//category 1 keywords
	public static final int NOTYPE = 0;
	public static final int KEY_PART = 1;
	public static final int KEY_CHAPTER = 2;
	public static final int KEY_APPENDIX = 3;
	//category 2 division numbers
	public static final int NUM_NOTYPE = 10;
	public static final int NUM_PART = 11;
	public static final int NUM_CHAPTER = 12;
	public static final int NUM_APPENDIX = 13;
	public static final int NUM_SUBCHAPTER = 14;
	//category 3 page numbers
	public static final int PAGE_REGULAR = 20;
	public static final int PAGE_PART = 21;
	public static final int PAGE_CHAPTER = 22;
	public static final int PAGE_APPENDIX = 23;
	public static final int PAGE_ENDMATTERS = 24;
	//category 4 miscellaneous
	public static final int WORD_REGULAR = 30;
	public static final int WORD_D1 = 31;//d1 is, a type of 2 tokens in line 
	public static final int D1 = 32;//delimiter ., usually appear on second token in line
	public static final int WORD_FRONTMATTERS = 38;//word, front matters
	public static final int WORD_ENDMATTERS = 39;//word, end matters
	//category 5 exceptions not currently handled
	public static final int NOTYPE_2TOKENS = 40;//2 tokens in line no particular type
	
	/**
	 * separate one list of sections by level into levels
	 * @param thelist arraylist of all sections in order of appearance
	 * @return newlevels save sections by level
	 */
	public static ArrayList<ArrayList<Section>> createlevels( ArrayList<Section> thelist ){
		ArrayList<ArrayList<Section>> newlevels = new ArrayList<ArrayList<Section>>(); 
		for(int i = 0; i < thelist.size(); i++){//add every section in list into different levels
			int level = thelist.get(i).getlevel();//get level of incoming section
			if( level - newlevels.size() > 0){//more levels than rows currently in 2D array, initialize more rows 
				for(int j = 0; j < level - newlevels.size(); j++){//add new levels
					newlevels.add( new ArrayList<Section>());
				}
			}
			newlevels.get(level-1).add( thelist.get(i) );//add section to level
		}
		return newlevels;
	}
}

/**
 * check if last char in string is a delimiter
 * @param in_str
 * @return true/false

public static boolean containsEndDelimiter(String in_str){
	String endchar = in_str.substring( in_str.length() - 1 );//obtain index of last character
	if( endchar.matches(".*[\\.].*") ){//checks for . in token
		return true;
	}
	return false;
} */

