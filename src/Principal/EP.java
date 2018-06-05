package Principal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import Objetos.ConjuntoDoAtributo;
import Objetos.Conjunto;
import Objetos.Instancia;
import Objetos.No;

public class EP
{

	public static int folds = 3;

	//Método de leitura mais flexível, recebe como parametro o nome do arquivo, remove missing values e inclui os possiveis valores 'automagicamente'
	public static Conjunto leitura(String nome_arquivo) throws Exception {

		long tempoInicio = System.currentTimeMillis();	//  tempo para ler e processar os dados


		//Abertura do arquivo
		FileInputStream in = null;
		try
		{
			File inputFile = new File(nome_arquivo);
			in = new FileInputStream(inputFile);
		}
		catch (Exception e)
		{
			System.err.println( "Não foi possível abrir o arquivo: " + nome_arquivo + "\n" + e);
			return null;
		}

		BufferedReader bin = new BufferedReader(new InputStreamReader(in) );
		String entrada;

		//leitura dos atributos (cabecalho)
		while(true)
		{
			entrada = bin.readLine();
			if (entrada == null) {
				System.err.println( "No data found in the data file: " + nome_arquivo + "\n");
				return null;
			}
			if (entrada.startsWith("//")) continue;
			if (entrada.equals("")) continue;
			break;
		}

		String[] atributos = entrada.split(", ");
		int numeroAtributos = atributos.length;
		ArrayList<ConjuntoDoAtributo> atributosConjunto = new ArrayList<ConjuntoDoAtributo>();

		if (numeroAtributos <= 1)
		{
			System.err.println( "Read line: " + entrada);
			System.err.println( "Could not obtain the names of attributes in the line");
			System.err.println( "Expecting at least one input attribute and one output attribute");
			return null;
		}
		for (String nome : atributos)
		{
			atributosConjunto.add(new ConjuntoDoAtributo(nome));
		}

		int numero_exemplos = 0;
		int numero_excluidos = 0;

		ArrayList<Instancia> exemplosConjunto = new ArrayList<Instancia>();

		while(true)
		{
			entrada = bin.readLine();
			if (entrada == null) break;
			if (entrada.startsWith("//")) continue;
			if (entrada.equals("")) continue;

			// A expressão regular ' *, *' dá conta de encurtar todas as strings removendo espaços, tabulações, etc.
			String[] valores = entrada.split(" *, *");
			if (valores.length != numeroAtributos)
			{
				System.err.println( "Read " + "" + " data");
				System.err.println( "Last line read: " + entrada);
				System.err.println( "Expecting " + numeroAtributos  + " attributes");
				return null;
			}

			//Verificar se há missing values: percorre todos os valores do exemplo procurando pelo símbolo '?'
			boolean missing_value = false;

			for (String s : valores)
			{
				if (s.equals("?"))
				{
					missing_value = true;
					break;
				}
			}

			if (!missing_value)
			{
				exemplosConjunto.add(new Instancia(valores));
				for (int i = 0; i < valores.length; i++)
				{
					if (!atributosConjunto.get(i).valorExiste(valores[i])) //valor ainda desconhecido do domínio
						atributosConjunto.get(i).adicionarValorAoDominio(valores[i]);
				}
			}
			else ++numero_excluidos;

			++numero_exemplos;
		}

		System.err.println("Total de exemplos: " + numero_exemplos);
		System.err.println("Total de exemplos descartados: " + numero_excluidos);
		long tempoFim = System.currentTimeMillis();
		System.err.println("Tempo de processamento: " + (tempoFim-tempoInicio)/1000);

		bin.close();

		Conjunto c = new Conjunto(atributosConjunto, exemplosConjunto);
		return c;
	}

	public static Conjunto particao(Conjunto c, ConjuntoDoAtributo atributo, String valorAlvo)
	{
		List<Instancia> exemplos = new ArrayList<Instancia>();
		int posicao = c.atributos().indexOf(atributo);	//indice do atributo 'a'

		for (Instancia i : c.exemplos())
		{
			if (i.valor(posicao).equals(valorAlvo)) exemplos.add(i.clone());
		}

		Conjunto saida = new Conjunto(c.atributos(), exemplos);
		saida.remover(atributo);
		return saida;
	}

