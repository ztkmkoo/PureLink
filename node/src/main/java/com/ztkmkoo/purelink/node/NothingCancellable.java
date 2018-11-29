package com.ztkmkoo.purelink.node;

import akka.actor.Cancellable;

public final class NothingCancellable implements Cancellable {

    public static final NothingCancellable instance = new NothingCancellable();

    private NothingCancellable() {}

    @Override
    public boolean cancel() {
        return true;
    }

    @Override
    public boolean isCancelled() {
        return true;
    }
}
