package com.tapereader.wire;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class DefaultReceiver implements Receiver, MessageListener {

    private final String BROKER_URL;
    private final String TOPIC_NAME;
    private Connection connection;
    private MessageConsumer updateConsumer;
    private MessageProtocol messageProtocol;
    private EventBus eventBus;

    @Inject
    public DefaultReceiver(@Named("mqbrokerurl") String brokerURL, @Named("mqtopic") String topicName, 
            EventBus eventBus, MessageProtocol messageProtocol) {
        BROKER_URL = brokerURL;
        TOPIC_NAME = topicName;
        this.eventBus = eventBus;
        this.messageProtocol = messageProtocol;
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMsg = (TextMessage) message;
                String newState = textMsg.getText();
                update(newState);
                System.out.println("Received!" + newState);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void update(String newState) throws JMSException {
        try {
            Object event = this.messageProtocol.handleProtocolMessage(newState);
            eventBus.post(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receive(Object object) {
        eventBus.register(object);
    }

    @Override
    public void init() {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination updateTopic = session.createTopic(TOPIC_NAME);
            updateConsumer = session.createConsumer(updateTopic);
            updateConsumer.setMessageListener(this);
            connection.start();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void terminate() {
        try {
            if (connection != null) {
                connection.stop();
                connection.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
