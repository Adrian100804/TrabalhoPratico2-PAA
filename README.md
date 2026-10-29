# Trabalho Prático 2 — Algoritmos em Grafos

Projeto desenvolvido em Java para a disciplina de **Projeto e Análise de Algoritmos (PAA)**.

**Aluno:** Adrian Henrique Maranesi Soares  
**Professor:** Douglas Castilho  
**Linguagem:** Java

---

## 1. Objetivo

O programa carrega um grafo a partir de um arquivo de texto e permite executar diferentes algoritmos por meio de um menu interativo.

O usuário pode escolher a representação do grafo e a operação desejada.

Foram mantidas as classes e interfaces fornecidas pelo professor, organizando os algoritmos em classes separadas para facilitar a leitura e a manutenção do projeto.

Foram realizadas melhorias para permitir a execução de testes com maior quantidade de vértices e arestas, priorizando o funcionamento correto, o controle de memória e a utilização de estruturas simples.

## 2. Organização do projeto

As classes `Vertice` e `Aresta` representam os elementos do grafo.

As interfaces `Grafo` e `AlgoritmosEmGrafos` definem os métodos utilizados pelo programa.

### Representações de grafos

Foram implementadas três representações:

- `MatrizAdjacencia` — matriz de adjacência.
- `ListaAdjacencia` — lista de adjacência.
- `MatrizIncidencia` — matriz de incidência.

### Classes dos algoritmos

| Classe | Responsabilidade |
|---|---|
| `BuscaProfundidade` | Executar DFS e classificar arestas |
| `BuscaLargura` | Executar BFS |
| `ExisteCiclo` | Detectar ciclos orientados |
| `ComponentesFortementeConexos` | Identificar componentes e construir o grafo reduzido |
| `Kruskal` | Calcular a árvore geradora mínima |
| `Dijkstra` | Calcular o caminho mínimo |
| `FluxoMaximo` | Calcular o fluxo máximo |

A classe `Algoritmos` realiza a leitura do arquivo e encaminha as operações para as classes responsáveis.

A classe `Main` contém o menu, recebe as entradas do usuário, apresenta os resultados e mede o tempo de execução dos algoritmos.

Foi adicionada a classe auxiliar `ArestasDoGrafo`, responsável por obter e organizar as arestas das diferentes representações, evitando a repetição de código nos algoritmos.

## 3. Entrada e representações

O programa recebe um arquivo de texto contendo as informações do grafo.

- A primeira linha informa a quantidade de vértices.
- Nas demais linhas, o primeiro número identifica o vértice de origem.
- Cada par `destino-peso` representa uma aresta.

### Exemplo de linha de entrada

```text
3 1-42; 4-7;
```

Essa linha representa:

- Uma aresta do vértice 3 para o vértice 1, com peso 42.
- Uma aresta do vértice 3 para o vértice 4, com peso 7.

Os vértices são numerados de `0` até `V - 1`.

O grafo é orientado. Portanto, adicionar uma aresta de 3 para 4 não cria automaticamente uma aresta de 4 para 3.

### Características das representações

**Matriz de adjacência**

Cada posição armazena uma aresta ou `null`, quando não existe ligação.

**Lista de adjacência**

Cada vértice possui uma lista contendo suas arestas de saída.

**Matriz de incidência**

Cada coluna representa uma aresta:

- `-1` indica a origem.
- `1` indica o destino.
- `2` representa um laço.

Os pesos são armazenados em um vetor separado.

As representações aceitam laços e pesos iguais a zero. Arestas repetidas com a mesma origem e destino são rejeitadas.

### Leitura e validação do arquivo

A leitura é realizada linha por linha utilizando `BufferedReader`, evitando carregar todas as linhas do arquivo simultaneamente na memória.

O programa verifica a quantidade de vértices, o formato das arestas, os índices informados e os pesos inválidos.

Também foi adicionada uma verificação do tamanho estimado das matrizes antes de sua criação, evitando algumas situações de consumo excessivo de memória.

Para grafos grandes com poucas arestas em relação ao número de vértices, a lista de adjacência normalmente apresenta menor consumo de memória.

## 4. Algoritmos implementados

### 4.1. Busca em profundidade — DFS

A busca em profundidade explora os vértices do grafo utilizando uma pilha implementada com vetor.

A implementação utiliza uma abordagem iterativa, evitando chamadas recursivas profundas que poderiam causar estouro de pilha em grafos grandes.

**Funcionamento:**

