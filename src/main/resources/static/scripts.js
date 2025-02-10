function updateEditor() {
  console.log("Update editor");
  document.getElementById("editor").submit();
}

function loadScroll() {
  var yScreen = localStorage.getItem("yPos");
  document.getElementById("left").scrollTop = yScreen;
}

function saveScroll() {
  var yScroll = document.getElementById("left").scrollTop;
  localStorage.setItem("yPos", yScroll);
}

function clearScroll() {
  localStorage.setItem("yPos", 0);
}