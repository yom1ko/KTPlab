package com.company;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

public class URLPool {
    private LinkedList<URLDepth> pending;
    private Set<URLDepth> seenUrls;
    private int maxDepth;
    private int waiting;

    public URLPool(int maxDepth) {
        pending = new LinkedList<>();
        seenUrls = new LinkedHashSet<>();
        this.maxDepth = maxDepth;
        waiting = 0;
    }

    /**
     * Этот метод достает первую пару ссылка-глубина из связанного списка с необработанными ссылками. Если список пуст
     */
    public synchronized  URLDepth get() throws InterruptedException {
        while (pending.size() == 0) {
            waiting++;
            wait();
            waiting--;
        }
        return pending.removeFirst();
    }

    /**
     * Этот метод добавляет пару Ссылка-глубина в конец списка необработанных ссылок и
     * извещает остановившиеся потоки, что можно продолжать свою работу (в методе get)
     */
    public synchronized void put(URLDepth url) {
        if (url.getDepth() < maxDepth) {
            pending.addLast(url);
        }
        seenUrls.add(url);
        notify();
    }

    /**
     * Этот проверяет была ли пара ссылка-глубина уже просмотрена
     */
    public synchronized boolean seen(URLDepth url) {
        return seenUrls.contains(url);
    }

    public synchronized Set<URLDepth> seen() {
        return seenUrls;
    }

    public synchronized int getMaxDepth() {
        return maxDepth;
    }

    public synchronized int getWaitingThreads() {
        return waiting;
    }
}