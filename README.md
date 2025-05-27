README PI

# Bem vindo ao SuperId!

O seu app gerenciador de senhas, de maneira fácil, rápida e segura. 
Além dessa funcionalidade o SuperId pode ser usado para fazer login em um site parceiro, facilitando para criar uma conta em um novo site.

# Como rodar o SuperId?

## Mobile

Para rodar a nossa aplicação no Android Studio não precisa fazer nenhuma configuração prévia, apenas garantir que tenha o SDK para fazer o build, efetuar o sync no build.gradle(app e módulo) para garantir que tenha todas as dependências, e inicie o app!

## Web:

Para rodar o site você precisará fazer algumas coisas antes.

1. Antes de tudo, instale o node na sua máquina.
2. Após dar o fork do projeto. Abra o seu terminal e localize o projeto. 
PI3-Turma3-1 - Deve se parecer com isso.
3. Após isso você deve entrar na pasta 
<pre>PI3-Turma3-1/web/backend</pre>
4. Dentro da pasta backend você precisará dar o comando 
<pre>npm i</pre> Para instalar as dependências do projeto
5. Apos instalar as dependências do projeto, você precisará instalar o firebase tools.
<pre> npm install -g firebase-tools</pre>
6. Instalado com sucesso, você precisará entrar na pasta functions, seguindo o caminho:
<pre>src/firebase-functions/functions</pre>
OBS: Você já deve estar na pasta <pre>web/backend</pre>
8. Feito isso, você precisará dar um 
<pre>npm i</pre> 
novamente, pois o firebase-functions cria um package.json diferente para rodas as dependências necessárias.
9. Instala com sucesso, você precisará fazer login com a conta do firebase que deseja utilizar e fazer as configurações necessárias para  colocar o projeto firebase que você está utilizando.
10. Após entrar com a conta, você precisa especificar o projeto firebase, dando o seguinte comando:
 <pre>firebase use —add ID_DO_SEU_PROJETO</pre>
 Assim como na pasta frontend/qrCode/qrCode.js, você precisará colocar o Id do projeto que deseja usar
11. Após esses processos, dê um build na mesma pasta para construir o arquivo lib
 <pre>npm run build </pre>
OBS: Após dar o build, será criado a pasta lib, verifique se na raiz do projeto tem o arquivo index.js, se não, você precisará pegar e arrastar ele da pasta <pre>lib/firebase-functions</pre>, se atente a isso! 
12. Após concluir todas as etapas, dentro da pasta <pre>web/backend</pre> você precisará dar o seguinte comando:  
<pre>npm run start:watch </pre>
Isso fará o server rodar, após o server rodar, dentro da pasta: <pre>src/firebase-functions/functions</pre>
use o seguinte comando: <pre> firebase emulators:start</pre>
13. Após isso, para testar se o qrcode está sendo gerado com sucesso abra o navegador e digite o seguinte caminho
 http://localhost:300/qrCode/qrCode.html. Após isso tudo deve estar funcionando. Se todas as dependências foram instaladas com sucesso e o qr code não está sendo gerado, mude as configurações da API do firebase no qrCode.js   

