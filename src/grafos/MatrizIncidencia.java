package grafos;

import java.util.ArrayList;
import java.util.Arrays;

public class MatrizIncidencia implements Grafo {

    private final int quantidadeVertices;

    private byte[][] matriz;
    private double[] pesos;

    private int quantidadeArestas;

    public MatrizIncidencia(int quantidadeVertices) {
        this(quantidadeVertices, 4);
    }

    public MatrizIncidencia(
            int quantidadeVertices,
            int capacidadeInicial
    ) {
        if (quantidadeVertices < 0) {
            throw new IllegalArgumentException(
                    "A quantidade de vertices nao pode ser negativa."
            );
        }

        if (capacidadeInicial < 0) {
            throw new IllegalArgumentException(
                    "A capacidade inicial nao pode ser negativa."
            );
        }

        this.quantidadeVertices = quantidadeVertices;

        matriz = new byte[quantidadeVertices][capacidadeInicial];
        pesos = new double[capacidadeInicial];

        quantidadeArestas = 0;
    }

    private int validarVertice(Vertice vertice) {
        if (vertice == null
                || vertice.id() < 0
                || vertice.id() >= quantidadeVertices) {
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

    private void garantirEspaco() {
        if (quantidadeArestas < pesos.length) {
            return;
        }

        int novaCapacidade = Math.max(1, pesos.length * 2);

        pesos = Arrays.copyOf(pesos, novaCapacidade);

        for (int vertice = 0;
             vertice < quantidadeVertices;
             vertice++) {
            matriz[vertice] = Arrays.copyOf(
                    matriz[vertice],
                    novaCapacidade
            );
        }
    }

    private int buscarColuna(int origem, int destino) {
        for (int coluna = 0;
             coluna < quantidadeArestas;
             coluna++) {

            if (origem == destino) {
                if (matriz[origem][coluna] == 2) {
                    return coluna;
                }

            } else if (matriz[origem][coluna] == -1
                    && matriz[destino][coluna] == 1) {
                return coluna;
            }
        }

        return -1;
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

        if (buscarColuna(u, v) != -1) {
            throw new IllegalArgumentException(
                    "Ja existe uma aresta entre esses vertices."
            );
        }

        garantirEspaco();

        int coluna = quantidadeArestas;

        if (u == v) {
            matriz[u][coluna] = 2;
        } else {
            matriz[u][coluna] = -1;
            matriz[v][coluna] = 1;
        }

        pesos[coluna] = peso;

        quantidadeArestas++;
    }

    @Override
    public boolean existeAresta(
            Vertice origem,
            Vertice destino
    ) {
        int u = validarVertice(origem);
        int v = validarVertice(destino);

        return buscarColuna(u, v) != -1;
    }

    @Override
    public int grauDoVertice(Vertice vertice) {
        int origem = validarVertice(vertice);
        int grau = 0;

        // Grau de saida: cada laco conta uma vez.
        for (int coluna = 0;
             coluna < quantidadeArestas;
             coluna++) {
            if (matriz[origem][coluna] == -1
                    || matriz[origem][coluna] == 2) {
                grau++;
            }
        }

        return grau;
    }

    @Override
    public int numeroDeVertices() {
        return quantidadeVertices;
    }

    @Override
    public int numeroDeArestas() {
        return quantidadeArestas;
    }

    @Override
    public ArrayList<Vertice> adjacentesDe(Vertice vertice) {
        int origem = validarVertice(vertice);
        ArrayList<Vertice> vizinhos = new ArrayList<>();

        for (int coluna = 0;
             coluna < quantidadeArestas;
             coluna++) {

            if (matriz[origem][coluna] == 2) {
                vizinhos.add(new Vertice(origem));

            } else if (matriz[origem][coluna] == -1) {
                for (int destino = 0;
                     destino < quantidadeVertices;
                     destino++) {
                    if (matriz[destino][coluna] == 1) {
                        vizinhos.add(new Vertice(destino));
                        break;
                    }
                }
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

        int coluna = buscarColuna(u, v);

        if (coluna == -1) {
            throw new IllegalArgumentException(
                    "Nao existe aresta entre esses vertices."
            );
        }

        pesos[coluna] = peso;
    }

    @Override
    public ArrayList<Aresta> arestasEntre(
            Vertice origem,
            Vertice destino
    ) {
        int u = validarVertice(origem);
        int v = validarVertice(destino);

        ArrayList<Aresta> resultado = new ArrayList<>();
        int coluna = buscarColuna(u, v);

        if (coluna != -1) {
            resultado.add(new Aresta(
                    new Vertice(u),
                    new Vertice(v),
                    pesos[coluna]
            ));
        }

        return resultado;
    }

    @Override
    public ArrayList<Vertice> vertices() {
        ArrayList<Vertice> resultado = new ArrayList<>();

        for (int i = 0; i < quantidadeVertices; i++) {
            resultado.add(new Vertice(i));
        }

        return resultado;
    }

    public Aresta[] todasArestas() {
        Aresta[] resultado = new Aresta[quantidadeArestas];

        for (int coluna = 0;
             coluna < quantidadeArestas;
             coluna++) {

            int origem = -1;
            int destino = -1;

            for (int vertice = 0;
                 vertice < quantidadeVertices;
                 vertice++) {

                if (matriz[vertice][coluna] == -1) {
                    origem = vertice;

                } else if (matriz[vertice][coluna] == 1) {
                    destino = vertice;

                } else if (matriz[vertice][coluna] == 2) {
                    origem = vertice;
                    destino = vertice;
                    break;
                }
            }

            resultado[coluna] = new Aresta(
                    new Vertice(origem),
                    new Vertice(destino),
                    pesos[coluna]
            );
        }

        return resultado;
    }
}