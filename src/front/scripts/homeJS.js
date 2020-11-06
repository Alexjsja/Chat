
const host = window.location.host;
const url = host+"/home";


function responseToHTML(response) {
    let div = document.getElementById("_response_");
    let text = document.createElement("div");
    text.innerText = response.text + '(' + response.author + ') '+response.sendTime;
    div.appendChild(text);
}

function send(){
    let receiveText = document.getElementById("readable");
    if(receiveText.value.length>0){
        let readable = {text:receiveText.value};
        exchanger("POST",url,readable)
            .then(response => responseToHTML(response))
            .catch(err => alert(err));
        receiveText.value="";
    }else{
        alert("Письмо пустое!")
    }
}