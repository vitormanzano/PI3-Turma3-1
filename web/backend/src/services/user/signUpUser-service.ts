import { IUserModel } from "../../models/user-model";
import * as httpResponse from "../../utils/http-helper";
import { IHttpResponseModel } from "../../models/httpResponse-model";
import { signUpUserAuth } from "../../auth/signUpUser-auth";

//Serviço de cadastro que chama o auth
export class SignUpUserService {
    async execute(userData: IUserModel): Promise<IHttpResponseModel> {
        const hasCreated = await signUpUserAuth(userData); //Cadastra o user no auth
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