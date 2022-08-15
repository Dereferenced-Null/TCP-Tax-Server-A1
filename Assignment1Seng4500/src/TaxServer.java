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
            String input = ASCIItoString(in.readLine());
            String output = null;
            while (!input.matches("TAX")) {
                out.println(buildASCIIString("Please create a new TAX session first"));
                input = ASCIItoString(in.readLine());
            }
            out.println(buildASCIIString("TAX: OK"));
            input = ASCIItoString(in.readLine());
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
                            out.println(buildASCIIString("Session already active"));
                        }
                        else{
                            break;
                        }
                    }
                }
                input = ASCIItoString(in.readLine());
                System.out.println(input);
            }
            if(input.matches("END")){
                out.println(buildASCIIString("END: OK"));
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

    public static String buildASCIIString(String str){
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i = 0; i < str.length(); i++) {
            stringBuilder.append((int) str.charAt(i));
        }
        return stringBuilder.toString();
    }

    private static String ASCIItoString(String ascii){
        int num = 0;
        String output = "";
        for (int i = 0; i < ascii.length(); i++) {
            num = num * 10 + (ascii.charAt(i) - '0');
            if (num >= 32 && num <= 122) {
                output = output + (char)num;
                num = 0;
            }
        }
        return output;
    }


}
