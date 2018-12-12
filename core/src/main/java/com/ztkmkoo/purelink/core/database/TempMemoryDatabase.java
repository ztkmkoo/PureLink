package com.ztkmkoo.purelink.core.database;

import com.ztkmkoo.purelink.core.Block;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TempMemoryDatabase extends AbstractDatabase<Integer, Block> {

    private int listSize = 0;

    TempMemoryDatabase() {}

    @Override
    public boolean initOnLoad() {
        log.info("TempMemoryDatabase<Integer, Block> init on load.");
        listSize = list.size();
        return true;
    }

    @Override
    public Block save(Integer key, Block value) {
        if (listSize != key)
            return null;

        list.add(value);
        listSize++;
        return value;
    }

    @Override
    public Block getByKey(Integer key) {
        if (listSize <= 0 || key < 0)
            return null;

        if (listSize < key)
            return null;

        final Block result = list.get(key);
        return result;
    }

    @Override
    public Block deleteByKey(Integer key) {
        final Block result = getByKey(key);

        if (result == null)
            return null;

        list.remove(key);

        return result;
    }
}
