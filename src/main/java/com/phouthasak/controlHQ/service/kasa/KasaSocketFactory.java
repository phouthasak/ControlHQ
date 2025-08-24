package com.phouthasak.controlHQ.service.kasa;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.Socket;

@Component
public class KasaSocketFactory implements SocketFactory {
    @Override
    public Socket createSocket() throws IOException {
        return new Socket();
    }
}
