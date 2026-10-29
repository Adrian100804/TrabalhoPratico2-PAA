package grafos;

import java.util.Arrays;

public class FluxoMaximo {

    private final int origem;
    private final int destino;

    public FluxoMaximo(int origem, int destino) {
        this.origem = origem;
        this.destino = destino;
    }

    public double executar(Grafo grafo) {
        int quantidadeVertices = grafo.numeroDeVertices();

        validarVertice(origem, quantidadeVertices);
        validarVertice(destino, quantidadeVertices);

        if (origem == destino) {
            throw new IllegalArgumentException(
                    "A fonte e o sorvedouro devem ser diferentes."
            );
        }

        double[][] residual = prepararResidual(grafo);

        int[] pai = new int[quantidadeVertices];
        double fluxoTotal = 0;

        while (buscarCaminho(residual, pai)) {
            double aumento = Double.POSITIVE_INFINITY;

            // Procura a menor capacidade disponivel no caminho.
            int atual = destino;

            while (atual != origem) {
                int anterior = pai[atual];

                aumento = Math.min(
                        aumento,
                        residual[anterior][atual]
                );

                atual = anterior;
            }

            // Atualiza as capacidades residuais.
            atual = destino;

            while (atual != origem) {
                int anterior = pai[atual];

                residual[anterior][atual] -= aumento;
                residual[atual][anterior] += aumento;

                atual = anterior;
            }

            fluxoTotal += aumento;
        }

        return fluxoTotal;
    }

    private void validarVertice(
            int vertice,
            int quantidadeVertices
    ) {
        if (vertice < 0 || vertice >= quantidadeVertices) {
            throw new IllegalArgumentException(
                    "Vertice invalido: " + vertice
            );
        }
    }

    private double[][] prepararResidual(Grafo grafo) {
        int quantidadeVertices = grafo.numeroDeVertices();

        double[][] residual =
                new double[quantidadeVertices][quantidadeVertices];

        try {
            for (int u = 0; u < quantidadeVertices; u++) {
                Vertice verticeOrigem = new Vertice(u);

                for (Vertice verticeDestino :
                        grafo.adjacentesDe(verticeOrigem)) {

                    for (Aresta aresta :
                            grafo.arestasEntre(
                                    verticeOrigem,
                                    verticeDestino
                            )) {

                        double capacidade = aresta.peso();
                        int v = aresta.destino().id();

                        if (!Double.isFinite(capacidade)
                                || capacidade < 0) {
                            throw new IllegalArgumentException(
                                    "As capacidades devem ser finitas "
                                            + "e nao negativas."
                            );
                        }

                        // Lacos nao contribuem para o fluxo.
                        if (u != v) {
                            residual[u][v] += capacidade;
                        }
                    }
                }
            }

        } catch (Exception erro) {
            throw new IllegalArgumentException(
                    "Nao foi possivel preparar as capacidades: "
                            + erro.getMessage(),
                    erro
            );
        }

        return residual;
    }

    private boolean buscarCaminho(
            double[][] residual,
            int[] pai
    ) {
        int quantidadeVertices = residual.length;

        Arrays.fill(pai, -1);

        int[] fila = new int[quantidadeVertices];
        int frente = 0;
        int fim = 0;

        fila[fim++] = origem;

        // Marca a fonte como descoberta.
        pai[origem] = origem;

        while (frente < fim) {
            int atual = fila[frente++];

            for (int vizinho = 0;
                 vizinho < quantidadeVertices;
                 vizinho++) {

                if (pai[vizinho] == -1
                        && residual[atual][vizinho] > 0) {

                    pai[vizinho] = atual;

                    if (vizinho == destino) {
                        return true;
                    }

                    fila[fim++] = vizinho;
                }
            }
        }

        return false;
    }
}