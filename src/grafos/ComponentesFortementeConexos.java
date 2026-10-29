package grafos;

import java.util.ArrayList;
import java.util.Arrays;

public class ComponentesFortementeConexos {

    public int[] componente;

    public Grafo executar(Grafo grafo) {
        int quantidadeVertices = grafo.numeroDeVertices();

        Aresta[][] adjacencias = prepararAdjacencias(grafo);

        boolean[] visitado = new boolean[quantidadeVertices];
        int[] ordem = new int[quantidadeVertices];
        int[] pilha = new int[quantidadeVertices];
        int[] proxima = new int[quantidadeVertices];

        int totalFinalizados = 0;

        for (int vertice = 0;
             vertice < quantidadeVertices;
             vertice++) {

            if (!visitado[vertice]) {
                totalFinalizados = registrarFinalizacao(
                    vertice,
                    adjacencias,
                    visitado,
                    ordem,
                    totalFinalizados,
                    pilha,
                    proxima
                );
            }
        }

        Aresta[][] transposto = transpor(adjacencias);

        componente = new int[quantidadeVertices];
        Arrays.fill(componente, -1);

        int quantidadeComponentes = 0;

        for (int i = totalFinalizados - 1; i >= 0; i--) {
            int vertice = ordem[i];

            if (componente[vertice] == -1) {
                marcarComponente(
                    vertice,
                    transposto,
                    quantidadeComponentes,
                    pilha
                );

                quantidadeComponentes++;
            }
        }

        return construirGrafoReduzido(
            adjacencias,
            quantidadeComponentes
        );
    }

    private int registrarFinalizacao(
            int origem,
            Aresta[][] adjacencias,
            boolean[] visitado,
            int[] ordem,
            int total,
            int[] pilha,
            int[] proxima
    ) {
        int topo = 0;

        pilha[0] = origem;
        visitado[origem] = true;

        while (topo >= 0) {
            int atual = pilha[topo];

            if (proxima[atual] >= adjacencias[atual].length) {
                ordem[total++] = atual;
                topo--;
                continue;
            }

            int vizinho =
                adjacencias[atual][proxima[atual]++]
                    .destino().id();

            if (!visitado[vizinho]) {
                visitado[vizinho] = true;
                pilha[++topo] = vizinho;
            }
        }

        return total;
    }

    private Aresta[][] transpor(Aresta[][] adjacencias) {
        int quantidadeVertices = adjacencias.length;

        int[] quantidadeEntradas =
            new int[quantidadeVertices];

        for (Aresta[] saidas : adjacencias) {
            for (Aresta aresta : saidas) {
                quantidadeEntradas[aresta.destino().id()]++;
            }
        }

        Aresta[][] transposto =
            new Aresta[quantidadeVertices][];

        for (int vertice = 0;
             vertice < quantidadeVertices;
             vertice++) {

            transposto[vertice] =
                new Aresta[quantidadeEntradas[vertice]];
        }

        int[] posicao = new int[quantidadeVertices];

        for (Aresta[] saidas : adjacencias) {
            for (Aresta aresta : saidas) {
                int origem = aresta.origem().id();
                int destino = aresta.destino().id();

                Aresta inversa = new Aresta(
                    new Vertice(destino),
                    new Vertice(origem),
                    aresta.peso()
                );

                transposto[destino][posicao[destino]++] =
                    inversa;
            }
        }

        return transposto;
    }

    private void marcarComponente(
            int origem,
            Aresta[][] transposto,
            int grupo,
            int[] pilha
    ) {
        int topo = 0;

        pilha[0] = origem;
        componente[origem] = grupo;

        while (topo >= 0) {
            int atual = pilha[topo--];

            for (Aresta aresta : transposto[atual]) {
                int vizinho = aresta.destino().id();

                if (componente[vizinho] == -1) {
                    componente[vizinho] = grupo;
                    pilha[++topo] = vizinho;
                }
            }
        }
    }

    private Grafo construirGrafoReduzido(
            Aresta[][] adjacencias,
            int quantidadeComponentes
    ) {
        ListaAdjacencia reduzido =
            new ListaAdjacencia(quantidadeComponentes);

        for (Aresta[] saidas : adjacencias) {
            for (Aresta aresta : saidas) {
                int grupoOrigem =
                    componente[aresta.origem().id()];

                int grupoDestino =
                    componente[aresta.destino().id()];

                if (grupoOrigem != grupoDestino) {
                    Vertice origem = new Vertice(grupoOrigem);
                    Vertice destino = new Vertice(grupoDestino);

                    if (!reduzido.existeAresta(origem, destino)) {
                        reduzido.adicionarAresta(origem, destino);
                    }
                }
            }
        }

        return reduzido;
    }

    private Aresta[][] prepararAdjacencias(Grafo grafo) {
        return ArestasDoGrafo.porOrigem(grafo);
    }
}