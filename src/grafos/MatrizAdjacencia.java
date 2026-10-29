package grafos;

import java.util.ArrayList;

public class MatrizAdjacencia implements Grafo {

    private final Aresta[][] matriz;
    private int quantidadeArestas;

    public MatrizAdjacencia(int quantidadeVertices) {
        if (quantidadeVertices < 0) {
            throw new IllegalArgumentException(
                    "A quantidade de vertices nao pode ser negativa."
            );
        }

        matriz = new Aresta[quantidadeVertices][quantidadeVertices];
        quantidadeArestas = 0;
    }

    private int validarVertice(Vertice vertice) {
        if (vertice == null
                || vertice.id() < 0
                || vertice.id() >= matriz.length) {
            throw new IllegalArgumentException("Vertice invalido.");
        }

        return vertice.id();
    }

    private void validarPeso(double peso) {
        if (!Double.isFinite(peso)) {
            throw new IllegalArgumentException(
                    "O peso deve ser um numero finito."
            );
        }
    }

    private Aresta copiarAresta(Aresta aresta) {
        return new Aresta(
                new Vertice(aresta.origem().id()),
                new Vertice(aresta.destino().id()),
                aresta.peso()
        );
    }

    @Override
    public void adicionarAresta(
            Vertice origem,
            Vertice destino
    ) {
        adicionarAresta(origem, destino, 1.0);
    }

    @Override
    public void adicionarAresta(
            Vertice origem,
            Vertice destino,
            double peso
    ) {
        int u = validarVertice(origem);
        int v = validarVertice(destino);

        validarPeso(peso);

        if (matriz[u][v] != null) {
            throw new IllegalArgumentException(
                    "Ja existe uma aresta entre esses vertices."
            );
        }

        matriz[u][v] = new Aresta(
                new Vertice(u),
                new Vertice(v),
                peso
        );

        quantidadeArestas++;
    }

    @Override
    public boolean existeAresta(
            Vertice origem,
            Vertice destino
    ) {
        int u = validarVertice(origem);
        int v = validarVertice(destino);

        return matriz[u][v] != null;
    }

    @Override
    public int grauDoVertice(Vertice vertice) {
        int origem = validarVertice(vertice);
        int grau = 0;

        // Conta as arestas que saem do vertice.
        for (int destino = 0; destino < matriz.length; destino++) {
            if (matriz[origem][destino] != null) {
                grau++;
            }
        }

        return grau;
    }

    @Override
    public int numeroDeVertices() {
        return matriz.length;
    }

    @Override
    public int numeroDeArestas() {
        return quantidadeArestas;
    }

    @Override
    public ArrayList<Vertice> adjacentesDe(Vertice vertice) {
        int origem = validarVertice(vertice);
        ArrayList<Vertice> vizinhos = new ArrayList<>();

        for (int destino = 0; destino < matriz.length; destino++) {
            if (matriz[origem][destino] != null) {
                vizinhos.add(new Vertice(destino));
            }
        }

        return vizinhos;
    }

    @Override
    public void setarPeso(
            Vertice origem,
            Vertice destino,
            double peso
    ) {
        int u = validarVertice(origem);
        int v = validarVertice(destino);

        validarPeso(peso);

        if (matriz[u][v] == null) {
            throw new IllegalArgumentException(
                    "Nao existe aresta entre esses vertices."
            );
        }

        matriz[u][v].setarPeso(peso);
    }

    @Override
    public ArrayList<Aresta> arestasEntre(
            Vertice origem,
            Vertice destino
    ) {
        int u = validarVertice(origem);
        int v = validarVertice(destino);

        ArrayList<Aresta> resultado = new ArrayList<>();

        if (matriz[u][v] != null) {
            resultado.add(copiarAresta(matriz[u][v]));
        }

        return resultado;
    }

    @Override
    public ArrayList<Vertice> vertices() {
        ArrayList<Vertice> resultado = new ArrayList<>();

        for (int i = 0; i < matriz.length; i++) {
            resultado.add(new Vertice(i));
        }

        return resultado;
    }

    public Aresta[] todasArestas() {
        Aresta[] resultado = new Aresta[quantidadeArestas];
        int posicao = 0;

        for (int origem = 0; origem < matriz.length; origem++) {
            for (int destino = 0; destino < matriz.length; destino++) {
                if (matriz[origem][destino] != null) {
                    resultado[posicao++] =
                            copiarAresta(matriz[origem][destino]);
                }
            }
        }

        return resultado;
    }
}