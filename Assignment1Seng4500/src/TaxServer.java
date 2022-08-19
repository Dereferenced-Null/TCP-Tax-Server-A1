import java.io.*;
import java.net.*;
import java.util.*;

public class TaxServer {

    public static void main(String []args){
        int port = 8000;
        ArrayList<TaxObject> taxMatrix = new ArrayList<TaxObject>();
        //for creating new sessions
        boolean sessionActive = true;
        if(args.length != 0){
            port = Integer.parseInt(args[0]);
        }
        try(
            ServerSocket server = new ServerSocket(port);
        ){
            while(sessionActive){
                sessionActive = newSession(server, taxMatrix);
            }
            server.close();
        }
         catch (Exception e){
            System.out.println(e);
            e.printStackTrace();
        }

    }

    //creates a new session
    private static boolean newSession(ServerSocket server, ArrayList taxMatrix){
        boolean activeSession = false;
        try (
            Socket clientSocket = server.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ){
            activeSession = processSession(out, in, clientSocket, taxMatrix);
        }
        catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
        return activeSession;
    }

    private static boolean processSession(PrintWriter out, BufferedReader in, Socket clientSocket, ArrayList taxMatrix){
        try {
            String input = ASCIItoString(in.readLine());
            out.println(buildASCIIString("TAX: OK"));
            //server actions that don't include shutdown of the server
            while (!input.matches("END") && !input.matches("BYE")){
                input = ASCIItoString(in.readLine());
                switch (input) {
                    case "STORE" : {
                        //incomingTaxValues
                        String [] incomingTV = new String[4];
                        //Recieve 4 messages
                        for(int i = 0; i < 4; i ++){
                            incomingTV[i] = ASCIItoString(in.readLine());
                        }
                        if (incomingTV[1].matches("~")) {
                            //Highest possible integer value, if tax calculations are attempted on a number higher than this,
                            //the program will crash.
                            incomingTV[1] = Integer.toString(Integer.MAX_VALUE);
                        }
                        TaxObject newTaxObject = new TaxObject(
                                Integer.parseInt(incomingTV[0]),
                                Integer.parseInt(incomingTV[1]),
                                Integer.parseInt(incomingTV[2]),
                                Integer.parseInt(incomingTV[3]));
                        storeTaxValues(taxMatrix, newTaxObject);
                        out.println(buildASCIIString("STORE: OK"));
                        break;
                    }
                    case "QUERY" : {
                        String output = "";
                        for (int n = 0; n < taxMatrix.size(); n++) {
                            output = "";
                            TaxObject current = (TaxObject) taxMatrix.get(n);
                            if(current.getEndIncome() == Integer.MAX_VALUE){
                                //tostring these
                                output = output + current.getStartIncome() +" ~ "+ current.getBaseTax() +" "+ current.getTaxPercent();
                            }
                            else{
                                output = output + current.getStartIncome() +" "+ current.getEndIncome() +" "+ current.getBaseTax() +" "+ current.getTaxPercent();
                            }
                            out.println(buildASCIIString(output));
                        }
                        out.println(buildASCIIString("QUERY: OK"));
                        break;
                    }
                    default : {
                        if(input.matches("[0-9]+")){
                            String output = Integer.toString(findTaxPayable(taxMatrix, input));
                            if(output.matches("-1")){
                                out.println(buildASCIIString("I DONT KNOW " + input));
                            }
                            else{
                                out.println(buildASCIIString("TAX IS " + output));
                            }
                            break;
                        }
                        else{
                            //invalid input
                            break;
                        }
                    }
                }
            }
            if(input.matches("END")){
                out.println(buildASCIIString("END: OK"));
                clientSocket.close();
                out.close();
                in.close();
                return false;
            }
            else{
                out.println(buildASCIIString("BYE: OK"));
                clientSocket.close();
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

    //Stores the tax values based on their numerical order
    //Note, if an added tax bracket is encompassed by another tax bracket, behaviour will not be as expected
    //as this case is not requires to be compensated for per spec.
    private static void storeTaxValues(ArrayList taxMatrix, TaxObject bracket) {
        boolean stored = false;
        if (taxMatrix.size() == 10) {
            return;
        }
        if (taxMatrix.size() < 1) {
            sortMatrix(taxMatrix, bracket);
        } else {
            for (int i = 0; i < taxMatrix.size() ; i++) {
                TaxObject current = (TaxObject) taxMatrix.get(i);
                TaxObject next = null;
                TaxObject prev = null;
                if (i > 1) {
                    prev = (TaxObject) taxMatrix.get(i - 1);
                }
                if (i < taxMatrix.size() - 1) {
                    next = (TaxObject) taxMatrix.get(i + 1);
                }
                    //subsumes
                    if (bracket.getStartIncome() <= current.getStartIncome() &&
                            bracket.getEndIncome() >= current.getEndIncome()) {
                        if (prev != null && bracket.getStartIncome() < prev.getEndIncome()) {
                            prev.setEndIncome(bracket.getStartIncome() - 1);
                            taxMatrix.set(i-1, prev);
                        }
                        if (next != null && bracket.getEndIncome() > next.getStartIncome()){
                            next.setStartIncome(bracket.getEndIncome() + 1);
                            taxMatrix.set(i + 1, next);
                        }
                        taxMatrix.set(i, bracket);
                        stored = true;
                    }
                    //between 2 brackets
                    else {
                        if (bracket.getEndIncome() >= current.getStartIncome() &&
                                bracket.getEndIncome() <= current.getEndIncome()) {
                            current.setStartIncome(bracket.getEndIncome() + 1);
                            taxMatrix.set(i, current);
                        }
                        if(bracket.getStartIncome() >= current.getStartIncome() &&
                                bracket.getStartIncome() <= current.getEndIncome()){
                            current.setEndIncome(bracket.getStartIncome() - 1);
                            taxMatrix.set(i, current);
                        }
                        //remove potential duplicates that have been subsumed by new brackets
                        //this would probably have to be the worst thing I've ever written
                        for(int n = 0; n < taxMatrix.size() - 1; n++){
                            if((((TaxObject) taxMatrix.get(n)).getStartIncome()) ==
                                    (((TaxObject) taxMatrix.get(n + 1)).getStartIncome()) &&
                                    (((TaxObject) taxMatrix.get(n)).getEndIncome()) ==
                                    (((TaxObject) taxMatrix.get(n + 1)).getEndIncome())){
                                taxMatrix.remove(n);
                            }
                        }
                    }
            }
            if(stored == false){
                sortMatrix(taxMatrix, bracket);
            }
        }
    }

    public static void sortMatrix(ArrayList taxMatrix, TaxObject bracket) {
        taxMatrix.add(bracket);
        Collections.sort(taxMatrix, new Comparator<TaxObject>() {
            @Override
            public int compare(TaxObject a, TaxObject b) {
                if ((a.getStartIncome()) > (b.getStartIncome())) {
                    return 1;
                } else if ((a.getStartIncome()) < (b.getStartIncome())) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

    }

    public static int findTaxPayable(ArrayList taxMatrix, String income){
        int num = Integer.parseInt(income);
        boolean found = false;
        float output = 0;
        try {
            for (int n = 0; n < taxMatrix.size(); n++) {
                TaxObject current = (TaxObject) taxMatrix.get(n);
                if (num >= (current.getStartIncome()) &&
                        num <= (current.getEndIncome())) {
                    found = true;
                    output = (current.getBaseTax() + num * (((float)current.getTaxPercent())/100));
                }
            }
            if(!found){
                return -1;
            }
        }
        catch(ArithmeticException e){
            return 0;
        }
        return Math.round(output);
    }

    //Builds an outgoing ASCII string to be sent
    public static String buildASCIIString(String str){
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i = 0; i < str.length(); i++) {
            stringBuilder.append((int) str.charAt(i));
        }
        return stringBuilder.toString();
    }


    //Converts the incoming ASCII string into a java string
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

    private static class TaxObject{

        public TaxObject(int startIncome, int endIncome, int baseTax, int taxPercent) {
            this.startIncome = startIncome;
            this.endIncome = endIncome;
            this.baseTax = baseTax;
            this.taxPercent = taxPercent;
        }

        private int startIncome;
        private int endIncome;
        private int baseTax;
        private int taxPercent;

        public int getStartIncome() {
            return startIncome;
        }

        public void setStartIncome(int startIncome) {
            this.startIncome = startIncome;
        }

        public int getEndIncome() {
            return endIncome;
        }

        public void setEndIncome(int endIncome) {
            this.endIncome = endIncome;
        }

        public int getBaseTax() {
            return baseTax;
        }

        public void setBaseTax(int baseTax) {
            this.baseTax = baseTax;
        }

        public int getTaxPercent() {
            return taxPercent;
        }

        public void setTaxPercent(int taxPercent) {
            this.taxPercent = taxPercent;
        }





    }

}

