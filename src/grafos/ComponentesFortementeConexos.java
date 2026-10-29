package grafos;

import java.util.ArrayList;
import java.util.Arrays;

public class ComponentesFortementeConexos {

    public int[] componente;

    public Grafo executar(Grafo grafo) {
        int quantidadeVertices = grafo.numeroDeVertices();

        Aresta[][] adjacencias = prepararAdjacencias(grafo);

        boolean[] visitado = new boolean[quantidadeVertices];
        ArrayList<Integer> ordem = new ArrayList<>();

        // Primeira busca: registra a ordem de finalizacao.
        for (int vertice = 0;
             vertice < quantidadeVertices;
             vertice++) {

            if (!visitado[vertice]) {
                registrarFinalizacao(
                        vertice,
                        adjacencias,
                        visitado,
                        ordem
                );
            }
        }

        // Inverte o sentido de todas as arestas.
        Aresta[][] transposto = transpor(adjacencias);

        componente = new int[quantidadeVertices];
        Arrays.fill(componente, -1);

        int quantidadeComponentes = 0;

        // Segunda busca: percorre a ordem inversa de finalizacao.
        for (int i = ordem.size() - 1; i >= 0; i--) {
            int vertice = ordem.get(i);

            if (componente[vertice] == -1) {
                marcarComponente(
                        vertice,
                        transposto,
                        quantidadeComponentes
                );

                quantidadeComponentes++;
            }
        }

        return construirGrafoReduzido(
                adjacencias,
                quantidadeComponentes
        );
    }

    private void registrarFinalizacao(
            int origem,
            Aresta[][] adjacencias,
            boolean[] visitado,
            ArrayList<Integer> ordem
    ) {
        visitado[origem] = true;

        for (Aresta aresta : adjacencias[origem]) {
            int destino = aresta.destino().id();

            if (!visitado[destino]) {
                registrarFinalizacao(
                        destino,
                        adjacencias,
                        visitado,
                        ordem
                );
            }
        }

        // Adiciona somente depois de explorar os destinos.
        ordem.add(origem);
    }

    private Aresta[][] transpor(Aresta[][] adjacencias) {
        int quantidadeVertices = adjacencias.length;

        int[] quantidadeEntradas = new int[quantidadeVertices];

        // Conta quantas arestas cada vertice recebera no transposto.
        for (Aresta[] saidas : adjacencias) {
            for (Aresta aresta : saidas) {
                quantidadeEntradas[aresta.destino().id()]++;
            }
        }

        Aresta[][] transposto = new Aresta[quantidadeVertices][];

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

                transposto[destino][posicao[destino]++] = inversa;
            }
        }

        return transposto;
    }

    private void marcarComponente(
            int origem,
            Aresta[][] transposto,
            int grupo
    ) {
        componente[origem] = grupo;

        for (Aresta aresta : transposto[origem]) {
            int destino = aresta.destino().id();

            if (componente[destino] == -1) {
                marcarComponente(
                        destino,
                        transposto,
                        grupo
                );
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

                // Arestas internas nao aparecem no grafo reduzido.
                if (grupoOrigem != grupoDestino) {
                    Vertice origem = new Vertice(grupoOrigem);
                    Vertice destino = new Vertice(grupoDestino);

                    if (!reduzido.existeAresta(origem, destino)) {
                        // Peso 1 representa apenas conectividade.
                        reduzido.adicionarAresta(origem, destino);
                    }
                }
            }
        }

        return reduzido;
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