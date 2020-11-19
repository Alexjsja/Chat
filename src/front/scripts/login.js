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
    let mail = document.getElementById("name").value;
    let pass = document.getElementById("password").value;
    if (mail.length > 0 && pass.length > 0) {
        let readable = {
            mail: mail,
            password: pass
        };
        mail.value = ""
        pass.value = ""
        fetch(url,{
            method:"POST",
            body:JSON.stringify(readable)}
        ).then(response => response.json())
            .then(response => login(response))
            .catch(err => alert(err));
    }else {alert("Логин или пароль пуст")}
}