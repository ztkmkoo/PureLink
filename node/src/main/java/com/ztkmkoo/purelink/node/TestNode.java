package com.ztkmkoo.purelink.node;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;

public class TestNode {

    public static void main(String[] args) {

        new Thread(() -> startNewActorSystem(2551)).start();
        new Thread(() -> startNewActorSystem(2552)).start();

        while (true) {
            try {
                Thread.sleep(2 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void startNewActorSystem(final int port) {

        Config config = ConfigFactory.load();

        final String portConfigPath = "akka.remote.netty.tcp.port";

        Config portConfig = config.withValue(portConfigPath, ConfigValueFactory.fromAnyRef(port));

        Config combinedConfig = portConfig.withFallback(config);
        Config completeConfig = ConfigFactory.load(combinedConfig);

        int configPort = completeConfig.getInt(portConfigPath);

//        ActorSystem actorSystem = ActorSystem.create("system" + port, completeConfig);
        ActorSystem actorSystem = ActorSystem.create("ClusterSystem", completeConfig);

        ActorRef actorRef = actorSystem.actorOf(Props.create(TestClusterActor.class));
    }
}
