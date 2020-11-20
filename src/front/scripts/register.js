const url = "/register";

function register(result) {
    let successful = result.suc
    console.log(successful)
    if (successful===true){
        window.location.href="/login"
    }else{
        notSuccess()
    }
}

function send(){
    let name = document.getElementById("name").value;
    let pass = document.getElementById("password").value;
    let mail = document.getElementById("email").value;
    if(name.length>0&&pass.length>0&&mail.length>0){
        let readable = {
            name:name,
            password:pass,
            mail:mail
        };
        fetch(url,{
            method:"POST",
            body:JSON.stringify(readable)}
        ).then(response => response.json())
            .then(response => register(response))
            .catch(err => alert(err+'server error'));
    }else{
        alert("Логин или пароль пуст")
    }
}

