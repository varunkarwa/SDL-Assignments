import java.io.*;
import java.lang.reflect.Array;
import java.net.Inet4Address;
import java.util.*;
import java.net.Socket;
import java.net.SocketException;

class Customer_booking implements Serializable
{
	private int number_of_passengers;
	private String destination_city;
	private ArrayList<Integer> seat_no = new ArrayList<Integer>();
	private ArrayList<String> passenger = new ArrayList<String>();
	public void set_destination_city(String destination_city) {
		this.destination_city = destination_city;
	}
	public void setNumber_of_passengers(int number_of_passengers) {
		this.number_of_passengers = number_of_passengers;
	}
	public void setPassenger(ArrayList<String> passenger)
	{
		int len = passenger.size();
		for(int i=0;i<len;i++)
		{
			this.passenger.add(passenger.get(i));
		}
	}
	public void setSeat_no(ArrayList<Integer> seat_no)
	{
		int len=seat_no.size();
		for(int i=0;i<len;i++)
			this.seat_no.add(seat_no.get(i));
	}
	public String getDestination_city()
	{
		return destination_city;
	}
	public int getNumber_of_passengers()
	{
		return number_of_passengers;
	}
	public ArrayList<String> getPassenger()
	{
		return passenger;
	}
	public ArrayList<Integer> getSeat_no()
	{
		return seat_no;
	}
}
class customer implements Serializable
{
	Customer_booking c1 = new Customer_booking();
	public String name,contact_number;
	int booking_id;
	HashMap<Integer,Customer_booking> bookings = new HashMap<Integer,Customer_booking>();
	public void set_customer(User us)
	{
		this.name = us.getName();
		this.contact_number = us.getContactNumber();
	}
	public void print_customer()
	{
		System.out.println("--------Customer Details--------");
		System.out.println("Name of the Customer:" + name);
		System.out.println("Contact Number:" + contact_number);
	}
	public void book_ticket()
	{
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter Destination City");
		String destination_city=sc.next();
		c1.set_destination_city(destination_city);
		System.out.println("Enter number of passengers");
		int number_of_passengers=Integer.parseInt(sc.next());
		c1.setNumber_of_passengers(number_of_passengers);
		ArrayList<String> passengers=new ArrayList<String>();
		ArrayList<Integer> seat_no = new ArrayList<Integer>();
		for(int i=0;i<number_of_passengers;i++)
		{
			System.out.println("Enter Passenger's Name");
			passengers.add(sc.next());
			System.out.println("Enter Seat No.:");
			seat_no.add(sc.nextInt());
		}
		c1.setPassenger(passengers);
		c1.setSeat_no(seat_no);
	}
	public void booking_details()
	{
		c1 = bookings.get(booking_id);
		int number_of_passengers = c1.getNumber_of_passengers();
		ArrayList<String> passengers = c1.getPassenger();
		ArrayList<Integer> seat_no = c1.getSeat_no();
		System.out.println("=====Customer Deatils=====");
		System.out.println("Booking Id:"+booking_id);
		System.out.println("Destination City:" + c1.getDestination_city());
		System.out.println("List of Passenger's:");
		for(Integer i=0;i<number_of_passengers;i++)
		{
			System.out.println(passengers.get(i));
			System.out.println("Seat Number:"+seat_no.get(i));
		}
	}
	public void display_bus(ArrayList<Integer> bus)
	{
		System.out.println("Seat Availables(0 means Available):");
		int i=0;
		while(i<bus.size())
		{
			System.out.print("\t"+bus.get(i++)+"\t"+bus.get(i++));
			System.out.println("\t"+bus.get(i++)+"\t"+bus.get(i++));
		}
	}
	public void display_cities(ArrayList<String> cities)
	{
		for(String city:cities)
			System.out.println(city);
	}
	public void display_customers(ArrayList<customer> list)
	{
		int i=1;
		System.out.println("------Customer List------");
		for(customer c:list)
		{
			System.out.println(i++ +". "+c.name);
			System.out.println("Booking Details");
			c.booking_details();
		}
	}
}
public class Client
{
	public static void main(String args[]) throws Exception {
		String serverSentence, choice;
		Scanner sc = new Scanner(System.in);

		Socket clientSocket = new Socket("localhost", 4353);
		System.out.println("Connected to Server");

		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		ObjectOutputStream os = new ObjectOutputStream(clientSocket.getOutputStream());
		ObjectInputStream is = new ObjectInputStream(clientSocket.getInputStream());

		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		while (true) {
			String booking_id, city;
			customer c = new customer();
			ArrayList<customer> list = new ArrayList<customer>();
			String username, password;
			ArrayList<String> city_list = new ArrayList<String>();
			ArrayList<Integer> bus = new ArrayList<Integer>();
			User user = new User();
			boolean isAuth = false;
			boolean admin = false;
			while (!isAuth) {
				System.out.println("Enter username:");
				username = sc.next();
				if (username.equals("admin")) {
					outToServer.writeBytes(username + "\n");
					System.out.println("Enter password:");
					password = sc.next();
					outToServer.writeBytes(password + "\n");
					serverSentence = inFromServer.readLine();
					if (serverSentence.equals("Welcome admin")) {
						admin = true;
						break;
					} else {
						System.out.println("Invalid Credentials");
						continue;
					}
				}
				outToServer.writeBytes(username + "\n");
				serverSentence = inFromServer.readLine();
				if (serverSentence.equals("User Present")) {
					System.out.println("Enter Password");
					password = inFromUser.readLine();
					outToServer.writeBytes(password + "\n");
				} else {
					System.out.println("Invalid Credentials");
					continue;
				}
				serverSentence = inFromServer.readLine();
				System.out.println(serverSentence);
				if (serverSentence.equals("Login Successful")) {
					user = (User) is.readObject();
					c.set_customer(user);
					System.out.println("Welcome " + user.getName());
					isAuth = true;
					break;
				}
			}

			if (!admin) {
				c.print_customer();
				while (true) {
					System.out.println("--------BUS RESERVATION SYSTEM--------");
					System.out.println("1.Book Tickets\n2.Cancel Ticket(s)\n3.Check Reservation\n4.Seat Availability\n5.Cities Available\n6.Exit");
					choice = inFromUser.readLine();
					if (choice.equals("6")) {
						outToServer.writeBytes("6" + "\n");
						break;
					}
					switch (choice) {
						case "1":
							c.book_ticket();
							outToServer.writeBytes("1" + "\n");
							os.writeObject(c);
							serverSentence = (String) inFromServer.readLine();
							if (serverSentence.equals("Booked!")) {
								c = (customer) is.readObject();
								c.booking_details();
							} else {
								System.out.println("Error:" + serverSentence);
							}
							break;
						case "2":
							System.out.println("Enter booking_id:");
							booking_id = inFromUser.readLine();
							outToServer.writeBytes("2" + "\n");
							outToServer.writeBytes(booking_id + "\n");
							serverSentence = inFromServer.readLine();
							System.out.println(serverSentence);
							break;
						case "3":
							System.out.println("Enter booking_id:");
							booking_id = inFromUser.readLine();
							outToServer.writeBytes("3" + "\n");
							outToServer.writeBytes(booking_id + "\n");
							serverSentence = inFromServer.readLine();
							if (serverSentence.equals("Yes")) {
								c = (customer) is.readObject();
								c.booking_details();
							} else {
								System.out.println(serverSentence);
							}
							break;
						case "4":
							System.out.println("Enter destination city:");
							city = inFromUser.readLine();
							outToServer.writeBytes("4" + "\n");
							outToServer.writeBytes(city + "\n");
							serverSentence = inFromServer.readLine();
							if (serverSentence.equals("Error!")) {
								System.out.println("Some Error has occurred!");
							} else {
								bus = (ArrayList) is.readObject();
								c.display_bus(bus);
							}
							break;
						case "5":
							outToServer.writeBytes("5" + "\n");
							city_list = (ArrayList) is.readObject();
							c.display_cities(city_list);
							break;
					}
				}
			} else {
				while (true) {
					System.out.println("1.Add City\n2.Delete City\n3.Add Bus\n4.Cancel Bus\n5.Customer List\n6.Exit");
					choice = inFromUser.readLine();
					if (choice.equals("6")) {
						outToServer.writeBytes("6" + "\n");
						break;
					}
					switch(choice)
					{
						case "1":
							System.out.println("Enter City Name:");
							city  = inFromUser.readLine();
							outToServer.writeBytes("1"+"\n");
							outToServer.writeBytes(city+"\n");
							serverSentence = inFromServer.readLine();
							System.out.println(serverSentence);
							break;
						case "2":
							System.out.println("Enter City Name:");
							city = inFromUser.readLine();
							outToServer.writeBytes("2"+"\n");
							outToServer.writeBytes(city+"\n");
							serverSentence = inFromServer.readLine();
							System.out.println(serverSentence);
							break;
						case "3":
							System.out.println("Enter Destination City:");
							city = inFromUser.readLine();
							outToServer.writeBytes("3"+"\n");
							outToServer.writeBytes(city+"\n");
							serverSentence = inFromServer.readLine();
							System.out.println(serverSentence);
							break;
						case "4":
							System.out.println("Enter Destination City:");
							city = inFromUser.readLine();
							outToServer.writeBytes("4"+"\n");
							outToServer.writeBytes(city+"\n");
							serverSentence = inFromServer.readLine();
							System.out.println(serverSentence);
							break;
						case "5":
							outToServer.writeBytes("5"+"\n");
							list = (ArrayList<customer>) is.readObject();
							c.display_customers(list);
							break;
					}
				}
			}
			System.out.println("Do you want to continue?(Y or N)");
			String choice1 = inFromUser.readLine();
			if(choice1.equals("N"))
			{
				outToServer.writeBytes("end" + "\n");
				clientSocket.close();
				break;
			}
			else
			{
				outToServer.writeBytes("Y"+"\n");
			}
		}
	}
}