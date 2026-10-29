package grafos;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Algoritmos implements AlgoritmosEmGrafos {

    private int inicio = 0;
    private int destinoFluxo = -1;

    private BuscaProfundidade ultimaBusca;
    private Grafo grafoDaUltimaBusca;
    private int inicioDaUltimaBusca = -1;
    private int arestasDaUltimaBusca = -1;

    public int[] descoberta;
    public int[] finalizacao;
    public int[] pai;

    public int[] distanciaBfs;
    public int[] ordemDescobertaBfs;

    public int[] componente;

    public void definirInicio(int vertice) {
        inicio = vertice;
    }

    public void definirTerminais(int origem, int destino) {
        inicio = origem;
        destinoFluxo = destino;
    }

    @Override
    public Grafo carregarGrafo(
            String caminho,
            TipoDeRepresentacao tipo
    ) throws Exception {

        if (tipo == null) {
            throw new IllegalArgumentException(
                "A representacao deve ser informada."
            );
        }

        int n;

        try (java.io.BufferedReader leitor =
                Files.newBufferedReader(
                    Paths.get(caminho),
                    StandardCharsets.UTF_8
                )) {

            String cabecalho = leitor.readLine();

            if (cabecalho == null) {
                throw new IllegalArgumentException(
                    "Arquivo vazio."
                );
            }

            try {
                n = Integer.parseInt(
                    cabecalho.replace("\uFEFF", "").trim()
                );

            } catch (NumberFormatException erro) {
                throw new IllegalArgumentException(
                    "A primeira linha deve conter a quantidade de vertices.",
                    erro
                );
            }
        }

        if (n < 0) {
            throw new IllegalArgumentException(
                "A quantidade de vertices nao pode ser negativa."
            );
        }

        Grafo grafo;

        switch (tipo) {
            case LISTA_DE_ADJACENCIA:
                grafo = new ListaAdjacencia(n);
                break;

            case MATRIZ_DE_ADJACENCIA:
                verificarMemoriaMatriz(
                    (long) n * n * 8,
                    "adjacencia"
                );
                grafo = new MatrizAdjacencia(n);
                break;

            case MATRIZ_DE_INCIDENCIA:
                int e = contarArestas(caminho);
                verificarMemoriaMatriz(
                    (long) n * e,
                    "incidencia"
                );
                grafo = new MatrizIncidencia(n, e);
                break;

            default:
                throw new IllegalArgumentException(
                    "Representacao desconhecida."
                );
        }

        // Processa o arquivo linha a linha.
        try (java.io.BufferedReader leitor =
                Files.newBufferedReader(
                    Paths.get(caminho),
                    StandardCharsets.UTF_8
                )) {

            leitor.readLine();

            boolean[] origemInformada = new boolean[n];

            String linha;
            int numeroLinha = 1;

            while ((linha = leitor.readLine()) != null) {
                numeroLinha++;
                linha = linha.trim();

                if (linha.isEmpty()) {
                    continue;
                }

                try {
                    String[] partes =
                        linha.split("\\s+", 2);

                    int u = Integer.parseInt(partes[0]);

                    if (u < 0 || u >= n) {
                        throw new IllegalArgumentException(
                            "Vertice de origem invalido."
                        );
                    }

                    if (origemInformada[u]) {
                        throw new IllegalArgumentException(
                            "A origem ja foi informada em outra linha."
                        );
                    }

                    origemInformada[u] = true;

                    if (partes.length == 1) {
                        continue;
                    }

                    String[] itens =
                        partes[1].split(";", -1);

                    for (int j = 0; j < itens.length; j++) {
                        String item = itens[j].trim();

                        if (item.isEmpty()
                                && j == itens.length - 1) {
                            continue;
                        }

                        int separador = item.indexOf('-');

                        if (separador <= 0) {
                            throw new IllegalArgumentException(
                                "A aresta deve estar no formato destino-peso."
                            );
                        }

                        int v = Integer.parseInt(
                            item.substring(0, separador).trim()
                        );

                        double peso = Double.parseDouble(
                            item.substring(separador + 1).trim()
                        );

                        if (v < 0 || v >= n) {
                            throw new IllegalArgumentException(
                                "Vertice de destino invalido."
                            );
                        }

                        if (!Double.isFinite(peso)) {
                            throw new IllegalArgumentException(
                                "O peso deve ser finito."
                            );
                        }

                        grafo.adicionarAresta(
                            new Vertice(u),
                            new Vertice(v),
                            peso
                        );
                    }

                } catch (Exception erro) {
                    throw new IllegalArgumentException(
                        "Erro na linha "
                        + numeroLinha
                        + ": "
                        + erro.getMessage(),
                        erro
                    );
                }
            }
        }

        return grafo;
    }

    private void verificarMemoriaMatriz(
            long bytesEstimados,
            String tipo
    ) {
        long limite = Math.min(
            256_000_000L,
            Runtime.getRuntime().maxMemory() / 3
        );

        if (bytesEstimados > limite) {
            throw new IllegalArgumentException(
                "Matriz de " + tipo
                + " grande demais para esta memoria. "
                + "Use a lista de adjacencia para testar este arquivo."
            );
        }
    }

    private int contarArestas(String caminho) throws Exception {
        long total = 0;

        try (java.io.BufferedReader leitor =
                Files.newBufferedReader(
                    Paths.get(caminho),
                    StandardCharsets.UTF_8
                )) {

            leitor.readLine();

            String linha;

            while ((linha = leitor.readLine()) != null) {
                String[] partes =
                    linha.trim().split("\\s+", 2);

                if (partes.length < 2) {
                    continue;
                }

                String[] itens =
                    partes[1].split(";", -1);

                for (String item : itens) {
                    if (!item.trim().isEmpty()) {
                        total++;

                        if (total > Integer.MAX_VALUE) {
                            throw new IllegalArgumentException(
                                "Quantidade de arestas excessiva."
                            );
                        }
                    }
                }
            }
        }

        return (int) total;
    }

    private BuscaProfundidade obterBusca(Grafo grafo) {
        if (ultimaBusca == null
                || grafoDaUltimaBusca != grafo
                || inicioDaUltimaBusca != inicio
                || arestasDaUltimaBusca != grafo.numeroDeArestas()) {

            ultimaBusca = new BuscaProfundidade(inicio);
            ultimaBusca.executar(grafo);

            grafoDaUltimaBusca = grafo;
            inicioDaUltimaBusca = inicio;
            arestasDaUltimaBusca = grafo.numeroDeArestas();
        }

        return ultimaBusca;
    }

    @Override
    public Collection<Aresta> buscaEmProfundidade(Grafo grafo) {
        BuscaProfundidade dfs = obterBusca(grafo);

        descoberta = dfs.descoberta;
        finalizacao = dfs.finalizacao;
        pai = dfs.pai;

        return dfs.arestasDeArvoreResultado();
    }

    @Override
    public Collection<Aresta> arestasDeArvore(Grafo grafo) {
        return obterBusca(grafo).arestasDeArvoreResultado();
    }

    @Override
    public Collection<Aresta> arestasDeRetorno(Grafo grafo) {
        return obterBusca(grafo).arestasDeRetornoResultado();
    }

    @Override
    public Collection<Aresta> arestasDeAvanco(Grafo grafo) {
        return obterBusca(grafo).arestasDeAvancoResultado();
    }

    @Override
    public Collection<Aresta> arestasDeCruzamento(Grafo grafo) {
        return obterBusca(grafo).arestasDeCruzamentoResultado();
    }

    @Override
    public Collection<Aresta> buscaEmLargura(Grafo grafo) {
        BuscaLargura bfs = new BuscaLargura(inicio);

        Collection<Aresta> resultado = bfs.executar(grafo);

        distanciaBfs = bfs.distanciaBfs;
        ordemDescobertaBfs = bfs.ordemDescobertaBfs;
        pai = bfs.pai;

        return resultado;
    }

    @Override
    public boolean existeCiclo(Grafo grafo) {
        return new ExisteCiclo().executar(grafo);
    }

    @Override
    public Grafo componentesFortementeConexos(Grafo grafo) {
        ComponentesFortementeConexos algoritmo =
            new ComponentesFortementeConexos();

        Grafo reduzido = algoritmo.executar(grafo);

        componente = algoritmo.componente;

        return reduzido;
    }

    @Override
    public Collection<Aresta> arvoreGeradoraMinima(Grafo grafo) {
        return new Kruskal().executar(grafo);
    }

    @Override
    public double custoDaArvoreGeradora(
            Grafo grafo,
            Collection<Aresta> arestas
    ) throws Exception {
        return new Kruskal().custo(grafo, arestas);
    }

    @Override
    public ArrayList<Aresta> caminhoMinimo(
            Grafo grafo,
            Vertice origem,
            Vertice destino
    ) {
        return new Dijkstra().executar(
            grafo,
            origem,
            destino
        );
    }

    @Override
    public double custoDoCaminhoMinimo(
            Grafo grafo,
            ArrayList<Aresta> arestas,
            Vertice origem,
            Vertice destino
    ) throws Exception {
        return new Dijkstra().custo(
            grafo,
            arestas,
            origem,
            destino
        );
    }

    @Override
    public double fluxoMaximo(Grafo grafo) {
        return new FluxoMaximo(
            inicio,
            destinoFluxo
        ).executar(grafo);
    }
}