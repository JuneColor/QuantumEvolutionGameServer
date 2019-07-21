package evolution.components.queue.impl;

import evolution.components.queue.MessageQueue;

import java.util.LinkedList;

public class LinkedListQueue implements MessageQueue {
    private volatile LinkedList<Object> queue;

    public LinkedListQueue() {
        this.queue = new LinkedList<>();
    }

    @Override
    public boolean pushTail(Object item) {
        if (null == item) {
            return false;
        }

        this.queue.addLast(item);
        return true;
    }

    @Override
    public boolean pushFront(Object item) {
        if (null == item) {
            return false;
        }

        this.queue.addFirst(item);
        return true;
    }

    @Override
    public Object popTail() {
        if (this.queue.isEmpty()) {
            return null;
        }

        return this.queue.removeLast();
    }

    @Override
    public Object popHead() {
        if (this.queue.isEmpty()) {
            return null;
        }

        return this.queue.removeFirst();
    }

    @Override
    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    @Override
    public int size() {
        return this.queue.size();
    }

    @Override
    public void clear() {
        this.queue.clear();
    }
}
