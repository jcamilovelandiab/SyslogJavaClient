/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ompj4.syslog;

import com.cloudbees.syslog.*;
import com.cloudbees.syslog.sender.SyslogMessageSender;
import com.cloudbees.syslog.util.InternalLogger;

import javax.annotation.Nonnull;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:cleclerc@cloudbees.com">Cyrille Le Clerc</a>
 */
public abstract class AbstractSyslogMessageSender implements SyslogMessageSender {
    protected final static Charset UTF_8 = Charset.forName("UTF-8");
    protected final InternalLogger logger = InternalLogger.getLogger(getClass());
    // default values
    protected String defaultAppName;
    protected Facility defaultFacility = Facility.USER;
    protected String defaultMessageHostname;
    protected Severity defaultSeverity = Severity.INFORMATIONAL;
    // remote syslog server config
    /**
     * Format of messages accepted by the remote syslog server
     * ({@link com.cloudbees.syslog.MessageFormat#RFC_3164 RFC_3164} or
     * {@link com.cloudbees.syslog.MessageFormat#RFC_5424 RFC_5424})
     */
    protected MessageFormat messageFormat = DEFAULT_SYSLOG_MESSAGE_FORMAT;
    // statistics
    protected final AtomicInteger sendCounter = new AtomicInteger();
    protected final AtomicLong sendDurationInNanosCounter = new AtomicLong();
    protected final AtomicInteger sendErrorCounter = new AtomicInteger();

    /**
     * Send the given text message
     *
     * @param message
     * @throws java.io.IOException
     */
    @Override
    public void sendMessage(CharArrayWriter message) throws IOException {

        SyslogMessage syslogMessage = new SyslogMessage()
                .withAppName(defaultAppName)
                .withFacility(defaultFacility)
                .withHostname(defaultMessageHostname)
                .withSeverity(defaultSeverity)
                .withMsg(message);

        sendMessage(syslogMessage);
    }

    @Override
    public void sendMessage(CharSequence message) throws IOException {
        System.out.println("SyslogMessageSender is trying to send: {"+message+"}");
        CharArrayWriter writer = new CharArrayWriter();
        writer.append(message);
        sendMessage(writer);
    }

    /**
     * Send the given {@link com.cloudbees.syslog.SyslogMessage}.
     *
     * @param message the message to send
     * @throws IOException
     */
    public abstract void sendMessage(@Nonnull SyslogMessage message) throws IOException;

    public String getDefaultAppName() {
        return defaultAppName;
    }

    public Facility getDefaultFacility() {
        return defaultFacility;
    }

    public MessageFormat getMessageFormat() {
        return messageFormat;
    }

    public String getDefaultMessageHostname() {
        return defaultMessageHostname;
    }

    public int getSendCount() {
        return sendCounter.get();
    }

    /**
     * Human readable view of {@link #getSendDurationInNanos()}
     *
     * @return total duration spent sending syslog messages
     */
    public long getSendDurationInMillis() {
        return TimeUnit.MILLISECONDS.convert(getSendDurationInNanos(), TimeUnit.NANOSECONDS);
    }

    /**
     * Human readable view of {@link #getSendDurationInNanos()}
     *
     * @return total duration spent sending syslog messages
     */
    public long getSendDurationInNanos() {
        return sendDurationInNanosCounter.get();
    }

    public int getSendErrorCount() {
        return sendErrorCounter.get();
    }

    public Severity getDefaultSeverity() {
        return defaultSeverity;
    }

    public void setDefaultAppName(String defaultAppName) {
        this.defaultAppName = defaultAppName;
    }

    public void setDefaultMessageHostname(String defaultHostname) {
        this.defaultMessageHostname = defaultHostname;
    }

    public void setDefaultFacility(Facility defaultFacility) {
        this.defaultFacility = defaultFacility;
    }

    public void setMessageFormat(MessageFormat messageFormat) {
        this.messageFormat = messageFormat;
    }

    public void setDefaultSeverity(Severity defaultSeverity) {
        this.defaultSeverity = defaultSeverity;
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "defaultAppName='" + defaultAppName + '\'' +
                ", defaultFacility=" + defaultFacility +
                ", defaultMessageHostname='" + defaultMessageHostname + '\'' +
                ", defaultSeverity=" + defaultSeverity +
                ", messageFormat=" + messageFormat +
                ", sendCounter=" + sendCounter +
                ", sendDurationInNanosCounter=" + sendDurationInNanosCounter +
                ", sendErrorCounter=" + sendErrorCounter +
                '}';
    }
}
