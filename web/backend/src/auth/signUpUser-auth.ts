import { createUserWithEmailAndPassword, getAuth } from "firebase/auth";
import { app } from "../firebaseConnection";
import { IUserModel } from "../models/user-model";

export async function signUpUserAuth(userData: IUserModel): Promise<boolean> {
    const auth = getAuth(app);
    var hasCreated: boolean = false;


    await createUserWithEmailAndPassword(auth, userData.email, userData.password)
    .then((userCredential) => {
        const user = userCredential.user;
        hasCreated = true;
    })
    .catch ((error) => {
        const errorCode = error.code;
        const errorMessage = error.message
        console.error("Erro ao criar usuário:", error.code, error.message);
    })
    
    return hasCreated;
    
}