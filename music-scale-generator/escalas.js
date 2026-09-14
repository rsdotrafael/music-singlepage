'use strict';

// Catálogo e cálculos locais, independentes do DOM e de serviços externos.
const Escalas = (() => {
    const heptatonica = (id, nome, intervalos) => ({
        id, nome, intervalos, deslocamentos: [0, 1, 2, 3, 4, 5, 6, 7]
    });
    const partes = (id, nome, intervalos, deslocamentos) => ({ id, nome, intervalos, deslocamentos });
    const tipos = [
        heptatonica('maior', 'Maior', [0, 2, 4, 5, 7, 9, 11, 12]),
        heptatonica('menor-natural', 'Menor natural', [0, 2, 3, 5, 7, 8, 10, 12]),
        heptatonica('menor-melodica', 'Menor melódica', [0, 2, 3, 5, 7, 9, 11, 12]),
        heptatonica('menor-harmonica', 'Menor harmônica', [0, 2, 3, 5, 7, 8, 11, 12]),
        heptatonica('modal-jonio', 'Modal - Jônio', [0, 2, 4, 5, 7, 9, 11, 12]),
        heptatonica('modal-dorico', 'Modal - Dórico', [0, 2, 3, 5, 7, 9, 10, 12]),
        heptatonica('modal-frigio', 'Modal - Frígio', [0, 1, 3, 5, 7, 8, 10, 12]),
        heptatonica('modal-lidio', 'Modal - Lídio', [0, 2, 4, 6, 7, 9, 11, 12]),
        heptatonica('modal-mixolidio', 'Modal - Mixolídio', [0, 2, 4, 5, 7, 9, 10, 12]),
        heptatonica('modal-eolio', 'Modal - Eólio', [0, 2, 3, 5, 7, 8, 10, 12]),
        heptatonica('modal-locrio', 'Modal - Lócrio', [0, 1, 3, 5, 6, 8, 10, 12]),
        partes('dom-dim', 'Dom dim (semitom-tom)', [0, 1, 3, 4, 6, 7, 9, 10, 12], [0, 1, 2, 2, 3, 4, 5, 6, 7]),
        partes('dim-dom', 'Dim dom (tom-semitom)', [0, 2, 3, 5, 6, 8, 9, 11, 12], [0, 1, 2, 3, 4, 5, 5, 6, 7]),
        partes('blues-menor', 'Blues menor', [0, 3, 5, 6, 7, 10, 12], [0, 2, 3, 4, 4, 6, 7]),
        partes('blues-maior', 'Blues maior', [0, 2, 3, 4, 7, 9, 12], [0, 1, 2, 2, 4, 5, 7]),
        heptatonica('frigio-dominante', 'Frígio dominante', [0, 1, 4, 5, 7, 8, 10, 12]),
        heptatonica('lidio-dominante', 'Lídio dominante', [0, 2, 4, 6, 7, 9, 10, 12]),
        partes('alterada', 'Alterada (superlócria)', [0, 1, 3, 4, 6, 8, 10, 12], [0, 1, 2, 2, 4, 5, 6, 7]),
        partes('bebop-dominante', 'Bebop dominante', [0, 2, 4, 5, 7, 9, 10, 11, 12], [0, 1, 2, 3, 4, 5, 6, 6, 7]),
        partes('cromatica', 'Cromática', [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12], [0, 0, 1, 1, 2, 3, 3, 4, 4, 5, 5, 6, 7]),
        partes('aumentada', 'Aumentada', [0, 3, 4, 7, 8, 11, 12], [0, 2, 2, 4, 5, 6, 7]),
        partes('pentatonica-maior', 'Pentatônica maior', [0, 2, 4, 7, 9, 12], [0, 1, 2, 4, 5, 7]),
        partes('pentatonica-menor', 'Pentatônica menor', [0, 3, 5, 7, 10, 12], [0, 2, 3, 4, 6, 7]),
        partes('tons-inteiros', 'Tons inteiros', [0, 2, 4, 6, 8, 10, 12], [0, 1, 2, 3, 4, 5, 7])
    ];
    const letras = 'CDEFGAB';
    const naturais = { C: 0, D: 2, E: 4, F: 5, G: 7, A: 9, B: 11 };
    const modulo = (valor, base) => ((valor % base) + base) % base;

    function gerar(tipo, tonica) {
        const definicao = tipos.find((item) => item.id === tipo);
        if (!definicao) throw new Error('Tipo de escala inválido');
        const entrada = typeof tonica === 'string' ? /^([A-Ga-g])([#b♯♭]?)$/.exec(tonica.trim()) : null;
        if (!entrada) throw new Error('Tônica inválida');
        const letraInicial = entrada[1].toUpperCase();
        const simbolo = entrada[2].replace('♯', '#').replace('♭', 'b');
        const alteracaoInicial = simbolo === '#' ? 1 : simbolo === 'b' ? -1 : 0;
        // A oitava escrita da tônica é 4, inclusive em Cb e B#.
        const midiInicial = 60 + naturais[letraInicial] + alteracaoInicial;
        const notas = definicao.intervalos.map((intervalo, indice) => {
            const letra = letras[modulo(letras.indexOf(letraInicial) + definicao.deslocamentos[indice], 7)];
            const midi = midiInicial + intervalo;
            const diferenca = modulo(midi - naturais[letra], 12);
            const alteracao = diferenca > 6 ? diferenca - 12 : diferenca;
            if (Math.abs(alteracao) > 2) throw new Error('A escala exige um acidente ainda não suportado');
            const acidente = alteracao > 0 ? '#'.repeat(alteracao) : 'b'.repeat(-alteracao);
            return {
                nome: letra + acidente,
                oitava: Math.floor((midi - naturais[letra] - alteracao) / 12) - 1,
                midi,
                frequencia: 440 * 2 ** ((midi - 69) / 12)
            };
        });
        return { tonica: letraInicial + simbolo, notas };
    }

    return { listarTipos: () => tipos.map(({ id, nome }) => ({ id, nome })), gerar };
})();

// Node é usado somente pelos testes; o navegador carrega este arquivo diretamente.
if (typeof module !== 'undefined' && module.exports) module.exports = Escalas;
