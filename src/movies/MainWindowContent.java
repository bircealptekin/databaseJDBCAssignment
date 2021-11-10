package movies;

import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.swing.*;
import javax.swing.border.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import java.sql.*;

@SuppressWarnings("serial")
public class MainWindowContent extends JInternalFrame implements ActionListener {	
	String cmd = null;

	// DB Connectivity Attributes
	private Connection con = null;
	private Statement stmt = null;
	private ResultSet rs = null;

	private Container content;

	private JPanel detailsPanel;
	private JPanel exportButtonPanel;
	private JPanel chartsPanel;
	private JPanel statusPanel;
	private JPanel ratingPanel;
	private JPanel placeHolder;
	//private JPanel exportConceptDataPanel;
	private JScrollPane dbContentsPanel;

	private Border lineBorder;

	private JLabel IDLabel=new JLabel("ID:                 ");
	private JLabel titleLabel=new JLabel("Title:               ");
	private JLabel genreLabel=new JLabel("Genre:      ");
	private JLabel yearLabel=new JLabel("Year:        ");
	private JLabel lengthLabel=new JLabel("Length:                 ");
	private JLabel directorLabel=new JLabel("Director:               ");
	private JLabel statusLabel=new JLabel("Status:      ");
	private JLabel ratingLabel=new JLabel("Rating:      ");
	private JLabel ageRestrictionLabel=new JLabel("Age Restriction:        ");

	private JTextField IDTF= new JTextField(10);
	private JTextField titleTF=new JTextField(10);
	private JTextField genreTF=new JTextField(10);
	private JTextField yearTF=new JTextField(10);
	private JTextField lengthTF=new JTextField(10);
	private JTextField directorTF=new JTextField(10);
	private JTextField statusTF=new JTextField(10);
	private JTextField ratingTF=new JTextField(10);
	private JTextField ageRestrictionTF=new JTextField(10);


	private static QueryTableModel TableModel = new QueryTableModel();
	//Add the models to JTabels
	private JTable TableofDBContents=new JTable(TableModel);
	//Buttons for inserting, and updating members
	//also a clear button to clear details panel
	private JButton updateButton = new JButton("Update");
	private JButton insertButton = new JButton("Insert");
	private JButton exportButton  = new JButton("Export");
	private JButton deleteButton  = new JButton("Delete");
	private JButton clearButton  = new JButton("Clear");
	private JButton genreChartButton = new JButton("Genre Chart");
	private JButton ratingChartButton = new JButton("Rating Chart");
	private JButton watchChartButton = new JButton("Watch Chart");
	private JButton ageChartButton = new JButton("Age Restriction Chart");
	
	private JButton numMovies = new JButton("NumMoviesForGenre:");
	private JTextField numMoviesTF = new JTextField(12);
	private JButton avgRatingGenre = new JButton("AvgRatingForGenre:");
	private JTextField avgRatingGenreTF  = new JTextField(12);
	private JButton ListAllWatched  = new JButton("ListAllWatched");
	private JButton ListAllUnwatched  = new JButton("ListAllUnwatched");
	
	private JLabel movieIdInput = new JLabel("ID of the movie you watched: ");
	private JButton updateMovieStatus = new JButton("Update Status");
	private JTextField updateMovieStatusTF = new JTextField(12);
	
	private JLabel movieIdInput2 = new JLabel("ID of the movie: ");
	private JTextField updateMovieRatingTF = new JTextField(12);
	private JLabel newMovieRating = new JLabel("New rating: ");	
	private JTextField newMovieRatingTF = new JTextField(12);
	private JButton updateMovieRatingButton = new JButton("Update Rating");
	

