package org.lirazs.chatty.util;

public interface FetchCallback<T> {

    void complete(T result);
}
