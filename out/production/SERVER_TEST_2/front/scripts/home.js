const url = "/home";
let date = new Date();
let sql_date ='2020-11-11 15:55:05'
// date.toISOString().slice(0, 19).replace('T', ' ');

async function responseToHTML(response) {
    console.log('lol')
    let div = document.getElementById("_response_");
    if (response.length > 1){
        sql_date = response[response.length-1].sendTime
    }
    for (let i = 0; i < response.length; i++) {
        let text = document.createElement("div");
        text.className='msg'
        text.innerText = response[i].text + '(' + response[i].author + ') '+response[i].sendTime;
        div.appendChild(text);
    }


}
function logout(){
    document.cookie="logout=1; path=/; max-age=1"
    fetch("home").then(r=>location.href="/register")
}
function send(){
    let receiveText = document.getElementById("readable");
    if(receiveText.value.length>0){
        let readable = {text:receiveText.value};
        fetch(url, {
            method: "POST",
            body: JSON.stringify(readable)
        })
        receiveText.value="";
    }else{
        alert("Письмо пустое!")
    }
}
function repeat(){
    document.cookie="last_time="+sql_date+"; path=/; max-age=1"
    fetch(url).then(resp =>{
        if (resp.status===200){
            responseToHTML(resp.json())
        }
    })
}
setInterval(repeat,1000)
