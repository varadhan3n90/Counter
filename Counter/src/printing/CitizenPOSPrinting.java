package printing;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Properties;

import jpos.JposException;
import jpos.POSPrinter;
import jpos.POSPrinterConst;

public class CitizenPOSPrinting {
	
	static CitizenPOSPrinting citizenPrinter = new CitizenPOSPrinting();
	
	public void printToken(String tokenNumber) {
		String PrinterDevice="CITIZEN S310II USB Windows";
		boolean claimed = false;
		POSPrinter printer = new POSPrinter();
		try {
			printer.open(PrinterDevice);
			printer.claim(1000);
			claimed = true;
			//System.out.println("Device claimed");
			printer.setDeviceEnabled(true);
			printer.printNormal(2,"\033|cAWelcome to\n");
			Properties properties = new Properties();
			properties.load(new FileInputStream(new File("Resources/serverstart.properties")));
			String logoFileName = properties.getProperty("logoFile");			
			String logoFile = new File(logoFileName).getAbsolutePath().replace('\\', '/');
			//System.out.println(logoFile);
            printer.setBitmap(1, 2, "file:/"+logoFile, POSPrinterConst.PTR_BM_ASIS, -1);
            printer.transactionPrint(2, POSPrinterConst.PTR_TP_TRANSACTION);
			printer.printNormal(2, "\033|cA\033|1B\n");			            
            Date d = new Date();
            String today = d.toString();
            printer.printNormal(2,"\033|cATime: "+today+"\n");
            printer.printNormal(2,"\033|cAService Request Number\n");
            printer.printNormal(2, "\033|cA\033|3C"+tokenNumber);
            printer.printNormal(2, "\n\n\n\n\n");
            printer.transactionPrint(2, POSPrinterConst.PTR_TP_NORMAL);
            printer.cutPaper(100);
			
            System.out.println("Printing successful");
		} catch(Exception e) {
			e.printStackTrace();
		}
		finally{
			if(claimed){
				try {
					printer.setDeviceEnabled(false);
					printer.release();
					printer.close();
				} catch (JposException e) {					
					e.printStackTrace();
				}					
			}
		}
	}
	
	/** Test code working properly */
	/*
	public static void main(String[] args){
		new CitizenPOSPrinting().printToken("1");
	}
	*/
	
	/** Single ton object */
	private CitizenPOSPrinting(){
		
	}
	
	public static CitizenPOSPrinting getInstance(){
		return citizenPrinter;
	}
}
