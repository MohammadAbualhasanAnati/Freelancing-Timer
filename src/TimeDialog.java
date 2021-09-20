import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public class TimeDialog extends Dialog<Integer>{
	
	private Dialog<Integer> dialog;
	
	int hours;
	int minutes;
	int seconds;
	
	private VBox parent;
	private VBox infoRow;
	private HBox inputsSection;
	private VBox hoursRow;
	private VBox minutesRow;
	private VBox secondsRow;
	private HBox buttonsRow;
	private Label labelTitle;
	private Label labelHours;
	private Label labelMinutes;
	private Label labelSeconds;
	private TextField hoursField;
	private TextField minutesField;
	private TextField secondsField;
	
	private EventHandler<MouseEvent> okButtonEventHandler;
	
	private Button addButton = new Button();
    private Button cancelButton = new Button("Cancel");
    
    private Runnable onClickOk;
	
	public TimeDialog(int hours,int minutes,int seconds) {
		dialog=this;
		
		this.hours=hours;
		this.minutes=minutes;
		this.seconds=seconds;
		
		initializeDialog();
		
		setEvents();
	}
	
	private void initializeDialog() {
		parent=new VBox();
		
		parent.setSpacing(20.0);
		
		infoRow=new VBox();
		inputsSection=new HBox();
		hoursRow=new VBox();
		minutesRow=new VBox();
		secondsRow=new VBox();
		buttonsRow=new HBox();
		
		infoRow.setMinHeight(25.0);
		infoRow.setSpacing(5.0);
		inputsSection.setSpacing(5.0);
		hoursRow.setSpacing(0.0);
		minutesRow.setSpacing(0.0);
		secondsRow.setSpacing(0.0);
		buttonsRow.setSpacing(5.0);
		buttonsRow.setAlignment(Pos.CENTER);
		
		labelTitle=new Label();
		infoRow.getChildren().add(labelTitle);
		
		labelHours=new Label("Hours");
		labelMinutes=new Label("Minutes");
		labelSeconds=new Label("Seconds");
		
		labelHours.setMinWidth(100.0);
		labelMinutes.setMinWidth(100.0);
		labelSeconds.setMinWidth(100.0);
		hoursRow.getChildren().add(labelHours);
		minutesRow.getChildren().add(labelMinutes);
		secondsRow.getChildren().add(labelSeconds);
		
		
		hoursField=new TextField();
		minutesField=new TextField();
		secondsField=new TextField();
		hoursField.setTooltip(new Tooltip(String.valueOf("hours")));
		hoursField.setText(String.valueOf(hours));
		minutesField.setTooltip(new Tooltip(String.valueOf("minutes")));
		minutesField.setText(String.valueOf(minutes));
		secondsField.setTooltip(new Tooltip(String.valueOf("seconds")));
		secondsField.setText(String.valueOf(seconds));
		hoursRow.getChildren().add(hoursField);
		minutesRow.getChildren().add(minutesField);
		secondsRow.getChildren().add(secondsField);
		inputsSection.getChildren().addAll(hoursRow,minutesRow,secondsRow);
		
		addButton.setMinWidth(100.0);
		cancelButton.setMinWidth(100.0);
		
		buttonsRow.getChildren().add(addButton);
		buttonsRow.getChildren().add(cancelButton);
		
		
		parent.getChildren().add(infoRow);
		parent.getChildren().add(inputsSection);
		parent.getChildren().add(buttonsRow);
		
		getDialogPane().setContent(parent);
	}
	
	
	private void setEvents() {
		Window window=this.getDialogPane().getScene().getWindow();
		window.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent arg0) {
				// TODO Auto-generated method stub
				window.hide();
			}
		});
		addButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(onClickOk!=null) {
					hours=Integer.valueOf(hoursField.getText());
					minutes=Integer.valueOf(minutesField.getText());
					seconds=Integer.valueOf(secondsField.getText());
					onClickOk.run();
				}
				window.hide();
			}
		});
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				window.hide();
			}
		});
		
		hoursField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
                	hoursField.setText(oldValue);
                }
            }
        });
		minutesField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
                	minutesField.setText(oldValue);
                }
            }
        });
		secondsField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
                	secondsField.setText(oldValue);
                }
            }
        });
	}
	
	public void setContentTitle(String title) {
		labelTitle.setText(title);
	}
	
	public void setOnOkButtonAction(Runnable onClickOk) {
		this.onClickOk=onClickOk;
	}
	
	public void setButtonText(String text) {
		this.addButton.setText(text);
	}
	
	public int getHours() {
		return this.hours;
	}
	public int getMinutes() {
		return this.minutes;
	}
	public int getSeconds() {
		return this.seconds;
	}

}
