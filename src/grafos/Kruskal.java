package grafos;

import java.util.ArrayList;
import java.util.Collection;

public class Kruskal {

    public Collection<Aresta> executar(Grafo grafo) {
        int quantidadeVertices = grafo.numeroDeVertices();

        Aresta[] arestas = obterArestas(grafo);
        Aresta[] auxiliar = new Aresta[arestas.length];

        int quantidadeNecessaria =
            Math.max(0, quantidadeVertices - 1);

        Aresta[] escolhidas =
            new Aresta[quantidadeNecessaria];

        int[] representante =
            new int[quantidadeVertices];

        int[] altura =
            new int[quantidadeVertices];

        // Cada vertice comeca no proprio conjunto.
        for (int vertice = 0;
             vertice < quantidadeVertices;
             vertice++) {
            representante[vertice] = vertice;
        }

        ordenar(arestas, auxiliar, 0, arestas.length);

        int quantidadeEscolhida = 0;

        for (Aresta aresta : arestas) {
            int origem = aresta.origem().id();
            int destino = aresta.destino().id();

            if (unir(
                    origem,
                    destino,
                    representante,
                    altura
            )) {
                escolhidas[quantidadeEscolhida++] = aresta;

                if (quantidadeEscolhida == quantidadeNecessaria) {
                    break;
                }
            }
        }

        if (quantidadeEscolhida != quantidadeNecessaria) {
            throw new IllegalArgumentException(
                "Grafo desconexo: nao existe arvore geradora."
            );
        }

        ArrayList<Aresta> resultado =
            new ArrayList<>(quantidadeEscolhida);

        for (int i = 0; i < quantidadeEscolhida; i++) {
            resultado.add(escolhidas[i]);
        }

        return resultado;
    }

    private Aresta[] obterArestas(Grafo grafo) {
        return ArestasDoGrafo.obter(grafo);
    }

    // Merge Sort.
    private void ordenar(
            Aresta[] arestas,
            Aresta[] auxiliar,
            int inicio,
            int fim
    ) {
        if (fim - inicio < 2) {
            return;
        }

        int meio = inicio + (fim - inicio) / 2;

        ordenar(arestas, auxiliar, inicio, meio);
        ordenar(arestas, auxiliar, meio, fim);

        int esquerda = inicio;
        int direita = meio;

        for (int posicao = inicio;
             posicao < fim;
             posicao++) {

            if (esquerda < meio
                    && (direita == fim
                    || arestas[esquerda].peso()
                    <= arestas[direita].peso())) {

                auxiliar[posicao] = arestas[esquerda++];
            } else {
                auxiliar[posicao] = arestas[direita++];
            }
        }

        for (int posicao = inicio;
             posicao < fim;
             posicao++) {
            arestas[posicao] = auxiliar[posicao];
        }
    }

    private int encontrarRepresentante(
            int vertice,
            int[] representante
    ) {
        while (vertice != representante[vertice]) {
            representante[vertice] =
                representante[representante[vertice]];

            vertice = representante[vertice];
        }

        return vertice;
    }

    private boolean unir(
            int origem,
            int destino,
            int[] representante,
            int[] altura
    ) {
        int raizOrigem =
            encontrarRepresentante(origem, representante);

        int raizDestino =
            encontrarRepresentante(destino, representante);

        if (raizOrigem == raizDestino) {
            return false;
        }

        if (altura[raizOrigem] < altura[raizDestino]) {
            representante[raizOrigem] = raizDestino;

        } else if (altura[raizOrigem] > altura[raizDestino]) {
            representante[raizDestino] = raizOrigem;

        } else {
            representante[raizDestino] = raizOrigem;
            altura[raizOrigem]++;
        }

        return true;
    }

    public double custo(
            Grafo grafo,
            Collection<Aresta> arestas
    ) throws Exception {
        int quantidadeVertices = grafo.numeroDeVertices();

        int quantidadeNecessaria =
            Math.max(0, quantidadeVertices - 1);

        if (arestas == null
                || arestas.size() != quantidadeNecessaria) {
            throw new IllegalArgumentException(
                "Quantidade de arestas invalida para uma arvore."
            );
        }

        int[] representante =
            new int[quantidadeVertices];

        int[] altura =
            new int[quantidadeVertices];

        for (int vertice = 0;
             vertice < quantidadeVertices;
             vertice++) {
            representante[vertice] = vertice;
        }

        double total = 0;

        for (Aresta aresta : arestas) {
            validarAresta(grafo, aresta);

            if (!unir(
                    aresta.origem().id(),
                    aresta.destino().id(),
                    representante,
                    altura
            )) {
                throw new IllegalArgumentException(
                    "As arestas informadas formam um ciclo."
                );
            }

            total += aresta.peso();
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