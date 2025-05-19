import * as httpResponse from "../../../utils/http-helper";
import {onRequest} from "firebase-functions/v2/https";
import * as logger from "firebase-functions/logger";

import { initializeApp } from "firebase-admin/app";
import { getFirestore, Timestamp } from "firebase-admin/firestore";
import { generateRandomBase64 } from "./generateRandomBase64";
import { generateQRCodeWithBase64 } from "./generateQrCodeWithBase64";

// Start writing functions
// https://firebase.google.com/docs/functions/typescript

// export const helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

initializeApp();
const db = getFirestore();

//Recebe um QR Code base64 com um token gerado para login

export const performAuth = onRequest(async (req, res) => {
  const { apiKey, url } = req.body;

  if (!apiKey || !url) {
    res.status(400).send("Missing apiKey or url");
    return;
  }

  try {
    const partnersRef = db.collection("partners");

    const snapshot = await partnersRef
      .where("url", "==", url)
      .where("apiKey", "==", apiKey)
      .get();

    if (snapshot.empty) {
      logger.warn("Parceiro não autorizado:", { apiKey, url });
      res.status(403).send("Unauthorized partner");
      return;
    }

    const loginToken = generateRandomBase64(256);
    const createdAt = Timestamp.now();

    await db.collection("login").doc(loginToken).set({
      apiKey,
      loginToken,
      createdAt,
      attempts: 0,
    });

    const qrCodeBase64 = await generateQRCodeWithBase64(loginToken);

    res.status(200).send({ qrBase64: qrCodeBase64, loginToken: loginToken });
  } 
  catch (error) {
    logger.error("Erro em performAuth", error);
    res.status(500).send("Internal server error");
  }
});

//O site parceiro consulta se o QR code foi usado por algum usuário (verifica o status do loginToken)
export const getLoginStatus = onRequest(async (req, res) => {
  const { loginToken } = req.body;

  if (!loginToken) {
    const response = await httpResponse.badRequest("Missing loginToken");

    res.status(response.statusCode).send(response.body);
    return;
  }

  try {
    const loginDocRef = db.collection("login").doc(loginToken);
    const loginSnap = await loginDocRef.get();

    if (!loginSnap.exists) {
      res.status(404).send("Token not found");
      return;
    }

    const loginData = loginSnap.data();
    const dateNow = Timestamp.now();
    const hasCreated = loginData?.createdAt as Timestamp;
    const differenceTime = dateNow.seconds - hasCreated.seconds;

    //se passou de um minuto ou mais de 3 tentativas, expira o qrcode
    if (differenceTime > 60 || (loginData?.attempts ?? 0) >= 3) {
      await loginDocRef.delete();
      res.status(410).send({ status: "expired" });
      return;
    }

    //Aumenta a quantidade de tentativas
    await loginDocRef.update({
      attempts: (loginData?.attempts ?? 0) + 1,
    });

    if (loginData?.user) {
      res.status(200).send({ status: "success", uid: loginData.user });
    } 
    else {
      res.status(202).send({ status: "pending" });
    }
  } 
  catch (error) {
    const response = await httpResponse.serverError("Internal server error"); 

    logger.error("Erro em getLoginStatus", error);
    res.status(response.body).send(response.body);
  }
});