	//particiona o conjunto em cima do dominio um atributo, retornando n conjuntos, onde n = tamanho do dominio do atributo
	public static Conjunto []particao(Conjunto conjunto, ConjuntoDoAtributo atributo)
	{
		List<String> dominio = atributo.dominio(); //possiveis valores de 'a'
		int posicao = conjunto.atributos().indexOf(atributo); //indice do atributo 'a' no conjunto

		Conjunto []saida = new Conjunto[dominio.size()]; //subconjuntos
		for (int i = 0; i < dominio.size(); i++)
			saida[i] = new Conjunto(conjunto.atributos()); //inicializar subconjuntos

		for (Instancia exemplo : conjunto.exemplos())
		{
			for (int i = 0; i < dominio.size(); i++) //adicionar a instancia ao subconjunto correto
				if (exemplo.valor(posicao).equals(dominio.get(i))) saida[i].instancia(exemplo.clone());
		}

		for (int i = 0; i < dominio.size(); i++)
			saida[i].remover(atributo); //descartar o atributo já utilizado
		return saida;
	}

	public static double entropia(Conjunto c) //calcula entropia total do conjunto
	{
		if (c.exemplos().size() == 0) return 0;
		double positivos = 0;
		double negativos = 0;
		for (Instancia e : c.exemplos()) //contar exemplos
		{
			if (e.classe().equals(c.positivo())) ++positivos;
			else ++negativos;
		}
		return calcularEntropia(positivos, negativos);
	}

	public static double entropia(Conjunto c, ConjuntoDoAtributo a, String v)
	{
		//verificar se o atributo 'a' está no conjunto
		int indice_atributo = c.atributos().indexOf(a);
		if (indice_atributo == -1) return -1; //erro

		//verificar se 'v' existe no dominio de valores de 'a'
		int indice_valor = a.dominio().indexOf(v);
		if (indice_valor == -1) return -1;

		//contar exemplos positivos e negativos de 'v'
		int positivos = 0;
		int negativos = 0;
		for (Instancia e : c.exemplos())
		{
			//contar exemplos que contem valor 'v' no atributo 'a'
			if (e.valor(indice_atributo).equals(v))
			{
				if (e.classe().equals(c.positivo())) positivos++;
				else negativos++;
			}
		}
		//calcular a entropia
		return calcularEntropia(positivos, negativos);
	}

	public static double calcularEntropia (double numPositivo, double numNegativo) {
		double probPositivo = numPositivo/(numPositivo + numNegativo);
		double probNegativo = numNegativo/(numPositivo + numNegativo);
		double entropia;

		if ((numPositivo==0)||(numNegativo==0))
			entropia = 0.0;
		else if (numPositivo==numNegativo)
			entropia = 1.0;
		else
			entropia = -(probPositivo)*(Math.log(probPositivo)/Math.log(2))-(probNegativo)*(Math.log(probNegativo)/Math.log(2));

		return entropia;
	}

	public static double ganho(Conjunto c, ConjuntoDoAtributo a)
	{
		//verificar se o atributo 'a' está no conjunto
		int indice_atributo = c.atributos().indexOf(a);
		if (indice_atributo == -1) return -1; //erro

		//ganho é a entropia do total do conjunto - as entropias para cada valor ponderada pelas suas probabilidades
		double ganho = entropia(c);
		//calcular e subtrair da entropia total a entropia de cada valor do dominio de 'a', ponderado por sua probabilidade
		double numero_exemplos = c.exemplos().size();
		for (String v : a.dominio())
		{
			//calcular a probabilidade
			double exemplos = c.contarExemplos(a, v);
			double probabilidade = exemplos/numero_exemplos;

			ganho -= (entropia(c, a, v)*probabilidade); //precisa colocar entre parenteses
		}
		return ganho;
	}

