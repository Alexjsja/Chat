
function exchanger(method,url,body=null) {
    return fetch(url,{
        method:method,
        headers:{
            'Content-Type':'application/json'
        },
        body:JSON.stringify(body)
})


    /*return new Promise(((resolve, reject) =>{
        let xhr = new XMLHttpRequest();

        xhr.open(action,url,true);

        xhr.responseType = "json";


        xhr.setRequestHeader("Content-Type","application/json");

        xhr.onload = function () {
            if(xhr.status >= 400){
                reject("ERROR");
            }else{
                resolve(xhr.response);
            }
        }
        xhr.onerror = function () {
            reject("ERROR");
        }
        xhr.send(JSON.stringify(body));
    }));*/
}