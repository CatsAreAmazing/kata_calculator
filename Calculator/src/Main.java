import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/*С учетом того, что у нас максимальное значение в этом калькуляторе может быть только 100 (10*10), думаю,
 римских чисел больше 100 нам не нужно*/

public class Main {
    public static void main(String[] args) throws IOException {

        Scanner in = new Scanner(System.in);
        //В задании не сказано, что калькулятор обязательно должен перестать работать после вывода нормального ответа, так что
        //для собственного удобства будем просто запрашивать следующий ввод
        while (true){
            System.out.print("Введите выражение: ");
            String expression = in.nextLine();
            expression=expression.toUpperCase(Locale.ROOT).replace(" ", "");//уберем пробелы и зависимость от ввода больших букв
            System.out.println(calc(expression));}
    }
    //метод для выковыривания переменных и знака из строки
    public static List<String> parse_input(String input, HashMap<String, Integer> dict, List<String> operators) throws IOException {
        List<String> numbers=new ArrayList<>();
        String currentPerem="";
        //поскольку число может быть и из двух цифр - мы не можем просто записать каждый символ в отедельную переменную, потому собираем числа в цикле
        for(char c : input.toCharArray()) {
            if(Character.isDigit(c)||dict.containsKey(""+c)){
                currentPerem+=c;
            } else if (operators.contains(""+c) && numbers.isEmpty()) {//Вторым условием избавляемся от вводов вида "9--4" или "5+9+"
                //Хотя эта реализация калькулятора бы посчитала и такую строку верно, но это было бы нелогично
                numbers.add(currentPerem);
                currentPerem="";
                numbers.add(""+c);
            }
            else{
                throw new IOException();

            }
        }
        numbers.add(currentPerem);
        return numbers;
    }

    //метод для проверки того, римское или арабское ли выражение введено
    public static boolean validate(List<String> numbers, HashMap<String, Integer> dict, List<String> operators) throws IOException{
        boolean arabian = true;
        //проверяем, что оба числа арабские и вообще числа...
        try {
            Integer.parseInt(numbers.get(0));
            Integer.parseInt(numbers.get(2));
            arabian = true;
        } catch (NumberFormatException e) {
            arabian = false;
        }
        //проверяем, что не числа - арабские числа, если нет - значит введено что-то некорректное
        //проверка на некорректное уже вообще есть и раньше, но она может пропустить строку вида X0II+5V7
        if (!arabian) {
            for (int i = 0; i <= 2; i += 2) {
                for (char c : numbers.get(i).toCharArray()) {
                    if (!dict.containsKey("" + c)) {
                        throw new IOException();
                    }
                }
            }
        }
        return arabian;
    }


    //метод для преобразования ответа обратно в римские цифры
    public  static String arabian_to_roman(String result){
        Integer number=Integer.parseInt(result);
        result="";
        while (number>0){
            if(number/100>0){
                if(number-100>=0){
                    result+="C";
                    number-=100;
                }
            } else if (number/10>0) {
                if(number-90>=0){
                    result+="XC";
                    number-=90;
                } else if (number-50>=0) {
                    result+="L";
                    number-=50;
                } else if (number-40>=0) {
                    result+="XL";
                    number-=40;}
                  else if (number-10>=0) {
                    result+="X";
                    number-=10;
                }
            }
            else{
                if(number-9>=0){
                    result+="IX";
                    number-=9;
                }
                else if(number-5>=0){
                    result+="V";
                    number-=5;
                }
                else if (number-4>=0) {
                    result+="IV";
                    number-=4;}
                else{
                    result+="I";
                    number-=1;
                }
            }
        }
        return result;
    }



