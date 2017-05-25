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
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * @创建者: feng
 * @时间: 13:58
 * @描述： 邮件工具类
 */
public class EmailUtil {
    private static final String myEmailAccount     = "13434159434@163.com";
    private static final String myEmailPassword    = "evil123";
    private static final String myEmailSMTPHost    = "smtp.163.com";
    private static final String receiveMailAccount = "fengxiaocan@foxmail.com";

    /**
     * 发送邮件
     *
     * @param appName   app名字
     * @param mailContent 内容
     *
     * @throws Exception
     */
    public static void sendMail2Me(String appName, String mailContent)
    {
        try {
            Properties props = new Properties(); //可以加载一个配置文件
            // 使用smtp：简单邮件传输协议
            props.put("com.evil.mail.smtp.host", myEmailSMTPHost);//存储发送邮件服务器的信息
            props.put("com.evil.mail.smtp.auth", "true");//同时通过验证
            props.put("com.evil.mail.smtp.ssl.enable", "true");//（注意）163邮箱只能用ssl！！！

            Session session = Session.getInstance(props);//根据属性新建一个邮件会话
            session.setDebug(true); //有他会打印一些调试信息。

            MimeMessage message = new MimeMessage(session);//由邮件会话新建一个消息对象
            message.setFrom(new InternetAddress(myEmailAccount));//设置发件人的地址
            message.setRecipient(Message.RecipientType.TO,
                                 new InternetAddress(receiveMailAccount));//设置收件人,并设置其接收类型为TO
            message.setSubject(appName + "出现BUG");//设置标题
            //设置信件内容
            //        message.setText(mailContent); //发送 纯文本 邮件
            message.setContent(mailContent, "text/html;charset=UTF-8"); //发送HTML邮件，内容样式比较丰富
            message.setSentDate(new Date());//设置发信时间
            message.saveChanges();//存储邮件信息

            //发送邮件
            //        Transport transport = session.getTransport("smtp");
            Transport transport = session.getTransport();
            transport.connect(myEmailAccount, myEmailPassword);
            transport.sendMessage(message, message.getAllRecipients());//发送邮件,其中第二个参数是所有已设好的收件人地址
            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送邮件
     *
     * @param fromMail    发送方Email地址，比如“yemengsky@163.com”
     * @param user        用户名（一般同上），比如“yemengsky@163.com”
     * @param password    密码（现在一般都用授权码，根据邮件提供商不同而不同）
     * @param toMail      接收方Email地址，比如“yemengsky@gmail.com”
     * @param smtpAddr    发送方stmp地址，比如“smtp.163.com”
     * @param mailTitle   邮件主题
     * @param mailContent 内容
     *
     * @throws Exception
     */
    public static void sendMail(String fromMail,
                                String user,
                                String password,
                                String toMail,
                                String smtpAddr,
                                String mailTitle,
                                String mailContent)
            throws Exception
    {

        Properties props = new Properties(); //可以加载一个配置文件
        // 使用smtp：简单邮件传输协议
        props.put("com.evil.mail.smtp.host", smtpAddr);//存储发送邮件服务器的信息
        props.put("com.evil.mail.smtp.auth", "true");//同时通过验证
        props.put("com.evil.mail.smtp.ssl.enable", "true");//（注意）163邮箱只能用ssl！！！

        Session session = Session.getInstance(props);//根据属性新建一个邮件会话
        session.setDebug(true); //有他会打印一些调试信息。

        MimeMessage message = new MimeMessage(session);//由邮件会话新建一个消息对象
        message.setFrom(new InternetAddress(fromMail));//设置发件人的地址
        message.setRecipient(Message.RecipientType.TO,
                             new InternetAddress(toMail));//设置收件人,并设置其接收类型为TO
        message.setSubject(mailTitle);//设置标题
        //设置信件内容
        //        message.setText(mailContent); //发送 纯文本 邮件
        message.setContent(mailContent, "text/html;charset=UTF-8"); //发送HTML邮件，内容样式比较丰富
        message.setSentDate(new Date());//设置发信时间
        message.saveChanges();//存储邮件信息

        //发送邮件
        //        Transport transport = session.getTransport("smtp");
        Transport transport = session.getTransport();
        transport.connect(user, password);
        transport.sendMessage(message, message.getAllRecipients());//发送邮件,其中第二个参数是所有已设好的收件人地址
        transport.close();
    }


    /**
     * 发送邮件
     *
     * @param fromMail    发送方Email地址，比如“yemengsky@163.com”
     * @param user        用户名（一般同上），比如“yemengsky@163.com”
     * @param password    密码（现在一般都用授权码，根据邮件提供商不同而不同）
     * @param toMail      接收方Email地址，比如“yemengsky@gmail.com”
     * @param smtpAddr    发送方stmp地址，比如“smtp.163.com”
     * @param mailTitle   邮件主题
     * @param mailContent 内容
     * @param file        附件
     *
     * @throws Exception
     */
    public static void sendMail(String fromMail,
                                String user,
                                String password,
                                String toMail,
                                String smtpAddr,
                                String mailTitle,
                                String mailContent,
                                File file)
            throws Exception
    {
        Properties props = new Properties(); //可以加载一个配置文件
        // 使用smtp：简单邮件传输协议
        props.put("com.evil.mail.smtp.host", smtpAddr);//存储发送邮件服务器的信息
        props.put("com.evil.mail.smtp.auth", "true");//同时通过验证
        props.put("com.evil.mail.smtp.ssl.enable", "true");//（注意）163邮箱只能用ssl！！！

        Session session = Session.getInstance(props);//根据属性新建一个邮件会话
        session.setDebug(true); //有他会打印一些调试信息。

        MimeMessage message = new MimeMessage(session);//由邮件会话新建一个消息对象
        message.setFrom(new InternetAddress(fromMail));//设置发件人的地址
        message.setRecipient(Message.RecipientType.TO,
                             new InternetAddress(toMail));//设置收件人,并设置其接收类型为TO
        message.setSubject(mailTitle);//设置标题


        // 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
        Multipart multipart = new MimeMultipart();


        //   设置邮件的文本内容
        BodyPart contentPart = new MimeBodyPart();
        contentPart.setText(mailContent);
        multipart.addBodyPart(contentPart);
        //添加附件
        BodyPart   messageBodyPart = new MimeBodyPart();
        DataSource source          = new FileDataSource(file);
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


        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-Java-content-handler=com.sun.com.evil.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.com.evil.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.com.evil.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.com.evil.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.com.evil.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);

        //发送邮件
        Transport transport = session.getTransport();
        transport.connect(user, password);
        transport.sendMessage(message, message.getAllRecipients());//发送邮件,其中第二个参数是所有已设好的收件人地址
        transport.close();
    }

    protected abstract static class TRunnable<T>
            implements Runnable
    {
        protected T t;

        public TRunnable() {
        }

        public TRunnable(T t) {
            this.t = t;
        }

        public T getT() {
            return t;
        }

        public void setT(T t) {
            this.t = t;
        }
    }
}