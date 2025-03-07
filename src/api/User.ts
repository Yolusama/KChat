import { type Result } from "../modules/Request";
import { api,authorization,requestUrl } from "./Api";

const userApiPettrn = "/Api/User";

function userApiUrl(url:string){
    return requestUrl(userApiPettrn,url); 
}

export function Login(account:string,password:string,successCallback:(result:Result)=>void){
    api.post(userApiUrl("/Login"),{},{
        account:account,
        password:password
    },successCallback);
}

export function Register(user:Record<string,any>,checkCode:string,successCallback:(result:Result)=>void){
    api.put(userApiUrl(`/Register/${checkCode}`),{},user,successCallback);
}

export function GetCheckCode(length:number,email:string,successCallback:(result:Result)=>void){
    api.get(userApiUrl(`/GetCheckCode/${length}?email=${email}`),{},successCallback);
}

export function VerifyToken(userId:string,token:string,successCallback:(result:Result)=>void,failCallback:()=>void){
    api.post(userApiUrl("/VerifyToken"),{},{
        userId:userId,
        token:token
    },successCallback,failCallback);
}

export function SearchUser(userId:string,identifier:string,successCallback:(res:Result)=>void){
    api.get(userApiUrl(`/SearchUser/${userId}?identifier=${identifier}`),authorization(),successCallback);
}

export function MakeFriends(contactModel:Record<string,any>,successCallback:(res:Result)=>void){
    api.put(userApiUrl("/MakeFriends"),authorization(),contactModel,successCallback);
}