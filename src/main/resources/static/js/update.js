// Page load hote hi sabhi form fields ki initial values store karo
const form = document.querySelector("form");
const submitBtn = document.getElementById("update-btn");

// Sabhi input, select, textarea fields ko track karo
const fields = form.querySelectorAll("input, select, textarea");

// Initial values save karo
const initialValues = {};
fields.forEach((field) => {
  if (field.type === "checkbox") {
    initialValues[field.name] = field.checked;
  } else if (field.type === "file") {
    initialValues[field.name] = ""; // file always empty on load
  } else {
    initialValues[field.name] = field.value;
  }
});

// Check karo kuch change hua ya nahi
function checkChanges() {
  let isChanged = false;

  fields.forEach((field) => {
    if (field.type === "checkbox") {
      if (field.checked !== initialValues[field.name]) isChanged = true;
    } else if (field.type === "file") {
      if (field.files && field.files.length > 0) isChanged = true;
    } else {
      if (field.value !== initialValues[field.name]) isChanged = true;
    }
  });

  if (isChanged) {
    // Enable button
    submitBtn.disabled = false;
    submitBtn.classList.remove("bg-blue-400", "cursor-not-allowed");
    submitBtn.classList.add(
      "bg-blue-600",
      "cursor-pointer",
      "hover:bg-blue-700",
    );
  } else {
    // Disable button
    submitBtn.disabled = true;
    submitBtn.classList.add("bg-blue-400", "cursor-not-allowed");
    submitBtn.classList.remove(
      "bg-blue-600",
      "cursor-pointer",
      "hover:bg-blue-700",
    );
  }
}

// Har field change pe check karo
fields.forEach((field) => {
  field.addEventListener("input", checkChanges);
  field.addEventListener("change", checkChanges);
});

const imageInput = document.getElementById("image_file_input");

const imagePreview = document.getElementById("upload_image_preview");

imageInput.addEventListener("change", function (event) {
  const file = event.target.files[0];

  if (file) {
    const imageURL = URL.createObjectURL(file);

    imagePreview.src = imageURL;

    imagePreview.classList.remove("hidden");
  }
});
