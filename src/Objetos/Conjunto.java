package Objetos;

import java.util.ArrayList;
import java.util.List;

public class Conjunto implements Cloneable {

	private List<ConjuntoDoAtributo> atributos = new ArrayList<ConjuntoDoAtributo>();
	private List<Instancia> exemplos = new ArrayList<Instancia>();

	public Conjunto() //inicializacao vazia
	{
		this.atributos = new ArrayList<ConjuntoDoAtributo>();
		this.exemplos = new ArrayList<Instancia>();
	}

	public Conjunto(List<ConjuntoDoAtributo> atributos, List<Instancia> exemplos) //para clonar
	{
		this.atributos.addAll(atributos);
		this.exemplos.addAll(exemplos);
	}

	public Conjunto(List<ConjuntoDoAtributo> atributos) //somente copia os atributos
	{
		this.atributos.addAll(atributos);
		this.exemplos = new ArrayList<Instancia>();
	}

	public Conjunto clone()
	{		
		return new Conjunto(this.atributos, this.exemplos);
	}

	public String imprimir() 
	{
		String saida = "Atributos (" + (this.atributos.size()-1) +"):\n";
		int i = 0;
		for (ConjuntoDoAtributo a : atributos)
		{
			saida += a.nome();
			if (i < atributos.size()-1) saida += "; ";
			++i;
		}
		saida += "\n";
		i = 1;
		for (Instancia e : exemplos)
		{
			saida += i + ": " + e.imprimir() + "\n";
			++i;
		}
		return saida;
	}

	public List<ConjuntoDoAtributo> atributos() {
		return this.atributos;
	}

	public void removerExemplo(int i) 
	{
		exemplos.remove(i);
	}
	
	public void removerTodos(){
		for(int i=0; i<exemplos.size(); i++){
			removerExemplo(i);
		}
	}

	public List<Instancia> exemplos() {
		return this.exemplos;
	}

	public void remover(ConjuntoDoAtributo a) 
	{
		int indice = atributos.indexOf(a);
		for (Instancia i : exemplos)
		{
			i.remove(indice);
		}
		atributos.remove(a);
	}

	public void instancia(Instancia i) {
		exemplos.add(i);
	}

	public ConjuntoDoAtributo classe() 
	{
		return atributos.get(atributos.size()-1);
	}

	public String positivo() {
		return classe().dominio().get(0);
	}

	public int contarExemplos(ConjuntoDoAtributo a, String v)
	{
		int indice = atributos.indexOf(a);
		if (indice == -1) return -1; //atributo não encontrado
		if (a.dominio().indexOf(v) == -1) return -1; //valor não encontrado
		
		int total = 0;
		for (Instancia e : exemplos)
			if (e.valor(indice).equals(v)) ++total;
		return total;
	}

	public String imprimirAtributos() 
	{
		String saida = "";
		for (ConjuntoDoAtributo a : atributos)
		{
			saida += a.nome() + ", ";
		}
		saida = saida.substring(0, saida.length()-2); //remover a ultima virgula
		return saida;
	}

	public int[] contarExemplos() {
		int []resp = new int[2];
		String classe1 = this.classe().dominio().get(0);
		for (Instancia e : exemplos)
		{
			if (e.classe().equals(classe1)) resp[0]++;
			else resp[1]++;
		}
		return resp;
	}

	public List<ConjuntoDoAtributo> getAtributos() {
		return atributos;
	}

	public void setAtributos(List<ConjuntoDoAtributo> atributos) {
		this.atributos = atributos;
	}

	public List<Instancia> getExemplos() {
		return exemplos;
	}

	public void setExemplos(List<Instancia> exemplos) {
		this.exemplos = exemplos;
	}

	public int indice(ConjuntoDoAtributo melhor) {		
		int ind = atributos.indexOf(melhor);
		return ind;
	}
}
