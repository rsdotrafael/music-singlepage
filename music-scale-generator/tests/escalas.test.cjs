const test = require('node:test');
const assert = require('node:assert/strict');
const { gerar, listarTipos } = require('../escalas.js');
const nomes = (tipo, tonica = 'C') => gerar(tipo, tonica).notas.map(n => n.nome).join(' - ');

test('catálogo com 24 tipos e cópias independentes', () => {
    const tipos = listarTipos();
    assert.equal(tipos.length, 24);
    assert.equal(new Set(tipos.map(t => t.id)).size, 24);
    assert.deepEqual(tipos[0], { id: 'maior', nome: 'Maior' });
    tipos[0].id = 'alterado';
    assert.equal(listarTipos()[0].id, 'maior');
});

test('escalas maiores com bemóis e sustenidos duplos', () => {
    assert.equal(nomes('maior', 'F'), 'F - G - A - Bb - C - D - E - F');
    assert.equal(nomes('maior', 'Gb'), 'Gb - Ab - Bb - Cb - Db - Eb - F - Gb');
    assert.equal(nomes('maior', 'G#'), 'G# - A# - B# - C# - D# - E# - F## - G#');
});

test('três formas de escala menor', () => {
    assert.equal(nomes('menor-natural', 'A'), 'A - B - C - D - E - F - G - A');
    assert.equal(nomes('menor-melodica', 'A'), 'A - B - C - D - E - F# - G# - A');
    assert.equal(nomes('menor-harmonica', 'A'), 'A - B - C - D - E - F - G# - A');
});

test('sete modos diatônicos', () => {
    const exemplos = {
        'modal-jonio': 'C - D - E - F - G - A - B - C',
        'modal-dorico': 'C - D - Eb - F - G - A - Bb - C',
        'modal-frigio': 'C - Db - Eb - F - G - Ab - Bb - C',
        'modal-lidio': 'C - D - E - F# - G - A - B - C',
        'modal-mixolidio': 'C - D - E - F - G - A - Bb - C',
        'modal-eolio': 'C - D - Eb - F - G - Ab - Bb - C',
        'modal-locrio': 'C - Db - Eb - F - Gb - Ab - Bb - C'
    };
    for (const [tipo, esperado] of Object.entries(exemplos)) assert.equal(nomes(tipo), esperado);
});

test('escalas com diferentes quantidades de notas e graus repetidos', () => {
    const exemplos = {
        'dom-dim': 'C - Db - Eb - E - F# - G - A - Bb - C',
        'dim-dom': 'C - D - Eb - F - Gb - Ab - A - B - C',
        'blues-menor': 'C - Eb - F - Gb - G - Bb - C',
        'blues-maior': 'C - D - Eb - E - G - A - C',
        'frigio-dominante': 'C - Db - E - F - G - Ab - Bb - C',
        'lidio-dominante': 'C - D - E - F# - G - A - Bb - C',
        alterada: 'C - Db - Eb - E - Gb - Ab - Bb - C',
        'bebop-dominante': 'C - D - E - F - G - A - Bb - B - C',
        cromatica: 'C - C# - D - D# - E - F - F# - G - G# - A - A# - B - C',
        aumentada: 'C - Eb - E - G - Ab - B - C',
        'pentatonica-maior': 'C - D - E - G - A - C',
        'pentatonica-menor': 'C - Eb - F - G - Bb - C',
        'tons-inteiros': 'C - D - E - F# - G# - A# - C'
    };
    for (const [tipo, esperado] of Object.entries(exemplos)) assert.equal(nomes(tipo), esperado);
});

test('oitavas escritas em Cb e B# e frequência de referência', () => {
    for (const [tonica, midi] of [['Cb', 59], ['B#', 72]]) {
        const notas = gerar('maior', tonica).notas;
        assert.equal(notas[0].midi, midi);
        assert.equal(notas[0].oitava, 4);
        assert.equal(notas.at(-1).midi, midi + 12);
        assert.equal(notas.at(-1).oitava, 5);
    }
    assert.deepEqual(gerar('maior', 'A').notas[0], { nome: 'A', oitava: 4, midi: 69, frequencia: 440 });
    assert.ok(Math.abs(gerar('maior', 'F#').notas[0].frequencia - 369.994) < 0.001);
});

test('normalização de tônicas e entradas inválidas', () => {
    assert.equal(gerar('maior', ' g♯ ').tonica, 'G#');
    assert.equal(gerar('maior', 'G♭').tonica, 'Gb');
    assert.throws(() => gerar('inexistente', 'C'), /Tipo de escala inválido/);
    for (const tonica of [undefined, null, '', ' ', 'X', 'C##', 'Cb#', 'C4']) {
        assert.throws(() => gerar('maior', tonica), /Tônica inválida/);
    }
});

test('504 combinações: 498 escalas válidas e seis limitações de grafia preservadas', () => {
    let quantidade = 0;
    for (const { id } of listarTipos()) for (const letra of 'CDEFGAB') for (const acidente of ['', '#', 'b']) {
        const tonica = letra + acidente;
        if (['cromatica', 'tons-inteiros'].includes(id) && ['E#', 'A#', 'B#'].includes(tonica)) {
            assert.throws(() => gerar(id, tonica), /acidente ainda não suportado/);
            quantidade++;
            continue;
        }
        const notas = gerar(id, tonica).notas;
        assert.equal(notas[0].nome, tonica);
        assert.equal(notas.at(-1).nome, tonica);
        assert.equal(notas.at(-1).midi - notas[0].midi, 12);
        for (let i = 1; i < notas.length; i++) assert.ok(notas[i].midi > notas[i - 1].midi);
        quantidade++;
    }
    assert.equal(quantidade, 504);
});
