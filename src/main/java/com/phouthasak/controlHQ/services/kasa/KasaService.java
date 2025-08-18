package com.phouthasak.controlHQ.services.kasa;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class KasaService {
    private static final int KASA_PORT = 9999;
    private static final int TIMEOUT_MS = 5000;

    private static byte[] kasaEncrypt(String plainText) {
        byte[] key = new byte[]{(byte) 0xAB};
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

    private static String kasaDecrypt(byte[] encrypted) {
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
