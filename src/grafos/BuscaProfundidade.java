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

        int[] pilha = new int[quantidadeVertices];
        int[] proxima = new int[quantidadeVertices];

        visitar(inicio, pilha, proxima);

        // Visita tambem os vertices nao alcancados pela origem.
        for (int vertice = 0;
             vertice < quantidadeVertices;
             vertice++) {

            if (descoberta[vertice] == 0) {
                visitar(vertice, pilha, proxima);
            }
        }

        return new ArrayList<>(arvore);
    }

    private Aresta[][] prepararAdjacencias(Grafo grafo) {
        return ArestasDoGrafo.porOrigem(grafo);
    }

    // DFS iterativa para suportar caminhos longos.
    private void visitar(
            int origem,
            int[] pilha,
            int[] proxima
    ) {
        int topo = 0;

        pilha[0] = origem;
        descoberta[origem] = ++tempo;

        while (topo >= 0) {
            int atual = pilha[topo];

            if (proxima[atual] >= adjacencias[atual].length) {
                finalizacao[atual] = ++tempo;
                topo--;
                continue;
            }

            Aresta aresta =
                adjacencias[atual][proxima[atual]++];

            int destino = aresta.destino().id();

            if (descoberta[destino] == 0) {
                arvore.add(aresta);
                pai[destino] = atual;
                descoberta[destino] = ++tempo;
                pilha[++topo] = destino;

            } else if (finalizacao[destino] == 0) {
                retorno.add(aresta);

            } else if (descoberta[atual] < descoberta[destino]) {
                avanco.add(aresta);

            } else {
                cruzamento.add(aresta);
            }
        }
    }

    public Collection<Aresta> arestasDeArvoreResultado() {
        return new ArrayList<>(arvore);
    }

    public Collection<Aresta> arestasDeRetornoResultado() {
        return new ArrayList<>(retorno);
    }

    public Collection<Aresta> arestasDeAvancoResultado() {
        return new ArrayList<>(avanco);
    }

    public Collection<Aresta> arestasDeCruzamentoResultado() {
        return new ArrayList<>(cruzamento);
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