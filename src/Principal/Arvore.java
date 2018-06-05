package Principal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.tree.DefaultMutableTreeNode;

import Objetos.ConjuntoDoAtributo;
import Objetos.Conjunto;
import Objetos.Instancia;
import Objetos.No;

public class Arvore {

	/**
	 * @param args
	 */
	public static int folds = 3; //testando com o playtennis
	public static Conjunto c;

	public static void main(String[] args)
	{
		try {
			c = EP.leitura("./dataset/playtennis.data");
			//usando o playtennis só pra ver se funciona o crossvalidation, demora pra rodar com o adult

			ConjuntoDoAtributo melhor = EP.maiorGanho(c);
			int indiceAtributo = c.atributos().indexOf(melhor);
			int nivel = 0;
			No raiz = new No(null, melhor, indiceAtributo, nivel, false);
			String padrao = EP.valorDaMaioria(c);
			//gerar arvore e retornar o total de nos criados

			long tempoIni = System.currentTimeMillis();
			separar(c);
			double propAcerto = rodarExemplos(raiz, c, padrao);
			long tempoFim = System.currentTimeMillis();
			System.out.println((tempoFim-tempoIni)/1000);

		} catch (Exception e) {
			System.out.println("Deu merda!");
			e.printStackTrace();
		}
	}
	//Separar em x conjuntos diferentes - x-fold-cross-validation
	public static void separar(Conjunto c){

		int tamanho = c.exemplos().size();
		Random r = new Random();
		int linha = 0;
		int i;

		for(int j = 1; j<=folds; j++){
			i = 0;
			while(i<(tamanho/folds)){
				linha = r.nextInt(tamanho); // gera um linha randomicamente

				if(c.exemplos().get(linha).getFold()==0){ // se ainda não atualizou o fold do exemplo, então atualiza
					c.exemplos().get(linha).setFold(j);
					i++;
				}
			}
		}
	}


	public static int rodarTeste(No raiz, Conjunto teste){
		int acertos = 0;
		Instancia ins;

		for(int i=0; i<teste.exemplos().size(); i++){
			ins = teste.exemplos().get(i);

			if(raiz.isFolha()){
				if(ins.classe().equals("sim") && raiz.isDecisao()) acertos++;
				else if(ins.classe().equals("não") && !raiz.isDecisao()) acertos++;
			}
			else{
				for(int j =0; j<teste.atributos().size(); j++){
					if(teste.atributos().get(j).nome().equals(raiz.atributo)){
						for(int k = 0; k<raiz.dominioDevalores.size(); k++){
							if(raiz.dominioDevalores.get(k).equals(teste.exemplos().get(j))){
								System.out.println(raiz.dominioDevalores.get(k));
								acertos += rodarTeste(raiz.filhos.get(k), teste);
							}
						}
					}
				}
			}
		}
		return acertos;
	}

	public static double rodarExemplos(No raiz, Conjunto c, String padrao){
		double proporcao = 0;
		double propMedia = 0;
		int acerto = 0;
		int nos = 0;
		Conjunto treinamento = new Conjunto();
		treinamento.setAtributos(c.atributos());
		Conjunto teste = new Conjunto();
		teste.setAtributos(c.atributos());

		ArrayList<Instancia> insTreino = new ArrayList<Instancia>();
		ArrayList<Instancia> insTeste = new ArrayList<Instancia>();

		for(int f=1; f<=folds; f++){

			removerInstancias(insTeste);
			removerInstancias(insTreino);
			treinamento.removerTodos();
			teste.removerTodos();

			for(int i=0; i<c.exemplos().size(); i++){
				if(c.exemplos().get(i).getFold() != f){
					// se o exemplo não estiver no fold atual, coloca no conjunto de treino
					insTreino.add(c.exemplos().get(i));
				}
				else{
					// se o exemplo estiver no fold atual, coloca no conjunto de teste
					insTeste.add(c.exemplos().get(i));
				}
				treinamento.setExemplos(insTreino);
				teste.setExemplos(insTeste);
			}
			//cria a árvore com o conjunto de treinamento
			nos = criarNos2(raiz, "" ,treinamento, padrao);
			System.out.println(nos);
			//percorre a árvore com o conjunto de teste
			acerto = rodarTeste(raiz, teste);
			//calcula a proporção de acerto
			proporcao = acerto/c.exemplos().size();
			propMedia += proporcao;


		}
		//retorna proporção média de acertos em todas as rodadas
		return propMedia/folds;
	}

	//remove todos os exemplos do array;
	public static void removerInstancias(ArrayList<Instancia> exemplos){
		for(int i=0; i<exemplos.size(); i++){
			exemplos.remove(i);
		}
	}

	private static String apresentarExemplo(Instancia e, No raiz)
	{
		String classificacao = "";

		if (raiz.isFolha()) return raiz.pai.toString();

		return classificacao;
	}

	private static int criarNos2(No pai, String ramo, Conjunto c, String padrao)
	{

		int total = 1;
		No folha;
		boolean decisao = false;
		//verificar se todos os exemplos são da mesma classe,
		//caso afirmativo retorna string que representa a classe, por exempo 'sim' ou 'não'
		//caso negativo retorna uma string vazia
		String classe = EP.todosDaMesmaClasse(c);
		//se não há exemplos retornar padrão
		if (c.exemplos().size() == 0)
		{
			//visualizacao
			folha = new No(pai, pai.getNivel()+1, true);
			folha.setClasse(padrao);
			pai.adicionar(padrao, folha);
			if(classe.equals("sim"))
				decisao = true;
			folha.setDecisao(decisao);

		}
		//se não há atributos retornar o valor da maioria, padrao
		else if (c.atributos().size() < 2)
		{
			//visualizacao
			folha = new No(pai, pai.getNivel()+1, true);
			folha.setClasse(padrao);
			pai.adicionar(padrao, folha);
			if(classe.equals("sim"))
				decisao = true;
			folha.setDecisao(decisao);

			return 0;
		}
		//retorna a classe ou "" caso haja exemplos de ambas as classes
		else if (!classe.equals(""))
		{
			folha = new No(pai, pai.getNivel()+1, true);
			folha.setClasse(classe);
			pai.adicionar(classe, folha);
			if(classe.equals("sim"))
				decisao = true;
			folha.setDecisao(decisao);

			return 0;
		}
		else
		{
			//escolher o atributo que melhor divide o conjunto
			ConjuntoDoAtributo melhor = EP.maiorGanho(c);
			//adiciona o filho à arvore
			No filho = new No(pai, melhor, c.indice(melhor), pai.getNivel()+1, true);
			pai.adicionar(filho);

			//define o valor padrao como sendo a classe da maioria
			String valorDaMaioria = EP.valorDaMaioria(c);
			//particiona o conjunto em subconjuntos dos valores do melhor atributo
			Conjunto []sub = EP.particao(c, melhor);
			//percorre cada um dos ramos
			for (int i = 0; i < sub.length; i++)
			{
				String valor = melhor.dominio().get(i);
				//avança recursivamente
				total += criarNos2(filho, valor, sub[i], valorDaMaioria);
			}
		}
		return total;
	}
}
