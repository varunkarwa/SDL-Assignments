import java.net.*;
import java.io.*;
import java.util.*;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Booking
{
    public synchronized String book(String dest_city, int no_of_pass, HashMap<String, Integer> passenger_list, Connection conn, int user_id)  {
        try {
            PreparedStatement ps = null;
            ps = conn.prepareStatement("select * from cities where city_name = ?");
            ps.setString(1,dest_city);
            ResultSet rs = ps.executeQuery();
            if(!rs.next())
            {
                return "City not available";
            }
            String city_name=rs.getString(2);
            int city_id = rs.getInt(1);
            ps = conn.prepareStatement("select * from buses where city_id = ?");
            ps.setInt(1,city_id);
            rs = ps.executeQuery();
            if(!rs.next())
            {
                return "Bus not available";
            }
            int bus_id = rs.getInt(1);
            int seat_booked = rs.getInt(2);
            if(seat_booked==40) {
                return "Seats Not Available";
            }
            Random rand = new Random();
            int booking_id = rand.nextInt(999999);
            ps = conn.prepareStatement("select seat_no from passengers where bus_id = ?");
            ps.setInt(1,bus_id);
            rs = ps.executeQuery();
            while(rs.next())
            {
                if(passenger_list.containsValue(rs.getInt(1)))
                {
                    return rs.getInt(1)+" seat is already booked!!\n Please see seat available option!!";
                }
            }
            ps = conn.prepareStatement("insert into booking values(?,?,?,?,?)");
            ps.setInt(1, user_id);
            ps.setInt(2, booking_id);
            ps.setInt(3, city_id);
            ps.setInt(4, bus_id);
            ps.setInt(5, no_of_pass);
            ps.executeUpdate();
            ps = conn.prepareStatement("insert into passengers values(?,?,?,?,?)");
            ps.setInt(2, booking_id);
            ps.setInt(3, bus_id);
            Set<String> key = passenger_list.keySet();
            Iterator<String> i = key.iterator();
            int j=1;
            String book_statement="Booking Id:" + booking_id + "\nBus Id:" +bus_id + "\nPassenger List:\n";
            while(i.hasNext())
            {
                ps.setInt(1,j++);
                String name = String.valueOf(i.next());
                int seat = passenger_list.get(name);
                ps.setString(4,name);
                ps.setInt(5, seat);
                ps.executeUpdate();
                book_statement += i + ". " + name + " " + seat + "\n";
            }
            ps = conn.prepareStatement("update buses set seat_booked = seat_booked + ? where bus_id = ?");
            ps.setInt(1,no_of_pass);
            ps.setInt(2,bus_id);
            ps.executeUpdate();
            return book_statement;
        }
        catch(Exception e)
        {
            return String.valueOf(e);
        }
    }

    public String check(int booking_id,Connection conn)
    {
        try{

            PreparedStatement ps = conn.prepareStatement(" select city_name,bus_id,passenger_name,seat_no,username from booking natural join passengers natural join cities natural join users where booking_id = ?");
            ps.setInt(1,booking_id);
            ResultSet rs = ps.executeQuery();
            if(!rs.next())
            {
                return "No Booking!!";
            }
            String check_statement="";
            String city = rs.getString(1) ;
            int bus_id = rs.getInt(2 );
            String name = rs.getString(3);
            int seat = rs.getInt(4);
            String username = rs.getString(5);
            check_statement += "Destination City: " + city + "\nBus id: " + bus_id + "\nPassenger List:\nName: "+name+" Seat Number: " + seat;
            while(rs.next())
            {
                name = rs.getString(3);
                seat = rs.getInt(4);
                check_statement += "\nName: "+name+" Seat Number: "+seat;
            }
            check_statement += "\nBooked by: " + username;
            return check_statement;
        }
        catch(Exception e)
        {
            return String.valueOf(e);
        }
    }
    public String cancel(int booking_id,int bus_id,Connection conn)
    {
        try
        {
            PreparedStatement ps = conn.prepareStatement("select * from booking where booking_id = ? and bus_id = ?");
            ps.setInt(1,booking_id);
            ps.setInt(2,bus_id);
            ResultSet rs = ps.executeQuery();
            if(!rs.next())
                return "No bookings!!";
            int no_of_pass = rs.getInt(5);
            ps = conn.prepareStatement("delete from booking where booking_id = ? and bus_id = ?");
            ps.setInt(1,booking_id);
            ps.setInt(2,bus_id);
            ps.executeUpdate();
            ps = conn.prepareStatement("update buses set seat_booked = seat_booked - ? where bus_id = ?");
            ps.setInt(1,no_of_pass);
            ps.setInt(2,bus_id);
            ps.executeUpdate();
            return "Ticket Cancelled!!";
        }
        catch(Exception e)
        {
            return String.valueOf(e);
        }
    }
    public String availability(String dest_city,Connection conn)
    {
        try
        {
            PreparedStatement ps = conn.prepareStatement("select city_id from cities where city_name = ?");
            ps.setString(1,dest_city);
            ResultSet rs = ps.executeQuery();
            if(!rs.next())
            {
                return "City Not Available!!";
            }
            int city_id = rs.getInt(1);
            ps = conn.prepareStatement("select bus_id,seat_booked from buses where city_id = ?");
            ps.setInt(1,city_id);
            rs = ps.executeQuery();
            String available_statement ="";
            while(rs.next()) {
                int bus_id = rs.getInt(1);
                available_statement += "Bus Id:" + bus_id + "\n";
                int seat_booked = rs.getInt(2);
                if (seat_booked == 0) {
                    for (int i = 1; i <= 40;) {
                        available_statement += (i++) + ".A  " + (i++) + ".A\t\t" + (i++) + ".A  " + (i++) + ".A\n";
                    }
                } else {
                    ps = conn.prepareStatement("select seat_no from passengers where bus_id = ?");
                    ps.setInt(1,bus_id);
                    ResultSet rs1 = ps.executeQuery();
                    ArrayList<Integer> seats = new ArrayList<>();
                    while(rs1.next())
                    {
                        seats.add(rs1.getInt(1));
                    }
                    for(int i=1;i<=40;i++)
                    {
                        if(seats.contains(i))
                        {
                            if(i%4==0)
                                available_statement += i + ".B\n";
                            else if(i%2==0)
                                available_statement += i+ ".B\t\t";
                            else
                                available_statement += i + ".B  ";
                        }
                        else
                        {
                            if(i%4==0)
                                available_statement += i + ".A\n";
                            else if(i%2==0)
                                available_statement += i+ ".A\t\t";
                            else
                                available_statement += i + ".A  ";

                        }
                    }
                }
            }
            return available_statement;
        }
        catch(Exception e)
        {
            return String.valueOf(e);
        }
    }
}

