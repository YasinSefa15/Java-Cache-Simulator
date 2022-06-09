
public class Converter {
    //This function converts the binary number to hexadecimal number and returns converted string.
    static String ConvertToHex(String binary) {
        String temp="",tempResult="",result="";
        int total=0;
        for (int i=binary.length()-1; i>=0;i--){
            temp=temp+binary.charAt(i);
            if (temp.length() % 4 ==0){
                for(int j = 0; j<=3; j++) {
                    int c = temp.charAt(j) - '0';
                    total = total + (int)Math.pow(2, j) * (int)c;
                }
                temp = "";
                if(total == 10)
                    tempResult = tempResult + "A";
                else if(total == 11)
                    tempResult = tempResult + "B";
                else if(total == 12)
                    tempResult = tempResult + "C";
                else if(total == 13)
                    tempResult = tempResult + "D";
                else if(total == 14)
                    tempResult = tempResult + "E";
                else if(total == 15)
                    tempResult = tempResult + "F";
                else
                    tempResult = tempResult + total;

                total = 0;
            }
        }
        if (temp.length()>0){
            for(int j = 0; j<temp.length(); j++) {
                int c = temp.charAt(j) - '0';
                total = total + (int)Math.pow(2, j) * (int)c;
            }
            tempResult=tempResult+total;
        }
        if(tempResult.length()<16){
            int tempLength=tempResult.length();
            for (int i=1;i<=16-tempLength;i++){
                tempResult=tempResult+'0';
            }
        }
        for (int i=tempResult.length()-1;i>=0;i--){
            result=result+tempResult.charAt(i);
        }
        return result;
    }

    //This function converts the hexadecimal number to binary number and returns converted string.
    static String ConvertToBinary(String hexNumber){
        String temp="",result="";
        int total=0;
        for (int i=0; i<hexNumber.length();i++){
            temp=temp+hexNumber.charAt(i);

            if (temp.equals("a"))
                total=10;
            else if(temp.equals("b"))
                total=11;
            else if (temp.equals("c"))
                total=12;
            else if (temp.equals("d"))
                total=13;
            else if (temp.equals("e"))
                total=14;
            else if (temp.equals("f"))
                total=15;
            else {
                total = Integer.parseInt(temp);
            }
            temp="";
            for (int j=3;j>=0;j--){
                if(total>=Math.pow(2,j)){
                    total-=Math.pow(2,j);
                    result=result+"1";
                }else{
                    result=result+"0";
                }
            }
            total=0;
        }
        if (result.length()<32){
            int resultLength=result.length();
            for (int i=1;i<=32-resultLength;i++){
                result='0'+result;
            }
        }
        return result;
    }

    static Integer binaryToDecimal(String binaryNumber){
        int currentIndex = binaryNumber.length() - 1 ;
        int result = 0;
        while (currentIndex >= 0){
            if (binaryNumber.indexOf(currentIndex) == 1){
                result += Math.pow(2,(binaryNumber.length() - 1) - currentIndex);
            }
            currentIndex--;
        }
        return result;
    }

    public static int hexaToDecimal(String hex) {
        hex = hex.toUpperCase();
        int n = hex.length();
        int base = hex.length();
        int pow = 0;
        int decimal = 0;
        for(int i = 0; i < n; i++) {
            char c = hex.charAt(i);

            pow = (int)Math.pow(16, base - 1);

            if( c >='0' && c <='9' ) {
                decimal += pow * (c - 48);
            }
            else if( c >='A' && c <= 'F' ) {
                decimal += pow * (c - 65 + 10);
            }
            base--;
        }
        return  decimal;
    }

    public static String DecimalToHex(String l) {

        long value = Long.parseLong(l);
        String reversedHexa = "";
        String Hexa = "";
        char ch;

        while(value != 0) {
            long hexvalue = value % 16;
            if(hexvalue >= 0 && hexvalue < 10) {
                reversedHexa += (char)(hexvalue + '0');
                value = value / 16;
            }
            else {
                reversedHexa += (char)(hexvalue - 10 +'A');
                value = value / 16;
            }
        }

        while(reversedHexa.length() != 16) {
            reversedHexa += "0";
        }

        for(int i = 0; i < 16; i++) {
            ch = reversedHexa.charAt(i);
            Hexa = ch + Hexa;
        }
        return Hexa;
    }
}

