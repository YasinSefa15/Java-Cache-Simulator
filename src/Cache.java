public class Cache {
    String type; //type defines what type of cache this is
    Set[] sets; //holds the sets belongs to cache

    //Object can be created with given type
    Cache(String type){
        this.type = type;
    }

    //Initializes cache structure by the given size
    void initSet(int size){
        sets = new Set[size];
        for(int i = 0; i < size; i++){
            sets[i] = new Set();
            sets[i].index = i;
        }
    }

    String content(){
        StringBuilder content = new StringBuilder("[\n\t'Cache Type' : '" + type + "',");
        StringBuilder setsString = new StringBuilder("");
        StringBuilder setLines;
        for (int i = 0 ; i < sets.length ; i++){
            setsString = new StringBuilder("");
            setsString.append("\n").append("    '").append(i).append("' :{\n\t\t\t{");
            setLines = new StringBuilder("");
            for (int j = 0 ; j < sets[i].lines.length ; j++){
                if (j == 0){
                    setLines.append("\n").append(sets[i].lines[j].content()).append("\n\t\t\t},");
                }
                else{
                    setLines.append("\n\t\t\t{\n").append(sets[i].lines[j].content()).append("\n\t\t\t},\n");
                }
            }
            setsString .append(setLines).append("\t\t},");
            content.append(setsString);
        }
        content.append("\n]");

        return content.toString();
    }

}
