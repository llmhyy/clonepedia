package ccdemon.evaluation.model;

import java.util.ArrayList;

public class DataRecord {
	
	public static int recordTime = 0;
	
	public static ArrayList<Long> focusIntervals = new ArrayList<Long>();
	public static int toNextTime = 0;
	public static int toPrevTime = 0;
	public static int manualEditTime = 0;
	
	public static void clear(){
		focusIntervals = new ArrayList<Long>();
		toNextTime = 0;
		toPrevTime = 0;
		manualEditTime = 0;
	}
}
