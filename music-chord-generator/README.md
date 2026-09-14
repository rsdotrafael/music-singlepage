# Gerador de Acordes Musicais

Página estática para construir, visualizar e ouvir acordes personalizados. A geração
e o áudio funcionam no navegador, sem servidor Java, instalação ou conexão com a internet.

Este aplicativo faz parte da [Oficina Musical](../README.md). Este README documenta
o gerador; a documentação da raiz descreve a navegação e a publicação do conjunto.

## Funcionalidades

- modo simples com seleção da fundamental e de 20 tipos comuns de acordes;
- modo avançado com controle independente de terça, quinta, sexta ou 13ª, sétima, nona e 11ª;
- escolha entre 21 grafias da fundamental;
- omissão de notas opcionais e inversões;
- grafia com sustenidos, bemóis e acidentes duplos;
- cálculo de oitava, MIDI e frequência;
- reprodução sequencial ou simultânea;
- timbres de piano e violino, volume e interrupção.

## Modos de geração

### Modo Simples

Permite escolher a fundamental e gerar rapidamente um dos 20 tipos mais comuns:

- maior, menor, diminuto e aumentado;
- sus2 e sus4;
- 6 e m6;
- maj7, 7, m7, m7♭5 e dim7;
- add9, madd9, maj9, 9, m9, 7♭9 e 7♯9.

### Modo Avançado

Permite montar o acorde componente por componente, selecionar omissões, extensões,
alterações e a nota do baixo para criar inversões. Os controles de reprodução
são compartilhados pelos dois modos.

## Tecnologias

- HTML, CSS, JavaScript e Web Audio API;
- Node.js apenas para executar os testes JavaScript (opcional).

## Como executar

Para usar com o menu principal, abra o `index.html` da raiz e escolha **Acordes**.
O endereço `index.html#acordes` da página principal também seleciona este gerador.

Para abrir apenas o gerador, abra `index.html` desta pasta. No PowerShell,
a partir da raiz de `music-singlepage`:

```powershell
Start-Process .\music-chord-generator\index.html
```

Esse arquivo contém a interface e carrega `acordes.js` da mesma pasta por um caminho relativo.
Não é necessário executar Maven, Java ou um servidor local. O som começa após clicar
em **Ouvir acorde**, em um navegador com suporte à Web Audio API.

Para publicar apenas este gerador, publique `index.html` e `acordes.js` juntos.
A página funciona também em subdiretórios. O README e os testes são materiais
de desenvolvimento e não precisam ser publicados.

Para publicar a Oficina Musical completa, siga a estrutura do [README principal](../README.md#publicação),
mantendo estes arquivos dentro de `music-chord-generator/`.

Na página principal, o gerador é carregado em um iframe. Trocar para outro
aplicativo interrompe o áudio e descarta as escolhas atuais; retornar abre uma
nova instância. Use **Abrir em nova aba** para usar o gerador separadamente.

## Testes

Com Node.js instalado, execute a partir da raiz de `music-singlepage`:

```powershell
node --test music-chord-generator/tests/acordes.test.cjs
```

Node.js é necessário apenas para os testes. A suíte verifica notas, cifras,
inversões, frequências, validações e os 20 atalhos do modo simples nas 21 fundamentais.

## Motor JavaScript

### Construir um acorde

```javascript
Acordes.gerar({
    tonica: 'C', terca: 'maior', quinta: 'justa',
    setima: 'menor', nona: 'menor', decimaPrimeira: 'aumentada'
});
```

Na interface, a nota-base é apresentada como **Fundamental**. Por compatibilidade, o
nome do parâmetro correspondente no motor permanece `tonica`.

Valores aceitos:

- `terca`: `omitida`, `menor`, `maior`, `sus2` ou `sus4`;
- `quinta`: `omitida`, `diminuta`, `justa` ou `aumentada`;
- `sexta`: `omitida`, `menor`, `maior`, `b13` ou `13`;
- `setima`: `omitida`, `diminuta`, `menor` ou `maior`;
- `nona`: `omitida`, `menor`, `maior` ou `aumentada`;
- `decimaPrimeira`: `omitida`, `justa` ou `aumentada`;
- `baixo`: `fundamental`, `terca`, `quinta`, `sexta`, `setima`, `nona` ou `decima-primeira`.

O baixo escolhido precisa estar presente no acorde.

Os componentes não informados usam os padrões: terça maior, quinta justa,
demais extensões omitidas e fundamental no baixo. O resultado contém `tonica`,
`tipo`, `cifra` e `notas`; cada nota informa `nome`, `oitava`, `midi` e `frequencia`.

## Arquitetura

- `acordes.js` calcula as notas, cifras, inversões,
  oitavas, MIDI e frequências, além de validar as escolhas;
- `index.html` contém a interface, os 20 atalhos do
  modo simples e o sintetizador de áudio;
- `tests/acordes.test.cjs` verifica o motor sem dependências externas.

O menu e a seleção de aplicativos pertencem a `../app.js`; o layout da página
principal pertence a `../app.css`. Os estilos deste gerador estão no seu próprio
`index.html` e não herdam o CSS da página principal. Ao alterar a identidade visual
comum, mantenha os estilos dos dois geradores e da página principal consistentes.

Alterações na lógica musical devem ser feitas em `acordes.js` e acompanhadas
pelos testes em `tests/acordes.test.cjs`. A implementação Java/Maven anterior
foi removida após a migração; a página não consulta `/api/acordes`.

O projeto não exige banco de dados, autenticação ou serviço externo de áudio.
