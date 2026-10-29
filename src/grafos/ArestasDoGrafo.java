package grafos;

public class ArestasDoGrafo {

    public static Aresta[] obter(Grafo grafo) {
        if (grafo instanceof ListaAdjacencia) {
            return ((ListaAdjacencia) grafo).todasArestas();
        }

        if (grafo instanceof MatrizAdjacencia) {
            return ((MatrizAdjacencia) grafo).todasArestas();
        }

        if (grafo instanceof MatrizIncidencia) {
            return ((MatrizIncidencia) grafo).todasArestas();
        }

        throw new IllegalArgumentException(
            "Representacao nao suportada."
        );
    }

    public static Aresta[][] porOrigem(Grafo grafo) {
        int n = grafo.numeroDeVertices();

        Aresta[] arestas = obter(grafo);
        int[] contagem = new int[n];

        for (Aresta aresta : arestas) {
            contagem[aresta.origem().id()]++;
        }

        Aresta[][] saidas = new Aresta[n][];

        for (int v = 0; v < n; v++) {
            saidas[v] = new Aresta[contagem[v]];
            contagem[v] = 0;
        }

        for (Aresta aresta : arestas) {
            int u = aresta.origem().id();
            saidas[u][contagem[u]++] = aresta;
        }

        return saidas;
    }
}