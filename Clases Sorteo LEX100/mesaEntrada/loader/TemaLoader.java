package com.base100.lex100.mesaEntrada.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import jxl.Cell;
import jxl.read.biff.BiffException;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.util.Strings;

import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.ObjetoJuicio;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.OficinaSorteo;
import com.base100.lex100.entity.Sorteo;
import com.base100.lex100.entity.Tema;

public class TemaLoader extends AbstractConfigurationLoader{

	
	private String TEMA_VALUE = "TEMA";
	private String SUBTEMA_VALUE = "SUBTEMA";
	private String OBJETO_JUICIO_VALUE = "OBJETO_JUICIO";
	
	private List<ObjetoJuicio> objetosJuicio = new ArrayList<ObjetoJuicio>();
	private List<Tema> subtemas = new ArrayList<Tema>();
	private Tema lastTema = null;
	private int insertCountTema = 0; 
	private int updateCountTema = 0; 
	private int updateCountObjetoJuicio = 0; 

	
	public TemaLoader(EntityManager entityManager, StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}
	
	protected void init() {
	}
	
	
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		if(isTemaRow(row)){
			deleteOtherSubtemas(lastTema, subtemas);
			subtemas.clear();
			updateOtherObjetosJuicio(lastTema, objetosJuicio);
			objetosJuicio.clear();
			lastTema = cargaTema(row, null);
			if(lastTema == null){
				fatal("Proceso abortado");
			}
		}else if(isSubtemaRow(row)){
			updateOtherObjetosJuicio(lastTema, objetosJuicio);
			objetosJuicio.clear();
			lastTema = cargaTema(row, lastTema);
			if(lastTema != null){
				subtemas.add(lastTema);
			}
		}else if(isObjetoJuicioRow(row)){
			ObjetoJuicio objetoJuicio = updateObjetoJuicio(row, lastTema);
			if(objetoJuicio != null){
				objetosJuicio.add(objetoJuicio);
			}
		}
	}

	protected void end() {
		if(lastTema != null){
			deleteOtherSubtemas(lastTema, subtemas);
			updateOtherObjetosJuicio(lastTema, objetosJuicio);
		}
		info("Tema: "+insertCountTema+" filas añadidas, "+updateCountTema+" filas modificadas");
		info("ObjetoJuicio: " + updateCountObjetoJuicio+" filas modificadas");
	}

	
	private void deleteOtherSubtemas(Tema tema,	List<Tema> subtemas) {
		if(tema != null){
			List<Integer> ids = new ArrayList<Integer>();
			for(Tema item: subtemas){
				ids.add(item.getId());
			}
			if(ids.size() > 0){
				getEntityManager().createQuery("update Tema set status = -1 where temaPadre = :tema and id not in (:ids)")
				.setParameter("tema", tema)			
				.setParameter("ids", ids).executeUpdate();
			}else {
				getEntityManager().createQuery("update Tema set status = -1 where temaPadre = :tema")
				.setParameter("tema", tema).executeUpdate();
			}
		}
		
	}

	private void updateOtherObjetosJuicio(Tema tema, List<ObjetoJuicio> objetosJuicio) {
		if(tema != null){
			List<Integer> ids = new ArrayList<Integer>();
			for(ObjetoJuicio item: objetosJuicio){
				ids.add(item.getId());
			}
			if(ids.size() > 0){
				getEntityManager().createQuery("update ObjetoJuicio set tema = null where tema = :tema and id not in (:ids)")
				.setParameter("tema", tema)			
				.setParameter("ids", ids).executeUpdate();
			}else {
				getEntityManager().createQuery("update ObjetoJuicio set tema = null where tema = :tema")
				.setParameter("tema", tema).executeUpdate();
				
			}
		}
		
	}

	private boolean isObjetoJuicioRow(Cell[] row) {
		return OBJETO_JUICIO_VALUE.equalsIgnoreCase(row[0].getContents());
	}

	private boolean isTemaRow(Cell[] row) {
		return TEMA_VALUE.equalsIgnoreCase(row[0].getContents());
	}

	private boolean isSubtemaRow(Cell[] row) {
		return SUBTEMA_VALUE.equalsIgnoreCase(row[0].getContents());
	}

	private ObjetoJuicio updateObjetoJuicio(Cell[] row, Tema tema) throws LoaderException {
		String codigoObjetoJuicio = getContent(row, 3);
		ObjetoJuicio objetoJuicio = null;
		if (getEntityManager() != null) {
			objetoJuicio = buscaObjetoJuicio (tema.getCamara(), tema.getMateria(), codigoObjetoJuicio, true);
			if (objetoJuicio != null) {
				objetoJuicio.setTema(tema);
				getEntityManager().flush();
				updateCountObjetoJuicio++;
			}
		}
		return objetoJuicio;
	}

	private Tema cargaTema(Cell[] row, Tema temaPadre) throws LoaderException {
		String codigoCamara = getContent(row, 1);
		String codigoMateria = getContent(row, 2);
		String codigoTema = getContent(row, 3);
		String descripcionTema = getContent(row, 4);

		return actualizaTema(codigoCamara, codigoMateria, codigoTema, descripcionTema, temaPadre); 
		
	}

	private Tema actualizaTema(String codigoCamara, String codigoMateria, String codigoTema,
			String descripcionTema, Tema temaPadre) throws LoaderException {
		
		Tema tema = null;
		if (getEntityManager() != null) {
			Camara camara = buscaCamara(codigoCamara);
			if(camara == null){
				fatal("Proceso abortado");
			}
			Materia materia = buscaMateria(codigoMateria, true);
			if(materia == null){
				fatal("Proceso abortado");
			}
			tema = buscaTema(codigoTema, materia, camara, false);
			if (tema == null) {
				tema = new Tema();
				tema.setCamara(camara);
				tema.setMateria(materia);
				tema.setCodigo(codigoTema);
				
			}
			tema.setDescripcion(descripcionTema);
			tema.setTemaPadre(temaPadre);
			if(Strings.isEmpty(tema.getUuid())){
				tema.setUuid(UUID.randomUUID().toString());
			}

			if (tema.getId() == null) {
				getEntityManager().persist(tema);
				insertCountTema++;
			}else{
				updateCountTema++;
			}
			getEntityManager().flush();
		}
		return tema;
	}

	public static void main(String[] args) {
		File file = new File(args[0]);
		
		TemaLoader temaLoader = new TemaLoader(null, null);
		
		try {
			temaLoader.load(new FileInputStream(file));			
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LoaderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
