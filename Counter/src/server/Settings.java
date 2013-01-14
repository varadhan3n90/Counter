package server;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.filechooser.FileFilter;

import utility.ConfigurationReader;
import utility.FileCopy;

public class Settings extends JFrame implements Serializable,ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6663102714214096970L;
	
	JLabel languageLabel;
	String[] availableLanguages;
	JButton OK;
	JButton removeAd;
	JButton selectFilesButton;
	JComboBox<String> languageList;
	JLabel uploadAddLabel;
	JFileChooser fc ;
	JList<String> existingAds;
	Vector<String> adNames;
	
	private void initialize(){
		languageLabel = new JLabel("Select your language.");
		uploadAddLabel = new JLabel("Choose files for ads ");
		selectFilesButton = new JButton("Browse");
		fc = new JFileChooser();
		fc.setFileFilter(new ImageFilter());
		fc.setAcceptAllFileFilterUsed(false);
		this.add(languageLabel);
		OK = new JButton("Apply");
		removeAd = new JButton("Remove");
				
		removeAd.addActionListener(this);
		OK.addActionListener(this);		
		languageList = new JComboBox<String>(availableLanguages);
		existingAds = new JList<String>(adNames);
		existingAds.setVisibleRowCount(5);
		//existingAds.
		this.add(languageList);
		this.add(uploadAddLabel);
		this.add(selectFilesButton);
		this.add(existingAds);
		selectFilesButton.addActionListener(this);
		this.add(removeAd);
		this.add(OK);		
	}
	
	
	private void setBackToMainScreen(){
		WindowListener listner = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we){				
				if(Server.myServer!=null){
					Server.myServer.setVisible(true);					
				}
				dispose();
			}			
		};
		this.addWindowListener(listner);
	}
	
	public Settings(){
		adNames = new Vector<String>();
		File folder = new File("Resources/ads");
		FilenameFilter ff = new FilenameFilter() {			
			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.endsWith("jpg");
			}
		};
		File f[] = folder.listFiles(ff);
		for(int i=0;i<f.length;i++){
			adNames.add(f[i].getName());
		}
        
		setBackToMainScreen();
		this.setTitle("QMS Settings");
		this.setSize(200, 200);
		this.setLayout(new FlowLayout());
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		String defaultLanguage = ConfigurationReader.getServerInit("language");
		availableLanguages = (defaultLanguage+","+ConfigurationReader.getServerInit("availableLanguages")).split(",");		
		initialize();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource()==selectFilesButton){
			int returnVal;
			returnVal = fc.showDialog(this, "Attach");
			System.out.println("Browse returned "+returnVal+" "+JFileChooser.APPROVE_OPTION);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				// System.out.println("Performing copy options");
	            File[] ads = {fc.getSelectedFile()};
	            System.out.println("total Selected files "+ads.length);
	            //This is where a real application would open the file.
	            for(int i=0;i<ads.length;i++){
	            	File newFile = new File("Resources/ads/"+ads[i].getName());
	            	try {
	            		System.out.println("Copying files for ads "+ads[i].getName());
						FileCopy.copyFile(ads[i],newFile);
					} catch (IOException e) {						
						e.printStackTrace();
					}	            	
	            	adNames.add(ads[i].getName());
	            }
	            existingAds.updateUI();
	        } else {
	            // log.append("Open command cancelled by user." + newline);
	        }
		}else if (arg0.getSource()==removeAd){
			int index = existingAds.getSelectedIndex();
			String fileName = adNames.remove(index);
			File f = new File("Resources/ads/"+fileName);
			if(f.exists())
				f.delete();
			existingAds.updateUI();
		}else if (arg0.getSource()==OK){
			String defaultLanguage = (String) languageList.getSelectedItem();
			System.out.println("Changing language: "+defaultLanguage);
			ConfigurationReader.setServerInit("language", defaultLanguage);
		}
		
	}
	
	
}


class ImageFilter extends FileFilter {

    //Accept all directories and all jpg.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = Utils.getExtension(f);
        if (extension != null) {
            if (extension.equals(Utils.jpg)) {
                    return true;
            } else {
                return false;
            }
        }

        return false;
    }

    //The description of this filter
    public String getDescription() {
        return "JPG files";
    }
}

class Utils {

    //public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    //public final static String gif = "gif";
    //public final static String tiff = "tiff";
    //public final static String tif = "tif";
    //public final static String png = "png";

    /*
     * Get the extension of a file.
     */  
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
}