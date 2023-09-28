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
import ch.qos.logback.core.AppenderBase;
import com.microsoft.applicationinsights.internal.common.LogTelemetryClientProxy;
import com.microsoft.applicationinsights.internal.common.TelemetryClientProxy;
import com.microsoft.applicationinsights.internal.logger.InternalLogger;

public class SDKApplicationInsightsAppender  extends AppenderBase<ILoggingEvent> {
    private boolean isInitialized = false;
    private LogTelemetryClientProxy logTelemetryClientProxy;
    private String instrumentationKey;

    private String isAudit = "false";

    public SDKApplicationInsightsAppender() {
    }

    public TelemetryClientProxy getTelemetryClientProxy() {
        return this.logTelemetryClientProxy;
    }


    public String getIsAudit() {
        return this.isAudit;
    }

    public void setIsAudit(String value) {
        this.isAudit = value;
    }

    public void setInstrumentationKey(String instrumentationKey) {
        this.instrumentationKey = instrumentationKey;
    }

    protected void append(ILoggingEvent eventObject) {
        if (this.isStarted() && this.isInitialized) {
            SDKApplicationInsightsLogEvent aiEvent = new SDKApplicationInsightsLogEvent(eventObject, isAudit);
            this.logTelemetryClientProxy.sendEvent(aiEvent);
        }
    }

    public void start() {
        super.start();

        try {
            this.logTelemetryClientProxy = new LogTelemetryClientProxy(this.instrumentationKey);
            this.isInitialized = true;
        } catch (Exception var2) {
            this.isInitialized = false;
            InternalLogger.INSTANCE.error("Failed to initialize appender with exception: %s.", new Object[]{var2.toString()});
        }

    }
}
