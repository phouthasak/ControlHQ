package com.phouthasak.controlHQ.service;

import com.phouthasak.controlHQ.domain.tapo.TapoAccount;
import com.phouthasak.controlHQ.util.Constants;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EnvironmentService {
    @Value("${KASA_SMART_PLUG_IPS}")
    private String KASA_SMART_PLUG_IPS;

    @Value("${TAPO_CAMERAS_IPS}")
    private String TAPO_CAMERAS_IPS;

    @Value("${TAPO_DEVICE_NAS_ACCOUNT_NAME}")
    private String TAPO_DEVICE_NAS_ACCOUNT_NAME;

    @Value("${TAPO_DEVICE_NAS_ACCOUNT_PWD}")
    private String TAPO_DEVICE_NAS_ACCOUNT_PWD;

    private Map<String, Object> envMap;

    @PostConstruct
    private void init() {
        envMap = new HashMap<>();

        envMap.put(Constants.KASA_SETTING_ENV_KEYS, parseKasaIps());
        envMap.put(Constants.TAPO_SETTING_ENV_KEYS, parseTapoAccounts());
    }

    public List<String> getKasaIps() {
        return (List<String>) envMap.get(Constants.KASA_SETTING_ENV_KEYS);
    }

    public List<TapoAccount> getTapoAccounts() {
        return (List<TapoAccount>) envMap.get(Constants.TAPO_SETTING_ENV_KEYS);
    }

    private List<String> parseKasaIps() {
        List<String> ips = Arrays.asList(KASA_SMART_PLUG_IPS.split(","));
        return ips;
    }

    private List<TapoAccount> parseTapoAccounts() {
        List<String> ips = Arrays.asList(TAPO_CAMERAS_IPS.split(","));
        List<String> accountNames = Arrays.asList(TAPO_DEVICE_NAS_ACCOUNT_NAME.split(" "));
        List<String> pwds = Arrays.asList(TAPO_DEVICE_NAS_ACCOUNT_PWD.split(" "));
        List<TapoAccount> tapoAccounts = new ArrayList<>();
        if (validateTapoInformation(ips, accountNames, pwds)) {
            int count = ips.size();
            for (int i = 0; i < count; i++) {
                tapoAccounts.add(TapoAccount.builder()
                        .ip(ips.get(i))
                        .accountName(accountNames.get(i))
                        .accountPwd(pwds.get(i))
                        .build());
            }
        }
        return tapoAccounts;
    }

    private boolean validateTapoInformation(List<String> ips, List<String> accountName, List<String> accountPwd) {
        if (CollectionUtils.isEmpty(ips)) {
            return false;
        }

        int count = ips.size();
        return count == accountName.size() && count == accountPwd.size();
    }
}
