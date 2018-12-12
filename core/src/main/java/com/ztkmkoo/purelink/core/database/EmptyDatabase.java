package com.ztkmkoo.purelink.core.database;

public class EmptyDatabase extends AbstractDatabase<Integer, Object> {

    EmptyDatabase() {}

    @Override
    public boolean initOnLoad() {
        return false;
    }

    @Override
    public Object save(Integer key, Object value) {
        return null;
    }

    @Override
    public Object getByKey(Integer key) {
        return null;
    }

    @Override
    public Object deleteByKey(Integer key) {
        return null;
    }
}
