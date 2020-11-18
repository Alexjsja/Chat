const url = "/home";
let sql_date ='start';

function goToUser(href){
    let id = href.dataset.id;
    document.cookie='user='+id+'; path=/user; max-age=10000'
    window.location.href='/user'
}
function logout(){
    document.cookie="logout=1; path=/home; max-age=1"
    fetch("home").then(r=>location.href="/register")
}




























