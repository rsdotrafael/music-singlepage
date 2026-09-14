# Oficina Musical

Página principal para acessar os geradores de escalas e acordes em um único lugar.
Funciona como site estático, sem instalação, servidor Java ou acesso à internet.

## Abrir

Abra `index.html` na raiz do projeto ou execute nesta pasta:

```powershell
Start-Process .\index.html
```

O gerador de escalas abre por padrão. Use o menu para alternar para acordes.
Cada gerador ocupa a área principal em um iframe. Ao trocar de aplicativo,
o anterior é descarregado, interrompendo seu áudio e reiniciando seu estado.
O histórico do navegador permite voltar e avançar entre aplicativos.

Em telas pequenas, o menu aparece no topo para dar espaço ao gerador.
Os geradores também continuam funcionando por seus próprios `index.html`.

Os endereços `index.html#escalas` e `index.html#acordes` abrem a página principal
diretamente na ferramenta escolhida. O link **Abrir em nova aba** abre apenas
o gerador, sem o menu principal.

## Arquitetura e documentação

Este é um único projeto Git com uma página principal e dois aplicativos estáticos
independentes. Não há etapa de compilação nem API de backend.

| Arquivo ou pasta | Responsabilidade |
|---|---|
| `index.html` | Estrutura da página principal e iframe |
| `app.css` | Identidade visual e layout do menu e da área principal |
| `app.js` | Catálogo de aplicativos, menu, seleção e navegação por fragmentos da URL |
| `music-scale-generator/` | Interface, lógica musical, documentação e testes de escalas |
| `music-chord-generator/` | Interface, lógica musical, documentação e testes de acordes |

Cada gerador contém seu próprio `index.html`, motor JavaScript, `README.md`,
pasta `tests/` e configurações Git. Os estilos e o áudio de cada gerador ficam
no seu HTML; `app.css` estiliza somente a página principal, pois o iframe
possui um documento independente.

As instruções gerais ficam neste README. Consulte os detalhes de cada aplicativo:

- [Gerador de escalas](music-scale-generator/README.md): catálogo, cálculo, limitações e testes.
- [Gerador de acordes](music-chord-generator/README.md): modos, componentes, inversões e testes.

## Publicação

Publique `index.html`, `app.css`, `app.js` e as duas pastas dos geradores,
mantendo os caminhos relativos. Para cada gerador bastam seu HTML e JavaScript;
documentação e testes não são necessários para a hospedagem.

Estrutura mínima de publicação do conjunto:

```text
index.html
app.css
app.js
music-scale-generator/
    index.html
    escalas.js
music-chord-generator/
    index.html
    acordes.js
```

Mantenha os nomes e a hierarquia das pastas: `app.js` usa esses caminhos para
carregar os geradores. A publicação do conjunto também funciona em subdiretórios.

## Adicionar aplicativos

Inclua uma entrada em `aplicativos`, no arquivo `app.js`, com identificador único,
nome, categoria, símbolo, título acessível e caminho relativo da página.
O menu agrupa automaticamente as entradas por categoria. Há apenas dois
aplicativos disponíveis por enquanto; novas categorias podem ser adicionadas
conforme as ferramentas forem desenvolvidas.

Mantenha a ferramenta independente, com recursos carregados por caminhos relativos
à sua própria página, e documente seu uso e testes na respectiva pasta.
Ao alterar cores ou tipografia comuns, atualize `app.css` e os estilos dos
geradores para preservar a identidade visual. Não é necessário modificar o
menu para adicionar um novo tipo de escala ou acorde a um gerador existente.

## Testes dos geradores

Com Node.js instalado, execute a partir da raiz de `music-singlepage`:

```powershell
node --test music-scale-generator/tests/escalas.test.cjs music-chord-generator/tests/acordes.test.cjs
```

Node.js é necessário apenas para os testes. Para verificar a integração, abra a
página principal, alterne entre os geradores, use voltar/avançar e confira o menu
em uma tela estreita. Teste também a reprodução e sua interrupção ao trocar de aplicativo.
