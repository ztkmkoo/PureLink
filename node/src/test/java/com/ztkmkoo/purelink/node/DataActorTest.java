package com.ztkmkoo.purelink.node;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.testkit.TestKit;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class DataActorTest {

    private static ActorSystem actorSystem;

    @BeforeClass
    public static void setup() {
        Config config = ConfigFactory.defaultApplication();
        DataActorTest.actorSystem = ActorSystem.create("actorSystem", config);
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(actorSystem, Duration.apply(10, TimeUnit.SECONDS), true);
        actorSystem = null;
    }

    @Test
    public void handlingDataActorInitTest() {
        new TestKit(actorSystem){{
            final Props props = DataActor.props();
            final ActorRef dataActor = actorSystem.actorOf(props);

            dataActor.tell(DataActor.Init.instance, testActor());
            expectNoMessage(FiniteDuration.apply(3, TimeUnit.SECONDS));

            LoggingAdapter log = Logging.getLogger(actorSystem, this);
            log.info("handlingDataActorInitTest done.");
        }};
    }
}