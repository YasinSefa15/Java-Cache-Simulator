public class Line {
    boolean valid;
    String tag; //t = m(which32) - (s bits long + b bits)
    String data; //b bits long
    int time;

    Line(){
        valid = false;
        tag = "";
        data = "";
        time = 0;
    }

    String content(){
        return  "\t\t\t\t'tag' : '" + tag + "',\n" +
                "\t\t\t\t'time' : '" + time + "',\n" +
                "\t\t\t\t'valid' : '" + valid + "',\n" +
                "\t\t\t\t'data' : '" + data + "',";
    }
}
