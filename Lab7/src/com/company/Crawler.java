package com.company;
import java.net.*;
import java.util.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * В этом классе реализован основной функционал Crawler.
 * Содержит метод getAllLinks, который ищет и возвращает все ссылки, найденные на странице,
 * а метод main работает со ссылками, размещая их в необходимые связанные списки.
 */
public class Crawler {
    private static final String URL_PREFIX = "href=\"(.*?)\"";
    public static final String FILE_NAME = "[/|?]?[a-z|A-Z|0-9|=]+.[a-zA-Z0-9]+?$";
    /**
     * Точка входа для сканнера.  Программа должна получить ссылку в виде строки и глубину поиска.
     * Ссылки подразделяются ан просмотренные, необработанные и обработанные, у каждой есть глубина.
     * Выводит все обработанные ссылки и соответствующие глубины. Проходится по каждой ссылке в
     * pendingURLs и после выполнения метода getAllLinks добавляет ссылку вprocessedURLs и seenURLs.
     */
    public static void main(String[] args) {

        // Переменная для хранения глубины текущей ссылки
        int depth = 0;

        // проверка было ли  введено два аргумента
        if (args.length != 2) {
            System.out.println("usage: java Crawler <URL> <depth>");
            System.exit(1);
        }
        else {
            try {
                // пытаемся привести второй переданный аргумент к целому числу
                depth = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException nfe) {
                // если невозможно привести в int
                // выводим ошибку
                System.out.println("usage: java Crawler <URL> <depth>");
                System.exit(1);
            }
        }

        // связанный список для необработаных URL
        LinkedList<URLDepth> pendingURLs = new LinkedList<URLDepth>();

        // связанный список для обработанных URL
        LinkedList<URLDepth> processedURLs = new LinkedList<URLDepth>();

        // На основе ссылки, которая была передана пользователем создается объект
        // с глубиной ноль
        URLDepth currentDepthPair = new URLDepth(args[0], 0);
        //URLDepth currentDepthPair = new URLDepth("http://info.cern.ch/", 0);

        // добавляем сайт, на который указывает ссылка, в список необработанных ссылок
        pendingURLs.add(currentDepthPair);

        // список просмотренных ссылок. добавляем туда введенную пользователем ссылку
        ArrayList<String> seenURLs = new ArrayList<String>();
        seenURLs.add(currentDepthPair.getURL());

        //пока список необработанных сайтов не пуст, пробегаемся по ссылкам на активной странице и добавляем
        // ссылки с каждой
        while (pendingURLs.size() != 0) {

            // взять первую ссылку из необработанных ссылок, добавить ее и ее глубину как объект
            // depthPair и переместить ее в обработанные
            // зафиксировать ее глубину в переменной myDepth
            URLDepth depthPair = pendingURLs.pop();
            processedURLs.add(depthPair);
            int myDepth = depthPair.getDepth();

            // получить все сссылки, размещенные на текущей странице и положить их в связанный список linksList
            LinkedList<String> linksList = new LinkedList<String>();
            linksList = Crawler.getAllLinks(depthPair);

            // если глубина не не достигла требуемой, то добавляем ссылки из списка в pendingURLs и seenURLs
            if (myDepth < depth) {
                for (int i = 0; i < linksList.size(); i++) {
                    String newURL = linksList.get(i);
                    // если ссылка уже присутстсвует, пропускаем ее
                    if (seenURLs.contains(newURL)) {
                        continue;
                    }
                    // если ссылка новая, тоо она добавляется в список просмотренных и необработанных (как пара ссылка - глубина)
                    else {
                        URLDepth newDepthPair = new URLDepth(newURL, myDepth + 1);
                        pendingURLs.add(newDepthPair);
                        seenURLs.add(newURL);
                    }
                }
            }
        }
        // выводим в консоль все обработанные ссылки и их уровни глубины.
        Iterator<URLDepth> iter = processedURLs.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }
        System.out.println(processedURLs.size());
    }
    /**
     * Метод получает пару URLDepth и возвращает связанный список строк со ссылками.
     * Соединяется с веб-сайтом, расположенном по адресу в переданном объекте, находит все ссылки
     * на странице и добавляет их в результирующий список.
     */
    private static LinkedList<String> getAllLinks(URLDepth myDepthPair) {
        //инициализируем список, который будет содержать найденные ссылки
        LinkedList<String> URLs = new LinkedList<String>();

        URL currentURL;
        try {
            currentURL= new URL(myDepthPair.getURL());
        }
        catch (MalformedURLException e) {
            System.err.println("MalformedURLException: " + e.getMessage());
            return URLs;
        }
        HttpURLConnection con;
        try {
            con = (HttpURLConnection) currentURL.openConnection();
            con.setRequestProperty ( "User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36" );
            con.setRequestMethod("GET");
            con.setReadTimeout(1000);
        }
        catch (IOException ex) {
            System.err.println("IOException: " + ex.getMessage());
            return URLs;
        }
        //Строки для хранения пути и веб-хоста страницы из объекта URLDepth
        String path = myDepthPair.getPath();
        String webHost = myDepthPair.getWebHost();

        // Объявление входящего потока
        InputStream inStream;

        // пытаемся получить getInputStream по установленному сокету.
        try {
            inStream = con.getInputStream();
        }
        // Ловим IOException и возвращаем пустой список.
        catch (IOException excep){
            System.err.println("IOException: " + excep.getMessage());
            return URLs;
        }
        // создаем объект чтения  InputStreamReader и BufferedReader чтобы считывать строчки
        InputStreamReader inStreamReader = new InputStreamReader(inStream);
        BufferedReader BuffReader = new BufferedReader(inStreamReader);
        int emptyStrings = 0;
        // пытаемся прочитать строчку из BufferedReader
        while (emptyStrings < 2) {
            String line;
            try {
                line = BuffReader.readLine();
            }
            // Ловим IOException и возвращаем пустой список
            catch (IOException except) {
                System.err.println("IOException: " + except.getMessage());
                return URLs;
            }

            if (line == null) {
                emptyStrings += 1;
                continue;
            } else emptyStrings = 0;
            //Scanner sc = new Scanner (inStream);

            //while ( sc.findWithinHorizon(URL_PREFIX, 0)!= null){


            //Если ссылка в строке не найдена - следующая итерация (следующая строка)
            Pattern pattern = Pattern.compile(URL_PREFIX, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()){
                String newLink = matcher.group(1); //так как в URL_PREFIX сгруппировано
                if (newLink.startsWith("/") || !newLink.startsWith("http")){
                    newLink = webHost + path.replaceAll(FILE_NAME, "/") + newLink;
                    newLink = newLink.replaceAll("//", "/");
                    newLink = "http://" + newLink;
                }
                URLs.add(newLink);
            }
        }
        //sc.close();
        // возвращаем связанный список найденных ссылок
        return URLs;
    }
}