    //метод для преобразования римских цифр в арабские для подсчетов. Уверен, что в интернете где-то лежит готовое, но мне лень искать, сделаю своё
    //я помню, что достаточно переводить числа 1-10, но так не интересно. Попробуем просто сделать максимально универсальное решение
    //плюс этого подхода, что увеличивать можно бесконечно просто добавлением чисел в словарик
    public static List<String> roman_to_arabian(List<String> numbers, HashMap<String, Integer> dict){
        Integer result=0;
        Integer Current=0;
        Integer Next;
        for (int i = 0; i <= 2; i += 2) {
            for (int j = 0; j < numbers.get(i).toCharArray().length; j++) {
                try {
                    Current = dict.get("" + numbers.get(i).toCharArray()[j]);
                    Next = dict.get("" + numbers.get(i).toCharArray()[j + 1]);
                    if (Current < Next) {
                        result -= Current;
                    } else {
                        result += Current;
                    }
                } catch (Exception e) {
                    result += Current;
                    numbers.set(i, String.valueOf(result));
                    break;
                }
            }
            result=0;
        }
        System.out.println(numbers);
        return numbers;
    }

    public static String calc(String input) throws IOException, ArithmeticException {
        Integer result=0; //Оно ругается, что переменная может быть не инициализирована :(
        /*Скорее всего, это можно сделать адекватнее, в том же питоне ничего не мешает создать словать сразу,
        а не вбивать все построчно, но в гугле структуры вида dict={key: value, key: value} не нашлось
        В любом случае, делаем словарик, по которому будем разбирать введенные римские числа\собирать результат*/
        HashMap<String, Integer> dictionary = new HashMap<>();
        dictionary.put("I", 1);
        dictionary.put("V", 5);
        dictionary.put("X", 10);
        dictionary.put("L", 50);
        dictionary.put("C", 100);
        //делаем список с операциями. Можно и без него, конечно, но почему бы нет. Когда я узнал, что в Java есть относительно питоноподобные списки - мне слишком захотелось его использовать
        List<String> operators=new ArrayList<>();
        operators.add("+");
        operators.add("-");
        operators.add("*");
        operators.add("/");
        operators.add(":");
        List<String> numbers;
        numbers=parse_input(input, dictionary, operators);
        //Когда у нас есть список с готовыми выражениями вида [число 1, знак, число 2] мы можем проверить, являются ли эти числа арабскими ими римскими
        Boolean is_arabian=validate(numbers, dictionary, operators);
        //делаем римские выражения арабскими для корректного подсчета
        if(!is_arabian){
            numbers=roman_to_arabian(numbers, dictionary);
        }
        //ближе к концу работы я вспомнил, что калькулятору обязательно обрабатывать только числа 1-10. Так что оставлю это условие тут
        if(Integer.parseInt(numbers.get(0))>10||Integer.parseInt(numbers.get(0))<1||Integer.parseInt(numbers.get(2))>10||Integer.parseInt(numbers.get(2))<1){
            throw new IOException();
        }
        switch (numbers.get(1)){
            case ("-"):
                result=Integer.parseInt(numbers.get(0))-Integer.parseInt(numbers.get(2));
                break;
            case ("+"):
                result=Integer.parseInt(numbers.get(0))+Integer.parseInt(numbers.get(2));
                break;
            case ("*"):
                result=Integer.parseInt(numbers.get(0))*Integer.parseInt(numbers.get(2));
                break;
            /*по заданию не очень ясно - остаток вообще просто игнорировать или всё же стоило
            сделать округление по правилам математики.
            Но проигнорировать проще, так что я проигнорировал*/
            case (":"):
                result=Integer.parseInt(numbers.get(0))/Integer.parseInt(numbers.get(2));
                break;
            case ("/"):
                result=Integer.parseInt(numbers.get(0))/Integer.parseInt(numbers.get(2));
                break;
        }
        //не арабские числа переводим в римские
        if(is_arabian){
            return String.valueOf(result);}
        else {
            if(result<1){
                throw new ArithmeticException();
            }
            return arabian_to_roman(String.valueOf(result));
        }
    }
}