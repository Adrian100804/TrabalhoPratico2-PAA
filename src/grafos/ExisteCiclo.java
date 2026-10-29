package grafos;

import java.util.ArrayList;

public class ExisteCiclo {

    public boolean executar(Grafo grafo) {
        int quantidadeVertices = grafo.numeroDeVertices();

        Aresta[][] adjacencias = prepararAdjacencias(grafo);
        int[] estado = new int[quantidadeVertices];

        /*
         * 0 = nao visitado
         * 1 = em exploracao
         * 2 = finalizado
         */

        for (int vertice = 0;
             vertice < quantidadeVertices;
             vertice++) {

            if (estado[vertice] == 0) {
                if (visitar(vertice, adjacencias, estado)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean visitar(
            int origem,
            Aresta[][] adjacencias,
            int[] estado
    ) {
        int[] pilha = new int[estado.length];
        int[] proxima = new int[estado.length];

        int topo = 0;

        pilha[0] = origem;
        estado[origem] = 1;

        while (topo >= 0) {
            int atual = pilha[topo];

            if (proxima[atual] >= adjacencias[atual].length) {
                estado[atual] = 2;
                topo--;
                continue;
            }

            int vizinho =
                adjacencias[atual][proxima[atual]++]
                    .destino().id();

            if (estado[vizinho] == 1) {
                return true;
            }

            if (estado[vizinho] == 0) {
                estado[vizinho] = 1;
                pilha[++topo] = vizinho;
            }
        }

        return false;
    }

    private Aresta[][] prepararAdjacencias(Grafo grafo) {
        return ArestasDoGrafo.porOrigem(grafo);
    }
}