package jp.co.sandbox.stream;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by kawakami_note on 2016/02/14.
 */
public class StreamSandbox {
    public static void main(String[] args) {
        System.out.println("### print numbers ###");
        int[] numbers = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        Arrays.stream(numbers).filter(num -> num % 2 == 0).forEach(System.out::println);

        System.out.println("### print words' length ###");
        List<String> words = Arrays.asList("Cupcake", "Donut", "Eclair", "Froyo", "Gingerbread", "Honyecomb", "Ice Cream Sandwich", "JerryBeans");
        words.stream().mapToInt(String::length).forEach(System.out::println);

        System.out.println("### print flat map ###");
        List<String> texts = Arrays.asList("Ibaragi,Mito", "Shimane,Matsue", "Ehime,Matsuyama");
        texts.stream().flatMap(text -> Arrays.stream(text.split(","))).forEach(System.out::println);

        System.out.println("### print collect ###");
        List<Integer> numberList = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        List<Integer> odds = numberList.stream().filter(num -> num % 2 == 1).collect(Collectors.toList());
        odds.stream().forEach(System.out::println);

        Double average = words.stream().collect(Collectors.averagingInt(String::length));
        System.out.println(String.format("print average:%s", average));

        List<String> people = Arrays.asList("Chihiro Kaneko", "Ken Tokame", "Kenta Maeda", "Hiroki Kuroda", "Kei Igawa");
        Map<String, List<String>> dictionary = people.stream().collect(Collectors.groupingBy(name -> {
            int index = name.lastIndexOf(" ");
            return name.substring(index + 1, index + 2);
        }));
        System.out.println(dictionary);

        Map<String, Long> dictionaryCount = people.stream().collect(Collectors.groupingBy(name -> {
            int index = name.lastIndexOf(" ");
            return name.substring(index + 1, index + 2);
        }, Collectors.counting()));
        System.out.println(dictionaryCount);

        dictionary.entrySet().stream().forEach(System.out::println);
        dictionaryCount.entrySet().stream().forEach(System.out::println);
    }
}
