package ibs;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication 
public class RasberryApplication implements CommandLineRunner {
	
	private List<Path> allPubFiles = new ArrayList<>() ;
	private List<Path> allTiragesFiles = new ArrayList<>();
	private List<String> configFilesValues = new ArrayList<String>() ;
	private String listVideoAllPubFiles = "" ;
	private String twoSecondeAfter = "null" ;
	private String tenSecondeAfter = "null" ;
	private String fifteenSecondeAfter = "null" ;
	private static File loopFile = new File("/home/pi/loop.zip") ;
	private static File loop2File = new File("/home/pi/loop2.zip") ;
	private static String urlDownload = "" ;
	
	public static void main(String[] args) {
		SpringApplication.run(RasberryApplication.class, args); 
	}
	
	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Started Run method...");
		configFilesValues.add(0, "/home/pi/loop") ;
		configFilesValues.add(1, "PUB") ;
		configFilesValues.add(2, "TIRAGE") ;
		configFilesValues.add(3, "11:45:00") ;
		configFilesValues.add(4, "14:45:00") ;
		configFilesValues.add(5, "15:45:00") ;
		configFilesValues.add(6, "18:45:00") ;
		configFilesValues.add(7, "05:00:00") ;
		configFilesValues.add(8, "https://www.dropbox.com/sh/e2p3s9880pq8uix/AABpAZCzjYilcns6EN9NDAr7a?dl=1") ;
		configFilesValues.add(9, "12:00:00") ;
		configFilesValues.add(10, "15:00:00") ;
		configFilesValues.add(11, "16:00:00") ;
		configFilesValues.add(12, "19:00:00") ;
		configFilesValues.add(13, "/home/pi/loop2") ;
		configFilesValues.add(14, "4:00:00") ;
		System.out.println("---------------------------------------------------------------");
		System.out.println("Lecture Fichier de Config : ConfigFile.txt dans le meme dossier que le .jar");
		System.out.println("---------------------------------------------------------------");		
		
		/* Lecture des valeur des Variables Externes dans le fchier de config */ 
		 try {
		      File myObj = new File("ConfigFile.txt");
		      Scanner myReader = new Scanner(myObj);
		      int j = 0 ; 
		      if (myObj.exists()) {
		    	  System.out.println("Les Données de ConfigFile.txt sont donc : ");
			      while (myReader.hasNextLine()) {
			        configFilesValues.set(j, myReader.nextLine())  ;
			        System.out.println(configFilesValues.get(j).toString());
			        j++ ;
			      }
			}else {
				System.out.println("Pas de Fichier ConfigFile.txt");
			}
		      
		      System.out.println("/---------------------------------------------------------------------------------------------/");
		      myReader.close();
		    } catch (FileNotFoundException e) {
		      System.out.println("File Not Found !!!, Searching in same folder of .jar ConfigFile.txt");
		      e.printStackTrace();
		    } 
		 
		 urlDownload = configFilesValues.get(8) ;
		 
		 /************* RECUP FICHIER DANS LOOP *********************************/
		 
		System.out.println("------------------------------------------------");
		System.out.println("Liste des Videos " + configFilesValues.get(1));
		
