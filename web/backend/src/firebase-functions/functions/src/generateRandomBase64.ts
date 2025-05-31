import * as crypto from "crypto";

//gera uma string aleatoria com o length(256)
export function generateRandomBase64(length: number): string {
    return crypto.randomBytes(length).toString("base64url").slice(0, length);
  }