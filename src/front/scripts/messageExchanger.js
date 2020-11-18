function responseToHTML(response) {
    if (typeof response !== "undefined") {
        let div = document.getElementById("_response_");
        sql_date = response[0].sendTime;
        for (let i = response.length - 1; i >= 0; i--) {
            let msg = document.createElement("div");
            msg.className = 'msg'
            if (response[i].role === 'admin') {
                msg.className = 'admin-msg msg'
            }
            msg.innerText = response[i].text
            let hrefText = response[i].author + '(' + response[i].authorId + ')'
            msg.innerHTML += '<a class="userHref" onclick="goToUser(this)" data-id="' + response[i].authorId + '">' + hrefText + '</a>'
            div.appendChild(msg);
            scroll();
        }
    }
}
function scroll(){
    let element = document.getElementById('_response_');
    element.scrollTop = element.scrollHeight
}
function send(){
    let receiveText = document.getElementById("readable");
    if(receiveText.value.length===0){
        alert("Письмо пустое!")
    }else if(receiveText.value.length>99){
        alert("Длинна письма не может превышать 100 символов")
    } else{
        let readable = {text:receiveText.value};
        fetch(url,{
            method:"POST"
            ,body:JSON.stringify(readable)
        }).then(r=>r.body)
        receiveText.value="";
    }
}
function repeatGetMessage(){
    document.cookie="last_time="+sql_date+"; path=/; max-age=1"
    fetch(url).then(resp =>{
        if (resp.status===200){
            return resp.json();
        }
    }).then(resp => {
        responseToHTML(resp)
    })
}
setInterval(repeatGetMessage,1000)