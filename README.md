# Trabalho Prático 2 — Algoritmos em Grafos

**Aluno:** Adrian Henrique Maranesi Soares  
**Disciplina:** Projeto e Análise de Algoritmos  
**Professor:** Douglas Castilho  
**Linguagem:** Java

## 1. Objetivo

O programa carrega um grafo de um arquivo de texto e permite
executar os algoritmos solicitados no trabalho. Pelo menu,
o usuário escolhe a representação do grafo e a operação desejada.

Foram mantidas as classes e interfaces fornecidas pelo professor.
Os algoritmos foram separados em classes para facilitar a leitura
e a organização do projeto.

## 2. Organização do projeto

As classes `Vertice` e `Aresta` representam os elementos do grafo.
As interfaces `Grafo` e `AlgoritmosEmGrafos` definem os métodos
utilizados pelo programa.

As três representações foram implementadas nas classes
`MatrizAdjacencia`, `ListaAdjacencia` e `MatrizIncidencia`.

Cada operação possui uma classe própria:

| Classe | Responsabilidade |
|---|---|
| `BuscaProfundidade` | Executar DFS e classificar as arestas |
| `BuscaLargura` | Executar BFS |
| `ExisteCiclo` | Detectar ciclos orientados |
| `ComponentesFortementeConexos` | Identificar componentes e construir o grafo reduzido |
| `Kruskal` | Calcular a árvore geradora mínima |
| `Dijkstra` | Calcular o caminho mínimo |
| `FluxoMaximo` | Calcular o fluxo máximo |

A classe `Algoritmos` realiza a leitura do arquivo e encaminha
as operações para as classes responsáveis. A `Main` contém
o menu, recebe as entradas e apresenta os resultados.

O arquivo `FileManager` foi preservado na base, mas a leitura
implementada utiliza a biblioteca padrão do Java.

## 3. Entrada e representações

A primeira linha do arquivo informa a quantidade de vértices.
Nas demais linhas, o primeiro número identifica a origem.
Cada par `destino-peso` representa uma aresta.

Por exemplo:

```text
3 1-42; 4-7;
```

Essa linha indica uma aresta de 3 para 1, com peso 42, e outra
de 3 para 4, com peso 7.

Os vértices são numerados de 0 até V - 1. O grafo é orientado:
adicionar uma aresta de 3 para 4 não cria automaticamente outra
de 4 para 3.

Foram implementadas as seguintes representações:

- **Matriz de adjacência:** cada posição guarda uma aresta ou
  `null`, quando não existe ligação.
- **Lista de adjacência:** cada vértice possui uma lista com
  suas arestas de saída.
- **Matriz de incidência:** cada coluna representa uma aresta.
  O valor -1 indica origem, 1 indica destino e 2 representa
  um laço. Os pesos ficam em um vetor separado.

As representações aceitam laços e pesos iguais a zero.
Arestas repetidas com a mesma origem e o mesmo destino
são rejeitadas.

O grau retornado é o grau de saída, contando cada laço uma vez.
Uma origem omitida no arquivo é considerada um vértice sem
arestas de saída.

## 4. Algoritmos implementados

### 4.1. Busca em profundidade — DFS

A busca começa no vértice escolhido pelo usuário e explora
os vértices ainda não descobertos por chamadas recursivas.

São registrados os tempos de descoberta e finalização e o pai
de cada vértice. As arestas são classificadas como árvore,
retorno, avanço ou cruzamento.

Depois da exploração inicial, a busca visita os vértices
restantes para incluir partes que não foram alcançadas
a partir da origem, formando uma floresta de busca.

### 4.2. Busca em largura — BFS

A BFS utiliza uma fila para explorar o grafo por níveis.

O resultado mostra a distância em quantidade de arestas,
a ordem de descoberta e o pai de cada vértice. Distância
e ordem de descoberta são apresentadas separadamente.

Vértices não alcançados recebem distância e ordem iguais
a -1. O pai da origem também recebe -1, pois ela não
possui predecessor.

Os pesos não são utilizados na escolha dos caminhos da BFS.

### 4.3. Existência de ciclo

A classe `ExisteCiclo` utiliza três estados para os vértices:
não visitado, em exploração e finalizado.

Uma aresta que aponta para um vértice ainda em exploração
indica um ciclo orientado. A verificação percorre todas
as partes do grafo e também identifica laços.

O resultado apresentado é verdadeiro ou falso.

### 4.4. Componentes fortemente conexos

Foi utilizado o algoritmo de Kosaraju.

Primeiro, uma busca registra a ordem de finalização dos
vértices. Depois, as arestas são invertidas e uma segunda
busca percorre os vértices na ordem inversa dessa finalização.

