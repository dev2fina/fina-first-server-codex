package net.fina.server.dcs.jms;

import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import jakarta.jms.Message;

public interface DcsJmsClientMessageCallback {

    Message create(JMSContext context) throws JMSException;
}
