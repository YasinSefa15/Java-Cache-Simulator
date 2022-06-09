import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

//Yasin Sefa KIRMAN 150119034
//Murat Özcan 150119008
//Berkkan Rençber 150119011
//Batuhan Baştürk 150119035

//to compile in src folder run the command : javac Simulator.java
//to execute in src folder run the command : java Simulator -L1s 0 -L1E 2 -L1b 3 -L2s 1 -L2E 2 -L2b 3 -t test_medium.trace

public class Simulator {
    static Cache L1Data, L1Instruction, L2;
    static String fileName;
    static int L1s = 0, L1E = 0, L1b = 0;
    static int L2s = 0, L2E = 0, L2b = 0;

    public static void main(String[] args) throws IOException {
        createNewRamFile(); //copies untouched ram file since we'll be operating on new ram file
        initCaches(args); //initializes the caches by sets,lines
        readFile(); //reads from .trace files and calls proper methods
        Message.writeCaches(L1Data);
        Message.writeCaches(L1Instruction);
        Message.writeCaches(L2);
        Message.printMessage(); //prints message to console
    }


    static void createNewRamFile() throws IOException {
        Files.deleteIfExists(Paths.get("UpdatedRAM.dat"));
        File source = new File("RAM.dat");
        File dest = new File("UpdatedRAM.dat");
        Files.copy(source.toPath(), dest.toPath());
    }

    static void readFile() throws IOException {
        FileInputStream fis = new FileInputStream(fileName);
        Scanner sc = new Scanner(fis);
        String operation = "";
        String operationAddress = "";
        String size = "";
        String data = "";
        Message.clearFile(); //if log.txt exists and has records from old runtime, they'll be deleted
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            operation = line.substring(0,1);
            operationAddress = line.substring(2,10);
            size = line.substring(12);

            if (line.indexOf(",") != line.lastIndexOf(",")){//if has data
                data = line.substring(15);
            }

            Operation.validSetAndLines(operationAddress); //decides which set will be used and tag value will be assigned
            Message.firstLine = line; //Instruction line
            if (operation.equals("I")){
                Operation.load("I",operationAddress); //calls correct operation
                Message.setOperation("I"); //defines the operation
                Message.keepLog('I'); //keeps log depends on the operation type
            }
            else if (operation.equals("L")){
                Operation.load("D",operationAddress);
                Message.setOperation("D");
                Message.keepLog('L');
            }
            else if (operation.equals("M")){
                Operation.load("D",operationAddress);
                Message.setOperation("D");
                Message.generateSecondLine();
                Message.generateLoadLines();
                Operation.store(operationAddress, data);
                Message.keepLog('M');
            }
            else { //Store operation
                Operation.store(operationAddress, data);
                Message.setOperation("S");
                Message.keepLog('S');
            }
        }
        sc.close();
    }

    static void initCaches(String[] args){
        if (args.length != 14){
            System.out.println(args.length);
            System.out.println("Wrong input type");
            System.exit(0);
        }
        L1s = Integer.parseInt(args[1]);
        L1E = Integer.parseInt(args[3]);
        L1b = Integer.parseInt(args[5]);
        L2s = Integer.parseInt(args[7]);
        L2E = Integer.parseInt(args[9]);
        L2b = Integer.parseInt(args[11]);
        fileName = args[13];

        int L1NumberOfSets = (int) Math.pow(2, L1s);
        int L2NumberOfSets = (int) Math.pow(2, L2s);

        L1Data = new Cache("L1 Data");
        L1Instruction = new Cache("L1 Instruction");
        L2 = new Cache("L2");

        L1Data.initSet(L1NumberOfSets);
        L1Instruction.initSet(L1NumberOfSets);
        for (int i = 0; i < L1NumberOfSets; i++) {
            L1Data.sets[i].initLine(L1E);
            L1Instruction.sets[i].initLine(L1E);
        }

        L2.initSet(L2NumberOfSets);
        for (int i = 0; i < L2NumberOfSets; i++) {
            L2.sets[i].initLine(L2E);
        }

    }
}
