package net.hmcts.springjmsdemo.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.jms.Session;


@Service
@Lazy
public class QueuePublisher {

    private final Logger logger = LoggerFactory.getLogger(QueuePublisher.class);

    @Value("${amqp.queue}")
    private String destination;

    private final JmsTemplate jmsTemplate;

    @Autowired
    public QueuePublisher(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @PostConstruct
    public void afterConstruct() {
        sendPing();
    }

    @Retryable(
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 3)
    )
    public void sendPing() {
        logger.info("Sending ping to queue");
        jmsTemplate.send(destination, (Session session) -> session.createTextMessage("ping"));
    }
}
