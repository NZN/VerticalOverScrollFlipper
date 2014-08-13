package com.gruponzn.verticaloverscrollflipper.utils;

import android.content.Context;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.gruponzn.tecmundo.helpers.itrf.IGoogleTracker;

/**
 * Classe que auxilia a integracao com o SDK do Google Analytics.
 * 
 * @author Bruno Tortato Furtado
 * @since 06 de fevereiro de 2014
 * 
 */
public class GoogleTrackerUtil {

	//public static final String EVENT_CATEGORY_PROGRAM = "Programs";
	//public static final String EVENT_ACTION_DOWNLOAD = "Download";
	
	/**
	 * Bloqueando criação de instancia
	 */
	private GoogleTrackerUtil () { }
	
	/**
	 * Envia evento para o Google Analytics. 
	 * 
	 * @param context
	 * @param category Nome da categoria
	 * @param action Nome da ação
	 */
	public static void trackEvent(Context context, String category, String action) {
		trackEvent(context, category, null, null);
	}
	
	/**
	 * Envia evento para o Google Analytics. 
	 * 
	 * @param context
	 * @param category Nome da categoria
	 * @param action Nome da ação
	 * @param label Descricão ou objeto envolvido na ação
	 */
	public static void trackEvent(Context context, String category, String action, String label) {
		trackEvent(context, category, action, label, 1);
	}
	
	/**
	 * Envia evento para o Google Analytics.
	 * 
	 * @param context
	 * @param category Nome da categoria
	 * @param action Nome da ação
	 * @param label Descrição ou objeto envolvido na ação
	 * @param value Valor da ação
	 */
	public static void trackEvent(Context context, String category, String action, String label, int value) {
		getTracker(context).send(MapBuilder.createEvent(category, action, label, Long.valueOf(value)).build());
	}
	
	/**
	 * Envia evento para o Google Analytics.
	 * 
	 * @param context
	 * @param category Nome da categoria
	 * @param action Nome da ação
	 * @param label Descrição ou objeto envolvido na ação
	 * @param value Valor da ação
	 */
	public static void trackEvent(Context context, String category, String action, int label, int value) {
		getTracker(context).send(MapBuilder.createEvent(category, action, String.valueOf(label), Long.valueOf(value)).build());
	}
	
	/**
	 * Envia evento para o Google Analytics.
	 * 
	 * @param context
	 * @param category Nome da categoria
	 * @param action Nome da ação
	 * @param label Descrição ou objeto envolvido na ação
	 * @param value Valor da ação
	 */
	public static void trackEvent(Context context, String category, String action, String label, Long value) {
		getTracker(context).send(MapBuilder.createEvent(category, action, label, value).build());
	}
	
	/**
	 * Envia nome de tela para relattórios utilizados no Google Analytics
	 * 
	 * @param context 
	 * @param params Parâmetros adicionais utilizados para gerar o nome na tela (Exemplo: /program/2000)
	 */
	public static void trackView(Object context, Object... params) {
		if (!(context instanceof IGoogleTracker)) {
			throw new IllegalArgumentException("'context' not implement 'IGoogleTracker' object");
		}
		
		if (!(context instanceof Context)) {
			throw new IllegalArgumentException("'context' is not a 'Context' object");
		}
		
		trackView((IGoogleTracker) context, (Context) context, params);
	}
	
	/**
	 * Envia nome de tela para relatórios utilizados no Google Analytics
	 * 
	 * @param itrf Classe que implementa a interface 'IGoogleTracker'
	 * @param context
	 * @param params Parâmetros adicionais utilizados para gerar o nome na tela (Exemplo: /program/2000)
	 */
	public static void trackView(IGoogleTracker itrf, Context context, Object... params) {
		if (itrf == null || itrf.getTrackViewName() == null) {
			throw new UnsupportedOperationException("Page tracker name not found. Implement 'getPageTrackerName()'");
		}
		
		Tracker tracker = getTracker(context);
		String screenName = getScreenName(context, itrf.getTrackViewName(), params);
		
		tracker.set(Fields.SCREEN_NAME, screenName);
		tracker.send(MapBuilder.createAppView().build());
	}
	
	/**
	 * Cria nome de tela com base nos parâmetros adicionais
	 * 
	 * @param context
	 * @param page Nome da tela
	 * @param paths Parâmetros adicionais, como por exemplo ID do aplicativo ou categoria
	 * @return
	 */
	private static String getScreenName(Context context, String page, Object... paths) {
		StringBuffer screenName = new StringBuffer();
		String separator = "/";
		
		screenName.append(separator).append("android");
		screenName.append(separator).append(page);
		
		for (Object path : paths) {
			screenName.append(separator).append(path.toString());
		}
		
		return screenName.toString();
	}
	
	/**
	 * Cria tracker do Analytics
	 * 
	 * Seu objetivo é facilitar futuras alterações caso
	 * ocorram mudanças no SDK do Analytics.
	 * 
	 * A implementação de tracker atual (EasyTracker) utiliza o arquivo 'analytics.xml'
	 * localizado dentro da pasta 'res/values'
	 * 
	 * @param context
	 * @return Tracker do Analytics
	 */
	private static Tracker getTracker(Context context) {
		Tracker tracker = EasyTracker.getInstance(context);
		tracker.set(Fields.NON_INTERACTION, "false");
		return tracker;
	}
}