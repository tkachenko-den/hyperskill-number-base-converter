package converter.radixconverter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Класс, по преобразовыванию чисел между разными системами счисления. Создан в качестве учебного проекта без
 * использования готовых библиотечных методов, но с использованием алгоритмов преобразования между различными базисами
 */
public class RadixConverter {

    /**
     * Преобразование числа в десятичной системе счисления в число в другой системе счисления, заданной параметром radix
     *
     * @param decimal число в десятичной системе
     * @param radix   базис системы в которую нужно перевести
     * @return строковое предствление числа в заданной системе
     */
    public static String DecToRadix(long decimal, int radix) {
        return RemaindersToRadixSymbols(GetRemaindersListReversed(decimal, radix));
    }

    /**
     * Преобразование числа, представленного в строковом виде, из системы счисления с базисом radix в
     * десятичную систему счисления
     * @param number число в системе счисления с базисом radix
     * @param radix базис системы счисления преобразуемого числа
     * @return Число в десятичной системе
     */
    public static long RadixToDec(String number, int radix) {
        int len = number.length();
        return Math.round(
                IntStream.range(1, len + 1)
                        .mapToDouble((i) ->
                                RadixSymbolToDec(number.charAt(i - 1)) * Math.pow(radix, len - i)
                        ).sum()
        );
    }

    /**
     * Преобразование числа, представленного в строковом виде, из системы счисления с базисом radix в
     * десятичную систему счисления. Результат представлен в BigInteger. Исходное число может быть любого размера
     * @param number число в системе счисления с базисом radix
     * @param radix базис системы счисления преобразуемого числа
     * @return Число BigInteger в десятичной системе
     */
    public static BigInteger RadixToBigInteger(String number, int radix) {
        return IntStream.rangeClosed(1, number.length())
                .mapToObj((i) ->
                        BigInteger
                                .valueOf(RadixSymbolToDec(number.charAt(i - 1)) )
                                .multiply(BigInteger.valueOf(radix).pow(number.length() - i)))
                .reduce(BigInteger.ZERO, BigInteger::add);
    }

    /**
     * Преобразование числа с дробной частью, представленного в строковом виде, из системы счисления с базисом radix в
     * десятичную систему счисления. Результат представлен в BigDecimal. Исходное число может быть любого размера
     * @param number вещественное число в системе счисления с базисом radix
     * @param radix базис системы счисления преобразуемого числа
     * @return Вещественное число в десятичной системе
     */
    public static BigDecimal RadixToBigDecimal(String number, int radix) {
        int pointPos = number.indexOf('.')+1;
        return IntStream.rangeClosed(1, number.length())
                .mapToObj(i ->
                        i == pointPos ?
                                BigDecimal.ZERO : // Точку переводим как ноль
                                i < pointPos ?
                                        // Перевод символа целой части
                                        BigDecimal.valueOf(RadixSymbolToDec(number.charAt(i - 1)))
                                                .multiply(BigDecimal.valueOf(radix).pow(pointPos - i - 1)) :
                                        // Перевод символа дробной части
                                        BigDecimal.valueOf(RadixSymbolToDec(number.charAt(i - 1)))
                                                .divide(BigDecimal.valueOf(radix).pow(Math.abs(pointPos - i)),20, RoundingMode.HALF_DOWN)
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static String BigDecimalToRadix(BigDecimal number, int radix, int scale) {
        // Отделяем целую часть и дробную
        BigDecimal[] parts = number.divideAndRemainder(BigDecimal.ONE);

        String partOne = RemaindersToRadixSymbols(GetRemaindersListReversed(parts[0].toBigInteger(), radix));
        String partTwo = ScaleString(DecFractionalPartToRadix(parts[1], radix,scale),scale,'0');

        return partOne + "." + partTwo;
    }

    protected static String ScaleString(String str, int scale, char scaleSymbol) {
        final char[] arr = new char[scale];
        Arrays.fill(arr, scaleSymbol);
        String fills = new String(arr);
        return str.length() >= scale ?
                str.substring(0, scale) :
                str + fills.substring(0, scale - str.length());
    }


    /**
     * Преобразования числа из одной системы счисления в другую
     * @param number строковое представления числа в исходной системе счисления
     * @param radixSource базис исходной системы счисления
     * @param radixTarget базис целевой системы счисления
     * @return строковое представление числа в целевой системе счисления
     */
    public static String RadixToRadix(String number, int radixSource, int radixTarget, int scale) {
        return number.indexOf('.') > 0 ?
                BigDecimalToRadix(RadixToBigDecimal(number, radixSource), radixTarget, scale) :
                RemaindersToRadixSymbols(
                        GetRemaindersListReversed(
                                RadixToBigInteger(number, radixSource),
                                radixTarget)
                );
    }

    protected static String DecFractionalPartToRadix(BigDecimal fractional, int radix, int scale) {
        List<Integer> list = new ArrayList<Integer>();
        do {
            BigDecimal[] parts = fractional
                    .multiply(BigDecimal.valueOf(radix))
                    .divideAndRemainder(BigDecimal.ONE);
            list.add(parts[0].intValue());
            fractional = parts[1];
        } while (fractional.compareTo(BigDecimal.ZERO)!=0 && list.size()<scale);

        return RemaindersToRadixSymbols(list);
    }

    /**
     * Получение символа системы с произвольным базисом по символу из десятичной системы
     *
     * @param decimal символ(цифра) в десятичной системы
     * @return символ в требуемой системе
     */
    protected static char DecToRadixSymbol(int decimal) {
        return (char) (decimal + (decimal > 9 ? 55 : 48));
    }

    /**
     * Перевод символа (цифры) произвольной системы счисления в число десятичной системы счисления
     * @param symbol - символ произвольной системы счисления
     * @return - соответствующее символу число в десятичной системе
     */
    protected static int RadixSymbolToDec(char symbol) {
        int s = String.valueOf(symbol).toUpperCase().charAt(0);
        return (s >= 48 && s <= 57) ?
                s - 48 :
                s - 55;
    }

    /**
     * Получение массива остатков (в реверсном порядке) от рекурсивного деления числа decimal на radix
     *
     * @param decimal число которое делим
     * @param radix   число на которое делим
     * @return массив остатков в обратном порядке
     */
    protected static List<Integer> GetRemaindersListReversed(long decimal, int radix) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        do {
            result.add(0, (int)(decimal % radix));
            decimal /= radix;
        } while (decimal > 0);
        return result;
    }

    /**
     * Получение массива остатков (в реверсном порядке) от рекурсивного деления числа decimal (типа BigInteger) на radix
     * @param decimal число которое делим
     * @param radix число на которое делим
     * @return массив остатков в обратном порядке
     */
    protected static List<Integer> GetRemaindersListReversed(BigInteger decimal, int radix) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        do {
            BigInteger[] pair = decimal.divideAndRemainder(BigInteger.valueOf(radix));
            result.add(0, pair[1].intValue());
            decimal = pair[0];
        } while (decimal.compareTo(BigInteger.ZERO)==1);
        return result;
    }

    /**
     * Склеивание массива остатков в строку с переводом в символы соответствующей системы счисления
     *
     * @param list массив остатков
     * @return получившаяся строка
     */
    protected static String RemaindersToRadixSymbols(List<Integer> list) {
        return list.stream().map(e -> String.valueOf(DecToRadixSymbol(e))).collect(Collectors.joining());
    }


}
