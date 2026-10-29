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
        estado[origem] = 1;

        for (Aresta aresta : adjacencias[origem]) {
            int destino = aresta.destino().id();

            // Uma aresta para um vertice ainda em exploracao
            // fecha um ciclo no grafo orientado.
            if (estado[destino] == 1) {
                return true;
            }

            if (estado[destino] == 0) {
                if (visitar(destino, adjacencias, estado)) {
                    return true;
                }
            }
        }

        estado[origem] = 2;

        return false;
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
}