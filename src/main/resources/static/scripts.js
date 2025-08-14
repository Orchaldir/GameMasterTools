function updateEditor() {
  console.log("Update editor");
  document.getElementById("editor").submit();
}

function loadScroll() {
  var yScreen = localStorage.getItem("yPos");
  document.getElementById("main").scrollTop = yScreen;
}

function saveScroll() {
  var yScroll = document.getElementById("main").scrollTop;
  localStorage.setItem("yPos", yScroll);
}

function clearScroll() {
  localStorage.setItem("yPos", 0);
}