package com.phouthasak.controlHQ.util;

import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;

@NoArgsConstructor
public final class KasaCipher {
    private static final byte INITIAL_KEY = (byte) 0xAB;

    public static byte[] encrypt(String plainText) {
        byte[] key = new byte[]{INITIAL_KEY};
        byte[] plainBytes = plainText.getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = new byte[plainBytes.length + 4];

        int length = plainBytes.length;
        encrypted[0] = (byte) ((length >> 24) & 0xFF);
        encrypted[1] = (byte) ((length >> 16) & 0xFF);
        encrypted[2] = (byte) ((length >> 8) & 0xFF);
        encrypted[3] = (byte) (length & 0xFF);

        byte currentKey = key[0];
        for (int i = 0; i < plainBytes.length; i++) {
            encrypted[i + 4] = (byte) (plainBytes[i] ^ currentKey);
            currentKey = encrypted[i + 4];
        }

        return encrypted;
    }

    public static String decrypt(byte[] encrypted) {
        if (encrypted.length < 4) {
            return "";
        }

        byte[] payload = new byte[encrypted.length - 4];
        System.arraycopy(encrypted, 4, payload, 0, payload.length);

        byte key = (byte) 0xAB;
        byte[] decrypted = new byte[payload.length];

        for (int i = 0; i < payload.length; i++) {
            decrypted[i] = (byte) (payload[i] ^ key);
            key = payload[i];
        }

        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
