package atm;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ATM {
	static void line() {
		System.out.println("\n**********************************************************************************************************************************************************************************************************");
	}
	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		// TODO Auto-generated method stub
		
		//checking if user is registered with bank--- 
		//if No--- register user --- then ask them to create account
		//if Yes ---ask if they have account with the bank
		//if no --- create account
		//if yes --- ask them to login
		Scanner sc = new Scanner(System.in);
//		System.out.print("Aleady existing user? (y/N): ");
//		String ch=sc.next();
//		if(ch.equalsIgnoreCase("y")) {
//			System.out.print("Aleady have an account ? (y/N): ");
//			String p=sc.next();
//			if(p.equalsIgnoreCase("n"))
//				Bank.createAccount();
//		}
//		else {
//			Bank.createUser();
//		}
		int x;
		do {
		System.out.println("\n************************************************************************************************** ONLINE BANK ********************************************************************************************");
		System.out.println("\n1. Create User\n2. Create Account\n3. Login");
		System.out.print("\nEnter option: ");
		x=sc.nextInt();
		switch(x){
			case 1: Bank.createUser();
					break;
			case 2: Bank.createAccount();
					break;
		}
		}while(x<=2);
		
		//connecting to DB
		Class.forName("com.mysql.jdbc.Driver");
		Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/java","root","root");
		System.out.println("Connection is established");
		Statement st=con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
		
		ResultSet re1 = st.executeQuery("select * from accounts");
		PreparedStatement ps1=con.prepareStatement("insert into transactions value(?,?,?,?,?,?)");
		int p=0,accountId,pin;
		
		//enter login details
		line();
		System.out.println("\n\nLogin to your account");
		int wrong_attempts=0;
		do {
			System.out.print("\nEnter Account Number: ");
			accountId = sc.nextInt();
			wrong_attempts++;
			System.out.print("Enter pin: ");
			pin = sc.nextInt();
			re1.beforeFirst();
			while(re1.next()) {
				if(re1.getInt(1)==accountId&&re1.getInt(4)==pin) {
					p=1;
					break;
				}
			}
			if(p==0) System.out.println("Account Number or Pin is invalid");
		}while(p==0&&wrong_attempts<3);
		if(wrong_attempts==3) {
			System.out.println("\n3 attempts done");
			System.exit(0);
		}
		
		//Main Menu:
		int t;
		do {
			System.out.println("\n************************************************************************************************** MAIN MENU ********************************************************************************************");
			System.out.print("\n\n1. Check Balance\n2. Deposit\n3. Withdraw\n4. Transaction History\n5. Transfer amount\n6. Pin Change\n7. Exit\n\n Enter Option: ");
			t=sc.nextInt();
			
			//setting account number and the account to which we are tranferring money in the transfers table
			ps1.setLong(1, accountId);
			ps1.setLong(4, 0);
			
			switch(t){
			
				case 1:	{
							//getting the balance from accounts table everytime we choose option 1. and printing it
							ResultSet re2 = st.executeQuery("select * from accounts");
							re2.beforeFirst();
							while(re2.next()) {
								if(re2.getInt(1)==accountId)
									break;
							}
							System.out.println("Balance: Rs."+re2.getDouble(3));
						}
						break;
						
				case 2: {
							//get balance from accounts table
							ResultSet re2 = st.executeQuery("select * from accounts");
							re2.beforeFirst();
							while(re2.next()) {
								if(re2.getInt(1)==accountId)
									break;
							}
							
							//entering the amount to deposit
							System.out.print("Enter ammount to deposit: ");
							double inc = sc.nextInt();
							
							//updating the balance in accounts table
							PreparedStatement ps=con.prepareStatement("update accounts set balance=? where accno=?");
							ps.setDouble(1, (inc+re2.getDouble(3)));
							ps.setInt(2, accountId);
							
							//updating the transfers table by adding the transaction name (deposit) , time, amount, and final balance
							ps1.setString(2, "Deposited Rs. ");
							SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
						    Date date = new Date();  
							ps1.setString(3, formatter.format(date));	
							ps1.setDouble(5, inc);
							ps1.setDouble(6, (inc+re2.getDouble(3)));
							int res = ps1.executeUpdate();
							int re = ps.executeUpdate();
							
							//displaying new balance after successful deposit
							System.out.println("Balanace: Rs."+(inc+re2.getDouble(3)));
						}
						break;
						
				case 3: {
					
							//get balance from accounts table
							ResultSet re2 = st.executeQuery("select * from accounts");
							re2.beforeFirst();
							while(re2.next()) {
								if(re2.getInt(1)==accountId)
									break;
							}
							double dec,amt;
							
							//entering amount to withdraw and making sure it is less than account balance
							do {
								System.out.print("\nEnter ammount to withdraw: ");
								dec = sc.nextInt();
								amt = re2.getDouble(3)-dec;
								if(dec<0) {
									System.out.println("Balance not suffecient. Try again");
								}
							}while (dec<0);
							
							//updating the account balance after withdrawing money
							PreparedStatement ps=con.prepareStatement("update accounts set balance=? where accno=?");
							ps.setDouble(1, amt);
							ps.setInt(2, accountId);
							
							//updating the transaction history in the transactions table
							ps1.setString(2, "Withdrawn Rs. ");
							SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
						    Date date = new Date();  
							ps1.setString(3, formatter.format(date));
							ps1.setDouble(5, dec);	
							ps1.setDouble(6, amt);	
							int res = ps1.executeUpdate();
							int re = ps.executeUpdate();
							
							//printing the balance after successful withdraw
							System.out.println("Balanace: Rs."+amt);
						}
						break;
						
				case 4:	{
							//printing the transaction history
							ResultSet re2 = st.executeQuery("select * from transactions");
							re2.beforeFirst();
							while(re2.next()) {
								
								//for deposit, withdraw
								if(re2.getLong(1)==accountId&&re2.getLong(4)==0)
									System.out.println(re2.getString(2)+re2.getDouble(5)+" at "+re2.getString(3)+" ending at "+re2.getDouble(6));
								
								//for money tranfer to another account, printing the account number to which money is transferred
								else if(re2.getLong(1)==accountId)
									System.out.println(re2.getString(2)+re2.getDouble(5)+" to "+re2.getLong(4)+" at "+re2.getString(3)+" ending at "+re2.getDouble(6));
							}
						}
						break;
						
				case 5:	{
							ResultSet re2 = st.executeQuery("select * from accounts");
							re2.beforeFirst();
							double bal = 0;
							while(re2.next()) {
								if(re2.getInt(1)==accountId) {
									bal=re2.getDouble(3);
									break;
								}
							}
							
							System.out.println("Enter account number to transfer amount:");
							int en=sc.nextInt();
							double m;
							do {					
								System.out.println("Enter amount:");
								m=sc.nextDouble();
							}while(bal-m<0);
							
							//sender
							PreparedStatement pst=con.prepareStatement("update accounts set balance=balance-? where accno= ?");
							pst.setDouble(1,m);
							pst.setInt(2,accountId);
							
							int r=pst.executeUpdate();
							
							//reciever
							String query = "update accounts set balance=balance+? where accno= ?";
							PreparedStatement pmt=con.prepareStatement(query);
		
							pmt.setDouble(1,m);
							pmt.setInt(2,en);
		
							int r1=pmt.executeUpdate();
							
							ps1.setString(2, "Transferred Rs. ");
							SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
						    Date date = new Date();  
							ps1.setString(3, formatter.format(date));
							ps1.setLong(4, en);
							ps1.setDouble(5, m);	
							ps1.setDouble(6, re2.getDouble(3)-m);	
							int res = ps1.executeUpdate();
							
							if(r1==1&&r==1)
							System.out.println("RECORD UPDATED SUCCESSFULLY");
							else
							System.out.println("ERROR IN UPDATION!!!");
						}
						break;
						
				case 6:	{
							String query = "update accounts set pin=? where pin=? and accno= ?";
							PreparedStatement  pmt=con.prepareStatement(query);
		
							System.out.println("Enter old pin");
							int p1=sc.nextInt();
							System.out.println("Enter new pin");
						    int pa=sc.nextInt();
		
							pmt.setInt(3,accountId);
							pmt.setInt(1,pa);
							pmt.setInt(2,p1);
		
							int r=pmt.executeUpdate();
							if(r>0) {
								System.out.println("Pin UPDATED SUCCESSFULLY");
							}
							else {
								System.out.println("Incorrect old pin!!!");
							}
						}
						break;
			}
			System.out.println("\n");
		}while(t<=6);
	}

}
