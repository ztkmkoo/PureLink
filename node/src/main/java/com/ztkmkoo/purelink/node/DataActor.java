package com.ztkmkoo.purelink.node;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.ztkmkoo.purelink.core.Block;
import com.ztkmkoo.purelink.core.database.AbstractDatabase;
import com.ztkmkoo.purelink.core.database.EmptyDatabase;
import com.ztkmkoo.purelink.core.database.TempMemoryDatabase;
import lombok.AllArgsConstructor;

import java.io.Serializable;

public class DataActor extends AbstractActor {

    public static Props props() {
        return Props.create(DataActor.class, () -> new DataActor());
    }

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final String id = self().path().toSerializationFormat();

    private AbstractDatabase<Integer, Block> blockChainDatabase = AbstractDatabase.createDatabaseStaticInstance(EmptyDatabase.class);

    private DataActor() {

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(DataActor.Init.class, this::handlingDataActorInit)
                .match(DataActor.WriteReq.class, this::handlingDataActorWriteReq)
                .match(DataActor.ReadReq.class, this::handlingDataActorReadReq)
                .matchAny(o -> log.warning("Unexpected message: {}", o.toString()))
                .build();
    }

    // message handling methods
    private boolean handlingDataActorInit(final DataActor.Init msg) {

        log.debug("[{}]handlingDataActorInit.", id);

        blockChainDatabase = AbstractDatabase.createDatabaseStaticInstance(TempMemoryDatabase.class);
        if (!blockChainDatabase.initOnLoad()) {
            // TODO - error handling. must stop the main p2p(cluster) node. Maybe send fatal exception message back to main node.
            log.error("Cannot init database. It is a fatal exception.");
            return false;
        }

        return true;
    }

    private boolean handlingDataActorWriteReq(final DataActor.WriteReq msg) {

        log.debug("[{}]handlingDataActorWriteReq:{}", id, msg.toString());

        final Block result = blockChainDatabase.save(msg.key, msg.value);
        final WriteAns writeAns = new WriteAns(!(result == null));
        sender().tell(writeAns, self());

        return true;
    }

    private boolean handlingDataActorReadReq(final DataActor.ReadReq msg) {

        log.debug("[{}]handlingDataActorReadReq:{}", id, msg.toString());

        final Block result = blockChainDatabase.getByKey(msg.key);
        final int key = (result == null) ? -1 : msg.key;

        final ReadAns readAns = new ReadAns(key, result);
        sender().tell(readAns, self());

        return true;
    }

    // Serializable message class for actor
    public static class Init implements Serializable {
        public static final Init instance = new Init();
    }

    @AllArgsConstructor
    public static class WriteReq implements Serializable {
        public final int key;
        public final Block value;
    }

    @AllArgsConstructor
    public static class WriteAns implements Serializable {
        public final boolean result;
    }

    @AllArgsConstructor
    public static class ReadReq implements Serializable {
        public final int key;
    }

    @AllArgsConstructor
    public static class ReadAns implements Serializable {
        public final int key;
        public final Block value;
    }
}
