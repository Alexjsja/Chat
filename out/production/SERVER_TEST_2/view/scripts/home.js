const url = "/home";
let sql_date ='start';

function logout(){
    document.cookie="logout=1; path=/home; max-age=1"
    fetch("home").then(r=>location.href="/register")
}




























