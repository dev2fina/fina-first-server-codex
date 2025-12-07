package net.fina.common.shared.comunicator;

import java.io.Serializable;

public class CommunicatorDashletModel implements Serializable {
    private String key;
    private Integer value;
    private MessagesStatistics messagesStatistics;

    public CommunicatorDashletModel() {
    }

    public CommunicatorDashletModel(String key, Integer value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public MessagesStatistics getMessagesStatistics() {
        return messagesStatistics;
    }

    public void setMessagesStatistics(MessagesStatistics messagesStatistics) {
        this.messagesStatistics = messagesStatistics;
    }

    public enum MessagesStatistics {
        INITIATIVE_MESSAGES_COUNT("net.fina.communicator.statistics.initiativeMessagesCount"),
        THREAD_COUNT("net.fina.communicator.statistics.threadCount"),
        SENTBOX_COUNT("net.fina.communicator.statistics.sentBoxCount"),
        INBOX_COUNT("net.fina.communicator.statistics.inboxCount");

        MessagesStatistics(String code) {
            this.code = code;
        }

        private String code;

        public String getCode() {
            return code;
        }

    }

}
