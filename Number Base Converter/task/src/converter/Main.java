package converter;

import converter.radixconverter.RadixConverter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
       // System.out.println(RadixConverter.RadixToRadix("af.xy",35,17,5));

        boolean exitFlag=false;
        while(!exitFlag) {

            System.out.print("Enter two numbers in format: {source base} {target base} (To quit type /exit) ");
            String choice = scanner.next();
            switch(choice) {
                case "/exit":
                    exitFlag=true;
                    break;
                default:
                    int sourceBase = Integer.valueOf(choice).intValue();
                    int targetBase = scanner.nextInt();
                    ConvertToAnyBase(sourceBase, targetBase);
                    break;
            }
        }
    }

    public static void ConvertToAnyBase(int sourceBase, int targetBase) {
        boolean exitFlag = false;
        while (!exitFlag) {
            System.out.print("Enter number in base " + sourceBase + " to convert to base " + targetBase + " (To go back type /back) ");
            String input = scanner.next();
            if (input.equals("/back"))
                exitFlag = true;
            else
                System.out.println("Conversion result: " +
                        RadixConverter.RadixToRadix(input, sourceBase, targetBase,5).toLowerCase() + "\n");
        }
    }

    public static void FromChoice() {
        System.out.print("Enter number in decimal system:");
        int number = scanner.nextInt();

        System.out.print("Enter target base: ");
        int radix = scanner.nextInt();

        System.out.println("Conversion result: " + RadixConverter.DecToRadix(number, radix)+"\n");
    }

    public static void ToChoice() {
        System.out.print("Enter source number:");
        String number = scanner.next();

        System.out.print("Enter source base:");
        int radix = scanner.nextInt();

        System.out.println("Conversion to decimal result:" + RadixConverter.RadixToDec(number, radix)+"\n");

    }
}
