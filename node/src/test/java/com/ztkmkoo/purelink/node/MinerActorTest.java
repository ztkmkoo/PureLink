package com.ztkmkoo.purelink.node;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.PatternsCS;
import akka.testkit.TestKit;
import com.ztkmkoo.purelink.core.Block;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import scala.concurrent.duration.Duration;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class MinerActorTest{

    private static ActorSystem actorSystem;

    @BeforeClass
    public static void setup() {
        MinerActorTest.actorSystem = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(actorSystem, Duration.apply(1, TimeUnit.SECONDS), true);
        actorSystem = null;
    }

    @Test
    public void handlingMinerActorStartTest() {
        new TestKit(actorSystem){{
            final Props props = MinerActor.props();
            final ActorRef minerActor = actorSystem.actorOf(props);

            MinerActor.Start msg = new MinerActor.Start(Block.genesisBlock());
            askTest(msg, minerActor, 0);

            LoggingAdapter log = Logging.getLogger(actorSystem, this);
            log.info("handlingMinerActorStartTest done.");
        }};
    }

    private void askTest(final Object msg, final ActorRef target, final int count) {
        if (count > 100)
            return;

        try {
            Object o = PatternsCS.ask(target, msg, 10000).toCompletableFuture().get();

            if (o instanceof MinerActor.Result) {
                MinerActor.Result result = MinerActor.Result.class.cast(o);
                MinerActor.Start newMsg = new MinerActor.Start(result.result);
                askTest(newMsg, target, count + 1);
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}