package atm;

import java.util.ArrayList;

public class Account extends Bank{
	
	private User owner;
	protected long accno;
	protected double balance;
	private ArrayList<Transactions> transaction;
	protected String receipt;
	

}
