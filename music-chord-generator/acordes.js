'use strict';

// Motor de acordes independente do DOM, de rede e de bibliotecas externas.
const Acordes = (() => {
    const letras = 'CDEFGAB';
    const naturais = { C: 0, D: 2, E: 4, F: 5, G: 7, A: 9, B: 11 };
    // Cada componente contém: intervalo em semitons, grau diatônico e símbolo.
    const componentes = {
        terca: {
            menor: [3, 2, 'm'], maior: [4, 2, ''],
            sus2: [2, 1, 'sus2'], sus4: [5, 3, 'sus4']
        },
        quinta: { diminuta: [6, 4, 'b5'], justa: [7, 4, ''], aumentada: [8, 4, '#5'] },
        sexta: { menor: [8, 5, 'b6'], maior: [9, 5, '6'], b13: [20, 12, 'b13'], '13': [21, 12, '13'] },
        setima: { diminuta: [9, 6, 'dim7'], menor: [10, 6, '7'], maior: [11, 6, 'maj7'] },
        nona: { menor: [13, 8, 'b9'], maior: [14, 8, '9'], aumentada: [15, 8, '#9'] },
        decimaPrimeira: { justa: [17, 10, '11'], aumentada: [18, 10, '#11'] }
    };
    const nomes = {
        terca: 'Terça', quinta: 'Quinta', sexta: 'Sexta ou décima terceira',
        setima: 'Sétima', nona: 'Nona', decimaPrimeira: 'Décima primeira'
    };
    const padroes = {
        terca: 'maior', quinta: 'justa', sexta: 'omitida', setima: 'omitida',
        nona: 'omitida', decimaPrimeira: 'omitida', baixo: 'fundamental'
    };
    const modulo = (valor, base) => ((valor % base) + base) % base;

    function lerTonica(texto) {
        const partes = typeof texto === 'string' ? /^([A-Ga-g])([#b♯♭]?)$/.exec(texto.trim()) : null;
        if (!partes) throw new Error('Tônica inválida');
        const letra = partes[1].toUpperCase();
        const simbolo = partes[2].replace('♯', '#').replace('♭', 'b');
        const alteracao = simbolo === '#' ? 1 : simbolo === 'b' ? -1 : 0;
        return { letra, nome: letra + simbolo, midi: 60 + naturais[letra] + alteracao };
    }

    function criarNota(tonica, intervalo, grau) {
        const midi = tonica.midi + intervalo;
        const letra = letras[modulo(letras.indexOf(tonica.letra) + grau, 7)];
        const diferenca = modulo(midi - naturais[letra], 12);
        const alteracao = diferenca > 6 ? diferenca - 12 : diferenca;
        if (Math.abs(alteracao) > 2) throw new Error('O acorde exige um acidente não suportado');
        const simbolo = alteracao > 0 ? '#'.repeat(alteracao) : 'b'.repeat(-alteracao);
        return {
            nome: letra + simbolo,
            oitava: Math.floor((midi - naturais[letra] - alteracao) / 12) - 1,
            midi,
            frequencia: 440 * 2 ** ((midi - 69) / 12)
        };
    }

    function gerar(parametros) {
        const tonica = lerTonica(parametros?.tonica);
        const configuracao = { ...padroes, ...parametros };
        const selecionados = [];
        for (const [grupo, opcoes] of Object.entries(componentes)) {
            const valor = configuracao[grupo];
            if (valor === 'omitida') continue;
            if (!Object.hasOwn(opcoes, valor)) throw new Error(`${nomes[grupo]} inválida`);
            const [intervalo, grau, simbolo] = opcoes[valor];
            selecionados.push({ grupo, intervalo, grau, simbolo });
        }
        const baixo = configuracao.baixo === 'decima-primeira' ? 'decimaPrimeira' : configuracao.baixo;
        if (baixo !== 'fundamental' && !Object.hasOwn(componentes, baixo)) {
            throw new Error('Inversão inválida');
        }
        if (baixo !== 'fundamental' && !selecionados.some((item) => item.grupo === baixo)) {
            throw new Error('A nota escolhida para o baixo foi omitida do acorde');
        }

        const ordenados = [...selecionados].sort((a, b) => a.intervalo - b.intervalo);
        const notas = [criarNota(tonica, 0, 0), ...ordenados.map((item) => criarNota(tonica, item.intervalo, item.grau))];
        if (baixo !== 'fundamental') {
            const indice = ordenados.findIndex((item) => item.grupo === baixo) + 1;
            const anteriores = notas.splice(0, indice);
            const midiDoBaixo = notas[0].midi;
            for (const nota of anteriores) {
                while (nota.midi <= midiDoBaixo) {
                    nota.midi += 12;
                    nota.oitava += 1;
                }
                nota.frequencia = 440 * 2 ** ((nota.midi - 69) / 12);
                notas.push(nota);
            }
        }

        let cifra = tonica.nome;
        const sufixosTerca = { menor: 'm', maior: '', sus2: 'sus2', sus4: 'sus4', omitida: '(no3)' };
        cifra += sufixosTerca[configuracao.terca];
        for (const item of selecionados) {
            if (item.grupo !== 'terca' && !(item.grupo === 'quinta' && configuracao.quinta === 'justa')) {
                cifra += `(${item.simbolo})`;
            }
        }
        if (configuracao.quinta === 'omitida') cifra += '(no5)';
        if (baixo !== 'fundamental') cifra += `/${notas[0].nome}`;
        return { tonica: tonica.nome, tipo: 'personalizado', cifra, notas };
    }

    return { gerar };
})();

// Permite testar o mesmo arquivo com Node, sem exigir Node para abrir a página.
if (typeof module !== 'undefined' && module.exports) module.exports = Acordes;