public class Server {
    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(8081);
        ExecutorService executor = Executors.newFixedThreadPool(5);
        System.out.println("Starting Server...");
        while (true) {
            Socket s = null;
            try {
                s = ss.accept();
                System.out.println("New Client Connected:" + s);
                DataOutputStream outToClient = new DataOutputStream(s.getOutputStream());
                DataInputStream inFromClient = new DataInputStream(s.getInputStream());
                ObjectOutputStream os=new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream is=new ObjectInputStream(s.getInputStream());
                System.out.println("Assigning Thread to Client");
                Thread t = new ClientHandler(s, inFromClient, outToClient,os,is);
                executor.execute(t);
            } catch (Exception e) {
                assert s != null;
                s.close();
                e.printStackTrace();
            }
        }
    }
}
class ClientHandler extends Thread {
    final DataInputStream inFromClient;
    final DataOutputStream outToClient;
    final Socket s;
    final ObjectInputStream is;
    final ObjectOutputStream os;

    public ClientHandler(Socket s, DataInputStream inFromClient, DataOutputStream outToClient,ObjectOutputStream os,ObjectInputStream is) throws Exception {
        this.s = s;
        this.inFromClient = inFromClient;
        this.outToClient = outToClient;
        this.os = os;
        this.is = is;
    }

