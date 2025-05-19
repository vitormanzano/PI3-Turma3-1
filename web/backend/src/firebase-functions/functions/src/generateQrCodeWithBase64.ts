import * as QRCode from "qrcode";

//gera qrcode a partir de um base64
export async function generateQRCodeWithBase64(text: string): Promise<string> {
    return await QRCode.toDataURL(text);
  }