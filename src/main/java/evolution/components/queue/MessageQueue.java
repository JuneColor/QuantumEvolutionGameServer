package evolution.components.queue;

import com.google.inject.ImplementedBy;
import evolution.components.queue.impl.LinkedListQueue;

@ImplementedBy(LinkedListQueue.class)
public interface MessageQueue {
    boolean pushTail(Object item);
    boolean pushFront(Object item);
    Object popTail();
    Object popHead();
    boolean isEmpty();
    int size();
    void clear();
}