    public void run() {
        try {
            Scanner sc = new Scanner(System.in);
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sdl_assignment_3?autoReconnect=true&useSSL=false", "root", "varunkarwa");
            Statement stmt = conn.createStatement();
            String choice,user_type="N";
            PreparedStatement ps=null;
            int user_id=0;
            do{
                boolean Authentication=false;
                while(!Authentication){
                    outToClient.writeUTF("1.Login\n2.Sign Up:");
                    choice = inFromClient.readUTF();
                    switch(choice)
                    {
                        case "1":{
                            outToClient.writeUTF("Enter Username:");
                            String username = inFromClient.readUTF();
                            outToClient.writeUTF("Enter Password:");
                            String password = inFromClient.readUTF();
                            ps = conn.prepareStatement("select user_id,name,contact_no,user_type from users where username = ? and password = ?");
                            ps.setString(1, username);
                            ps.setString(2, password);
                            try{ResultSet rs = ps.executeQuery();
                                if (rs.next()) {
                                    outToClient.writeUTF("User_id:" + rs.getInt(1) + "\nName:" + rs.getString(2) + "\nContact: " + rs.getString(3));
                                    user_type=rs.getString(4);
                                    user_id = rs.getInt(1);
                                    Authentication = true;
                                    System.out.println("User logged in!\n" + "User_id:" + rs.getInt(1) + "\nName:" + rs.getString(2) + "\nContact: " + rs.getString(3));

                                } else {
                                    outToClient.writeUTF("Invalid Credentials!!");
                                }}
                            catch (Exception e)
                            {
                                System.out.println(e);
                                outToClient.writeUTF("Error!");}
                            outToClient.writeBoolean(Authentication);
                            outToClient.writeUTF(user_type);
                            break;
                        }
                        case "2": {
                            outToClient.writeUTF("Enter Name:");
                            String name = inFromClient.readUTF();
                            outToClient.writeUTF("Enter username:");
                            String username = inFromClient.readUTF();
                            outToClient.writeUTF("Enter password:");
                            String password = inFromClient.readUTF();
                            outToClient.writeUTF("Enter Contact_no:");
                            int contact = inFromClient.readInt();
                            outToClient.writeUTF("User Type(Admin A/Normal N):");
                            String type = inFromClient.readUTF();
                            ps = conn.prepareStatement("insert into users(username,password,name,contact_no,user_type) values(?,?,?,?,?)");
                            ps.setString(1,username);
                            ps.setString(2,password);
                            ps.setString(3,name);
                            ps.setInt(4,contact);
                            ps.setString(5,type);
                            try {
                                int rs = ps.executeUpdate();
                                outToClient.writeUTF("Account Created!!\nLogin to continue!!");
                                System.out.println("Account Created!!\n Users Details:\nUsername:" + username + "\nPassword:" + password + "\nName:" + name + "\nContact No:" + contact);
                            }catch(Exception e)
                            {
                                outToClient.writeUTF(String.valueOf(e));
                            }
                            break;
                        }
                        default:
                            System.out.println("Incorrect Choice!!");
                            break;
                    }
                }
                Booking b = new Booking();
                if(user_type.equals("N"))
                {
                    do {
                        outToClient.writeUTF("------MENU------");
                        outToClient.writeUTF("1.Book Tickets\n2.Check Reservation\n3.Cancel Tickets\n4.Seat Availability\n5.Service to Cities\n6.Logout");
                        choice = inFromClient.readUTF();
                        switch (choice) {
                            case "1": {
                                outToClient.writeUTF("Enter destination city:");
                                String dest_city = inFromClient.readUTF();
                                outToClient.writeUTF("Enter no.of passengers");
                                int no_of_pass = inFromClient.readByte();
                                outToClient.writeUTF("Enter passenger's name and seat_no");
                                HashMap<String, Integer> passenger_list = (HashMap<String, Integer>) is.readObject();
                                String book_statement = b.book(dest_city, no_of_pass, passenger_list, conn, user_id);
                                outToClient.writeUTF(book_statement);
                                break;
                            }
                            case "2":{
                                outToClient.writeUTF("Enter Booking ID:");
                                int booking_id = inFromClient.readInt();
                                String check_statement = b.check(booking_id,conn);
                                outToClient.writeUTF(check_statement);
                                break;
                            }
                            case "3":{
                                outToClient.writeUTF("Enter booking id:");
                                int booking_id = inFromClient.readInt();
                                outToClient.writeUTF("Enter bus_id:");
                                int bus_id = inFromClient.readInt();
                                String cancel_statement = b.cancel(booking_id,bus_id,conn);
                                outToClient.writeUTF(cancel_statement);
                                break;
                            }
                            case "4":
                            {
                                outToClient.writeUTF("Enter destination city:");
                                String dest_city = inFromClient.readUTF();
                                String available_statement = b.availability(dest_city,conn);
                                outToClient.writeUTF(available_statement);
                                break;
                            }
                            case "5":
                            {
                                ResultSet rs = stmt.executeQuery("select city_name from cities");
                                String city_statement = "Cities:\n";
                                while(rs.next())
                                {
                                    city_statement += rs.getString(1) + "\n";
                                }
                                outToClient.writeUTF(city_statement);
                                break;
                            }
                        }
                    }while(!choice.equals("6"));
                }
                else
                {
                    do
                    {
                        outToClient.writeUTF("------MENU-----");
                        outToClient.writeUTF("1.Add City\n2.Add Bus\n3.Delete Bus\n4.Delete City\n5.Passenger List\n");
                        choice = inFromClient.readUTF();
                        switch(choice)
                        {
                            case "1":
                            {
                                outToClient.writeUTF("Enter City Name");
                                String dest_city = inFromClient.readUTF();
                                ps = conn.prepareStatement("insert into cities(city_name) values(?)");
                                ps.setString(1,dest_city);
                                try
                                {
                                    ps.executeUpdate();
                                    outToClient.writeUTF("Added Successfully");
                                }
                                catch(Exception e)
                                {
                                    outToClient.writeUTF(String.valueOf(e));
                                }
                                break;
                            }
                            case "2":{
                                outToClient.writeUTF("Enter City Name");
                                String dest_city = inFromClient.readUTF();
                                ps = conn.prepareStatement("select city_id from cities where city_name = ?");
                                ps.setString(1,dest_city);
                                ResultSet rs = ps.executeQuery();
                                if(!rs.next())
                                {
                                    outToClient.writeUTF("Please first add this city!!");
                                    break;
                                }
                                int city_id = rs.getInt(1);
                                ps = conn.prepareStatement("insert into buses(seat_booked,city_id) values(0,?)");
                                ps.setInt(1,city_id);
                                ps.executeUpdate();
                                outToClient.writeUTF("Added Successfully!");
                                break;
                            }
                            case "3":{
                                outToClient.writeUTF("Enter bus id:");
                                int bus_id = inFromClient.readInt();
                                ps = conn.prepareStatement("delete from buses where bus_id = ?");
                                ps.setInt(1,bus_id);
                                if(ps.executeUpdate() == 0)
                                    outToClient.writeUTF("Bus already cancelled!!");
                                else
                                    outToClient.writeUTF("Bus Cancelled!!");
                                break;
                            }
                            case "4":{
                                outToClient.writeUTF("Enter city_name:");
                                String dest_city = inFromClient.readUTF();
                                ps = conn.prepareStatement("delete from cities where city_name = ?");
                                ps.setString(1,dest_city);
                                if(ps.executeUpdate() == 0)
                                    outToClient.writeUTF("First add the city please!!");
                                else
                                    outToClient.writeUTF("City deleted Successfully!!");
                                break;
                            }
                            case "5":
                            {
                                String passenger_statement = "Today's Bookings:\n";
                                ResultSet rs =stmt.executeQuery("select * from passengers order by bus_id,seat_no");
                                if(rs.next()) {
                                    passenger_statement += "Passenger Id:" + rs.getInt(1) + "\tBooking Id:" + rs.getInt(2) + "\tBus Id:" + rs.getInt(3) + "\tName:" + rs.getString(4) + "\tSeat:" + rs.getInt(5) + "\n";
                                    while (rs.next()) {
                                        passenger_statement += "Passenger Id:" + rs.getInt(1) + "\tBooking Id:" + rs.getInt(2) + "\tBus Id:" + rs.getInt(3) + "\tName:" + rs.getString(4) + "\tSeat:" + rs.getInt(5) + "\n";
                                    }
                                    outToClient.writeUTF(passenger_statement);
                                }
                                else
                                    outToClient.writeUTF("No Bookings!!");
                                break;
                            }
                        }
                    }while(!choice.equals("6"));
                }

               outToClient.writeUTF("1.Exit\n2.Continue");
               choice = inFromClient.readUTF();
            }while(!choice.equals("1"));
        }
        catch (Exception e) {
            System.out.println(e);
        }
        try{
            this.inFromClient.close();
            this.outToClient.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}