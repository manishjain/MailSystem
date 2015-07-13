# EmailQueue Dependency

1. mysql-connector-java-bin.jar
2. javamail.jar

Steps to Generate Data:

1. Create a mysql db named mail_system
2. Set db credentials(USER and PASS) in EmailQueue.java and InsertEmailQueue.java
3. Create table in database by running create_EmailQueue.sql
4. Populate entries in database table by first specifying EntryCount, fromMail & toMail. Run InsertEmailQueue will populate so many entries in db.

Specifications of Email Queue:

DB specifications:
1. id: It is incremental id for email, unique for all emails.
2. from_mail: It is be a string field having from email address.
3. to_mail: It is a string field having to email address.
4. subject: It is a string field having subject of email.
5. body: It is a text field having body of email.
6. state: It is an enum field havind three values possible: 'N' for not done/pending, 'W' for Working,
   and 'D' for done. Initially all emails will be in 'N' state.
7. sent_on: will be a timestamp having initially value as NULL. After mail is sent it will be having 
   the value of time it was sent.

Workflow specifications:
1. EmailQueue is a multithreaded script to send mail.
2. It first create a single session to the smtp server for mail sending(In this case by giving gmail 
   smtp credentials in EmailAdapter class).
3. It then generates a number of threads equal to THREAD_COUNT. 
4. Each thread will get a new db connection to communicate with db.
5. It uses "Select.. for update" query inside a db transaction to achieve a row level lock which will
   prevent concurrent access to the same row while it is picked for execution.
6. Any thread will select & update an email entry to 'W' state, and then starts processing the email 
   regarding it. Because of row level lock, no other thread will be able to read that row in between this time.
7. The thread will process the email by instiating EmailAdapter class and send mail.
8. After mail is sent, It will again update the state of the entry to 'D'(for done) and also put the sent 
   time of mail.
9. This script if it run of different machines, will again improves the performance without any 
   concurrent access to the same entry.