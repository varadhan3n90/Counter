package serialProgram;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;


/**
 * This program uses the java RXTX library 2.71 and requires rxtxSerial.dll to be present in the java.library.path
 * The program is supposed to be same for 64 bit but a different vendor provides support.
 * @author Varadhan
 *
 */
public class ButtonRead8051 {
	
	/** The Com port to be used. */
	String portName;
	
	/**
	 * Predefined settings of 9600 baud rate. 1 stop bit and 8 data bits.
	 * @param portName example COM1, COM2
	 * @throws NoSuchPortException 
	 * @throws PortInUseException 
	 * @throws UnsupportedCommOperationException 
	 * @throws IOException 
	 * @throws Exception
	 */
	
	
    public String readPort ( ) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException {
    	
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() ){
            System.out.println("Error: Port is currently in use");
            throw new PortInUseException();
        } 
        else{            
        	CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);            
            if ( commPort instanceof SerialPort ){
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);                
                InputStream in = serialPort.getInputStream();                
                while(true){                	
					byte[] bs = new byte[1];
					in.read(bs);
					String st = new String(bs).trim();
					if(st!=null&&!st.equals(""))
						return new String(bs);
					//System.out.println("read: "+new String(bs));				
                }
            }
            else {
            	throw new UnsupportedCommOperationException();
                //System.out.println("Error: Only serial ports are handled by this example.");
            }
        }     
    }
    
    public ButtonRead8051(String portName){
    	this.portName = portName;    	
    }
    
    /** class to read from serial stream.
     *  Modified the above connect function to perform the same
     *  This class is not required separately.
     */
    
    /*
    public static class SerialReader implements Runnable {
        InputStream in;        
        public SerialReader ( InputStream in ){
            this.in = in;
        }        
        public void run (){
            byte[] buffer = new byte[1024];
            int len = -1;
            try{
                while ( ( len = this.in.read(buffer)) > -1 ){
                    System.out.print(new String(buffer,0,len));
                }
            }
            catch ( IOException e ){
                e.printStackTrace();
            }            
        }
    }
	*/

    /** Code to test 8051 serial connection.
     *  Assumes serial port is Com1.
     *  Some chinese make usb to serial controllers do not work as prolific driver is not supported.
     */
    /*
    public static void main ( String[] args ){
        try{
            (new ButtonRead8051()).connect("COM1");
        }
        catch ( Exception e ){            
            e.printStackTrace();
        }
    }
    */

}
