import java.util.ArrayList;
import java.util.Iterator;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.util.Callback;

public class MainInterface extends AnchorPane {
	
	private MainInterface root;
	private GridPane parent;
	
	private TableView<Job> jobsListTable;
	private TableColumn<Job,Object> jobsNameColumn;
	private TableColumn<Job,Object> jobsSecondsColumn;
	
	private Button insertButton;
	private Button deleteButton;
	private Button refreshButton;
	private Button startTimerButton;
	private Button updateTimeButton;
	
	private HBox topBar;
	private ComboBox<Integer> refreshRateCombo;
	
	
	public static MainInterface createInstance() {
		MainInterface instance=new MainInterface();
		instance.initialize();
		return instance;
	}
	
	public void initialize() {
		root=this;
		initializeInterface();
		setEvents();
		loadJobs();
	}
	
	private void initializeInterface() {
		parent=new GridPane();
		
		ColumnConstraints column1=new ColumnConstraints();
		column1.setPercentWidth(100);
		parent.getColumnConstraints().addAll(column1);
		
		RowConstraints row1=new RowConstraints();
		row1.setPercentHeight(10);
		RowConstraints row2=new RowConstraints();
		row2.setPercentHeight(75);
		RowConstraints row3=new RowConstraints();
		row3.setPercentHeight(15);
		parent.getRowConstraints().addAll(row1,row2,row3);
		
		
		jobsListTable=new TableView<Job>();
		jobsListTable.setEditable(false);
		jobsListTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		jobsListTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		
		HBox topBar=new HBox();
		topBar.setFillHeight(true);
		topBar.setAlignment(Pos.CENTER);
		topBar.setSpacing(10.0);
		
		Label label=new Label("Refresh Table Rate: ");
		topBar.getChildren().add(label);
		ObservableList<Integer> refreshRateOptions=FXCollections.observableArrayList(1,2,5,10,15);
		refreshRateCombo=new ComboBox<Integer>(refreshRateOptions);
		refreshRateCombo.setValue(1);
		topBar.getChildren().add(refreshRateCombo);
		
		
		HBox toolsBar=new HBox();
		toolsBar.setFillHeight(true);
		toolsBar.setAlignment(Pos.CENTER);
		toolsBar.setSpacing(10.0);
		
		insertButton=new Button("Insert");
		insertButton.setMinSize(100, 20);
		insertButton.setFont(new Font(18));
		insertButton.setCursor(Cursor.HAND);
		toolsBar.getChildren().add(insertButton);
		deleteButton=new Button("Delete");
		deleteButton.setMinSize(100, 20);
		deleteButton.setFont(new Font(18));
		deleteButton.setCursor(Cursor.HAND);
		toolsBar.getChildren().add(deleteButton);
		refreshButton=new Button("Refresh");
		refreshButton.setMinSize(100, 20);
		refreshButton.setFont(new Font(18));
		refreshButton.setCursor(Cursor.HAND);
		toolsBar.getChildren().add(refreshButton);
		updateTimeButton=new Button("Update Time");
		updateTimeButton.setMinSize(100, 20);
		updateTimeButton.setFont(new Font(18));
		updateTimeButton.setCursor(Cursor.HAND);
		toolsBar.getChildren().add(updateTimeButton);
		startTimerButton=new Button("Start Timer");
		startTimerButton.setMinSize(100, 20);
		startTimerButton.setFont(new Font(18));
		startTimerButton.setCursor(Cursor.HAND);
		toolsBar.getChildren().add(startTimerButton);
		
		
		jobsNameColumn=new TableColumn<Job,Object>("Job Name");
		jobsSecondsColumn=new TableColumn<Job,Object>("Time Passed");
		jobsListTable.getColumns().addAll(jobsNameColumn,jobsSecondsColumn);
		
		
		
		parent.add(topBar, 0, 0);
		parent.add(jobsListTable, 0, 1);
		parent.add(toolsBar, 0, 2);
		
		AnchorPane.setTopAnchor(parent, 0.0);
		AnchorPane.setLeftAnchor(parent, 0.0);
		AnchorPane.setBottomAnchor(parent, 0.0);
		AnchorPane.setRightAnchor(parent, 0.0);
		root.getChildren().add(parent);
	}
	
