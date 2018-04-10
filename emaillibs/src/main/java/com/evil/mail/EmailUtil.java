package com.evil.mail;


import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * The type Email util.
 *
 * @创建者: feng
 * @时间: 13 :58
 * @描述： 邮件工具类
 */
public class EmailUtil {
    private static final String myEmailAccount = "13434159434@163.com";
    private static final String myEmailPassword = "Evil123456";
    private static final String myEmailSMTPHost = "smtp.163.com";

    /**
     * 创建配置文件
     *
     * @param emailSMTPHost
     * @return
     */
    private static Properties createProperties(String emailSMTPHost) {
        Properties props = new Properties(); //可以加载一个配置文件
        props.put("mail.smtp.ssl.enable","true");//（注意）163邮箱只能用ssl！！！
        props.setProperty("mail.transport.protocol","smtp");   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host",emailSMTPHost);   // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth","true");            // 需要请求认证
        return props;
    }

    /**
     * 创建邮件会话
     *
     * @param emailSMTPHost
     * @return
     */
    private static Session createSession(String emailSMTPHost) {
        return Session.getInstance(createProperties(emailSMTPHost));//根据属性新建一个邮件会话
    }

    /**
     * 创建邮件消息对象
     *
     * @param fromMail
     * @param toMail
     * @param mailTitle
     * @param mailContent
     * @return
     * @throws Exception
     */
    private static MimeMessage createMessage(
            Session session,String fromMail,String toMail,String mailTitle,String mailContent
    ) throws Exception
    {
        MimeMessage message = new MimeMessage(session);//由邮件会话新建一个消息对象
        message.setFrom(new InternetAddress(fromMail));//设置发件人的地址
        message.setRecipient(Message.RecipientType.TO,
                             new InternetAddress(toMail)
        );//设置收件人,并设置其接收类型为TO
        message.setSubject(mailTitle);//设置标题
        //设置信件内容
        //        message.setText(mailContent); //发送 纯文本 邮件
        message.setContent(mailContent,"text/html;charset=UTF-8"); //发送HTML邮件，内容样式比较丰富
        message.setSentDate(new Date());//设置发信时间
        message.saveChanges();//存储邮件信息
        return message;
    }

    /**
     * 创建需要附件的消息对象
     *
     * @param session
     * @param fromMail
     * @param toMail
     * @param mailTitle
     * @param mailContent
     * @param file
     * @return
     * @throws Exception
     */
    private static MimeMessage createMessage(
            Session session,
            String fromMail,
            String toMail,
            String mailTitle,
            String mailContent,
            File file
    ) throws Exception
    {
        MimeMessage message = new MimeMessage(session);//由邮件会话新建一个消息对象
        message.setFrom(new InternetAddress(fromMail));//设置发件人的地址
        message.setRecipient(Message.RecipientType.TO,
                             new InternetAddress(toMail)
        );//设置收件人,并设置其接收类型为TO
        message.setSubject(mailTitle);//设置标题

        // 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
        Multipart multipart = new MimeMultipart();
        //   设置邮件的文本内容
        BodyPart contentPart = new MimeBodyPart();
        contentPart.setText(mailContent);
        multipart.addBodyPart(contentPart);
        //添加附件
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(file);
        //添加附件的内容
        messageBodyPart.setDataHandler(new DataHandler(source));
        //添加附件的标题
        //这里很重要，通过下面的Base64编码的转换可以保证你的中文附件标题名在发送时不会变成乱码
        messageBodyPart.setFileName(file.getName());
        multipart.addBodyPart(messageBodyPart);
        //将multipart对象放到message中
        message.setContent(multipart);
        //保存邮件
        message.saveChanges();
        return message;
    }

    /**
     * 关闭邮件会话
     *
     * @param transport
     */
    private static void closeTransport(Transport transport) {
        try {
            if (transport != null) {
                transport.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送邮件
     *
     * @param title 标题
     * @param mailContent 内容
     * @param receiveMailAccount 收件人邮箱
     * @throws Exception the exception
     */
    public static void sendMail2Other(String title,String mailContent,String receiveMailAccount)
            throws Exception
    {
        sendMail(myEmailAccount,
                 myEmailAccount,
                 myEmailPassword,
                 receiveMailAccount,
                 myEmailSMTPHost,
                 title,
                 mailContent
        );
    }

    /**
     * 发送邮件
     *
     * @param title 标题
     * @param mailContent 内容
     * @param receiveMailAccount 收件人邮箱
     * @param file 附件
     * @throws Exception the exception
     */
    public static void sendMail2Other(
            String title,
            String mailContent,
            String receiveMailAccount,
            File file
    ) throws Exception
    {
        sendMail(myEmailAccount,
                 myEmailAccount,
                 myEmailPassword,
                 receiveMailAccount,
                 myEmailSMTPHost,
                 title,
                 mailContent,
                 file
        );
    }

    /**
     * 发送邮件
     *
     * @param fromMail 发送方Email地址，比如“yemengsky@163.com”
     * @param user 用户名（一般同上），比如“yemengsky@163.com”
     * @param password 密码（现在一般都用授权码，根据邮件提供商不同而不同）
     * @param toMail 接收方Email地址，比如“yemengsky@gmail.com”
     * @param smtpAddr 发送方stmp地址，比如“smtp.163.com”
     * @param mailTitle 邮件主题
     * @param mailContent 内容
     * @throws Exception the exception
     */
    public static void sendMail(
            String fromMail,
            String user,
            String password,
            String toMail,
            String smtpAddr,
            String mailTitle,
            String mailContent
    ) throws Exception
    {
        Session session = createSession(smtpAddr);
        MimeMessage message = createMessage(session,fromMail,toMail,mailTitle,mailContent);
        //发送邮件
        //        Transport transport = session.getTransport("smtp");
        Transport transport = session.getTransport();
        transport.addTransportListener(new MyTransportAdapter(transport));
        transport.connect(user,password);
        transport.sendMessage(message,message.getAllRecipients());//发送邮件,其中第二个参数是所有已设好的收件人地址
        transport.close();
    }

    /**
     * 发送邮件
     *
     * @param fromMail 发送方Email地址，比如“yemengsky@163.com”
     * @param user 用户名（一般同上），比如“yemengsky@163.com”
     * @param password 密码（现在一般都用授权码，根据邮件提供商不同而不同）
     * @param toMail 接收方Email地址，比如“yemengsky@gmail.com”
     * @param smtpAddr 发送方stmp地址，比如“smtp.163.com”
     * @param mailTitle 邮件主题
     * @param mailContent 内容
     * @param file 附件
     * @throws Exception the exception
     */
    public static void sendMail(
            String fromMail,
            String user,
            String password,
            String toMail,
            String smtpAddr,
            String mailTitle,
            String mailContent,
            File file
    ) throws Exception
    {
        Session session = createSession(smtpAddr);
        session.setDebug(true);
        MimeMessage message = createMessage(session,fromMail,toMail,mailTitle,mailContent,file);

        MailcapCommandMap mc = (MailcapCommandMap)CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-Java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap(
                "text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap(
                "multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap(
                "message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);

        //发送邮件
        Transport transport = session.getTransport();
        transport.addTransportListener(new MyTransportAdapter(transport));
        transport.connect(user,password);
        transport.sendMessage(message,message.getAllRecipients());//发送邮件,其中第二个参数是所有已设好的收件人地址
        transport.close();
    }

    private static class MyTransportAdapter implements TransportListener {
        Transport mTransport;

        public MyTransportAdapter(Transport transport) {
            mTransport = transport;
        }

        @Override
        public void messageDelivered(TransportEvent transportEvent) {
            //"发送成功"
            closeTransport(mTransport);
        }

        @Override
        public void messageNotDelivered(TransportEvent transportEvent) {
            //发送失败
            closeTransport(mTransport);
        }

        @Override
        public void messagePartiallyDelivered(TransportEvent transportEvent) {
            //部分发送成功
            closeTransport(mTransport);
        }
    }
}
