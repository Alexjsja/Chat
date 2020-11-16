const host = window.location.host;
const url = "/login";

function login(result) {
    let successful = result.suc
    console.log(result)
    if (successful===true){
        window.location.href="/home"
    }else{
        alert("Логин и/или пароль не верные")
    }
}

function send() {
    let mail = document.getElementById("mail").value;
    let pass = document.getElementById("password").value;
    let mailformat = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
    if (mail.match(mailformat)) {
        if (mail.length > 0 && pass.length > 0) {
            let readable = {
                mail: mail,
                password: pass
            };
            mail.value = ""
            pass.value = ""
            exchanger("POST", url, readable)
                .then(response => response.json())
                .then(response => login(response))
                .catch(err => alert(err));
        }else {alert("Логин или пароль пуст")}
    }else {alert("Ваша почта не похоша на почту")}
}