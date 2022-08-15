import java.io.*;
import java.net.*;

public class TaxServer {

    private double [][] taxMatrix = new double [10][1];

    public static void main(String []args){
        int port = 8000;
        //for creating new sessions
        boolean sessionActive = true;
        if(args.length != 0){
            port = Integer.parseInt(args[0]);
        }
        try(
            ServerSocket server = new ServerSocket(port);
        ){
            while(sessionActive){
                sessionActive = newSession(server);
            }
            server.close();


            System.out.println("Complete");
        }
         catch (Exception e){
            System.out.println(e);
            e.printStackTrace();
        }

    }

    private static boolean newSession(ServerSocket server){
        boolean activeSession = false;
        try (
            Socket clientSocket = server.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ){
             activeSession = processSession(out, in, clientSocket);
        }
        catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
        return activeSession;
    }

    private static boolean processSession(PrintWriter out, BufferedReader in, Socket clientSocket){
        try {
            String input = in.readLine();
            String output = null;
            while (!input.matches("TAX")) {
                out.println("Please create a new TAX session first");
                input = in.readLine();
            }
            out.println("TAX: OK");
            input = in.readLine();
            //BYE and END will be esacaping variables
            while (!input.matches("END") && !input.matches("BYE")){
                switch (input) {
                    case "STORE" : {

                    }
                    case "QUERY" : {

                    }
                    default : {
                        if(input.matches("[0-9]+")){
                            System.out.println("This is a number");
                        }
                        else if(input.matches("TAX")){
                            out.println("Session already active");
                        }
                        else{
                            break;
                        }
                    }
                }
                input = in.readLine();
                System.out.println(input);
            }
            if(input.matches("END")){
                out.println("END: OK");
                clientSocket.close();
                out.close();
                in.close();
                return false;
            }
            else{
                return true;
            }

        }
        catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
        //redundant measure
        return false;
    }


}
