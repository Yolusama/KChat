import type { Result } from "../modules/Request";
import { api, authorization } from "./Api";


const applyApiPerren = "/Api/UserApply";

function applyApiUrl(url:string){
    return applyApiPerren+url;
}

export function MakeApply(apply:Record<string,any>,successCallback:(res:Result)=>void){
    api.put(applyApiUrl("/MakeApply"),authorization(),apply,successCallback);
}