O programa mostra os vértices de cada componente e constrói
o grafo reduzido. Nesse grafo, cada componente passa a ser
um único vértice.

As ligações internas são removidas e as ligações repetidas
entre componentes são unificadas. O peso padrão 1 representa
apenas conectividade, sem somar os pesos originais.

### 4.5. Árvore geradora mínima — Kruskal

As arestas são ordenadas pelo peso utilizando Merge Sort.
Depois, são examinadas da menor para a maior.

Os vetores de representantes e alturas controlam os conjuntos
de vértices conectados. Uma ligação é aceita somente quando
seus extremos pertencem a conjuntos diferentes, evitando ciclos.

Em um grafo conectado com V vértices, a árvore geradora possui
V - 1 arestas. Se o grafo for desconexo, o programa informa
que não existe uma árvore geradora para todos os vértices.

Kruskal trabalha com ligações não orientadas. Como a entrada
contém arcos, o menu solicita confirmação para ignorar os
sentidos somente nesse cálculo. Cada arco passa a ser uma
ligação candidata. Essa convenção não modifica o grafo original.

O resultado apresenta as arestas escolhidas e o custo total.

### 4.6. Caminho mínimo — Dijkstra

O usuário informa os vértices de origem e destino.

O algoritmo mantém as menores distâncias conhecidas e escolhe,
por varredura, o vértice ainda não processado de menor distância.
Em seguida, verifica se suas arestas permitem melhorar o custo
de chegada aos vizinhos.

As arestas anteriores são guardadas para reconstruir o caminho.
O resultado apresenta suas arestas e a soma dos pesos.

Pesos negativos são rejeitados. Quando não existe caminho,
o programa apresenta uma mensagem. Se origem e destino
forem iguais, o caminho é vazio e tem custo zero.

### 4.7. Fluxo máximo

Foi utilizado Edmonds-Karp, com buscas em largura em uma
matriz residual.

Os pesos representam capacidades não negativas. A fonte
e o sorvedouro são informados pelo usuário e devem
ser diferentes.

Cada caminho encontrado permite aumentar o fluxo até o limite
da menor capacidade disponível nele. As capacidades residuais
são atualizadas nos sentidos direto e reverso.

O cálculo termina quando não existe mais caminho aumentante,
retornando o fluxo total encontrado.

## 5. Uso de vetores estáticos no Dijkstra e no Kruskal

Conforme a orientação recebida, Dijkstra e Kruskal foram
implementados utilizando vetores estáticos, entendidos neste
projeto como arrays de tamanho fixo durante o processamento.

No Dijkstra, esses vetores armazenam:

- As menores distâncias conhecidas.
- Os vértices já processados.
- As arestas anteriores de cada caminho.
- As arestas utilizadas na reconstrução do resultado.

No Kruskal, os vetores armazenam:

- As arestas que serão ordenadas.
- O auxiliar utilizado pelo Merge Sort.
- As arestas escolhidas para a árvore.
- Os representantes e as alturas dos conjuntos disjuntos.

Os tamanhos são definidos a partir da quantidade de vértices
e arestas, sem redimensionamento durante o processamento.

A conversão final para coleções atende aos tipos de retorno
exigidos pela interface fornecida pelo professor. As consultas
ao grafo também utilizam os retornos previstos nessa interface.

A restrição de vetores estáticos foi aplicada ao processamento
do Dijkstra e do Kruskal. A leitura do arquivo e as demais
partes do programa podem utilizar coleções.

Considerando as arestas já obtidas do grafo, os custos de
processamento são:

| Algoritmo | Complexidade |
|---|---|
| Dijkstra, com seleção por varredura | O(V² + E) |
| Kruskal, com Merge Sort e conjuntos disjuntos | O(V + E log E) |

V representa a quantidade de vértices e E, a quantidade de
arestas. O custo de consultar e preparar as arestas é adicional
e depende da representação escolhida.

## 6. Bibliografia para aprofundamento

As obras abaixo são referências para o estudo dos algoritmos
e de suas análises de complexidade.

CORMEN, Thomas H.; LEISERSON, Charles E.; RIVEST, Ronald L.;
STEIN, Clifford. *Introduction to Algorithms*. 4. ed.
Cambridge, MA: MIT Press, 2022.

KLEINBERG, Jon; TARDOS, Éva. *Algorithm Design*. 1. ed.
Boston: Pearson/Addison-Wesley, 2006.

## 7. Apoio de inteligência artificial

O ChatGPT foi utilizado como apoio ao estudo dos algoritmos,
à elaboração de trechos da implementação e à organização
e formatação deste README.#   T r a b a l h o P r a t i c o 2 - P A A  
 