function validate() {
    var name = document.forms["contactForm"]["name"].value;
    if (name == "") {
        alert("Name must be filled out");
        return false;
    }
    if (name.length > 70) {
        alert("Name must be less than 70 characters long");
        return false;
    }
    var regex = /^(([^<>()\[\]\.,;:\s@\"]+(\.[^<>()\[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
    var email = document.forms["contactForm"]["email"].value;
    if ((email != "") && (!regex.test(email))) {
        alert("Invalid e-mail address");
        return false;
    }
    if (email.length > 254) {
        alert("E-mail must be less than 254 characters long");
        return false;
    }
    var message = document.forms["contactForm"]["message"].value;
    if (message == "") {
        alert("Message must be filled out");
        return false;
    }
    if ((message.length < 5) || (message.length > 1000)) {
        alert("Message must be between 5 and 1000 characters long");
        return false;
    }
    return true;
}
