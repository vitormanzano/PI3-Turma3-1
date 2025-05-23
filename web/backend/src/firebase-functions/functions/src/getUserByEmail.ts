const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.getUserByEmail = functions.https.onCall(async (data: any, context: any) => {
    const email = data.email;
    try {
      const userRecord = await admin.auth().getUserByEmail(email);
      
      return {
        uid: userRecord.uid,
        emailVerified: userRecord.emailVerified,
      };
    } catch (error) {
      throw new functions.https.HttpsError('not-found', 'Usuário não encontrado');
    }
  });