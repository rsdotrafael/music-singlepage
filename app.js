'use strict';

// Inclua novas ferramentas aqui; o menu é agrupado automaticamente por categoria.
const aplicativos = [
    { id: 'escalas', nome: 'Escalas', categoria: 'Geradores', simbolo: '♮', titulo: 'Gerador de escalas musicais', caminho: './music-scale-generator/index.html' },
    { id: 'acordes', nome: 'Acordes', categoria: 'Geradores', simbolo: '♬', titulo: 'Gerador de acordes musicais', caminho: './music-chord-generator/index.html' }
];

const menu = document.querySelector('#menu-aplicativos');
const frame = document.querySelector('#aplicativo');
const abrir = document.querySelector('#abrir-aplicativo');
let aplicativoAtual = 'escalas';

menu.replaceChildren();
for (const categoria of new Set(aplicativos.map(app => app.categoria))) {
    const titulo = document.createElement('h2');
    titulo.textContent = categoria;
    menu.append(titulo);
    for (const app of aplicativos.filter(app => app.categoria === categoria)) {
        const link = document.createElement('a');
        link.className = 'item-menu';
        link.href = `#${app.id}`;
        link.dataset.aplicativo = app.id;
        const simbolo = document.createElement('span');
        simbolo.className = 'item-simbolo';
        simbolo.setAttribute('aria-hidden', 'true');
        simbolo.textContent = app.simbolo;
        const nome = document.createElement('span');
        nome.textContent = app.nome;
        link.append(simbolo, nome);
        menu.append(link);
    }
}

function selecionarAplicativo() {
    const app = aplicativos.find(item => `#${item.id}` === location.hash) || aplicativos[0];
    for (const link of menu.querySelectorAll('[data-aplicativo]')) {
        if (link.dataset.aplicativo === app.id) link.setAttribute('aria-current', 'page');
        else link.removeAttribute('aria-current');
    }
    document.querySelector('#categoria-atual').textContent = app.categoria;
    document.querySelector('#nome-atual').textContent = app.nome;
    document.title = `${app.nome} · Oficina Musical`;
    frame.title = app.titulo;
    abrir.href = app.caminho;
    abrir.setAttribute('aria-label', `Abrir ${app.nome} em uma nova aba`);
    if (aplicativoAtual !== app.id) {
        // Um único iframe: ao trocar de aplicativo, seu áudio e estado são encerrados.
        frame.src = app.caminho;
        aplicativoAtual = app.id;
    }
}

window.addEventListener('hashchange', selecionarAplicativo);
selecionarAplicativo();
