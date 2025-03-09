import stateStroge from "./StateStorage"

const user = stateStroge.get("user");

const defaultOpenCallback = ()=>{
    console.log("SSE开启...")
};

export function assginSSE(messageCallback:(e:MessageEvent<any>)=>void|null,openCallback:()=>void = defaultOpenCallback){
   let sse = new EventSource(`http://localhost:5725/Api/Common/SSE/${user.id}`); 
   sse.onopen = openCallback;
   sse.onerror = ()=>{
      sse.close();
      sse = new EventSource(`http://localhost:5725/Api/Common/SSE/${user.id}`); 
   }
   sse.onmessage = messageCallback
   return sse;
}