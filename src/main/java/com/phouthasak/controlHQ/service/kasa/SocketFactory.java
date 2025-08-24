package com.phouthasak.controlHQ.service.kasa;

import java.io.IOException;
import java.net.Socket;

public interface SocketFactory {
    Socket createSocket() throws IOException;
}
