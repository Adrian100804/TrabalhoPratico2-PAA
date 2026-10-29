package grafos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class BuscaLargura {

    private final int inicio;

    public int[] distanciaBfs;
    public int[] ordemDescobertaBfs;
    public int[] pai;

    public BuscaLargura(int inicio) {
        this.inicio = inicio;
    }

    public Collection<Aresta> executar(Grafo grafo) {
        int quantidadeVertices = grafo.numeroDeVertices();

        distanciaBfs = new int[quantidadeVertices];
        ordemDescobertaBfs = new int[quantidadeVertices];
        pai = new int[quantidadeVertices];

        Arrays.fill(distanciaBfs, -1);
        Arrays.fill(ordemDescobertaBfs, -1);
        Arrays.fill(pai, -1);

        ArrayList<Aresta> arvore = new ArrayList<>();

        if (quantidadeVertices == 0) {
            return arvore;
        }

        if (inicio < 0 || inicio >= quantidadeVertices) {
            throw new IllegalArgumentException(
                    "Vertice inicial invalido."
            );
        }

        Aresta[][] adjacencias = prepararAdjacencias(grafo);

        int[] fila = new int[quantidadeVertices];
        int frente = 0;
        int fim = 0;
        int ordem = 1;

        distanciaBfs[inicio] = 0;
        ordemDescobertaBfs[inicio] = ordem;

        fila[fim++] = inicio;

        while (frente < fim) {
            int origem = fila[frente++];

            for (Aresta aresta : adjacencias[origem]) {
                int destino = aresta.destino().id();

                // Somente vertices ainda nao descobertos entram na fila.
                if (distanciaBfs[destino] == -1) {
                    distanciaBfs[destino] =
                            distanciaBfs[origem] + 1;

                    pai[destino] = origem;
                    ordemDescobertaBfs[destino] = ++ordem;

                    fila[fim++] = destino;

                    arvore.add(aresta);
                }
            }
        }

        return arvore;
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