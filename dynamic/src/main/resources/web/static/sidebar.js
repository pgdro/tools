function openNav() {
    document.getElementById("mySidebar").style.width = "16rem";
    document.getElementById("TitleContainer").style.marginLeft = "16rem";
    document.getElementById("main").style.marginLeft = "16rem";
    document.getElementById("footer").style.marginLeft = "16rem";
    document.getElementById("openBtn").style.visibility = "hidden";
    document.getElementById("closeBtn").style.visibility = "visible";
}

function closeNav() {
    document.getElementById("mySidebar").style.width = "0";
    document.getElementById("TitleContainer").style.marginLeft= "0";
    document.getElementById("main").style.marginLeft= "0";
    document.getElementById("footer").style.marginLeft = "0";
    document.getElementById("openBtn").style.visibility = "visible";
    document.getElementById("closeBtn").style.visibility = "hidden";
}