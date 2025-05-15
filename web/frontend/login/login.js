function isValid(email, password){
    var valid = false;
    email = email.trim();
    password = password.trim();

    if (email && password){
        valid = true;
    }else if(email.length == 0 && password.length == 0){
        // dar um erro dizendo para preencher os campos
        showErrorMessage("Preencha os campos.");
    }else if(email.length == 0){
        // dar um erro dizendo para preencher o email
        showErrorMessage("Preencha o email.");
    }else{
        // dar um erro dizendo para preencher a senha
        showErrorMessage("Preencha a senha.");
    }
    return valid;
}

// Exibe uma mensagem de erro
function showErrorMessage(message){
    var mb = document.getElementById("messageBox");
    document.getElementById("message").innerHTML = message;
    mb.style.display = "block";
}

// Exibe uma mensagem de sucesso
function showSucessMessage(message){
    var mb = document.getElementById("messageBox");
    document.getElementById("message").innerHTML = message;
    mb.style.display = "block";
    mb.style.backgroundColor = "green";
}

// Oculta a mensagem de erro
function hideErrorMessage(){
    var mb = document.getElementById("messageBox");
    mb.style.display = "none";
}

// Submete os dados do SignIn para a API
async function performSignIn() {
    var email = document.getElementById("email").value;
    var password = document.getElementById("password").value;

    if (isValid(email, password)) {
        try {
            const response = await fetch("http://localhost:3000/login", {
                method: 'POST', // corrigido para POST
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    email: email,
                    password: password
                })
            });

            if (!response.ok) {
                const errorText = await response.text();
                showErrorMessage(errorText);
                throw new Error(`Erro HTTP! Status: ${response.status}, Mensagem: ${errorText}`);
            }

            const result = await response.json(); // resposta JSON esperada

            const token = result.token;

            if (token) {
                localStorage.setItem('authToken', token);
                showSucessMessage("Login realizado com sucesso!");
                setTimeout(() => {
                    window.location.href = "../home_page/";
                }, 2000);
            } else {
                showErrorMessage("Token não recebido do servidor.");
                console.error("Token não encontrado na resposta:", result);
            }

        } catch (error) {
            console.error('Erro ao realizar login:', error);
            showErrorMessage("Erro ao realizar login.");
        }
    }
}


function togglePasswordVisibility() {
    const passwordInput = document.getElementById('password');
    const showPasswordCheckbox = document.getElementById('show-password');

    // Alterna entre os tipos "password" e "text"
    if (showPasswordCheckbox.checked) {
        passwordInput.type = 'text';
    } else {
        passwordInput.type = 'password';
    }
}

