console.log("Script Loaded");

//change theme work
let currentTheme = getTheme();
// initialy set the theme

document.addEventListener("DOMContentLoaded", () => {
  changeTheme();
});

// todo
function changeTheme() {
  //  set to web page
  changePageTheme(currentTheme, currentTheme);

  // set listener to change theme button
  const changeThemeButton = document.querySelector("#theme_change_button");
  const oldTheme = currentTheme;

  changeThemeButton.addEventListener("click", (event) => {
    let oldTheme = currentTheme; //
    console.log("change theme button clicked");

    if (currentTheme == "dark") {
      //theme ko light

      currentTheme = "light";
    } else {
      currentTheme = "dark";
    }

    changePageTheme(currentTheme, oldTheme);
  });
}

// set theme to local storage
function setTheme(theme) {
  const oldTheme = currentTheme;
  localStorage.setItem("theme", theme);
}

// get theme from local storage
function getTheme() {
  let theme = localStorage.getItem("theme");
  return theme ? theme : "light";
}

//change current page theme
function changePageTheme(theme, oldTheme) {
  // local storage main update krenge
  setTheme(currentTheme);

  //remove the current theme
  document.querySelector("html").classList.remove(oldTheme);
  //add the current theme
  document.querySelector("html").classList.add(theme);

  //change the text of button
  document
    .querySelector("#theme_change_button")
    .querySelector("span").textContent = theme == "light" ? "Dark" : "Light";
}
