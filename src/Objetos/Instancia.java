package Objetos;

import java.util.ArrayList;

public class Instancia implements Cloneable {
	
	public ArrayList<String> valores;
	public int fold;
	
	public Instancia(String[] valores)
	{
		this.valores = new ArrayList<String>();
		for (String s : valores) this.valores.add(s);
	}
	
	public Instancia(ArrayList<String> valores) {
		this.valores = new ArrayList<String>();
		this.valores.addAll(valores);
	}

	public Instancia clone()
	{
		return new Instancia(this.valores);
	}
	
	public String classe()
	{
		return valores.get(valores.size()-1);
	}

	public String imprimir() 
	{
		String saida = "";
		for (String s : valores)
		{
			saida += s + ", ";
		}
		saida = saida.substring(0, saida.length()-2); //remover a ultima virgula
		return saida;
	}

	public String valor(int indice) {
		return valores.get(indice);
	}

	public void remove(int indice) {
		valores.remove(indice);
	}

	public void setvalor(int indice_atributo, String valor) 
	{
		valores.remove(indice_atributo); //remove o valor antigo
		valores.add(indice_atributo, valor); //insere o valor novo na mesma posicao
	}

	public int nroValores() {
		return valores.size();
	}

	public int getFold() {
		return fold;
	}

	public void setFold(int fold) {
		this.fold = fold;
	}
	
}
