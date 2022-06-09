import java.io.RandomAccessFile;

public class Operation {
    static int time, placeableLineIndex;
    static boolean L1Hit = false, placed = false, timeIncremented = false, L2Hit = false;
    static int L1UsedSet; //set index
    static int L2UsedSet, L2UsedLine;
    static String L1tag, L2tag;
    static int L1I_Hits = 0, L1I_Misses = 0, L1I_Eviction = 0;
    static int L1D_Hits = 0, L1D_Misses = 0, L1D_Eviction = 0;
    static int L2_Hits = 0, L2_Misses = 0, L2_Eviction = 0;


    static void ifL1DMiss(String address){
        L1D_Misses++; //Increment misses variable by one
        int minTimeValue = Simulator.L1Data.sets[L1UsedSet].lines[0].time; //keeps the lowest minTimeValue line
        Line currentLine = null;
        for (int i = 0 ; i < Simulator.L1E ; i++){ //traverse in all lines
            currentLine = Simulator.L1Data.sets[L1UsedSet].lines[i];
            if (currentLine.valid) { //if line is valid we will trace it by its time value
                if (currentLine.time < minTimeValue){
                    minTimeValue = currentLine.time;
                    placeableLineIndex = i;
                }
            } else { //we found a place to put data
                placed = true; //placed data to a line
                currentLine.valid = true;
                currentLine.tag = L1tag;
                currentLine.data = fetchFromMemory(Converter.hexaToDecimal(address));
                timeIncremented = true; //time incremented
                time++;
                currentLine.time = time;
                break;
            }
        }
    }

    static void ifL1IMiss(String address){
        L1I_Misses++;
        Line currentLine = null;
        int minTimeValue = Simulator.L1Data.sets[L1UsedSet].lines[0].time;
        for (int i = 0 ; i < Simulator.L1E ; i++){
            currentLine = Simulator.L1Instruction.sets[L1UsedSet].lines[i];
            if (!currentLine.valid){
                placed = true;
                currentLine.valid = true;
                currentLine.tag = L1tag;
                timeIncremented = true;
                currentLine.data = fetchFromMemory(Converter.hexaToDecimal(address));
                time++;
                currentLine.time = time;
                break;
            }else {
                if (currentLine.time < minTimeValue){
                    minTimeValue = currentLine.time;
                    placeableLineIndex = i;
                }
            }
        }
    }

    //has similar principle with ifL1DMiss method
    static void ifL2Miss(String address){
        if (!L2Hit){
            L2_Misses++;
            int minTimeValue = Simulator.L1Data.sets[L1UsedSet].lines[0].time;
            Line currentLine = null;
            for (int i = 0 ; i < Simulator.L2E ; i++){
                currentLine = Simulator.L2.sets[L2UsedSet].lines[i];
                if (!currentLine.valid){
                    placed = true;
                    currentLine.valid = true;
                    currentLine.tag = L2tag;
                    currentLine.data = fetchFromMemory(Converter.hexaToDecimal(address));
                    time += timeIncremented ? 0 : 1; //in all operations time can be incremented only once
                    currentLine.time = time;
                    break;
                }else { //trace the line has the lowest time.
                    if (currentLine.time < minTimeValue){
                        minTimeValue = currentLine.time;
                        placeableLineIndex = i;
                    }
                }
            }
            if (!placed){
                L2_Eviction++;
                ifNotPlaced(Simulator.L2,L2UsedSet,L2tag, address, placeableLineIndex, null);
            }
        }
    }