	private void setEvents() {
		insertButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String jobName=Main.showInputDialog("Enter the job name:", "Job Name");
				if(jobName!=null) {
					TimeDialog timeDialog=new TimeDialog(0, 0, 0);
					timeDialog.setButtonText("Add Job");
					timeDialog.setContentTitle("Set the initial time that passed for the job:");
					timeDialog.setOnOkButtonAction(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							addJob(jobName,timeDialog.getHours(),timeDialog.getMinutes(),timeDialog.getSeconds());
						}
					});
					timeDialog.showAndWait();
				}
			}
		});
		
		deleteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				deleteSelectedJobs();
			}
		});
		
		refreshButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				loadJobs();
			}
		});
		
		startTimerButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(Main.isJobTimerOn) {
					if(Main.showConfirmDialog("Do you want to stop timer of the job ("+Main.selectedJob.getJobName()+")?")) {
						Main.stopJobTimer();
						startTimerButton.setText("Start Timer");
					}
				}
				
				Job job=jobsListTable.getSelectionModel().getSelectedItem();
				if(job!=null) {
					if(!Main.isJobTimerOn) {
						if(Main.showConfirmDialog("Do you want to start timer of the job ("+job.getJobName()+")?")) {
							Main.startJobTimer(job);
							startTimerButton.setText("Stop Timer");
						}
					}
				}
			}
		});
		updateTimeButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub			
				Job job=jobsListTable.getSelectionModel().getSelectedItem();
				if(job==null) {
					return;
				}
				String jobName=job.getJobName();
				if(job!=null) {
					if(!Main.isJobTimerOn) {
						TimeDialog timeDialog=new TimeDialog(job.getTimerHours(), job.getTimerMinutes(),job.getTimerSeconds());
						timeDialog.setButtonText("Update Time");
						timeDialog.setContentTitle("Set the time that is passed for the job:");
						timeDialog.setOnOkButtonAction(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								updateJob(jobName,timeDialog.getHours(),timeDialog.getMinutes(),timeDialog.getSeconds());
							}
						});
						timeDialog.showAndWait();
					}
				}
			}
		});
		
		refreshRateCombo.valueProperty().addListener(new ChangeListener<Integer>() {
			@Override
			public void changed(ObservableValue<? extends Integer> arg0, Integer arg1, Integer arg2) {
				// TODO Auto-generated method stub
				Main.TIMER_REFRESH_INTERVAL=arg2;
			}
		});
		
		jobsNameColumn.setCellValueFactory(new NameCellFactory());
		jobsSecondsColumn.setCellValueFactory(new TimeCellFactory());
		jobsNameColumn.prefWidthProperty().bind(jobsListTable.widthProperty().divide(2));
		jobsSecondsColumn.prefWidthProperty().bind(jobsListTable.widthProperty().divide(2));
	}
	
	class NameCellFactory implements Callback<TableColumn.CellDataFeatures<Job, Object>, ObservableValue<Object>> {
	    @Override
	    public ObservableValue<Object> call(TableColumn.CellDataFeatures<Job, Object> data) {
	        return new ReadOnlyObjectWrapper<>(data.getValue().getJobName());
	    }
	}
	class TimeCellFactory implements Callback<TableColumn.CellDataFeatures<Job, Object>, ObservableValue<Object>> {
	    @Override
	    public ObservableValue<Object> call(TableColumn.CellDataFeatures<Job, Object> data) {
	        return new ReadOnlyObjectWrapper<>(data.getValue().timerToString());
	    }
	}
	
	
	private void addJob(String jobName,int hours,int minutes,int seconds) {
		if(!isJobExists(jobName)) {
			int secondsTotal=(hours*60*60)+(minutes*60)+seconds;
			Main.dbJobs.insert("name,seconds","'"+jobName+"',"+String.valueOf(secondsTotal)+"");
			Main.showMessageDialog("The job is added successfully!!");
			loadJobs();
		}else {
			Main.showMessageDialog("The job is already exists!! add another name.", AlertType.WARNING);
		}
	}
	private void updateJob(String jobName,int hours,int minutes,int seconds) {
		if(isJobExists(jobName)) {
			int secondsTotal=(hours*60*60)+(minutes*60)+seconds;
			Main.dbJobs.update("seconds", String.valueOf(secondsTotal), new String[] {"name"}, new String[] {"'"+jobName+"'"});
			Main.showMessageDialog("The job is updated successfully!!");
			loadJobs();
		}else {
			Main.showMessageDialog("The job does not exist!! Something went wrong..", AlertType.WARNING);
		}
	}
	
	private void deleteSelectedJobs() {
		if(Main.showConfirmDialog("Do you want to delete selected jobs?")) {
			ObservableList<Job> selectedItems=jobsListTable.getSelectionModel().getSelectedItems();
			Iterator<Job> iterator=selectedItems.iterator();
			while(iterator.hasNext()) {
				Job jobToDelete=iterator.next();
				Main.dbJobs.deleteRows(new String[] {"name"}, new String[] {"'"+jobToDelete.getJobName()+"'"});
			}
			loadJobs();
		}
	}
	
	private void loadJobs() {
		ObservableList<Job> jobsObservable=FXCollections.observableArrayList();
		ArrayList<String[]> jobsList=Main.dbJobs.selectAll(new String[] {"name","seconds"}, new String[] {"string","int"});
		Iterator<String[]> iterateor=jobsList.iterator();
		while(iterateor.hasNext()) {
			String[] jobItem=iterateor.next();
			Job job=new Job();
			job.setJobName(jobItem[0]);
			job.setSecondsTotal(Integer.valueOf(jobItem[1]));
			jobsObservable.add(job);
		}
		jobsListTable.getItems().setAll(jobsObservable);
	}
	
	private boolean isJobExists(String jobName) {
		return Main.dbJobs.isTupleExists(new String[] {"name"}, new String[] {"'"+jobName+"'"});
	}

	public void refreshTable() {
		loadJobs();
	}

	public void disableForTimer() {
		insertButton.setDisable(true);
		deleteButton.setDisable(true);
		refreshButton.setDisable(true);
		updateTimeButton.setDisable(true);
	}
	public void enableAfterTimer() {
		insertButton.setDisable(false);
		deleteButton.setDisable(false);
		refreshButton.setDisable(false);
		updateTimeButton.setDisable(false);
	}
	
	

}
