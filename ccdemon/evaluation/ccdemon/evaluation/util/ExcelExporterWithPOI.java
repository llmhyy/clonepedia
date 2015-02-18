package ccdemon.evaluation.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelExporterWithPOI {
	Workbook book; 
	Sheet sheet;

	public void start(){
		book = new XSSFWorkbook(); 
		sheet = book.createSheet("data");  
        Row row = sheet.createRow((short) 0); 
        //title
        String titles[] = {"cloneSetID","instanceNum","avgLineNum","typeIIorIII","type1to7","recall","precision","configurationEffort","savedEditingEffort","trialTime","diffTime","APITime","cloneInstance"};
        for(int i = 0; i < titles.length; i++){
        	row.createCell(i).setCellValue(titles[i]); 
        }
	}
	
	public void export(ArrayList<String> datas, int lineNum){
		Row row = sheet.createRow(lineNum+1);
        try {
        	for(int j = 0; j < datas.size(); j++){
        		row.createCell(j).setCellValue(datas.get(j));
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void end(String filename){
		try {
			FileOutputStream fileOut = new FileOutputStream(filename + ".xlsx");
			book.write(fileOut); 
			fileOut.close(); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
        System.out.println("excel export done");
	}

}
