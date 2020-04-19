package com.fmjava.core.listener;

import com.fmjava.core.service.CmsService;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.io.Serializable;

public class PageListener implements MessageListener {

     @Autowired
     private CmsService cmsService;

    @Override
    public void onMessage(Message message) {
        ActiveMQObjectMessage activeMQObjectMessage = (ActiveMQObjectMessage)message;

        try {
            Long[] ids= (Long[])activeMQObjectMessage.getObject();

            for (Long id : ids) {
                try {
                    cmsService.createStaticPage(id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
