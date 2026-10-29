package grafos;

import java.util.ArrayList;
import java.util.Arrays;

public class Dijkstra {

    public ArrayList<Aresta> executar(
            Grafo grafo,
            Vertice origem,
            Vertice destino
    ) {
        int quantidadeVertices = grafo.numeroDeVertices();

        int s = validarVertice(origem, quantidadeVertices);
        int t = validarVertice(destino, quantidadeVertices);

        Aresta[] arestas = obterArestas(grafo);

        for (Aresta aresta : arestas) {
            if (aresta.peso() < 0) {
                throw new IllegalArgumentException(
                    "Dijkstra nao admite pesos negativos."
                );
            }
        }

        Aresta[][] adjacencias = prepararAdjacencias(
                quantidadeVertices,
                arestas
        );

        double[] distancia = new double[quantidadeVertices];
        boolean[] visitado = new boolean[quantidadeVertices];
        Aresta[] anterior = new Aresta[quantidadeVertices];

        Arrays.fill(distancia, Double.POSITIVE_INFINITY);
        distancia[s] = 0;

        for (int rodada = 0;
             rodada < quantidadeVertices;
             rodada++) {

            int atual = -1;

            for (int vertice = 0;
                 vertice < quantidadeVertices;
                 vertice++) {

                if (!visitado[vertice]
                        && (atual == -1
                        || distancia[vertice] < distancia[atual])) {
                    atual = vertice;
                }
            }

            if (atual == -1
                    || Double.isInfinite(distancia[atual])) {
                break;
            }

            visitado[atual] = true;

            if (atual == t) {
                break;
            }

            for (Aresta aresta : adjacencias[atual]) {
                int vizinho = aresta.destino().id();

                double novaDistancia =
                    distancia[atual] + aresta.peso();

                if (!visitado[vizinho]
                        && novaDistancia < distancia[vizinho]) {
                    distancia[vizinho] = novaDistancia;
                    anterior[vizinho] = aresta;
                }
            }
        }

        if (Double.isInfinite(distancia[t])) {
            throw new IllegalArgumentException(
                "Nao existe caminho entre os vertices informados."
            );
        }

        Aresta[] caminhoInvertido =
            new Aresta[quantidadeVertices];

        int quantidadeArestas = 0;
        int atual = t;

        while (atual != s) {
            Aresta aresta = anterior[atual];

            caminhoInvertido[quantidadeArestas++] = aresta;
            atual = aresta.origem().id();
        }

        ArrayList<Aresta> caminho =
            new ArrayList<>(quantidadeArestas);

        for (int i = quantidadeArestas - 1; i >= 0; i--) {
            caminho.add(caminhoInvertido[i]);
        }

        return caminho;
    }

    private int validarVertice(
            Vertice vertice,
            int quantidadeVertices
    ) {
        if (vertice == null
                || vertice.id() < 0
                || vertice.id() >= quantidadeVertices) {
            throw new IllegalArgumentException(
                "Vertice invalido."
            );
        }

        return vertice.id();
    }

    private Aresta[] obterArestas(Grafo grafo) {
        return ArestasDoGrafo.obter(grafo);
    }

    private Aresta[][] prepararAdjacencias(
            int quantidadeVertices,
            Aresta[] arestas
    ) {
        int[] quantidadeSaidas =
            new int[quantidadeVertices];

        for (Aresta aresta : arestas) {
            quantidadeSaidas[aresta.origem().id()]++;
        }

        Aresta[][] resultado =
            new Aresta[quantidadeVertices][];

        for (int vertice = 0;
             vertice < quantidadeVertices;
             vertice++) {
            resultado[vertice] =
                new Aresta[quantidadeSaidas[vertice]];
        }

        int[] posicao = new int[quantidadeVertices];

        for (Aresta aresta : arestas) {
            int origem = aresta.origem().id();

            resultado[origem][posicao[origem]++] = aresta;
        }

        return resultado;
    }

    public double custo(
            Grafo grafo,
            ArrayList<Aresta> caminho,
            Vertice origem,
            Vertice destino
    ) throws Exception {
        int quantidadeVertices = grafo.numeroDeVertices();

        int s = validarVertice(origem, quantidadeVertices);
        int t = validarVertice(destino, quantidadeVertices);

        if (caminho == null) {
            throw new IllegalArgumentException(
                "Caminho nulo."
            );
        }

        int atual = s;
        double total = 0;

        for (Aresta aresta : caminho) {
            validarAresta(grafo, aresta);

            if (aresta.origem().id() != atual) {
                throw new IllegalArgumentException(
                    "As arestas nao formam um caminho continuo."
                );
            }

            total += aresta.peso();
            atual = aresta.destino().id();
        }

        if (atual != t) {
            throw new IllegalArgumentException(
                "O caminho nao termina no destino informado."
            );
        }

        return total;
    }

    private void validarAresta(
            Grafo grafo,
            Aresta aresta
    ) throws Exception {
        if (aresta == null) {
            throw new IllegalArgumentException(
                "Aresta nula."
            );
        }

        for (Aresta original :
                grafo.arestasEntre(
                    aresta.origem(),
                    aresta.destino()
                )) {

            if (Double.compare(
                    original.peso(),
                    aresta.peso()
            ) == 0) {
                return;
            }
        }

        throw new IllegalArgumentException(
            "Aresta ou peso nao pertence ao grafo."
        );
    }
}