const url = "/home";
let startSend = false;

//fixme
let date = new Date();
let sql_date ='2020-11-15 18:57:41.271';
    // date.toISOString().slice(0, 19).replace('T', ' ');


function responseToHTML(response) {
    if(typeof response!=="undefined"){
        let div = document.getElementById("_response_");
        sql_date = response[response.length-1].sendTime
        for (let i = 0; i < response.length; i++) {
            let text = document.createElement("div");
            text.className='msg'
            text.innerText = response[i].text + '(' + response[i].author + ') '+response[i].sendTime;
            div.prepend(text);
        }
    }
}
function logout(){
    document.cookie="logout=1; path=/; max-age=1"
    fetch("home").then(r=>location.href="/register")
}
function send(){
    let button = document.getElementById("sendButton");
    button.disabled=true
    let receiveText = document.getElementById("readable");
    if(receiveText.value.length===0){
        alert("Письмо пустое!")
    }else if(receiveText.value.length>99){
        alert("Длинна письма не может превышать 100 символов")
    } else{
        let readable = {text:receiveText.value};
        fetch(url, {
            method: "POST",
            body: JSON.stringify(readable)
        })
        receiveText.value="";
        setTimeout( ()=>button.disabled=false,400)

    }
}
function repeat(){
    document.cookie="last_time="+sql_date+"; path=/; max-age=1"
    fetch(url).then(resp =>{
        if (resp.status===200){
            return resp.json();
        }
    }).then(resp => responseToHTML(resp))
}
setInterval(repeat,1000)





























