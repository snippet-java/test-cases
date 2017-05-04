import java.util.*;
public class test02 {
  public static void main(String[] args) {
    String phrase = "this is a reversed phrase";
    List<String> list = Arrays.asList(phrase.split(" "));
    Collections.reverse(list);
    System.out.println(String.join(" ", list.toArray(new String[0])));
  }
}
