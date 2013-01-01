package serialProgram;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;

public class ButtonRead8051 {
    void connect ( String portName ) throws Exception
    {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
            
            if ( commPort instanceof SerialPort )
            {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                
                InputStream in = serialPort.getInputStream();
                //OutputStream out = serialPort.getOutputStream();
                while(true){				
				byte[] bs = new byte[1];
				in.read(bs);
				String st = new String(bs).trim();
				if(st!=null&&!st.equals(""))
				System.out.println("read: "+new String(bs));
				
				}
                //(new Thread(new SerialReader(in))).start();
                //(new Thread(new SerialWriter(out))).start();

            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }     
    }
    
    /** */
    public static class SerialReader implements Runnable 
    {
        InputStream in;        
        public SerialReader ( InputStream in ){
            this.in = in;
        }        
        public void run (){
			//System.out.println("Going to start reading");
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

    /*
    public static void main ( String[] args )
    {
        try
        {
            (new ButtonRead8051()).connect("COM1");
        }
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    */


}
