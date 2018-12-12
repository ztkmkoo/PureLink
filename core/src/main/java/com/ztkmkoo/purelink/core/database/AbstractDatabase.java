package com.ztkmkoo.purelink.core.database;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDatabase<K, V> {

    protected final List<V> list = new ArrayList<>();

    abstract public boolean initOnLoad();
    abstract public V save(final K key, final V value);
    abstract public V getByKey(final K key);
    abstract public V deleteByKey(final K key);

    public static AbstractDatabase createDatabaseStaticInstance() {
        return new TempMemoryDatabase();
    }
}
