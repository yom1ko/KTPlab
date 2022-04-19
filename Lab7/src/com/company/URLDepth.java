package com.company;
import java.net.*;
/**
 * Класс для работы со ссылками
 */
public class URLDepth {

    private String URL;
    private int depth;

    public URLDepth (String url, int num) {
        URL = url;
        depth = num;
    }

    /**
     * Метод возвращает унифицированную метку ресурса для данного объекта
     */
    public String getURL() {
        return URL;
    }
    /**
     * Метод возвращает глубину поиска для данного объекта
     */
    public int getDepth (){
        return depth;
    }

    /**
     * Метод, возвращающий путь для данной ссылки, либо возвращающий пустую строку, если такого пути нет
     */
    public String getPath() {
        try {
            URL url = new URL(URL);
            return url.getPath();
        }
        catch (MalformedURLException e) {
            System.err.println("MalformedURLException: " + e.getMessage());
            return null;
        }
    }

    /*
     * Метод возвращает глубину поиска и URL в виде строки
     */
    public String toString() {
        String stringDepth = Integer.toString(depth);
        return stringDepth + '\t' + URL;
    }

    /*
     * Метод возвращающий хост в виде строки
     */
    public String getWebHost() {
        try {
            URL url = new URL(URL);
            return url.getHost();
        }
        catch (MalformedURLException e) {
            System.err.println("MalformedURLException: " + e.getMessage());
            return null;
        }
    }
}