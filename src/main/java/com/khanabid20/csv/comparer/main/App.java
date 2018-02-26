package com.khanabid20.csv.comparer.main;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

/**
 * This class runs the whole project
 * 
 * @author abid.khan
 *
 */
public class App {

	static CSVPrinter csvPrinter;
	private static String FILE_NAME1, FILE_NAME2, NEW_FILE_NAME;
	static Logger log = Logger.getLogger(App.class);

	public static void main(String[] args) throws IOException {

		try {
			if(!(args.length<3)){
				FILE_NAME1= args[0];
				FILE_NAME2 = args[1];
				NEW_FILE_NAME = args[2];
				
				if(!FILE_NAME1.endsWith(".csv")||!FILE_NAME2.endsWith(".csv")||!NEW_FILE_NAME.endsWith(".csv")){
					System.out.println("Not a csv file(s)...");
					log.error("Not a csv file(s)..!");
					System.exit(0);
				}
			}
			else{
				log.error("Provide all input files...!!");
				System.err.println("Provide input csv file(s)..");
				System.exit(0);
			}
		} catch (Exception e) {
			log.error("Not provided all required input files..");
			System.out.println("Less no. of arguments..\n arg1: old_file path\n arg2: updated_file path\n arg3: new_file path");
			System.exit(0);
		}
		
		log.info("Reading csv files into CSV Records.");
		List<CSVRecord> listRecord1 = readCsvIntoRecords(args[0]);
		List<CSVRecord> listRecord2 = readCsvIntoRecords(args[1]);

		showContents(listRecord1, args[0]);
		showContents(listRecord2, args[1]);

		compareCSVs(listRecord1, listRecord2);
		log.info("Done.");
	}

	/**
	 * This method compares two csv files and write differences in third file
	 * 
	 * @param file1_Records
	 * @param file2_Records
	 * @throws IOException
	 */
	private static void compareCSVs(List<CSVRecord> file1_Records, List<CSVRecord> file2_Records)
			throws IOException {

		csvPrinter = new CSVPrinter(new FileWriter(NEW_FILE_NAME), CSVFormat.INFORMIX_UNLOAD_CSV);
		csvPrinter.printComment("Compared....");

		if (file1_Records.isEmpty() && file2_Records.isEmpty()) {
			System.out.println("Files are empty.");
		} 
		else {
			System.out.println();
			if(file2_Records.size()<=1){
				csvPrinter.printRecords(file1_Records);
			}
			else{
				csvPrinter.printRecord(file2_Records.get(0));
				for (CSVRecord file2_record : file2_Records) {
					
					//count :: number of column values matched
					int count = 0;
					
					for (CSVRecord file1_record : file1_Records) {
						//columns comparison
						for (int i = 0; i < file2_record.size(); i++) {
							if (file2_record.get(i).equals(file1_record.get(i))) {
								count++;
							} 
							else {
								break;
							}
						}

					}
					
					// skipping first row(headers)
					if (file2_record.getRecordNumber()!=1) {
						
						// count=0 means no columns matched in each record
						if(count==0){
							System.out.println("record "+(file2_record.getRecordNumber()-1)+" is newly added in file:"+FILE_NAME2);
							csvPrinter.printRecord(file2_record);
						}
						//count = no. of columns >> means same record found
						else if (count == file2_record.size() - 1) {
							System.out.println("record " + (file2_record.getRecordNumber()-1) + " of file:" + FILE_NAME1
									+ " is same in file:" + FILE_NAME2);
						}
						//means only few column is matched -> updated record
						else if (count>0 && count < file2_record.size()-1) {
							System.out.println("record "+(file2_record.getRecordNumber()-1)+" of file:"+FILE_NAME2+" is updated record");
							csvPrinter.printRecord(file2_record);
						} 
						
					}

				}
				

			}
		}
		csvPrinter.close();
	}

	/**
	 * reading each records from list of CSVRecord and displaying it's content
	 * 
	 * @param listRecords
	 * @param filePath
	 */
	private static void showContents(List<CSVRecord> listRecords, String filePath) {
		System.out.println("\nFile " + filePath + " content -->> \n");

		for (CSVRecord record : listRecords) {
			for (String s : record)
			System.out.printf("%-20s", s);
			System.out.println();
		}
	}

	/**
	 * This method read csv file and returns as list of csv record
	 * 
	 * @param filePath
	 * @return
	 */
	private static List<CSVRecord> readCsvIntoRecords(String filePath) {
		List<CSVRecord> csvRecordList = null;
		try {
			CSVParser csvParser = CSVFormat.EXCEL.parse(new FileReader(filePath));
			csvRecordList = csvParser.getRecords();
			csvParser.close();
		} catch (Exception e) {
			log.error("Unable to parse csv file");
			System.err.println("File Not Found");
		}
		return csvRecordList;
	}

}