1. Inicia no vértice escolhido.
2. Visita os vértices ainda não descobertos.
3. Registra os tempos de descoberta e finalização.
4. Armazena o pai de cada vértice.
5. Classifica as arestas encontradas.

**Classificações das arestas:**

- Árvore
- Retorno
- Avanço
- Cruzamento

Após a exploração inicial, o algoritmo também percorre vértices não alcançados, formando uma floresta de busca.

### 4.2. Busca em largura — BFS

A BFS utiliza uma **fila implementada com vetor** para explorar os vértices por níveis.

O algoritmo registra:

- Distância em quantidade de arestas.
- Ordem de descoberta.
- Pai de cada vértice.

Os pesos das arestas não são considerados na escolha dos caminhos.

Vértices não alcançados recebem distância e ordem iguais a `-1`.

A preparação das adjacências utiliza a classe auxiliar `ArestasDoGrafo`.

### 4.3. Detecção de ciclos

A classe `ExisteCiclo` verifica a existência de ciclos orientados.

O algoritmo utiliza uma busca em profundidade iterativa e três estados:

1. Não visitado.
2. Em exploração.
3. Finalizado.

Uma aresta que aponta para um vértice ainda em exploração indica a existência de um ciclo.

O algoritmo também identifica laços e percorre todas as partes do grafo.

A utilização de uma pilha em vetor evita problemas de recursão em caminhos muito longos.

### 4.4. Componentes fortemente conexos

Foi utilizado o algoritmo de **Kosaraju**.

**Etapas:**

1. Executar uma busca para registrar a ordem de finalização.
2. Inverter as arestas do grafo.
3. Executar uma nova busca na ordem inversa de finalização.
4. Identificar os componentes fortemente conexos.
5. Construir o grafo reduzido.

No grafo reduzido, cada componente passa a ser representado por um único vértice.

As buscas foram implementadas de maneira iterativa, utilizando vetores como pilhas para suportar caminhos longos.

### 4.5. Árvore geradora mínima — Kruskal

O algoritmo de Kruskal calcula uma árvore geradora mínima.

**Funcionamento:**

1. Obtém as arestas do grafo.
2. Ordena as arestas pelo peso usando Merge Sort.
3. Analisa as arestas da menor para a maior.
4. Verifica se os vértices pertencem a conjuntos diferentes.
5. Adiciona a aresta caso não forme ciclo.
6. Retorna as arestas selecionadas e o custo total.

São utilizados vetores de representantes e alturas para controlar os conjuntos disjuntos, por meio da estrutura Union-Find.

Para um grafo conectado com `V` vértices, a árvore geradora possui `V - 1` arestas.

Caso o grafo seja desconexo, o programa informa que não existe uma árvore geradora para todos os vértices.

Como Kruskal considera ligações não orientadas, o menu solicita confirmação para desconsiderar o sentido das arestas durante esse cálculo.

O grafo original não é modificado.

### 4.6. Caminho mínimo — Dijkstra

O algoritmo de Dijkstra encontra um caminho de custo mínimo entre dois vértices.

**Funcionamento:**

1. Recebe os vértices de origem e destino.
2. Inicializa os vetores de distâncias e controle.
3. Seleciona o vértice não processado de menor distância.
4. Analisa as arestas que saem desse vértice.
5. Atualiza as distâncias quando encontra um caminho melhor.
6. Armazena as arestas anteriores.
7. Reconstrói o caminho encontrado.

A seleção do próximo vértice é realizada por varredura dos vetores, sem utilizar fila de prioridade.

O algoritmo não aceita pesos negativos.

Caso não exista um caminho entre origem e destino, uma mensagem é apresentada.

Se a origem e o destino forem iguais, o custo será zero.

### 4.7. Fluxo máximo — Ford-Fulkerson

O fluxo máximo é calculado utilizando o método **Ford-Fulkerson com busca em profundidade (DFS)**.

A busca é implementada de maneira iterativa, utilizando uma pilha em vetor para encontrar caminhos aumentantes.

A rede residual utiliza listas de arcos diretos e inversos, evitando o armazenamento de uma matriz residual completa.

**Etapas:**

1. Recebe o vértice fonte e o sorvedouro.
2. Constrói a rede residual.
3. Procura um caminho aumentante utilizando DFS.
4. Identifica a menor capacidade residual do caminho, chamada gargalo.
5. Atualiza as capacidades residuais dos arcos diretos e inversos.
6. Soma o gargalo ao fluxo total.
7. Repete o processo enquanto existirem caminhos aumentantes.
8. Retorna o fluxo máximo encontrado.