	public MainWindowContent(String aTitle) {	
		//setting up the GUI
		super(aTitle, false,false,false,false);
		setEnabled(true);

		initiate_db_conn();
		//add the 'main' panel to the Internal Frame
		content=getContentPane();
		content.setLayout(null);
		content.setBackground(Color.lightGray);
		lineBorder = BorderFactory.createEtchedBorder(15, Color.red, Color.black);

		//setup details panel and add the components to it
		detailsPanel=new JPanel();
		detailsPanel.setLayout(new GridLayout(11,2));
		detailsPanel.setBackground(Color.lightGray);
		detailsPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "CRUD Actions"));

		detailsPanel.add(IDLabel);			
		detailsPanel.add(IDTF);
		detailsPanel.add(titleLabel);		
		detailsPanel.add(titleTF);
		detailsPanel.add(genreLabel);		
		detailsPanel.add(genreTF);
		detailsPanel.add(yearLabel);	
		detailsPanel.add(yearTF);
		detailsPanel.add(lengthLabel);		
		detailsPanel.add(lengthTF);
		detailsPanel.add(directorLabel);
		detailsPanel.add(directorTF);
		detailsPanel.add(statusLabel);
		detailsPanel.add(statusTF);
		detailsPanel.add(ratingLabel);
		detailsPanel.add(ratingTF);
		detailsPanel.add(ageRestrictionLabel);
		detailsPanel.add(ageRestrictionTF);

		//setup details panel and add the components to it
		exportButtonPanel=new JPanel();
		exportButtonPanel.setLayout(new GridLayout(3,2));
		exportButtonPanel.setBackground(Color.lightGray);
		exportButtonPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Export Data"));
		exportButtonPanel.add(numMovies);
		exportButtonPanel.add(numMoviesTF);
		exportButtonPanel.add(avgRatingGenre);
		exportButtonPanel.add(avgRatingGenreTF);
		exportButtonPanel.add(ListAllWatched);
		exportButtonPanel.add(ListAllUnwatched);
		
		exportButtonPanel.setSize(380, 200);
		exportButtonPanel.setLocation(3, 300);
		content.add(exportButtonPanel);
		
		chartsPanel=new JPanel();
		chartsPanel.setLayout(new GridLayout(4,2));
		chartsPanel.setBackground(Color.lightGray);
		chartsPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Chart Data"));
		chartsPanel.setSize(250, 200);
		chartsPanel.setLocation(395, 300);
		chartsPanel.add(genreChartButton);
		chartsPanel.add(ratingChartButton);
		chartsPanel.add(watchChartButton);
		chartsPanel.add(ageChartButton);
		content.add(chartsPanel);
		
		statusPanel = new JPanel();
		statusPanel.setLayout(new GridLayout(3,1));
		statusPanel.setBackground(Color.lightGray);
		statusPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Update Status"));
		statusPanel.setSize(250, 200);
		statusPanel.setLocation(657, 300);
		statusPanel.add(movieIdInput);
		statusPanel.add(updateMovieStatusTF);
		statusPanel.add(updateMovieStatus);
		content.add(statusPanel);
		
		placeHolder = new JPanel();
		placeHolder.setBackground(Color.lightGray);
		placeHolder.setSize(125, 100);
		
		ratingPanel = new JPanel();
		ratingPanel.setLayout(new GridLayout(3,2));
		ratingPanel.setBackground(Color.lightGray);
		ratingPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Update Rating"));
		ratingPanel.setSize(250, 200);
		ratingPanel.setLocation(919, 300);
		ratingPanel.add(movieIdInput2);
		ratingPanel.add(updateMovieRatingTF);
		ratingPanel.add(newMovieRating);
		ratingPanel.add(newMovieRatingTF);
		ratingPanel.add(placeHolder);
		ratingPanel.add(updateMovieRatingButton);
		content.add(ratingPanel);

		insertButton.setSize(100, 30);
		updateButton.setSize(100, 30);
		exportButton.setSize (100, 30);
		deleteButton.setSize (100, 30);
		clearButton.setSize (100, 30);

		insertButton.setLocation(370, 10);
		updateButton.setLocation(370, 110);
		exportButton.setLocation (370, 160);
		deleteButton.setLocation (370, 60);
		clearButton.setLocation (370, 210);

		insertButton.addActionListener(this);
		updateButton.addActionListener(this);
		exportButton.addActionListener(this);
		deleteButton.addActionListener(this);
		clearButton.addActionListener(this);

		this.numMovies.addActionListener(this);
		this.avgRatingGenre.addActionListener(this);
		this.ListAllWatched.addActionListener(this);
		this.ListAllUnwatched.addActionListener(this);
		this.genreChartButton.addActionListener(this);
		this.ratingChartButton.addActionListener(this);
		this.watchChartButton.addActionListener(this);
		this.ageChartButton.addActionListener(this);
		this.updateMovieStatus.addActionListener(this);
		this.updateMovieRatingButton.addActionListener(this);

		content.add(insertButton);
		content.add(updateButton);
		content.add(exportButton);
		content.add(deleteButton);
		content.add(clearButton);
	

		TableofDBContents.setPreferredScrollableViewportSize(new Dimension(900, 300));

		dbContentsPanel=new JScrollPane(TableofDBContents,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		dbContentsPanel.setBackground(Color.lightGray);
		dbContentsPanel.setBorder(BorderFactory.createTitledBorder(lineBorder,"Database Content"));

		detailsPanel.setSize(360, 300);
		detailsPanel.setLocation(3,0);
		dbContentsPanel.setSize(700, 300);
		dbContentsPanel.setLocation(477, 0);

		content.add(detailsPanel);
		content.add(dbContentsPanel);

		setSize(1200,645);
		setVisible(true);

		TableModel.refreshFromDB(stmt);
	}

	public void initiate_db_conn() {
		try {
			// Load the JConnector Driver
			Class.forName("com.mysql.cj.jdbc.Driver");
			// Specify the DB Name
			String url="jdbc:mysql://localhost:3306/assignment";
			// Connect to DB using DB URL, username and password
			con = DriverManager.getConnection(url, "root", "12345");
			//Create a generic statement which is passed to the TestInternalFrame1
			stmt = con.createStatement();
		}
		catch(Exception e) {
			System.out.println("Error: Failed to connect to database\n"+e.getMessage());
		}
	}
	public  void pieGraph(ResultSet rs, String title) {
		try {
			DefaultPieDataset dataset = new DefaultPieDataset();

			while (rs.next()) {
				String category = rs.getString(1);
				String value = rs.getString(2);
				dataset.setValue(category+ " "+value, new Double(value));
			}
			JFreeChart chart = ChartFactory.createPieChart(
					title,  
					dataset,             
					false,              
					true,
					true
			);

			ChartFrame frame = new ChartFrame(title, chart);
			chart.setBackgroundPaint(Color.WHITE);
			frame.pack();
			frame.setVisible(true);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//event handling 
	public void actionPerformed(ActionEvent e) {
		Object target=e.getSource();
		ResultSet rs=null;
		String query = null;
		
		if (target == clearButton) {
			IDTF.setText("");
			titleTF.setText("");
			genreTF.setText("");
			yearTF.setText("");
			lengthTF.setText("");
			directorTF.setText("");
			statusTF.setText("");
			ratingTF.setText("");
			ageRestrictionTF.setText("");

		}

		if (target == insertButton) {		 
			try {
				String updateTemp ="INSERT INTO movies VALUES("+
				null +",'"+titleTF.getText()+"','"+genreTF.getText()+"',"+yearTF.getText()+",'"+lengthTF.getText()+"','"
				+directorTF.getText()+"','"+statusTF.getText()+"',"+ratingTF.getText()+","+ageRestrictionTF.getText()+");";

				stmt.executeUpdate(updateTemp);

			}
			catch (SQLException sqle) {
				System.err.println("Error with  insert:\n"+sqle.toString());
			}
			finally {
				TableModel.refreshFromDB(stmt);
			}
		}
		
		if (target == deleteButton) {
			try {
				String updateTemp ="DELETE FROM movies WHERE id = "+IDTF.getText()+";"; 
				stmt.executeUpdate(updateTemp);

			}
			catch (SQLException sqle) {
				System.err.println("Error with delete:\n"+sqle.toString());
			}
			finally {
				TableModel.refreshFromDB(stmt);
			}
		}
		
		if (target == updateButton) {	 	
			try { 			
				String updateTemp ="UPDATE movies SET " +
				"title = '"+titleTF.getText()+
				"', genre = '"+genreTF.getText()+
				"', year = "+yearTF.getText()+
				", length ='"+lengthTF.getText()+
				"', director = '"+directorTF.getText()+
				"', status = '"+statusTF.getText()+
				"', rating = "+ratingTF.getText()+
				", age_restriction = "+ageRestrictionTF.getText()+
				" where id = "+IDTF.getText();


				stmt.executeUpdate(updateTemp);
				//these lines do nothing but the table updates when we access the db.
				rs = stmt.executeQuery("SELECT * from movies ");
				rs.next();
				rs.close();	
			}
			catch (SQLException sqle) {
				System.err.println("Error with  update:\n"+sqle.toString());
			}
			finally {
				TableModel.refreshFromDB(stmt);
			}
		}

		if(target == this.numMovies) {
			String genreName = this.numMoviesTF.getText();

			query = "SELECT genre, COUNT(*) "+  "FROM movies " + "WHERE genre = '"  +genreName+"';";

			System.out.println(query);
			try {					
				rs= stmt.executeQuery(query); 	
				writeToFile(rs);
			}
			catch(Exception e1){e1.printStackTrace();}

		}
		
		if(target == this.avgRatingGenre) {
			query = "SELECT avg(rating) FROM movies where genre = '"+ this.avgRatingGenreTF.getText() +"';";
			System.out.println(query);
			try {					
				rs= stmt.executeQuery(query); 	
				writeToFile(rs);
			}
			catch(Exception e1)	{ e1.printStackTrace(); }

		}
		
		if(target == this.ListAllWatched) {
			query = "SELECT title FROM movies WHERE status = 'Watched';";
			try {					
				rs= stmt.executeQuery(query); 	
				writeToFile(rs);
			}
			catch(Exception e1)	{ e1.printStackTrace(); }

		}

		if(target == this.ListAllUnwatched) {
			query = "SELECT title FROM movies WHERE status = 'Unwatched';";
			System.out.println(query);
			try {					
				rs= stmt.executeQuery(query); 	
				writeToFile(rs);
			}
			catch(Exception e1)	{ e1.printStackTrace(); }

		}
		
		// chart buttons
		if (target == genreChartButton)
		{
			query = "SELECT genre, COUNT(*) FROM movies GROUP BY genre;";
			try {
				rs= stmt.executeQuery(query);
			} 
			catch (SQLException e1) {
				e1.printStackTrace();
			} 
			pieGraph(rs, "Genre");	
		}
		
		if (target == ratingChartButton)
		{
			query = "SELECT rating, COUNT(*) FROM movies GROUP BY rating;";
			try {
				rs= stmt.executeQuery(query);
			}
			catch (SQLException e1) {
				e1.printStackTrace();
			} 
			pieGraph(rs, "Rating");	
		}
		
		if (target == watchChartButton) {
			query = "SELECT status, COUNT(*) FROM movies GROUP BY status;";
			try {
				rs= stmt.executeQuery(query);
			}
			catch (SQLException e1) {
				e1.printStackTrace();
			} 
			pieGraph(rs, "Watched");	
		}
		
		if (target == ageChartButton) {
			query = "SELECT age_restriction, COUNT(*) FROM movies GROUP BY age_restriction;";
			try {
				rs= stmt.executeQuery(query);
			}
			catch (SQLException e1) {
				e1.printStackTrace();
			} 
			pieGraph(rs, "Age Restrictions");
		}
		
		// stored procedure
		if(target == updateMovieStatus) {
			int movieID = Integer.parseInt(this.updateMovieStatusTF.getText());
			query = "call update_status(" + movieID + ");";
			try {
				stmt.executeUpdate(query);
			}
			catch (SQLException sqle)
			{
				System.err.println("Error with update:\n"+sqle.toString());
			}
			finally
			{
				TableModel.refreshFromDB(stmt);
			}
		}
		
		if(target == updateMovieRatingButton) {
			int movieID = Integer.parseInt(this.updateMovieRatingTF.getText());
			float newRating = Float.parseFloat(this.newMovieRatingTF.getText());
			query = "call update_rating(" + movieID + ", " + newRating + ");";
			try {
				stmt.executeUpdate(query);
			}
			catch (SQLException sqle)
			{
				System.err.println("Error with update:\n"+sqle.toString());
			}
			finally
			{
				TableModel.refreshFromDB(stmt);
			}
		}

	}
	
	///////////////////////////////////////////////////////////////////////////

	private void writeToFile(ResultSet rs){
		try{
			System.out.println("In writeToFile");
			FileWriter outputFile = new FileWriter("Birce.csv");
			PrintWriter printWriter = new PrintWriter(outputFile);
			ResultSetMetaData rsmd = rs.getMetaData();
			int numColumns = rsmd.getColumnCount();

			for(int i=0;i<numColumns;i++){
				printWriter.print(rsmd.getColumnLabel(i+1)+",");
			}
			printWriter.print("\n");
			while(rs.next()){
				for(int i=0;i<numColumns;i++){
					printWriter.print(rs.getString(i+1)+",");
				}
				printWriter.print("\n");
				printWriter.flush();
			}
			printWriter.close();
		}
		catch(Exception e){e.printStackTrace();}
	}
}
