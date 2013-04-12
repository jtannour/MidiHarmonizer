
/*
 * This assignment relied heavily on Peter Elsea's ideas on how to
 * represent music for musical compositions
 * All fuzzy set fiddling to find the proper intervals were taken from
 * the paper referenced in the project report.
 * 
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.sound.midi.InvalidMidiDataException;

import org.jfugue.Pattern;
import org.jfugue.Player;


public class Harmonizer {
	
	//a pitch set consists of 12 possible values to make a note
	//the set goes from C to B
	double pitchSet[]; 
	
	//these are our major and minor scales
	final static double[] MAJSCALE = {1,0,1,0,1,1,0,1,0,1,0,1};
	
	//All song demonstrations use the Major Scale.
	double currentScale[] = MAJSCALE;
	int TONIC = 0;
	
	//our up intervals
	final static double[] f2 = {0,0.9,1,0,0,0,0,0,0,0,0,0}; 	//second above
	final static double[] f3 = {0,0,0,1,0.9,0,0,0,0,0,0,0};		//third above
	final static double[] f4 = {0,0,0,0,0,1,0.5,0,0,0,0,0};		//4th above
	final static double[] f5 = {0,0,0,0,0,0,0.9,1,0.5,0,0,0};	//5th above
	final static double[] f6 = {0,0,0,0,0,0, 0,0,1,0.9,0,0};	//6th above
	final static double[] f7 = {0,0,0,0,0,0,0,0,0,0,0.9,1};		//7th above
		
	//our down intervals
	final static double[] f2B = {0,0,0,0,0,0,0,0,0,0,1,0.9};	//2nd below
	final static double[] f3B = {0,0,0,0,0,0,0,0,0.9,1,0,0};	//3rd below
	final static double[] f4B = {0,0,0,0,0,0,0.5,1,0,0,0,0};	//4th below
	final static double[] f5B = {0,0,0,0,0.5,1,0.9,0,0,0,0,0};	//5th below
	final static double[] f6B = {0,0,0.9,1,0,0, 0,0,0,0,0,0};	//6th below
	final static double[] f7B = {0,0.9,1,0,0,0,0,0,0,0,0,0};	//7th below
	
	
	//Here are our pitches
	public static int PC_C = 0;
	public static int PC_DFLAT = 1;
	public static int PC_D = 2;
	public static int PC_EFLAT = 3;
	public static int PC_E = 4;
	public static int PC_F = 5;
	public static int PC_GFLAT = 6;
	public static int PC_G = 7;
	public static int PC_AFLAT = 8;
	public static int PC_A = 9;
	public static int PC_BFLAT = 10;
	public static int PC_B = 11;
	
	//map the numeric values of an octave to it's midi value
	static Map<String, Integer> midiValues;
	
	PlaySong ps ;		//this is the player that takes care of playing the songs
	public Harmonizer(){
		 ps = new PlaySong();

		
		midiValues = new HashMap<String, Integer>();
		int value =0;
		int row = 0;
		int octave =0;
		String note = "C";
		for(int i=0; i < 121; i++){
			
			if(i%11 == 0 && i !=0){
				row++;
				octave =0;
				switch(row){
				case 1:
					value = 1;
					note="C#/Db";
					break;
				case 2:
					value = 2;
					note="D";
					break;
				case 3:
					value = 3;
					note="D#/Eb";
					break;
				case 4:
					value = 4;
					note="E";
					break;
				case 5:
					value = 5;
					note="F";
					break;
				case 6:
					value = 6;
					note="F#/Gb";
					break;
				case 7:
					value = 7;
					note="G";
					break;
				case 8:
					value = 8;
					note="G#/Ab";
					break;
				case 9:
					value = 9;
					note = "A";
					break;
				case 10:
					value = 10;
					note = "A#/Bb";
					break;
				case 11:
					value = 11;
					note = "B";
					break;
				}

			}
			midiValues.put(note+String.valueOf(octave), value);
			value+=12;
			octave++;
		}
		
	}
	
	
	/**
	 * creates a pitch set from an array entry like (6,5,2) = 0,0,1,0,0,1,1,0,0,0,0,0,0
	 * @param values
	 * @return
	 */
	public static double[] createPitchSet(int values[]){
		
		if(values.length >= 12){
			System.err.println("Sorry but a pitch set has a max of 12 values");
		}
		else{
			
			for(int i=0; i < values.length; i++){
				if (values[i] >= 12){
					System.err.println("Sorry but a pitch cannot be more than 12");
					return null;
				}
			}
		}
		
		
		double ps[] = new double[12];
		
		for(int i=0; i < ps.length; i++){
			ps[i] = 0;
		}
		
			
		for(int i=0; i < values.length; i++){
			ps[values[i]] = 1;
		}
		
		return ps;
	}
	
	/**
	 * 
	 * We can rotate our set to get a different scale
	 * the value provided is by how much we are shifiting
	 * @param rotateValue
	 * @param set
	 */
	public static double[] rotatePitch(int rotateValue, double set[]){ 

		double[] newSet = new double[set.length];
		for(int i=0; i < newSet.length; i++){
			newSet[i] = 0;
		}

		for(int i= set.length-1; i >=0; i--){
			
			if(set[i] > 0 && !(i + rotateValue > set.length-1 ||  i + rotateValue < 0)){
				newSet[i+rotateValue] = set[i];
			}
			else if (set[i] > 0 && i + rotateValue > set.length-1){
				newSet[rotateValue+i - set.length] = set[i];
				
			}
			else if(set[i] > 0 && rotateValue + i< 0){
				newSet[set.length + (rotateValue+i)] = set[i];
			}
			else if (set[i] ==0){
				
			}
			else{
				System.err.println("Something went wrong with the rotation");
				System.err.println("set[i] = " + set[i]);
		
			}
		}
		
		//printSet(newSet);
		return newSet;
		
	}
	

	/**
	 * returns a list of the highest indices in the pitch set
	 * @param set
	 * @return
	 */
	public List<Integer> getTopSet(double set[]){
		
		List<Integer> indexList = new ArrayList<Integer>();
		
		for(int i=0; i < set.length; i++){
			
			if(set[i] > 0){
				indexList.add(i);
			}
			
		}
		
		return indexList;
		
	}
	
	
	/**
	 * returns the highest valued index
	 * @param set
	 * @return
	 */
	public static int getTopIndex(double set[]){

		int maxIndex = 0;
		double maxValue =0;
		for(int i=0; i < set.length; i++){
			
			if(set[i] > maxValue){
				maxValue = set[i];
				maxIndex = i;
			}
			
		}
		
		return maxIndex;
		
	}
	
	/**
	 * To make notes playable, move to MIDI regions
		Since chords are constructed from pitch classes, they will not be in ascending order.
		An Amin, for instance looks like (9 0 4 ).
		Add 12 to the first two to get a root position.
		Then add the desired octave.
	 * @param theList
	 * @return
	 */
	public static double[] Ascend_List(double[] theList){
		
		int root = 0;
		for(int i=0; i < theList.length; i++){
			
			if(theList[i] < theList[root]){
				double toEvaluate = theList[i] + 12;
				theList[i] =  toEvaluate;
			}
		}
		
		
		return theList;
	}
	
	
	public static double[] add_Octave (double[] theList, int octave){
		
		for(int i=0; i < theList.length; i++){
			
			double newValue = theList[i] + (octave* 12);
			theList[i] = newValue;
			
		}
		
		theList = Ascend_List(theList);
		
		return theList;
	}
	
	
	
	
	public static void printSet(double set[]){
		if(set == null)
			return;
		
		System.out.print("\n[  ");
		for(int i=0; i < set.length; i++){

			System.out.print(set[i] + "  " );
		}
		System.out.print(" ]");
		System.out.println();
	}
	
	
	
	
	
	//this is our fuzzy chord builder
	public  int SECOND_ABOVE(int note){return getTopIndex(Fuzzy.getIntersection(rotatePitch(note, f2), currentScale));}
	
	public  int THIRD_ABOVE(int note){return getTopIndex(Fuzzy.getIntersection(rotatePitch(note, f3), currentScale));}
	
	public  int FOURTH_ABOVE(int note){return getTopIndex(Fuzzy.getIntersection(rotatePitch(note, f4), currentScale));}
	
	public  int FIFTH_ABOVE(int note){return getTopIndex(Fuzzy.getIntersection(rotatePitch(note, f5), currentScale));}
	
	public  int SIXTH_ABOVE(int note){return getTopIndex(Fuzzy.getIntersection(rotatePitch(note, f6), currentScale));}
	
	public  int SEVENTH_ABOVE(int note){return getTopIndex(Fuzzy.getIntersection(rotatePitch(note, f7), currentScale));}
	
	
	//now for our intervals down
	public  int SECOND_BELOW(int note){return getTopIndex(Fuzzy.getIntersection(rotatePitch(note, f2B), currentScale));}
	
	public  int THIRD_BELOW(int note){return getTopIndex(Fuzzy.getIntersection(rotatePitch(note, f3B), currentScale));}
	
	public  int FOURTH_BELOW(int note){return getTopIndex(Fuzzy.getIntersection(rotatePitch(note, f4B), currentScale));}
	
	public  int FIFTH_BELOW(int note){return getTopIndex(Fuzzy.getIntersection(rotatePitch(note, f5B), currentScale));}
	
	public  int SIXTH_BELOW(int note){return getTopIndex(Fuzzy.getIntersection(rotatePitch(note, f6B), currentScale));}
	
	public  int SEVENTH_BELOW(int note){return getTopIndex(Fuzzy.getIntersection(rotatePitch(note, f7B), currentScale));}
	
	
	
	/**
	 * Build a given note as the root
	 * @param theNote
	 * @return
	 */
	public  int[] buildAsRoot(int theNote){
		
		int []chords = new int[3];
		
		
		chords[0] = theNote;
		chords[1] = THIRD_ABOVE(theNote);
		chords[2] = FIFTH_ABOVE(theNote);
		
		return chords;
	}
	
	/**
	 * Build the given note as third
	 * @param theNote
	 * @return
	 */
	public  int[] buildAsThird(int theNote){
		
		int []chords = new int[3];
		
		chords[0] = THIRD_BELOW(theNote);
		chords[1] = theNote;
		
		chords[2] = FIFTH_ABOVE(THIRD_BELOW(theNote));
		
		return chords;
	}
	

	/**
	 * Build the given note as fifth
	 * @param theNote
	 * @return
	 */
	public  int[] buildAsFifth(int theNote){
	
		int []chords = new int[3];
		
		chords[0] =FIFTH_BELOW(theNote);
	
		chords[1] =THIRD_ABOVE(FIFTH_BELOW(theNote));
		chords[2] = theNote;
		
		return chords;
	}

	
	////////////This is where all the fuzzy and defuzzification happens////////////////
	
	/*
	(1 0 0) would indicate use of the given pitch as the root.
	(0 1 0) would indicate use of the given pitch as the third.
	(0 0 1) would indicate use of the given pitch as the fifth.
	*/

	double SOL_SET[] = {0, 0, 0}; 		//places are (as-root as-third as-fifth)
	double LAST_SOL[] = {0, 0, 0};  	// stores inversion
	double LAST_SOL_WEIGHT =  0.6;  	// Weighting of last solution
	int LAST_PC =  0;
	double LAST_CHORD[] = {0, 4, 7};
	int LAST_ROOT =  7;
	int THIS_ROOT =  0;
	double OLD_CHORD_SET[] =  {0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1}; 	// prime with G this affects how piece will start
	double AS_ROOT_SET[]  = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	double AS_THIRD_SET[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};   
	double AS_FIFTH_SET[] = {0, 0, 0, 0, 0,0,0 ,0 ,0 ,0, 0, 0};
	
	
	/*
	common tone rules
	If root position keeps common tones, then root position.
    If first inversion keeps common tones, then first inversion.
    If second inversion keeps common tones, then second inversion.
    If last position was root, then first inversion or second inversion.
    If there have been too many firsts in a row, then root or second.
    If there have been too many seconds in a row, then root or first.
	*/
	
	public void setTrialSets(int theNote){
		AS_ROOT_SET = createPitchSet(buildAsRoot(theNote));
		AS_THIRD_SET = createPitchSet(buildAsThird(theNote)); 
		AS_FIFTH_SET = createPitchSet(buildAsFifth(theNote));
	}
	
	
	public static int commonTones(double[] set1, double[] set2){
		double[] intersection = Fuzzy.getIntersection(set1, set2);
		
		int sum = 0;
		for(int i=0; i < intersection.length; i++){
			sum += intersection[i];
		}
		
		return sum;
	}
	
	
	public static double[] commonToneRules(double[] set1, double[] set2, double[] set3, double[] master){
		int [] result = {0,0,0};
		
		result[0] = commonTones(set1, master);
		result[1] = commonTones(set2, master);
		result[2] = commonTones(set3, master);
		
		return Fuzzy.deffuzify(result);
		
	}
	
	
	public double[] lastSolTest(){
		int test = getTopIndex(LAST_SOL);
		
		if(test == 0){
			double sol[] = {0,1,1};
			return sol;
		}
		else if (test == 1){
			double sol[] = {0,1,1};
			return sol;
		}
		else{
			double sol[] = {1,0,0};
			return sol;
		}
		
	}
	
	public int[] pickChord(int theNote){
		
		theNote = theNote % 12;
		
		AS_ROOT_SET = createPitchSet(buildAsRoot(theNote));
		AS_THIRD_SET = createPitchSet(buildAsThird(theNote));
		AS_FIFTH_SET = createPitchSet(buildAsFifth(theNote));
		
		double[] solution1 = commonToneRules(AS_ROOT_SET, AS_THIRD_SET, AS_FIFTH_SET, Fuzzy.crisp(OLD_CHORD_SET));
		
		double[] solution2 = lastSolTest();
		
		double[] theSolution = new double[solution1.length + solution2.length];
		
		int solutionI = 0;
		boolean doneFirst = false;
		for(int i=0; i < theSolution.length; i++){
			
			if(solutionI > 2){
				solutionI = 0;
				doneFirst = true;
			}
			
			if(doneFirst) 	theSolution[i] = solution1[solutionI];
			else			theSolution[i] = solution2[solutionI];
		}
		
		LAST_SOL = theSolution;
		
		int [] result;

		switch (getTopIndex(LAST_SOL)){
			case 0: result = buildAsRoot(theNote);
					break;
			case 1: result = buildAsThird(theNote);
					break;
			case 2: result = buildAsFifth(theNote);
					break;
			default: result = buildAsRoot(theNote);
		}
		
		OLD_CHORD_SET = createPitchSet(result);
		
		THIS_ROOT = result[0];
		
		return result;
		
	}
	
	
	int howManyRoots =  0;
	double[] TooManyRoots =  {0, 0.2, 0.6, 1, 1, 1, 1, 1};
	int howMany6_4 =  0;
	double[]  tooMany6_4 =  {0, 1, 1, 1, 1, 1, 1, 1, 1, 1};
	int  howMany6 =  0;
	double[] tooMany6 =  {0, 0.2, 0.6, 1, 1, 1, 1, 1, 1};
	
	public static int countCommon(double[] list1, double[] list2){
		
		int commonCount = 0;
		
		if(list1.length != list2.length){
			System.err.println("CountCommon cannot perform on lists of different sizes");
		}
		
		for(int i=0; i < list1.length; i++){
			
			if(list1[i] == list2[i]){
				commonCount++;
			}
		}
		
		return commonCount;
		
	}
	

	
	public int updateCounts(int inversions){
		
		int count = 0;
		switch(inversions){
		
		case 0: 
			count = howManyRoots = howManyRoots++;
			howMany6_4 = 0;
			howMany6 = 0;
			break;
		case 1:
			howManyRoots = 0;
			count = howMany6_4 = howMany6_4++;
			howMany6 = 0;
			break;
		case 2:
			howManyRoots = 0;
			howMany6_4 = 0;
			count = howMany6 = howMany6++;
			break;
				
		}
		
		return count;
		
	}
	
	public boolean checkForFifth(double a, double b){
		
		double s = a - b;
		
		return (s == 7 && LAST_ROOT == LAST_CHORD[0]);
		
	}
	
	public double[] invSet(double[] chordList){
		
		
		int[] list = new int[3];

		list[0] = countCommon(LAST_CHORD, chordList);
		list[1] = countCommon(LAST_CHORD, rotatePitch(1, chordList));
		list[2] = countCommon(LAST_CHORD, rotatePitch(2, chordList));
		
		return Fuzzy.deffuzify(list);
	}
	
	public double[] makeInversion(double[] chordList){
		
		double root = THIS_ROOT;
		
		double [] inverSet1 = invSet(chordList); //rule 1
		double list[] = {0, 0.8, 1};
		double[] l = {1,0,1};
		double[] l2 = {1,0.8,0}; 
		
		double[] inverSet2 = Fuzzy.rule(howManyRoots, TooManyRoots, list); //rule 2
		double[] result = addLists(inverSet1, inverSet2);
		
		double[] inverSet3 = Fuzzy.rule(howMany6_4, tooMany6_4,l); //rule 3
		result = addLists(result, inverSet3);
		
		double[] inverSet4 = Fuzzy.rule(howMany6, tooMany6,l2); //rule 4
		result = addLists(result, inverSet4);
		
		if(checkForFifth(root, LAST_ROOT)){
			double[] inverSet5 = {1,0,0};
			result = addLists(result, inverSet5);
		}
		if(TONIC == root){
			double[] inverSet6 = {0.2,0,0};
			result = addLists(result, inverSet6);
		}
		

		LAST_ROOT = (int)root;
		LAST_CHORD = rotatePitch(updateCounts(getTopIndex(result)), chordList);
		
		return LAST_CHORD;
	}
	
	public static double [] addLists (double[] list1, double[] list2){
		
		int max = 0;
		if(list1.length > list2.length){max = list1.length;}
		else{max = list2.length;}
		
		double[] newList = new double[max];
		
		for(int i=0; i < max; i++){
		
			if(i > list1.length-1){
				newList[i] =  list2[i];
			}
			else if(i > list2.length-1){
				newList[i] =  list1[i];
			}
			else{
				newList[i] = list1[i] + list2[i];
			}
		}
		
		return newList;
	}
	
	public List<Integer> Harmonize(double[] pitchList){

		List<Integer> result = new ArrayList<Integer>();
	//	System.out.println("Starting");
		for(int i=0; i < pitchList.length; i++){
	//		System.out.println("and npw" + i);
			int temp[] = pickChord((int) pitchList[i]);
			
			double[] inver = new double[temp.length];
			
			for(int j=0; j< inver.length-1; j++){
				
				inver[j] = temp[j];
			}
			 double[] temp2 = add_Octave(Ascend_List(makeInversion(inver)), 4);
			 for(int k=0; k < temp2.length; k++){
				 result.add((int)temp2[k]);
			 }
		}
		return result;
	}
	
	public  String midiConvertor(Pattern p){
		
		String notes = p.toString();
		String pattern = "([^a-gA-G][0-9])";
		notes = notes.replaceAll(pattern, "");
		
		
		StringTokenizer token = new StringTokenizer(notes, " q i h 0");
		ArrayList<Integer> midiSongs = new ArrayList<Integer>();
		while(token.hasMoreTokens()){
		//	System.out.println("woot" + token.nextToken());
			String s = token.nextToken();
			if(s.matches("[a-gA-G][0-9]"))
				midiSongs.add(midiValues.get(s));
		}
		
	//	System.out.println(" = " + midiSongs);
		double[] doubleMidi = new double[midiSongs.size()];
		
		String theSong="";
		for(int i=0; i< midiSongs.size(); i++){
			theSong += "["+midiSongs.get(i)+"] ";
			doubleMidi[i] = midiSongs.get(i);
			
		}
		
		originalTune = doubleMidi;
	
		return theSong;
	}
	
	//Has to stop player here or else freezes up
	public void stopSong(){

		if(ps.getPlayer().isPlaying()){
			ps.getPlayer().close();
		}

	}
	
	
	public void playOriginal(){
		
		String theSong="";
		for(int i=0; i < originalTune.length; i++){
			theSong += "["+(int)originalTune[i]+"] ";
		}
		
		//player.play(theSong);
		ps.setMusicString(theSong, true);
	}
	
	public void justPlayHarmony(){
		
		List<Integer> whoa = Harmonize(originalTune);

		Iterator<Integer> it = whoa.iterator();
		String musicString = "";
		int i =0;
		while(it.hasNext()){
			if(i > 2) i=0;
			musicString += "V"+i+" ["+String.valueOf(it.next())+"] ";
			i++;
		}
		
	//	player.play(musicString);
		ps.setMusicString(musicString, true);
	}
	
	public void playHarmonizedTune(){
		
		List<Integer> whoa = Harmonize(originalTune);

		Iterator<Integer> it = whoa.iterator();
		String musicString = "";
		int i =0;
		while(it.hasNext()){
			if(i > 2) i=0;
			musicString += "V"+i+" ["+String.valueOf(it.next())+"] ";
			i++;
		}
		
		String theSong="";
		for(int iter=0; iter < originalTune.length; iter++){
			theSong += "["+(int)originalTune[iter]+"] ";
		}

		//player.play(musicString + "V3 " + theSong);
		
		ps.setMusicString(musicString + "V3 " + theSong, true);
		
	}
	double[] lowOctave = {2, 4, 5, 7, 5, 4, 2, 4, 7, 9, 11, 12, 11, 9, 7, 9, 11, 2, 4, 7, 9, 7, 9, 11, 2, 4, 7, 9, 7, 5, 4, 5, 4};
	double[] lBridge = {67, 69, 67, 65, 64, 65, 67, 62, 64, 65, 64, 65, 67, 67, 69, 67, 65, 64, 65, 67, 62, 67, 60};
	double[] originalTune = lBridge;
	
	public void setOriginalTune(String name, File midiFile){
		
		if(name.equals("London Bridges")){
			originalTune = lBridge;
		}
		else if (name.equals("Low Octave Tune")){
			originalTune = lowOctave;
		}
		else{
			
			Pattern pattern = null;
			try {
				 pattern = ps.getPlayer().loadMidi(midiFile);
			} catch (IOException | InvalidMidiDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		midiConvertor(pattern);
			String ferejac = midiConvertor(pattern);
		}
			
	}
	
	public static void main(String args[]){
		
		Harmonizer h = new Harmonizer();
		
		GUI gui = new GUI(h);
		

	}

}
