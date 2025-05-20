 const PROJECT_ID = "superid-ab179"; 
const API_KEY = "f6UIj/l23X+M4xW7yZ9aQ0bVpEs6iYdZgB8nJt2HuKl9rSwzXcAe5oPq1I7b8UjYn2OmLwzXcAe5oPq1I7b8e5oPq1I7b8UjYn2OmLwzXcAe5oPq1I7b8UjYn2OmLw==";
const SITE_URL = "www.example.com";

let loginToken = null;
let interval = null;
let intervalsCount = 0;
const MAX_INTERVALS = 4;
const INTERVAL_VERIFICATE_QRCODE = 15000; // 15 segundos

async function generateQRCode() {
  try {
    const res = await fetch(`http://localhost:5001/${PROJECT_ID}/us-central1/performAuth`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        apiKey: API_KEY,
        url: SITE_URL
      })
    });

    const data = await res.json();
    loginToken = await data.loginToken;
    console.log("Login Token: ", loginToken);

    document.getElementById("qrcode").src = data.qrBase64;
    document.getElementById("status").innerText = "Escaneie com o app SuperID";

    intervalsCount = 0;
    if (interval) clearInterval(interval);

    interval = setInterval(checkLoginStatus, INTERVAL_VERIFICATE_QRCODE);

    }
  catch (err) {
    document.getElementById("status").innerText = "Erro ao gerar QR Code";
    console.error(err);
  }
}

async function checkLoginStatus() {
  try {
    const res = await fetch(`http://localhost:5001/${PROJECT_ID}/us-central1/getLoginStatus`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ loginToken })
    });

    const data = await res.json();

    if (data.status === "success") {
      clearInterval(interval);

      document.getElementById("status").innerText = "Login efetuado com sucesso!";
      setTimeout(() => {
        document.getElementById("status").innerText = "Usuário logado com sucesso!";
      }, 1000);
    } 
    else if (data.status === "expired") {
      clearInterval(interval);

      document.getElementById("status").innerText = "Token expirado. Gerando novo QR Code...";
      setTimeout(generateQRCode, 1000);
    } 
    else {
      intervalsCount++;

      if (intervalsCount >= MAX_INTERVALS) {
        clearInterval(interval);
          
        document.getElementById("status").innerText = "Tempo esgotado. Gerando novo QR Code...";
        setTimeout(generateQRCode, 1000);
      }
    }

  } 
  catch (err) {
    clearInterval(interval);
    document.getElementById("status").innerText = "Erro ao verificar status";
    console.error(err);
  }
}
// Gera QR Code ao abrir a página
generateQRCode();