    static void load(String type, String address){
        L1Hit = false;
        L2Hit = false;
        placed = false;
        timeIncremented = false;
        placeableLineIndex = 0;
        //InstructionCacheLoad
        if (type.equals("I")){
            //first travels through the lines and search whether it is already put in cache
            for (int i = 0 ; i < Simulator.L1E ; i++){
                //checks if data is already put in cache
                if (Simulator.L1Instruction.sets[L1UsedSet].lines[i].valid){
                    if (Simulator.L1Instruction.sets[L1UsedSet].lines[i].tag.equals(L1tag)){
                        L1Hit = true;
                        L1I_Hits++;
                        break;
                    }
                }
            }
            //if there is no hit then load from RAM and save it to L1Instruction cache
            if (!L1Hit){
                ifL1IMiss(address);
                if (!placed){
                    L1I_Eviction++; //New data will be replaced with the old saved data.
                    timeIncremented = true;
                    time++;
                    ifNotPlaced(Simulator.L1Data, L1UsedSet, L1tag, address, placeableLineIndex, null);
                }
            }
        }
        else if(type.equals("D")){
            //first travels through the lines and search whether it is already put in cache
            for (int i = 0 ; i < Simulator.L1E ; i++){
                //checks if data is already put in cache
                if (Simulator.L1Data.sets[L1UsedSet].lines[i].valid){
                    if (Simulator.L1Data.sets[L1UsedSet].lines[i].tag.equals(L1tag)){
                        L1Hit = true;
                        L1D_Hits++;
                        break;
                    }
                }
            }
            //if there is no hit then load from RAM and save it to L1Data cache
            if (!L1Hit){
                ifL1DMiss(address);
                if (!placed){
                    L1D_Eviction++;
                    timeIncremented = true;
                    time++;
                    ifNotPlaced(Simulator.L1Data, L1UsedSet, L1tag, address, placeableLineIndex, null);
                }
            }

        }

        placeableLineIndex = 0;
        placed = false;
        //traversing in L2Cache lines in corrects set
        for (int i = 0 ; i < Simulator.L2E ; i++){
            //checks if data is already put in cache
            if (Simulator.L2.sets[L2UsedSet].lines[i].valid){
                if (Simulator.L2.sets[L2UsedSet].lines[i].tag.equals(L2tag)){
                    L2Hit = true;
                    L2_Hits++;
                    break;
                }
            }
        }
        //if there is no hit then load from RAM and save it to L2 cache
        ifL2Miss(address);
    }

    //if all blocks are valid then place it to block has the smallest time value
    private static void ifNotPlaced(Cache cache,int usedSet, String tag,String address, int placeableLineIndex, String data) {
        Line currentLine;
        currentLine = cache.sets[usedSet].lines[placeableLineIndex];
        currentLine.valid = true;
        currentLine.tag = tag;
        currentLine.data = data == null ? fetchFromMemory(Converter.hexaToDecimal(address)) : data;
        currentLine.time = time;
    }

    //will go to ram and save the given value. Also update the caches
    static void store(String address ,String data){
        L1Hit = false;
        L2Hit = false;
        placed = false;
        timeIncremented = false;
        int placeableLineIndex = 0;
        String changeableData = fetchFromMemory(Converter.hexaToDecimal(address)); //old value fetch from ram
        changeableData = updateValue(data, changeableData); //new changed value

        //before updating caches, go to ram and update the value.
        updateMemory(changeableData, Converter.hexaToDecimal(address));

        //checks if data is already put in cache. if it is then update the data
        for (int i = 0 ; i < Simulator.L1E ; i++){
            if (Simulator.L1Data.sets[L1UsedSet].lines[i].valid){
                if (Simulator.L1Data.sets[L1UsedSet].lines[i].tag.equals(L1tag)){
                    Simulator.L1Data.sets[L1UsedSet].lines[i].data = changeableData;
                    L1Hit = true;
                    L1D_Hits++;
                    break;
                }
            }
        }
        //could not hit in L1Data cache
        if (!L1Hit){
            L1D_Misses++;
            int minTimeValue = Simulator.L1Data.sets[L1UsedSet].lines[0].time;
            Line currentLine = null;
            for (int i = 0 ; i < Simulator.L1E ; i++){
                currentLine = Simulator.L1Data.sets[L1UsedSet].lines[i];
                if (currentLine.valid) {
                    if (currentLine.time < minTimeValue){
                        minTimeValue = currentLine.time;
                        placeableLineIndex = i;
                    }
                } else {
                    placed = true;
                    currentLine.valid = true;
                    currentLine.tag = L1tag;
                    currentLine.data = changeableData;
                    timeIncremented = true;
                    time++;
                    currentLine.time = time;
                    break;
                }
            }
            if (!placed){
                L1D_Eviction++;
                ifNotPlaced(Simulator.L1Data, L1UsedSet, L1tag ,address, placeableLineIndex, changeableData);
            }
        }

        //traversing in L2Cache lines in corrects set
        for (int i = 0 ; i < Simulator.L2E ; i++){
            //checks if data is already put in cache
            if (Simulator.L2.sets[L2UsedSet].lines[i].valid){
                if (Simulator.L2.sets[L2UsedSet].lines[i].tag.equals(L2tag)){
                    Simulator.L2.sets[L2UsedSet].lines[i].data = changeableData;
                    L2Hit = true;
                    L2_Hits++;
                    break;
                }
            }
        }
        //if there is no hit then load from RAM and save it to L2 cache
        if (!L2Hit){
            placeableLineIndex = 0;
            L2_Misses++;
            int minTimeValue = Simulator.L1Data.sets[L1UsedSet].lines[0].time;
            Line currentLine = null;
            for (int i = 0 ; i < Simulator.L2E ; i++){
                currentLine = Simulator.L2.sets[L2UsedSet].lines[i];
                if (!currentLine.valid){
                    placed = true;
                    currentLine.valid = true;
                    currentLine.tag = L2tag;
                    currentLine.data = changeableData;
                    time += timeIncremented ? 0 : 1;
                    currentLine.time = time;
                    break;
                }else { //trace the line has the lowest time.
                    if (currentLine.time < minTimeValue){
                        minTimeValue = currentLine.time;
                        placeableLineIndex = i;
                    }
                }
            }
            if (!placed){
                L2_Eviction++;
                ifNotPlaced(Simulator.L2, L2UsedSet, L2tag, address, placeableLineIndex, changeableData);
            }
        }
    }

