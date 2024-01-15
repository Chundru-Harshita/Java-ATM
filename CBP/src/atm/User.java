package atm;

import java.util.ArrayList;
import java.util.Random;

public class User extends Bank {

	private String fname, lname, address;
	protected long userid, phno;
	private ArrayList<Account> accounts;
	
	
	
	User(String fname, String lname, long phno) {
		this.fname=fname;
		this.lname=lname;
		this.phno=phno;
	}

}
