const test = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');
const { gerar } = require('../acordes.js');
const nomes = (acorde) => acorde.notas.map((nota) => nota.nome);

test('tríade padrão e Lá4 a 440 Hz', () => {
    assert.deepEqual(nomes(gerar({ tonica: 'C' })), ['C', 'E', 'G']);
    assert.equal(gerar({ tonica: 'C' }).cifra, 'C');
    const la = gerar({ tonica: 'A', terca: 'menor' }).notas[0];
    assert.deepEqual(la, { nome: 'A', oitava: 4, midi: 69, frequencia: 440 });
});

test('grafia de acidentes duplos e fronteiras de oitava', () => {
    assert.deepEqual(nomes(gerar({ tonica: 'G#', setima: 'maior' })), ['G#', 'B#', 'D#', 'F##']);
    assert.deepEqual(nomes(gerar({ tonica: 'C', terca: 'menor', quinta: 'diminuta', setima: 'diminuta' })), ['C', 'Eb', 'Gb', 'Bbb']);
    for (const [tonica, midi] of [['Cb', 59], ['B#', 72]]) {
        const nota = gerar({ tonica }).notas[0];
        assert.equal(nota.midi, midi);
        assert.equal(nota.oitava, 4);
    }
    assert.equal(gerar({ tonica: ' g♯ ' }).tonica, 'G#');
    assert.equal(gerar({ tonica: 'G♭' }).tonica, 'Gb');
});

test('extensões alteradas e cifra preservam a implementação Java', () => {
    const acorde = gerar({ tonica: 'C', quinta: 'aumentada', setima: 'maior', nona: 'aumentada', decimaPrimeira: 'aumentada' });
    assert.equal(acorde.cifra, 'C(#5)(maj7)(#9)(#11)');
    assert.deepEqual(nomes(acorde), ['C', 'E', 'G#', 'B', 'D#', 'F#']);
    assert.deepEqual(acorde.notas.map((n) => n.midi), [60, 64, 68, 71, 75, 78]);
});

test('inversão mantém a grafia, a ordem anterior e as alturas', () => {
    const acorde = gerar({ tonica: 'C', terca: 'menor', quinta: 'diminuta', setima: 'menor', nona: 'maior', baixo: 'terca' });
    assert.equal(acorde.cifra, 'Cm(b5)(7)(9)/Eb');
    assert.deepEqual(nomes(acorde), ['Eb', 'Gb', 'Bb', 'D', 'C']);
    assert.deepEqual(acorde.notas.map((n) => n.midi), [63, 66, 70, 74, 72]);
});

test('inversão na 13ª eleva as notas por mais de uma oitava', () => {
    const acorde = gerar({ tonica: 'C', sexta: '13', baixo: 'sexta' });
    assert.equal(acorde.cifra, 'C(13)/A');
    assert.deepEqual(acorde.notas.map((n) => n.midi), [81, 84, 88, 91]);
    assert.deepEqual(acorde.notas.map((n) => n.oitava), [5, 6, 6, 6]);
});

test('omissões e entradas inválidas', () => {
    const acorde = gerar({ tonica: 'C', terca: 'omitida', quinta: 'omitida' });
    assert.equal(acorde.cifra, 'C(no3)(no5)');
    assert.deepEqual(nomes(acorde), ['C']);
    for (const tonica of [null, '', 'X', 'C##', 'Cb#']) assert.throws(() => gerar({ tonica }), /Tônica inválida/);
    assert.throws(() => gerar({ tonica: 'C', quinta: 'inexistente' }), /Quinta inválida/);
    assert.throws(() => gerar({ tonica: 'C', baixo: 'setima' }), /omitida/);
    assert.throws(() => gerar({ tonica: 'C', baixo: 'inexistente' }), /Inversão inválida/);
    assert.throws(() => gerar({ tonica: 'Fb', setima: 'diminuta' }), /acidente não suportado/);
});

test('os 20 atalhos da interface geram acordes em todas as 21 fundamentais', () => {
    const html = fs.readFileSync(path.join(__dirname, '../index.html'), 'utf8');
    const configuracoes = vm.runInNewContext(html.match(/const configuracoesSimples = (\{[\s\S]*?\n    \});/)[1].replace(/^/, '(') + ')');
    assert.equal(Object.keys(configuracoes).length, 20);
    let gerados = 0;
    let limitados = 0;
    for (const letra of 'CDEFGAB') for (const acidente of ['', '#', 'b']) {
        for (const configuracao of Object.values(configuracoes)) {
            try {
                const acorde = gerar({ tonica: letra + acidente, ...configuracao });
                assert.equal(acorde.notas[0].nome, letra + acidente);
                for (const nota of acorde.notas) assert.ok(Number.isFinite(nota.frequencia) && nota.frequencia > 0);
                gerados++;
            } catch (erro) {
                // Certas grafias exigem acidentes triplos, também rejeitados pelo Java.
                assert.match(erro.message, /acidente não suportado/);
                limitados++;
            }
        }
    }
    assert.equal(gerados + limitados, 420);
    assert.ok(gerados > 400);
});
