import { getAuth, signInWithEmailAndPassword } from "@firebase/auth";
import { IUserLoginModel } from "../models/userLogin-model";
import { app } from "../firebaseConnection";

export async function loginUserAuth(userData: IUserLoginModel): Promise<Boolean> {
    const auth = getAuth(app);
    var hasCreated: Boolean = false;

    await signInWithEmailAndPassword(auth, userData.email, userData.password) 
        .then((userCredential) => {
            const user = userCredential.user;
            hasCreated = true;
        })
        .catch ((error) => {
            const errorCode = error.code;
            const errorMessage = error.message;
        });
    
    return hasCreated;
    
}