	public static double ganho(Conjunto c, ArrayList<Instancia> valores, Double d)
	{
		int positivos = 0;
		int negativos = 0;
		//ganho é a entropia do total do conjunto - as entropias para cada valor ponderada pelas suas probabilidades
		for (Instancia e : valores) //contar exemplos
		{
			if (e.classe().equals(c.positivo())) ++positivos;
			else ++negativos;
		}
		double ganho = calcularEntropia(positivos, negativos);


		//calcular e subtrair da entropia total a entropia de cada valor do dominio de 'a', ponderado por sua probabilidade
		double numero_exemplos = valores.size();
		int menores_positivos = 0;
		int menores_negativos = 0;
		int maiores_positivos = 0;
		int maiores_negativos = 0;
		double exemplos_menores = 0;
		double exemplos_maiores = 0;
		String classe_positiva = c.positivo();

		for (Instancia i : valores)
		{
			//contar os exemplos
			if (Double.parseDouble(i.valor(0)) <= d)
			{
				if (i.classe().equals(classe_positiva)) menores_positivos++;
				else menores_negativos++;
				exemplos_menores++;
			}
			else
			{
				if (i.classe().equals(classe_positiva)) maiores_positivos++;
				else maiores_negativos++;
				exemplos_maiores++;
			}
		}
		double prob_menores = (exemplos_menores/numero_exemplos);
		double prob_maiores = (exemplos_maiores/numero_exemplos);
		//precisa colocar entre parenteses
		ganho = ((ganho)-(calcularEntropia(menores_positivos, menores_negativos)*prob_menores)-(calcularEntropia(maiores_positivos, maiores_negativos)*prob_maiores));
		return ganho;
	}

	//calcular o atributo com maior ganho em um conjunto
	public static ConjuntoDoAtributo maiorGanhoX(Conjunto c) {
		double maior = -1.0;
		ConjuntoDoAtributo resposta = c.atributos().get(0);
		//conjunto só tem um atributo ??
		if (c.atributos().size()-1 == 1) return resposta;
		for (int i = 0; i < c.atributos().size()-1; i++)
		{
			ConjuntoDoAtributo a = c.atributos().get(i);
			double ganho = ganho(c, a);
			ganho = EP.arredondar(ganho, 3); //para garantir a ordem em Java
			if (ganho > maior)
			{
				maior = ganho;
				resposta = a;
			}
		}
		return resposta;
	}

	//calcular o atributo com maior ganho em um conjunto
	public static ConjuntoDoAtributo maiorGanho(Conjunto c) {
		double maior = -1.0;
		ConjuntoDoAtributo resposta = new ConjuntoDoAtributo("Estranho");
		//conjunto só tem um atributo ??

		for (int i = 0; i < c.atributos().size()-1; i++)
		{
			ConjuntoDoAtributo a = c.atributos().get(i);
			double ganho = ganho(c, a);
			ganho = EP.arredondar(ganho, 3); //para garantir a ordem em Java
			if (ganho > maior)
			{
				maior = ganho;
				resposta = a;
			}
		}
		return resposta;
	}

