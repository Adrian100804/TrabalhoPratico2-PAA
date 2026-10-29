package grafos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class Main {

    private static final Scanner teclado =
            new Scanner(System.in);

    public static void main(String[] args) {
        Algoritmos algoritmos = new Algoritmos();
        Grafo grafo;

        System.out.println("=== ALGORITMOS EM GRAFOS ===");

        try {
            System.out.print(
                "Caminho do arquivo (Enter para Teste.txt): "
            );

            if (!teclado.hasNextLine()) {
                return;
            }

            String caminho = teclado.nextLine().trim();

            if (caminho.isEmpty()) {
                caminho = "Teste.txt";
            }

            System.out.println();
            System.out.println("Escolha a representacao:");
            System.out.println("1 - Matriz de adjacencia");
            System.out.println("2 - Matriz de incidencia");
            System.out.println("3 - Lista de adjacencia");

            int escolha = lerNumero("Representacao: ");

            TipoDeRepresentacao tipo;

            switch (escolha) {
                case 1:
                    tipo = TipoDeRepresentacao.MATRIZ_DE_ADJACENCIA;
                    break;

                case 2:
                    tipo = TipoDeRepresentacao.MATRIZ_DE_INCIDENCIA;
                    break;

                case 3:
                    tipo = TipoDeRepresentacao.LISTA_DE_ADJACENCIA;
                    break;

                default:
                    throw new IllegalArgumentException(
                        "Representacao invalida."
                    );
            }

            grafo = algoritmos.carregarGrafo(caminho, tipo);

            System.out.println();
            System.out.println("Grafo carregado!");
            System.out.println(
                "Vertices: " + grafo.numeroDeVertices()
            );
            System.out.println(
                "Arestas: " + grafo.numeroDeArestas()
            );

        } catch (Exception erro) {
            System.out.println(
                "Nao foi possivel carregar o grafo: "
                + erro.getMessage()
            );
            return;
        }

        while (true) {
            mostrarMenu();

            if (!teclado.hasNextLine()) {
                return;
            }

            try {
                int opcao = Integer.parseInt(
                    teclado.nextLine().trim()
                );

                switch (opcao) {
                    case 0:
                        System.out.println("Programa encerrado.");
                        return;

                    case 1: {
                        int inicio = lerNumero("Vertice inicial: ");

                        algoritmos.definirInicio(inicio);

                        long tempoInicio = System.nanoTime();

                        algoritmos.buscaEmProfundidade(grafo);

                        mostrarTempo(tempoInicio);

                        System.out.println();
                        System.out.println(
                            "Vertice | Descoberta | Finalizacao | Pai"
                        );

                        for (int v = 0;
                             v < grafo.numeroDeVertices();
                             v++) {

                            System.out.println(
                                v
                                + " | " + algoritmos.descoberta[v]
                                + " | " + algoritmos.finalizacao[v]
                                + " | " + algoritmos.pai[v]
                            );
                        }

                        System.out.println(
                            "Pai -1 indica uma raiz."
                        );

                        System.out.println();
                        System.out.println("Arestas de arvore:");

                        mostrarArestas(
                            algoritmos.arestasDeArvore(grafo), true
                        );

                        System.out.println("Arestas de retorno:");

                        mostrarArestas(
                            algoritmos.arestasDeRetorno(grafo), true
                        );

                        System.out.println("Arestas de avanco:");

                        mostrarArestas(
                            algoritmos.arestasDeAvanco(grafo), true
                        );

                        System.out.println("Arestas de cruzamento:");

                        mostrarArestas(
                            algoritmos.arestasDeCruzamento(grafo), true
                        );

                        break;
                    }

                    case 2: {
                        int inicio = lerNumero("Vertice inicial: ");

                        algoritmos.definirInicio(inicio);

                        long tempoInicio = System.nanoTime();

                        algoritmos.buscaEmLargura(grafo);

                        mostrarTempo(tempoInicio);

                        System.out.println();
                        System.out.println(
                            "Vertice | Distancia | Ordem de descoberta | Pai"
                        );

                        for (int v = 0;
                             v < grafo.numeroDeVertices();
                             v++) {

                            System.out.println(
                                v
                                + " | " + algoritmos.distanciaBfs[v]
                                + " | " + algoritmos.ordemDescobertaBfs[v]
                                + " | " + algoritmos.pai[v]
                            );
                        }

                        System.out.println(
                            "Distancia em quantidade de arestas."
                        );
                        System.out.println(
                            "Distancia e ordem -1: vertice nao alcancado."
                        );
                        System.out.println(
                            "Pai -1: ausencia de predecessor."
                        );

                        break;
                    }

                    case 3: {
                        long tempoInicio = System.nanoTime();

                        boolean resultado =
                            algoritmos.existeCiclo(grafo);

                        mostrarTempo(tempoInicio);

                        System.out.println(
                            "Existe ciclo: " + resultado
                        );

                        break;
                    }

                    case 4: {
                        long tempoInicio = System.nanoTime();

                        Grafo reduzido =
                            algoritmos.componentesFortementeConexos(
                                grafo
                            );

                        mostrarTempo(tempoInicio);

                        System.out.println();
                        System.out.println(
                            "Quantidade de componentes: "
                            + reduzido.numeroDeVertices()
                        );

                        StringBuilder[] porComponente =
                            new StringBuilder[
                                reduzido.numeroDeVertices()
                            ];

                        for (int grupo = 0;
                             grupo < porComponente.length;
                             grupo++) {

                            porComponente[grupo] =
                                new StringBuilder();
                        }

                        for (int v = 0;
                             v < grafo.numeroDeVertices();
                             v++) {

                            porComponente[algoritmos.componente[v]]
                                .append(v).append(' ');
                        }

                        for (int grupo = 0;
                             grupo < porComponente.length;
                             grupo++) {

                            System.out.println(
                                "Componente " + grupo
                                + ": " + porComponente[grupo]
                            );
                        }

                        System.out.println();
                        System.out.println("Grafo reduzido:");

                        mostrarGrafoReduzido(reduzido);

                        break;
                    }

                    case 5: {
                        System.out.println(
                            "Kruskal calcula uma AGM nao orientada."
                        );
                        System.out.println(
                            "Neste calculo, os sentidos das arestas "
                            + "serao ignorados."
                        );

                        System.out.print(
                            "Digite SIM para continuar: "
                        );

                        if (!teclado.hasNextLine()) {
                            return;
                        }

                        String resposta =
                            teclado.nextLine().trim();

                        if (!resposta.equalsIgnoreCase("SIM")) {
                            System.out.println("Calculo cancelado.");
                            break;
                        }

                        long tempoInicio = System.nanoTime();

                        Collection<Aresta> arvore =
                            algoritmos.arvoreGeradoraMinima(grafo);

                        double custo =
                            algoritmos.custoDaArvoreGeradora(
                                grafo, arvore
                            );

                        mostrarTempo(tempoInicio);

                        System.out.println("Arestas da AGM:");

                        mostrarArestas(arvore, false);

                        System.out.println(
                            "Custo total da AGM: " + custo
                        );

                        break;
                    }

                    case 6: {
                        Vertice origem = new Vertice(
                            lerNumero("Vertice de origem: ")
                        );

                        Vertice destino = new Vertice(
                            lerNumero("Vertice de destino: ")
                        );

                        long tempoInicio = System.nanoTime();

                        ArrayList<Aresta> caminho =
                            algoritmos.caminhoMinimo(
                                grafo, origem, destino
                            );

                        double custo =
                            algoritmos.custoDoCaminhoMinimo(
                                grafo, caminho, origem, destino
                            );

                        mostrarTempo(tempoInicio);

                        System.out.println("Caminho minimo:");

                        if (caminho.isEmpty()
                                && origem.id() == destino.id()) {
                            System.out.println(
                                "A origem e o destino sao o mesmo vertice."
                            );
                        } else {
                            mostrarArestas(caminho, true);
                        }

                        System.out.println(
                            "Custo total do caminho: " + custo
                        );

                        break;
                    }

                    case 7: {
                        int origem = lerNumero("Fonte: ");
                        int destino = lerNumero("Sorvedouro: ");

                        algoritmos.definirTerminais(origem, destino);

                        long tempoInicio = System.nanoTime();

                        double fluxo =
                            algoritmos.fluxoMaximo(grafo);

                        mostrarTempo(tempoInicio);

                        System.out.println(
                            "Fluxo maximo: " + fluxo
                        );

                        break;
                    }

                    default:
                        System.out.println("Opcao invalida.");
                }

            } catch (NumberFormatException erro) {
                System.out.println(
                    "Entrada invalida: digite um numero inteiro."
                );

            } catch (Exception erro) {
                System.out.println(
                    "Nao foi possivel executar: "
                    + erro.getMessage()
                );
            }
        }
    }

    private static int lerNumero(String mensagem) {
        System.out.print(mensagem);

        if (!teclado.hasNextLine()) {
            throw new IllegalStateException(
                "A entrada foi encerrada."
            );
        }

        return Integer.parseInt(
            teclado.nextLine().trim()
        );
    }

    private static void mostrarTempo(long inicio) {
        double milissegundos =
            (System.nanoTime() - inicio) / 1_000_000.0;

        System.out.printf(
            "Tempo de calculo: %.3f ms%n",
            milissegundos
        );
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("=== MENU ===");
        System.out.println("1 - Busca em profundidade (DFS)");
        System.out.println("2 - Busca em largura (BFS)");
        System.out.println("3 - Verificar existencia de ciclo");
        System.out.println("4 - Componentes fortemente conexos");
        System.out.println("5 - Arvore geradora minima (Kruskal)");
        System.out.println("6 - Caminho minimo (Dijkstra)");
        System.out.println("7 - Fluxo maximo");
        System.out.println("0 - Sair");
        System.out.print("Opcao: ");
    }

    private static void mostrarArestas(
            Collection<Aresta> arestas,
            boolean orientadas
    ) {
        if (arestas.isEmpty()) {
            System.out.println("(nenhuma aresta)");
            return;
        }

        String ligacao = orientadas ? " -> " : " -- ";

        for (Aresta aresta : arestas) {
            System.out.println(
                aresta.origem().id()
                + ligacao
                + aresta.destino().id()
                + " | peso: "
                + aresta.peso()
            );
        }
    }

    private static void mostrarGrafoReduzido(
            Grafo grafo
    ) throws Exception {
        System.out.println(
            "Vertices: " + grafo.numeroDeVertices()
        );
        System.out.println(
            "Arestas: " + grafo.numeroDeArestas()
        );

        if (grafo.numeroDeArestas() == 0) {
            System.out.println(
                "Nao existem ligacoes entre componentes diferentes."
            );
            return;
        }

        for (Vertice origem : grafo.vertices()) {
            for (Vertice destino :
                    grafo.adjacentesDe(origem)) {

                System.out.println(
                    "Componente " + origem.id()
                    + " -> Componente " + destino.id()
                );
            }
        }
    }
}