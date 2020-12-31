/*
Όνομα: Γραμμένος Θεόδωρος
ΑΕΜ: 3294
E-mail: grammenot@csd.auth.gr
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Cores {

    private static class Client{
        private int coresRequested;
        private double corePrice;

        Client(int coresRequested, double corePrice){
            this.coresRequested = coresRequested;
            this.corePrice = corePrice;
        }

        public int getCoresRequested(){
            return coresRequested;
        }

        public double getCorePrice() {
            return corePrice;
        }
    }

    private ArrayList<Client> clients;
    private int availableCores;
    private final int[] availableVMCoreValues = {1,2,7,11};
    // Keeps the maximum amount of cores a single client has requested
    private int maxClientCores;

    Cores(String filename){
        clients = new ArrayList<>();
        loadFile(filename);
    }

    private void loadFile(String fileName){
        BufferedReader inputReader = null;
        try {
            inputReader = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            System.err.println("File " + fileName + " not found!");
            System.exit(1);
        }
        maxClientCores = -1;
        String line;
        try {
            String[] numArray;
            // Number of available cores
            line = inputReader.readLine();
            availableCores = Integer.parseInt(line);
            // Read client offers
            while ((line = inputReader.readLine()) != null) {
                numArray = line.split(" ");
                int cores = Integer.parseInt(numArray[0]);
                // Keep the maximum amount of cores a client has requested
                if(cores>maxClientCores){
                    maxClientCores = cores;
                }
                double price = Double.parseDouble(numArray[1]);
                clients.add(new Client(cores, price));
            }
        }catch (Throwable e){
            e.printStackTrace();
        }
    }

    /**
     * Calculates the minimum amount of Virtual Machines needed for each client
     * @return A string containing the number of virtual machines needed for each client
     */
    public String minVMs(){
        // minVMs[n] contains the minimum number of VMs needed to serve n cores
        int[] minVMs = new int[maxClientCores+1];
        // 0 VMs needed for 0 cores
        minVMs[0] = 0;
        // Calculate only up to the maximum amount of cores a single client has requested
        for(int i=1;i<=maxClientCores;i++){
            int temp = Integer.MAX_VALUE;
            int j = 0;
            // Look only for vm core values that are equal or smaller than the current core count
            while(j<availableVMCoreValues.length && i >= availableVMCoreValues[j]){
                // Look through possible combinations. For example to create a 10 core machine we can add
                // 1 1-core machine to a the machines that already provide 9 cores, or 1 2-core machine to the
                // machines that provide 8 cores or 1 7-core machine to the machines that provide 3 cores
                temp = Math.min(minVMs[i-availableVMCoreValues[j]],temp);
                j = j + 1;
            }
            minVMs[i] =  temp + 1;
        }
        StringBuilder outputString = new StringBuilder();
        int i =1;
        for(Client c:clients){
            outputString.append("Client ").append(i).append(": ").append(minVMs[c.coresRequested]).append(" VMs");
            outputString.append('\n');
            i = i + 1;
        }
        return outputString.toString();
    }

    /**
     * Calculates the max profit that can be achieved by serving customers
     * @return A string with the max profit
     */
    public String maxProfit(){
        // Implement the knapsack algorithm with core number as weight and coreNumber*pricePerCore as value
        double[][] table = new double[clients.size()+1][availableCores+1];
        // First row and first column are already zero
        // Calculating row-by-row
        for(int i = 1;i<table.length;i++){
            // i is the index of the item we are adding
            for(int j = 1; j < table[0].length; j++){
                // j is the number of currently available cores
                // Client at 1st row is the 0th client on the clients array
                Client currentClient = clients.get(i-1);
                // Check if we have enough cores
                if(currentClient.getCoresRequested() > j){
                    table[i][j] = table[i-1][j];
                }else{
                    double clientProfit = currentClient.getCorePrice() * currentClient.getCoresRequested();
                    table[i][j] = Math.max(table[i-1][j],clientProfit+table[i-1][j-currentClient.getCoresRequested()]);
                }
            }
        }
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.HALF_UP);
        return "Total amount: " + df.format(table[clients.size()][availableCores]);
    }

    public static void main(String[] args) {
        Cores c = new Cores(args[0]);
        System.out.print(c.minVMs());
        System.out.println(c.maxProfit());
    }
}
