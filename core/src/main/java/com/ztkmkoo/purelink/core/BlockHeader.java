package com.ztkmkoo.purelink.core;

public class BlockHeader {

    public final int version = VersionConst.VERSION_BLOCK_HEADER_ADORA;
    public final byte[] previousBlockHash;

    private final String toString;

    public BlockHeader(
            final byte[] previousBlockHash
    ) {
        this.previousBlockHash = previousBlockHash;

        this.toString = version + new String(previousBlockHash);
    }

    @Override
    public String toString() {
        return toString;
    }
}
