import java.sql.*;
import javax.sql.*;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailQueue{
	static int THREAD_COUNT = 10;

	public static void main(String args[]){

  //Print Initial Time
	// java.util.Date d1 = new java.util.Date();
	// Timestamp t1 = new Timestamp(d1.getTime());
	// System.out.println(t1);

		EmailAdapter.setSession();
		EmailThread[] emailThead = new EmailThread[THREAD_COUNT];
		for(int i=0;i<THREAD_COUNT; i++){
			emailThead[i] = new EmailThread("Thread "+i);
			emailThead[i].start();
		}



	}
} 

class EmailThread extends Thread{

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/mail_system";

	//  Database credentials
	static final String USER = "root";
	static final String PASS = "";
	static String getQuery = "Select * FROM EmailQueue where state='N' order by id limit 1 for update";
	static String workingQuery = "Update EmailQueue set state = 'W' where id= ?";
	static String doneQuery = "Update EmailQueue set state = 'D', sent_on=CURRENT_TIMESTAMP where id= ?";


	EmailThread(String s){
		super(s);
	}
	public void run(){
		try{
			Class.forName(JDBC_DRIVER);
			Connection con = DriverManager.getConnection (DB_URL, USER, PASS);
			Statement stmt = con.createStatement();
			PreparedStatement pStmtWorking = con.prepareStatement(workingQuery);
			PreparedStatement pStmtDone = con.prepareStatement(doneQuery);

			while(true){
				con.setAutoCommit(false);

				ResultSet rs = stmt.executeQuery(getQuery);
				if(rs.next()) {
					String messageId = rs.getString(1);
					String from      = rs.getString(2);
					String to        = rs.getString(3);
					String subject   = rs.getString(4);
					String body      = rs.getString(5);
					pStmtWorking.setString (1, messageId);
					pStmtWorking.execute();
					con.commit();
					con.setAutoCommit(true);
					System.out.println("Sending Message with id "+ messageId + 
						" by thread "+ Thread.currentThread().getName());

					EmailAdapter emailAdapter = new EmailAdapter(messageId, from, to, subject, body);
					emailAdapter.send();

					pStmtDone.setString(1, messageId);
					pStmtDone.execute();
				}
				else{
					con.commit();
					break;
				}
			}

  // print final time of each thread
	// java.util.Date d2 = new java.util.Date();
	// Timestamp t2 = new Timestamp(d2.getTime());
	// System.out.println(t2);

			con.close();
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
}

class EmailAdapter {
	private String messageId;
	private Message message;
	private static Session session;

  public static void setSession(){  
    final String username = "username";//change accordingly
    final String password = "pass";//change accordingly
    String host = "smtp.gmail.com";
    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.port", "587");

    // Get the Session object.
    session = Session.getInstance(props,
      new javax.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(username, password);
        }
      });
  }

	EmailAdapter(String messageId, String from, String to, String subject, String body){  
		try {

		this.messageId = messageId;

	  // Create a default MimeMessage object.
		this.message = new MimeMessage(session);

		// Set From: header field of the header.
    this.message.setFrom(new InternetAddress(from));

    // Set To: header field of the header.
    this.message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(to));

    // Set Subject: header field
    this.message.setSubject(subject);

    // Now set the actual message
    this.message.setText(body);
    } catch (MessagingException e) {
    	throw new RuntimeException(e);
    }


  }  

  public void send(){
  	try {
      // Send message
      Transport.send(this.message);
      System.out.println("Sent message "+ this.messageId + " successfully....");
    } catch (MessagingException e) {
    	throw new RuntimeException(e);
    }
  }
}
