package com.base100.lex100.mesaEntrada.sorteo.listeners;

import javax.persistence.EntityManager;

import com.base100.lex100.mesaEntrada.sorteo.SorteoExpedienteEntry;

public interface ISorteoListener{
	public void notifySorteoStarted(EntityManager entityManager, SorteoExpedienteEntry entry);
	public void notifySorteoFinished(EntityManager entityManager, SorteoExpedienteEntry entry, boolean result);
}
