package Objetos;

import java.util.ArrayList;
import java.util.List;

public class No {
	
	public String atributo;
	public int indiceAtributo;
	public No pai;
	public ArrayList<No> filhos;
	public List<String> dominioDevalores;
	public int nivel;
	public String classe;
		
	public boolean isFolha = false;
	public boolean decisao; //positivo, negativo - true > 50 / false <= 50
	
	public No(No pai, ConjuntoDoAtributo atributo, int indiceAtributo, int nivel, boolean isFolha)
	{
		this.pai = pai;
		this.atributo = atributo.atributo;
		this.dominioDevalores = atributo.dominio();
		this.indiceAtributo = indiceAtributo;
		this.nivel = nivel;
		this.isFolha = isFolha;
		filhos = new ArrayList<No>();
		dominioDevalores = new ArrayList<String>();
	}

	//construtor para as folhas
	public No(No raiz, int nivel, boolean isFolha) 
	{
		this.pai = raiz;
		this.nivel = nivel;
		this.isFolha = isFolha;
	}

	public boolean isFolha() {
		return isFolha;
	}

	public void setFolha(boolean isFolha) {
		this.isFolha = isFolha;
	}

	public boolean isDecisao() {
		return decisao;
	}

	public void setDecisao(boolean decisao) {
		this.decisao = decisao;
	}

	public void adicionar(No folha) 
	{
		filhos.add(folha);
	}

	public int getNivel() {
		return nivel;
	}

	public void setClasse(String padrao) {
		this.classe = padrao;
	}

	public void adicionar(String padrao, No folha) {
		filhos.add(folha);
		filhos.get(filhos.indexOf(folha)).setClasse(padrao);		
	}
}
