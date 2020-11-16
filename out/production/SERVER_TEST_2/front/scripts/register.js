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
    if(name.length>0&&pass.length>0){
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
}
