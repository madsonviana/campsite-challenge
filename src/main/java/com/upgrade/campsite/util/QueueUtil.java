package com.upgrade.campsite.util;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.Serializable;

public class QueueUtil {
    public static final String BOOKING_QUEUE = "bookQueue";

    public static ObjectMessage createObjectMessage(Session session, Serializable object) throws JMSException {
        ObjectMessage message = session.createObjectMessage();
        message.setObject(object);
        return message;
    }

}
