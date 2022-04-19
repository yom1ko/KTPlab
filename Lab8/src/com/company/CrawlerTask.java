package com.company;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

    /*private LinkedList<URLDepth> pendingURLs;
    private LinkedList<String> visited;*/

public class CrawlerTask implements Runnable {
    private static URLPool urlPool;
    private static final int WEBPORT = 80;
    private static final String REGEX = "href=\"(.*?)\"";
    public static final String FILE_NAME = "[/|?]?[a-z|A-Z|0-9|=]+.[a-zA-Z0-9]+?$";
    public static final String URL_PREFIX = "http://";
    private static List<Thread> threadList = new ArrayList<>();
    private volatile boolean running = true;

    public CrawlerTask(URLPool pool) {
        urlPool = pool;
    }

    public boolean isUrlValid(String url) {
        return url.startsWith(URL_PREFIX);
    }

    public void run() {
        // В потоке запускается бесконечный цикл, который пассивно ожидает появления необработанных ссылок
        while (running) {
            try {
                URLDepth currentPair = urlPool.get(); // wait done w/i URLpool object
                processWebPage(currentPair);
            } catch (IOException e) {
                System.out.println(e);
            } catch (InterruptedException ie) {
                running = false;
            }
        }
    }

    private void processWebPage(URLDepth webpage) throws IOException {

        Socket sock = new Socket(webpage.getWebHost(), WEBPORT);
        sock.setSoTimeout(3000);

        // Позволяет отправлять данные серверу через выходящий поток с помощью writer
        OutputStream os = sock.getOutputStream();
        PrintWriter writer = new PrintWriter(os, true);

        // посылаем GET запрос
        String path = webpage.getDocPath();
        String webHost = webpage.getWebHost();
        writer.println("GET " + path + " HTTP/1.1");
        writer.println("Host: " + webHost);
        writer.println("Connection: close");
        writer.println();

        // Создаем входящий поток и объект reader для него
        InputStream is = sock.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        int emptyLines = 0;
        while (emptyLines < 2) {
            String line = br.readLine();
            if (line == null) {
                emptyLines++;
                continue;
            } else {
                emptyLines = 0;
            }

            Pattern p = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(line);
            while (m.find()) {
                String url = m.group(1);
                if (url.startsWith("/") || !url.startsWith("http")){
                    url = webHost + path.replaceAll(FILE_NAME, "/") + url;
                    url = url.replaceAll("//", "/");
                    url = "http://" + url;
                }
                if (isUrlValid(url)) {
                    URLDepth current = new URLDepth(url, webpage.getDepth() + 1);

                    if (!urlPool.seen(current)) {
                        int depth = webpage.getDepth();
                        if (depth < urlPool.getMaxDepth()) {
                            urlPool.put(current);
                        }
                    }
                }
            }
        }
        sock.close();
    }

    public static void main(String[] args) {

        if (args.length < 3) {
            System.out.println("Usage: java Crawler <URL> <depth> <threads>");
            System.exit(1);
        }

        int maxDepth = 0;
        int numberOfThreads = 0;

        try {
            maxDepth = Integer.parseInt(args[1]);
            numberOfThreads = Integer.parseInt(args[2]);
        }
        catch (NumberFormatException nfe) {
            System.out.println("The second and third arguments must be integers.");
            System.exit(1);
        }

        URLPool urlPool = new URLPool(maxDepth);
        urlPool.put(new URLDepth(args[0], 0));
        //urlPool.put(new URLDepth("http://info.cern.ch/", 0));


        // Создаем и запускаем требуемое количество потоков
        for (int i = 0; i < numberOfThreads; i++) {
            CrawlerTask c = new CrawlerTask(urlPool);
            Thread t = new Thread(c);
            threadList.add(t);
            t.start();
        }

        while (urlPool.getWaitingThreads() != numberOfThreads) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                System.out.println("caught Interrupted Exception, ignoring...");
            }
        }


        urlPool.seen().stream().forEach(System.out::println);

        //System.exit(0);
        // Прекращает все потоки, чтобы программа завершилась
        threadList.stream().forEach(Thread::interrupt);
    }
}