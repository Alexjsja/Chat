function personalWithUser(href){
    let id = href.dataset.id;
    document.cookie='receiver='+id+'; path=/personal; max-age=10000'
    window.location.href='/personal'
}