import com.sun.xml.internal.ws.handler.ClientMessageHandlerTube;

import java.util.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class User implements Serializable
{
	private String name;
	private String password;
	private String username;
	private String contactNo;

	User(String username,String name,String contactNumber,String password) {
		this.setName(name);
		this.setContactNumber(contactNumber);
		this.setPassword(password);
		this.setUsername(username);
	}
	User() {}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getContactNumber() {
		return contactNo;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNo = contactNumber;
	}

}

class Booking implements Serializable
{
	public ArrayList<String> Cities=new ArrayList<String>();
	public HashMap<String, ArrayList> buses = new HashMap<String, ArrayList>();
	public ArrayList<customer> customer_list = new ArrayList<customer>();
	public void add_cities()
	{
		Cities.add("Mumbai");
		Cities.add("Ahemdabad");
		Cities.add("Kolhapur");
		Cities.add("Nashik");
		Cities.add("Indore");
		Cities.add("Nagpur");
		Cities.add("Jalgaon");
	}
	public void add_buses()
	{
		for(String c:Cities)
		{
			ArrayList<Integer> bus = new ArrayList<Integer>();
			for(Integer i=0;i<40;i++)
				bus.add(0);
			buses.put(c,bus);
		}
	}
	public String add_city(String city)
	{
		if(!Cities.contains(city))
		{
			Cities.add(city);
			add_bus(city);
			System.out.println("City added");
			return "Added!";
		}
		System.out.println("City already added");
		return "Error!";
	}
	public String delete_city(String city)
	{
		if(Cities.contains(city)) 
		{
			Cities.remove(city);
			buses.remove(city);
			System.out.println("City deleted");
			return "Deleted!";
		}
		System.out.println("City not present!");
		return "Error!";
	}
	public String add_bus(String city)
	{
		if(Cities.contains(city))
		{
			ArrayList<Integer> bus=new ArrayList<Integer>();
			for(Integer i=0;i<40;i++)
				bus.add(0);
			buses.put(city,bus);
			System.out.println("Bus added");
			return "Added!";
		}
		System.out.println("City not Available");
		return "Error!";
	}
	public String cancel_bus(String city)
	{
		if(Cities.contains(city))
		{
			buses.remove(city);
			return "Removed";
		}
		System.out.println("City not present");
		return  "Error!";
	}
	public String book(customer c)
	{
		int seat;
		int number_of_passengers = c.c1.getNumber_of_passengers();
		String dest_city=c.c1.getDestination_city();
		ArrayList<String> passengers = c.c1.getPassenger();
		ArrayList<Integer> seat_no = c.c1.getSeat_no();
		Random rand=new Random();
		if(!Cities.contains(dest_city))
		{
			return "Sorry!! This city is not available.";
		}
		if(!buses.containsKey(dest_city))
		{
			return "Bus is cancelled for this city";
		}
		ArrayList <Integer> bus=buses.get(dest_city);
		//available(c.destination_city);
		if(!bus.contains(0))
		{
			return "This Bus is Full!!";
		}
		for(int i=0;i<number_of_passengers;i++)
		{
			seat=seat_no.get(i);
			if(bus.get(seat-1)==0)
			{
				bus.set(seat-1,1);
				seat_no.add(seat);
			}
			else
			{
				return "Seat is already booked!!";
			}
		}
		String city=dest_city;
		buses.replace(city,bus);
		c.booking_id=rand.nextInt(9999999);
		//System.out.println("Tickets are Booked!!");
		//c.print_details();
		customer_list.add(c);
		c.bookings.put(c.booking_id,c.c1);
		return "Booked!";
	}
	public Object check(int booking_id)
	{
		for(customer cust:customer_list)
		{
			if(cust.booking_id==booking_id)
				return cust;
		}
		return null;
	}
	public String cancel(int booking_id)
	{
		customer c = (customer) check(booking_id);
		if(c==null)
			return "Error!";
		c.c1 = c.bookings.get(booking_id);
		String dest_city = c.c1.getDestination_city();
		int number_of_passengers = c.c1.getNumber_of_passengers();
		ArrayList<Integer> seat_no = c.c1.getSeat_no();
		ArrayList<Integer> bus = buses.get(dest_city);
		for(int i=0;i<number_of_passengers;i++)
		{
			bus.set(seat_no.get(i)-1,0);
		}
		buses.replace(dest_city,bus);
		int i=0;
		for(customer cust:customer_list)
		{
			if(cust.booking_id==booking_id)
			{
				customer_list.remove(i);
				break;
			}
			i++;
		}
		c.bookings.remove(booking_id);
		return "Cancelled";
	}
	public ArrayList<Integer> available(String city) {
		if (Cities.contains(city)) {
			if (buses.containsKey(city)) {
				ArrayList<Integer> bus = buses.get(city);
				return bus;
			}
		}
		return null;
	}
}

class ClientHandler extends Thread
{
	BufferedReader inFromClient;
	DataOutputStream outToClient;
	ObjectInputStream is;
	ObjectOutputStream os;
	Booking b;

	ClientHandler()
	{

	}

