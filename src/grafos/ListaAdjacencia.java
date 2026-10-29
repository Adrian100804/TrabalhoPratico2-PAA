package grafos;

import java.util.ArrayList;

public class ListaAdjacencia implements Grafo {

    private final ArrayList<ArrayList<Aresta>> listas;
    private int quantidadeArestas;

    public ListaAdjacencia(int quantidadeVertices) {
        if (quantidadeVertices < 0) {
            throw new IllegalArgumentException(
                    "A quantidade de vertices nao pode ser negativa."
            );
        }

        listas = new ArrayList<>();

        for (int i = 0; i < quantidadeVertices; i++) {
            listas.add(new ArrayList<Aresta>());
        }

        quantidadeArestas = 0;
    }

    private int validarVertice(Vertice vertice) {
        if (vertice == null
                || vertice.id() < 0
                || vertice.id() >= listas.size()) {
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

    private Aresta buscarAresta(int origem, int destino) {
        for (Aresta aresta : listas.get(origem)) {
            if (aresta.destino().id() == destino) {
                return aresta;
            }
        }

        return null;
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

        if (buscarAresta(u, v) != null) {
            throw new IllegalArgumentException(
                    "Ja existe uma aresta entre esses vertices."
            );
        }

        Aresta novaAresta = new Aresta(
                new Vertice(u),
                new Vertice(v),
                peso
        );

        listas.get(u).add(novaAresta);
        quantidadeArestas++;
    }

    @Override
    public boolean existeAresta(
            Vertice origem,
            Vertice destino
    ) {
        int u = validarVertice(origem);
        int v = validarVertice(destino);

        return buscarAresta(u, v) != null;
    }

    @Override
    public int grauDoVertice(Vertice vertice) {
        int origem = validarVertice(vertice);

        // Grau de saida: quantidade de arestas na lista.
        return listas.get(origem).size();
    }

    @Override
    public int numeroDeVertices() {
        return listas.size();
    }

    @Override
    public int numeroDeArestas() {
        return quantidadeArestas;
    }

    @Override
    public ArrayList<Vertice> adjacentesDe(Vertice vertice) {
        int origem = validarVertice(vertice);
        ArrayList<Vertice> vizinhos = new ArrayList<>();

        for (Aresta aresta : listas.get(origem)) {
            vizinhos.add(
                    new Vertice(aresta.destino().id())
            );
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

        Aresta aresta = buscarAresta(u, v);

        if (aresta == null) {
            throw new IllegalArgumentException(
                    "Nao existe aresta entre esses vertices."
            );
        }

        aresta.setarPeso(peso);
    }

    @Override
    public ArrayList<Aresta> arestasEntre(
            Vertice origem,
            Vertice destino
    ) {
        int u = validarVertice(origem);
        int v = validarVertice(destino);

        ArrayList<Aresta> resultado = new ArrayList<>();
        Aresta aresta = buscarAresta(u, v);

        if (aresta != null) {
            resultado.add(copiarAresta(aresta));
        }

        return resultado;
    }

    @Override
    public ArrayList<Vertice> vertices() {
        ArrayList<Vertice> resultado = new ArrayList<>();

        for (int i = 0; i < listas.size(); i++) {
            resultado.add(new Vertice(i));
        }

        return resultado;
    }

    public Aresta[] todasArestas() {
        Aresta[] resultado = new Aresta[quantidadeArestas];
        int posicao = 0;

        for (ArrayList<Aresta> lista : listas) {
            for (Aresta aresta : lista) {
                resultado[posicao++] = copiarAresta(aresta);
            }
        }

        return resultado;
    }
}