Os pesos das arestas representam capacidades não negativas.

Os arcos inversos permitem desfazer ou redirecionar parte de um fluxo anteriormente enviado.

O algoritmo termina quando não existe mais caminho aumentante entre a fonte e o sorvedouro na rede residual.

## 5. Utilização de vetores estáticos

Conforme as exigências do trabalho, os algoritmos de Dijkstra e Kruskal utilizam arrays de tamanho fixo durante o processamento.

### Dijkstra

Os vetores armazenam:

- `distancia[]` — menores distâncias conhecidas.
- `visitado[]` — vértices já processados.
- `anterior[]` — arestas anteriores de cada caminho.
- `caminhoInvertido[]` — arestas utilizadas na reconstrução do resultado.

### Kruskal

Os vetores armazenam:

- `arestas[]` — arestas que serão ordenadas.
- `auxiliar[]` — vetor auxiliar do Merge Sort.
- `escolhidas[]` — arestas selecionadas para a árvore.
- `representante[]` — representantes dos conjuntos disjuntos.
- `altura[]` — alturas utilizadas na união dos conjuntos.

Os tamanhos dos vetores são definidos a partir da quantidade de vértices e arestas.

Não ocorre redimensionamento desses arrays durante o processamento.

As conversões finais para coleções são utilizadas para respeitar os tipos de retorno definidos pelas interfaces do código-base.

## 6. Complexidade dos algoritmos

Considerando as adjacências já preparadas, as complexidades principais são:

| Algoritmo | Complexidade de tempo |
|---|---|
| Busca em profundidade — DFS | `O(V + E)` |
| Busca em largura — BFS | `O(V + E)` |
| Detecção de ciclos | `O(V + E)` |
| Kosaraju | `O(V + E)` nas buscas e transposição |
| Kruskal (Merge Sort e Union-Find) | `O(V + E log E)` |
| Dijkstra (seleção por varredura) | `O(V² + E)` |
| Ford-Fulkerson com DFS | `O((V + E) × F)` para capacidades inteiras |

**Onde:**

- `V` representa a quantidade de vértices.
- `E` representa a quantidade de arestas.
- `F` representa o valor do fluxo máximo quando as capacidades são inteiras.

O custo de consultar e preparar as arestas depende da representação utilizada. No Kosaraju, a construção do grafo reduzido também pode acrescentar processamento.

No Ford-Fulkerson, o número de caminhos aumentantes depende das capacidades e dos caminhos encontrados. A implementação com DFS não garante o mesmo desempenho para todos os tipos de redes.

### Medição do tempo de execução

O programa utiliza `System.nanoTime()` para medir o tempo gasto nas operações dos algoritmos.

O resultado é convertido para milissegundos e apresentado no menu.

Essa medição não inclui o carregamento inicial do arquivo nem a impressão dos resultados na tela.

### Melhorias para testes maiores

Foram realizadas algumas alterações para melhorar a execução em grafos grandes:

- Utilização de pilhas iterativas para evitar recursão profunda.
- Leitura do arquivo por etapas utilizando `BufferedReader`.
- Organização das arestas por meio da classe `ArestasDoGrafo`.
- Utilização de listas para representar a rede residual do Ford-Fulkerson.
- Verificação preventiva do consumo estimado de memória nas matrizes.
- Reaproveitamento dos resultados da DFS ao apresentar as classificações das arestas.

Essas alterações procuram equilibrar simplicidade, desempenho e estabilidade, sem utilizar necessariamente a solução mais otimizada possível.

O programa possui limitações relacionadas à memória disponível, à quantidade de vértices, à quantidade de arestas e às características de cada algoritmo.

## 7. Referências bibliográficas

CORMEN, Thomas H.; LEISERSON, Charles E.; RIVEST, Ronald L.; STEIN, Clifford. *Introduction to Algorithms*. 4. ed. MIT Press, 2022.

KLEINBERG, Jon; TARDOS, Éva. *Algorithm Design*. Pearson/Addison-Wesley, 2006.

https://coddy.tech/visualize/graphs/kruskals-algorithm?lang=pt&view=array&speed=2&size=14

https://coddy.tech/visualize/pt/graphs/dijkstras-algorithm?view=array&speed=2&size=14

## 8. Apoio de inteligência artificial

O ChatGPT foi utilizado como ferramenta de apoio ao estudo dos algoritmos, à revisão e elaboração de trechos da implementação e à organização da documentação do projeto.

---

**Trabalho Prático 2 — Projeto e Análise de Algoritmos**