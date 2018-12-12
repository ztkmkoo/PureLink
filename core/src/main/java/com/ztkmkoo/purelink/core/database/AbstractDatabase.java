package com.ztkmkoo.purelink.core.database;

import java.util.ArrayList;
import java.util.List;

/**
 * Expect the list is non concurrency.
 */
public abstract class AbstractDatabase<K, V> {

    protected final List<V> list = new ArrayList<>();

    abstract public boolean initOnLoad();
    abstract public V save(final K key, final V value);
    abstract public V getByKey(final K key);
    abstract public V deleteByKey(final K key);

    public static <T extends AbstractDatabase> AbstractDatabase createDatabaseStaticInstance(final Class<T> tClass) {
        if (tClass.equals(EmptyDatabase.class)) {
            return new EmptyDatabase();
        }

        return new TempMemoryDatabase();
    }
}
