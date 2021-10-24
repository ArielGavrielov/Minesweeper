package mines;

import java.util.Random;

import javafx.scene.control.Button;

enum CellStat {
	Regular,
	Mine
}

public class Mines {
	private final int height, width;
	private boolean showAll;
	private Random rand;
	private Cell cells[][];
	private int openCells, flagCells, numMines;
	
	public class Cell {
		private CellStat cellStat;
		private boolean isOpen;
		private boolean flag;
		private int nearMines;
		private Button b;
		
		public Cell() {
			this.cellStat = CellStat.Regular;
			this.isOpen = false;
			this.flag = false;
		}
		
		private void updateButtonText() {
			if(showAll || isOpen) {
				b.setDisable(true);
				if(this.isMine()) {
					b.getStyleClass().add("mine");
					if(b.getStyleClass().contains("flag")) {
						b.getStyleClass().remove("flag");
						b.setStyle("-fx-background-color: #F00;");
					}
				}
				else if(nearMines == 0) b.setText("");
				else {
					b.getStyleClass().remove("flag");
					b.setText(Integer.toString(nearMines));
					
					switch(b.getText()) {
		    		case "1": b.setStyle("-fx-text-fill: blue;");
		    		break;
		    		case "2": b.setStyle("-fx-text-fill: green;");
		    		break;
		    		case "3": b.setStyle("-fx-text-fill: red;");
		    		break;
		    		case "4": b.setStyle("-fx-text-fill: purple;");
		    		break;
		    		case "5": b.setStyle("-fx-text-fill: orange;");
		    		break;
		    		case "6":case "7": case "8": b.setStyle("-fx-text-fill: brown;");
		    		break;
		    		}
				}
				
			}
			else {
				if(flag) {
					b.getStyleClass().add("flag");
					b.setStyle("-fx-text-fill: #f13705");
				}
				else {
					b.getStyleClass().remove("flag");
					b.setStyle("-fx-text-fill: black");
				}
			}
		}
		
		public void incNearMines() {
			nearMines++;
		}
		
		public Button getButton() { 
			if(b == null) this.b = new Button();
			return this.b;
		}

		public void setCellStat(CellStat cellStat) {
			this.cellStat = cellStat;
		}

		public void open() {
			this.isOpen = true;
			updateButtonText();
			openCells++;
		}
		
		public boolean isOpen() {
			return this.isOpen;
		}
		
		public void setFlag(boolean flag) {
			this.flag = flag;
			updateButtonText();
		}
		
		public boolean getFlag() {
			return this.flag;
		}
		
		public boolean isMine() {
			return this.cellStat == CellStat.Mine;
		}
		
		public String toString() {
			if(showAll) {
				return isMine() ? "X" : nearMines == 0 ? " " : Integer.toString(nearMines);
			}
			if(!isOpen()) return getFlag() ? "F" : ".";
			return isMine() ? "X" : nearMines == 0 ? " " : Integer.toString(nearMines);
		}
	}

	public Mines(int height, int width, int numMines) {
		if(height < 2 || width < 2) throw new IllegalArgumentException("Board height/width cannot be less than 2.");
		if(numMines >= height*width) throw new IllegalArgumentException("The mines cannot be on all the board.");
		if(height <= 30 && width <= 30) {
			this.height = height;
			this.width = width;
			this.cells = new Cell[height][width];
			createCells();
			
			this.showAll = false;
			if(numMines > 0) {
				this.rand = new Random();
				addRandomMines(numMines);
			}
		}
		else throw new IllegalArgumentException("Rules:\nMinimum board size 2X2.\nMaximum board size is 30X30.\nIt imposible to play with mines on all the board.\nGood Luck!");
	}
	
	private void createCells() {
		for(int i = 0; i < height; i++)
			for(int j = 0; j < width; j++)
				this.cells[i][j] = new Cell();
	}
	
	public boolean addMine(int i, int j) {
		if(i < 0 || i >= height || j < 0 || j >= width || cells[i][j].isMine() || cells[i][j].isOpen()) return false;
		this.cells[i][j].setCellStat(CellStat.Mine);
		numMines++;

		int lowRow = i-1;
        lowRow = lowRow < 0 ? 0 : lowRow;
        int highRow = i+2;
        highRow = highRow > height ? height : highRow;

        int lowCol = j-1;
        lowCol = lowCol < 0 ? 0 : lowCol;
        int highCol = j+2;
        highCol = highCol > width ? width : highCol;
        
        for (int x = lowRow; x < highRow; x++)
            for (int y = lowCol; y < highCol; y++)
                if(!this.cells[x][y].isMine())
                	cells[x][y].incNearMines();
		return true;
	}
	
	private void addRandomMines(int n) {
		if(n == 0) return;
		int i = rand.nextInt(height), j = rand.nextInt(width);
		if(addMine(i, j)) {
			addRandomMines(n-1);
			//numMines--;
		}
		else addRandomMines(n);
	}
	
	public boolean open(int i, int j) {
		if(cells[i][j].isMine() || cells[i][j].getFlag()) { return false; }
		this.cells[i][j].open();
		
		if(cells[i][j].nearMines == 0) {
			int lowRow = i-1;
	        lowRow = lowRow < 0 ? 0 : lowRow;
	        int highRow = i+2;
	        highRow = highRow > height ? height : highRow;

	        int lowCol = j-1;
	        lowCol = lowCol < 0 ? 0 : lowCol;
	        int highCol = j+2;
	        highCol = highCol > width ? width : highCol;
	        
	        for (int x = lowRow; x < highRow; x++)
	            for (int y = lowCol; y < highCol; y++) 
	            	if(!this.cells[x][y].isOpen())
	            		this.open(x,y);
	            		
		}
		return true;
	}
	
	public void toggleFlag(int x, int y)  {
		if(this.cells[x][y].getFlag()) flagCells--;
		else flagCells++;
		this.cells[x][y].setFlag(!this.cells[x][y].getFlag());
	}
	
	public boolean getFlag(int x, int y) {
		return this.cells[x][y].getFlag();
	}
	public boolean isDone() {
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				if(!this.cells[i][j].isOpen() && !this.cells[i][j].isMine() || this.cells[i][j].isMine() && !this.cells[i][j].getFlag()) return false;
			}
		}
		if(flagCells != numMines) return false;
		return true;
	}
	/*
	public String get(int i, int j) {
		if(showAll) {
			return this.cells[i][j].isMine() ? "X" : checkNear(i,j) == 0 ? " " : Integer.toString(checkNear(i,j));
		}
		if(!this.cells[i][j].isOpen()) return this.cells[i][j].getFlag() ? "F" : ".";
		return this.cells[i][j].isMine() ? "X" : checkNear(i,j) == 0 ? " " : Integer.toString(checkNear(i,j));
	}
	*/
	public void setShowAll(boolean showAll) {
		this.showAll = showAll;
		if(showAll)
			for(int i = 0; i < cells.length; i++)
				for(int j = 0; j < cells[i].length; j++)
					cells[i][j].updateButtonText();
	}
	
	public Cell[][] getBoard() { return this.cells; }
	
	public int getOpenedCells() { return this.openCells; }
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				sb.append(this.cells[i][j]);
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
