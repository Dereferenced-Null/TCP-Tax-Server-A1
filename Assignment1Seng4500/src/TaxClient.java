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
            boolean exitFlag = false;
            String output = null;
            String input = null;
            out.println(buildASCIIString("TAX"));
            input = ASCIItoString(in.readLine());
            System.out.println(input);
            //main loop
            //Note: System output for store and query are disabled as they are not explicitly required in the spec
            do{
                input = clientIn.readLine();
                switch (input){
                    case "STORE" : {
                        out.println(buildASCIIString("STORE"));
                        //Four messages out
                        for (int i = 0; i < 4; i++){
                            input = buildASCIIString(clientIn.readLine());
                            out.println(input);
                        }
                        input = ASCIItoString(in.readLine());
//                        System.out.println(input);
                        break;
                    }
                    case "QUERY" : {
                        out.println(buildASCIIString("QUERY"));
                        do{
                            input = ASCIItoString(in.readLine());
//                            System.out.println(input);
                        }
                        while(!input.matches("QUERY: OK"));
                        break;
                    }
                    case "BYE" : {
                        out.println(buildASCIIString("BYE"));
                        input = ASCIItoString(in.readLine());
                        System.out.println(input);
                        exitFlag = true;
                        break;
                    }
                    case "END" : {
                        out.println(buildASCIIString(input));
                        //allow for server reply before exit,
                        //input itself is technically irrelevant
                        input = ASCIItoString(in.readLine());
                        System.out.println(input);
                        exitFlag = true;
                        break;
                    }
                    default: {
                        if(input.matches("[0-9]+")){
                            out.println(buildASCIIString(input));
                            input = ASCIItoString(in.readLine());
                            System.out.println(input);
                            break;
                        }
                        //this default also means that incorrect user input will not be sent.
                    }
                }

            }
            while(!exitFlag);
            socket.close();
            out.close();
            in.close();
            clientIn.close();
            clientInput.close();
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
            if (num >= 32 && num <= 126) {
                output = output + (char)num;
                num = 0;
            }
        }
        return output;
    }
}
