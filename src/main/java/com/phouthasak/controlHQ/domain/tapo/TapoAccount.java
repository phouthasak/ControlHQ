package com.phouthasak.controlHQ.domain.tapo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TapoAccount {
    private String ip;
    private String accountName;
    private String accountPwd;
}
