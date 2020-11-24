import javax.swing.plaf.synth.SynthTextAreaUI;
import java.lang.reflect.Array;
import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException
    {
        String serveraddress = "127.0.0.1";
        int port =8081;
        String user_type = "N";
        Scanner sc=new Scanner(System.in);
        String choice;
        Socket client = new Socket(serveraddress,port);
        System.out.println("Connected to: "+client.getRemoteSocketAddress());
        DataOutputStream outToServer = new DataOutputStream(client.getOutputStream());
        DataInputStream inFromServer = new DataInputStream(client.getInputStream());
        ObjectOutputStream os=new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream is=new ObjectInputStream(client.getInputStream());
        do{
            boolean Authentication=false;
            do {
                System.out.println(inFromServer.readUTF());
                choice = sc.next();
                outToServer.writeUTF(choice);
                switch (choice) {
                    case "1": {
                        System.out.println(inFromServer.readUTF());
                        String username = sc.next();
                        outToServer.writeUTF(username);
                        System.out.println(inFromServer.readUTF());
                        String password = sc.next();
                        outToServer.writeUTF(password);
                        System.out.println(inFromServer.readUTF());
                        Authentication = inFromServer.readBoolean();
                        user_type = inFromServer.readUTF();
                        break;
                    }
                    case "2": {
                        System.out.println(inFromServer.readUTF());
                        String name = sc.next();
                        outToServer.writeUTF(name);
                        System.out.println(inFromServer.readUTF());
                        String username = sc.next();
                        outToServer.writeUTF(username);
                        System.out.println(inFromServer.readUTF());
                        String password = sc.next();
                        outToServer.writeUTF(password);
                        System.out.println(inFromServer.readUTF());
                        int contact = sc.nextInt();
                        outToServer.writeInt(contact);
                        System.out.println(inFromServer.readUTF());
                        String type = sc.next();
                        outToServer.writeUTF(type);
                        System.out.println(inFromServer.readUTF());
                        break;
                    }
                    default:
                        System.out.println("Incorrect Choice!!");
                        break;
                }
            }while(!Authentication);
            if(user_type.equals("N"))
            {
                do {
                    System.out.println(inFromServer.readUTF());
                    System.out.println(inFromServer.readUTF());
                    choice = sc.next();
                    outToServer.writeUTF(choice);
                    switch(choice)
                    {
                        case "1":
                        {
                            System.out.println(inFromServer.readUTF());
                            String dest_city = sc.next();
                            outToServer.writeUTF(dest_city);
                            System.out.println(inFromServer.readUTF());
                            int no_of_pass = sc.nextInt();
                            outToServer.writeByte(no_of_pass);
                            System.out.println(inFromServer.readUTF());
                            HashMap<String,Integer> passenger_list = new HashMap<>();
                            for(int i=0;i<no_of_pass;i++)
                            {
                                passenger_list.put(sc.next(),sc.nextInt());
                            }
                            os.writeObject(passenger_list);
                            System.out.println(inFromServer.readUTF());
                            break;
                        }
                        case "2":{
                            System.out.println(inFromServer.readUTF());
                            int booking_id = sc.nextInt();
                            outToServer.writeInt(booking_id);
                            System.out.println(inFromServer.readUTF());
                            break;
                        }
                        case "3":{
                            System.out.println(inFromServer.readUTF());
                            int booking_id = sc.nextInt();
                            outToServer.writeInt(booking_id);
                            System.out.println(inFromServer.readUTF());
                            int bus_id = sc.nextInt();
                            outToServer.writeInt(bus_id);
                            System.out.println(inFromServer.readUTF());
                            break;
                        }
                        case "4":{
                            System.out.println(inFromServer.readUTF());
                            String dest_city = sc.next();
                            outToServer.writeUTF(dest_city);
                            System.out.println(inFromServer.readUTF());
                            break;
                        }
                        case "5":{
                            System.out.println(inFromServer.readUTF());
                            break;
                        }
                    }
                }while(!choice.equals("6"));
            }
            else
            {
                do
                {
                    System.out.println(inFromServer.readUTF());
                    System.out.println(inFromServer.readUTF());
                    choice = sc.next();
                    outToServer.writeUTF(choice);
                    switch(choice)
                    {
                        case "1":{
                            System.out.println(inFromServer.readUTF());
                            String dest_city = sc.next();
                            outToServer.writeUTF(dest_city);
                            System.out.println(inFromServer.readUTF());
                            break;
                        }
                        case "2":{
                            System.out.println(inFromServer.readUTF());
                            String dest_city = sc.next();
                            outToServer.writeUTF(dest_city);
                            System.out.println(inFromServer.readUTF());
                            break;
                        }
                        case "3":{
                            System.out.println(inFromServer.readUTF());
                            int bus_id = sc.nextInt();
                            outToServer.writeInt(bus_id);
                            System.out.println(inFromServer.readUTF());
                            break;
                        }
                        case "4":{
                            System.out.println(inFromServer.readUTF());
                            String dest_city = sc.next();
                            outToServer.writeUTF(dest_city);
                            System.out.println(inFromServer.readUTF());
                            break;
                        }
                        case "5":System.out.println(inFromServer.readUTF());
                        break;
                    }
                }while(!choice.equals("6"));
            }
            System.out.println(inFromServer.readUTF());
            choice = sc.next();
            outToServer.writeUTF(choice);
        }while(!choice.equals("1"));
    }
}
