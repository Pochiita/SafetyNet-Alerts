package com.safetynet.alerts.dto;

import java.util.List;

public class CommunityMailDTO {

    private final List<String> mails;

    public CommunityMailDTO(List<String> mails) {
        this.mails = mails;
    }

    public List<String> getMails() {
        return mails;
    }
}
