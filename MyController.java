package mines;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mines.Mines.Cell;

public class MyController {
	private Stage stage;
	private Mines m;
	private Cell[][] cells;
	private boolean gameover = false;
	private Timer timer;
	private boolean gamePaused = true, gameStart = false;
	
	public void setStage(Stage stage) {
		this.stage = stage;
		stage.setOnHiding( event -> {if(timer != null) timer.cancel();} );
	}

	@FXML private HBox header;
	@FXML private VBox allBoard;
	@FXML private GridPane gameBoard;
    @FXML private Button reset, pause;
    @FXML private TextField minesInput, heightInput, widthInput;
    @FXML private Label counter;
    @FXML private Label minesLeft;
    
    TimerTask getTask() {
    	return new TimerTask()
        {
            public void run() {
            	if(gameover) {
            		timer.cancel();
            		return;
            	}
            	Platform.runLater(() -> incClock());
            }
            void incClock() {
            	short sec = Short.parseShort(counter.getText().substring(counter.getText().indexOf(":")+1));
            	short min = Short.parseShort(counter.getText().substring(0, counter.getText().indexOf(":")));
            	if(sec == 59) {
            		sec = 0;
            		min++;
            	}
            	else sec++;
            	counter.setText((min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec);
            }
        };
    }

    @FXML
    void submitAction(ActionEvent event) {
    	restartGame();
    }
    
    @FXML
    void pauseGame(ActionEvent event) {
    	gamePaused = true;
    	pause.setDisable(true);
    	timer.cancel();
    }

    void restartGame() {
    	gameBoard.getChildren().clear();
    	if(timer != null) {
    		counter.setText("00:00");
    		timer.cancel();
    	}
    	gameover = false;
    	gameStart = false;
    	startGame();
    }
    
    void startGame() {
    	try {
    		int width, height, mines;
    		try {
    			height = Integer.parseInt(heightInput.getText());
    			width = Integer.parseInt(widthInput.getText());
    			mines = Integer.parseInt(minesInput.getText());
    		}
    		catch(IllegalArgumentException e) {
    			Alert alert = new Alert(AlertType.ERROR);
            	alert.setTitle("ERROR");
            	alert.setHeaderText(null);
            	alert.setContentText("Inputs MUST be an Integer!!!");
            	alert.show();
            	return;
    		}
			m = new Mines(height, width, mines);
			cells = m.getBoard();
			minesLeft.setText(minesInput.getText());
			pause.setDisable(true);
		} catch (IllegalArgumentException e) {
			Alert alert = new Alert(AlertType.ERROR);
        	alert.setTitle("ERROR");
        	alert.setHeaderText(null);
        	alert.setContentText(e.getMessage());
        	alert.show();
        	return;
		}

    	for(int i = 0; i < Integer.parseInt(heightInput.getText()); i++) {
    		for(int j = 0; j < Integer.parseInt(widthInput.getText()); j++) {
    			Button b = cells[i][j].getButton();
    			b.getStyleClass().addAll("gameButtons", (i+j)%2==0 ? "btndiv" : "btnundiv");
    			//b.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
    			b.setMinSize(25, 25);
    			b.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    			GridPane.setHgrow(b, Priority.SOMETIMES);
    			GridPane.setVgrow(b, Priority.SOMETIMES);
    			
    			b.setOnMouseClicked(new EventHandler<MouseEvent>() {
    			    @Override 
    			    public void handle(MouseEvent mouseEvent) {
    			    	if(gamePaused || !gameStart) {
    			    		timer = new Timer();
    			    	    timer.schedule(getTask(), 0, 1000);
    			    	    pause.setDisable(false);
    			    	    gamePaused = false;
    			    	    if(!gameStart) gameStart = true;
    			    	}
    			    	if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
    			    		if(!m.getFlag(GridPane.getRowIndex(b), GridPane.getColumnIndex(b))) {
		    			        if(!m.open(GridPane.getRowIndex(b), GridPane.getColumnIndex(b))) {
		    			        	m.setShowAll(true);
		    			        	Alert alert = new Alert(AlertType.INFORMATION);
		    			        	alert.setTitle("GAMEOVER!");
		    			        	alert.setHeaderText(null);
		    			        	alert.setContentText("You click on a bomb!");
		    			        	alert.show();
		    			        	m.setShowAll(true);
		    			        	gameover = true;
		    			        	pause.setDisable(true);
		    			        }
		    			        checkIfDone();
	    			        }
    			    	} else if(mouseEvent.getButton().equals(MouseButton.SECONDARY)){
    			    		if(m.getFlag(GridPane.getRowIndex(b), GridPane.getColumnIndex(b))) {
    			    			m.toggleFlag(GridPane.getRowIndex(b), GridPane.getColumnIndex(b));
    			    			minesLeft.setText(Integer.toString(Integer.parseInt(minesLeft.getText())+1));
    			    		}
    			    		else if(Integer.parseInt(minesLeft.getText()) > 0) {
	    			    		m.toggleFlag(GridPane.getRowIndex(b), GridPane.getColumnIndex(b));
	    			    		minesLeft.setText(Integer.toString(Integer.parseInt(minesLeft.getText())-1));
    			    		}
    			    		if(Integer.parseInt(minesLeft.getText()) == 0)
    			    			checkIfDone();
    			    	}
    			    }
    			    
    			    void checkIfDone() {
    			    	if(m.isDone()) {
    			        	gameover = true;
    			        	pause.setDisable(true);
    			        	m.setShowAll(true);
    			        	Alert alert = new Alert(AlertType.CONFIRMATION);
    			        	alert.setTitle("You won this game!!");
    			        	alert.setHeaderText(null);
    			        	alert.setContentText("Start new game?");
    			        	alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
    			        	alert.showAndWait().ifPresent(type -> {
    			                if (type == ButtonType.YES) {
    			                	restartGame();
	    			        	    return;
    			                }
    			            
    			            });
    			        }
    			    }
    			});
    			gameBoard.add(b,j,i);
    		}
    	}
    	stage.sizeToScene();
		header.setMinWidth(gameBoard.getWidth());
		stage.setMinWidth(gameBoard.getWidth());
		stage.setMinHeight(gameBoard.getHeight()+header.getHeight()+25.0);
    }
    
}