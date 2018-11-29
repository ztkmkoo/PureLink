package com.ztkmkoo.purelink.node;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.ztkmkoo.purelink.core.Block;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class MinerActor extends AbstractActor {

    public static Props props() {
        return Props.create(MinerActor.class, () -> new MinerActor());
    }

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final FiniteDuration miningInterval = Duration.apply(10L, TimeUnit.MILLISECONDS);

    private Cancellable miningCancellable = NothingCancellable.instance;
    private Block latestBlock = null;

    private MinerActor() {
        log.info("MinerActor init");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MinerActor.Start.class, this::handlingMinerActorStart)
                .build();
    }

    // message handling methods

    private boolean handlingMinerActorStart(final MinerActor.Start msg) {

        stopMiningScheduleIfRunning();
        startMiningSchedule(msg.latestBlock);

        return true;
    }

    // inner function for message handling methods

    private void stopMiningScheduleIfRunning() {

        log.debug("stopMiningScheduleIfRunning");

        if (!(miningCancellable instanceof NothingCancellable)) {
            if (!miningCancellable.isCancelled()) {

                log.info("Try to stop mining schedule.");
                if(!miningCancellable.cancel()) {
                    // TODO - error handling. throw exception?
                    log.error("There is an error when stop mining schedule.");
                }
            }

            miningCancellable = NothingCancellable.instance;
        }
    }

    private void startMiningSchedule(final Block latestBlock) {

        log.debug("startMiningSchedule");

        this.latestBlock = latestBlock.deepCopy();
        final ActorRef sender = getSender();

        miningCancellable = getContext().getSystem().scheduler().schedule(
                miningInterval,
                miningInterval,
                () -> mining(sender),
                getContext().dispatcher()
        );
    }

    private void mining(final ActorRef sender) {
        if (latestBlock == null) {
            log.warning("Cannot start mining when latest block is null");
            return;
        }

        log.info("MinerActor mining start.");
        final Block newBlock = new Block(latestBlock.blockHeader);
        log.info("MinerActor mining done. {} -> {}", latestBlock.blockHash.toString(), newBlock.blockHash.toString());

        sender.tell(Result.of(newBlock), getSelf());

        stopMiningScheduleIfRunning();
    }

    // Serializable message class for actor

    public static class Start implements Serializable {
        public final Block latestBlock;
        public Start(final Block latestBlock) { this.latestBlock = latestBlock; }
    }

    public static class Result implements Serializable {
        public static Result of(final Block result) { return new Result(result); }

        public final Block result;
        private Result(final Block result) { this.result = result; }
    }
}
