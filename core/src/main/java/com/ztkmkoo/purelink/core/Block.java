package com.ztkmkoo.purelink.core;


import java.security.MessageDigest;

public class Block {

    private static final String DEFAULT_CHARSET_NAME = "UTF-8";

    public static final Block genesisBlock() {
        final byte[] genesisBlockPreviousHeaderByte = new byte[16];
        final BlockHeader blockHeader = new BlockHeader(genesisBlockPreviousHeaderByte);

        return new Block(blockHeader);
    }

    public final BlockHeader blockHeader;
    public final byte[] blockHash;

    public Block(final BlockHeader blockHeader) {
        this.blockHeader = blockHeader;
        this.blockHash = getBlockHash(blockHeader);
    }

    private byte[] getBlockHash(final BlockHeader blockHeader) {
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            final byte[] headerBytes = messageDigest.digest(blockHeader.toString().getBytes(DEFAULT_CHARSET_NAME));

            if (headerBytes == null || headerBytes.length <= 0)
                 return null;

            final StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < headerBytes.length; i++) {
                final String hex = Integer.toHexString(headerBytes[i]);
                if (hex.length() == 1)
                    stringBuffer.append('0');
                stringBuffer.append(hex);
            }

             return stringBuffer.toString().getBytes(DEFAULT_CHARSET_NAME);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
