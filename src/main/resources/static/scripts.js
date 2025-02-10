function updateEditor() {
  console.log("Update editor");
  document.getElementById("editor").submit();
}

function setScreen() {
  var yScreen = localStorage.getItem("yPos");
  document.getElementById("left").scrollTop = yScreen;
}

function setScroll() {
  var yScroll = document.getElementById("left").scrollTop;
  localStorage.setItem("yPos", yScroll);
}