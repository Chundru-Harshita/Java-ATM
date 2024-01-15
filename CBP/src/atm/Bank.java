package atm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.Scanner;

public class Bank {
	static void cls() {
		for(int i=0;i<50;i++) {
			System.out.println();
		}
	}
	public static void createUser() throws ClassNotFoundException, SQLException {
		
		//normal connection creation
		Class.forName("com.mysql.jdbc.Driver");
		Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/java","root","root");
		System.out.println("Connection is established");
		
		PreparedStatement ps=con.prepareStatement("insert into user value(?,?,?,?)");
		Statement st=con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
		ResultSet re1 = st.executeQuery("select * from user");
		
		//reading name and number from user
		cls();
		Scanner sc = new Scanner(System.in);
		System.out.println("User Creation");
		System.out.print("Enter first name: ");
		String fname = sc.next();
		System.out.print("Enter last name: ");
		String lname = sc.next();
		System.out.print("Enter phone number: ");
		Long phno;
		
		//making sure there is no user with same ph number
		int b,p;
		do {
			phno = sc.nextLong();
			b=0;
			re1.beforeFirst();
			while(re1.next()) {
				if(re1.getLong(4)==phno) {
					b=1;
					System.out.print("Account with *******"+(phno%1000)+" already exists\nEnter phone number again: ");
				}
			}
		}while(b==1);
		
		//creating user object and passing the details
		User u=new User(fname, lname, phno);
		Random x=new Random();
		
		//making sure there to randomly generate a unique userid to identify user
		do {
			u.userid=1000+x.nextInt(8999)+1;
			p=0;
			re1.beforeFirst();
			while(re1.next()) {
				if(re1.getLong(1)==u.userid) {
					p=1;
				}
			}
		}while(p==1);
		
		//entering the details into user table
		ps.setLong(1, u.userid);
		ps.setString(2, fname);
		ps.setString(3, lname);
		ps.setLong(4, phno);
		int re = ps.executeUpdate();
		System.out.print(re+" user created successfully!!\n");
		createAccount();
	}
	
	public static void createAccount() throws ClassNotFoundException, SQLException {
		
		//connecting to db
		Class.forName("com.mysql.jdbc.Driver");
		Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/java","root","root");
		System.out.println("Connection is established");
		
		PreparedStatement ps=con.prepareStatement("insert into accounts values(?,?,?,?)");
		Statement st=con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
		ResultSet re1 = st.executeQuery("select * from user");
		Scanner sc = new Scanner(System.in);
		
		cls();
		System.out.println("Account Creation");
		System.out.print("Enter phone number: ");
		
		//ensuring phonenum entered to create new account is existing 
		int b=0,p,userid=0;
		do {
			Long phno = sc.nextLong();
			re1.beforeFirst();
			while(re1.next()) {
				if(re1.getLong(4)==phno) {
					b=1;
					userid=re1.getInt(1);
				}
			}
			if(b==0)
				System.out.println("No user with the given ph no please try again: ");
		}while(b==0);
		
		//creating pin for new account
		System.out.print("Enter pin: ");
		int pin = sc.nextInt();
		
		//creating new account
		Account a=new Account();
		Random x=new Random();
		ResultSet re2 = st.executeQuery("select * from accounts");
		
		//creating and assigning an account num to account
		do {
			a.accno=10000+x.nextInt(89999)+1;
			p=0;
			re2.beforeFirst();
			while(re2.next()) {
				if(re2.getLong(1)==a.accno) {
					p=1;
				}
			}
		}while(p==1);
		
		//entering account details into table in db
		ps.setLong(1, a.accno);
		ps.setLong(2, userid);
		ps.setDouble(3, 0);
		ps.setInt(4, pin);
		int re = ps.executeUpdate();
		System.out.println(re+" no. of rows affected");
		System.out.println("Account created!\nAccount number is: "+a.accno);
	}
	
}
