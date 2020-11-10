
const host = window.location.host;
const url = "/home";


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
function r(){
    fetch(url).then(resp => resp.status).then(resp => console.log(resp))
    document.cookie="last_time=TEST; path=/; max-age=1"
}
    setInterval(r,1000)
