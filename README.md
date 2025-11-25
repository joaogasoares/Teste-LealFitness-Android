# Leal Treinos (LealFitness) 

Solução desenvolvida para o desafio técnico Android da **Leal Apps**.

O aplicativo é um gerenciador de fichas de treino que permite ao usuário criar rotinas, definir dias da semana e adicionar exercícios com fotos, persistindo todos os dados na nuvem.

## Funcionalidades

* **Gestão de Treinos (CRUD):** Criação, leitura e exclusão de fichas de treino.
* **Seletor de Dias:** Interface visual para marcar os dias da semana de cada treino.
* **Exercícios (1:N):** Adição de múltiplos exercícios dentro de um treino específico.
* **Imagens:** Upload de fotos dos exercícios (via Galeria) com integração ao Firebase Storage.

## Tech Stack & Arquitetura

O projeto foi construído seguindo as diretrizes modernas de desenvolvimento Android (Modern Android Development - MAD):

* **Linguagem:** Kotlin 100%
* **Interface (UI):** Jetpack Compose (Material Design 3)
* **Arquitetura:** MVVM (Model-View-ViewModel)
* **Injeção de Dependências:** ViewModel nativo (`viewModels()`)
* **Banco de Dados:** Firebase Firestore (NoSQL)
* **Armazenamento:** Firebase Storage
* **Carregamento de Imagens:** Coil (Coroutine Image Loader)
* **Assincronismo:** Kotlin Coroutines & Flow

## Requisitos para Rodar

Para executar este projeto, você precisará de:

* **Android Studio:** Versão Ladybug (2024.2.1) ou superior.
* **JDK:** Java 17 ou superior.
* **SDK Android:**
    * `minSdk`: 24 (Android 7.0)
    * `targetSdk`: 34 (Android 14) / 35

## Como Executar

1.  **Clonar o repositório:**
    ```bash
    git clone https://github.com/joaogasoares/Teste-LealFitness-Android
    ```

2.  **Abrir no Android Studio:**
    * Selecione a pasta raiz do projeto.
    * Aguarde a sincronização do Gradle (Sync).

3.  **Configuração do Firebase:**
    * O arquivo `google-services.json` já está incluído no projeto para facilitar a execução.

4.  **Executar:**
    * Selecione um Emulador ou Dispositivo Físico.
    * Clique no botão **Run** ou **Shift+f10**.

## Observação

* **Tipagem:** O campo "Nome" do exercício foi implementado como `String` (texto) ao invés de `number`, conforme inferido pela natureza do dado.


---
Desenvolvido por **João Soares**