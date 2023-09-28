/*
 * ApplicationInsights-Java
 * Copyright (c) Microsoft Corporation
 * All rights reserved.
 *
 * MIT License
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the ""Software""), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package com.efs.sdk.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import com.microsoft.applicationinsights.internal.common.ApplicationInsightsEvent;
import com.microsoft.applicationinsights.internal.logger.InternalLogger;
import com.microsoft.applicationinsights.telemetry.SeverityLevel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class SDKApplicationInsightsLogEvent extends ApplicationInsightsEvent {
    private final String isAudit;
    private ILoggingEvent loggingEvent;

    public SDKApplicationInsightsLogEvent(ILoggingEvent loggingEvent, String isAudit) {
        this.loggingEvent = loggingEvent;
        this.isAudit = isAudit;
    }

    public String getMessage() {
        return this.loggingEvent.getFormattedMessage();
    }

    public boolean isException() {
        return this.loggingEvent.getThrowableProxy() != null;
    }

    public Exception getException() {
        Exception exception = null;
        if (this.isException()) {
            Throwable throwable = ((ThrowableProxy)this.loggingEvent.getThrowableProxy()).getThrowable();
            exception = throwable instanceof Exception ? (Exception)throwable : new Exception(throwable);
        }

        return exception;
    }

    public Map<String, String> getCustomParameters() {
        Map<String, String> metaData = new HashMap();
        metaData.put("SourceType", "LOGBack");
        addLogEventProperty("LoggerName", this.loggingEvent.getLoggerName(), metaData);
        addLogEventProperty("LoggingLevel", this.loggingEvent.getLevel() != null ? this.loggingEvent.getLevel().levelStr : null, metaData);
        addLogEventProperty("ThreadName", this.loggingEvent.getThreadName(), metaData);
        addLogEventProperty("TimeStamp", getFormattedDate(this.loggingEvent.getTimeStamp()), metaData);
        addLogEventProperty("AUDIT", this.isAudit, metaData);
        if (this.isException()) {
            addLogEventProperty("Logger Message", this.getMessage(), metaData);
        }

        Iterator var2 = this.loggingEvent.getMDCPropertyMap().entrySet().iterator();

        while(var2.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry)var2.next();
            addLogEventProperty((String)entry.getKey(), (String)entry.getValue(), metaData);
        }

        return metaData;
    }

    public SeverityLevel getNormalizedSeverityLevel() {
        int log4jLevelAsInt = this.loggingEvent.getLevel().toInt();
        switch (log4jLevelAsInt) {
            case Integer.MIN_VALUE:
            case 5000:
            case 10000:
                return SeverityLevel.Verbose;
            case 20000:
                return SeverityLevel.Information;
            case 30000:
                return SeverityLevel.Warning;
            case 40000:
                return SeverityLevel.Error;
            case 50000:
                return SeverityLevel.Critical;
            default:
                InternalLogger.INSTANCE.error("Unknown Logback option, %d, using TRACE level as default", new Object[]{log4jLevelAsInt});
                return SeverityLevel.Verbose;
        }
    }
}
