package com.base100.lex100.mesaEntrada.ingreso;

public interface IEditPojo {
	public void setModified(boolean b);
	public boolean isModified();
	public void setNew(boolean b);
	public boolean isNew();
	public boolean isEmpty();
	public boolean needUpdateOrAdd();
	public boolean needDelete();

}
