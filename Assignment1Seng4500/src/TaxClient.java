import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
            Scanner console = new Scanner(System.in);
        ){
            String output = null;
            String serverInput = null;
            String userInput = null;
            out.println(buildASCIIString("TAX"));
            ASCIItoString(in.readLine());
            do{
                userInput = console.nextLine();
                console.nextLine();
                switch (userInput){
                    case "STORE" : {
                        out.println(buildASCIIString("STORE"));
                        output = "";
                        //Four messages out
                        for (int i = 0; i < 4;){
                            userInput = console.nextLine();
                            System.out.println("THIS IS THE INPUT:" + userInput);
                            output = output + buildASCIIString(userInput);
                            i++;
                        }
                        System.out.println(output);
                        out.println(output);
                        userInput = ASCIItoString(in.readLine());
                        System.out.println(userInput);
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
                        out.println(buildASCIIString("END"));
                        //allow for server reply before exit,
                        //input itself is technically irrelevant
                        in.readLine();
                        break;
                    }
                    default: {
                        if(userInput.matches("[0-9]+")){
                            System.out.println("This is a number");
                            break;
                        }
                    }
                }

            }
            while(!userInput.matches("END"));
            socket.close();
            out.close();
            in.close();
            console.close();
            System.out.println("Complete");
        }
        catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
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
}
