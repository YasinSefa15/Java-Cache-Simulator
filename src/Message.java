import java.io.FileWriter;

public class Message {
    static String operation, firstLine, secondLine, thirdLine, fourthLine, fifthLine;

    //creates new log.txt file
    static void clearFile(){
        try {
            FileWriter fw = new FileWriter("log.txt");
            fw.close();
        }catch (Exception e){
            System.out.println("Error on Message.java line 15.");
            System.exit(0);
        }

    }

    static void writeCaches(Cache cache){
        try
        {
            FileWriter fw = new FileWriter("caches/" + cache.type + ".txt");
            fw.write(cache.content() + "\n");
            fw.close();
        }
        catch(Exception e)
        {
            System.err.println("An error occurred on Message.java method:writeCaches. Error : " + e.toString());
            System.exit(0);
        }
    }


    //writes to the file
    static void keepLog(char lineOperation){
        switch (lineOperation) {
            //calls right functions based on the operation
            case 'I', 'L' -> {
                generateSecondLine();
                generateLoadLines();
            }
            case 'S' -> {
                generateSecondLine();
                generateStoreLines();
            }
            case 'M' -> generateModifyLines();
            default -> {
            }
        }
        try
        {
            FileWriter fw = new FileWriter("log.txt", true);
            fw.write(logMessage() + "\n");
            fw.close();
        }
        catch(Exception e)
        {
            System.err.println("An error occurred on Message.java method:keepLog. Error : " + e.toString());
            System.exit(0);
        }

    }

    //generate methods creates lines dynamically
    static void generateModifyLines(){
        //if hit is true that means found a match
        fourthLine += "L1" + operation + (Operation.L1Hit ? " hit, " : " miss, ");
        fourthLine += Operation.L2Hit ? "L2 hit" : "L2 miss";
        fifthLine += "Store in L1D, L2, RAM";
    }

    static void generateSecondLine(){
        secondLine = "L1" + operation + " ";
        thirdLine = "";
        secondLine += Operation.L1Hit ? "hit, " : "miss, ";
        secondLine += Operation.L2Hit ? "L2 hit" : "L2 miss";
    }

    static void generateLoadLines(){
        thirdLine += Simulator.L2s == 0 ? "Place in L2, " : "Place in L2 set " + Operation.L2UsedSet + ", ";
        thirdLine += Simulator.L1s == 0 ? "Place in L1" + operation :
                "Place in L1"+ operation +" set " + Operation.L1UsedSet ;
    }

    static void generateStoreLines(){
        thirdLine += "Store in L1" + operation + ", L2, RAM";
    }

    //returns the message will be printed to the file
    static String logMessage(){
        String logMessage = firstLine +
                "\n\t" + secondLine +
                "\n\t" + thirdLine;
        if (!fourthLine.equals("")){
            logMessage += "\n\t" + fourthLine +
                            "\n\t" + fifthLine;
        }
        return logMessage;
    }

    //console message at the end of the program done
    static void printMessage(){
        System.out.println("L1I-hits:" + Operation.L1I_Hits + " L1I-misses:" + Operation.L1I_Misses + " L1I-evictions:" + Operation.L1I_Eviction);
        System.out.println("L1D-hits:" + Operation.L1D_Hits + " L1D-misses:" + Operation.L1D_Misses + " L1D-evictions:" + Operation.L1D_Eviction);
        System.out.println("L2-hits:" + Operation.L2_Hits + " L2-misses:" + Operation.L2_Misses + " L2-evictions:" + Operation.L2_Eviction);
    }

    static void setOperation(String oper){
        //before writing to file we need to do the assignment since we're checking this lines values each iteration
        fourthLine = ""; fifthLine = "";
        operation = oper;
    }
}
