package com.phouthasak.controlHQ.domain.kasa;

import org.springframework.stereotype.Service;

@Service
public class KasaPayloadBuilder {
    private static final String GET_DEVICE_INFO_PAYLOAD = "{\"system\":{\"get_sysinfo\":{}}}";
    private static final String SET_REPLAY_STATE_PAYLOAD = "{\"system\":{\"set_relay_state\":{\"state\":%d}}}";

    public static String getDeviceInfoPayload() {
        return GET_DEVICE_INFO_PAYLOAD;
    }

    public static String getReplayStatePayload(boolean state) {
        return String.format(SET_REPLAY_STATE_PAYLOAD, (state) ? 1 : 0);
    }
}
