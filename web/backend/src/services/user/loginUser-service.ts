import { loginUserAuth } from "../../auth/loginUser-auth";
import { IUserLoginModel } from "../../models/userLogin-model";
import * as httpResponse from "../../utils/http-helper";

export class LoginUserService {
    async execute(userData: IUserLoginModel) {
        const hasCreated = await loginUserAuth(userData);
        let response = null;

        if (hasCreated) {
            response = httpResponse.created()
        }
        else {
            response = httpResponse.badRequest("erro ao criar usuário!")
        }
        
        return response;
    }
}