		try (Stream<Path> paths = Files.walk(Paths.get(configFilesValues.get(0)))) {
		    paths
		        .filter(Files::isRegularFile)
		        .forEach(file -> {
		        	// allFiles.add(file) ;
		        	String fileName = "" + file.getFileName() ;
		        	if (fileName.contains(configFilesValues.get(1))) {
						allPubFiles.add(file) ;
						System.out.println(file.toFile());
					}
		        });
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		allPubFiles.forEach( pubVideo -> {
			listVideoAllPubFiles += "loop/" + pubVideo.getFileName() + " " ;
		});
		System.out.println("listVideoAllPubFiles = " + listVideoAllPubFiles);


		try {
			// Runtime.getRuntime().exec("C:\\Program Files\\VideoLAN\\VLC\\vlc.exe -fL " + "D:\\Access\\" + allTiragesFiles.get(0).getFileName() + " \"D:\\Access\\" + allTiragesFiles.get(1).getFileName() + "\"" );
			Runtime.getRuntime().exec("vlc -fL --one-instance " + listVideoAllPubFiles );
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/***********	GESTION DES TIRAGES SUIVANTS L'HEURE 	******************/
		
		String[] tab = configFilesValues.get(9).split(":") ;
    	int i = Integer.parseInt(tab[2]) + 5 ;
    	tab[2] = ""+i ;
    	String fiveSecondeAfter1 = tab[0] + ":" + tab[1] + ":0" + tab[2] ;
    	
    	tab = configFilesValues.get(10).split(":") ;
    	i = Integer.parseInt(tab[2]) + 5 ;
    	tab[2] = ""+i ;    	
    	String fiveSecondeAfter2 = tab[0] + ":" + tab[1] + ":0" + tab[2] ;
    	
    	
    	tab = configFilesValues.get(11).split(":") ;
    	i = Integer.parseInt(tab[2]) + 5 ;
    	tab[2] = ""+i ; 	
    	String fiveSecondeAfter3 = tab[0] + ":" + tab[1] + ":0" + tab[2] ;
    	
    	
    	tab = configFilesValues.get(12).split(":") ;
    	i = Integer.parseInt(tab[2]) + 5 ;
    	tab[2] = ""+i ;
    	String fiveSecondeAfter4 = tab[0] + ":" + tab[1] + ":0" + tab[2] ;

    	
		Timer t = new Timer();  
		TimerTask tt = new TimerTask() {  
		    @Override  
		    public void run() {  
		    	Calendar c = Calendar.getInstance() ;
				String heure = "" + c.getTime() ;
				// System.out.println(heure);
				/*************** 1ER TEST HEURE : 11H45 Download ***************************/
				if (heure.contains(configFilesValues.get(3))) {
		    		System.out.println("Il est " + configFilesValues.get(3));
		    		download(urlDownload,loop2File) ;
		    		try {
						Runtime.getRuntime().exec("unzip /home/pi/loop2.zip -d /home/pi/loop2") ;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		
				}
				/*************** 1ER TEST HEURE : 12H Arret VLC et lancement tirage 5 seconde apres ***************************/
		    	if (heure.contains(configFilesValues.get(9))) {
		    		System.out.println("Il est " + configFilesValues.get(9));
		    		RecupVideoTirageEtTri();
		    		try {
						Runtime.getRuntime().exec("vlc vlc://quit --one-instance" ) ;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		
				}
		    	/******* ARRET DE LA BOUCLE ET LANCEMENT 5 SECONDES APRES **************************/
		    	
		    	// System.out.println(fiveSecondeAfter);
		    	if (heure.contains(fiveSecondeAfter1)) {
		    		try {
						Runtime.getRuntime().exec("vlc -fL --one-instance " + "loop2/" + allTiragesFiles.get(0).getFileName() + " " + listVideoAllPubFiles ) ;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		
				}
		    	
		    	/*************** 2E TEST HEURE : 14H45 Download ***************************/
				if (heure.contains(configFilesValues.get(4))) {
		    		System.out.println("Il est " + configFilesValues.get(4));
		    		download(urlDownload,loop2File) ;
		    		try {
						Runtime.getRuntime().exec("unzip /home/pi/loop2.zip -d /home/pi/loop2") ;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		
				}
				/*************** 1ER TEST HEURE : 15H Arret VLC et lancement tirage 5 seconde apres ***************************/
				if (heure.contains(configFilesValues.get(10))) {
		    		System.out.println("Il est " + configFilesValues.get(10));
		    		RecupVideoTirageEtTri();
		    		try {
						Runtime.getRuntime().exec("vlc vlc://quit --one-instance" ) ;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		
				}
		    	/******* ARRET DE LA BOUCLE ET LANCEMENT 5 SECONDES APRES **************************/
		    	
		    	// System.out.println(fiveSecondeAfter);
		    	if (heure.contains(fiveSecondeAfter2)) {
		    		try {
						Runtime.getRuntime().exec("vlc -fL --one-instance " + "loop2/" + allTiragesFiles.get(1).getFileName() + " " + listVideoAllPubFiles ) ;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		
				}
		    	
		    	/*************** 3E TEST HEURE : 15H45 Download ***************************/
				if (heure.contains(configFilesValues.get(5))) {
		    		System.out.println("Il est " + configFilesValues.get(5));
		    		download(urlDownload,loop2File) ;
		    		try {
						Runtime.getRuntime().exec("unzip /home/pi/loop2.zip -d /home/pi/loop2") ;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		
				}
				/*************** 1ER TEST HEURE : 16H Arret VLC et lancement tirage 5 seconde apres ***************************/
		    	if (heure.contains(configFilesValues.get(11))) {
		    		System.out.println("Il est " + configFilesValues.get(11));
		    		RecupVideoTirageEtTri();
		    		try {
						Runtime.getRuntime().exec("vlc vlc://quit --one-instance" ) ;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		
				}
		    	/******* ARRET DE LA BOUCLE ET LANCEMENT 5 SECONDES APRES **************************/
		    	
		    	// System.out.println(fiveSecondeAfter);
		    	if (heure.contains(fiveSecondeAfter3)) {
		    		try {
						Runtime.getRuntime().exec("vlc -fL --one-instance " + "loop2/" + allTiragesFiles.get(2).getFileName() + " " + listVideoAllPubFiles ) ;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		
				}
		    	
		    	/*************** 4E TEST HEURE : 18H45 Download ***************************/
				if (heure.contains(configFilesValues.get(6))) {
		    		System.out.println("Il est " + configFilesValues.get(6));
		    		download(urlDownload,loop2File) ;
		    		try {
						Runtime.getRuntime().exec("unzip /home/pi/loop2.zip -d /home/pi/loop2") ;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		
				}
				/*************** 1ER TEST HEURE : 19H Arret VLC et lancement tirage 5 seconde apres ***************************/
		    	if (heure.contains(configFilesValues.get(12))) {
		    		System.out.println("Il est " + configFilesValues.get(12));
		    		RecupVideoTirageEtTri();
		    		try {
						Runtime.getRuntime().exec("vlc vlc://quit --one-instance" ) ;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		
				}
		    	/******* ARRET DE LA BOUCLE ET LANCEMENT 5 SECONDES APRES **************************/
		    	
		    	// System.out.println(fiveSecondeAfter);
		    	if (heure.contains(fiveSecondeAfter4)) {
		    		try {
						Runtime.getRuntime().exec("vlc -fL --one-instance " + "loop2/" + allTiragesFiles.get(3).getFileName() + " " + listVideoAllPubFiles ) ;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		
				}
		    	
		    	/*************** 14E TEST HEURE : 4H ***************************/
		    	if (heure.contains(configFilesValues.get(14))) {
		    		System.out.println("Il est " + configFilesValues.get(14) + ", Arret VLC et Suppression des Fichiers de Tirages");
		    		try {
						Runtime.getRuntime().exec("vlc vlc://quit --one-instance" ) ;
						Runtime.getRuntime().exec("rm -r " + configFilesValues.get(13) ) ;
						download(urlDownload,loopFile) ;
						Runtime.getRuntime().exec("unzip /home/pi/loop.zip -d /home/pi/loop") ;

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		
		          
		    }
		    	
		    	
		    	/*************** 7E TEST HEURE : 5H ***************************/
		    	if (heure.contains(configFilesValues.get(7))) {
		    		
		    		System.out.println("Il est " + configFilesValues.get(7) + ", Nouvelle Reuperation des videos de PUB");
		    		System.out.println("------------------------------------------------");
		    		System.out.println("Liste des Videos " + configFilesValues.get(1));
		    		allPubFiles = new ArrayList<Path>() ;
		    		try (Stream<Path> paths = Files.walk(Paths.get(configFilesValues.get(0)))) {
		    		    paths
		    		        .filter(Files::isRegularFile)
		    		        .forEach(file -> {
		    		      	// allFiles.add(file) ;
		    		        	String fileName = "" + file.getFileName() ;
		    		        	if (fileName.contains(configFilesValues.get(1))) {
		    						allPubFiles.add(file) ;
		    						System.out.println(file.toFile());
		    					}
		    		        });
		    		}catch (Exception e) {
		    			// TODO: handle exception
		    			e.printStackTrace();
		    		}
		    		
		    		listVideoAllPubFiles = "" ;
		    		allPubFiles.forEach( pubVideo -> {
		    			listVideoAllPubFiles += "loop/" + pubVideo.getFileName() + " " ;
		    		});
		    		System.out.println("listVideoAllPubFiles = " + listVideoAllPubFiles);
		    		
		    		try {
		    			// Runtime.getRuntime().exec("C:\\Program Files\\VideoLAN\\VLC\\vlc.exe -fL " + "D:\\Access\\" + allTiragesFiles.get(0).getFileName() + " \"D:\\Access\\" + allTiragesFiles.get(1).getFileName() + "\"" );
		    			Runtime.getRuntime().exec("vlc -fL --one-instance " + listVideoAllPubFiles );
		    			
		    		} catch (IOException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
		          
		    }

			 
		}; // FIN METHODE RUN OF TIMER
		
		} ;
		 	
		t.schedule(tt, new Date(),1000); 

    
		}
	
	private void RecupVideoTirageEtTri() {
		// TODO Auto-generated method stub
		allTiragesFiles = new ArrayList<Path>() ;
		System.out.println("------------------------------------------------");
		System.out.println("Liste des Videos " + configFilesValues.get(2));
		
		try (Stream<Path> paths = Files.walk(Paths.get(configFilesValues.get(13)))) {
		    paths
		        .filter(Files::isRegularFile)
		        .forEach(file -> {
		        	// allFiles.add(file) ;
		        	String fileName = "" + file.getFileName() ;
		        	if (fileName.contains(configFilesValues.get(2))) {
						allTiragesFiles.add(file) ;
						System.out.println(file.toFile());
					}
		        });
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		/******************* TRI VIDEO TIRAGE ************************************/
		
		if (allTiragesFiles.isEmpty()) {
			System.out.println("Tirage Is Empty, Let's fill it with PUB1234");
			allTiragesFiles.add(0, allPubFiles.get(0));
			allTiragesFiles.add(1, allPubFiles.get(1));
			allTiragesFiles.add(2, allPubFiles.get(2));
			allTiragesFiles.add(3, allPubFiles.get(3));
		} else {
			
			if (allTiragesFiles.size() == 1) {
				allTiragesFiles.add(1, allPubFiles.get(1));
				allTiragesFiles.add(2, allPubFiles.get(2));
				allTiragesFiles.add(3, allPubFiles.get(3));
			}
			
			if (allTiragesFiles.size() == 2) {
				allTiragesFiles.add(2, allPubFiles.get(2));
				allTiragesFiles.add(3, allPubFiles.get(3));
			}
			
			if (allTiragesFiles.size() == 3) {
				allTiragesFiles.add(3, allPubFiles.get(3));
			}
			
			for (int i=0 ; i<4 ; i++ ) {
				
				Path path = allTiragesFiles.get(i);
				Path pathIntermediaire ;
				String fileName = ""+path.getFileName() ;
				if (fileName.contains(configFilesValues.get(2))) {
					String splitTest = fileName.split("-")[2] ;
					// System.out.println("splitTest(fileName.split[2]) = " + splitTest);
					if (splitTest.contains("1.")) {
						pathIntermediaire = allTiragesFiles.get(0) ;
						allTiragesFiles.set(0, path ) ;
						allTiragesFiles.set(i, pathIntermediaire) ;
					}
					if (splitTest.contains("2.")) {
						pathIntermediaire = allTiragesFiles.get(1) ;
						allTiragesFiles.set(1, path ) ;
						allTiragesFiles.set(i, pathIntermediaire) ;
					}
					if (splitTest.contains("3.")) {
						pathIntermediaire = allTiragesFiles.get(2) ;
						allTiragesFiles.set(2, path ) ;
						allTiragesFiles.set(i, pathIntermediaire) ;
					}
					if (splitTest.contains("4.")) {
						pathIntermediaire = allTiragesFiles.get(3) ;
						allTiragesFiles.set(3, path ) ;
						allTiragesFiles.set(i, pathIntermediaire) ;
					}
				}			
			}
		}
		System.out.println("Liste des Videos " + configFilesValues.get(2) + " Apres Avoir Trié ou Si Non Vide : ");
		System.out.println(allTiragesFiles.get(0).toString());
		System.out.println(allTiragesFiles.get(1).toString());
		System.out.println(allTiragesFiles.get(2).toString());
		System.out.println(allTiragesFiles.get(3).toString());
		
		  
	}

	public void inter (String timeToInter) {
		String[] tab = timeToInter.split(":") ;
    	int i = Integer.parseInt(tab[2]) + 2 ;
    	int i10 = Integer.parseInt(tab[2]) + 20 ;
    	int i15 = Integer.parseInt(tab[2]) + 25 ;
    	tab[2] = ""+i ;
    	twoSecondeAfter = tab[0] + ":" + tab[1] + ":" + tab[2] ;
    	tab[2] = ""+i10 ;
    	tenSecondeAfter = tab[0] + ":" + tab[1] + ":" + tab[2] ;
    	tab[2] = ""+i15 ;
    	fifteenSecondeAfter = tab[0] + ":" + tab[1] + ":" + tab[2] ;
		System.out.println("Inter effectier sur " + timeToInter);
		System.out.println("twoSecondeAfter = " + twoSecondeAfter);
		System.out.println("tenSecondeAfter = " + tenSecondeAfter);
		System.out.println("fifteenSecondeAfter = " + fifteenSecondeAfter);
	}
	
	public static void download(String urlString, File destination) {    
        try {
            URL website = new URL(urlString);
            ReadableByteChannel rbc;
            rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(destination);
            System.out.println(fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE));
            fos.close();
            rbc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	 
}

