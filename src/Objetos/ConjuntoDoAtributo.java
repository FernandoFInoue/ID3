package Objetos;

import java.util.ArrayList;
import java.util.List;

public class ConjuntoDoAtributo {
	
	String atributo;
	private List<String> valores;	//os valores possiveis
	boolean continuo = true;
	
	public ConjuntoDoAtributo(String NomeDoAtributo) 
	{
		this.atributo = NomeDoAtributo;
		this.valores = new ArrayList<String>();
	}
	
	public ConjuntoDoAtributo(String NomeDoAtributo, boolean continuo) 
	{
		this.atributo = NomeDoAtributo;
		this.valores = new ArrayList<String>();
		this.continuo = continuo;
	}
	
	public void adicionarValorAoDominio(String valor)
	{
		this.valores.add(valor);
	}
	
	public boolean valorExiste(String valor)
	{
		if (this.valores.contains(valor)) return true;
		else return false;
	}

	public String nome() 
	{
		return this.atributo;
	}

	public List<String> dominio() {
		return valores;
	}

	public boolean ehContinuo() {
		return this.continuo;
	}
}
