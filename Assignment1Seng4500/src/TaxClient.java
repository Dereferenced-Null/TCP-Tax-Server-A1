import java.net.*;
import java.io.*;
import java.util.*;

public class TaxClient {

    public static void main(String[] args){
        String hostname = "localhost";
        int port = 8000;
        if(args.length != 0){
            hostname = args[0];
            port = Integer.parseInt(args[1]);
        }
        try(
            Socket socket = new Socket(hostname, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            InputStreamReader clientInput = new InputStreamReader(System.in);
            BufferedReader clientIn = new BufferedReader(clientInput);
        ){
            String output = null;
            String input = null;
            String numberRegex = "[0-9]+";
            do{
                //I think maybe the point is more that the client puts whatever in and its just parsed, all the switch stuff may be unnecessary.
                //Ask and then it can be removed if needed. Although that would require the client to know exactly what messages to send and when.
                input = clientIn.readLine();
                switch (input){
                    case "TAX" : {
                        System.out.println("Tax");
                        out.println(input);
                        input = in.readLine();
                        if(!input.matches("TAX: OK")){
                            System.out.println(input);
                            break;
                        }
                        break;
                    }
                    case "STORE" : {
                        System.out.println("Store");
                        break;
                    }
                    case "QUERY" : {
                        System.out.println("Query");
                        break;
                    }
                    case "BYE" : {
                        System.out.println("Bye");
                        break;
                    }
                    case "END" : {
                        out.println(input);
                        //allow for server reply before exit
                        in.readLine();
                        break;
                    }
                    default: {
                        if(input.matches("[0-9]+")){
                            System.out.println("This is a number");
                            break;
                        }
                    }
                }

            }
            while(!input.matches("END"));
            socket.close();
            out.close();
            in.close();
            clientIn.close();
            clientInput.close();
            System.out.println("Complete");
        }
        catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
}
