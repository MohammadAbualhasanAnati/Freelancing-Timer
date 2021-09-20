import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application{
	
	public static int WINDOW_WIDTH=500;
	public static int WINDOW_HEIGHT=809;
	public static String APP_NAME="Freelancing Timer";
	public static String APP_TITLE="Freelancing Timer";
	
	public static String APPDATA_DIR="";
	public static String DB_PATH="";
	
	protected static Stage stage;
	
	protected static DBSqlite dbJobs;
	
	protected static Timer jobTimer;
	protected static Job selectedJob;
	protected static boolean isJobTimerOn=false;
	protected static int TIMER_REFRESH_INTERVAL=1;
	protected static int timerCounter=0;
	
	private static MainInterface mainInterface;
	
	 private static final int SINGLE_INSTANCE_LISTENER_PORT = 9955;
	 private static final String SINGLE_INSTANCE_FOCUS_MESSAGE = "focus_freelance";
	 private static final String instanceId = UUID.randomUUID().toString();
	 private static final int FOCUS_REQUEST_PAUSE_MILLIS = 500;
	 
	 protected static String OS_NAME;
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		OS_NAME=System.getProperty("os.name").toLowerCase();
		getDirectories();
		dbInitialization();
		Application.launch(args);
	}
	
	public void init() {
		CountDownLatch instanceCheckLatch = new CountDownLatch(1);
		
		Thread instanceListener = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(SINGLE_INSTANCE_LISTENER_PORT, 10)) {
                instanceCheckLatch.countDown();

                while (true) {
                    try (
                            Socket clientSocket = serverSocket.accept();
                            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
                    ) {
                        String input = in.readLine();
                        System.out.println("Received single instance listener message: " + input);
                        if (input.startsWith(SINGLE_INSTANCE_FOCUS_MESSAGE) && stage != null) {
                            Thread.sleep(FOCUS_REQUEST_PAUSE_MILLIS);
                            Platform.runLater(() -> {
                                System.out.println("To front " + instanceId);
                                stage.setIconified(false);
                                stage.show();
                                stage.toFront();
                            });
                        }
                    } catch (IOException e) {
                        System.out.println("Single instance listener unable to process focus message from client");
                        e.printStackTrace();
                    }
                }
            } catch(java.net.BindException b) {
                System.out.println("SingleInstanceApp already running");

                try (
                        Socket clientSocket = new Socket(InetAddress.getLocalHost(), SINGLE_INSTANCE_LISTENER_PORT);
                        PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()))
                ) {
                    System.out.println("Requesting existing app to focus");
                    out.println(SINGLE_INSTANCE_FOCUS_MESSAGE + " requested by " + instanceId);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("Aborting execution for instance " + instanceId);
                Platform.exit();
            } catch(Exception e) {
                System.out.println(e.toString());
            } finally {
                instanceCheckLatch.countDown();
            }
        }, "instance-listener");
        instanceListener.setDaemon(true);
        instanceListener.start();

        try {
            instanceCheckLatch.await();
        } catch (InterruptedException e) {
            Thread.interrupted();
        }
	}

	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub
		stage=arg0;
		
		mainInterface=MainInterface.createInstance();
		
		Image logoImage = new Image("/Resources/logo.png");
		
		
		getWindowDimensions();
		Scene scene=new Scene(mainInterface,WINDOW_WIDTH,WINDOW_HEIGHT);
		stage.setScene(scene);
		stage.setTitle(APP_TITLE);
		stage.getIcons().add(logoImage);
		stage.show();
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent arg0) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
		centerStage(stage);
	}
	
	public void stop() {
        System.out.println("Exiting instance " + instanceId);
    }
	
	
	private void getWindowDimensions() {
		double r=(double) WINDOW_HEIGHT/WINDOW_WIDTH;
		Dimension dimension=Toolkit.getDefaultToolkit().getScreenSize();
		double screenWidth=dimension.getWidth();
		WINDOW_WIDTH=(int)((double)((1/r)*screenWidth));
		WINDOW_HEIGHT=(int)((double)(WINDOW_WIDTH/r));
	}
	
	private void centerStage(Stage stage) {
		Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
	}
	
	
	private static void getDirectories() {
		if(OS_NAME.contains("windows")) {
			APPDATA_DIR=System.getenv("APPDATA");
			System.out.println(APPDATA_DIR);
		}else {
			APPDATA_DIR=System.getProperty("user.home");
			APPDATA_DIR=browseFileOrFolder(APPDATA_DIR, ".local");
			APPDATA_DIR=browseFileOrFolder(APPDATA_DIR, "share");
		}
		APPDATA_DIR=browseFileOrFolder(APPDATA_DIR, APP_NAME);
		DB_PATH=browseFileOrFolder(APPDATA_DIR, "jobs.db");
		
		File directory = new File(APPDATA_DIR);
		
	    if (!directory.exists()){
	        directory.mkdirs();
	    }
	}
	
	private static String browseFileOrFolder(String dir,String fileOrFolder) {
		String result="";
		if(!dir.substring(dir.length()-1).equals(File.separator)) {
			result=dir+File.separator;
		}
		result+=fileOrFolder;
		return result;
	}
	
	private static void dbInitialization() {
		dbJobs=new DBSqlite(DB_PATH);
		dbJobs.checkTable("jobs", 
						"id integer PRIMARY KEY, "
						+ "name text NOT NULL UNIQUE, "
						+ "seconds integer DEFAULT 0"
					);
	}
	
	
	public static String showInputDialog(String contentText,String defaultValue) {
		String result=null;
		TextInputDialog dialog=new TextInputDialog(defaultValue);
		dialog.setTitle(Main.APP_TITLE);
		dialog.setHeaderText("Input Dialog");
		dialog.setContentText(contentText);
		
		Optional<String> resultD=dialog.showAndWait();
		if(resultD.isPresent()) {
			result=resultD.get();
		}
		return result;
	}
	public static void showMessageDialog(String contentText) {
		Alert dialog=new Alert(AlertType.INFORMATION);
		dialog.setTitle(Main.APP_TITLE);
		dialog.setHeaderText("Message Dialog");
		dialog.setContentText(contentText);
		
		dialog.showAndWait();
	}
	public static void showMessageDialog(String contentText,AlertType type) {
		Alert dialog=new Alert(type);
		dialog.setTitle(Main.APP_TITLE);
		dialog.setHeaderText("Message Dialog");
		dialog.setContentText(contentText);
		
		dialog.showAndWait();
	}
	
	public static boolean showConfirmDialog(String contentText) {
		boolean result=false;
		Alert dialog=new Alert(AlertType.CONFIRMATION);
		dialog.setTitle(Main.APP_TITLE);
		dialog.setHeaderText("Message Dialog");
		dialog.setContentText(contentText);
		
		Optional<ButtonType> resultD=dialog.showAndWait();
		if(resultD.get()==ButtonType.OK) {
			result=true;
		}
		
		return result;
	}
	
	public static void startJobTimer(Job job) {
		isJobTimerOn=true;
		jobTimer=new Timer();
		jobTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(timerCounter==TIMER_REFRESH_INTERVAL) {
					mainInterface.refreshTable();
					timerCounter=0;
				}
				if(!isJobTimerOn) {
					selectedJob=null;
					timerCounter=0;
					jobTimer.cancel();
				}
				increaseTimeForJob(job);
				timerCounter+=1;
			}
		}, 0, 1000);
		selectedJob=job;
		
		mainInterface.disableForTimer();
	}
	
	private static void increaseTimeForJob(Job job) {
		job.setSecondsTotal(job.getSecondsTotal()+1);
		saveJobToDB(job);
	}

	private static void saveJobToDB(Job job) {
		dbJobs.update("seconds", String.valueOf(job.getSecondsTotal()), new String[] {"name"}, new String[] {"'"+job.getJobName()+"'"});
	}

	public static void stopJobTimer() {
		isJobTimerOn=false;
		mainInterface.enableAfterTimer();
		Timer tempTimer=new Timer();
		tempTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mainInterface.refreshTable();
				tempTimer.cancel();
			}
		}, 1000);
	}

}
