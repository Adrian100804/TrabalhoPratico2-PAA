package grafos;

import java.util.ArrayList;
import java.util.Arrays;

public class FluxoMaximo {
    private final int origem;
    private final int destino;

    private static class Arco {
        int destino;
        double capacidade;
        Arco inverso;

        Arco(int destino, double capacidade) {
            this.destino = destino;
            this.capacidade = capacidade;
        }
    }

    public FluxoMaximo(int origem, int destino) {
        this.origem = origem;
        this.destino = destino;
    }

    public double executar(Grafo grafo) {
        int n = grafo.numeroDeVertices();

        validarVertice(origem, n);
        validarVertice(destino, n);

        if (origem == destino) {
            throw new IllegalArgumentException(
                "A fonte e o sorvedouro devem ser diferentes."
            );
        }

        ArrayList<Arco>[] rede = prepararRede(grafo);

        int[] pai = new int[n];
        Arco[] caminho = new Arco[n];

        double fluxoTotal = 0;

        while (buscarCaminho(rede, pai, caminho)) {
            double aumento = Double.POSITIVE_INFINITY;

            for (int v = destino; v != origem; v = pai[v]) {
                aumento = Math.min(
                    aumento, caminho[v].capacidade
                );
            }

            if (!(aumento > 0) || !Double.isFinite(aumento)) {
                throw new IllegalStateException(
                    "Caminho aumentante invalido."
                );
            }

            if (fluxoTotal + aumento == fluxoTotal) {
                throw new IllegalStateException(
                    "Precisao insuficiente no calculo do fluxo."
                );
            }

            for (int v = destino; v != origem; v = pai[v]) {
                Arco arco = caminho[v];

                arco.capacidade -= aumento;
                arco.inverso.capacidade += aumento;
            }

            fluxoTotal += aumento;
        }

        return fluxoTotal;
    }

    private void validarVertice(int vertice, int n) {
        if (vertice < 0 || vertice >= n) {
            throw new IllegalArgumentException(
                "Vertice invalido: " + vertice
            );
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Arco>[] prepararRede(Grafo grafo) {
        int n = grafo.numeroDeVertices();

        ArrayList<Arco>[] rede =
            (ArrayList<Arco>[]) new ArrayList[n];

        for (int i = 0; i < n; i++) {
            rede[i] = new ArrayList<>();
        }

        for (Aresta aresta : ArestasDoGrafo.obter(grafo)) {
            int u = aresta.origem().id();
            int v = aresta.destino().id();
            double capacidade = aresta.peso();

            if (!Double.isFinite(capacidade) || capacidade < 0) {
                throw new IllegalArgumentException(
                    "As capacidades devem ser finitas e nao negativas."
                );
            }

            if (u == v || capacidade == 0) {
                continue;
            }

            Arco direto = new Arco(v, capacidade);
            Arco inverso = new Arco(u, 0);

            direto.inverso = inverso;
            inverso.inverso = direto;

            rede[u].add(direto);
            rede[v].add(inverso);
        }

        return rede;
    }

    private boolean buscarCaminho(
            ArrayList<Arco>[] rede,
            int[] pai,
            Arco[] caminho
    ) {
        int n = rede.length;

        Arrays.fill(pai, -1);

        int[] pilha = new int[n];
        int[] proximo = new int[n];

        int topo = 0;
        pilha[0] = origem;
        pai[origem] = origem;

        while (topo >= 0) {
            int atual = pilha[topo];

            if (atual == destino) {
                return true;
            }

            if (proximo[atual] >= rede[atual].size()) {
                topo--;
                continue;
            }

            Arco arco = rede[atual].get(proximo[atual]++);
            int vizinho = arco.destino;

            if (arco.capacidade > 0 && pai[vizinho] == -1) {
                pai[vizinho] = atual;
                caminho[vizinho] = arco;

                pilha[++topo] = vizinho;
            }
        }

        return false;
    }
}