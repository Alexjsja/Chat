function exchanger(method,url,body=null) {
    return fetch(url,{
        method:method,
        headers:{
            'Content-Type':'application/json'
        },
        body:JSON.stringify(body)
})}