package com.bigmac.engine;

import com.espertech.esper.client.EventType;
import com.espertech.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.epl.expression.core.ExprNode;
import com.espertech.esper.view.*;

import java.util.List;

public class OHLCBarPlugInViewFactory extends ViewFactorySupport {
    private ViewFactoryContext viewFactoryContext;
    private List<ExprNode> viewParameters;
    private ExprNode timestampExpression;
    private ExprNode valueExpression;
    private ExprNode symbolExpression;

    public void setViewParameters(ViewFactoryContext viewFactoryContext, List<ExprNode> viewParameters) throws ViewParameterException {
        this.viewFactoryContext = viewFactoryContext;
        if (viewParameters.size() != 3) {
            throw new ViewParameterException("View requires a two parameters: the expression returning timestamps and the expression supplying OHLC data points");
        }
        this.viewParameters = viewParameters;
    }

    public void attach(EventType parentEventType, StatementContext statementContext, ViewFactory optionalParentFactory, List<ViewFactory> parentViewFactories) throws ViewParameterException {
        ExprNode[] validatedNodes = ViewFactorySupport.validate("OHLC view", parentEventType, statementContext, viewParameters, false);

        symbolExpression = validatedNodes[0];
        timestampExpression = validatedNodes[1];
        valueExpression = validatedNodes[2];
        
        if (symbolExpression.getForge().getEvaluationType() != String.class) {
            throw new ViewParameterException("View requires string-typed timestamp values in parameter 1");
        }
        if ((valueExpression.getForge().getEvaluationType() != double.class) && (valueExpression.getForge().getEvaluationType() != Double.class)) {
            throw new ViewParameterException("View requires double-typed values for in parameter 2");
        }
        if ((timestampExpression.getForge().getEvaluationType() != long.class) && (timestampExpression.getForge().getEvaluationType() != Long.class)) {
            throw new ViewParameterException("View requires long-typed timestamp values in parameter 1");
        }
    }

    public View makeView(AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext) {
        return new OHLCBarPlugInView(agentInstanceViewFactoryContext, symbolExpression, timestampExpression, valueExpression);
    }

    public EventType getEventType() {
        return OHLCBarPlugInView.getEventType(viewFactoryContext.getEventAdapterService());
    }

    public String getViewName() {
        return OHLCBarPlugInView.class.getSimpleName();
    }
}
