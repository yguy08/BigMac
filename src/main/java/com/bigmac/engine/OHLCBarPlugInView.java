package com.bigmac.engine;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import com.espertech.esper.core.service.EPStatementHandleCallback;
import com.espertech.esper.core.service.EngineLevelExtensionServicesContext;
import com.espertech.esper.epl.expression.core.ExprNode;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.schedule.ScheduleHandleCallback;
import com.espertech.esper.view.ViewSupport;
import com.bigmac.enumeration.TickerType;
import com.bigmac.marketdata.Bar;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

/**
 * Custom view to compute minute OHLC bars for double values and based on the event's timestamps.
 * <p>
 * Assumes events arrive in the order of timestamps, i.e. event 1 timestamp is always less or equal event 2 timestamp.
 * <p>
 * Implemented as a custom plug-in view rather then a series of EPL statements for the following reasons:
 * - Custom output result mixing aggregation (min/max) and first/last values
 * - No need for a data window retaining events if using a custom view
 * - Unlimited number of groups (minute timestamps) makes the group-by clause hard to use
 */
public class OHLCBarPlugInView extends ViewSupport {
    private final static int LATE_EVENT_SLACK_SECONDS = 1;

    private final AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext;
    private final long scheduleSlot;
    private final ExprNode symbolExpression;
    private final ExprNode timestampExpression;
    private final ExprNode valueExpression;
    private final EventBean[] eventsPerStream = new EventBean[1];

    private EPStatementHandleCallback handle;
    private String symbol;
    private Long cutoffTimestampMinute;
    private Long currentTimestampMinute;
    private Double first;
    private Double last;
    private Double max;
    private Double min;
    private EventBean lastEvent;

    public OHLCBarPlugInView(AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext, ExprNode symbolExpression, 
            ExprNode timestampExpression, ExprNode valueExpression) {
        this.agentInstanceViewFactoryContext = agentInstanceViewFactoryContext;
        this.symbolExpression = symbolExpression;
        this.timestampExpression = timestampExpression;
        this.valueExpression = valueExpression;
        this.scheduleSlot = agentInstanceViewFactoryContext.getStatementContext().getScheduleBucket().allocateSlot();
    }

    public void update(EventBean[] newData, EventBean[] oldData) {
        if (newData == null) {
            return;
        }

        for (EventBean theEvent : newData) {
            eventsPerStream[0] = theEvent;
            symbol = (String) symbolExpression.getForge().getExprEvaluator().evaluate(eventsPerStream, true, agentInstanceViewFactoryContext);
            Long timestamp = (Long) timestampExpression.getForge().getExprEvaluator().evaluate(eventsPerStream, true, agentInstanceViewFactoryContext);
            Long timestampMinute = truncateTo(timestamp, ChronoUnit.DAYS);
            double value = (Double) valueExpression.getForge().getExprEvaluator().evaluate(eventsPerStream, true, agentInstanceViewFactoryContext);

            // test if this minute has already been published, the event is too late
            if ((cutoffTimestampMinute != null) && (timestampMinute <= cutoffTimestampMinute)) {
                continue;
            }

            // if the same minute, aggregate
            if (timestampMinute.equals(currentTimestampMinute)) {
                applyValue(value);
            } else {
                // first time we see an event for this minute
                // there is data to post
                if (currentTimestampMinute != null) {
                    postData();
                }

                currentTimestampMinute = timestampMinute;
                applyValue(value);

                // schedule a callback to fire in case no more events arrive
                scheduleCallback();
            }
        }
    }

    public EventType getEventType() {
        return getEventType(agentInstanceViewFactoryContext.getStatementContext().getEventAdapterService());
    }

    public Iterator<EventBean> iterator() {
        throw new UnsupportedOperationException("Not supported");
    }

    private void applyValue(double value) {
        if (first == null) {
            first = value;
        }
        last = value;
        if (min == null) {
            min = value;
        } else if (min.compareTo(value) > 0) {
            min = value;
        }
        if (max == null) {
            max = value;
        } else if (max.compareTo(value) < 0) {
            max = value;
        }
    }

    protected static EventType getEventType(EventAdapterService eventAdapterService) {
        return eventAdapterService.addBeanType(Bar.class.getName(), Bar.class, false, false, false);
    }

    private static long removeSeconds(long timestamp) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        return cal.getTimeInMillis();
    }
    
    private static long truncateTo(long timestamp, TemporalUnit unit) {
        return Instant.ofEpochMilli(timestamp).truncatedTo(unit).toEpochMilli();
    }

    private void scheduleCallback() {
        if (handle != null) {
            // remove old schedule
            agentInstanceViewFactoryContext.getStatementContext().getSchedulingService().remove(handle, scheduleSlot);
            handle = null;
        }

        long currentTime = agentInstanceViewFactoryContext.getStatementContext().getSchedulingService().getTime();
        long currentRemoveSeconds = truncateTo(currentTime, ChronoUnit.DAYS);
        long targetTime = currentRemoveSeconds + (60 + LATE_EVENT_SLACK_SECONDS) * 1000; // leave some seconds for late comers
        long scheduleAfterMSec = targetTime - currentTime;

        ScheduleHandleCallback callback = new ScheduleHandleCallback() {
            public void scheduledTrigger(EngineLevelExtensionServicesContext extensionServicesContext) {
                handle = null;  // clear out schedule handle
                OHLCBarPlugInView.this.postData();
            }
        };

        handle = new EPStatementHandleCallback(agentInstanceViewFactoryContext.getEpStatementAgentInstanceHandle(), callback);
        agentInstanceViewFactoryContext.getStatementContext().getSchedulingService().add(scheduleAfterMSec, handle, scheduleSlot);
    }

    private void postData() {
        Bar barValue = new Bar(currentTimestampMinute, symbol, TickerType.BINANCE, first, max, min, last, 10, Duration.ofDays(1));
        EventBean outgoing = agentInstanceViewFactoryContext.getStatementContext().getEventAdapterService().adapterForBean(barValue);
        if (lastEvent == null) {
            this.updateChildren(new EventBean[]{outgoing}, null);
        } else {
            this.updateChildren(new EventBean[]{outgoing}, new EventBean[]{lastEvent});
        }
        lastEvent = outgoing;

        cutoffTimestampMinute = currentTimestampMinute;
        symbol = null;
        first = null;
        last = null;
        max = null;
        min = null;
        currentTimestampMinute = null;
    }
}
