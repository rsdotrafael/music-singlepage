# Gerador de Escalas Musicais

Página estática, sem servidor ou conexão com a internet, para gerar, visualizar e ouvir escalas musicais com diferentes quantidades de notas e grafia correta de sustenidos e bemóis.

Este aplicativo faz parte da [Oficina Musical](../README.md). Este README documenta
o gerador; a documentação da raiz descreve a navegação e a publicação do conjunto.

## Funcionalidades

- geração de escalas maiores e menores;
- suporte a escalas pentatônicas e de tons inteiros;
- suporte aos sete modos diatônicos e às escalas octatônicas dom-dim e dim-dom;
- escalas blues, dominantes modernas, bebop dominante, cromática e aumentada;
- catálogo declarativo, extensível sem criar um novo algoritmo para cada escala;
- suporte às 21 grafias de tônica natural, sustenida ou bemolizada, de `C` a `B`;
- uso automático de sustenidos, bemóis, sustenidos duplos e bemóis duplos quando necessários;
- cálculo da oitava pelo intervalo absoluto, do número MIDI e da frequência;
- reprodução sequencial no navegador pela Web Audio API;
- timbres sintetizados de piano e violino, controle de volume e interrupção;
- catálogo de tipos de escala carregado localmente;
- interface responsiva, sem framework JavaScript e sem banco de dados.

Tipos disponíveis:

| Identificador | Nome |
|---|---|
| `maior` | Maior |
| `menor-natural` | Menor natural |
| `menor-melodica` | Menor melódica ascendente |
| `menor-harmonica` | Menor harmônica |
| `modal-jonio` | Modal - Jônio |
| `modal-dorico` | Modal - Dórico |
| `modal-frigio` | Modal - Frígio |
| `modal-lidio` | Modal - Lídio |
| `modal-mixolidio` | Modal - Mixolídio |
| `modal-eolio` | Modal - Eólio |
| `modal-locrio` | Modal - Lócrio |
| `dom-dim` | Dom dim (semitom-tom) |
| `dim-dom` | Dim dom (tom-semitom) |
| `blues-menor` | Blues menor |
| `blues-maior` | Blues maior |
| `frigio-dominante` | Frígio dominante |
| `lidio-dominante` | Lídio dominante |
| `alterada` | Alterada (superlócria) |
| `bebop-dominante` | Bebop dominante |
| `cromatica` | Cromática |
| `aumentada` | Aumentada |
| `pentatonica-maior` | Pentatônica maior |
| `pentatonica-menor` | Pentatônica menor |
| `tons-inteiros` | Tons inteiros |

Exemplos:

```text
F maior              -> F - G - A - Bb - C - D - E - F
G# maior             -> G# - A# - B# - C# - D# - E# - F## - G#
A menor harmônica   -> A - B - C - D - E - F - G# - A
C pentatônica maior -> C - D - E - G - A - C
C tons inteiros      -> C - D - E - F# - G# - A# - C
```

## Como executar

Para usar com o menu principal, abra o `index.html` da raiz: **Escalas** é o
aplicativo inicial. O endereço `index.html#escalas` da página principal também
seleciona este gerador.

Para abrir apenas o gerador, abra `index.html` desta pasta. No PowerShell,
a partir da raiz de `music-singlepage`:

```powershell
Start-Process .\music-scale-generator\index.html
```

Não é necessário instalar Java, Maven, Node ou iniciar um servidor local para usar
a página. O som usa a Web Audio API e começa após clicar em **Ouvir escala**.

Para publicar apenas este gerador, publique `index.html` e `escalas.js` juntos.
Os caminhos são relativos, permitindo publicar em um subdiretório.
A documentação e os testes não precisam ser publicados.

Para publicar a Oficina Musical completa, siga a estrutura do [README principal](../README.md#publicação),
mantendo estes arquivos dentro de `music-scale-generator/`.

Na página principal, o gerador é carregado em um iframe. Trocar para outro
aplicativo interrompe o áudio e descarta as escolhas atuais; retornar abre uma
nova instância. Use **Abrir em nova aba** para usar o gerador separadamente.

## Como usar

1. Selecione a tônica e um dos 24 tipos de escala.
2. Clique em **Gerar escala**.
3. Escolha piano ou violino, ajuste o volume e clique em **Ouvir escala**.
4. Use **Parar** para interromper a reprodução.

## Motor JavaScript

```javascript
Escalas.listarTipos();
Escalas.gerar('maior', 'F#');
Escalas.gerar('pentatonica-menor', 'C');
```

`listarTipos()` retorna objetos com `id` e `nome`.
`gerar(tipo, tonica)` retorna `{ tonica, notas }`; cada nota contém
`nome`, `oitava`, `midi` e `frequencia` em hertz, com A4 = 440 Hz.
A tônica começa na oitava escrita 4, inclusive nas grafias Cb e B#.

São aceitas letras A–G, naturais, com # ou b, e os símbolos ♯ ou ♭.
Espaços nas extremidades e letras minúsculas são normalizados.
Tipos desconhecidos, tônicas inválidas e grafias não suportadas lançam erros
apresentados pela interface.

### Limitação de grafia

Assim como na implementação anterior, o motor suporta até acidentes duplos.
As escalas cromática e de tons inteiros em E#, A# e B# exigem acidentes triplos
com os deslocamentos diatônicos do catálogo e são rejeitadas. Nesses casos,
selecione a tônica enarmônica F, Bb ou C, respectivamente.

## Estrutura e manutenção

- `index.html`: interface, estilos e reprodução de áudio;
- `escalas.js`: catálogo, validação e cálculo musical;
- `tests/escalas.test.cjs`: testes automatizados sem dependências externas;
- `README.md`: instruções de uso e manutenção;
- `.gitignore` e `.gitattributes`: configurações de versionamento.

O menu e a seleção de aplicativos pertencem a `../app.js`; o layout da página
principal pertence a `../app.css`. Os estilos deste gerador estão no seu próprio
`index.html` e não herdam o CSS da página principal. Ao alterar a identidade visual
comum, mantenha os estilos dos dois geradores e da página principal consistentes.

O catálogo em `escalas.js` separa intervalos em semitons de deslocamentos
diatônicos. Os intervalos determinam a altura sonora; os deslocamentos determinam
a letra escrita. Isso permite representar escalas pentatônicas, cromáticas e
outras sem pressupor sete notas diferentes.

Para adicionar um tipo, inclua no catálogo uma definição com `heptatonica`
(deslocamentos de 0 a 7) ou `partes` (deslocamentos explícitos), por exemplo:

```javascript
partes('pentatonica-maior', 'Pentatônica maior',
    [0, 2, 4, 7, 9, 12], [0, 1, 2, 4, 5, 7])
```

Use um identificador único. Intervalos e deslocamentos devem ter o mesmo tamanho
e começar em zero. Acrescente testes com resultados musicais esperados.
A interface lista o catálogo automaticamente.

## Testes

Com Node.js instalado, execute a partir de `music-singlepage`:

```powershell
node --test music-scale-generator/tests/escalas.test.cjs
```

Os testes cobrem os 24 tipos, as 21 tônicas, grafias, oitavas, MIDI, frequências,
normalização e entradas inválidas. Node é necessário apenas para os testes.

A implementação Java/Maven e seus arquivos de execução foram removidos após
a comparação dos resultados com a versão JavaScript. A página não consulta APIs
nem depende de banco de dados, login ou serviço externo de áudio.
