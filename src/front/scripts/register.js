const url = "/register";

function register(result) {
    let successful = result.suc
    console.log(successful)
    if (successful===true){
        window.location.href="/login"
    }else{
        alert("Такой человек уже зарегистрирован")
    }
}

function send(){
    let name = document.getElementById("name").value;
    let pass = document.getElementById("password").value;
    let mail = document.getElementById("mail").value;
    let mailformat = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
    if (mail.match(mailformat)){
        if(name.length>0&&pass.length>0&&mail.length>0){
            let readable = {
                name:name,
                password:pass,
                mail:mail
            };
            exchanger("POST",url,readable)
                .then(response => response.json())
                .then(result => register(result))
                .catch(err => alert(err));
        }else{
            alert("Логин или пароль пуст")
        }
    }else {alert("Ваша почта не похоша на почту")}
}