	void run(String args[]) throws Exception
	{
		String clientSentence,serverSentence;
		int booking_id;

			if(!admin)
			{
				while(true)
				{
					clientSentence = (String) inFromClient.readLine();
					System.out.println(clientSentence);
					if(clientSentence.equals("6"))
					{
						break;
					}
					switch(clientSentence) {
						case "1":
							customer cust = (customer) is.readObject();
							serverSentence = b.book(cust);
							outToClient.writeBytes(serverSentence + "\n");
							if(serverSentence.equals("Booked!"))
								os.writeObject(cust);
							break;
						case "2":
							clientSentence = inFromClient.readLine();
							booking_id = Integer.parseInt(clientSentence);
							serverSentence = b.cancel(booking_id);
							outToClient.writeBytes(serverSentence + "\n");
							break;
						case "3":
							clientSentence = inFromClient.readLine();
							booking_id = Integer.parseInt(clientSentence);
							customer c = (customer) b.check(booking_id);
							if (c != null) {
								outToClient.writeBytes("Yes" + "\n");
								os.writeObject(c);
							} else
								outToClient.writeBytes("Error!");
							break;
						case "4":
							clientSentence = inFromClient.readLine();
							ArrayList<Integer> bus = b.available(clientSentence);
							if (bus != null) {
								outToClient.writeBytes("Success" + "\n");
								os.writeObject(bus);
							} else
								outToClient.writeBytes("Error!" + "\n");
							break;
						case "5":
							os.writeObject(b.Cities);
							break;
					}
				}
			} else {
				while(true)
				{
					clientSentence = (String) inFromClient.readLine();
					System.out.println(clientSentence);
					if(clientSentence.equals("6"))
					{
						break;
					}
					switch(clientSentence)
					{
						case "1":
							clientSentence = inFromClient.readLine();
							serverSentence = b.add_city(clientSentence);
							outToClient.writeBytes(serverSentence+"\n");
							break;
						case "2":
							clientSentence = inFromClient.readLine();
							serverSentence = b.delete_city(clientSentence);
							outToClient.writeBytes(serverSentence+"\n");
							break;
						case "3":
							clientSentence = inFromClient.readLine();
							serverSentence = b.add_bus(clientSentence);
							outToClient.writeBytes(serverSentence+"\n");
							break;
						case "4":
							clientSentence = inFromClient.readLine();
							serverSentence = b.cancel_bus(clientSentence);
							outToClient.writeBytes(serverSentence+"\n");
							break;
						case "5":
							os.writeObject(b.customer_list);
							break;
					}
				}
			}

			clientSentence = inFromClient.readLine();
			if(clientSentence.equals("end")) {
				connectionSocket.close();
			} else {
				System.out.println("Connection Live");
			}
		}
	}
}

public class Server
{
	public Vector<User> Users = new Vector<User>();
	Server() {
		User a = new User("varunkarwa","Varun","7617209448","varunkarwa");
		Users.add(a);
		User b = new User("mpm1712","Miti","8378099502","mitimehta");
		Users.add(b);
	}
	public boolean checkUser(String username) {
		for(User s : Users) {
			if(s.getUsername().equals(username)) {
				return true;
			}
		}
		return false;
	}

	public User validatingUser(String usern, String pass) {
		for(User s: Users) {
			if(s.getUsername().equals(usern)) {
				if(s.getPassword().equals(pass)) {
					return s;
				} else {
					return null;
				}
			}
		}

		return null;
	}
	public static void main(String args[]) throws IOException {
		String clientSentence;
		String serverSentence;
		Booking b = new Booking();
		ServerSocket welcomeSocket = new ServerSocket(4353);
		System.out.println("ServerSocket awaiting connection...");
		Socket connectionSocket = welcomeSocket.accept();
		System.out.println("Connection from " + connectionSocket);
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		ObjectInputStream is = new ObjectInputStream(connectionSocket.getInputStream());
		ObjectOutputStream os = new ObjectOutputStream(connectionSocket.getOutputStream());
		DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
		Server s = new Server();
		b.add_cities();
		b.add_buses();

		while(true) {
			System.out.println("Waiting for Input");
			boolean isAuth = false;
			boolean admin = false;
			while (!isAuth) {
				String username = inFromClient.readLine();
				if (username.equals("admin")) {
					System.out.println("Getting password for admin");
					//outToClient.writeBytes("Enter password for admin" + "\n");
					String password = inFromClient.readLine();
					if (password.equals("admin")) {
						System.out.println("Welcome admin");
						outToClient.writeBytes("Welcome admin" + "\n");
						admin = true;
						break;
					} else {
						outToClient.writeBytes("Invalid password!!");
					}
				}
				System.out.println("Username:" + username);
				if (s.checkUser(username)) {
					System.out.println("User Present");
					outToClient.writeBytes("User Present" + "\n");
				} else {
					System.out.println("Username invalid");
					outToClient.writeBytes("Username invalid" + "\n");
					continue;
				}
				String pass = inFromClient.readLine();
				User p = s.validatingUser(username, pass);
				if (p != null) {
					outToClient.writeBytes("Login Successful" + "\n");
					os.writeObject(p);
					isAuth = true;
					break;
				} else {
					outToClient.writeBytes("Credentials false" + "\n");
				}
			}
		}

		clientSentence = inFromClient.readLine();
		if(clientSentence.equals("end")) {
			connectionSocket.close();
		} else {
			System.out.println("Connection Live");
		}
	}
}