	public static boolean discretizar(Conjunto c, ConjuntoDoAtributo a)
	{
		long tempoInicio = System.currentTimeMillis();
		//verificar se o atributo 'a' está no conjunto
		int indice_atributo = c.atributos().indexOf(a);
		if (indice_atributo == -1) return false; //erro

		//verificar se 'a' é continuo
		if (!a.ehContinuo()) return false;

		//copiar valores do atributo 'a' e suas respectivas classes
		ArrayList<Instancia> valores = new ArrayList<Instancia>();
		for (Instancia e : c.exemplos())
		{
			String []val = {e.valor(indice_atributo), e.classe()};
			valores.add(new Instancia(val));
		}

		//ordenar por valor
		Collections.sort(valores, new ComparadorAtributo());

		ArrayList<Double> candidatos = new ArrayList<Double>();
		//percorrer a lista buscando candidatos
		for (int i = 0; i < valores.size()-1; i++)
		{
			String classe_a = valores.get(i).classe();
			String classe_b = valores.get(i+1).classe();
			//se a classe de dois valores adjacentes é diferente, a média é um novo candidato
			if (classe_a.equals(classe_b) == false)
			{
				double valor_A = Double.parseDouble(valores.get(i).valor(0));
				double valor_B = Double.parseDouble(valores.get(i+1).valor(0));
				double candidato = (valor_A+valor_B)/2;
				candidatos.add(candidato);
			}
		}

		//calcular o ganho para cada um dos candidatos e selecionar o maior
		double maior = -1;
		double resposta = 0;
		for (Double d : candidatos)
		{
			double ganho = ganho(c, valores, d);
			if (ganho > maior)
			{
				maior = ganho;
				resposta = d;
			}
		}
		System.err.println("Candidatos: " + candidatos.size());

		//alterar o atributo com base na decisao
		ConjuntoDoAtributo discreto = new ConjuntoDoAtributo(a.nome(), false); //mesmo nome, continuo = false
		discreto.adicionarValorAoDominio("<="+resposta);
		discreto.adicionarValorAoDominio(">"+resposta);
		c.atributos().remove(indice_atributo);
		c.atributos().add(indice_atributo, discreto);

		//modificar as instancias
		for (Instancia i : c.exemplos())
		{
			double valor = Double.parseDouble(i.valor(indice_atributo));
			if (valor <= resposta) i.setvalor(indice_atributo, "<="+resposta);
			else i.setvalor(indice_atributo, ">"+resposta);
		}

		System.out.println("Resposta: " + resposta);

		long tempoFim = System.currentTimeMillis();
		System.err.println("Tempo de processamento: " + (tempoFim-tempoInicio)/1000);
		return true;
	}

	public static void gravar(Conjunto c, String nome_arquivo) throws IOException
	{
		long tempoInicio = System.currentTimeMillis();	//  tempo para ler e processar os dados

		//Abertura do arquivo
		FileOutputStream out = null;
		try
		{
			File arquivoSaida = new File(nome_arquivo);
			out = new FileOutputStream(arquivoSaida);
		}
		catch (Exception e)
		{
			System.err.println( "Não foi possível abrir o arquivo: " + nome_arquivo + "\n" + e);
			return;
		}

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));

		//imprimir cabeçalho
		bw.write(c.imprimirAtributos());
		for (Instancia i : c.exemplos())
		{
			bw.write("\n" + i.imprimir());
		}
		bw.close();

		long tempoFim = System.currentTimeMillis();
		System.err.println("Tempo de gravação: " + (tempoFim-tempoInicio)/1000);
	}

	public static String valorDaMaioria(Conjunto c)
	{
		//contar o numero de instancias de cada classe
		String a = c.classe().dominio().get(0);
		String b = c.classe().dominio().get(1);
		int n_a = 0;
		int n_b = 0;
		for (int i = 0; i < c.exemplos().size(); i++)
		{
			if (c.exemplos().get(i).classe().equals(a)) n_a++;
			else n_b++;
		}
		if (n_a >= n_b) return a;
		else return b;
	}

	public static String todosDaMesmaClasse(Conjunto c)
	{
		int nroExemplos = c.exemplos().size();
		if (nroExemplos == 0) return "";
		//pega o alvo do primeiro exemplo, se algum for diferente sai
		String classe = c.exemplos().get(0).classe();
		if (nroExemplos == 1) return classe;
		else
		{
			for (int i = 1; i < nroExemplos; i ++)
				if (!c.exemplos().get(i).classe().equals(classe)) return "";
		}
		return classe;
	}

	public static double arredondar(double valor, double casas)
	{
		int fator = (int) Math.pow(10, casas); //Ex.: 3 casas = 1000
		valor = valor*fator; //desloca o valor 3 casas para a frente
		valor = Math.round(valor); //arredonda a 4a. casa
		return valor/fator; //retorna o numero com n casas
	}
}
