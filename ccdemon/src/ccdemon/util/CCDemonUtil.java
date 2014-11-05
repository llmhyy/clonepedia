package ccdemon.util;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.internal.handlers.WidgetMethodHandler;

@SuppressWarnings("restriction")
public class CCDemonUtil {
	
	public static void callBackDefaultEvent(String name, ExecutionEvent event) throws ExecutionException{
		WidgetMethodHandler handler = new WidgetMethodHandler();
		handler.setInitializationData(null, null, name);
		handler.execute(event);
	}
	
	
}
