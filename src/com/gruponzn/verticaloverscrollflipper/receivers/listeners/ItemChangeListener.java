package com.gruponzn.verticaloverscrollflipper.receivers.listeners;

import android.os.Parcelable;

public interface ItemChangeListener {

	/**
	 * Informa que deve ser instanciada uma nova view.
	 * 
	 * @param next
	 *            flag para informar se esta rolando para o pr??ximo (
	 *            <code>true</code>) ou para o anterior (<code>false</code>)
	 */
	public void prepareView(boolean next);

	/**
	 * Informa que a view deve popular com o item carregado.
	 * 
	 * @param item
	 *            item para popular a view
	 */
	public void setupItem(Parcelable item);

	/**
	 * Um item foi recebido ap??s o in??cio do overscroll.
	 * 
	 * @param item
	 *            o item para preencher a pr??xima view
	 * @param next
	 *            flag para informar se esta rolando para o pr??ximo (
	 *            <code>true</code>) ou para o anterior (<code>false</code>)
	 */
	public void itemReceived(Parcelable item, boolean next);

	/**
	 * Informa que o topo da lista principal foi atingido.
	 */
	public void topReached();

	/**
	 * Informa que o final da lista principal foi atingido.
	 */
	public void bottomReached();

	/**
	 * Ativado quando ?? necess??rio chamar algum comportamento quando ?? iniciado
	 * o procedimento de overscroll.
	 */
	public void overScrolling();
}