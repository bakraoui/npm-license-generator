let submit = document.getElementById("form-submit");

const REQUEST_URL = (dependency) => `http://localhost:8080/${dependency.name}/${dependency.version}`

let dependency = {
    name:"",
    version:""
}

let inputs = document.getElementsByTagName("form")[0];

let dependencyName = document.getElementById("name");
dependencyName.addEventListener("input", (e) => {
    let name = e.target.value.replace("/", "__").trim();
    if (!isNameValid(name)) {
        dependencyName.classList.remove("valid");
        dependencyName.classList.add("not-valid");
    }else{
        dependencyName.classList.remove("not-valid");
        dependencyName.classList.add( "valid");
    }
    dependency.name = name;
})


let dependencyVersion = document.getElementById("version");
dependencyVersion.addEventListener("input", (e) => {  
    let version = e.target.value.trim();
    if (!isVersionValid(version)) {
        dependencyVersion.classList.remove("valid");
        dependencyVersion.classList.add("not-valid");
    }else{
        dependencyVersion.classList.remove("not-valid");
        dependencyVersion.classList.add( "valid");
    }
    dependency.version = version;
})

let isNameValid = (input) => {

    if (input.length === 0) {
        return false;
    }

    let specialChars = [" ", "&", "~", "{", "}", "(", ")", "[", "]", "#", "$", "%", "\\", "^"]
    for (let i = 0; i < specialChars.length; i++) {
        if (input.includes(specialChars[i])) {
            return false;
        }
    }

    return true;
}

let isVersionValid = (input) => {
    
    if (input.length === 0) {
        return false;
    }
    
    let specialChars = [" ", "&", "~", "{", "}", "(", ")", "[", "]", "//", "#", "$", "%", "\\", "^"]
    for (let i = 0; i < specialChars.length; i++) {
        if (input.includes(specialChars[i])) {
                return false;
        }
    }

    if(!input.match(/[0-9]*\.[0-9]*\./)) {
        return false;
    }

    return true;
}

submit.onsubmit = (e) => {
    e.preventDefault();
    if (!isNameValid(dependency.name) || !isVersionValid(dependency.version)) {
        document.querySelector("#input-error").style.display = "block";
        return;
    }
    let url = REQUEST_URL(dependency)
    
    document.querySelector("#generate-license").setAttribute("disabled", true)
    let download = document.querySelector("#download");
        download.classList.remove("visible");
        download.classList.add("hidden");


    let loading = document.querySelector("#loading");
        loading.classList.remove( "hidden");
        loading.classList.add("visible");
 

    fetch(url, { method: "POST" })
        .then(function(response) {
            return response.json();
        }).then(function (data) {

            if (data.status !== "OK") {
                setMessage(data.message, "error");
                download.classList.remove( "visible");
                download.classList.add( "hidden");
                // download.classList.replace( "hidden", "hidden");
            }

            if (data.status === "OK") {
                setMessage("You can download your Tree and license right now.", "success");
                prepareDownloadUrls(data.filename);
                download.classList.remove( "hidden");
                download.classList.add( "visible");
                // download.classList.replace( "hidden", "visible");
            }
            loading.classList.remove( "visible");
            loading.classList.add(  "hidden");
            document.querySelector("#generate-license").removeAttribute("disabled")
        });

    
    

}


function prepareDownloadUrls(filename) {

    document.getElementById("license")
        .setAttribute("href", "http://localhost:8080/license/"+filename);
    document.getElementById("tree")
        .setAttribute("href", "http://localhost:8080/tree/"+filename);

    document.getElementById("log")
        .setAttribute("href", "http://localhost:8080/log/"+filename);

}


function setMessage(message, style) {
    let element = document.getElementById("message");
    element.innerText = message;

    element.classList.remove(...element.classList)
    element.classList.add(style);

    setTimeout(() => {
        element.classList.remove(style);
    }, 10_000)
}