package Principal;

import java.util.Comparator;

import Objetos.Instancia;

public class ComparadorAtributo implements Comparator<Instancia> {
	
	@Override
	public int compare(Instancia r1, Instancia r2) {
		if (Double.parseDouble(r1.valor(0)) < Double.parseDouble(r2.valor(0))) return -1;
		if (Double.parseDouble(r1.valor(0)) > Double.parseDouble(r2.valor(0))) return 1;
		return 0;
	}
}
