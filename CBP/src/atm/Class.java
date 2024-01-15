//package atm;
//import javax.swing.*;
//public class Class {
//	public static void main(String[] args) {
//	final String password, message = "Enter password";
//	if( System.console() == null ) 
//	{ // inside IDE like Eclipse or NetBeans
//	  final JPasswordField pf = new JPasswordField(); 
//	  password = JOptionPane.showConfirmDialog( null, pf, message,
//	    JOptionPane.OK_CANCEL_OPTION,
//	    JOptionPane.QUESTION_MESSAGE ) == JOptionPane.OK_OPTION ? 
//	      new String( pf.getPassword() ) : "";
//	}
//	else 
//	  password = new String( System.console().readPassword( "%s> ", message ) );
//	System.out.println(password);
//	}
//}