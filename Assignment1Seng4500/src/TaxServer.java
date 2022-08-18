import java.io.*;
import java.net.*;

public class TaxServer {

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
            int[][] taxMatrix = new int [10][4];
            activeSession = processSession(out, in, clientSocket, taxMatrix);
        }
        catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
        return activeSession;
    }

    private static boolean processSession(PrintWriter out, BufferedReader in, Socket clientSocket, int[][] taxMatrix){
        try {
            String input = ASCIItoString(in.readLine());
            out.println(buildASCIIString("TAX: OK"));
            input = ASCIItoString(in.readLine());
            //server actions that don't include shutdown of the server
            while (!input.matches("END") && !input.matches("BYE")){
                if(input == "STORE"){
                    System.out.println(input);
                }
                switch (input) {
                    case "STORE" : {
                        int [] incomingTaxValues = new int[4];
                        //Recieve 4 messages
                        System.out.println("STORE!");
                        input = ASCIItoString(in.readLine());
                        System.out.println(input);
                        for(int i = 0; i < 4; i ++){

                        }
//                        incomingTaxValues[0] = Integer.parseInt(ASCIItoString(in.readLine()));
                        if(incomingTaxValues[1] < incomingTaxValues[0]){
                            out.print(buildASCIIString("STORE: FAILED: check values"));
                        }
                        storeTaxValues(taxMatrix, incomingTaxValues);

                        //store the values based on size, can just check the first and second values in the matrix
                        //array must be sorted, but creating a sorting on insertion function should be enough
                        //This function will have
                        out.println(buildASCIIString("STORE: OK"));

                        break;
                        //these values can be stored in the matrix but will need to be stored in order
                        //for ease of access
                    }
                    case "QUERY" : {
                        //handle query protocol
                    }
                    default : {
                        if(input.matches("[0-9]+")){
                            System.out.println("This is a number");
                        }
                        else{
                            break;
                        }
                    }
                }
                input = ASCIItoString(in.readLine());
                in.readLine();
            }
            if(input.matches("END")){
                out.println(buildASCIIString("END: OK"));
                clientSocket.close();
                out.close();
                in.close();
                return false;
            }
            else{
                //handle bye protocol
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

    //Builds an outgoing ASCII string to be sent
    public static String buildASCIIString(String str){
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i = 0; i < str.length(); i++) {
            stringBuilder.append((int) str.charAt(i));
        }
        return stringBuilder.toString();
    }


    //Converts the incoming ASCII string into a java string, note ignores endline values, may need to be re-added
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

    private static void storeTaxValues(int[][] taxMatrix, int [] taxValues){
            int i = 0;
            for(int taxValue: taxValues){
                taxMatrix [0][i] = taxValue;
                i++;
            }
        System.out.println(taxMatrix[0][1]);
    }


}
