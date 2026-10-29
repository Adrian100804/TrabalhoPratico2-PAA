package grafos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class BuscaProfundidade {

    private final int inicio;
    private int tempo;

    public int[] descoberta;
    public int[] finalizacao;
    public int[] pai;

    private Aresta[][] adjacencias;

    private ArrayList<Aresta> arvore;
    private ArrayList<Aresta> retorno;
    private ArrayList<Aresta> avanco;
    private ArrayList<Aresta> cruzamento;

    public BuscaProfundidade(int inicio) {
        this.inicio = inicio;
    }

    public Collection<Aresta> executar(Grafo grafo) {
        int quantidadeVertices = grafo.numeroDeVertices();

        descoberta = new int[quantidadeVertices];
        finalizacao = new int[quantidadeVertices];
        pai = new int[quantidadeVertices];

        Arrays.fill(pai, -1);

        arvore = new ArrayList<>();
        retorno = new ArrayList<>();
        avanco = new ArrayList<>();
        cruzamento = new ArrayList<>();

        tempo = 0;

        if (quantidadeVertices == 0) {
            return new ArrayList<>(arvore);
        }

        if (inicio < 0 || inicio >= quantidadeVertices) {
            throw new IllegalArgumentException(
                    "Vertice inicial invalido."
            );
        }

        adjacencias = prepararAdjacencias(grafo);

        visitar(inicio);

        // Visita tambem os vertices nao alcancados pela origem.
        for (int vertice = 0;
             vertice < quantidadeVertices;
             vertice++) {

            if (descoberta[vertice] == 0) {
                visitar(vertice);
            }
        }

        return new ArrayList<>(arvore);
    }

    private Aresta[][] prepararAdjacencias(Grafo grafo) {
        int quantidadeVertices = grafo.numeroDeVertices();

        Aresta[][] resultado = new Aresta[quantidadeVertices][];

        try {
            for (int origem = 0;
                 origem < quantidadeVertices;
                 origem++) {

                Vertice verticeOrigem = new Vertice(origem);
                ArrayList<Aresta> saidas = new ArrayList<>();

                for (Vertice destino :
                        grafo.adjacentesDe(verticeOrigem)) {

                    saidas.addAll(
                            grafo.arestasEntre(
                                    verticeOrigem,
                                    destino
                            )
                    );
                }

                resultado[origem] = saidas.toArray(new Aresta[0]);
            }

        } catch (Exception erro) {
            throw new IllegalArgumentException(
                    "Nao foi possivel consultar as arestas do grafo.",
                    erro
            );
        }

        return resultado;
    }

    private void visitar(int origem) {
        descoberta[origem] = ++tempo;

        for (Aresta aresta : adjacencias[origem]) {
            int destino = aresta.destino().id();

            if (descoberta[destino] == 0) {
                // O destino ainda nao foi descoberto.
                arvore.add(aresta);
                pai[destino] = origem;

                visitar(destino);

            } else if (finalizacao[destino] == 0) {
                // O destino ainda esta em exploracao.
                retorno.add(aresta);

            } else if (descoberta[origem] < descoberta[destino]) {
                // O destino e um descendente ja finalizado.
                avanco.add(aresta);

            } else {
                // O destino pertence a outro ramo ja explorado.
                cruzamento.add(aresta);
            }
        }

        finalizacao[origem] = ++tempo;
    }

    public Collection<Aresta> arestasDeArvore(Grafo grafo) {
        executar(grafo);
        return new ArrayList<>(arvore);
    }

    public Collection<Aresta> arestasDeRetorno(Grafo grafo) {
        executar(grafo);
        return new ArrayList<>(retorno);
    }

    public Collection<Aresta> arestasDeAvanco(Grafo grafo) {
        executar(grafo);
        return new ArrayList<>(avanco);
    }

    public Collection<Aresta> arestasDeCruzamento(Grafo grafo) {
        executar(grafo);
        return new ArrayList<>(cruzamento);
    }
}