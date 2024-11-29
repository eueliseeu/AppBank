
# AppBank

AppBank é um sistema bancário simples desenvolvido em Java utilizando Maven como gerenciador de dependências. Este projeto possui suporte para manipulação de dados em formato JSON e utiliza a linguagem Java versão 17 com recursos habilitados de prévia (`--enable-preview`).

---

## **Pré-requisitos**

Antes de executar o sistema, certifique-se de que você tem os seguintes requisitos instalados em sua máquina:

1. **Java Development Kit (JDK) 17** ou superior.
   - Verifique a instalação com o comando:
     ```bash
     java -version
     ```
     Certifique-se de que a versão exibida é pelo menos 17.
2. **Maven** (Gerenciador de dependências e build do projeto).
   - Verifique a instalação com o comando:
     ```bash
     mvn -v
     ```

3. **Git** (para clonar o repositório, opcional).
   - Verifique a instalação com o comando:
     ```bash
     git --version
     ```

---

## **Como configurar e executar o projeto**

### 1. Clone o repositório
Utilize o Git para clonar o repositório do projeto ou baixe-o diretamente como um arquivo ZIP.

```bash
git clone https://github.com/seu-usuario/AppBank.git
cd AppBank
```

### 2. Compile e instale as dependências
O Maven irá gerenciar as dependências listadas no arquivo `pom.xml`. Execute o seguinte comando na raiz do projeto:

```bash
mvn clean install
```

Este comando irá:
- Limpar qualquer build anterior.
- Baixar e instalar as dependências necessárias.
- Compilar o código.

---

### 3. Execute o sistema
O arquivo `pom.xml` está configurado com o **Main-Class** definida como `com.atividade.appbank.AppBank`. Para executar a aplicação, utilize:

```bash
mvn exec:java
```

---

## **Estrutura do Projeto**

### Diretórios principais:
- `src/main/java` - Contém o código-fonte do sistema.
- `src/test/java` - Contém testes unitários e de integração (caso aplicável).
- `pom.xml` - Arquivo de configuração Maven, que define dependências e propriedades do projeto.

---

## **Dependências do projeto**

O AppBank utiliza as seguintes bibliotecas externas:

1. **org.jsonx:json (v0.2.2)**
    - Biblioteca para manipulação de objetos JSON.
    - Documentação: [JSONx](https://jsonx.org).

2. **org.codeartisans:org.json (v20161124)**
    - Outra biblioteca para trabalhar com JSON.
    - Documentação: [JSON](https://github.com/stleary/JSON-java).

---

## **Configuração de Build**

- **Java Compiler Version:** 17
- **Preview Features:** Habilitado (`--enable-preview`).

---

## **Como testar**

Após realizar o build, você pode verificar o funcionamento da aplicação utilizando:

1. **Maven Tests**  
   Execute todos os testes disponíveis no projeto:
   ```bash
   mvn test
   ```
   
Esse sistema utilza API.