    //updates the value of in the ram given address
    static String updateValue(String data, String oldValue){
        StringBuilder newValue = new StringBuilder();
        int i = 0;
        for (; i < data.length() ; i++){
            newValue.append(data.charAt(i));
        }
        for (; i < oldValue.length() ; i ++){
            newValue.append(oldValue.charAt(i));
        }
        return newValue.toString();
    }

    //fetch data from the memory
    static String fetchFromMemory(int address){
        try {
            RandomAccessFile file = new RandomAccessFile("UpdatedRAM.dat", "r");
            file.seek(address); //Seeks the address
            long x = file.readLong(); //gets the value in the address
            file.close();
            return Converter.DecimalToHex(String.valueOf(Math.abs(x))).toLowerCase();
        }
        catch (Exception e){
            System.out.println("An error occurred on Operation.java method:fetchFromRam. Error : " + e.toString());
            System.exit(0);
        }
        return "";

    }

    //writes to memory
    static void updateMemory(String data, int address){
        try {
            RandomAccessFile file = new RandomAccessFile("UpdatedRAM.dat", "rw");
            file.seek(address); //seeks the address
            file.writeBytes(data); //writes to the address value
            file.close();
        }catch (Exception e){
            System.out.println("An error occurred on Operation.java method:UpdateMemory. Error : " + e.toString());
            System.exit(0);
        }

    }

    //parses the tag, set info from read address
    static void validSetAndLines(String address){
        String binaryAddress = Converter.ConvertToBinary(address);
        //operations on L1
        L1tag = binaryAddress.substring(0, binaryAddress.length() - (Simulator.L1s + Simulator.L1b));
        String set = binaryAddress.substring(L1tag.length(), L1tag.length() + Simulator.L1s);
        String line = binaryAddress.substring(L1tag.length() + set.length());
        L1tag = Converter.ConvertToHex(L1tag);
        L1UsedSet = Converter.binaryToDecimal(set) % Simulator.L1Instruction.sets.length;


        L2tag = binaryAddress.substring(0, binaryAddress.length() - (Simulator.L2s + Simulator.L2b));
        set = binaryAddress.substring(L2tag.length(), L2tag.length() + Simulator.L2s);
        line = binaryAddress.substring(L2tag.length() + set.length());
        L2tag = Converter.ConvertToHex(L2tag);
        L2UsedSet = Converter.binaryToDecimal(set) % Simulator.L2.sets.length;

    }
}
