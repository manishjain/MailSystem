import java.sql.*;
import javax.sql.*;

public class InsertEmailQueue{

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/mail_system";

	//  Database credentials
	static final String USER = "root";
	static final String PASS = "";


	public static void main(String args[]){
		String fromMail = "from@sample.com";
		String toMail = "to@sample.com";
		String mailSubject = "Sample Mail Subject";
		String mailBody = "Sample Mail Body";
		int EntryCount = 20;

		try {

			Class.forName(JDBC_DRIVER);
			Connection con = DriverManager.getConnection (DB_URL, USER, PASS);

			// getting current timestamp
			/* 
			java.util.Date date = new java.util.Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			String query = "insert into EmailQueue (from_mail, to_mail, subject, body, sent_on)"
        + " values (?, ?, ?, ?, ?)";
      */
    
    // the mysql insert statement
			String query = "insert into EmailQueue (from_mail, to_mail, subject, body)"
			     + " values (?, ?, ?, ?)";
    // create the mysql insert preparedstatement
			for(int i=0; i<EntryCount; i++){

	      PreparedStatement preparedStmt = con.prepareStatement(query);
	      preparedStmt.setString (1, fromMail);
	      preparedStmt.setString (2, toMail);
	      preparedStmt.setString (3, mailSubject);
	      preparedStmt.setString (4, mailBody);
	      //preparedStmt.setTimestamp(5, timestamp);

	      // execute the preparedstatement
	      preparedStmt.execute();

			}

			con.close();
		} //end try

		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch(SQLException e) {
			e.printStackTrace();
		}

	}

} 