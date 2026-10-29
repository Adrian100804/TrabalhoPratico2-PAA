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

        List<String> linhas = Files.readAllLines(
                Paths.get(caminho),
                StandardCharsets.UTF_8
        );

        if (linhas.isEmpty()) {
            throw new IllegalArgumentException("Arquivo vazio.");
        }

        // Remove uma eventual marca BOM de arquivos UTF-8.
        String cabecalho = linhas.get(0)
                .replace("\uFEFF", "")
                .trim();

        int quantidadeVertices;

        try {
            quantidadeVertices = Integer.parseInt(cabecalho);

        } catch (NumberFormatException erro) {
            throw new IllegalArgumentException(
                    "A primeira linha deve conter a quantidade de vertices.",
                    erro
            );
        }

        if (quantidadeVertices < 0) {
            throw new IllegalArgumentException(
                    "A quantidade de vertices nao pode ser negativa."
            );
        }

        ArrayList<Aresta> arestasLidas = new ArrayList<>();

        boolean[] origemInformada =
                new boolean[quantidadeVertices];

        for (int i = 1; i < linhas.size(); i++) {
            String linha = linhas.get(i).trim();

            if (linha.isEmpty()) {
                continue;
            }

            try {
                // Separa a origem do restante da linha.
                String[] partes = linha.split("\\s+", 2);

                int origem = Integer.parseInt(partes[0]);

                if (origem < 0 || origem >= quantidadeVertices) {
                    throw new IllegalArgumentException(
                            "Vertice de origem invalido."
                    );
                }

                if (origemInformada[origem]) {
                    throw new IllegalArgumentException(
                            "A origem ja foi informada em outra linha."
                    );
                }

                origemInformada[origem] = true;

                // Uma linha somente com a origem indica ausencia de saidas.
                if (partes.length == 1) {
                    continue;
                }

                String[] itens = partes[1].split(";", -1);

                for (int j = 0; j < itens.length; j++) {
                    String item = itens[j].trim();

                    // Permite o ponto e virgula no final da linha.
                    if (item.isEmpty() && j == itens.length - 1) {
                        continue;
                    }

                    int separador = item.indexOf('-');

                    if (separador <= 0) {
                        throw new IllegalArgumentException(
                                "A aresta deve estar no formato destino-peso."
                        );
                    }

                    int destino = Integer.parseInt(
                            item.substring(0, separador).trim()
                    );

                    double peso = Double.parseDouble(
                            item.substring(separador + 1).trim()
                    );

                    if (destino < 0 || destino >= quantidadeVertices) {
                        throw new IllegalArgumentException(
                                "Vertice de destino invalido."
                        );
                    }

                    if (!Double.isFinite(peso)) {
                        throw new IllegalArgumentException(
                                "O peso deve ser um numero finito."
                        );
                    }

                    arestasLidas.add(new Aresta(
                            new Vertice(origem),
                            new Vertice(destino),
                            peso
                    ));
                }

            } catch (RuntimeException erro) {
                throw new IllegalArgumentException(
                        "Erro na linha " + (i + 1)
                                + ": " + erro.getMessage(),
                        erro
                );
            }
        }

        Grafo grafo;

        switch (tipo) {
            case MATRIZ_DE_ADJACENCIA:
                grafo = new MatrizAdjacencia(
                        quantidadeVertices
                );
                break;

            case MATRIZ_DE_INCIDENCIA:
                grafo = new MatrizIncidencia(
                        quantidadeVertices,
                        arestasLidas.size()
                );
                break;

            case LISTA_DE_ADJACENCIA:
                grafo = new ListaAdjacencia(
                        quantidadeVertices
                );
                break;

            default:
                throw new IllegalArgumentException(
                        "Representacao desconhecida."
                );
        }

        for (Aresta aresta : arestasLidas) {
            grafo.adicionarAresta(
                    aresta.origem(),
                    aresta.destino(),
                    aresta.peso()
            );
        }

        return grafo;
    }

    @Override
    public Collection<Aresta> buscaEmProfundidade(Grafo grafo) {
        BuscaProfundidade dfs = new BuscaProfundidade(inicio);

        Collection<Aresta> resultado = dfs.executar(grafo);

        descoberta = dfs.descoberta;
        finalizacao = dfs.finalizacao;
        pai = dfs.pai;

        return resultado;
    }

    @Override
    public Collection<Aresta> arestasDeArvore(Grafo grafo) {
        return new BuscaProfundidade(inicio)
                .arestasDeArvore(grafo);
    }

    @Override
    public Collection<Aresta> arestasDeRetorno(Grafo grafo) {
        return new BuscaProfundidade(inicio)
                .arestasDeRetorno(grafo);
    }

    @Override
    public Collection<Aresta> arestasDeAvanco(Grafo grafo) {
        return new BuscaProfundidade(inicio)
                .arestasDeAvanco(grafo);
    }

    @Override
    public Collection<Aresta> arestasDeCruzamento(Grafo grafo) {
        return new BuscaProfundidade(inicio)
                .arestasDeCruzamento(grafo);
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