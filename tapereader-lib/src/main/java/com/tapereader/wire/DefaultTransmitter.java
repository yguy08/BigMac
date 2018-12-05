package com.tapereader.wire;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class DefaultTransmitter implements Transmitter {
    
    protected Connection connection;
    protected Session session;
    protected MessageProducer updateProducer;
    
    @Inject(optional = true)
    @Named("mqbrokerurl")
    private String BROKER_URL;
    
    @Inject(optional = true)
    @Named("mqtopic")
    private String TOPIC_NAME;

    @Override
    public void transmit(Object event) {
        try {
            TextMessage message = session.createTextMessage(event.toString());
            updateProducer.send(message);
            System.out.println("SENT!" + event.toString());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void init() {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination topic = session.createTopic(TOPIC_NAME);
            updateProducer = session.createProducer(topic);
            connection.start();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void terminate() {
        if (connection != null) {
            try {
                connection.stop();
                connection.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
