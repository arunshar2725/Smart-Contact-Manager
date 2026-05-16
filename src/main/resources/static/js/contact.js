const baseURl = "http://localhost:8081";
const defaultContactPicture =
  "https://imgs.search.brave.com/jHDp_R14w-tbRDiYsyiOCGDeCSPE4WqsVfFwiXVDyow/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly90NC5m/dGNkbi5uZXQvanBn/LzExLzY4LzUwLzU3/LzM2MF9GXzExNjg1/MDU3OTRfSUJDRWlhZnNJckhGSjA5ZTY1UDJ2aDUxMTVDMVhJN2UuanBn";

console.log("This is contact Modal page");

setTimeout(() => {
  const messageBox = document.getElementById("message-box");

  if (messageBox && messageBox.innerHTML.trim() !== "") {
    messageBox.style.transition = "opacity 0.5s ease";
    messageBox.style.opacity = "0";

    setTimeout(() => {
      messageBox.remove();
    }, 500);
  }
}, 3000);

const viewContactModal = document.getElementById("view_contact_modal");

const options = {
  placement: "bottom-right",
  backdrop: "dynamic",
  backdropClasses: "bg-gray-900/50 dark:bg-gray-900/80 fixed inset-0 z-40",
  closable: true,
  onHide: () => {
    document.activeElement?.blur();
  },
};

const instanceOptions = {
  id: "view_contact_modal",
  override: true,
};

const contactModal =
  viewContactModal && typeof Modal !== "undefined"
    ? new Modal(viewContactModal, options, instanceOptions)
    : null;

function openContactModal() {
  contactModal?.show();
}

function closeContactModal() {
  contactModal?.hide();
}

async function loadContactData(id) {
  try {
    const response = await fetch(`${baseURl}/api/contacts/${id}`);
    const data = await response.json();

    document.querySelector("#contact_name").textContent = data.name;
    document.querySelector("#contact_email").textContent = data.email;
    document.querySelector("#contact_phone").textContent = data.phoneNumber;
    document.querySelector("#contact_address").textContent =
      data.address || "Address not provided";
    document.querySelector("#contact_about").textContent =
      data.description || "No description available.";

    const pictureEl = document.querySelector("#contact_picture");
    pictureEl.onerror = () => {
      pictureEl.onerror = null;
      pictureEl.src = defaultContactPicture;
    };
    pictureEl.src = data.picture || defaultContactPicture;

    const favEl = document.querySelector("#contact_favourite");
    favEl.innerHTML = data.favourite
      ? '<span class="bg-yellow-100 text-yellow-800 text-xs font-medium me-2 px-2.5 py-0.5 rounded-full dark:bg-yellow-900 dark:text-yellow-300"><i class="fa-solid fa-star"></i> Favourite</span>'
      : "";

    const websiteEl = document.querySelector("#contact_website");
    const linkedInEl = document.querySelector("#contact_linkedin");

    websiteEl.classList.add("hidden");
    linkedInEl.classList.add("hidden");

    if (data.websiteLink) {
      websiteEl.href = data.websiteLink;
      websiteEl.classList.remove("hidden");
    }

    if (data.linkedInLink) {
      linkedInEl.href = data.linkedInLink;
      linkedInEl.classList.remove("hidden");
    }

    openContactModal();
  } catch (error) {
    console.log("Error fetching contact data: ", error);
  }
}

function deleteContact(id, page, size, type) {
  Swal.fire({
    title: "Are you absolutely sure?",
    text: "This contact will be permanently deleted!",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#EF4444",
    cancelButtonColor: "#6B7280",
    confirmButtonText: "Yes, delete it!",
    cancelButtonText: "Cancel",
    background: "#ffffff",
    customClass: {
      popup: "rounded-2xl shadow-xl",
    },
  }).then((result) => {
    if (result.isConfirmed) {
      const targetPage = page !== undefined ? page : 0;
      const targetSize = size !== undefined ? size : 10;
      const tagQuery = type ? `&type=${encodeURIComponent(type)}` : "";
      window.location.href = `/user/contacts/delete/${id}?page=${targetPage}&size=${targetSize}${tagQuery}`;
    }
  });
}

function showAlert(message, icon = "!") {
  const alertMessage = document.getElementById("alert-message");
  const alertIcon = document.getElementById("alert-icon");
  const customAlert = document.getElementById("custom-alert");

  if (!alertMessage || !alertIcon || !customAlert) {
    alert(message);
    return;
  }

  alertMessage.textContent = message;
  alertIcon.textContent = icon;
  customAlert.classList.remove("hidden");
}

function closeAlert() {
  document.getElementById("custom-alert")?.classList.add("hidden");
}

function setupContactSearchForm(searchForm) {
  const searchBtn = searchForm.querySelector('button[type="submit"], #search-btn');
  const selectField = searchForm.querySelector('select[name="field"]');
  const searchInput = searchForm.querySelector('input[name="value"]');

  if (!selectField || !searchInput || !searchBtn) {
    return;
  }

  function isReady() {
    return selectField.value !== "" && searchInput.value.trim() !== "";
  }

  function updateButtonStyle() {
    if (isReady()) {
      searchBtn.classList.remove("bg-gray-400");
      searchBtn.classList.add("bg-blue-600", "hover:bg-blue-700");
    } else {
      searchBtn.classList.add("bg-gray-400");
      searchBtn.classList.remove("bg-blue-600", "hover:bg-blue-700");
    }
    searchBtn.style.cursor = "pointer";
  }

  function validateSearchForm() {
    if (selectField.value === "" && searchInput.value.trim() === "") {
      showAlert("Please select a field and enter a search value!");
      return false;
    }
    if (selectField.value === "") {
      showAlert("Please select a field first!");
      selectField.focus();
      return false;
    }
    if (searchInput.value.trim() === "") {
      showAlert("Please enter a search value!");
      searchInput.focus();
      return false;
    }
    return true;
  }

  searchForm.addEventListener("submit", (event) => {
    if (!validateSearchForm()) {
      event.preventDefault();
    }
  });

  searchInput.addEventListener("keydown", (event) => {
    if (event.key === "Enter") {
      event.preventDefault();
      searchForm.requestSubmit();
    }
  });

  selectField.addEventListener("change", updateButtonStyle);
  searchInput.addEventListener("input", updateButtonStyle);
  updateButtonStyle();
}

document.addEventListener("DOMContentLoaded", () => {
  document
    .querySelectorAll("form[data-contact-search-form]")
    .forEach(setupContactSearchForm);
});
